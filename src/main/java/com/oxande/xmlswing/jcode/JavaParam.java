package com.oxande.xmlswing.jcode;

public class JavaParam {
	JavaType type;
	String name;

	public JavaParam( String name, JavaType type ){
		this.type = type;
		this.name = name;
	}
	
	public JavaParam( String name, String className ){
		this(name,className,0);
	}

	public JavaParam( String name, Class<?> clazz ){
		this(name,clazz.getName(),0);
	}

	public JavaParam( String name, String classname, int arrayDepth ){
		this.name = name;
		this.type = new JavaType( classname, JavaType.PACKAGE, arrayDepth );
	}

	public String toString(){
		return type.toString() + " " + this.name;
	}
}
