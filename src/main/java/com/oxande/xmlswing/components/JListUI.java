package com.oxande.xmlswing.components;

import java.util.List;

import javax.swing.JList;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaParam;
import com.oxande.xmlswing.jcode.JavaType;

/**
 * For JList implementation.
 * 
 * @author wrey
 *
 */
public class JListUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		DRAGGABLE_ATTRIBUTE_DEF,
			new AttributeDefinition( "fixedHeight", "setFixedCellHeight", ClassType.INTEGER ),
			new AttributeDefinition( "fixedWidth", "setFixedCellWidth", ClassType.INTEGER ),
			new AttributeDefinition( "orientation", "setLayoutOrientation", ClassType.JLIST_ORIENTATION ),
			new AttributeDefinition( "valueIsAdjusting", "setValueIsAdjusting", ClassType.BOOLEAN ),
			new AttributeDefinition( "rows", "setVisibleRowCount", ClassType.INTEGER ),
		};
	
	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		jclass.addImport(JList.class);
		varName = Parser.addDeclaration( jclass, root, JList.class );

		createSimpleMapEntry(jclass);
		addItems(initMethod, root, varName);
//		Element list = Parser.getChildElement(root, "data");
//		if( list != null ){
//			initMethod.addCode(varName + ".setListData( new String[] { " );
//			List<Element> values = Parser.getChildElements(list, "value");
//			int selected = -1;
//			int count = 0;
//			for( Element e : values ){
//				String v = e.getTextContent();
//				initMethod.addCode( "\t" + JavaCode.toParam(v) + "," );
//				if( v == null ) v = "";
//				if( Parser.getBooleanAttribute(e, "selected", false) ){
//					selected = count;
//				}
//				count++;
//			}
//			initMethod.addCode( "\t} );" );
//			if( selected > 0 ){
//				initMethod.addCall(varName + ".setSelectedIndex", JavaCode.toParam(selected));
//			}
//		}
		
		CONTROLLER.addToMethod(initMethod, root, varName);
		String scrollName = JPanelUI.addScrollPane(varName,jclass,initMethod,root);
		return (scrollName == null ? varName : scrollName );
	}

	public static void addItems(JavaMethod initMethod, Element root, String varName){
		List<Element> options = Parser.getChildElements(root, "option");
		int count = 0;
		for( Element e : options ){
			String value = e.getTextContent();
			String key = Parser.getAttribute(e, "value");
			if( key == null ) key = value;
			initMethod.addCall( varName+".addItem", "new SimpleMapEntry( " + JavaCode.toParam(key) + ", "  + JavaCode.toParam(value) + ")");
			if( Parser.getBooleanAttribute(e, "selected", false) ){
				initMethod.addCall( varName+".setSelectedIndex", JavaCode.toParam(count) );
			}
			count++;
		}
	}

	public static void createSimpleMapEntry( JavaClass jclass ){
		JavaClass sme = new JavaClass("SimpleMapEntry");
		if( jclass.getVersion() > 0x0104 ){
			// For JAVA above 1.04
			// Do not hesitate: gives a real map entry!
			sme.addInterface( "java.util.Map.Entry<String,String>");
		}
		else {
			sme.addInterface( "java.util.Map.Entry");
		}
		
		sme.addDeclaration("key", new JavaType("String", JavaType.PRIVATE), null);
		sme.addDeclaration("value", new JavaType("String", JavaType.PRIVATE), null);
		
		JavaMethod m = new JavaMethod("SimpleMapEntry",
				new JavaParam("key",String.class),
				new JavaParam("value",String.class));
		m.setReturnType(new JavaType("",JavaType.PUBLIC));
		m.addCode("this.key = key;");
		m.addCode("this.value = value;");
		sme.addMethod(m);
		
		m = new JavaMethod("getKey");
		m.setReturnType(new JavaType("String", JavaType.PUBLIC));
		m.addCode("return key;");
		sme.addMethod(m);
		
		m = new JavaMethod("getValue");
		m.setReturnType(new JavaType("String", JavaType.PUBLIC));
		m.addCode("return value;");
		sme.addMethod(m);

		if( jclass.getVersion() > 0x0104 ){
			m = new JavaMethod("setValue", new JavaParam("value", "String" ));
			m.setReturnType(new JavaType("String", JavaType.PUBLIC));
		}
		else {
			m = new JavaMethod("setValue", new JavaParam("value", Object.class));
			m.setReturnType(new JavaType("Object", JavaType.PUBLIC));
		}
		m.addCode("String old = this.value;");
		m.addCode("this.value = value.toString();");
		m.addCode("return old;");
		sme.addMethod(m);

		// Don't forget this one...!
		m = new JavaMethod("toString");
		m.setReturnType(new JavaType("String", JavaType.PUBLIC));
		m.addCode("return this.value;");
		sme.addMethod(m);

		jclass.addInnerClass(sme);
	}
	
}
