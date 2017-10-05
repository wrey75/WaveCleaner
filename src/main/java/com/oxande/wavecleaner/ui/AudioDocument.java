package com.oxande.wavecleaner.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import com.oxande.wavecleaner.RMSSample;

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
public class AudioDocument {
	String fileName;
	AudioRecordingStream stream;
	FilePlayer filePlayer = null;
	int bufferSize;
	int sampleRate;
	int leftChannel = 0;
	int rightChannel = 1;
	int nbChannels = 2;
	private int totalSamples = 0;
	
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
	
	public int getSampleSize(){
		return this.bufferSize;
	}
	
	private FilePlayer getFilePlayer(){
		if( this.filePlayer == null ){
			this.filePlayer = new FilePlayer( stream );
		}
		return this.filePlayer;
	}
	
	/**
	 * Create an audio document.
	 * 
	 * @param minim
	 * @param f
	 */
	public AudioDocument(Minim minim, File f) {
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
		new Thread(() -> loadWaveSamples()).start();
	}
	
	public void register( WaveFormComponent component ){
		this.listeners.add(component);
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
	
	protected int getNumberOfSamples() {
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
	
	
	
//	protected void stop(){
//		stream.pause();
//	}

	/**
	 * Load the samples of the audio file. This method should be
	 * called in a different thread.
	 * 
	 */
	public synchronized void loadWaveSamples(){
		int nbChunks = this.getNumberOfChunks();
		stream.play();
		MultiChannelBuffer buf = new MultiChannelBuffer(bufferSize, stream.getFormat().getChannels());
		this.samples = new RMSSample[nbChunks];
		for (int i = 0; i < nbChunks; i++) {
			stream.read(buf);
			float left[] = buf.getChannel(leftChannel);
			float right[] = buf.getChannel(rightChannel);
			samples[i] = RMSSample.create(left, right);
			if (i % 100 == 0){
				publish(100);
			}
		}
		stream.pause();
		publish(0);
	}

	public synchronized void stop(){
		filePlayer.pause();
	}
	
	public synchronized void play(AudioOutput out){
		FilePlayer player = getFilePlayer();
		player.patch(out);
		player.cue(1000 * 30 );
		player.play();
	}
	
}
