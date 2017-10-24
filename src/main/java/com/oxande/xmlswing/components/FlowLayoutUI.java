package com.oxande.xmlswing.components;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The flow layout is a layout manager, not a
 * component but it is managed as a JPanel component.
 * Can be managed transparently.
 * 
 * @author wrey75
 *
 */
public class FlowLayoutUI extends JPanelUI {
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		jclass.addImport( FlowLayout.class );
		jclass.addImport( JPanel.class );
		varName = Parser.addDeclaration( jclass, root, JPanel.class );
		
		// Create the layout...
		String layoutName = Parser.addDeclaration( jclass, root, FlowLayout.class);
		
		if( root.hasAttribute("hgap") ){
			initMethod.addCall(layoutName + ".setHgap", root.getAttribute("hgap"));
		}
		if( root.hasAttribute("vgap") ){
			initMethod.addCall(layoutName + ".setVgap", root.getAttribute("vgap"));
		}
		if( root.hasAttribute("alignment") ){
			String alignment = root.getAttribute("alignement").toUpperCase();
			initMethod.addCall(layoutName + ".setAlignment", "FlowLayout." + alignment);
		}
		
		initMethod.addCall(varName + ".setLayout", layoutName);
		
		List<Element> elements = Parser.getChildElements(root );
		for( Element e : elements ){
			String name = ComponentUI.parseComponent( jclass, initMethod, e );
			initMethod.addCall( varName+".add", name );
		}
		return varName;
	}
	
	public static String parseFlow( JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		FlowLayoutUI flow = new FlowLayoutUI();
		String varName = flow.parse(jclass, initMethod, root);
		return varName;
	}

}
