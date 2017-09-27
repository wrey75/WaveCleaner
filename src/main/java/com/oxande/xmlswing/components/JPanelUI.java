package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JPanel;



import org.w3c.dom.Element;

import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * Implementation of the JPanel. A JPanel can inherit
 * of a scrollpane if necessary (can useful when the frame
 * is resized).
 * 
 * 
 * @author wrey75
 * @version $Rev: 86 $
 *
 */
public class JPanelUI extends JComponentUI {

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JPanel.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		List<Element> childs = Parser.getChildElements(root);
		for( Element e : childs ){
			String name = JComponentUI.parseComponent(jclass, initMethod, e);
			initMethod.addCall(varName+".add", name );
		}
		
		if( !root.getTagName().equals( JTabbedPaneUI.TAB_TAG ) ){
			// Forget a border for tabbed panes.
			addBorder( root, initMethod, varName);
		}
		
		String scrollName = JTextAreaUI.addScrollPane(varName,jclass,initMethod,root);
		return (scrollName == null ? varName : scrollName );
	}

}
