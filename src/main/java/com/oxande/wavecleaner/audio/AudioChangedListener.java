package com.oxande.wavecleaner.audio;

import java.util.EventListener;

/**
 * A listener interface to implement.
 * 
 * @author wrey75
 *
 */
public interface AudioChangedListener extends EventListener {
	
	/**
	 * Called when the audio has changed. The audio
	 * changed when at least one sample of the record
	 * changed.
	 * 
	 */
	void audioChanged( );
}
