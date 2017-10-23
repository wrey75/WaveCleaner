package com.oxande.xmlswing.components;

import javax.swing.JSlider;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * A slider (JSlider).
 * 
 * @author wrey
 * @version $Rev: 49 $
 *
 */
public class JSliderUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "maximum", "setMaximum", ClassType.INTEGER ),
		new AttributeDefinition( "minimum", "setMinimum", ClassType.INTEGER ),
		new AttributeDefinition( "majorTickSpace", "setMajorTickSpacing", ClassType.INTEGER ),
		new AttributeDefinition( "minorTickSpace", "setMinorTickSpacing", ClassType.INTEGER ),
		new AttributeDefinition( "orientation", "setOrientation", ClassType.VERTICAL_OR_HORIZONTAL ),
		new AttributeDefinition( "paintLabels", "setPaintLabels", ClassType.BOOLEAN ),
		new AttributeDefinition( "paintTicks", "setPaintTicks", ClassType.BOOLEAN ),
		new AttributeDefinition( "paintTrack", "setPaintTrack", ClassType.BOOLEAN ),
		new AttributeDefinition( "snap", "setSnapToTicks", ClassType.BOOLEAN ),
		new AttributeDefinition( "value", "setValue", ClassType.INTEGER ),
		new AttributeDefinition( "inverted", "setInverted", ClassType.BOOLEAN ),
		new AttributeDefinition( "extent", "setExtent", ClassType.INTEGER ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element e ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, e, JSlider.class );
		CONTROLLER.addToMethod(initMethod, e, varName);
		addSpecifics(jclass, initMethod, e);
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		super.addSpecifics(jclass, initMethod, root);
		addChangeListener(jclass, initMethod, root, varName);
	}

}
