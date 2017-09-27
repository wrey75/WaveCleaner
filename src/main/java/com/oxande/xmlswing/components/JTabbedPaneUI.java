package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JTabbedPane;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * 
 * @author wrey75
 * @version $Rev: 81 $
 *
 */
public class JTabbedPaneUI extends JComponentUI {

	public static final String TAGNAME = "tabs";
	public static final String TAB_TAG = "tab";
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element tab ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, tab, JTabbedPane.class );
		CONTROLLER.addToMethod(initMethod, tab, varName);
		List<Element> elements = Parser.getChildElements(tab);
		for( Element e : elements ){
			String tagName = e.getTagName();
			if( !tagName.equals(TAB_TAG) ){
				throw new IllegalArgumentException( "Only <tab> tags expected." );
			}
			parseTab( jclass, initMethod, e );
		}
		return varName;
	}
	
	protected String parseTab( JavaClass jclass, JavaMethod initMethod, Element e ) throws UnexpectedTag{
		JPanelUI panel = new JPanelUI();
		String panelName = panel.parse(jclass, initMethod, e);
		
		String iconParam = new AttributeDefinition( "icon", null, ClassType.ICON ).getParameterIfExist(e);
		String titleParam = new AttributeDefinition( "title", null, ClassType.STRING ).getParameterIfExist(e);
		if( titleParam == null ){
			throw new UnexpectedTag(e,"Attribute \"title\" expected for a <tag> element.");
		}
		String tooltipParam = new AttributeDefinition( "tooltip", null, ClassType.STRING ).getParameterIfExist(e);
		String[] params = new String[] { titleParam, iconParam, panelName, tooltipParam };
		initMethod.addCall( varName + ".addTab", params );
		return panelName;
	}

}
