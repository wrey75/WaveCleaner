package com.oxande.xmlswing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The menu item for AWT.
 * 
 * TODO: debug this part. 
 * 
 * @author wrey75
 * @version $Rev$
 *
 */
public class MenuItemUI extends MenuComponentUI {
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "enabled", "setEnabled", ClassType.BOOLEAN ),
		new AttributeDefinition( "*", "setLabel", ClassType.TEXT ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( MenuComponentUI.CONTROLLER, PROPERTIES );
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JMenuItem.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addAction(jclass,initMethod,root, true);
		return varName;
	}
	
	protected void addAction( JavaClass jclass, JavaMethod initMethod, Element root, boolean withMessage ){
		jclass.addImport(AbstractAction.class);
		jclass.addImport(Action.class);
		jclass.addImport(ActionEvent.class );

		JavaClass actionClass = new JavaClass(ActionListener.class.getSimpleName());
		
		AbstractButtonUI.addActionListener(jclass, actionClass, root, withMessage, varName);
		initMethod.addCall(varName+".setAction", actionClass.toParam() );
	}


}
