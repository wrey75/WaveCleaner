package com.oxande.wavecleaner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.NumberUtils;

/**
 * A project is a simple class to store information about the project.
 * 
 * @author wrey75
 *
 */
public class AudioProject {
	public static String DEFAULT_EXT = ".wclean";
	
	private String name;
	private boolean saved;
	private ListenerManager<ProjectListener> listenerManager = new ListenerManager<>();
	
	/**
	 * A project can contain more than 1 side of the disc.
	 * 
	 * @author wrey75
	 *
	 */
	public static class DiscSide {
		String sourceFile;
		
	}
	
	private List<DiscSide> parts = new ArrayList<>();
	
	public AudioProject(){
		
	}
	
	public void addListener(ProjectListener listener){
		this.listenerManager.add(listener);
	}
	
	public void removeListener(ProjectListener listener){
		
	}
	
	public String getName(){
		return (this.name == null ? "New project" : this.name);
	}
	
	
	/**
	 * Save the information relative to the project.
	 * 
	 * @throws IOException if an I/O error occurred.
	 */
	public void saveProject(String name) throws IOException {
		if(!name.toLowerCase().endsWith( DEFAULT_EXT )){
			name += DEFAULT_EXT;
		}
		File f = new File(name);
		FileOutputStream out = new FileOutputStream(f);
		Properties config = new Properties();
		config.setProperty("struct", "1");
		config.setProperty("name", name);
		config.setProperty("sides", parts.size() + "");
		for( int i = 0; i < parts.size(); i++ ){
			DiscSide side = parts.get(i);
			String prefix = "side." + i + ".";
			config.setProperty(prefix + "source", side.sourceFile );
		}
		this.name = f.getName();
		config.store(out, "The information about the project");
		this.saved = true;
	}
	
	protected void updated(){
		this.saved = false;
	}
	
	public void addSource(File f){
		DiscSide source = new DiscSide();
		source.sourceFile = f.getPath();
		this.parts.add(source);
		this.updated();
	}
	
	public void loadProject(String fileName) throws IOException {
		InputStream input = new FileInputStream(fileName);
		Properties config = new Properties();
		config.load(input);
		try {
			int fileStructure = Integer.parseInt(config.getProperty("struct"));
			switch(fileStructure){
				case 1: 
					loadVersion1(config);
					break;
			}
			throw new IOException("The audio project has been saved by a newer version. We can not load it.");
		}
		catch(NumberFormatException ex ){
			throw new IOException("Not a valid audio project.");
		}
	}
	
	/**
	 * Load the information stored in the configuration file.
	 * 
	 * @param config the configuration file
	 */
	private void loadVersion1(Properties config){
		this.parts.clear();
		this.name = config.getProperty("name");
		int sides = NumberUtils.toInt(config.getProperty("sides"), 0);
		for(int i = 0; i < sides; i++){
			DiscSide side = new DiscSide();
			
			String prefix = "side." + i + ".";
			side.sourceFile = config.getProperty(prefix + "source", null);
			this.parts.add(side);
		}
	}
}
