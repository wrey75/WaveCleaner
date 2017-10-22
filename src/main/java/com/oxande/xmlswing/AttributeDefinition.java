package com.oxande.xmlswing;

import java.awt.Color;
import java.awt.Cursor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.w3c.dom.Element;

import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;



/**
 * A class to initialize all the parameter of an object
 * based on a attribute. Rather than creating code for
 * all the attributes, this class helps the programmer
 * for looking for some attributes and create the JAVA
 * code associated.
 * 
 * @author wrey
 * @version $Rev: 85 $
 * 
 */
public class AttributeDefinition {
	
	public final static Map<String, String> ALIGNS = new HashMap<String, String>();
	public final static Map<String, String> CLOSE_OPERATIONS = new HashMap<String, String>();
	public final static Map<String, String> CURSORS = new HashMap<String, String>();
	public final static Map<String, String> LIST_SEL_MODE = new HashMap<String, String>();
	
	static {
		
		CURSORS.put( "crosshair", "CROSSHAIR_CURSOR" );
		CURSORS.put( "default", "DEFAULT_CURSOR" );
		CURSORS.put( "hand", "HAND_CURSOR" );
		CURSORS.put( "move", "MOVE_CURSOR" );
		
//		public static final int 	CUSTOM_CURSOR 	-1
//		public static final int 	N_RESIZE_CURSOR 	8
//		public static final int 	NE_RESIZE_CURSOR 	7
//		public static final int 	NW_RESIZE_CURSOR 	6
//		public static final int 	S_RESIZE_CURSOR 	9
//		public static final int 	SE_RESIZE_CURSOR 	5
//		public static final int 	SW_RESIZE_CURSOR 	4
//		CURSORS.put( "", "W_RESIZE_CURSOR" );
		
		CURSORS.put( "text", "TEXT_CURSOR" );
		CURSORS.put( "wait", "WAIT_CURSOR" );
		
		CLOSE_OPERATIONS.put( "nothing", "DO_NOTHING_ON_CLOSE" );
		CLOSE_OPERATIONS.put( "hide", "HIDE_ON_CLOSE" );
		CLOSE_OPERATIONS.put( "dispose", "DISPOSE_ON_CLOSE" );
		CLOSE_OPERATIONS.put( "exit", "EXIT_ON_CLOSE" );

		String scName = SwingConstants.class.getName();
		ALIGNS.put( "bottom", scName + ".BOTTOM" );
		ALIGNS.put( "center", scName + ".CENTER" );
		ALIGNS.put( "east", scName + ".EAST" );
		ALIGNS.put( "leading", scName + ".LEADING" );
		ALIGNS.put( "left", scName + ".LEFT" );
		ALIGNS.put( "next", scName + ".NEXT" );
		ALIGNS.put( "north", scName + ".NORTH" );
		ALIGNS.put( "right", scName + ".RIGHT" );
		ALIGNS.put( "top", scName + ".TOP" );
		ALIGNS.put( "trailing", scName + ".TRAILING" );
		ALIGNS.put( "west", scName + ".WEST" );
		
		
		LIST_SEL_MODE.put("single", "javax.swing.ListSelectionModel.SINGLE_SELECTION" ); 
		LIST_SEL_MODE.put("interval", "javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION" );
		LIST_SEL_MODE.put("any", "javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION" );
	}
	
	/**
	 * Values for automatic affectation of the correct
	 * parameters for the methods.  
	 *
	 */
	public enum ClassType {
		TEXT,
		STRING,
		CHAR,
		INTEGER,
		BOOLEAN,
		PERCENT,
		COLOR,
		ALIGNMENT,
		DIMENSION,
		INSETS,
		KEYSTROKE,
		VERTICAL_OR_HORIZONTAL,    // Not yet implemented
		JSPLITPANE_ORIENTATION,
		JLIST_ORIENTATION,
		ICON,
		CURSOR,
		COMPONENT,
		JTABLE_AUTO_RESIZE, // For JTable implementation
		JLIST_SELECT_MODE,
	};
	
	private String attrName;
	private String methodName;
	private String defaultValue = null;
	private ClassType type;
	
