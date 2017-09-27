package com.oxande.xmlswing.components;

import javax.swing.JRadioButton;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The JRadioButton implementation.
 * 
 * @author wrey75
 * @version $rev$
 * 
 *
 */
public class JRadioButtonUI extends JToggleButtonUI {

	public static final AttributeDefinition[] PROPERTIES = {
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JToggleButtonUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JRadioButton.class );
		String text = Parser.getTextContents(root);
		initMethod.addCall( varName + ".setText", JavaCode.toParam(text) );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addGroup( varName, jclass, initMethod, root );
		addBooleanProperty(jclass, root, varName);
		return varName;
	}
	

}
