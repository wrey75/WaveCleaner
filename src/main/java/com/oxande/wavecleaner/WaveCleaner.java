package com.oxande.wavecleaner;

import java.io.File;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.audio.AudioCache;
import com.oxande.wavecleaner.ui.MainScreen;
import com.oxande.wavecleaner.util.ProcessingLegacy;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import ddf.minim.Recordable;
import ddf.minim.spi.AudioRecordingStream;

public class WaveCleaner {
	public static final String TITLE = "Wavecleaner"; 
	public static String workingDir = ".";
	
	private static Logger LOG = LogFactory.getLog(WaveCleaner.class);
	private static WaveCleaner application;
	private MainScreen mainFrame;
	public Minim minim;
	protected File userDir = null;
	protected File tempDir = null;
	
	private WaveCleaner(){
		if( WaveCleaner.application != null ){
			throw new IllegalStateException("The application is ALREADY created!");
		}

		this.userDir = new File( lookingForUserDirectory() );
	}
	
	/**
	 * Get the application instance.
	 * 
	 * @return the application instance.
	 */
	public static final WaveCleaner getApplication(){
		if( application == null ){
			throw new IllegalStateException("The application MUST be created at this point.");
		}
		return application;
	}
	
	/**
	 * Looking for the user directory.
	 * 
	 * @return the working directory
	 */
	protected static String lookingForUserDirectory(){
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
        
        application = new WaveCleaner();
        application.start();
		application.cleanUp();
		String userDir = lookingForUserDirectory();
		for( int i = 0; i < args.length; i++ ){
			if(args[i].charAt(0) == '-'){
				switch( args[i].charAt(1) ){
				case 's' :
					// Load the sound file
					application.mainFrame.loadSoundFile(args[++i]);
					break;
					
				case 'u' :
					application.userDir = new File(args[++i]);
					break;
				
				case 'r' :
					SwingUtilities.invokeLater(() -> {
						application.mainFrame.onRecordSound();					
					});
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
	 * Retrieve an audio recorder.
	 * 
	 * @param f the file to save the sound.
	 * @param sampleRate the sample rate
	 * @return the {@link AudioRecorder}.
	 */
	public AudioRecorder getAudioRecorder(File f, float sampleRate){
		AudioInput in = this.minim.getLineIn(Minim.STEREO, (int)(sampleRate / 20), sampleRate);
		AudioRecorder stream = minim.createRecorder(in, f.getAbsolutePath());
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
		this.tempDir = new File( System.getProperty("java.io.tmpdir") );
		
		// Initialize the main screen
		mainFrame = new MainScreen();
		minim = new Minim(new ProcessingLegacy());
		mainFrame.init(this);
	}

	public AudioInput getLineIn(float sampleRate, int bufferSize){
		AudioInput input = this.minim.getLineIn(Minim.STEREO, bufferSize, sampleRate);
		return input;
	}
	
	public AudioOutput getLineOut(){
		return this.minim.getLineOut(Minim.STEREO);
	}

	public String fixAudioFilename(String filename ){
		// Add an extension if necessary
		String extension = "";
		if( filename.indexOf(".") >= 0 ){
			extension = filename.toLowerCase().substring(filename.lastIndexOf('.'));
		}
		if( !Arrays.asList(".wav", ".aiff").contains(extension) ){
			filename += ".wav"; 
		}
		
		return filename;
	}
	
	public AudioRecorder createRecorder(AudioInput source, String filename){
		return this.minim.createRecorder(source, filename);
	}

}
