package com.oxande.xmlswing.jcode;


public class JavaType {
	public static final int PACKAGE = 0x00;
	public static final int PUBLIC = 0x01;
	public static final int PRIVATE = 0x02;
	public static final int PROTECTED = 0x03;
	
	public static final int STATIC = 0x10;
	public static final int FINAL = 0x20;
	public static final int TRANSIENT = 0x40;

	String className;
	int access = PUBLIC;
	int arrayDepth = 0;

	public JavaType(){
		this(null);
	}

	public JavaType( String typeName ){
		this(typeName, PUBLIC);
	}

	public JavaType( String typeName, int modifiers ){
		this(typeName,modifiers,0);
	}

	public JavaType( String typeName, int modifiers, int depth ){
		this.className = typeName;
		this.access = modifiers;
		this.arrayDepth = depth;
	}

	/**
	 * @return the arrayDepth
	 */
	public int getArrayDepth() {
		return arrayDepth;
	}

	/**
	 * @param arrayDepth the arrayDepth to set
	 */
	public void setArrayDepth(int arrayDepth) {
		this.arrayDepth = arrayDepth;
	}

	public void setClass( Class<?> clazz ){
		setClassName( clazz.getName() ); 
	}

	public void setClassName( String className ){
		this.className = className; 
	}
	
	public String getClassName(){
		return this.className;
	}

	
	public String toString(){
		StringBuilder buf = new StringBuilder();
		int mode = access & 0x0f;
		switch( mode ){
		case PUBLIC : buf.append("public "); break;
		case PRIVATE : buf.append("private "); break;
		case PROTECTED : buf.append("protected "); break;
		case PACKAGE :
			// Empty
			break;
		default:
			buf.append( "/* " + mode + " unsupported */ " );
			break;
		}
		if( (access & STATIC) == STATIC ) buf.append("static ");
		if( (access & FINAL) == FINAL ) buf.append("final ");
		if( (access & TRANSIENT) == TRANSIENT ) buf.append("transient ");
		if( className != null ){
			buf.append( className );
			for(int i = 0; i < arrayDepth; i++ ){
				buf.append("[]");
			}
		}
		return buf.toString();
	}
	
	boolean isArray(){
		return arrayDepth > 0;
	}
	
	public void setArray( boolean v ){
		arrayDepth = ( v ? 1 : 0);
	}
	
	public void setAccess( int modifiers ){
		this.access = modifiers;
	}
}
