package com.oxande.xmlswing.components;

import javax.swing.JToggleButton;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * 
 * 
 * @author wrey
 * @version $Rev: 28 $
 *
 */
public class JToggleButtonUI extends AbstractButtonUI {
	public static final AttributeDefinition[] PROPERTIES = {
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( AbstractButtonUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JToggleButton.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addTextAndMnemonic(root, initMethod, varName);
		addGetterSetter(jclass, root, varName, "selected", JComponentUI.BOOLEAN_TYPE );
		return varName;
	}
	
}
