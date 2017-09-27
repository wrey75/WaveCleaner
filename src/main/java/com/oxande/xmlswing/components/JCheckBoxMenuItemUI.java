package com.oxande.xmlswing.components;

import javax.swing.JCheckBoxMenuItem;



import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


public class JCheckBoxMenuItemUI extends JMenuItemUI {
	public static final String TAGNAME = "checkboxitem";

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "state", "setState", ClassType.BOOLEAN ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JMenuItemUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JCheckBoxMenuItem.class );
		addTextAndMnemonic(root, initMethod, varName);
		CONTROLLER.addToMethod(initMethod, root, varName);
		addAction(jclass,initMethod,root,false);
		addSpecifics(jclass, initMethod, root);
		addBooleanProperty(jclass, root, varName);
		return varName;
	}

}
