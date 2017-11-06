package com.oxande.wavecleaner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.oxande.wavecleaner.util.NumberUtils;

/**
 * A project is a simple class to store information about the project.
 * 
 * @author wrey75
 *
 */
public class AudioProject {
	private String name;
	
	public static class DiscSide {
		
	}
	
	private List<DiscSide> parts = new ArrayList<>();
	
	public AudioProject(){
		
	}
	
	/**
	 * Save the information relative to the project.
	 * 
	 * @throws IOException if an I/O error occurred.
	 */
	public void saveProject() throws IOException {
		FileOutputStream out = new FileOutputStream("");
		Properties config = new Properties();
		config.setProperty("struct", "1");
		config.setProperty("name", name);
		config.setProperty("sides", parts.size() + "");
		config.store(out, "The information about the project");
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
	
	
	private void loadVersion1(Properties config){
		this.name = config.getProperty("name");
		int sides = NumberUtils.toInt(config.getProperty("sides"), 0);
		for(int i = 0; i < sides; i++){
			String prefix = "side." + i + ".";
			
		}
	}
}
