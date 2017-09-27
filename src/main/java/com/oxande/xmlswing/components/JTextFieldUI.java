package com.oxande.xmlswing.components;

import javax.swing.JTextField;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * JTextField implementation.
 *  
 * @author wrey
 * @version $Rev: 85 $
 * 
 */
public class JTextFieldUI extends JTextAreaUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "align", "setHorizontalAlignment", ClassType.ALIGNMENT ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JTextAreaUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTextField.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE | JComponentUI.INTEGER_TYPE );
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		int cols = Parser.getIntegerAttribute(root, "cols", 15);
		if( cols > 0 ){
			initMethod.addCall(varName+".setColumns", JavaCode.toParam(cols));
		}
	}

}
