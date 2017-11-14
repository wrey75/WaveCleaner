package com.oxande.wavecleaner.util;

import java.util.Objects;

import javax.swing.SwingUtilities;

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

	public final static void notNull( Object o ){
		if(o == null){
			throw new IllegalArgumentException("Expected value is null.");
		}
	}
	
	public final static void isEventDispatchThread(){
		if(!SwingUtilities.isEventDispatchThread()){
			throw new IllegalStateException("You must run this in the AWT Thread.");
		}
	}
	
	public final static void equals( Object a, Object b){
		if(!Objects.equals(a, b)){
			throw new IllegalArgumentException("Expected values must be equal.");
		}
	}
}
