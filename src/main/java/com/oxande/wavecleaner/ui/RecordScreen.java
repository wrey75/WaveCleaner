package com.oxande.wavecleaner.ui;

import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;

/**
 * The class which implements the recording.
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class RecordScreen extends AbstractRecordScreen implements AudioListener {
	private static Logger LOG = LogFactory.getLog(RecordScreen.class);
	protected AudioRecorder recorder;
	protected AudioInput lineIn;
	protected MainScreen mainScreen = null;
	protected String recordFileName = "noname";

	// List<Mixer> availableMixers = new ArrayList<>();

	RecordScreen(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
	}

	/**
	 * Make this recording screen visible.
	 * 
	 */
	public void initComponents() {
		Assert.isEventDispatchThread();
		super.initComponents();

		/*
		 * Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		 * availableMixers.clear(); for (Mixer.Info mixerInfo : mixers){
		 * System.out.println(mixerInfo); Mixer m =
		 * AudioSystem.getMixer(mixerInfo);
		 * 
		 * Line.Info[] lines = m.getSourceLineInfo(); for (Line.Info li :
		 * lines){ LOG.debug("Found target line: " + li); try { //m.open(); Line
		 * line = m.getLine(li); if( li instanceof Port.Info ){ Port.Info in =
		 * (Port.Info) li; this.inputLine.addItem( new SimpleMapEntry("" +
		 * availableMixers.size(), in.getName())); availableMixers.add(m);
		 * LOG.debug("** Added {} as {}", in.getName(), m); } //m.close(); }
		 * catch (LineUnavailableException e){ LOG.error("Line unavailable."); }
		 * } }
		 * 
		 * for(Mixer infoMix : availableMixers ){ LOG.info("Mixer {}: {}",
		 * infoMix, infoMix.getMixerInfo().getName()); }
		 */

		setVisible(true);
		this.recordFileName = WaveCleaner.getApplication().fixAudioFilename("noname");
		this.recorder = getRecorder(this.recordFileName);
		updateComponents();
		
		// Lock the screen
		setModal(true);
	}

	/**
	 * Update the components based on the controller.
	 */
	protected void updateComponents(){
		Assert.isEventDispatchThread();
		boolean isRecording = this.recorder.isRecording();
		this.recStart.setEnabled(!isRecording);
		this.recStop.setEnabled(isRecording);
	}
	
	/**
	 * Get a recorder. Note if the extension is not given, the extension
	 * ".wav" is automatically added. There is no advantage to use WAV files
	 * rather than AIFF files.
	 * 
	 * @param filename the file name for the recoreded sound
	 * @return the recorder
	 */
	protected AudioRecorder getRecorder(String filename) {
		if (lineIn != null) {
			lineIn.removeListener(this);
			lineIn.close();
		}
		
		WaveCleaner app = WaveCleaner.getApplication();
		
		// get a stereo line-in: sample buffer length of 2048
		// default sample rate is 44100, default bit depth is 16
		lineIn = app.getLineIn(48000, 2048);
		if (lineIn == null) {
			JOptionPane.showMessageDialog(this, "Can not select the selected line", "Audio error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		// this.app.minim.setInputMixer(mixer);

		// create an AudioRecorder that will record from in to the filename
		// specified.
		// the file will be located in the sketch's main folder.
		recorder = app.createRecorder(lineIn, filename);
		lineIn.addListener(this);
		lineIn.toString();
		lineIn.getFormat();
		return recorder;
	}

	protected void startRecord() {
		recorder.beginRecord();
		updateComponents();
	}

	protected void endRecord() {
		recorder.endRecord();
		updateComponents();
		recorder.save();
		this.closeDialog();
	}
	
	public void closeDialog(){
		Assert.isEventDispatchThread();
		
		// Do the closing job
		this.recorder = null;
		this.lineIn.close();
		this.mainScreen.loadSoundFile(this.recordFileName);
		dispose();
	}

	@Override
	public void samples(float[] samp) {
		samples(samp, samp);
	}

	@Override
	public void samples(float[] sampL, float[] sampR) {
		fastWave.update(sampL, sampR);
	}
}
