package com.oxande.xmlswing.components;

import javax.swing.JDialog;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

/**
 * The implementation of a dialog box.
 * 
 * @author wrey75
 *
 */
public class JDialogUI extends DialogUI {
	public static final AttributeDefinition[] PROPERTIES = {
	};

	public static final AttributesController CONTROLLER = new AttributesController(DialogUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JDialog.class );
		addSpecifics(jclass, initMethod, root);
		CONTROLLER.addToMethod(initMethod, root, varName);
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		super.addSpecifics(jclass, initMethod, root);
		JFrameUI.addJMenuBar(jclass, initMethod, root, varName);
	}

}
