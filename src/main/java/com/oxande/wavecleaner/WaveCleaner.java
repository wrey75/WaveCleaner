package com.oxande.wavecleaner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

import com.oxande.wavecleaner.ui.MainScreen;
import com.oxande.wavecleaner.util.ProcessingLegacy;

import ddf.minim.AudioInput;
import ddf.minim.AudioRecorder;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

public class WaveCleaner {
	private MainScreen mainFrame;
	private Minim minim;

	public static void main(String[] args) {
		/*
		//AudioFormat(float sampleRate, int sampleSizeInBits,
		// int channels, boolean signed, boolean bigEndian)

		AudioFormat audioFormat = new AudioFormat(48000,16,2,true,true);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		if (!AudioSystem.isLineSupported(info)) {
		  System.err.println("Audio Format specified is not supported");
		  return;
		}

		// On récupère le DataLine adéquat
		TargetDataLine line;
		try {
			Line.Info[] infos = AudioSystem.getTargetLineInfo(info);
			for(int j = 0; j < infos.length; j++ ){
				System.out.println(infos[j].getLineClass());
			}
			
			line = (TargetDataLine) AudioSystem.  getLine(info);
			System.out.println("LINE = " + line);
		} catch (LineUnavailableException e) { 
		  e.printStackTrace();
		  return;
		}


		try {
			line.open();
		} catch (LineUnavailableException e1) {
		e1.printStackTrace();
		return;
		}


			line.start();

		 AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
		 AudioInputStream audioInputStream = new AudioInputStream(line);
		 try {
		   OutputStream file = new FileOutputStream("/Users/wrey/sound.wav");
		AudioSystem.write(audioInputStream,targetType, file );
		 } catch (IOException e1) {
		    e1.printStackTrace();
		 } finally {
		    line.close();
		    try {
		       audioInputStream.close();
		    } catch (IOException e) {
		       e.printStackTrace();
		    }
		 }
		/*
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		List<Line.Info> availableLines = new ArrayList<>();
		for (Mixer.Info mixerInfo : mixers){
		    System.out.println("\nFound Mixer: " + mixerInfo );

		    Mixer m = AudioSystem.getMixer(mixerInfo);

		    Line.Info[] lines = m.getTargetLineInfo();

		    for (Line.Info li : lines){
		    	if( li.matches(Port.Info.LINE_IN)){
		    		System.out.println(">> SUPPORT LINE-IN");
		    	}
		    	if( li.matches(Port.Info.MICROPHONE)){
		    		System.out.println(">> SUPPORT MICROPHONE");
		    	}
		        System.out.println("Found target line: " + li);
		        try {
		            m.open();
		            availableLines.add(li);                  
		        } catch (LineUnavailableException e){
		            System.out.println("Line unavailable.");
		        }
		    }  
		}

		System.out.println("Available lines: " + availableLines);

		System.exit(0);
				*/
		
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

	public void startRecord(){
		AudioInput in = minim.getLineIn(Minim.STEREO, 2048, 48000f, 16);
		AudioRecorder recorder = minim.createRecorder(in, "~/myrecording.wav");
		recorder.beginRecord();
	}

}
