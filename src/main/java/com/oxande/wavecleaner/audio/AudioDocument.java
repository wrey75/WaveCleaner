package com.oxande.wavecleaner.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.RMSSample;
import com.oxande.wavecleaner.filters.ControllerFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.ui.WaveFormComponent;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioListener;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;

/**
 * Management of an audio file. The audio document can be seen as
 * the "driver" for its display. I created a listener for the document
 * as it is the case for mouse events and so on. It is based on
 * Microsoft MFC management with a <code>CDocument</code> and a
 * <code>CView</code>. 
 * 
 *   <p>
 *   When the file is linked to this audio document class, the
 *   levels are read from the disk at full speed (works also on
 *   MP3!) and visually updated in the display. We use a thread
 *   to read the file then the update can be done behind the
 *   scenes.
 *   </p>
 *   
 *   
 * 
 * @author wrey75
 *
 */
public class AudioDocument implements AudioListener {
	private Logger LOG = LogFactory.getLog(AudioDocument.class);
	
	String fileName;
	AudioRecordingStream stream;
	AudioCache cache;
	FilePlayer filePlayer = null;
	AudioOutput lineOut = null;
	int bufferSize;
	int sampleRate;
	int leftChannel = 0;
	int rightChannel = 1;
	int nbChannels = 2;
	private int totalSamples = 0;
	public DecrackleFilter decrackFilter = new DecrackleFilter();
	public ControllerFilter controlFilter = new ControllerFilter();
	
	/**
	 * The listeners
	 */
	private List<AudioDocumentListener> listeners = new ArrayList<>();
	
	private RMSSample[] samples = null;
	
	
	/**
	 * Returns the level samples.
	 * 
	 * @return the array of samples
	 */
	public RMSSample[] getLevels(){
		return this.samples;
	}
	
	public float getSampleRate(){
		return this.stream.getFormat().getSampleRate();
	}
	
	/**
	 * Return the size of the buffer.
	 * 
	 */
	public int getChunkSize(){
		return this.bufferSize;
	}
	
	/**
	 * Get the file player. If there was no file player,
	 * a new one is created.
	 * 
	 * @return the file player.
	 */
	private FilePlayer getFilePlayer(){
		if( this.filePlayer == null ){
			this.filePlayer = new FilePlayer( stream );
		}
		return this.filePlayer;
	}

	/**
	 * Attach a line out for playing directly.
	 * 
	 * @param out the line out (usually speakers)
	 */
	public void attachLineOut(AudioOutput out){
		this.lineOut = out;
	}
	
	public boolean isPlaying(){
		return getFilePlayer().isPlaying();
	}
	
	/**
	 * Create an audio document.
	 * 
	 * @param minim the minim object.
	 * @param f the file
	 * @throws IOException 
	 */
	public AudioDocument(Minim minim, File f) throws IOException {
		this.fileName = f.getAbsolutePath();
		// construct a new MultiChannelBuffer with 2 channels and 1024 sample
		// frames.
		// in our particular case, it doesn't really matter what we choose for
		// these
		// two values because loadFileIntoBuffer will reconfigure the buffer
		// to match the channel count and length of the file.
		// MultiChannelBuffer sampleBuffer = new MultiChannelBuffer(1, 1024);

		// we pass the buffer to the method and Minim will reconfigure it to
		// match
		// the file. if the file doesn't exist, or there is some other problen
		// with
		// loading it, the function will return 0 as the sample rate.
		this.stream = minim.loadFileStream(f.getAbsolutePath());
		this.filePlayer = new FilePlayer( stream );
		this.sampleRate = (int)this.stream.getFormat().getSampleRate();
		switch( sampleRate ){
		case 44100:
		case 48000:
			bufferSize = 1024;
			break;
		case 96000:
			bufferSize = 2048;
			break;
		case 192000:
			bufferSize = 4096;
			break;
		default:
			bufferSize = 512;
		}
		// buffer = new MultiChannelBuffer(bufferSize, stream.getFormat().getChannels());
		nbChannels = stream.getFormat().getChannels();
		if( nbChannels < 2 ){
			rightChannel = leftChannel = 0;
		}
		
		int streamDuration = stream.getMillisecondLength();
		totalSamples = (int)( ( streamDuration * 0.001 * sampleRate )  ) + 1;
		int nbChunks = (int)(totalSamples / bufferSize) + 1;
		this.cache = new AudioCache(bufferSize, nbChunks);
		new Thread(() -> loadWaveSamples()).start();
	}
	
