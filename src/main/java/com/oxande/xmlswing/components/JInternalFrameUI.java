package com.oxande.xmlswing.components;

import javax.swing.JInternalFrame;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * Implementation of the internal frame.
 * 
 * @author wrey75
 * @version $Rev$
 *
 */
public class JInternalFrameUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "closable", "setClosable", ClassType.BOOLEAN ),
		new AttributeDefinition( "icon", "setFrameIcon", ClassType.ICON ),
		new AttributeDefinition( "iconifiable", "setIconifiable", ClassType.BOOLEAN ),
		new AttributeDefinition( "maximizable", "setMaximizable", ClassType.BOOLEAN ),
		new AttributeDefinition( "resizable", "setResizable", ClassType.BOOLEAN ),
		new AttributeDefinition( "title", "setTitle", ClassType.STRING ),
		};

	public static final AttributesController CONTROLLER = new AttributesController(JComponentUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JInternalFrame.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		return varName;
	}
}
