package com.oxande.wavecleaner.util;

public class NumberUtils {
	public static final int toInt(String value, int defaultValue){
		try {
			return Integer.parseInt(value);
		}
		catch(NumberFormatException ex ){
			return defaultValue;
		}
	}
}
