package com.oxande.wavecleaner.filters;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.StereoSampleQueue;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.UGen;
import ddf.minim.spi.AudioStream;

/**
 * This is the base for audio filters. Note the audio filter is an high-end
 * filter (mainly because it can work on many samples and not only one) but also
 * because it can (and must drive) components to modify its working
 * capabilities.
 * 
 * <p>
 * Rather than implementing the {@link UGen} class, the {@link AudioFilter}
 * provides the capability to process a complete buffer. The size of the buffer
 * can be initialized by the class itself.
 * </p>
 * 
 * 
 * <p>
 * IMPORTANT NOTES
 * </p>
 * <ul>
 * <li>In inherited classes, you have to declare <code>synchronized</code> all
 * the methods which modifies a parameter of your filter. This is needed to
 * avoid issues during the processing of the buffer.</li>
 * <li>The audio filters from "gwc" are based on a selected number of samples
 * that can span all the file. In this software, we are working on "live", then
 * on buffers.</li>
 * </ul>
 * 
 * @author wrey75
 *
 */
public class AudioFilter extends UGen {
	private static Logger LOG = LogFactory.getLog(AudioFilter.class);
	private StereoSampleQueue queue;
	private UGen audio;
	// private UGenInput input; // The main input
	
	/** buffer we use to read from the stream */
	private MultiChannelBuffer buffer;

	/** where in the buffer we should read the next sample from */
	private float[][] samples = new float[2][];
	private int sampleIndex;
	
	private UGenInput enabled = new UGenInput(InputType.CONTROL, 1);

	/**
	 * Enables or disables the filter. When disabled, the filter is bypassed
	 * but these parameters are kept. The already calculated audio is played
	 * first and when all the buffer is consumed, we just swap to the original
	 * input. 
	 * 
	 * <p>
	 * Due to the expected "fast" switch, we do not generate events to
	 * inform the use the filter is now bypassed.
	 * </p> 
	 * 
	 * @param b true to enable the filter (or false to bypass it).
	 */
	public void setEnable(boolean b){
		this.enabled.setLastValue(b ? 1 : 0);
		LOG.info("Filter {} {}", this.getClass().getSimpleName(), (b ? "enabled" : "disabled"));
	}
	
	/**
	 * Resize an array of floats
	 * 
	 * @param array the original array or null
	 * @param newSize the new size
	 * @return an array having the new size and the values from
	 * the original array copied.
	 */
	public static float[] newFloatArray(float[] array, int newSize ){
		float[] newArray = new float[newSize];
		if(array != null){
			int len = Math.min(newSize, array.length);
			System.arraycopy(array, 0, newArray, 0, len);
		}
		return newArray;
	}
	
	public boolean isEnabled(){
		return enabled.getLastValue() > 0;
	}
	
	protected float[][] loadSamples(int len) {
		return loadSamples(len,0);
	}

	protected float[][] loadSamples(int len, int extra) {
		return queue.getSamples(len, extra);
	}

	
	@Override
	protected void addInput(UGen input) {
		audio = input;
		if( audio != null ){
			this.queue = new StereoSampleQueue(this.audio);
		}
	}
	
	@Override
	protected void removeInput(UGen input)
	{
		if ( audio == input )
		{
			audio = null;
			this.queue = null;
		}
	}
	
	/**
	 * Check if we are bypassing the filter. This method returns the
	 * opposite value compared to isEnabled() except in a small range
	 * of time. You should refer the usage of {@link AudioFilter#isEnabled()}
	 * except if you need a real time (less than 20 milliseconds) information.
	 * Note also if this is the first filter of a pipeline, the sound currently
	 * played can be still filtered even you are already bypassing the filters.
	 * 
	 * @return true is the filter is bypassed.
	 */
	boolean isBypassing(){
		if( sampleIndex > samples[0].length) {
			return true;
		}
		return false;
	}

	@Override
	final protected void uGenerate(float[] channels) {
		++sampleIndex;
		if (sampleIndex >= samples[0].length) {
			// We have to consume ALL the audio before switching off the filter.
			if( isEnabled() ){
				// We have to synchronize here because the
				// synchronization is not inherited in JAVA
				// (see
				// https://stackoverflow.com/questions/15998335/is-synchronized-inherited-in-java)
				synchronized (this) {
					samples = nextSamples();
					sampleIndex = 0; // Reset.
				}
			}
			else {
				// Just copy from input!
				this.audio.tick(channels);
				return;
			}
		}
		channels[0] = samples[0][sampleIndex];
		channels[1] = samples[1][sampleIndex];
	}

