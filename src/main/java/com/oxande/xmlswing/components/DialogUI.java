package com.oxande.xmlswing.components;

import java.awt.Dialog;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The implementation if the dialog as a AWT object.
 * 
 * @author wrey75
 * @version $Rev: 74 $
 *
 */
public class DialogUI extends WindowUI {
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "title", "setTitle", ClassType.STRING ),
		new AttributeDefinition( "resizable", "setResizable", ClassType.BOOLEAN ),
		new AttributeDefinition( "undecorated", "setUndecorated", ClassType.BOOLEAN ),
		new AttributeDefinition( "modal", "setModal", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController(WindowUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, Dialog.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		super.addSpecifics(jclass, initMethod, root);
		addWindowListener(jclass, initMethod, root, varName);
	}
}
