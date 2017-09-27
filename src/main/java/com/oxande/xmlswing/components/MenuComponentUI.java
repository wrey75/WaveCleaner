package com.oxande.xmlswing.components;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

public class MenuComponentUI implements IComponent {
	public static final String VISIBLE = "visible";

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "name", "setName", ClassType.STRING ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( PROPERTIES );

	protected String varName = null;

	/**
	 * Return the name of the component.
	 * 
	 * @return the name of the component.
	 */
	public String getName(){
		return varName;
	}

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		throw new UnsupportedOperationException( "Not implemented for " + this.getClass().getName() );
	}

	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag {
		throw new UnsupportedOperationException( "Not implemented for " + this.getClass().getName() );
	}
}
