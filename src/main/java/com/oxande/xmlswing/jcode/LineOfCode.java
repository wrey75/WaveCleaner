package com.oxande.xmlswing.jcode;

import java.io.IOException;
import java.io.Writer;

public class LineOfCode extends JavaCode {
	CharSequence lineOfCode = null;
	
	public LineOfCode( CharSequence code ){
		this(code,false);
	}

	public LineOfCode( CharSequence code, boolean trim ){
		String line = code.toString();
		if( trim ) line = line.trim();
		lineOfCode = line;
	}

	@Override
	protected void writeCode(Writer writer, int tabs) throws IOException {
		String tab = getTabulations(tabs);
		writer.append( tab ).append( lineOfCode ).append( CRLF );
	}
}
