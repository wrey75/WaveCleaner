package com.oxande.wavecleaner.filters;

import java.util.EventListener;

/**
 * A listener interface to implement for listening an audio player
 * (only the AudioDocumentPlayer supports it).
 * 
 * @author wrey75
 *
 */
public interface AudioPlayerListener extends EventListener {
	
	/**
	 * A sample is played (then audio is playing). The last
	 * sample played is sent.
	 * 
	 * @param sample the last sample played.
	 */
	void audioPlayed( int sample );
	
	/**
	 * The file has stopped to play.
	 */
	void audioPaused();
}
