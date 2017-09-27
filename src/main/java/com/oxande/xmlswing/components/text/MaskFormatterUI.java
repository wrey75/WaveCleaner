package com.oxande.xmlswing.components.text;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;

public class MaskFormatterUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "invalidChars", "setInvalidCharacters", ClassType.STRING ),
		new AttributeDefinition( "validChars", "setValidCharacters", ClassType.STRING ),
		new AttributeDefinition( "valueContainsLiteral", "setValueContainsLiteralCharacters", ClassType.BOOLEAN ),
		//new AttributeDefinition( "mask", "setMask", ClassType.STRING ),
		new AttributeDefinition( "filler", "setPlaceholder", ClassType.STRING ),
		new AttributeDefinition( "placeholder", "setPlaceholderCharacter", ClassType.CHAR ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		String varName = Parser.addDeclaration( jclass, root, MaskFormatter.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		String mask = Parser.getAttribute(root, "mask", true );
		addMask(initMethod, varName, mask);
		return varName;
	}
	
	/**
	 * This method is needed due to the exception
	 * ParserException to be processed.
	 * 
	 * @param initMethod
	 * @param name
	 * @param mask
	 */
	public static void addMask( JavaMethod initMethod, String name, String mask ){
		initMethod.addCode("try {");
		initMethod.addCall( name + ".setMask", JavaMethod.toParam(mask) );
		initMethod.addCode("}");
		initMethod.addCode("catch( ParseException e ){}");
	}

}
