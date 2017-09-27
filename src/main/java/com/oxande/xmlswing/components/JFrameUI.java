package com.oxande.xmlswing.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;



public class JFrameUI extends FrameUI {

	public static final AttributeDefinition[] PROPERTIES = {
	};

	public static final AttributesController CONTROLLER = new AttributesController(FrameUI.CONTROLLER, PROPERTIES);

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JFrame.class );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addIconImage(jclass, initMethod, root, varName);
		addJMenuBar(jclass, initMethod, root, varName);
		return varName;
	}

	/**
	 * Add the menu bar.
	 * @param jclass the JAVA class.
	 * @param initMethod the method for initializing the elements of the frame.
	 * @param root the root element.
	 * @param varName the name of the root element.
	 * @throws UnexpectedTag when there is an issue with the tag.
	 */
	public static void addJMenuBar(JavaClass jclass, JavaMethod initMethod, Element root, String varName ) throws UnexpectedTag{
		Element menubarElem = Parser.getChildElement(root, "menubar");
		if( menubarElem!= null ){
			JMenuBarUI menu = new JMenuBarUI();
			String menuName = menu.parse(jclass, initMethod, menubarElem);
			initMethod.addCall( varName + ".setJMenuBar", menuName );
			root.removeChild(menubarElem);
		}
	}

	/**
	 * Add the status bar for the frame. If a status bar is created,
	 * the element isremoved of the XML document to avoid an
	 * analyse by another system.
	 * 
	 * @param jclass The main class
	 * @param initMethod the method for the initialization
	 * @param root the XML element.
	 * @param varName the name of the JFrame element
	 * @throws UnexpectedTag if there is an issue with a tag.
	 */
	public static void addStatusBar(JavaClass jclass, JavaMethod initMethod, Element root, String varName ) throws UnexpectedTag{
		Element sbElem = Parser.getChildElement(root, "statusbar");
		if( sbElem != null ){
			String sbName;
			List<Element> list = Parser.getChildElements(sbElem);
			if( list.size() == 0 ){
				// If there is no elements, don't blame the
				// programmer, it is the label on all the
				// block.
				JLabelUI labelUI = new JLabelUI();
				sbName = labelUI.parse(jclass, initMethod, sbElem);
				// Now add a beautiful border to be compliant with standards...
				jclass.addImport(BorderFactory.class);
				jclass.addImport(Border.class);
				String borderName = Parser.getUniqueId("border");
				initMethod.addCode("Border " + borderName + " = BorderFactory.createLoweredBevelBorder();");
				initMethod.addCall(sbName + ".setBorder", borderName );
				
				// To avoid shrinking of the JLabel!!!
				initMethod.addCall(sbName + ".setText", JavaClass.toParam("Ready.") ); 
				
				// Put a plain font (the default is bold for some L&F).
				jclass.addImport(Font.class);
				initMethod.addCall(sbName + ".setFont", sbName + ".getFont().deriveFont( Font.PLAIN )" );
			}
			else {
				// Create a complex status bar...
				sbName = Parser.addDeclaration( jclass, root, JPanel.class );
				jclass.addImport(FlowLayout.class);
				initMethod.addCall( sbName +".setLayout", "new FlowLayout()" );
				for( Element e : list ){
					String name = ComponentUI.parseComponent(jclass, initMethod, e);
					initMethod.addCall( sbName + ".add", name );
				}
			}
			
			// Add to the SOUTH part...
			initMethod.addCall( varName + ".add", sbName, "BorderLayout.SOUTH" );
			root.removeChild(sbElem);
		}
	}


	public static void addToolBars(JavaClass jclass, JavaMethod initMethod, Element root, String varName ) throws UnexpectedTag {
		Element tbElement = Parser.getChildElement(root, "toolbars");
		if( tbElement == null ){
			tbElement = Parser.getChildElement(root, "toolbar");
			if( tbElement != null ){
				JToolBarUI ui = addToolBar(jclass, initMethod, tbElement, varName );
				initMethod.addCall( varName + ".add", ui.getName(), JavaClass.toParam( ui.getOrientation()) );
				root.removeChild(tbElement);
			}
		}
		else {
			// Several toolbars to add then we need to use panels...
			// We create 3 panels: noth, west and east
			List<Element> list = Parser.getChildElements(tbElement, "toolbar");
			String[] panels = new String[3];
			for( int i = 0; i < 3; i++ ){
				panels[i] = Parser.addDeclaration( jclass, root, JPanel.class );
				initMethod.addCall(panels[i] + ".setLayout", "new FlowLayout(FlowLayout.LEFT)" );
			}
			
			for( Element e : list ){
				JToolBarUI ui = addToolBar(jclass, initMethod, e, varName );
				if( ui.getOrientation().equals(BorderLayout.NORTH)){
					initMethod.addCall( panels[0] + ".add", ui.getName());
				}
				else if( ui.getOrientation().equals(BorderLayout.WEST)){
					initMethod.addCall( panels[1] + ".add", ui.getName());
				}
				else if( ui.getOrientation().equals(BorderLayout.EAST)){
					initMethod.addCall( panels[2] + ".add", ui.getName());
				}
				else {
					throw new IllegalArgumentException("Bad orientation.");
				}
			}
			
			initMethod.addCall(varName + ".add", panels[0], "BorderLayout.NORTH" );
			initMethod.addCall(varName + ".add", panels[1], "BorderLayout.WEST" );
			initMethod.addCall(varName + ".add", panels[2], "BorderLayout.EAST" );
			
			root.removeChild(tbElement);
		}
	}

	/**
	 * Add a toolbar.
	 * 
	 * @param jclass
	 * @param initMethod
	 * @param toolbarElement
	 * @param varName
	 * @param panels the panels array, 0 for north, 1 for west and
	 * 			2 for east.
	 * @return
	 * @throws UnexpectedTag
	 */
	public static JToolBarUI addToolBar(JavaClass jclass, JavaMethod initMethod, Element toolbarElement, String varName ) throws UnexpectedTag{
		// Create the status bar.
		JToolBarUI tbUI = new JToolBarUI();
		
		String toolbarName = tbUI.parse(jclass, initMethod, toolbarElement);
		String position = Parser.getAttribute(toolbarElement, "position");
		if( position == null ) position = "top";
		if( position.equals("top") ){
			initMethod.addCall( toolbarName + ".setOrientation", "JToolBar.HORIZONTAL");
			tbUI.setOrientation(BorderLayout.NORTH);
			//initMethod.addCall( varName + ".add", toolbarName, panels[0] );
		}
		else if (position.equals("left")){
			initMethod.addCall( toolbarName + ".setOrientation", "JToolBar.VERTICAL");
			tbUI.setOrientation(BorderLayout.WEST);
			//initMethod.addCall( varName + ".add", toolbarName, panels[1] );
		}
		else if (position.equals("right")){
			initMethod.addCall( toolbarName + ".setOrientation", "JToolBar.VERTICAL");
			tbUI.setOrientation(BorderLayout.EAST);
			//initMethod.addCall( varName + ".add", toolbarName, panels[0] );
		}
		else {
			throw new UnexpectedTag(toolbarElement, "position attribute must be \"top\", \"left\" or \"right\".");
		}
		return tbUI;
	}

}
