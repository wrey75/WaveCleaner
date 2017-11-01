package com.oxande.wavecleaner.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Used by Minim for challenging processing environment (see progressing.org).
 * 
 * @author wrey75
 *
 */
public class ProcessingLegacy {
	 public String sketchPath( String fileName ){
		 if( fileName.startsWith("~/") ){
			 String homeDir = System.getProperty("user.home");
			 return homeDir + fileName.substring(1);
		 }
		 return fileName;
	 }
	 
	 public InputStream createInput( String fileName ) throws FileNotFoundException{
		 File f = new File( sketchPath(fileName));
		 InputStream in = new FileInputStream(f);
		 return in;
	 }
}
