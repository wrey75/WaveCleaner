package com.oxande.xmlswing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import org.w3c.dom.Element;

import com.oxande.xmlswing.jcode.JavaMethod;



public class AttributesController {
	List<AttributeDefinition> list = new ArrayList<AttributeDefinition>();
	AttributesController parent = null;

	public AttributesController( AttributesController parent, AttributeDefinition[] arr ){
		this.parent = parent;
		this.list.addAll( Arrays.asList(arr) );
	}

	public AttributesController( AttributeDefinition[] arr ){
		this(null,arr);
	}

	/**
	 * @return the parent
	 */
	public AttributesController getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AttributesController parent) {
		this.parent = parent;
	}

	public void addToMethod( JavaMethod jmethod, Element e, String varName ) throws UnexpectedTag{
		if( parent != null ){
			parent.addToMethod(jmethod, e, varName);
		}
		for( AttributeDefinition def : list ){
			def.addToMethod(jmethod, e, varName);
		}
	}
	
}
