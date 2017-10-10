package com.oxande.wavecleaner.audio;

/**
 * A listener interface to implement.
 * 
 * @author wrey75
 *
 */
public interface AudioDocumentListener {
	
	/**
	 * Called when the audio has changed. The audio
	 * changed when at least one sample of the record
	 * changed.
	 * 
	 */
	void audioChanged( );
	
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
