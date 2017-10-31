package com.oxande.wavecleaner.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioRecorder;
import ddf.minim.Minim;

public class RecordScreen extends AbstractRecordScreen {
	private static Logger LOG = LogFactory.getLog(RecordScreen.class);
	protected AudioRecorder recorder;
	protected WaveCleaner app = null;
	
	RecordScreen( WaveCleaner application ){
		this.app = application;
	}
	
	
	/**
	 * Make this recording screen visible.
	 * 
	 */
	public void initComponents(){
		super.initComponents();
		setModal(true);
		File f = new File("recording.wav");
		recorder = this.app.getAudioRecorder(f, 48000);
		
		
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		List<Line.Info> availableLines = new ArrayList<>();
		for (Mixer.Info mixerInfo : mixers){
		    System.out.println(mixerInfo);
		    Mixer m = AudioSystem.getMixer(mixerInfo);

		    Line.Info[] lines = m.getSourceLineInfo();
		    for (Line.Info li : lines){
		        System.out.println("Found target line: " + li);
		        try {
		            m.open();
		            if( li instanceof Port.Info ){
		            	Port.Info in = (Port.Info) li;
		            	this.inputLine.addItem( new SimpleMapEntry("" + availableLines.size(), in.getName()));
		            	availableLines.add(li);
		            }
		        } catch (LineUnavailableException e){
		            System.out.println("Line unavailable.");
		        }
		    }  
		}
		
		for(Line.Info infoLine : availableLines ){
			LOG.info("Target line {}: {}", infoLine.getClass(), infoLine);
		}
		
		
		int sampleRate = 48000;
		int bitDepth = 16;
		int type = Minim.STEREO;
		int bufferSize = 4096;
		AudioFormat format = new AudioFormat(sampleRate, bitDepth, type, true, false);
//		TargetDataLine line = getTargetDataLine(format, bufferSize * 4);
//		if (line != null)
//		{
//			return new JSAudioInput(line, bufferSize);
//		};
//		TargetDataLine line;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(info)) {
		    // Handle the error.
		}
		    // Obtain and open the line.
		try {
			Line.Info[] infoLines = AudioSystem.getSourceLineInfo(info);
			for(Line.Info infoLine : infoLines ){
				LOG.info("Source line: {}", infoLine);
			}
			infoLines = AudioSystem.getTargetLineInfo(info);
			for(Line.Info infoLine : infoLines ){
				LOG.info("Target line {}: {}", infoLine.getClass(), infoLine);
			}
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(format);
		} catch (LineUnavailableException ex) {
		        // Handle the error.
		    //... 
		}
		

		setVisible(true);
	}

	protected void startRecord(){
		recorder.beginRecord();
	}
	
	protected void endRecord(){
		recorder.endRecord();
		recorder.save();
	}
}
