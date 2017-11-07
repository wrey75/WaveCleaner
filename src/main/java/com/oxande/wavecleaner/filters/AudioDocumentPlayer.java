package com.oxande.wavecleaner.filters;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.ui.WaveFormComponent;
import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;

/**
 * The audio document player is very similar to the {@link FilePlayer}
 * but will copy the original sound to a queue to be processed by the 
 * {@link PreamplifierFilter}. The duplicated signal goes directly to B
 * but include no delay.
 * 
 * I mean the signal (A) is sent directly to (B) and the samples received
 * from (F) and (B) are fully synchronized.
 * 
 * <code>
 * 
 * +-------------------------+
 * |                         | (A)
 * |  RecordingAudioStream   |-----+
 * |                         |     |
 * +-------------------------+     |
 *         | (C)                   |
 *         |                       |
 *         |                       |
 *        \|/                      |
 *      +----------------+         |
 *      |                |         |
 *      |  Decrackling   |         |
 *      |                |         |
 *      +----------------+         |
 *         | (D)                   |
 *         |                       |
 *         |                       |
 *        \|/                      |
 *      +----------------+         |
 *      |                |         |
 *      |  Other filters |         |
 *      |                |         |
 *      +----------------+         |
 *         | (E)                   |
 *         |         +-------------+
 *         |         |
 *        \|/ (F)   \|/ (B)
 *      +-------------------------+
 *      |                         |
 *      |  ControllerFilter       |
 *      |  (select source,        |
 *      |  add volume...)         |
 *      |                         |
 *      +-------------------------+
 *         |                      
 *         |                      
 *         |                  /|    
 *         |               __/ |   
 *         | Speakers     |    |
 *         +------------->|    |
 *                        |__  |  
 *                           \ |
 *                            \|
 *                            
 * </code>
 *  
 * @author wrey75
 *
 */
public class AudioDocumentPlayer extends FilePlayer {
	private static Logger LOG = LogFactory.getLog(AudioDocumentPlayer.class);
	
	private static final int NB_CHANNELS = 2;
		
	float[] queue;
	int first = 0; // First positio in the queue
	int last = 0;  // Last positio in the queue

	/**
	 * Return the delay in samples. The delay is the exact number of
	 * samples in the queue. Not very tricky.
	 * 
	 * @return the delay in samples
	 */
	public int getDelay(){
		return last - first;
	}

	public AudioDocumentPlayer(AudioRecordingStream iFileStream) {
		super(iFileStream);
		this.queue = new float[100];
	}
	
	public int playHead(){
		// TODO: Why no sample rate?
		float sampleRate = 48000 /*sampleRate() */;
		double pos = this.position() / 1000.0;
		int sample = (int)(pos * sampleRate) /*+ last - first*/;
		return sample;
	}

	public void cue(int millis){
		// We should know this... Because we are not at the same point anymore
		super.cue(millis);
		listenerManager.publish( l -> l.audioPlayed(this.playHead()) );
	}
	
	private int buffsize(){
		return this.queue.length / NB_CHANNELS;
	}
	

	/**
	 * Pop one (and only one) sample per channel. Use to get the original
	 * signal for the original file.
	 * 
	 * @param channels the STEREO channel.
	 */
	public synchronized void pop(float[] channels) {
		if (first < 0 || last < first) {
			throw new IllegalAccessError("The queue is empty!");
		}
		int pos = first * NB_CHANNELS;
		channels[0] = queue[pos];
		channels[1] = queue[pos + 1];
		
		// For debugging purposes, create a white noise
		queue[pos] =  (float)(Math.random() * 2.0 - 1.0);
		queue[pos + 1] = (float)(Math.random() * 2.0 - 1.0);
		
		first++; // We are ahead
		if (first == buffsize()) {
			// Not fully necessary because we do a modulo when pushing
			// but helps in calling the refresh
			first -= buffsize();
			last -= buffsize();
		}
		if( first % 100 == 0 ){
			listenerManager.publishOnce( l -> l.audioPlayed(this.playHead() - last + first));
		}
	}

	
	/**
	 * Push a new sample in the queue. Note the queue is a basic 
	 * list (an array) where the data is pushed in a circular manner. Then
	 * there is no need of copy (except when the queue must be expanded).
	 * 
	 * @param left the left sample.
	 * @param right the right sample.
	 */
	synchronized void push(float left, float right) {
		if ( (last - first) + 1 > buffsize()) {
			// Add 100 more samples
			int newSize = (this.buffsize() + 100) * NB_CHANNELS;
			float[] newQueue = new float[newSize];
			// The copy below is not performant.
			for(int i = first; i < last; i++ ){
				int oldPos = (i % buffsize()) * NB_CHANNELS;
				int newPos = ((i - first) % buffsize()) * NB_CHANNELS;
				for(int ch = 0; ch < NB_CHANNELS; ch ++ ){
					newQueue[ newPos++ ] = newQueue[ oldPos++ ]; 
				}
			}
			
			// The queue is now from 0.
			last -= first;
			first = 0;
			queue = newQueue;
			LOG.info("Increased buffer to {} samples.", buffsize());
		}

		int pos = (last % buffsize()) * NB_CHANNELS;
		queue[pos] = left;
		queue[pos + 1] = right;
		last++;
	}

	
	protected void uGenerate(float[] channels) 
	{
		super.uGenerate(channels);
		push(channels[0], channels[1]); // Store in the original buffer
	}
	

	private ListenerManager<AudioPlayerListener> listenerManager = new ListenerManager<AudioPlayerListener>();
	
	
	/**
	 * Register a new listener. Used at the beginning for the
	 * {@link WaveFormComponent} but any object can listen.
	 * 
	 * @param listener
	 *            an audio listener.
	 */
	public void addPlayerListener(AudioPlayerListener listener) {
		this.listenerManager.add(listener);
	}

	public void removePlayerListener(AudioPlayerListener listener) {
		this.listenerManager.remove(listener);
	}
}
