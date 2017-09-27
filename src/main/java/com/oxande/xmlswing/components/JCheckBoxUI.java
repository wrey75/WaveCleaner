package com.oxande.xmlswing.components;

import javax.swing.JCheckBox;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;

public class JCheckBoxUI extends JToggleButtonUI {

	public static final AttributeDefinition[] PROPERTIES = {
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JToggleButtonUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JCheckBox.class );
		String text = Parser.getTextContents(root);
		initMethod.addCall( varName + ".setText", JavaCode.toParam(text) );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addBooleanProperty(jclass, root, varName);
		return varName;
	}

}
