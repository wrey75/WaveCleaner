package com.oxande.xmlswing.jcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Creates JAVA class. You can include in this
 * objects the methods, variables, imports 
 * (alphabeticaly ordered automatically),
 * static code and comments. The class can
 * implements interfaces and extend another class.
 * 
 * 
 * @author wrey75
 * @version $Rev: 135 $
 *
 */
public class JavaClass extends JavaCode {
	private String fullClassName = null;
	private String extClassName = null;
	private Set<String> implementInterfaces = new HashSet<String>(); 
	private JavaType javaType = new JavaType();
	private JavaComments comments = null;
	private LinesOfCode declarations = new LinesOfCode();
	private Set<String> importSet = new HashSet<String>(); 
	private Map<String, JavaMethod> methods = new HashMap<String, JavaMethod>();
	private LinesOfCode staticCode = new LinesOfCode();
	private Map<String,JavaClass> innerClasses = new HashMap<String, JavaClass>();
	private Properties props = new Properties();
	private Map<String,String> registered = new HashMap<String, String>();

	public void addStatic( JavaCode code ){
		staticCode.addCode(code);
	}

	public void addStatic( String line ){
		staticCode.addCode( new LineOfCode(line) );
	}

	/**
	 * Add the interface. Note once the class implements
	 * an interface, this interface is imported.
	 * 
	 * @param iClass the interface class. 
	 */
	public void addInterface( Class<?> iClass ){
		addImport(iClass);
		implementInterfaces.add(iClass.getSimpleName());
	}

	public void addInterface( String iClass ){
		implementInterfaces.add(iClass);
	}

	/**
	 * @return the comments
	 */
	public JavaComments getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(JavaComments comments) {
		this.comments = comments;
	}

	/**
	 * Retrieve a method based on its name. Note if
	 * several methods having the same name
	 * have been added to this class,
	 * only the first one is returned.
	 * 
	 * @param name the method name.
	 * @return the method if one exists with the specified
	 * 		name, if not return <code>null</code>.
	 */
	public JavaMethod getMethod( String name ){
		return methods.get(name);
	}

	public String addMethodIfNotExists( JavaMethod m ){
		if( getMethod( m.getName()) == null ){
			return addMethod(m);
		}
		return m.getName();
	}

	public String addMethod( JavaMethod m ){
		String root = m.getName();
		if( methods.containsKey(root) ){
			int i = 1;
			while( methods.containsKey(m.getName()+"."+i) ){
				i++;
			}
			root = m.getName() + "." + i;
		}
		methods.put( root, m);
		return root;
	}


	/**
	 * Add an anonymous declaration. It means you have to
	 * give all the declaration but the variable is not registred.
	 * 
	 * @param lineOfCode the line of code to add in the 
	 * 		declaration part of the class.
	 * @see #register(String, Class) for registring the
	 * 		variable (if necessary).
	 */
	public void addAnonymousDeclaration( CharSequence lineOfCode ) {
		declarations.addCode(lineOfCode);
	}

	/**
	 * Add a declaration. 
	 * 
	 * @param varName the variable name;
	 * @param type the Java type.
	 * @param params the parameters for the initialization. If
	 * 		<code>null</code>, there is no initialisation. A
	 * 		<code>null</code> array (or no parameter) means
	 * 		the variable is created empty.
	 */
	public void addDeclaration( String varName, JavaType type, String[] params ) {
		// jclass.addDeclaration( modifier + " " + className + " " + varName + " = new " + className + "();" );
		StringBuilder buf = new StringBuilder();
		buf.append( type ).append(" ").append(varName);
		if( params != null ){
			buf.append( " = new " ).append( type.getClassName() ).append("(");
			for( int i = 0; i < params.length; i++ ){
				if( i > 1 ) buf.append(", ");
				buf.append( params[i] );
			}
			buf.append(")");
		}
		buf.append(";");
		declarations.addCode(buf.toString());
	}
	
	/**
	 * Add the class specified as an import of
	 * the current class. It simplifies the code
	 * created.
	 * 
	 * @param className the class name.
	 * @see #addImport(Class)
	 */
	public void addImport( String className ){
		importSet.add(className);
	}

	/**
	 * Import a new class in the class. The class can
	 * be referenced with its simple name. For example,
	 * if you import the <code>java.util.List</code> class,
	 * you will be able to refer to it with <code>List</code>
	 * only.
	 * 
	 * <p>You can import the same class multiple times:
	 * the class name imported is registred to avoid
	 * duplicate lines in the final code.
	 * </p>
	 * 
	 * @param clazz the class to import.
	 */
	public void addImport( Class<?> clazz ){
		addImport( clazz.getName() );
	}

	/**
	 * Creates the class.
	 * 
	 * @param name the class name (a full name including
	 * 		the package; if not the class will be created
	 * 		in the default package).
	 */
	public JavaClass( String name ){
		this.fullClassName = name;
	}
	
	public JavaMethod getConstructor( JavaParam ... params ){
		JavaMethod constructor = new JavaMethod( this.getClassName() );
		constructor.setReturnType(new JavaType(""));
		constructor.setParams(params);
		addMethod(constructor);
		return constructor;
	}
	
	public String getClassName(){
		int pos = fullClassName.lastIndexOf(".");
		return (pos < 0 ? fullClassName : fullClassName.substring(pos+1) );
	}

	/**
	 * Checks if a class has been imported.
	 * 
	 * @param clazz the class to import.
	 * @return <code>true</code> if the class already
	 * 		exists in the import list, <code>false</code>
	 * 		in the other cases.
	 */
	public boolean isImported( Class<?> clazz ){
		return importSet.contains(clazz.getName());
	}
	
