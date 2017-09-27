package com.oxande.wavecleaner.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Used by Minim for challenging processing environnement (see progressing.org).
 * 
 * @author wrey
 *
 */
public class ProcessingLegacy {
	 public String sketchPath( String fileName ){
		 return fileName;
	 }
	 
	 public InputStream createInput( String fileName ) throws FileNotFoundException{
		 File f = new File( sketchPath(fileName));
		 InputStream in = new FileInputStream(f);
		 return in;
	 }
}