	/**
	 * Create an audio filter. You should always call this method because it
	 * initialize the stream and creates a buffer.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public AudioFilter() {
//		int nbChannels = iStream.getFormat().getChannels();
//		if (nbChannels != Minim.STEREO) {
//			throw new IllegalArgumentException("The input stream must be STEREO.");
//		}
//		this.mStream = iStream;
		
//		this.input = new UGenInput(InputType.AUDIO);
//		this.input.setChannelCount(2);
		// this.queue = new StereoSampleQueue(stream);

		// Create empty samples array
		this.samples = new float[2][];
		this.samples[0] = new float[0];
		this.samples[1] = new float[0];
		this.sampleIndex = 0;
	}
	


	/**
	 * Construct a FilePlayer that will read from iFileStream.
	 * 
	 * @param iStream
	 *            AudioRecordingStream: the stream this should read from
	 * @param bufferSize
	 *            the size in samples of the buffer. Try to keep it the lowest
	 *            possible (less than 512 bytes if possible) to ensure the
	 *            updated parameters are taken into account quickly.
	 * 
	 * @example Synthesis/filePlayerExample
	 */
	public void init(int bufferSize) {
		this.buffer = new MultiChannelBuffer(bufferSize, 2);
	}

	/**
	 * Get the buffer size of the stream.
	 * 
	 * @return the buffer size
	 */
	final public int getBufferSize() {
		return this.buffer.getBufferSize();
	}


	/**
	 * Overwrite this method if you want to work with the current buffer. This
	 * is quite ideal for applying FFT and some other stuff
	 * 
	 * <p>
	 * Currently this method does nothing.
	 * </p>
	 * 
	 * @param buff
	 *            the buffer to modify.
	 */
	protected void process(MultiChannelBuffer buff) {
		LOG.debug("process() MUST BE IMPLEMENTED.");
	}

	/**
	 * Process the next part. wHen the buffer is empty, process the next one.
	 * This work should be done as fast as possible because we read the next
	 * samples.
	 * 
	 * <p>
	 * Basically, the contract is to read new data (using the
	 * {@link AudioStream#read(MultiChannelBuffer)}) and processing the new data
	 * loaded. In the default implementation, the next buffer is read and
	 * process by the {@link AudioFilter#process(MultiChannelBuffer)} method. If
	 * you need to work with ahead samples, this method is the correct one to
	 * modify.
	 * </p>
	 * 
	 * @param stream
	 *            the audio stream (we have to read the next samples)
	 * @param buff
	 *            the current {@link MultiChannelBuffer} to store the STEREO
	 *            data.
	 * @return a new (or the current) {@link MultiChannelBuffer} where we will
	 *         read the next samples.
	 * 
	 */
	protected MultiChannelBuffer processNext(MultiChannelBuffer buff) {
		float[][] samples = loadSamples(this.buffer.getBufferSize());
		this.buffer.setChannel(0, samples[0]);
		this.buffer.setChannel(1, samples[1]);
		process(buffer);
		return buffer;
	}

	/**
	 * The basic way. Returns the next samples available. The default
	 * implementation relies on processing buffers by buffer size of samples.
	 * But in some cases, the input buffer can overlap then we create the exact
	 * portion of output necessary.
	 * 
	 * @return an array of floats containing the left side on the index 0 and
	 *         the right side in the index 1. The left and right channels must
	 *         have the same number of samples and 2 channels only are accepted.
	 */
	protected float[][] nextSamples() {
		// Generally return the same buffer but not a requirement.
		this.buffer = processNext(this.buffer);
		if (this.buffer == null) {
			throw new IllegalArgumentException("The returned buffer is null!");
		} else if (this.buffer.getChannelCount() != Minim.STEREO) {
			throw new IllegalArgumentException("The returned buffer must be STEREO!");
		}

		// Create a new array for output
		float[][] ret = new float[2][];
		ret[0] = this.buffer.getChannel(0);
		ret[1] = this.buffer.getChannel(1);
		return ret;
	}
}
