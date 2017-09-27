package com.oxande.xmlswing.components;

import javax.swing.text.JTextComponent;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * The JTextComponent implementation.
 * 
 * @author wrey75
 * @version $Rev: 85 $
 *
 */
public class JTextComponentUI extends JComponentUI {
	

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "caretColor", "setCaretColor", ClassType.COLOR ),
		new AttributeDefinition( "disabledTextColor", "setDisabledTextColor", ClassType.COLOR ),
		new AttributeDefinition( "selectionColor", "setSelectionColor", ClassType.COLOR ),
		DRAGGABLE_ATTRIBUTE_DEF,
		new AttributeDefinition( "editable", "setEditable", ClassType.BOOLEAN ),
		new AttributeDefinition( "*", "setText", ClassType.TEXT ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTextComponent.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE );
		return varName;
	}
	
	

}
