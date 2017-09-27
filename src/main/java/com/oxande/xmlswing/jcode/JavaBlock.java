package com.oxande.xmlswing.jcode;

import java.io.IOException;
import java.io.Writer;


/**
 * A block of JAVA instructions.
 * 
 * @author wrey
 *
 */
public class JavaBlock extends LinesOfCode {

	public JavaBlock(){
	}

	public JavaBlock( JavaCode code ){
		addCode(code);
	}

	@Override
	protected void writeCode(Writer writer, int tabs) throws IOException {
		String tab = getTabulations(tabs);
		writer.write( tab + "{" + CRLF );
		super.writeCode(writer, tabs+1);
		writer.write( tab + "}" +CRLF );
	}
	
}
