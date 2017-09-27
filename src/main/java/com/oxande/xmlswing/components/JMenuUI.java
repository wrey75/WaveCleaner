package com.oxande.xmlswing.components;


import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.jcode.JavaBlock;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;
import com.oxande.xmlswing.jcode.JavaType;


/**
 * Implementation of JMenu.
 * 
 * @author wrey75
 * @version $Rev: 89 $
 *
 */
public class JMenuUI extends JMenuItemUI {
	
	public static final String TAGNAME = "menu";
		
	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "delay", "setDelay", ClassType.INTEGER ),
	};
	
	public static final AttributesController CONTROLLER = new AttributesController( JMenuItemUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag {
		varName = Parser.addDeclaration( jclass, root, JMenu.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addTextAndMnemonic(root, initMethod, varName);
		analyseMenus(jclass, initMethod, root,varName);
		return varName;
	}
	
	public static void analyseMenus(JavaClass jclass, JavaMethod initMethod, Element root, String rootName) throws UnexpectedTag {
		List<Element> childs = Parser.getChildElements(root);
		for( Element e : childs ){
			String tagName = e.getTagName();
			if( tagName.equals(TAGNAME) ){
				JMenuUI submenu = new JMenuUI();
				String subMenuName = submenu.parse(jclass, initMethod, e);
				initMethod.addCall(rootName+".add", subMenuName);
			}
			else if( tagName.equals(JMenuItemUI.TAGNAME)
						|| tagName.equals("item")){
				JMenuItemUI submenu = new JMenuItemUI();
				String itemName = submenu.parse(jclass, initMethod, e);
				initMethod.addCall(rootName+".add", itemName);
			}
			else if( tagName.equals(JRadioButtonMenuItemUI.TAGNAME) ){
				JRadioButtonMenuItemUI radioB = new JRadioButtonMenuItemUI();
				String itemName = radioB.parse(jclass, initMethod, e);
				initMethod.addCall(rootName+".add", itemName);
			}
			else if( tagName.equals(JCheckBoxMenuItemUI.TAGNAME) ){
				JCheckBoxMenuItemUI checkBox = new JCheckBoxMenuItemUI();
				String itemName = checkBox.parse(jclass, initMethod, e);
				initMethod.addCall(rootName+".add", itemName);
			}
			else if( tagName.equals("separator") ){
				initMethod.addCall(rootName+".addSeparator" );
			}
			else if( tagName.equals("lookandfeel") ){
				// Add the look and feel entries...
				jclass.addImport(UIManager.class);
				jclass.addImport(ButtonGroup.class);
				jclass.addImport(SwingUtilities.class);
				jclass.addImport(ActionListener.class);
				jclass.addImport(JRadioButtonMenuItem.class);
				String name = Parser.getUniqueId( "landf" );
				String grpName = Parser.getUniqueId( "group" );
				String toRefresh = Parser.getStringAttribute(root, "refresh", "getRootPane()" );
				jclass.addDeclaration(grpName,new JavaType("ButtonGroup"), new String[0]);
				jclass.addImport( Cursor.class );
				
				JavaBlock forLoop = new JavaBlock();
				forLoop.addCode("JRadioButtonMenuItem item = new JRadioButtonMenuItem(" + name + "[i].getName());",
						"item.setActionCommand(" + name + "[i].getClassName());",
						"item.addActionListener(new ActionListener() {",
						"   public void actionPerformed(ActionEvent event) {",
						"      try {",
						"         Cursor old = getCursor();",
						"         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));",
						"         UIManager.setLookAndFeel(event.getActionCommand());",
						"         SwingUtilities.updateComponentTreeUI(" + toRefresh + ");",
				      	"         setCursor(old);",
						"      } catch(Exception e) {",
						"         // Does nothing",
						"      }",
						"   }",
						"});" );
				forLoop.addCall(rootName+".add", "item" );
				forLoop.addCall(grpName+".add", "item" );
				forLoop.addCode( 
						"if( UIManager.getLookAndFeel().getName().equals(" + name +"[i].getName()) ){",
						"   item.setSelected(true);",
						"}");
				initMethod.addCode(
				  "UIManager.LookAndFeelInfo[] " + name + " = UIManager.getInstalledLookAndFeels();",
  				  "for(int i = 0; i < " + name +".length; i++)" );
				initMethod.addCode(forLoop);
			}
			else {
				throw new UnexpectedTag(e);
			}
		}
	}

}
