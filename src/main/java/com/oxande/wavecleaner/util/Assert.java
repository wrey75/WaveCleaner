package com.oxande.wavecleaner.util;

/**
 * To do some checks.
 * 
 * @author wrey
 *
 */
final public class Assert {
	public final static void isTrue( boolean value ){
		if(!value){
			throw new IllegalArgumentException("Expected value is false.");
		}
	}
}
