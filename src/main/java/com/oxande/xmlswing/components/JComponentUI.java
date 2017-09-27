package com.oxande.xmlswing.components;

import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaParam;
import com.oxande.xmlswing.jcode.JavaType;

public class JComponentUI extends ContainerUI {
	
	public static final String PROPERTY_ATTR = "property";
	public static final String TOOLTIP_ATTRIBUTE = "tooltip";

	protected static final AttributeDefinition DRAGGABLE_ATTRIBUTE_DEF = new AttributeDefinition( "draggable", "setDragEnabled", ClassType.BOOLEAN );

	public static final int STRING_TYPE = 0x01;
	public static final int INTEGER_TYPE = 0x02;
	public static final int BOOLEAN_TYPE = 0x04;
	
	public static final AttributeDefinition[] PROPERTIES = {
			new AttributeDefinition( "autoscrolls", "setAutoscrolls", ClassType.BOOLEAN ),
			new AttributeDefinition( "background", "setBackground", ClassType.COLOR ),
			new AttributeDefinition( "doubleBuffered", "setDoubleBuffered", ClassType.BOOLEAN ),
			new AttributeDefinition( "enabled", "setEnabled", ClassType.BOOLEAN ),
			new AttributeDefinition( "foreground", "setForeground", ClassType.COLOR ),
			new AttributeDefinition( "opaque", "setOpaque", ClassType.BOOLEAN ),
			new AttributeDefinition( TOOLTIP_ATTRIBUTE, "setToolTipText", ClassType.STRING ),
		};
	
	public static final AttributesController CONTROLLER = new AttributesController( ContainerUI.CONTROLLER, PROPERTIES );

	public static String capitalizeFirst( String str ){
		return Character.toUpperCase( str.charAt(0) ) + str.substring(1);
	}
	
	/**
	 * Used for the property attribute. If an attribute is specified,
	 * we add a getter and a setter to be able to set and get directly
	 * the value bypassing the tread limitations.
	 * 
	 * @param jclass the main class.
	 * @param root the element on which the "property" attribute is set.
	 * @param varName the component name to use for setting and getting.
	 * @param property the property of the component (for example <code>text</code>
	 * 		will use <code>setText</code> to set the value and <code>getText</code>
	 * 		to retrieve it. 
	 * @param finalType
	 * @return
	 */
	public boolean addGetterSetter( JavaClass jclass, Element root, String varName, String property, int finalType ){
		String propName = root.getAttribute( PROPERTY_ATTR ).trim();
		if( propName.length() > 0 ){
			int type = STRING_TYPE;
			if( propName.endsWith("#") ){
				propName = propName.substring(0, propName.length() - 1); 
				type = INTEGER_TYPE;
			}
			else if( propName.endsWith("%") ){
				propName = propName.substring(0, propName.length() - 1); 
				type = BOOLEAN_TYPE;
			}
			else if( propName.endsWith("$") ){
				propName = propName.substring(0, propName.length() - 1); 
				type = STRING_TYPE;
			}
			else {
				type = finalType & STRING_TYPE;
				if( type == 0 ){
					type = finalType & INTEGER_TYPE;
				}
				if( type == 0 ){
					type = finalType & BOOLEAN_TYPE;
				}
			}
			
			JavaMethod getter = null;
			JavaMethod setter = null;
			String setterClassName = null;
			switch( type ){
			case STRING_TYPE :
				getter = new JavaMethod("get" + capitalizeFirst(propName));
				getter.setReturnType( new JavaType("String", JavaType.PUBLIC ));
				getter.addCode("return " + varName + ".get" + capitalizeFirst(property) + "();");
				setter = new JavaMethod("set" + capitalizeFirst(propName), new JavaParam("in", new JavaType("String", 0)));
				// setter.addCode(varName + ".set" + capitalizeFirst(property) + "(in);");
				setterClassName = createRunnable( jclass, varName, propName, "String");
				jclass.addImport(SwingUtilities.class);
				setter.addCall("SwingUtilities.invokeLater", "new " + setterClassName + "(in)" );
				break;
			case BOOLEAN_TYPE :
				getter = new JavaMethod("is" + capitalizeFirst(propName));
				getter.setReturnType( new JavaType("boolean", JavaType.PUBLIC ));
				getter.addCode("return " + varName + ".get" + capitalizeFirst(property) + "();");
				setter = new JavaMethod("set" + capitalizeFirst(propName), new JavaParam("in", new JavaType("boolean", 0)));
				setter.addCode(varName + ".set" + capitalizeFirst(property) + "(in);");
//				setterClassName = createRunnable( jclass, varName, propName, "boolean");
//				jclass.addImport(SwingUtilities.class);
//				setter.addCall("SwingUtilities.invokeLater", "new " + setterClassName + "(in)" );
				break;
			case INTEGER_TYPE :
				getter = new JavaMethod("get" + capitalizeFirst(propName));
				getter.setReturnType( new JavaType("int", JavaType.PUBLIC ));
				getter.addCode("return Integer.parseInt(" + varName + ".get" + capitalizeFirst(property) + "());");
				setter = new JavaMethod("set" + capitalizeFirst(propName), new JavaParam("in", new JavaType("int", 0)));
				// setter.addCode(varName + ".set" + capitalizeFirst(property) + "(String.valueOf(in));");
				setterClassName = createRunnable( jclass, varName, propName, "int");
				jclass.addImport(SwingUtilities.class);
				setter.addCall("SwingUtilities.invokeLater", "new " + setterClassName + "(in)" );
				break;
			default :
				throw new IllegalArgumentException("Property does not support the requested type.");
			}
			jclass.addMethod(getter);
			jclass.addMethod(setter);
			return true;
		}
		return false;
	}
	
