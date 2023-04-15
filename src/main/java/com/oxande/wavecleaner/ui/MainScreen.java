package com.oxande.wavecleaner.ui;

import com.oxande.wavecleaner.AudioProject;
import com.oxande.wavecleaner.ProjectListener;
import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.audio.AudioChangedListener;
import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.filters.AudioPlayerListener;
import com.oxande.wavecleaner.util.AWTUtils;
import com.oxande.wavecleaner.util.Assert;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
@Slf4j
public class MainScreen extends AbstractMainScreen
		implements AudioPlayerListener, AudioChangedListener, ProjectListener {
	private WaveCleaner app;
	private AudioDocument audio;
	private AudioOutput lineOut;
	private AudioProject project;

	public void init(WaveCleaner app) {
		this.app = app;
		this.lineOut = this.app.getLineOut();
		this.project = new AudioProject();
		this.project.addListener(this);

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
	public void setWaveForm(AudioDocument audio) {
		this.lineOut = this.app.minim.getLineOut(Minim.STEREO, 512, 48000f, 16);
		audio.attachLineOut(lineOut);
		this.audio = audio;
		this.song.setAudioDocument(audio);
		this.instant.setAudioDocument(audio);
		this.audio.addChangedAudioListener(this);
		this.audio.addAudioPlayerListener(this);

		// Initialize the controller component
		this.audio.preamplifer.setVUMeter(this.vuMeter);
		this.vuMeter.setVisible(true);
		this.controller.setFilters(audio.decrackFilter, audio.clickFilter, audio.preamplifer);
	}

	/**
	 * A record has been requested.
	 * 
	 */
	public void onRecordSound() {
		RecordScreen dialog = new RecordScreen(this);
		dialog.initComponents();
	}

	/**
	 * Load the sound file
	 * 
	 */
	protected synchronized void onLoadSound() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound files", "aiff", "wav", "mp3");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String name = chooser.getSelectedFile().getAbsolutePath();
			this.loadSoundFile(name);
		}
	}

	protected void saveMixed()  {
		Assert.isEventDispatchThread();
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter aiffFilter = new FileNameExtensionFilter("Audio Interchange File", "aiff");
		FileNameExtensionFilter wavFilter = new FileNameExtensionFilter("Waveform Audio File", "wav");
		FileNameExtensionFilter mp3Filter = new FileNameExtensionFilter("Format compressé", "mp3");
		fc.addChoosableFileFilter(aiffFilter);
		fc.addChoosableFileFilter(wavFilter);
		fc.addChoosableFileFilter(mp3Filter);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String name = fc.getSelectedFile().getAbsolutePath();
			AWTUtils.runFreely( () -> {
//				try {
					this.audio.saveTo(name);
					AWTUtils.showMessage( "Your record has been saved.");
//				}
//				catch(IOException ex){
//					AWTUtils.showErrorMessage(ex.getCause().getLocalizedMessage());
//				}
			});

		}
	}

	/**
	 * Load the file sound specified.
	 * 
	 * @param name
	 *            the file to load
	 */
	public void loadSoundFile(String name) {
		File f = new File(name);
		if (f.exists()) {
			try {
				AudioDocument audio = new AudioDocument(this.app, f);
				this.setWaveForm(audio);
				this.project.addSource(f);
				this.setTitle(f.getName() + " | " + WaveCleaner.TITLE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Can not load the file " + name, ex.getLocalizedMessage(),
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "File " + name, "File does not exists", JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void onPlayPause() {
		int pos = song.getPlayHead();
		if (this.audio.isPlaying()) {
			this.audio.stop();
			this.vuMeter.reset();
		} else {
			this.audio.play(pos);
		}
	}

	protected void onZoomIn() {
		// int nb = this.song.getExtent() - this.audio.getNumberOfSamples() /
		// 20;
		int nb = (int) (this.song.getExtent() * 1.10);
		this.song.setExtent(nb);
	}

	protected void onZoomOut() {
		// int nb = this.song.getExtent() + this.audio.getNumberOfSamples() /
		// 20;
		int nb = (int) (this.song.getExtent() * 0.90);
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
		// LOG.info("Audio played.");
		Assert.isTrue(SwingUtilities.isEventDispatchThread());
		this.song.setPlayHead(sample, true);
		this.instant.mode = WaveComponent.WAVE_MODE;
		this.instant.setVisibleWindow(sample, sample + audio.getChunkSize());
		repaint();
		if (this.audio.getNumberOfSamples() < sample) {
			LOG.info("END OF SONG: STOP THE PLAYER");
			this.audio.stop();
			this.vuMeter.reset();
		}
	}

	@Override
	public void audioPaused() {
		// TODO - Update the "PLAY/RECORD BUTTONS"
	}

}
