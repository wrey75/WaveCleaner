package com.oxande.wavecleaner;

import java.io.File;

import com.oxande.wavecleaner.ui.MainScreen;
import com.oxande.wavecleaner.util.ProcessingLegacy;

import ddf.minim.AudioSample;
import ddf.minim.Minim;

public class WaveCleaner {
	private MainScreen mainFrame;
	private Minim minim;

	public static void main(String[] args) {
		WaveCleaner app = new WaveCleaner();
		app.start();
		for( int i = 0; i < args.length; i++ ){
			if(args[i].charAt(0) == '-'){
				switch( args[i].charAt(1) ){
				case 's' :
					// Load the sound file
					app.loadSoundFile( args[++i]);
					break;
					
				}
			}
		}
	}
	
	public AudioSample getAudioSample( File f ){
		String name = f.getAbsolutePath();
		return minim.loadSample(name);
	}
	
	public void loadSoundFile( String name ){
		File f = new File(name);
		mainFrame.setWaveForm(f);
	}

	/**
	 * Terminates the application
	 */
	public void dispose(){
		minim.dispose();
	}
	
	public void start(){
		// Initialize the main screen
		mainFrame = new MainScreen();
		mainFrame.init(this);
		minim = new Minim(new ProcessingLegacy());
	}


}