	protected String createRunnable( JavaClass jclass, String varName, String property, String clazzName ){
		jclass.addImport(Runnable.class);
		String subClassName= "Set" + capitalizeFirst(property) +"Class";
		JavaClass subClass = new JavaClass(subClassName);
		subClass.setModifiers( JavaType.PRIVATE );
		subClass.addInterface(Runnable.class);
		subClass.addAnonymousDeclaration("private " + clazzName + " input;");
		JavaMethod constructor = subClass.getConstructor(new JavaParam( "input", clazzName ));
		constructor.addCode("this.input = input;");

		JavaMethod run = new JavaMethod("run" );
		run.addCode(varName + ".setText(String.valueOf(input));");
		subClass.addMethod(run);
		jclass.addInnerClass(subClass);
		return subClassName;
	}
	

	public static int getMnemonicInformation( String text, StringBuilder cleanText, StringBuilder mnemonic ){
		cleanText.setLength(0);
		mnemonic.setLength(0);
		int pos = text.indexOf('_');
		if( pos >= 0 ){
			if( pos > 0) cleanText.append( text.substring(0, pos) );
			cleanText.append( text.substring(pos+1) );
			char car = Character.toUpperCase( text.charAt(pos+1) );
			switch( car ){
			case ' ':
				mnemonic.append( KeyEvent.class.getName() + ".VK_SPACE" );
				break;
			default:
				if( (car < 0 && car > 9) || (car < 'A' && car > 'Z') ){
					pos = -1; // Invalid mnemonic
					
				}
				else {
					mnemonic.append( KeyEvent.class.getName() + ".VK_" + car );
				}
				break;
			}
		}
		else {
			cleanText.append(text);
		}
		return pos;
	}


	public static void addTextAndMnemonic(Element root, JavaMethod m, String varName){
		String text = getTextAttribute(root);
		if( text != null ){
			StringBuilder realText = new StringBuilder();
			StringBuilder mnemonic = new StringBuilder();
			int pos = getMnemonicInformation( text, realText, mnemonic );
			m.addCall(varName+".setText", JavaCode.toParam(realText.toString()) );
			if( pos >= 0 ){
				m.addCall(varName+".setMnemonic", mnemonic.toString() );
				m.addCall(varName+".setDisplayedMnemonicIndex", JavaCode.toParam(pos) );
			}
		}
	}
	
	/**
	 * Any {@link JComponent} can have its own border.
	 * Currently, adding a "title" attribute creates
	 * a component with a titled border.
	 * 
	 * @param root the root element.
	 * @param m the method in charge of the initialization.
	 * @param varName the name of the component.
	 * @throws UnexpectedTag the &lt;border&gt; is not yet supported.
	 * 
	 */
	public static void addBorder( Element root, JavaMethod m, String varName) throws UnexpectedTag{
		Element border = Parser.getChildElement(root, "border");
		if( border == null ){
			String title = Parser.getAttribute(root, "title");
			if( title != null ){
				String name = Parser.getUniqueId( "border" );
				m.addCode( TitledBorder.class.getName() + " " + name + " = new " + TitledBorder.class.getName() 
						+ "(" + JavaCode.toParam(title) + ");" );
				m.addCall( varName+".setBorder", name);
			}
		}
		else {
			throw new UnexpectedTag(root,"<border> not yet available.");
		}
	}
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag {
		varName = Parser.addDeclaration(jclass, root, JCheckBox.class);
		// String text = Parser.getTextContents(root);
		// CONTROLLER.addToMethod(initMethod, root, varName);
		return varName;
	}

}
