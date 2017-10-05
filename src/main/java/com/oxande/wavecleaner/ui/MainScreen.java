package com.oxande.wavecleaner.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.oxande.wavecleaner.WaveCleaner;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;


@SuppressWarnings("serial")
public class MainScreen extends AbstractMainScreen {

	private WaveCleaner app;
	private AudioDocument audio;
	private AudioOutput lineOut;
	
	public void init(WaveCleaner app) {
		this.app = app;
		this.lineOut = this.app.getLineOut();
		
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
		this.audio = audio;
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
	
	@Override
	public void onPlay(){
		AudioOutput out = this.app.minim.getLineOut(Minim.STEREO, 2048, 48000f, 16);
		this.audio.play(out);
	}
}
