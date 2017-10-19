package com.oxande.wavecleaner.ui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.audio.AudioDocumentListener;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;


@SuppressWarnings("serial")
public class MainScreen extends AbstractMainScreen implements AudioDocumentListener {

	private WaveCleaner app;
	private AudioDocument audio;
	private AudioOutput lineOut;
	
	public void init(WaveCleaner app) {
		this.app = app;
		this.lineOut = this.app.getLineOut();
		
		// Init the components
		initComponents();
		this.pack();
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
    	this.lineOut = this.app.minim.getLineOut(Minim.STEREO, 512, 48000f, 16);
		audio.attachLineOut(lineOut);
    	this.audio = audio;
		this.song.setAudioDocument(audio);
		this.instant.setAudioDocument(audio);
		this.audio.register(this);
		this.infos.setAudioDocument(audio);
	}
	
	protected void onRecordSound(){
		app.startRecord();
	}
	
	/**
	 * Load the sound file
	 * 
	 */
	protected synchronized void onLoadSound(){
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
	public void onPlayPause(){
		int pos = song.getPlayHead();
		if(this.audio.isPlaying()){
			this.audio.stop();
		}
		else {
			this.audio.play(pos);
		}
	}
	
	
	protected void onZoomIn(){
		// int nb = this.song.getExtent() - this.audio.getNumberOfSamples() / 20;
		int nb = (int)(this.song.getExtent() * 1.10);
		this.song.setExtent(nb);
	}
	
	
	protected void onZoomOut(){
		// int nb = this.song.getExtent() + this.audio.getNumberOfSamples() / 20;
		int nb = (int)(this.song.getExtent() * 0.90);
		this.song.setExtent(nb);
	}
	
	
	@Override
	public void audioChanged() {
		// int max = audio.getNumberOfSamples();
		// if( max != numberOfSamples ){
		this.song.updateAudio();
		// }
		// repaint();
	}
	
	@Override
	public void audioPlayed(int sample) {
		this.song.setPlayHead(sample, true);
		this.instant.mode = WaveComponent.WAVE_MODE;
		this.instant.setVisibleWindow(sample, sample + audio.getChunkSize());
		repaint();
	}

	@Override
	public void audioPaused() {
		// TODO - Update the "PLAY/RECORD BUTTONS"
	}
}
