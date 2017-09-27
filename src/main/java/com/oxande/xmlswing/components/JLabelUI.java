package com.oxande.xmlswing.components;


import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;



public class JLabelUI extends JComponentUI {

	public static final String TAGNAME = "label";
	
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "autoscrolls", "setAutoscrolls", ClassType.BOOLEAN ),
		new AttributeDefinition( "valign", "setVerticalAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "align", "setHorizontalAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "vtextalign", "setVerticalTextPosition", ClassType.ALIGNMENT ),
		new AttributeDefinition( "textalign", "setHorizontalTextPosition", ClassType.ALIGNMENT ),
		new AttributeDefinition( "gap", "setIconTextGap", ClassType.INTEGER ),
		new AttributeDefinition( "icon", "setIcon", ClassType.ICON ),
		new AttributeDefinition( "*", "setText", ClassType.TEXT ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public JLabelUI(){
	}
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, javax.swing.JLabel.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		
		String forComponent = Parser.getAttribute(root, "for");
		if( forComponent != null ){
			// The component already exists when called. 
			initMethod.addCall( varName +".setLabelFor", forComponent );
		}
		
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE );
		
		return varName;
	}
}
