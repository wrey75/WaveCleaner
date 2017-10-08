package com.oxande.xmlswing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Xml4Swing {
	/**
	 * The main class for the parser. Note the parser generates JAVA
	 * classes then you have to run this before you test your application.
	 * 
	 * @param args the arguments
	 * @throws Exception if an exception ocured.
	 */
	public static void main( String[] args ) throws Exception {
		UIParser ui = new UIParser();

		for(int i = 0; i < args.length; i++ ){
			if( args[i].charAt(0) == '-' ){
				if(args[i].length() == 1){
					// Following the UNIX convention, if the file
					// is "-", the standard input is used.
					ui.parseInput(System.in);
				}
				else {
					char opt = args[i].charAt(1);
					switch(opt){
					case 'd' :
						// The directory
						ui.currentDir = args[++i];
						if( ui.currentDir.endsWith( File.separator )){
							ui.currentDir += File.separator;
						}
						break;

					case 'V' :
						// Include the version.
						ui.includeVersion = true;
						break;

					default :
						System.err.println("Option " + args[i] + " unkown. Please read the documentation.");
						System.exit(1);
					}
				}
			}
			else {
				// Parse the specified file.
				File f = new File( args[i] );
				if( !f.exists() ){
					System.err.println("The file '"+f.getAbsolutePath()+"' does not exists.");
					System.exit(1);
				}
				ui.parseInput( new BufferedInputStream( new FileInputStream(f)));
			}
		}
	}
	
}
