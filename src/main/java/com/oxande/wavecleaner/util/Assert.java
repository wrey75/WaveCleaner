package com.oxande.wavecleaner.util;

import java.util.Objects;

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
	
	public final static void equals( Object a, Object b){
		if(!Objects.equals(a, b)){
			throw new IllegalArgumentException("Expected values must be equal.");
		}
	}
}
