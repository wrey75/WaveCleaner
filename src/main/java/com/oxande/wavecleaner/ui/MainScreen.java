package com.oxande.wavecleaner.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.audio.AudioChangedListener;
import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.filters.AudioPlayerListener;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;


@SuppressWarnings("serial")
public class MainScreen extends AbstractMainScreen implements AudioPlayerListener, AudioChangedListener {
	private static Logger LOG = LogFactory.getLog(MainScreen.class);
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
		this.audio.addChangedAudioListener(this);
		this.audio.addAudioPlayerListener(this);
		// this.infos.setAudioDocument(audio);
		
		// Initialize the controller component
		this.audio.preamplifer.setVUMeter(this.vuMeter);
		this.vuMeter.setVisible(true);
		this.controller.setFilters(audio.decrackFilter, audio.preamplifer);
	}
	
	protected void onRecordSound(){
		RecordScreen dialog = new RecordScreen(this.app);
		dialog.initComponents();
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
	    	this.loadSoundFile(name);
	    }
	}
	

	/**
	 * Load the file sound specified.
	 * 
	 * @param name the file to load
	 */
	public void loadSoundFile( String name ){
		File f = new File(name);
		if(f.exists()){
			try {
				AudioDocument audio = new AudioDocument(this.app, f);
				this.setWaveForm(audio);
				this.setTitle(f.getName() + " | " + WaveCleaner.TITLE );
			}
			catch(IOException ex){
				JOptionPane.showMessageDialog(this, "Can not load the file " + name, ex.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "File " + name, "File does not exists", JOptionPane.WARNING_MESSAGE);
		}
	}

	
	@Override
	public void onPlayPause(){
		int pos = song.getPlayHead();
		if(this.audio.isPlaying()){
			this.audio.stop();
			this.vuMeter.reset();
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
		Assert.isTrue( SwingUtilities.isEventDispatchThread() );
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
