package com.oxande.wavecleaner.filters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

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
 * <li>The audio filters from "gwc" are based on a slected number of samples
 * that can span all the file. In this software, we are working on "live", then
 * on buffers.</li>
 * </ul>
 * 
 * @author wrey75
 *
 */
public class AudioFilter extends UGen {
	private static Logger LOG = LogFactory.getLog(AudioFilter.class);
	private AudioStream mStream;

	/** buffer we use to read from the stream */
	private MultiChannelBuffer buffer;
	private int currBuff = 0;

	/** where in the buffer we should read the next sample from */

	private LinkedList<Float> extraLeft = new LinkedList<>();
	private LinkedList<Float> extraRight = new LinkedList<>();
	private float[][] samples = new float[2][];
	private int sampleIndex;
	private int bufferSize;

	/**
	 * We load the requested number of samples. This method ensures the quantity
	 * of samples read will be correct in any case. We keep an eye to the
	 * position in the {@link MultiChannelBuffer} used in the class. This is the
	 * best method to load packets of data because there is no need to have the
	 * same number of samples sent back by the {@link AudioFilter#nextSamples()}
	 * method.
	 * 
	 * @param len
	 *            the number of samples to read.
	 * @return the samples read. The buffer is updated with fresh data if
	 *         needed.
	 */
	protected float[][] loadSamples(int len, int prefetch) {
		int pos = 0;
		float[][] ret = new float[2][len + prefetch];

		while (pos < len) {
			int load = Math.min(len - pos, this.getBufferSize() - currBuff);
			for (int ch = 0; ch < 2; ch++) {
				System.arraycopy(buffer.getChannel(ch), currBuff, ret[ch], pos, load);
			}
			pos += load;
			currBuff += load;
			if (currBuff == this.getBufferSize()) {
				// Read the next buffer
				this.buffer.setBufferSize(bufferSize);
				this.mStream.read(this.buffer);
				currBuff = 0;
			}
		}
		
		if( prefetch > 0 ){
			if( currBuff + prefetch >= this.getBufferSize() ) {
				// Add some space in the buffer
				int extra = Math.max(prefetch * 2, this.bufferSize);
				this.buffer.setBufferSize(bufferSize + extra);
				MultiChannelBuffer buf2 = new MultiChannelBuffer(extra, Minim.STEREO);
				this.mStream.read(buf2);
				for( int j = 0; j < extra; j++ ){
					this.buffer.setSample(0, bufferSize+j, buf2.getSample(0, j));
					this.buffer.setSample(1, bufferSize+j, buf2.getSample(1, j));
				}
			}
		
			// Load the prefetch
			while( pos < len + prefetch ){
				for (int ch = 0; ch < 2; ch++) {
					ret[ch][pos] = buffer.getSample(ch, currBuff + pos - len );
				}
				pos++;
			}
		}

		return ret;
	}
	
	protected float[][] loadSamples(int len) {
		float[][] ret = loadSamples(len, 0);
		return ret;
	}

	/**
	 * Push back samples. Because if can be interesting
	 * 
	 * @param samples
	 *            an array of samples to push back.
	 */
	protected void pushSamples(float[][] samples) {

	}

	@Override
	final protected void uGenerate(float[] channels) {
		++sampleIndex;
		if (sampleIndex >= samples[0].length) {
			// We have to synchronize here because the
			// synchronization is not inherited in JAVA
			// (see
			// https://stackoverflow.com/questions/15998335/is-synchronized-inherited-in-java)
			synchronized (this) {
				samples = nextSamples();
				sampleIndex = 0; // Reset.
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
	public AudioFilter(AudioStream iStream) {
		int nbChannels = iStream.getFormat().getChannels();
		if (nbChannels != Minim.STEREO) {
			throw new IllegalArgumentException("The input stream must be STEREO.");
		}
		this.mStream = iStream;
		this.bufferSize = 512;
		this.buffer = new MultiChannelBuffer(this.bufferSize, 2);
		this.sampleIndex = 0;


		// Create empty
		this.samples = new float[2][];
		this.samples[0] = new float[0];
		this.samples[1] = new float[0];
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
		this.bufferSize = bufferSize;
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
	 * Returns the underlying AudioRecordingStream.
	 * 
	 * @return AudioRecordingStream: the underlying stream
	 * 
	 * @related Minim
	 * @related AudioRecordingStream
	 * @related FilePlayer
	 */
	final protected AudioStream getStream() {
		return this.mStream;
	}

	/**
	 * Calling close will close the AudioStream that this wraps, which is proper
	 * cleanup for using the stream.
	 * 
	 * @related FilePlayer
	 */
	public void close() {
		mStream.close();
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
		LOG.debug("No filtering apply.");
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
	protected MultiChannelBuffer processNext(AudioStream stream, MultiChannelBuffer buff) {
		mStream.read(buffer);
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
		this.buffer = processNext(this.mStream, this.buffer);
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
