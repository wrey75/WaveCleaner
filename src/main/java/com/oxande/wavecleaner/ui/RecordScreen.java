package com.oxande.wavecleaner.ui;

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
	protected WaveCleaner app = null;

//	List<Mixer> availableMixers = new ArrayList<>();
	
	RecordScreen( WaveCleaner application ){
		this.app = application;
	}
	
	
	/**
	 * Make this recording screen visible.
	 * 
	 */
	public void initComponents(){
		Assert.isEventDispatchThread();
		super.initComponents();

		
		/*
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		availableMixers.clear();
		for (Mixer.Info mixerInfo : mixers){
		    System.out.println(mixerInfo);
		    Mixer m = AudioSystem.getMixer(mixerInfo);

		    Line.Info[] lines = m.getSourceLineInfo();
		    for (Line.Info li : lines){
		        LOG.debug("Found target line: " + li);
		        try {
		            //m.open();
		            Line line = m.getLine(li);
		            if( li instanceof Port.Info ){
		            	Port.Info in = (Port.Info) li;
		            	this.inputLine.addItem( new SimpleMapEntry("" + availableMixers.size(), in.getName()));
		            	availableMixers.add(m);
		            	LOG.debug("** Added {} as {}", in.getName(), m);
		            }
		            //m.close();
		        } catch (LineUnavailableException e){
		        	LOG.error("Line unavailable.");
		        }
		    }  
		}
		
		for(Mixer infoMix : availableMixers ){
			LOG.info("Mixer {}: {}", infoMix, infoMix.getMixerInfo().getName());
		}
		*/
		
		mixerSelected();
		setVisible(true);
		setModal(true);
	}

   protected void mixerSelected()
   {
//	   int index = inputLine.getSelectedIndex();
//	   if(index < availableMixers.size()){
//		   Mixer mixer = availableMixers.get(index);

		   if(lineIn != null){
			   lineIn.removeListener(this);
			   lineIn.close();
		   }
		   
		   // get a stereo line-in: sample buffer length of 2048
		   // default sample rate is 44100, default bit depth is 16
		   lineIn = app.minim.getLineIn(Minim.STEREO, 2048, 48000);
		   if( lineIn == null ){
			   JOptionPane.showMessageDialog(this, "Can not select the selected line", "Audio error", JOptionPane.ERROR_MESSAGE);
			   return;
		   }
		   // this.app.minim.setInputMixer(mixer);
		   
		   // create an AudioRecorder that will record from in to the filename specified.
		   // the file will be located in the sketch's main folder.
		   recorder = this.app.minim.createRecorder(lineIn, "myrecording.wav");
		   lineIn.addListener(this);
		   lineIn.toString();
		   lineIn.getFormat();
		   // lineIn.enableMonitoring();
//	   }
   }
   
	protected void startRecord(){
		recorder.beginRecord();
	}
	
	protected void endRecord(){
		recorder.endRecord();
		recorder.save();
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
