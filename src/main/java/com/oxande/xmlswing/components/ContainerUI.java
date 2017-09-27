package com.oxande.xmlswing.components;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.AttributeDefinition.ClassType;


public class ContainerUI extends ComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "focusCycleRoot", "setFocusCycleRoot", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( ComponentUI.CONTROLLER, PROPERTIES );

}
