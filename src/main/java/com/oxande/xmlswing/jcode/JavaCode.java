package com.oxande.xmlswing.jcode;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


/**
 * Implements the basics for creating JAVA code. This class
 * includes only one method to implement ({@link #writeCode(Writer, int)}):
 * this is the very basic work for any JAVA code.
 * 
 * <p>
 * In addition, the class provides some helper methods to convert
 * single characters and strings to their JAVA code counterpart. These
 * methods can also be used for other basic JAVA types (numbers, dates).
 * </p>
 *
 * @author wrey75
 * @version $Rev: 92 $
 *
 */
public abstract class JavaCode {

	public static final String TABULATION = "   ";
	public static final String CRLF = System.getProperty("line.separator");
	
	/**
	 * The method to write the code into a stream.
	 * 
	 * @param writer a writer where the JAVA code is outputted.
	 * @param tabs the number of tabulations.
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract void writeCode( Writer writer, int tabs ) throws IOException;
	
	/**
	 * Retrieve a string containing the required tabulations.
	 * 
	 * @param tabs the number of tabulations.
	 * @return the representation of the tabulations.
	 */
	protected static String getTabulations( int tabs ){
		StringBuilder buf = new StringBuilder( tabs * TABULATION.length() );
		for(int i = 0; i < tabs; i++ ){
			buf.append( TABULATION );
		}
		return buf.toString();
	}
	
	/**
	 * Writes an empty line to the writer.
	 * 
	 * @param w the writer.
	 * @throws IOException if an I/O error occurs.
	 */
	protected static void println( Writer w ) throws IOException {
		w.write(CRLF);
	}
	
	/**
	 * Writes a string to the writer and goes to the next line.
	 * 
	 * @param w the writer.
	 * @throws IOException if an I/O error occurs.
	 */
	protected static void println( Writer w, String str ) throws IOException {
		w.write(str + CRLF);
	}

	/**
	 * Return an overview of the current code.
	 * 
	 */
	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PrintWriter w = new PrintWriter( out );
			writeCode(w, 0);
		} catch (IOException e) {
			// No I/O error is possible for a memory writer.
		}
		
		String ret;
		byte[] array = out.toByteArray();
		if( array.length > 80 ){
			ret = new String(array,0,72) + "...";
		}
		else {
			ret = new String(array); 
		}
		return ret;
	}
	
	/**
	 * Convert a Color to its parameter expression.
	 * The {@link Color} instance is transformed by
	 * the initialisation of its RGB counterpart
	 * (including the alpha if present).
	 * 
	 * @param c the color to convert
	 * @return a string compatible with a JAVA code
	 * 		source. 
	 */
	public static String toParam( Color c ){
		if( c == null ) return "null";
		StringBuilder buf = new StringBuilder( 30 );
		int red = c.getRed(); 
		int green = c.getGreen();
		int blue = c.getBlue();
		int alpha = c.getAlpha();
		buf.append("new ").append( Color.class.getName() ).append( "(" );
		buf.append(red).append(",").append(green).append(",").append(blue);
		if( alpha > 0 ){
			buf.append(",").append(alpha);
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * A string is transferred to its JAVA counterpart.
	 * 
	 * @param s the string.
	 * @return a string compatible for a JAVA code source.
	 */
	public static String toParam( CharSequence str ){
		return str2java(str);
	}
	
	/**
	 * Get the JAVA representation of a character. This
	 * representation is compatible with the JAVA specifications
	 * for string and characters.
	 * 
	 * <p>
	 *  NOTE: single quotes and double quotes are always returned
	 *  	prefixed by the antislash character.
	 * </p>
	 * 
	 * @param c the character.
	 * @return the character as a string or its expression
	 * 		compatible with JAVA (always a backslash followed
	 * 		by the character or a Unicode sequence).
	 */
	public static String getCharOf( char c ){
		switch(c){
		case '\"': return "\\\"";
		case '\'': return "\\\'";
		case '\\': return "\\";
		case '\b': return "\\b";
		case '\n': return "\\n";
		case '\r': return "\\r";
		case '\t': return "\\t";
		default:
			if( c < 32 || c > 127 ){
				// Use the UNICODE counterpart
				String hexa = Integer.toHexString(c);
				while( hexa.length() < 4 ) hexa = "0" + hexa;
				return "\\u" + hexa;
			}

			// Use a normal character.
			return Character.toString(c);
		}
	}

	
	/**
	 * Convert a string to its JAVA counterpart.
	 * 
	 * @param s a string.
	 * @return a new string with the quotes values and
	 * 		the characters expressed in the respect of the
	 * 		JAVA specification for sources. Return the
	 * 		text "<code>null</code>" if the string was a 
	 * 		<code>null</code> pointer.
	 */
	public static String str2java( CharSequence s ){
		if( s == null ) return "null";
		
		int len = s.length();
		StringBuilder buf = new StringBuilder(len + 10);
		buf.append('\"');
		int clen = 0;
		for( int i = 0; i < len; i++ ){
			char c = s.charAt(i);
			buf.append(getCharOf(c));
			if( c == '\n' || (clen++ > 120) ){
				// Split of the string on several lines... 
				buf.append("\"\n +    \"");
				clen = 0;
			}
		}
		buf.append('\"');
		return buf.toString();
	}
	
	/**
	 * Converts a boolean to its JAVA counterpart.
	 * 
	 * @param b a boolean value.
	 * @return "<code>null</code>", <code>"true"</code>
	 * 		or <code>"false"</code> depensing of the
	 * 		value of <i>b</i>.
	 */
	public static String toParam( Boolean b ){
		if( b == null ) return "null";
		return b.toString();
	}

	public static String toParam( Double val ){
		if( val == null ) return "null";
		return String.valueOf(val.doubleValue());
	}

	public static String toParam( int val ){
		return String.valueOf(val);
	}

	/**
	 * Return the character to its JAVA counterpart.
	 * 
	 * @param c the character to convert.
	 * @return The character inside simple quotes. If
	 * 		the character is below 32 or above 128, its
	 * 		JAVA representation is returned.
	 */
	public static String toParam( char c ){
		return "\'" + getCharOf(c) + "\'";
	}

	public static String toParam( Integer i ){
		if( i == null ) return "null";
		return i.toString();
	}

}
