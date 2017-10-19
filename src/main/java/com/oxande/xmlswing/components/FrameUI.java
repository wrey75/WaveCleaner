package com.oxande.xmlswing.components;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * Implementation of a Frame. You should never use
 * directly this class as the normal implementation
 * comes from the {@link JFrameUI} class.
 * 
 * @author wrey75
 *
 */
public class FrameUI extends WindowUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "title", "setTitle", ClassType.STRING ),
		new AttributeDefinition( "resizable", "setResizable", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController(WindowUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JFrame.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addIconImage(jclass,initMethod,root,varName);
		return varName;
	}
	
	public static void addIconImage(JavaClass jclass, JavaMethod initMethod, Element root, String varName){
		String icon = Parser.getAttribute(root, "icon");
		if( icon != null ){
			jclass.addImport(ImageIcon.class);
			String theIcon = "(new ImageIcon(getClass().getResource(" + JavaCode.toParam(icon) + "))).getImage()";
			initMethod.addCall(varName + ".setIconImage", theIcon );
		}			
	}

}
