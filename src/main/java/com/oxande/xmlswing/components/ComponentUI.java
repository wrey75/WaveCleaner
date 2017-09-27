package com.oxande.xmlswing.components;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaParam;
import com.oxande.xmlswing.jcode.JavaType;


/**
 * The base implementation for components. This class includes
 * several methods (static or not) used by other more complex
 * components.
 * 
 * @author wrey75
 *
 */
public class ComponentUI implements IComponent {
	public static final String VISIBLE = "visible";
	public static final String TEXT_ATTRIBUTE = "text";
	public static final String ACTION_ATTRIBUTE = "action";


	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "minsize", "setMinimumSize", ClassType.DIMENSION ),
		new AttributeDefinition( "maxsize", "setMaximumSize", ClassType.DIMENSION ),
		new AttributeDefinition( "coolsize", "setPreferredSize", ClassType.DIMENSION ),
		new AttributeDefinition( "size", "setSize", ClassType.DIMENSION ),
		new AttributeDefinition( VISIBLE, "setVisible", ClassType.BOOLEAN ),
		new AttributeDefinition( "cursor", "setCursor", ClassType.CURSOR ),
		new AttributeDefinition( "name", "setName", ClassType.STRING ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( PROPERTIES );

	protected String varName = null;

	/**
	 * Return the name of the component.
	 * 
	 * @return the name of the component.
	 */
	public String getName(){
		return varName;
	}

	/**
	 * Parsing of the component. This method should be overriden on all the compoenents.
	 * 
	 * 
	 */
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		throw new UnsupportedOperationException( "Parsing not implemented for " + this.getClass().getName() );
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		addMouseListener(jclass, initMethod, root, varName);
	}
	
	public static String parseComponent( JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		String tagName = root.getTagName();
		String ret = null;
		IComponent component = null;
		if( tagName.equals("split") || tagName.equals("splitpane") ){
			component = new JSplitPaneUI();
		}
		else if( tagName.equals("component") ){
			component = new JComponentUI();
		}
		else if( tagName.equals("tree") ){
			component = new JTreeUI();
		}
		else if( tagName.equals(JLabelUI.TAGNAME)){
			component = new JLabelUI();
		}
		else if( tagName.equals("tabs")){
			component = new JTabbedPaneUI();
		}
		else if( tagName.equals("textfield")){
			component = new JTextFieldUI(); 
		}
		else if( tagName.equals("formattedfield")){
			component = new JFormattedTextFieldUI(); 
		}
		else if( tagName.equals("password")){
			component = new JPasswordFieldUI(); 
		}
		else if( tagName.equals("textComponent")){
			component = new JTextComponentUI(); 
		}
		else if( tagName.equals("textarea")){
			component = new JTextAreaUI(); 
		}
		else if( tagName.equals("list")){
			component = new JListUI(); 
		}
		else if( tagName.equals("toolbar")){
			component = new JToolBarUI(); 
		}
		else if( tagName.equals( GridBagLayoutUI.TABLE_TAG )){
			component = new GridBagLayoutUI(); 
		}
		else if( tagName.equals("iframe")){
			component = new JInternalFrameUI(); 
		}
		else if( tagName.equals("select")){
			component = new JComboBoxUI(); 
		}
		else if( tagName.equalsIgnoreCase("JTable")){
			component = new JTableUI(); 
		}
		else if( tagName.equals("radiobutton") || tagName.equals("radio")){
			component = new JRadioButtonUI(); 
		}
		else if( tagName.equals("checkbox")){
			component = new JCheckBoxUI(); 
		}
		else if( tagName.equals(JButtonUI.TAGNAME)){
			if( Parser.getBooleanAttribute(root, "toggle", false) ){
				// Only if "toggle" attribute set to "true".
				component = new JToggleButtonUI();
			}
			else {
				component = new JButtonUI();
			}
		}
		else if( tagName.equals("input")){
			//
			// For HTML compatibility
			//
			String type = root.getAttribute("type");
			if( type.equals("radio") ){
				component = new JRadioButtonUI();
			}
			else if( type.equals("text") ){
				component = new JTextFieldUI();
			}
			else if( type.equals("password") ){
				component = new JPasswordFieldUI();
			}
			else if( type.equals("checkbox") ){
				component = new JCheckBoxUI();
			}
			else {
				throw new UnexpectedTag(root, "<input> expects a valid \"type\" attribute: radio, checkbox, text, password.");
			}
		}
		else {
			throw new UnexpectedTag(root);
		}
		
		ret = component.parse(jclass, initMethod, root);
		return ret;
	}
	
	protected static JavaClass addListener( JavaClass jclass, 
									Element root, 
									String[] arrNames,
									Class<? extends Object> listenerClass,
									Class<? extends EventObject> eventClass){
		boolean listenerAdded = false;
		JavaClass innerClass = new JavaClass( Parser.getUniqueId(listenerClass.getSimpleName()));
		innerClass.setExtend( listenerClass );
		
		for(int i = 0; i < arrNames.length; i++ ){
			String[] parts = arrNames[i].split(":");
			String attrName = parts[0];
			String methodName = parts[1];
			String actionMethod = Parser.getAttribute(root, attrName);
			JavaMethod performMethod = new JavaMethod(methodName, new JavaParam( "e", eventClass.getSimpleName()) );
			if( actionMethod == null ){
				// Not declared as an attribute,
				// can be declared as embedded code in the XML...
				Element e = Parser.getChildElement(root, attrName);
				if( e!= null ){
					// We have some code available...
					performMethod.addCode( Parser.getTextContents(e) );
					innerClass.addMethod(performMethod);
					listenerAdded = true;
					root.removeChild(e); // Remove to avoid side-effects
				}
			}
			else {
				// Method name included in the attributes.
				boolean eventParam = false;
				if( actionMethod.endsWith("+")){
					// The parameter for the event must be added.
					eventParam = true;
					actionMethod = actionMethod.substring(0,actionMethod.length()-1);
				}
				jclass.addImport(JOptionPane.class);
				JavaMethod method = new JavaMethod( actionMethod );
				jclass.addImport(UnsupportedOperationException.class);
				method.setReturnType( new JavaType("void", JavaType.PROTECTED ));
				method.addCode("throw new " + UnsupportedOperationException.class.getSimpleName() + "(\"Not implemented\");" );

				if( eventParam ){
					performMethod.addCall(actionMethod, "e");
					method.setParams(new JavaParam("e", new JavaType(eventClass.getSimpleName(),JavaType.PACKAGE)));
				}
				else{
					// without any parameter
					performMethod.addCall(actionMethod);
				}
				jclass.addMethodIfNotExists( method );
				innerClass.addMethod(performMethod);
				listenerAdded = true;
			}
		}
		if( listenerAdded ){
			jclass.addImport(listenerClass);
			jclass.addImport(eventClass);
			return innerClass;
		}
		return null;
	}
	
	public static void addMouseListener(JavaClass jclass, JavaMethod initMethod, Element root, String varName){
		
		JavaClass listenerClass = addListener(jclass, root, 
				new String[] {"onClick:mouseClicked",
								"onPress:mousePressed",
								"onRelease:mouseReleased",
								"onEntered:mouseEntered",
								"onExit:mouseExited"}, 
				MouseAdapter.class, MouseEvent.class);
		
		if( listenerClass != null ){
			// Add the inner class and related stuff..
			jclass.addInnerClass(listenerClass);
			initMethod.addCall(varName + ".addMouseListener", "new " + listenerClass.getClassName() + "()" );
		}
	}
	
	public static String getTextAttribute( Element root ){
		String text = Parser.getAttribute(root, TEXT_ATTRIBUTE );
		if( text == null ) {
			text = Parser.getTextContents(root).trim();
		}
		return text;
	}

}