	/**
	 * Create the attribute.
	 * 
	 * @param attributeName the attribute name.
	 * @param methodName the method name (if <code>null</code> then
	 * 			the method name is created implicitly based on
	 * 			the attribute name with the first character capitalized
	 * 			and prefixed by "set".
	 * @param type the type of the expected variable.
	 * @param defaultValue the default value if exists (usually not provided).
	 * 			This default value is used only for some attribute types.
	 */
	public AttributeDefinition( String attributeName, String methodName, ClassType type, String defaultValue ){
		this.attrName = attributeName;
		if( methodName == null ){
			this.methodName = "set" + UIParser.capitalizeFirst(attributeName);
		}
		else {
			this.methodName = methodName;
		}
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public AttributeDefinition( String attributeName, String methodName, ClassType type ){
		this( attributeName, methodName, type, null );
	}

	public String getParameterIfExist( Element e ) throws UnexpectedTag{
		String[] params = getParameters(e);
		return( params == null ? null : params[0]);
	}
	
	/**
	 * Get the parameters for this element.
	 * 
	 * @param e the element.
	 * @return the list of the arguments or <code>null</code>
	 * 		if there is no attribute for this definition.
	 * @throws UnexpectedTag 
	 */
	public String[] getParameters( Element e ) throws UnexpectedTag{
		String[] params = null;
		
		switch( type ){
		case TEXT :
			String txt = Parser.getTextContents(e).trim();
			if( txt.trim().length() == 0 ){
				txt = Parser.getAttribute(e, "text");
			}
			if( txt != null ){
				params = new String[] { JavaCode.toParam(txt) };
			}
			break;
			
		case BOOLEAN :
			Boolean b = Parser.getBooleanAttribute(e, attrName);
			if( b != null ){
				params = new String[] { JavaCode.toParam(b) };
			}
			break;

		case INTEGER :
			Integer i = Parser.getIntegerAttribute(e, attrName);
			if( i != null ){
				params = new String[] { JavaCode.toParam( i ) };
			}
			break;

		case PERCENT :
			Double percent = Parser.getPercentageAttribute(e, attrName);
			if( percent != null ){
				params = new String[] { JavaCode.toParam( percent.doubleValue() ) };
			}
			break;

		case STRING :
			String s = Parser.getStringAttribute(e, attrName, defaultValue );
			if( s != null ){
				params = new String[] { JavaCode.toParam( s ) };
			}
			break;

		case CHAR :
			String chars = Parser.getAttribute(e, attrName);
			if( chars != null ){
				params = new String[] { JavaCode.toParam( chars.charAt(0) ) };
			}
			break;

		case COLOR :
			Color color = Parser.getColorAttribute(e, attrName);
			if( color != null ){
				params = new String[] { JavaCode.toParam( color ) };
			}
			break;

		case ALIGNMENT :
			String align = Parser.getAttribute(e, attrName);
			if( align != null ){
				String constant = null;
				constant = ALIGNS.get( align.trim().toLowerCase() );
				if( constant != null ){
					params = new String[] { constant };
				}
			}
			break;

		case ICON :
			String iconName = Parser.getAttribute(e, attrName);
			if( iconName != null ){
				String icon;
				if( iconName.startsWith("http:" ) || iconName.startsWith("ftp:" ) ){
					icon = "new " + ImageIcon.class.getName() + "( new " + URL.class.getName() + "( " + iconName + ") )";
				}
				else {
					icon = "(new javax.swing.ImageIcon(getClass().getResource(" + JavaClass.toParam(iconName) + ")))";
				}
				params = new String[] { icon };
			}
			break;

		case CURSOR :
			String cursorName = Parser.getAttribute(e, attrName);
			if( cursorName != null ){
				String cursor = Cursor.class.getName() + "getPredefinedCursor( " + Cursor.class.getName() + cursorName + ") )";
				params = new String[] { cursor };
			}
			break;

		case JSPLITPANE_ORIENTATION :
			String orientation = Parser.getAttribute(e, attrName);
			if( orientation != null ){
				if( orientation.equalsIgnoreCase("horizontal") ){
					params = new String[] { "JSplitPane.HORIZONTAL_SPLIT" };
				}
				else if( orientation.equalsIgnoreCase("vertical") ){
					params = new String[] { "JSplitPane.VERTICAL_SPLIT" };
				}
				else {
					throw new IllegalArgumentException("horizontal or vertical expected for this tag." );
				}
			}
			break;

		case JLIST_ORIENTATION :
			orientation = Parser.getAttribute(e, attrName);
			if( orientation != null ){
				if( orientation.equalsIgnoreCase("hwrap") ){
					params = new String[] { "JList.HORIZONTAL_WRAP" };
				}
				else if( orientation.equalsIgnoreCase("vwrap") ){
					params = new String[] { "JSplitPane.VERTICAL_WRAP" };
				}
				else if( orientation.equalsIgnoreCase("vertical") ){
					params = new String[] { "JSplitPane.VERTICAL" };
				}
				else {
					throw new IllegalArgumentException("hwrap, vwrap or vertical expected for this tag." );
				}
			}
			break;

		case DIMENSION :
			String dim = Parser.getAttribute(e, attrName);
			if( dim != null ){
				String[] vector = dim.split(",");
				if( vector.length < 2 ){
					throw new UnexpectedTag("attribute \"" + attrName + "\" expects 2 comma separated values." );
				}
				try {
					int width = Integer.parseInt( vector[0].trim() );
					int height = Integer.parseInt( vector[1].trim() );
					params = new String[] { "new java.awt.Dimension(" + width + "," + height +")" };
				}
				catch( NumberFormatException ex ){
					throw new UnexpectedTag("attribute \"" + attrName + "\" expects numeric values." );
				}
			}
			break;

		case INSETS :
			String insets = Parser.getAttribute(e, attrName);
			if( insets != null ){
				String[] vector = insets.split(",");
				try {
					int top = 0, left = 0, bottom = 0, right = 0;
					// if only one value, the inset is assumed for others.
					switch( vector.length ){
					case 1 :
						top = Integer.parseInt( vector[0].trim() );
						left = bottom = right = top;
						break;
					case 2 :
						bottom = top = Integer.parseInt( vector[0].trim() );
						right = left = Integer.parseInt( vector[1].trim() );
						break;
					case 4 :
						top = Integer.parseInt( vector[0].trim() );
						left = Integer.parseInt( vector[1].trim() );
						bottom = Integer.parseInt( vector[2].trim() );
						right = Integer.parseInt( vector[3].trim() );
						break;
					default :
						throw new UnexpectedTag("attribute \"" + attrName + "\" expects only 1, 2 or 4 numeric values." );					
					}
					params = new String[] { "new java.awt.Insets(" + top + "," + left + "," + bottom + "," + right + ")" };
				}
				catch( NumberFormatException ex ){
					throw new UnexpectedTag("attribute \"" + attrName + "\" expects numeric values." );
				}
			}
			break;

		case KEYSTROKE :
			String keystroke = Parser.getAttribute(e, attrName);
			if( keystroke != null ){
				KeyStroke ks = KeyStroke.getKeyStroke(keystroke);
				if( ks == null ){
					throw new UnexpectedTag("Keystroke \"" + keystroke + "\" invalid" );
				}
				params = new String[] { "javax.swing.KeyStroke.getKeyStroke(" + JavaClass.toParam(keystroke) + ")" };
			}
			break;
			
		case JTABLE_AUTO_RESIZE :
			String autoResizeMode = Parser.getStringAttribute(e, attrName, defaultValue);
			if( autoResizeMode != null ){
				if( autoResizeMode.equalsIgnoreCase("off")){
					params = new String[] { "JTable.AUTO_RESIZE_OFF" };
				}
				else if( autoResizeMode.equalsIgnoreCase("next")){
					params = new String[] { "JTable.AUTO_RESIZE_NEXT_COLUMN" };
				}
				else if( autoResizeMode.equalsIgnoreCase("subsequent")){
					params = new String[] { "JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS" };
				}
				else if( autoResizeMode.equalsIgnoreCase("last")){
					params = new String[] { "JTable.AUTO_RESIZE_LAST_COLUMN" };
				}
				else if( autoResizeMode.equalsIgnoreCase("all")){
					params = new String[] { "JTable.AUTO_RESIZE_ALL_COLUMNS" };
				}
				else {
					throw new UnexpectedTag("JTable \"autoResizeMode\" attribute not valid.");
				}
			}
			break;
		
		case JLIST_SELECT_MODE :
			String selectMode = Parser.getStringAttribute(e, attrName, defaultValue);
			if( selectMode != null ){
				String constant = null;
				constant = LIST_SEL_MODE.get( selectMode.toLowerCase() );
				if( constant != null ){
					params = new String[] { constant };
				}
				else {
					throw new UnexpectedTag("value \"" + selectMode + "\" for attribute " + attrName + " is not valid.");
				}
			}
			break;
			
		case VERTICAL_OR_HORIZONTAL :
			orientation = Parser.getAttribute(e, attrName);
			if( orientation != null ){
				if( orientation.equalsIgnoreCase("horizontal") ){
					params = new String[] { "JSlider.HORIZONTAL" };
				}
				else if( orientation.equalsIgnoreCase("vertical") ){
					params = new String[] { "JSlider.VERTICAL" };
				}
				else {
					throw new IllegalArgumentException("horizontal or vertical expected for this tag ('" + orientation + "') provided." );
				}
			}
			break;

		default :
			if( Parser.getAttribute(e, attrName) != null ){
				throw new IllegalArgumentException("Type " + type + " not yet supported." );
			}
		}
		return params;
	}
	
	public void addToMethod( JavaMethod jmethod, Element e, String varName ) throws UnexpectedTag{
		String[] params = getParameters(e);
		if( params != null ){
			jmethod.addCall( varName + "." + methodName, params );
		}
	}
	

}
