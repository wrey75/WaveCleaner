package com.oxande.xmlswing.components;



import org.w3c.dom.Element;

import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


public interface IComponent {

	/**
	 * Return the name of the component.
	 * 
	 * @return the name of the component.
	 */
	public String getName();
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element e ) throws UnexpectedTag;

	/**
	 * Add specificities to a component.
	 * 
	 * @param jclass the class of the implementation.
	 * @param initMethod the initialization method.
	 * @param root the element to analyse.
	 * @throws UnexpectedTag if there is something wrong during the analysis.
	 */
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag;
	
}
