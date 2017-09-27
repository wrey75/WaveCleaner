package com.oxande.xmlswing.components;

import javax.swing.JPopupMenu;


import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * 
 * @author wrey75
 * @version $Rev: 47 $
 *
 */
public class JPopupMenuUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "borderPainted", "setBorderPainted", ClassType.BOOLEAN ),
		new AttributeDefinition( "label", "setLabel", ClassType.STRING ),
		new AttributeDefinition( "lightweight", "setLightWeightPopupEnabled", ClassType.BOOLEAN ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JPopupMenu.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		JMenuUI.analyseMenus(jclass, initMethod, root, varName);
		return varName;
	}


}
