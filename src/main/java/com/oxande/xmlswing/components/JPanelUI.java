package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.w3c.dom.Element;

import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * Implementation of the JPanel. A JPanel can inherit
 * of a scroll pane if necessary (can useful when the frame
 * is resized).
 * 
 * 
 * @author wrey75
 * @version $Rev: 86 $
 *
 */
public class JPanelUI extends JComponentUI {
	
	public static String addScrollPane(String objName, JavaClass jclass, JavaMethod initMethod, Element root ){
		String hScroll = Parser.getAttribute(root, "hscroll");
		String vScroll = Parser.getAttribute(root, "vscroll");
		if( hScroll == null && vScroll == null ){
			// No scroll pane to initialize
			return null;
		}
		
		// We initialize a scroll pane...
		// See: http://download.oracle.com/javase/tutorial/uiswing/components/scrollpane.html
		jclass.addImport(JScrollPane.class);
		jclass.addImport(ScrollPaneConstants.class);

		String hsbPolicy = "ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED";
		if( hScroll != null ){
			if( hScroll.equals("always") ){
				hsbPolicy = "ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS";
			}
			else if( hScroll.equals("never") ){
				hsbPolicy = "ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER";
			}
		}

		String vsbPolicy = "ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED";
		if( vScroll != null ){
			if( vScroll.equals("always") ){
				vsbPolicy = "ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS";
			}
			else if( vScroll.equals("never") ){
				vsbPolicy = "ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER";
			}
		}

		String scrollName = objName + "Scroll";
		initMethod.addCode("JScrollPane " + scrollName +" = new JScrollPane(" + objName 
				+ "," + vsbPolicy 
				+ "," + hsbPolicy + ");");
		return scrollName;
	}


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
		
		String scrollName = JPanelUI.addScrollPane(varName,jclass,initMethod,root);
		return (scrollName == null ? varName : scrollName );
	}

}
