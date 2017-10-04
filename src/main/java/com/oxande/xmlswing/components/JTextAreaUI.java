package com.oxande.xmlswing.components;

import javax.swing.JTextArea;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The JTextArea implementation.
 * 
 * @author wrey75
 * @version $Rev: 47 $
 *
 */
public class JTextAreaUI extends JTextComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "cols", "setColumns", ClassType.INTEGER ),
		new AttributeDefinition( "wrap", "setLineWrap", ClassType.BOOLEAN ),
		new AttributeDefinition( "rows", "setRows", ClassType.INTEGER ),
		new AttributeDefinition( "wrapwords", "setWrapStyleWord", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JTextComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTextArea.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		String scrollName = JPanelUI.addScrollPane(varName,jclass,initMethod,root);
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE );
		return (scrollName == null ? varName : scrollName );
	}
	
	
}
