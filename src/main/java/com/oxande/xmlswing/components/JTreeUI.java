package com.oxande.xmlswing.components;


import javax.swing.JTree;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;



public class JTreeUI extends JComponentUI {

	public static final String TAGNAME = "tree";
	
	public static final AttributeDefinition[] PROPERTIES = {
		DRAGGABLE_ATTRIBUTE_DEF,
		new AttributeDefinition( "editable", "setEditable", ClassType.BOOLEAN ),
		new AttributeDefinition( "expandsSelectedPaths", "setExpandsSelectedPaths", ClassType.BOOLEAN ),
		new AttributeDefinition( "invokeStopCellEditing", "setInvokesStopCellEditing", ClassType.BOOLEAN ),
		new AttributeDefinition( "largeModel", "setLargeModel", ClassType.BOOLEAN ),
		new AttributeDefinition( "rootVisible", "setRootVisible", ClassType.BOOLEAN ),
		new AttributeDefinition( "rowHeight", "setRowHeight", ClassType.INTEGER ),
		new AttributeDefinition( "scrollOnExpand", "setScrollOnExpand", ClassType.BOOLEAN ),
		new AttributeDefinition( "showRootHandles", "setShowRootHandles", ClassType.BOOLEAN ),
		new AttributeDefinition( "toogleClickCount", "setToogleClickCount", ClassType.INTEGER ),
		new AttributeDefinition( "visibleRowCount", "setVisibleRowCount", ClassType.INTEGER ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public JTreeUI(){
	}
	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTree.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		String scrollName = JPanelUI.addScrollPane(varName,jclass,initMethod,root);
		return (scrollName == null ? varName : scrollName );
	}
}
