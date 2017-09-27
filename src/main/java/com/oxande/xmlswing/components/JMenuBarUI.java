package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JMenuBar;

import org.w3c.dom.Element;

import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * Menu bar for applets and frames.
 * 
 * @author wrey
 * @version $Id: JMenuBarUI.java 43 2010-10-16 13:47:15Z wrey75 $
 *
 */
public class JMenuBarUI extends JComponentUI {

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JMenuBar.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		List<Element> menus = Parser.getChildElements(root);
		for( Element e : menus ){
			String tagName = e.getTagName();
			if( tagName.equals(JMenuUI.TAGNAME) ){
				JMenuUI menu = new JMenuUI();
				String menuName = menu.parse(jclass, initMethod, e);
				initMethod.addCall( varName+".add", menuName );
				
				if( Parser.getBooleanAttribute(e, "helpmenu", false) ){
					// NOT YET IMPLEMENTED -- TO BE REMOVE IN FUTURE VERSIONS OF JAVA???
					// initMethod.addCall(varName+".setHelpMenu", menuName);
				}
			}
		}
		return varName;
	}

}
