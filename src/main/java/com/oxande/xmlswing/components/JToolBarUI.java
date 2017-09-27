package com.oxande.xmlswing.components;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JToolBar;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The JToolBar implementation. A toolbar is not linked to
 * a frame but is independant and can be attached to any
 * panel.
 * 
 * @author wrey75
 * @version $Rev$
 *
 */
public class JToolBarUI extends JComponentUI {
	
	private String orientation = BorderLayout.NORTH;

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "floatable", "setFloatable", ClassType.BOOLEAN ),
		new AttributeDefinition( "rollover", "setRollover", ClassType.BOOLEAN ),
		new AttributeDefinition( "orientation", "setOrientation", ClassType.VERTICAL_OR_HORIZONTAL ),
	};

	public static final AttributesController CONTROLLER = new AttributesController(JComponentUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JToolBar.class );
		CONTROLLER.addToMethod(initMethod, root, varName);

		List<Element> elements = Parser.getChildElements(root);
		for( Element e: elements ){
			// TODO: Implement the toolbar elements.
			String tagName = e.getTagName();
			if( tagName.equals("action") ){
				// use the action
				String refName = Parser.getAttribute(e, "reference");
				if( refName == null ){
					throw new UnexpectedTag(root, "reference attribute expected");			
				}
				else {
					initMethod.addCall( varName + ".add", refName + ".getAction()" ); 
				}
			}
		}

		return varName;
	}

	/**
	 * @return the orientation
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
}
