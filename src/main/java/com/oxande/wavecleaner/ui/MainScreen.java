package com.oxande.wavecleaner.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.oxande.wavecleaner.WaveCleaner;

import ddf.minim.AudioRecorder;
import ddf.minim.AudioSample;

@SuppressWarnings("serial")
public class MainScreen extends AbstractMainScreen {

	private WaveCleaner app;
	
	public void init(WaveCleaner app) {
		this.app = app;
		
		// Init the components
		initComponents();
		this.setVisible(true);
	}

	/**
	 * Exit the application.
	 */
	protected void onExit() {
		app.dispose();
		this.dispose();
		System.exit(0);
	}
	
	/**
	 * Set the wave form based on the audio document.
	 * 
	 * @param audio
	 */
	public void setWaveForm( AudioDocument audio ){
		this.song.setDocument(audio);
	}
	
	protected void onRecordSound(){
		app.startRecord();
		  
	}
	
	/**
	 * Load the sound file
	 * 
	 */
	protected void onLoadSound(){
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound files", "aiff", "wav", "mp3" );
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String name = chooser.getSelectedFile().getAbsolutePath();
	    	this.app.loadSoundFile(name);
	    }
	}
}
