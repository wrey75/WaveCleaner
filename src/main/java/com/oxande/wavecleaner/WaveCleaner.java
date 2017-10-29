package com.oxande.wavecleaner;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.audio.AudioCache;
import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.ui.MainScreen;
import com.oxande.wavecleaner.util.ProcessingLegacy;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;

public class WaveCleaner {
	public static final String TITLE = "Wavecleaner"; 
	public static String workingDir = ".";
	
	private static Logger LOG = LogFactory.getLog(WaveCleaner.class);
	private MainScreen mainFrame;
	public Minim minim;
	protected File userDir = null;
	protected File tempDir = null;
	
	
	/**
	 * Looking for the user directory.
	 * 
	 * @return the working directory
	 */
	protected String lookingForUserDirectory(){
		String userHome = System.getProperty("user.home");
		if( userHome != null ){
			LOG.info("User directory set from 'user.home'" );
			return userHome;
		}
		
		String homePath = System.getenv("HOMEPATH");
		if( homePath != null ){
			LOG.info("User directory set from HOMEDRIVE/PATH." );
			String homeDrive = System.getenv("HOMEDRIVE");
			String homeShare = System.getenv("HOMESHARE");
			return (homeDrive == null ? homeShare : homeDrive) + homePath;
		}
		
		String home = System.getenv("HOME");
		if( home != null ){
			return home;
		}
		
		return ".";
	}

	public static void main(String[] args) {
		LOG.debug("Program started.");
		
		// For Mac users!
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException e) {
			System.err.println("Does not support the native look and feel");
		}
        
		WaveCleaner app = new WaveCleaner();
		app.start();
		app.cleanUp();
		for( int i = 0; i < args.length; i++ ){
			if(args[i].charAt(0) == '-'){
				switch( args[i].charAt(1) ){
				case 's' :
					// Load the sound file
					app.mainFrame.loadSoundFile(args[++i]);
					break;
				}
			}
		}
	}
	
	
	/**
	 * load the file stream.
	 * 
	 * @param f the file to load
	 * @return the stream loaded.
	 */
	public AudioRecordingStream loadFileStream(File f){
		String filename = f.getAbsolutePath();
		AudioRecordingStream stream = this.minim.loadFileStream(filename);
		return stream;
	}
	
	/**
	 * Check for old temporary files created by other instances. If found, they are deleted.
	 * Note the deletion works only if the file is not locked (then 2 instances can run 
	 * simultaneously)
	 *  
	 */
	protected void cleanUp(){
		File files[] = tempDir.listFiles();
		for(File f : files){
			String name = f.getName();
			if( name.startsWith(AudioCache.PREFIX) && name.endsWith(AudioCache.SUFFIX)){
				boolean success = f.delete();
				LOG.info("Deletion of {} has {}.", f, (success ? "SUCCEED" : "FAILED"));
			}
		}
	}
	
	public AudioSample getAudioSample( File f ){
		String name = f.getAbsolutePath();
		return minim.loadSample(name);
	}
	
	/**
	 * Terminates the application
	 */
	public void dispose(){
		minim.dispose();
	}
	
	/**
	 * Starts the program
	 */
	public void start(){
		this.userDir = new File( lookingForUserDirectory() );
		this.tempDir = new File( System.getProperty("java.io.tmpdir") );
		
		// Initialize the main screen
		mainFrame = new MainScreen();
		minim = new Minim(new ProcessingLegacy());
		mainFrame.init(this);
	}

	public void startRecord(){
		AudioInput in = minim.getLineIn(Minim.STEREO, 2048, 48000f, 16);
		AudioRecorder recorder = minim.createRecorder(in, "~/myrecording.wav");
		recorder.beginRecord();
	}


	
	public AudioOutput getLineOut(){
		return this.minim.getLineOut(Minim.STEREO);
	}

}