	/**
	 * Returns the audio file (a reference to the).
	 * 
	 * @return the file
	 */
	public File getFile(){
		File f = new File(this.fileName);
		return f;
	}
	/**
	 * Register a new listener. Used at the beginning for the {@link WaveFormComponent}
	 * but any object can listen.
	 * 
	 * @param listener an audio listener.
	 */
	public void register( AudioDocumentListener listener ){
		this.listeners.add(listener);
	}
	
	public static long lastPublish = 0;
	
	private void publish(long debounce){
		if( debounce == 0 || (lastPublish + debounce < System.currentTimeMillis()) ){
			for(AudioDocumentListener listener : listeners){
				listener.audioChanged();
			}
			lastPublish = System.currentTimeMillis();
		}
	}
	
	public AudioFormat getFormat(){
		return stream.getFormat();
	}
	
	/**
	 * Returns the number of chunks. A chunk contains the audio levels for
	 * about 0.2 seconds of music.
	 *  
	 * @return the number of chunks.
	 */
	protected int getNumberOfChunks(){
		int totalChunks = (totalSamples / bufferSize) + 1;
		return totalChunks;
	}
	
	public int getNumberOfSamples() {
		return totalSamples;
	}
	
//	/**
//	 * Start to listen.
//	 * 
//	 * @return the buffer where data will be stored.
//	 */
//	protected MultiChannelBuffer start(){
//		stream.play();
//		MultiChannelBuffer buf = new MultiChannelBuffer(bufferSize, stream.getFormat().getChannels());
//		return buf;
//	}
//	
//	protected WaveSample nextSample(MultiChannelBuffer buf){
//		stream.read(buf);
//		float left[] = buf.getChannel(leftChannel);
//		float right[] = buf.getChannel(rightChannel);
//		return WaveSample.create(left, right);
//	}
	
	/**
	 * Get the audio samples in a chunk. Use a mapped
	 * memory to load the samples inside the chunk. 
	 * 
	 * @param chunk the chunk. The size of a chunk is depending of
	 * the window size (basically a chunk is about 20 milliseconds).
	 * @return the array of samples (always stereo expected).
	 */
	public float[][] getAudioSamples( int chunk ){
		float[][] samples = null;
		samples = this.cache.getSamples(chunk);
		return samples;
	}

	/**
	 * Load the samples of the audio file. This method should be
	 * called in a different thread.
	 * @throws IOException 
	 * 
	 */
	public synchronized void loadWaveSamples() {
		int nbChunks = this.getNumberOfChunks();
		stream.play();
		MultiChannelBuffer buf = new MultiChannelBuffer(bufferSize, stream.getFormat().getChannels());
		this.samples = new RMSSample[nbChunks];
		for (int i = 0; i < nbChunks; i++) {
			stream.read(buf);
			float left[] = buf.getChannel(leftChannel);
			float right[] = buf.getChannel(rightChannel);
			samples[i] = RMSSample.create(left, right);
			try {
				this.cache.saveSamples(i, left, right);
			} catch (IOException e) {
				LOG.error("Can not save into the cache.");
			}
			if (i % 100 == 0){
				publish(100);
			}
		}
		stream.pause();
		publish(0);
	}

	/**
	 * Stop to play. Basically a mute but we unpatch the line out!
	 * 
	 */
	public synchronized void stop(){
		filePlayer.pause();
		lineOut.removeListener(this);
		filePlayer.unpatch(decrackFilter);
		decrackFilter.unpatch(controlFilter);
		controlFilter.unpatch(lineOut);
		LOG.info("PAUSED");
		for(AudioDocumentListener listener : listeners){
			listener.audioPaused();
		}
	}
	
	public synchronized void play(int pos){
		int ms = (int)(pos * 1000.0 / this.getFormat().getSampleRate());
		FilePlayer player = getFilePlayer();
		if(player.isPlaying()){
			LOG.info("MOVED PLAY TO {}", pos );
			player.cue(ms);
			return;
		}

		this.lineOut.addListener(this);
		player.patch(decrackFilter).patch(controlFilter).patch(lineOut);
		// REAL VERSION player.patch(lineOut);
		player.rewind();
		player.play();
		player.cue(ms);
		LOG.info("START PLAY (from sample {})", pos );
	}

	@Override
	public void samples(float[] samp) {
		samples(samp, samp);
	}

	@Override
	public void samples(float[] sampL, float[] sampR) {
		int sample = (int)(filePlayer.position() / 1_000.0 * this.getFormat().getSampleRate());
		if( sample > this.getNumberOfSamples() - 200 ){
			/** Stop if we have heard all the file */
			this.stop();
		}
		for(AudioDocumentListener listener : listeners){
			listener.audioPlayed(sample);
		}
	}
	
}
