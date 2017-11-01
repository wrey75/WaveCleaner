package com.oxande.xmlswing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaComments;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaParam;
import com.oxande.xmlswing.jcode.JavaType;



public class AbstractButtonUI extends JComponentUI {
	
	public static final String GROUP_ATTRIBUTE = "group";
	
	protected String actionVarName;

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "borderPainted", "setBorderPainted", ClassType.BOOLEAN ),
		new AttributeDefinition( "selected", "setSelected", ClassType.BOOLEAN ),
		new AttributeDefinition( "valign", "setVerticalAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "halign", "setHorizontalAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "vtextalign", "setVerticalTextAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "htextalign", "setHorizontalTextAlignment", ClassType.ALIGNMENT ),
		new AttributeDefinition( "gap", "setIconTextGap", ClassType.INTEGER ),
		new AttributeDefinition( "margins", "setMargin", ClassType.INSETS ),
		new AttributeDefinition( "multiClickThreshold", "setMultiClickThreshhold", ClassType.INTEGER ),
		new AttributeDefinition( "rollover", "setRolloverEnabled", ClassType.BOOLEAN ),
		new AttributeDefinition( "icon", "setIcon", ClassType.ICON ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root )  throws UnexpectedTag  {
		varName = Parser.addDeclaration( jclass, root, JPopupMenu.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addSpecifics(jclass, initMethod, root);
		addAction(jclass, initMethod, root, true);
		return varName;
	}
	
	public void addSpecifics(JavaClass jclass, JavaMethod initMethod, Element root) throws UnexpectedTag {
		jclass.addImport(ActionListener.class);
		jclass.addImport(ActionEvent.class);
		JavaClass actionClass = new JavaClass(ActionListener.class.getName());
		addActionListener(jclass, actionClass, root, true, varName);
		initMethod.addCall(varName + ".addActionListener", actionClass.toParam());
	}
	
	private void addInit( JavaClass jclazz, String methodName, String ... params ){
		jclazz.addStatic( JavaMethod.getCallSyntax(methodName, params));
	}

	public static void addActionListener(JavaMethod initMethod, JavaClass jclass, Element root, String varName ){
		jclass.addImport(ActionListener.class);
		jclass.addImport(ActionEvent.class);
		JavaClass actionClass = new JavaClass(ActionListener.class.getName());
		addActionListener(jclass, actionClass, root, false, varName);
		initMethod.addCall(varName + ".addActionListener", actionClass.toParam());
	}
	
	/**
	 * Add an action listener for this button. The implementation
	 * relies on the <i>action</i> attribute of the <i>root</i>
	 * XML element: if an action is defined, we use it as the name
	 * of a method we will create if necessary.
	 * 
	 * If the &lt;action&gt; tag is available as a child, it means
	 * the code is implemented directly in the XML file and we
	 * copy it verbatim.
	 * 
	 * If the attribute for <i>action</i> exists but the value is
	 * <code>null</code>, we consider the programmer wants an
	 * empty implementation (a comment line is created to reflect
	 * this).
	 * 
	 * @param jclass the class
	 * @param actionClass ?
	 * @param root the root element
	 * @param withMessage set to <code>true</code> if a message
	 * 		must be displayed until the implementation is available.
	 * 		If set to <code>false</code>, the default implementation
	 * 		is empty. 
	 * @param varName ?
	 */
	protected static void addActionListener( JavaClass jclass, JavaClass actionClass, Element root, boolean withMessage, String varName ){
		JavaMethod performMethod = new JavaMethod("actionPerformed", new JavaParam( "e", ActionEvent.class.getSimpleName()) );
		actionClass.addMethod(performMethod);
		String actionMethod = Parser.getAttribute(root, ACTION_ATTRIBUTE);
		if( actionMethod == null ){
			Element e = Parser.getChildElement(root, ACTION_ATTRIBUTE);
			if( e!= null ){
				performMethod.addCode( Parser.getTextContents(e) );
			}
			else {
 				if( withMessage ){
					jclass.addImport(JOptionPane.class);
					performMethod.addCode( "JOptionPane.showMessageDialog(" + varName + ", \"Not implemented.\","
							+ varName + ".getText(), JOptionPane.INFORMATION_MESSAGE);" );
				}
			}
		}
		else if( actionMethod.equals("null")){
			performMethod.addLineComment("Nothing to do.");
		}
		else {			
			JavaMethod method;
			method = jclass.getMethod(actionMethod);
			if( method == null ){
				// Create the method only if not already exists.
				// Some actions or menu items can share the same method.
				method = new JavaMethod( actionMethod );
				method.setReturnType( new JavaType("void", JavaType.PROTECTED ));
				jclass.addImport(JOptionPane.class);
				if( withMessage ){
					method.addCode( "JOptionPane.showMessageDialog(" + varName + ", \"Not implemented.\","
							+ varName + ".getText(), JOptionPane.INFORMATION_MESSAGE);" );
				}
				jclass.addMethod( method );
			}
			performMethod.addCall(actionMethod);
			
			Node node = root;
			StringBuilder menuList = new StringBuilder();
			while( node != null ) {
				if( node.getNodeType() == Node.ELEMENT_NODE ){
					Element e = (Element)node;
					String text = Parser.getTextContents(e).trim();
					if( text.length() == 0 ){
						text = Parser.getAttribute(e, "text");
					}
					if( text != null ){
						if(menuList.length() > 0) menuList.insert(0, "/");
						menuList.insert(0, text.replaceAll("\\_", ""));
					}
				}
				node = node.getParentNode();
			}
			
			JavaComments cmt = method.getComments();
			cmt.add("Called by the menu item <i>" + menuList.toString() + "</i>.");
		}
	}

	/**
	 * Add an action to the button. When the button is
	 * clicked, an action is fired 5in the EDT thread).
	 * 
	 * @param jclass the class
	 * @param initMethod the init method where the code is added.
	 * @param root the root element
	 * @param withMessage
	 */
	protected void addAction( JavaClass jclass, JavaMethod initMethod, Element root, boolean withMessage ){
		jclass.addImport(AbstractAction.class);
		jclass.addImport(Action.class);
		jclass.addImport(ActionEvent.class );

		JavaClass actionClass = new JavaClass(AbstractAction.class.getSimpleName());
		
		// JavaMethod constructor = actionClass.getConstructor();
		String tooltipText = Parser.getAttribute(root, TOOLTIP_ATTRIBUTE);
		if( tooltipText != null ){
			addInit(actionClass,"putValue", "Action.SHORT_DESCRIPTION", JavaCode.toParam(tooltipText) );
		}

		String text = getTextAttribute(root);
		if( text != null ){
			StringBuilder realText = new StringBuilder();
			StringBuilder mnemonic = new StringBuilder();
			int pos = getMnemonicInformation( text, realText, mnemonic );
			addInit(actionClass,"putValue", "Action.NAME", JavaCode.toParam(realText.toString()) );
			if( pos >= 0 ){
				addInit(actionClass,"putValue", "Action.MNEMONIC_KEY", mnemonic.toString() );
				addInit(actionClass,"putValue", "Action.DISPLAYED_MNEMONIC_INDEX_KEY", "new Integer(" + pos + ")" );
			}
		}
		
		String icon = Parser.getAttribute(root,"icon");
		if( icon != null ){
			addInit(actionClass,"putValue", "Action.SMALL_ICON", varName + ".getIcon()" );
		}
		
		String shortcut = Parser.getAttribute(root, JMenuItemUI.SHORTCUT_ATTRIBUTE);
		if( shortcut != null ){
			addInit(actionClass, "putValue", "Action.ACCELERATOR_KEY", varName + ".getAccelerator()" );
		}
		
		addActionListener(jclass, actionClass, root, withMessage, varName);
		initMethod.addCall(varName+".setAction", actionClass.toParam() );
		
		//actionVarName = Parser.getUniqueId("action");
		//jclass.addDeclaration( actionVarName, new JavaType(AbstractAction.class.getSimpleName(), JavaType.PRIVATE),  );
	}

	public static boolean addBooleanProperty( JavaClass jclass, Element root, String varName ){
		String propName = root.getAttribute( PROPERTY_ATTR ).trim();
		if( propName.length() > 0 ){
			JavaMethod getter = new JavaMethod("is" + capitalizeFirst(propName));
			getter.setReturnType( new JavaType("boolean", JavaType.PUBLIC ));
			getter.addCode("return " + varName + ".isSelected();");
			JavaMethod setter = new JavaMethod("set" + capitalizeFirst(propName), new JavaParam("b", new JavaType("boolean", 0)));
			setter.addCode(varName + ".setSelected(b);");
			jclass.addMethod(getter);
			jclass.addMethod(setter);
			return true;
		}
		return false;
	}

	/**
	 * Create a group for the buttons. See the definition
	 * of {@link ButtonGroup} for more details. Rather than
	 * creating a group manually, the programmer just add
	 * a <i>group</i> attribute and puts the same name, the 
	 * group is created automatically as an anonymous one.
	 * Pretty simple implementation.
	 * 
	 * @param varName the variable name
	 * @param jclass the class
	 * @param initMethod the init method
	 * @param root the root element.
	 */
	protected static void addGroup( String varName, JavaClass jclass, JavaMethod initMethod, Element root ){
		String groupName = Parser.getAttribute(root, GROUP_ATTRIBUTE );
		if( groupName != null ){
			if( !jclass.isRegistered(groupName) ){
				jclass.addImport( ButtonGroup.class );
				jclass.addAnonymousDeclaration("ButtonGroup " + groupName + " = new ButtonGroup();" );
				jclass.register(groupName, ButtonGroup.class );
			}
			initMethod.addCall(groupName+".add", varName);
		}
	}

}
