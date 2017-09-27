package com.oxande.xmlswing.components;

import javax.swing.JPasswordField;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The implementation of the JPasswordField (input of
 * a password).
 * 
 * @author wrey75
 * @version $Rev: 66 $
 *
 */
public class JPasswordFieldUI extends JTextFieldUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "echo", "setEchoChar", ClassType.CHAR ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JTextFieldUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JPasswordField.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		return varName;
	}

}
