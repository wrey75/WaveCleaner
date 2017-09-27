package com.oxande.xmlswing.components;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * Radio button for menu.
 * 
 * @author wrey
 * @version $Rev: 42 $
 * 
 */
public class JRadioButtonMenuItemUI extends JMenuItemUI {
	public static final String TAGNAME = "radioitem";

	public static final AttributeDefinition[] PROPERTIES = {
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JMenuItemUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		jclass.addImport(JRadioButtonMenuItem.class);
		varName = Parser.addDeclaration( jclass, root, JRadioButtonMenuItem.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addTextAndMnemonic(root, initMethod, varName);
		addAction(jclass,initMethod,root,false);
		addGroup( varName, jclass, initMethod, root );
		addBooleanProperty(jclass,root,varName);
		UIManager.getSystemLookAndFeelClassName();
		return varName;
	}
	
	
}
