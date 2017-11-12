package com.oxande.wavecleaner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.commons.compress.utils.Charsets;

/**
 * The configuration of the application.
 * 
 * @author wrey75
 *
 */
public class AppConfiguration {
	protected File configFile = null;
	private Properties config = new Properties();
	public static final int MAX_RECENT = 5;
	
	/**
	 * Create the configuration file
	 * 
	 * @param f
	 *            the file
	 */
	AppConfiguration(File f) {
		this.configFile = f;
		load();
	}

	/**
	 * Load the configuration.
	 * 
	 */
	private void load() {
		try {
			InputStream input = new FileInputStream(this.configFile);
			Reader in = new InputStreamReader(input);
			config.load(in);
		} catch (FileNotFoundException ex) {
			config = new Properties();
		} catch (IOException ex) {
			JOptionPane.showInputDialog(null, "Can not read the configuration. Using an empty one.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Save the configuration.
	 * 
	 */
	public void save() {
		try {
			OutputStream out = new FileOutputStream(this.configFile);
			Writer w = new OutputStreamWriter(out, Charsets.UTF_8.name());
			config.store(w, "Configuration as of " + new Date());
		} catch (IOException ex) {
			JOptionPane.showInputDialog(null, ex.getLocalizedMessage(), "Can not write the configuration", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public List<String> getRecentFiles(){
		List<String> list = new ArrayList<>();
		for(int i = 0; i < 5; i++){
			String file = config.getProperty("recent."+i);
			if( file != null ){
				list.add(file);
			}
		}
		return list;
	}
	
	
	public void addRecentFile(String recent){
		List<String> files = getRecentFiles();
		files.remove(recent); // to avoid duplicates
		files.add(0,recent); // add to first position
		while( files.size() > MAX_RECENT ){
			files.remove(MAX_RECENT-1);
		}
		int i = 0;
		for(String f : files){
			config.setProperty("recent."+i, f);
		}
		
		// in case MAX_RECENT is less than before
		while( config.remove("recent."+i) != null ){
			i ++; 
		}
		save();
	}
}