	public void setExtend( Class<?> clazz ){
		extClassName = (isImported( clazz ) ? clazz.getSimpleName() : clazz.getName());
	}

	public void setExtend( String clazzName ){
		extClassName = clazzName;
	}
	
	public static String name2file( String s ){
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < s.length(); i++ ){
			char c = s.charAt(i);
			switch( c ){
			case '.' : buf.append( File.separator ); break;
			default : buf.append(c); break;
			}
		}
		return buf.toString();
	}

	/**
	 * Get the package name for this class.
	 * 
	 * @return the package name
	 */
	public String getPackageName(){
		int pos = fullClassName.lastIndexOf(".");
		return (pos < 0 ? null : fullClassName.substring(0,pos) );
	}

	public void newAnonymousClass( String className, JavaClass clazz, String params ) throws IOException {
		
	}

	/**
	 * Write the class on the disk.
	 * 
	 * @param rootDir the root directory for the JAVA source
	 * 		code.
	 * @return the path of the JAVA class. 
	 * @throws IOException if an i/O error occurred.
	 */
	public String writeClass( String rootDir ) throws IOException {
		String packageName = getPackageName();
		String className = getClassName();
		
		String packageDir = "";
		if( packageName != null ){
			packageDir = File.separator + name2file( packageName );
		}
		String fileName = rootDir + packageDir + File.separator + className + ".java";
		
		Writer w = new OutputStreamWriter( new FileOutputStream(fileName) );
		if( packageName != null ){
			println( w, "package " + packageName + ";" );
		}
		
		// Import classes
		String[] importArray = importSet.toArray(new String[0]);
		Arrays.sort(importArray);
		String rootName = "";
		for( String importLibraryName : importArray ){
			String[] subNames = importLibraryName.split("\\.");
			if( !subNames[0].equals(rootName) ){
				println(w);
				rootName = subNames[0];
			}
			println( w, "import " + importLibraryName + ";" );
		}
		println(w);

		writeCode(w, 0,false);
		w.close();
		return fileName;
	}

	@Override
	protected void writeCode(Writer w, int tabs) throws IOException {
		writeCode(w, tabs,false);
	}

	public void writeAnonymous(Writer w, int tabs) throws IOException {
		writeCode(w, tabs, true);
	}

	public String toParam() {
		try {
			StringWriter w = new StringWriter(); 
			writeCode(w, 0, true);
			w.close();
			return w.getBuffer().toString();
		}
		catch(IOException ex ){
			throw new OutOfMemoryError("I/O Error:" + ex.getMessage());
		}
	}

	protected void writeCode(Writer w, int tabs, boolean anonymous) throws IOException {
		
		if( anonymous ){
			w.write("new " + getClassName() + "() " );
		}
		else {
			// Comments
			if( comments != null ) comments.writeCode(w, tabs);
		
			// Class declaration
			javaType.setClassName("class");
			w.write( javaType + " " + getClassName() );
			if( extClassName != null ) {
				w.write( " extends " + extClassName );
			}

			if( this.implementInterfaces.size() > 0 ){
				// Interfaces implemented.
				boolean first = true;
				for(String className : this.implementInterfaces){
					w.write( first ? " implements " : ", " );
					w.write(className);
					first = false;
				}
			}
		}
		w.write( " {" + JavaCode.CRLF );
		
		declarations.writeCode(w, tabs + 1);
		
		if( staticCode.size() > 0 ){
			String prefix = (anonymous ? "" : "static ");
			w.write( getTabulations(tabs+1) + prefix + "{" + JavaCode.CRLF );
			staticCode.writeCode(w, tabs+2);
			w.write( getTabulations(tabs+1) + "}" + JavaCode.CRLF );
		}

		for( JavaClass c : innerClasses.values() ){
			c.writeCode(w, tabs+1, false);
		}

		// Methods...
		for( JavaMethod m : methods.values() ){
			println(w);
			m.writeCode(w, tabs+1);
		}
		
		// Class ending
		w.write( "}" + JavaCode.CRLF + JavaCode.CRLF );
	}
	
	/**
	 * Register an object. The registration of an object
	 * is a simple way to mark the object to be owned
	 * by the class. It is just a flag (no code is
	 * associated). The registration is used to
	 * register a group or some similar stuff.
	 * 
	 * @param obj the name of the object to register.
	 * @param clazz the class of the object.
	 */
	public void register( String obj, Class<?> clazz ){
		registered.put(obj, clazz.getName());
	}

	public void register( String obj, String className ){
		registered.put(obj, className );
	}

	public boolean isRegistered( String obj ){
		String className = registered.get(obj);
		return (className != null);
	}
	
	public void addInnerClass( JavaClass jclass ){
		innerClasses.put( jclass.getClassName(), jclass );
		
	}
	
	/**
	 * Dummy method: returns 0x0104 for the release
	 * 1.5 of the JAVA classes.
	 * 
	 * @return the version of the java classes.
	 * 
	 */
	public int getVersion() {
		return 0x0105;
	}

    /**
     * Set a property to this class. A proprty has no meaning and is
     * not used to construct the class. Nevertheless, it can be helpful
     * to store some global information during the construction of the
     * class. 
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @see #getProperty
     */
	public void setProperty(String key, String value){
		props.setProperty(key, value);
	}

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the method returns
     * <code>null</code>.
     *
     * @param   key   the property key.
     * @return  the value in this property list with the specified key value.
     * @see     #setProperty
     */
	public String getProperty(String key){
		return props.getProperty(key);
	}
	
	public void setModifiers( int modifiers ){
		javaType.setAccess(modifiers);
	}
}
