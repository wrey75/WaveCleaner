package com.oxande.xmlswing.jcode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Store the comments. This class can store classic comments
 * but also the documented comments (for the javadoc utility).
 * 
 * @author wrey75
 * @version $Rev: 92 $
 *
 */
public class JavaComments extends JavaCode {

	private List<CharSequence> lineOfComments = new ArrayList<CharSequence>();

	public boolean isJavaDoc = true;
	
	
	public JavaComments(){
	}

	public JavaComments( String cmt ){
		this();
		add(cmt);
	}

	/**
	 * @return the isJavaDoc
	 */
	public boolean isJavaDoc() {
		return isJavaDoc;
	}

	/**
	 * @param isJavaDoc the isJavaDoc to set
	 */
	public void setJavaDoc(boolean isJavaDoc) {
		this.isJavaDoc = isJavaDoc;
	}

	
	/**
	 * Add one line of comment.
	 * 
	 * @param cmt the comment to add.
	 */
	public void add( String cmt ){
		lineOfComments.add(cmt);
	}

	/**
	 * Add several lines of comments.
	 * 
	 * @param cmt the comments to add.
	 */
	public void add( String ... cmt ){
		for( String s : cmt ){
			add(s);
		}
	}

	public void println( String ... lines ){
		if( lines.length == 0 ){
			add(""); // Empty line.
		}
		else {
			for( String line : lines ){
				if( line == null ) line = "";
				add( line );
			}
		}
	}

	@Override
	protected void writeCode(Writer writer, int tabs) throws IOException {
		if( lineOfComments.size() > 0 ){
			String tab = getTabulations(tabs);
			if( !isJavaDoc && lineOfComments.size() == 1 ){
				println( writer, tab + "// " + lineOfComments.get(0) );
			}
			else {
				writer.write( tab + "/*" );
				if( isJavaDoc ) writer.write("*");
				println( writer );
				for( CharSequence s : lineOfComments ){
					println( writer, tab + " * " + s );
				}
				println( writer, tab + " */" );
			}
		}
		else {
			// No comment...!?!
		}
	}

}
