package com.oxande.wavecleaner.util;

public class ConvertUtils {
	public static boolean flt2bool( float f ){
		return (f > 0.5f);
	}
	
	public static float bool2flt( boolean b ){
		return (b ? 1.0f : 0.0f);
	}
}
