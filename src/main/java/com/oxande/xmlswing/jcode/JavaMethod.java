package com.oxande.xmlswing.jcode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class describing a method. The method must be attached to the
 * class.
 * 
 * @author wrey75
 * @version $Rev: 88 $
 *
 */
public class JavaMethod extends JavaBlock {
	// JavaBlock contents = new JavaBlock();
	JavaComments comments = new JavaComments();
	String name;
	JavaType returnType = new JavaType( "void" );
	
	/**
	 * @return the returnType
	 */
	public JavaType getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(JavaType returnType) {
		this.returnType = returnType;
	}

	List<JavaParam> params = new ArrayList<JavaParam>();
	/**
	 * @return the params
	 */
	public JavaParam[] getParams() {
		return params.toArray(new JavaParam[0]);
	}

	public JavaComments getComments(){
		return this.comments;
	}

	public void setComments( JavaComments comments ){
		this.comments = comments;
	}

	public void setComments( String comments ){
		this.comments = new JavaComments(comments);
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(JavaParam ... params) {
		this.params = Arrays.asList( params );
	}

	
	public JavaMethod(){
	}

	public JavaMethod( String name ){
		this();
		this.name = name;
	}

	public JavaMethod( String name, JavaParam ... params ){
		this(name);
		setParams(params);
	}



//	public void addCode( JavaCode code ){
//		contents.addCode(code);
//	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	@Override
	protected void writeCode(Writer writer, int tabs) throws IOException {
		String tab = getTabulations(tabs);
		if( comments != null) comments.writeCode(writer, tabs);
		writer.write( tab + returnType + " " + getName() + "(" );
		boolean firstParam = true;
		for( JavaParam param : params ){
			if( !firstParam ){
				writer.write(", ");
			}
			writer.write( param.toString() );
			firstParam = false;
		}
		writer.write( ")" + CRLF );
		super.writeCode(writer, tabs);
	}

	/**
	 * Add a line comment in the method. The
	 * comment starts with "//" and it is added
	 * at the end of the current code. 
	 * 
	 * @param comment the comment line to add.
	 * @see #setComments(JavaComments) to set
	 * 		the comments of the method. 
	 */
	public void addLineComment( String comment ){
		JavaComments cmt = new JavaComments();
		cmt.setJavaDoc(false);
		cmt.add(comment);
		addCode(cmt);
	}

}
