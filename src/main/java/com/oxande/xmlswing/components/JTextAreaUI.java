package com.oxande.xmlswing.components;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The JTextArea implementation.
 * 
 * @author wrey75
 * @version $Rev: 47 $
 *
 */
public class JTextAreaUI extends JTextComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "cols", "setColumns", ClassType.INTEGER ),
		new AttributeDefinition( "wrap", "setLineWrap", ClassType.BOOLEAN ),
		new AttributeDefinition( "rows", "setRows", ClassType.INTEGER ),
		new AttributeDefinition( "wrapwords", "setWrapStyleWord", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JTextComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTextArea.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		String scrollName = addScrollPane(varName,jclass,initMethod,root);
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE );
		return (scrollName == null ? varName : scrollName );
	}
	
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

		String scrollName = Parser.getUniqueId("scrollPane");
		initMethod.addCode("JScrollPane " + scrollName +" = new JScrollPane(" + objName 
				+ "," + vsbPolicy 
				+ "," + hsbPolicy + ");");
		return scrollName;
	}

}
