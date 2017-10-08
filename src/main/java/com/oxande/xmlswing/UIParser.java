package com.oxande.xmlswing;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.oxande.xmlswing.components.ComponentUI;
import com.oxande.xmlswing.components.FlowLayoutUI;
import com.oxande.xmlswing.components.JButtonUI;
import com.oxande.xmlswing.components.JDialogUI;
import com.oxande.xmlswing.components.JFrameUI;
import com.oxande.xmlswing.components.WindowUI;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaComments;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaParam;
import com.oxande.xmlswing.jcode.JavaType;

/**
 * Parser to create JAVA Swing code from an XML file.
 * 
 * @author wrey75
 *
 */
public class UIParser {
	String currentDir = "." + File.separator;
	private JavaClass jclass;
	private Document doc;
	boolean includeVersion = false;

	public Document parse( File f ) throws ParserConfigurationException, SAXException, IOException{
		return parseXML( new FileInputStream(f) );
	}
	
	public Document parseXML( InputStream in ) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false); // should be true
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse( in );
		return doc;
	}
	
	
//	protected String parseLabel(JavaClass jclass, Element e) throws UnexpectedTag{
//		JLabelUI label = new JLabelUI();
//		label.parse(jclass, initMethod, e);
//		return label.getName();
//	}



	private JavaComments getClassComments(){
		JavaComments comments = new JavaComments();
		comments.println( 
				"Class created automatically -- DO NOT UPDATE MANUALLY.",
				"This class has been created based on a XML file and must",
				"be extended by your own code. The following code only",
				"provide an easy way to obtain a basic GUI." );
		if( includeVersion ){
			// Include the version could have drawbacks
			// when used with a source version management (like CVS).
			comments.println("", "@version " + new Date() );
		}
		return comments;
	}
	
	private void addBaseClass( JavaClass jclass, Element root, Class<?> clazz ){
		// "extends" attribute
		String extName = Parser.getAttribute(root,"extends");
		if(extName == null){
			jclass.addImport( clazz );
			jclass.setExtend( clazz );
		}
		else {
			jclass.setExtend( extName );
		}
	}
	
	protected void packIfNeeded( Element root, JavaMethod method, String varName ){
		// "pack" attribute
		if( Parser.getBooleanAttribute(root,"pack",true) ){
			method.addCall( varName + ".pack" );
		}
	}
	
	private void setDefaulButtonIfNeeded(JavaClass jclass, JavaMethod initMethod){
		String buttonName = jclass.getProperty(JButtonUI.DEFAULT_BUTTON); 
		if( buttonName != null ){
			initMethod.addCall("getRootPane().setDefaultButton", buttonName);
		}
	}
	
	/**
	 * Parse a JDialog.
	 * 
	 * @param root the root element (the document root).
	 * @return the JAVA class.
	 * @throws UnexpectedTag if there an issue in the XML tags.
	 */
	public JavaClass parseDialog( Element root )  throws UnexpectedTag {
		JavaMethod initMethod = new JavaMethod( "initComponents" );
		String className = Parser.getAttribute(root,"name",true);
		jclass = new JavaClass( className );
		
		jclass.setComments( getClassComments() );
		addBaseClass(jclass, root, JDialog.class);
		

		// The content Pane is a border layout.
		jclass.addImport(JPanel.class);
		jclass.addImport( BorderLayout.class );
		String contentPaneName = Parser.addDeclaration(jclass, root, JPanel.class.getSimpleName(), "contentPane" );
		initMethod.addCall(contentPaneName + ".setLayout", "new BorderLayout()" );
		
		// Depending of the type, the objects are added to
		// the dialog directly (menu bar...) or to the content
		// pane.
		JFrameUI.addJMenuBar(jclass, initMethod, root, "this");
		JFrameUI.addWindowListener(jclass, initMethod, root, "this");

		List<Element> children = Parser.getChildElements(root);
		switch( children.size() ){
		case 0 :
			// Nothing defined as content pane -> Add an empty (and anonymous) label.
			jclass.addImport(Box.class);
			initMethod.addCall( contentPaneName + ".add", "Box.createGlue()", "BorderLayout.CENTER");
			break;

		case 1 :
			// Set the element as the CENTER of the JFrame. 
			String name = ComponentUI.parseComponent(jclass, initMethod, children.get(0));
			initMethod.addCall( contentPaneName + ".add", name, "BorderLayout.CENTER");
			break;

		default : 
			String layoutName = FlowLayoutUI.parseFlow(jclass, initMethod, root);
			initMethod.addCall(contentPaneName + ".add", layoutName, "BorderLayout.CENTER" );
			break;
		
		}
		initMethod.addCall("this.setContentPane", contentPaneName );

		setDefaulButtonIfNeeded(jclass, initMethod);
		packIfNeeded(root, initMethod, "this");
		Parser.setDefaultAttributeValue(root, WindowUI.DEFAULT_LOCATION, "true" );
		JDialogUI.CONTROLLER.addToMethod(initMethod, root, "this");

		jclass.addMethod( initMethod );

		// Add a method for testing purposes...
		JavaMethod main = new JavaMethod( "main" );
		main.setReturnType( new JavaType("void", JavaType.STATIC | JavaType.PUBLIC ));
		main.setParams( new JavaParam( "args", "String", 1) );
		main.addCode( jclass.getClassName() + " appl = new " + jclass.getClassName() + "();" );
		main.addCall( "appl." + initMethod.getName() );
		main.addCall( "appl.setDefaultCloseOperation", JFrame.class.getName() + ".EXIT_ON_CLOSE" );
		main.addCall( "appl.setVisible", JavaCode.toParam(true) );
		jclass.addMethod( main );
		return jclass;
	}

	/**
	 * Parse a JFrame.
	 * 
	 * @param root the root element (the document root).
	 * @return the JAVA class.
	 * @throws UnexpectedTag if there an issue in the XML tags.
	 */
	public JavaClass parseFrame( Element root )  throws UnexpectedTag {
		JavaMethod initMethod = new JavaMethod( "initComponents" );
		
		// "name" attribute
		String className = Parser.getAttribute(root,"name",true);
		jclass = new JavaClass( className );
		jclass.setComments( getClassComments() );
		addBaseClass( jclass, root, JFrame.class );
		

		// The content Pane is a border layout.
		jclass.addImport(JPanel.class);
		jclass.addImport( BorderLayout.class );
		String contentPaneName = Parser.addDeclaration(jclass, root, JPanel.class.getSimpleName(), "contentPane" );
		initMethod.addCall(contentPaneName + ".setLayout", "new BorderLayout()" );
		
		// Depending of the type, the objects are added to
		// the frame directly (menu bar) or to the content
		// pane.
		JFrameUI.addIconImage(jclass, initMethod, root, "this");
		JFrameUI.addJMenuBar(jclass, initMethod, root, "this");
		JFrameUI.addStatusBar(jclass, initMethod, root, contentPaneName );
		JFrameUI.addToolBars(jclass, initMethod, root, contentPaneName );
		JFrameUI.addWindowListener(jclass, initMethod, root, "this");

		List<Element> children = Parser.getChildElements(root);
		switch( children.size() ){
		case 0 :
			// Nothing defined as content pane -> Add an empty (and anonymous) label.
			jclass.addImport(Box.class);
			initMethod.addCall( contentPaneName + ".add", "Box.createGlue()", "BorderLayout.CENTER");
			break;

		case 1 :
			// Set the element as the CENTER of the JFrame. 
			String name = ComponentUI.parseComponent(jclass, initMethod, children.get(0));
			initMethod.addCall( contentPaneName + ".add", name, "BorderLayout.CENTER");
			break;

		default : 
//			String layoutName = Parser.addDeclaration( jclass, null, JPanel.class );
//			jclass.addImport(FlowLayout.class);
//			initMethod.addCall(layoutName + ".setLayout", "new FlowLayout()");
//			for( Element enode : children ){
//				name = parseBorderLayout( jclass, enode );
//				initMethod.addCall(layoutName + ".add", name);
//			}
			String layoutName = FlowLayoutUI.parseFlow(jclass, initMethod, root);
			initMethod.addCall(contentPaneName + ".add", layoutName, "BorderLayout.CENTER" );
			break;
		
		}
		initMethod.addCall("this.setContentPane", contentPaneName );

		Parser.setDefaultAttributeValue(root, WindowUI.DEFAULT_LOCATION, "true" );
		JFrameUI.CONTROLLER.addToMethod(initMethod, root, "this");

		setDefaulButtonIfNeeded(jclass, initMethod);
		packIfNeeded( root, initMethod, "this" );

		jclass.addMethod( initMethod );

		// Add a method for testing purposes...
		JavaMethod main = new JavaMethod( "main" );
		main.setReturnType( new JavaType("void", JavaType.STATIC | JavaType.PUBLIC ));
		main.setParams( new JavaParam( "args", "String", 1) );
		main.addCode( jclass.getClassName() + " appl = new " + jclass.getClassName() + "();" );
		main.addCall( "appl." + initMethod.getName() );
		main.addCall( "appl.setDefaultCloseOperation", JFrame.class.getName() + ".EXIT_ON_CLOSE" );
		main.addCall( "appl.setVisible", JavaCode.toParam(true) );
		jclass.addMethod( main );
		return jclass;
	}
	
	protected void clean(){
		Parser.clearIds();
	}

	public void parseInput( InputStream in ) throws Exception {
		Document doc = parseXML(in);
		Element rootElement = doc.getDocumentElement();
		String rootTag = rootElement.getNodeName();
		JavaClass clazz = null;
		clean();
		if( rootTag.equals("JFrame") ){
			clazz = parseFrame(rootElement);
		}
		else if( rootTag.equals("JDialog") ){
			clazz = parseDialog(rootElement);
		}
		else {
			throw new UnexpectedTag("No <" + rootTag + "> expected as root tag.");
		}
		String classFileName = clazz.writeClass(currentDir);
		System.out.println("Written [" + classFileName +"]" );
	}


	
	public static String capitalizeFirst( String str ){
		return Character.toUpperCase( str.charAt(0) ) + str.substring(1);
	}
	
	public static boolean isEmpty( String str ){
		return (str == null || str.trim().length() == 0);
	}
}
