package com.oxande.wavecleaner.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.ConvertUtils;
import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.StereoSampleQueue;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.UGen;
import ddf.minim.spi.AudioStream;
import ddf.minim.ugens.FilePlayer;

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
	
	/**
	 * The enabled switch. This switch is NOT listed in the parameters because
	 * it is accessed by setEnabled(). But the change can be listened though
	 * the classic listener.
	 * 
	 */
	public static final String ENABLE = "enable";
	
	/**
	 * Listener to be informed when a value of a control has changed.
	 * Used mainly by the controller component to react when a value
	 * has been changed. This avoid to update the value when the user
	 * changed it and it is also very cool in case of the preamplifier
	 * where the AUTO-LIMITER will change its gain automatically!
	 *   
	 * @author wrey75
	 *
	 */
	public static interface ControlListener {
		/**
		 * When a control has its value changed, you are informed. Note
		 * a way to do is to call the parameter to get the formatted value 
		 * rather than the basic floating one.
		 * 
		 * @param filter the filter who generates the change
		 * @param name the name of the control
		 * @param val the value of the control (expressed as a float).
		 */
		public void controlChanged(AudioFilter filter, String name, float val);
	}
	
	private ListenerManager<ControlListener> listenerManager = new ListenerManager<>();
	private StereoSampleQueue queue;
	private UGen audio;
	protected Map<String,Parameter> parameters = new HashMap<>();
	
	/** The original stream (not filtered) */
	private UGen originalStream;
	
	/** buffer we use to read from the stream */
	private MultiChannelBuffer buffer;

	/** where in the buffer we should read the next sample from */
	private float[][] samples = new float[2][];
	private int sampleIndex;
	
	private UGenInput enabled = new UGenInput(InputType.CONTROL, 1);
	
	private static final Function<Float, String>BOOLEAN_FORMATTER = (v) -> {
		return (v > 0.5f ? "ON" : "OFF");
	};

	/**
	 * Return the parameters in a list.
	 * 
	 * @return the control parameters for the filter.
	 */
	public List<Parameter> getParameters(){
		return new ArrayList<Parameter>( parameters.values() );
	}

	/**
	 * Add the listener for callback when a control change.
	 * 
	 * @param listener the listener to call when a control is changed.
	 */
	public void addListener(ControlListener listener){
		listenerManager.add(listener);
	}
	
	public void removeListener(ControlListener listener){
		listenerManager.remove(listener);
	}
	
	protected Parameter addBooleanParameter(String name, boolean defaultValue ){
		return addParameter(name, 0.0f, 1.0f, 1.0f, ConvertUtils.bool2flt(defaultValue), BOOLEAN_FORMATTER);
	}

	protected synchronized Parameter addParameter(String name, float min, float max, float defaultValue, float tick, Function<Float, String>formatter ){
		Parameter p = new Parameter(name, min, max, defaultValue, formatter);
		p.setTick(tick);
		this.parameters.put(name.toUpperCase(), p);
		return p;
	}

	protected synchronized Parameter addSelectorParameter(String name, int nb ){
		Parameter p = new Parameter(name, 1, nb, 0, v -> String.valueOf(v));
		this.parameters.put(name.toUpperCase(), p);
		return p;
	}
	
	/**
	 * Get the original stream
	 * 
	 * @return the original stream
	 */
	public UGen getOriginalStream(){
		return this.originalStream;
	}

	/**
	 * Set the parameter with the specified name.
	 * 
	 * @param name name of the parameter (case insensitive).
	 * @param value the value.
	 * @return the effective value set. 
	 */
	public float setControl(String name, float value ){
		Parameter p = getParameter(name);
		if( p == null ){
			LOG.error("Parameter '{}' unknown.", name);
			return 0.0f;
		}
		p.setValue(value);
		return p.getValue();
	}
	
	/**
	 * Get the parameter with the specified name.
	 * 
	 * @param name name of the parameter (case insensitive)
	 * @return the value.
	 */
	public float getControl(String name){
		Parameter p = getParameter(name);
		if( p == null ){
			LOG.error("Parameter '{}' unknown.", name);
			return 0.0f;
		}
		return p.getValue();
	}
	
	public int getIntControl(String name){
		float v = this.getControl(name);
		return (int)v;
	}
	
	public Parameter getParameter(String name){
		String key = name.toUpperCase().trim();
		Parameter p = this.parameters.get(key);
		return p;
	}
	
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
		listenerManager.publish(listener -> {
			listener.controlChanged(this, ENABLE, this.enabled.getLastValue());
		});
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
		return enabled.getLastValue() > 0.5f;
	}
	
	protected float[][] loadSamples(int len) {
		return loadSamples(len,0);
	}

	protected float[][] loadSamples(int len, int extra) {
		float[][] loaded = queue.getSamples(len, extra);
		return loaded;
	}

	public class Parameter {
		private String name;
		private int type;
		private float min;
		private float max;
		private UGenInput input;
		// private float factor;
		private float tick = 0.1f;
		private Function<Float, String>formatter;
		
		Parameter(String name, float min, float max, float defaultValue, Function<Float, String>formatter ){
			this.name = name;
//			this.type = type;
			this.min = min;
			this.max = max;
//			switch(type){
//				case BOOLEAN_PARAM:
//					this.step = 1.0f;
//					this.min = 0;
//					this.max = 1;
//					break;
//					
//				case INT_PARAM:
//					this.step = 1.0f;
//					break;
//			}

			this.input = new UGenInput(InputType.CONTROL);
			this.setValue(defaultValue);
			this.formatter = formatter;
		}
		
		public float getTick(){
			return this.tick;
		}

		public void setTick(float tick){
			this.tick = tick;
		}
		
		void setValue( float v ){
			float val; // Needed to be final
			if( v < min ){
				LOG.warn("Parameter '{}' set to minimum {} instead of {}", name, min, v);
				val = min;
			}
			else if( v > max ){
				LOG.warn("Parameter '{}' set to maximum {} instead of {}", name, max, v);
				val = max;
			}
			else {
				val = v;
			}
			if( val != this.input.getLastValue() ){
				LOG.debug("Parameter '{}' set to {}", name, val);
				this.input.setLastValue(val);
				listenerManager.publish((listener) -> {
					listener.controlChanged(AudioFilter.this, getName(), val);
				});
			}
		}
		
		public float getValue(){
			float v = this.input.getLastValue();
			// LOG.warn("Parameter '{}' returns {}", name, v);
			return v;
		}

		public String getFormattedValue(){
			float v = this.input.getLastValue();
			return this.formatter.apply(v);
		}
		
		public String getName() {
			return name;
		}

		public int getType() {
			return type;
		}

		public float getMinimum() {
			return min;
		}

		public float getMaximum() {
			return max;
		}

//		public float getFactor() {
//			return this.factor;
//		}
//
//		Parameter setFactor( float f ){
//			this.factor = f;
//			return this;
//		}
	}
	
	@Override
	protected void addInput(UGen input) {
		audio = input;
		if( audio != null ){
			if( input instanceof AudioFilter ){
				LOG.info("Filter {} attached to {}", audio, this);
				this.originalStream = ((AudioFilter)input).getOriginalStream();
			}
			else if( input instanceof FilePlayer){
				LOG.info("FilePlayer {} attached to {}", audio, this);
				this.originalStream = audio;
			}
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
	public final boolean isBypassing(){
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
			synchronized (this) {
				if( isEnabled() ){
					// We have to synchronize here because the
					// synchronization is not inherited in JAVA
					// (see
					// https://stackoverflow.com/questions/15998335/is-synchronized-inherited-in-java)
					samples = nextSamples();
				}
				else {
					// Just load some samples
					samples = loadSamples( 500 );
				}
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
	public AudioFilter() {
		// Create empty samples array
		this.samples = new float[2][];
		this.samples[0] = new float[0];
		this.samples[1] = new float[0];
		this.sampleIndex = 0;
		this.init(1024);
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
	 * is quite ideal for applying FFT and some other stuff. Basically, you have
	 * to rewrite the contents of the {@link MultiChannelBuffer} which contains
	 * the samples.
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
	protected MultiChannelBuffer processNext(MultiChannelBuffer buf) {
		float[][] samples = loadSamples(buf.getBufferSize());
		buf.setChannel(0, samples[0]);
		buf.setChannel(1, samples[1]);
		process(buf);
		return buf;
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
