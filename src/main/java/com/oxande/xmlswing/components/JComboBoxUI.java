package com.oxande.xmlswing.components;

import javax.swing.JComboBox;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

public class JComboBoxUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "editable", "setEditable", ClassType.BOOLEAN ),
		new AttributeDefinition( "rows", "setMaximumRowCount", ClassType.INTEGER ),
		new AttributeDefinition( "popupVisible", "setPopupVisible", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController(JComponentUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JComboBox.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		JListUI.createSimpleMapEntry(jclass);
		JListUI.addItems(initMethod,root,varName);
		addSpecifics(jclass, initMethod, root);
		return varName;
	}

	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag {
		AbstractButtonUI.addActionListener(initMethod, jclass, root, varName);
	}
}
