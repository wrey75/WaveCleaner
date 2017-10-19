package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JSplitPane;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * 
 * @author wrey75
 * @version $Rev: 47 $
 *
 */
public class JSplitPaneUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "orientation", "setOrientation", ClassType.JSPLITPANE_ORIENTATION ),
		new AttributeDefinition( "continuousLayout", "setContinuousLayout", ClassType.BOOLEAN ),
		new AttributeDefinition( "oneTouchExpandable", "setOneTouchExpandable", ClassType.BOOLEAN ),
		new AttributeDefinition( "dividerLocation", "setDividerLocation", ClassType.PERCENT ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( ContainerUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JSplitPane.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		
		int count = 0;
		List<Element> list = Parser.getChildElements(root);
		for( Element e : list ){
			String name = ComponentUI.parseComponent(jclass, initMethod, e);
			if( count == 0 ){
				initMethod.addCall(varName+".setTopComponent", name);
			}
			else if( count == 1 ){
				initMethod.addCall(varName+".setBottomComponent", name);
			}
			else {
				throw new IllegalArgumentException("Only 2 components for a split.");
			}
			count++;
		}
		return varName;
	}

}
