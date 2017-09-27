package com.oxande.xmlswing.components;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

public class WindowUI extends ContainerUI {

	public static final String DEFAULT_LOCATION = "defaultLocation";
	
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( DEFAULT_LOCATION, "setLocationByPlatform", ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController(ContainerUI.CONTROLLER, PROPERTIES);

	public static void addWindowListener(JavaClass jclass, JavaMethod initMethod, Element root, String varName){
		
		JavaClass listenerClass = addListener(jclass, root, 
				new String[] {"onOpen:windowOpened",
								"onClose:windowClosing",
								"onClosed:windowClosed",
								"onIconize:windowIconified",
								"onDeiconize:windowDeiconified",
								"activated:windowActivated",
								"deactivated:windowDeactivated",
								"stateChanged:windowStateChanged",
								"onFocus:windowGainedFocus",
								"lostFocus:windowLostFocus"}, 
				WindowAdapter.class, WindowEvent.class);
		
		//String apdaterClassName = Parser.getUniqueId( "adapter" );

//		JavaClass winAdapter = new JavaClass(apdaterClassName);
//		winAdapter.setExtend( WindowAdapter.class );
		
		if( listenerClass != null ){
			// Add the inner class and related stuff..
			jclass.addInnerClass(listenerClass);
			initMethod.addCall(varName + ".addWindowListener", "new " + listenerClass.getClassName() + "()" );
		}
	}
}
