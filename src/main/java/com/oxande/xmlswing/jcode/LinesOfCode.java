package com.oxande.xmlswing.jcode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class LinesOfCode extends JavaCode {
	List<JavaCode> lines = new ArrayList<JavaCode>();

	public LinesOfCode(){
	}
	
	public void addCode( JavaCode pieceOfCode ){
		lines.add( pieceOfCode );
	}

	public void addCode( CharSequence line ){
		JavaCode pieceOfCode = new LineOfCode(line);
		lines.add( pieceOfCode );
	}
	
	public void addCode( CharSequence ... lines ){
		for( CharSequence line : lines ){
			addCode(line);
		}
	}

	public static String getCallSyntax( String methodName, String ... params ){
		StringBuilder buf = new StringBuilder();
		buf.append(methodName).append("(");
		boolean first = true;
		for( String p : params ){
			if( !first ) buf.append(", ");
			buf.append(p);
			first = false;
		}
		buf.append(");");
		return buf.toString();
	}
	
	/**
	 * Add a call to a method. This is an helper method
	 * to add the necessary staff to call a method. 
	 * 
	 * @param methodName the method to call. 
	 * @param params the parameters to use expressed as {@link String}s.
	 * 
	 */
	public void addCall( String methodName, String ... params ){
		addCode(getCallSyntax(methodName, params));
	}

	@Override
	protected void writeCode(Writer writer, int tabs) throws IOException {
		for( JavaCode code : lines ){
			code.writeCode(writer, tabs );
		}
	}
	
	public int size(){
		return lines.size();
	}
}
