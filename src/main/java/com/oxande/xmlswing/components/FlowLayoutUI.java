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
 * @author William R
 * @version $Rev$
 *
 */
public class FlowLayoutUI extends JPanelUI {
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		jclass.addImport( FlowLayout.class );
		jclass.addImport( JPanel.class );
		varName = Parser.addDeclaration( jclass, root, JPanel.class );
		initMethod.addCall(varName + ".setLayout", "new " + FlowLayout.class.getSimpleName() + "()");
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
