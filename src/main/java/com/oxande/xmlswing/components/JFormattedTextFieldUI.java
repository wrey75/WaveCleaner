package com.oxande.xmlswing.components;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.components.text.MaskFormatterUI;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * JFormattedTextField implementation. This implementation
 * is using the default {@link MaskFormatter} capabilities
 * to ensure the formatting.
 * 
 * It is used in JCompta to limit the number of characters.
 *  
 * @author wrey
 * @version $Rev: 108 $
 * 
 */
public class JFormattedTextFieldUI extends JTextFieldUI {

	public static final String MASK_ATTRIBUTE = "mask";
	
	public static final AttributeDefinition[] PROPERTIES = {
	};

	public static final AttributesController CONTROLLER = new AttributesController( JTextFieldUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JFormattedTextField.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		addMask(jclass, root, initMethod, varName);
		addGetterSetter(jclass, root, varName, "text", JComponentUI.STRING_TYPE | JComponentUI.INTEGER_TYPE );
		return varName;
	}

	public void addMask(JavaClass jclass, Element root, JavaMethod initMethod, String varName ) throws UnexpectedTag{
		jclass.addImport( MaskFormatter.class );
		jclass.addImport( ParseException.class );
		jclass.addImport( DefaultFormatterFactory.class );
		String nameF = Parser.addDeclaration( jclass, null, DefaultFormatterFactory.class.getSimpleName(), Parser.ID_ATTRIBUTE );
		
		String mask = Parser.getAttribute(root, MASK_ATTRIBUTE );
		if( mask != null ){
			// The mask is defined directly, just we use it!
			String name = Parser.addDeclaration( jclass, null, MaskFormatter.class.getSimpleName(), Parser.ID_ATTRIBUTE );
			MaskFormatterUI.addMask( initMethod, name, mask );
			initMethod.addCall( nameF + ".setDefaultFormatter", name );
			initMethod.addCall( varName + ".setFormatterFactory", nameF );
		}
		else {
			Element e = Parser.getChildElement(root, MASK_ATTRIBUTE);
			if( e != null ){
				MaskFormatterUI component = new MaskFormatterUI();
				String name = component.parse(jclass, initMethod, root);
				initMethod.addCall( nameF + ".setDefaultFormatter", name );
				initMethod.addCall( varName + ".setFormatterFactory", nameF );
			}
			else {
				System.err.println("Element " + varName + " created without a mask.");
			}
		}
	}
	
}
