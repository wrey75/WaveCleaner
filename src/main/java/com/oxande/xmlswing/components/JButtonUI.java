package com.oxande.xmlswing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * JButton implementation.
 * 
 * @author wrey75
 * @version $Rev: 85 $
 *
 */
public class JButtonUI extends AbstractButtonUI {
	public static final String DEFAULT_BUTTON = "button.default";
	private boolean defaultButton = false;
	
	public static final String TAGNAME = "button";
	
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "default", "setDefaultCapable", ClassType.BOOLEAN ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( AbstractButtonUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JButton.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		super.addSpecifics(jclass, initMethod, root);
		addTextAndMnemonic(root, initMethod, varName );
		defaultButton = Parser.getBooleanAttribute(root, "default", false );
		if( defaultButton ){
			// Set the property to the class...
			jclass.setProperty(DEFAULT_BUTTON,varName);
		}
		
		jclass.addImport(ActionListener.class);
		jclass.addImport(ActionEvent.class);
		JavaClass actionClass = new JavaClass(ActionListener.class.getName());
		addActionListener(jclass, actionClass, root, true, varName);
		initMethod.addCall(varName+".addActionListener", actionClass.toParam() );
	}

	/**
	 * Return true if it is the default button.
	 *  
	 * @return the default button for the root pane. 
	 */
	public boolean isDefaultButton() {
		return defaultButton;
	}
}
