package com.oxande.xmlswing.components;

import javax.swing.JMenuItem;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * 
 * @author wrey75
 * @version $Rev: 74 $
 *
 */
public class JMenuItemUI extends AbstractButtonUI {
	
	public static final String TAGNAME = "menuitem";
	public static final String SHORTCUT_ATTRIBUTE = "shortcut"; 
	
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "armed", "setArmed", ClassType.BOOLEAN ),
		new AttributeDefinition( SHORTCUT_ATTRIBUTE, "setAccelerator", ClassType.KEYSTROKE ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( AbstractButtonUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JMenuItem.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addTextAndMnemonic(root, initMethod, varName);
		addSpecifics(jclass, initMethod, root);
		// addAction(jclass,initMethod,root, true);
		return varName;
	}

}
