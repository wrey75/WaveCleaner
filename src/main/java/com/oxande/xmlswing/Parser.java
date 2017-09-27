package com.oxande.xmlswing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;


/**
 * Utilities for parsing the XML entities.
 *  
 * @author wrey75
 *
 */
public final class Parser {
	public static final Map<String, Color> COLORS = new HashMap<String, Color>();
	
	static {
		COLORS.put( "black", Color.BLACK );
		COLORS.put( "blue", Color.BLUE );
		COLORS.put( "cyan", Color.CYAN );
		COLORS.put( "darkgray", Color.DARK_GRAY );
		COLORS.put( "gray", Color.GRAY );
		COLORS.put( "green", Color.GREEN );
		COLORS.put( "lightgray", Color.LIGHT_GRAY );
		COLORS.put( "magenta", Color.MAGENTA );
		COLORS.put( "orange", Color.ORANGE );
		COLORS.put( "pink", Color.PINK );
		COLORS.put( "red", Color.RED );
		COLORS.put( "white", Color.WHITE );
		COLORS.put( "yellow", Color.YELLOW );
	}
	
	/**
	 * The attribute for the variable name of the component.
	 */
	public static final String ID_ATTRIBUTE = "id";
	
	/**
	 * The attribute for the class name of the component. Usually,
	 * the class name is a predefined class, but if the developper
	 * wants to used an extended class rather than the normal class,
	 * he can do it by changing this attribute. This attribute is available
	 * for any component.
	 * 
	 */
	public static final String CLASS_ATTRIBUTE = "class";
	
	private static Map<String, Integer> ids = new HashMap<String, Integer>();

	/**
	 * Clear the IDs created before.
	 */
	public static void clearIds(){
		ids.clear();
	}
	
	public static synchronized String getUniqueId( String root ){
		Integer id = null;
		synchronized( ids ){
			id = ids.get(root);
			if( id == null ){
				id = new Integer(1);
			}
			else {
				id = new Integer( id.intValue() + 1 );
			}
			ids.put(root, id); // Store the new value
		}
		return root + id.toString();
	}

	public static Element getChildElement( Element root, String tagName ) {
		List<Element> list = getChildElements(root,tagName);
		switch( list.size() ){
			case 0:
				return null;
				
			case 1: 
				return list.get(0);
				
			default :
				throw new IllegalArgumentException( "Multiple <" + tagName + "> found. Only one expected." ); 
		}
	}
	
	public static List<Element> getChildElements( Element root, String tagName ){
		List<Element> selected = new LinkedList<Element>();
		List<Element> childs = getChildElements(root);
		for( Element e : childs ){
			if( e.getTagName().equals(tagName) ){
				selected.add(e);
			}
		}
		return selected;
	}

	/**
	 * Retrieve the elements contained in the parent element.
	 * 
	 * @param e the parent element
	 * @return the child elements.
	 */
	public static List<Element> getChildElements( Element e ){
		NodeList nodes = e.getChildNodes();
		int len = nodes.getLength();
		List<Element> elements = new ArrayList<Element>( len );
		for(int i = 0; i < len; i++ ){
			Node node = nodes.item(i);
			if( node.getNodeType() == Node.ELEMENT_NODE ){
				elements.add( (Element)node );
			}
		}
		return elements;
	}
	
	public static List<Element> getChildElementsExcept( Element e, String ... list  ){
		NodeList nodes = e.getChildNodes();
		int len = nodes.getLength();
		List<Element> elements = new ArrayList<Element>( len );
		for(int i = 0; i < len; i++ ){
			Node node = nodes.item(i);
			if( node.getNodeType() == Node.ELEMENT_NODE ){
				String nodeName = node.getNodeName();
				boolean toBeAdded = true;
				for(int j = 0; j < list.length; j++){
					if( nodeName.equalsIgnoreCase(list[j]) ){
						toBeAdded = false;
					}
				}
				if( toBeAdded ) elements.add( (Element)node );
			}
		}
		return elements;
	}

	/**
	 * The attribute as a string.
	 * 
	 * @param e the XML element.
	 * @param attributeName the attribute.
	 * @param mandatory if the attribute is mandatory (if the attribute is
	 * 		mandatory and not found or is empty, a {@link NullPointerException}
	 * 		is thrown).
	 * @return the string value or <code>null</code> if not exists or is
	 * 		empty.
	 */
	public static String getAttribute( Element e, String attributeName, boolean mandatory ){
		String value = normalized( e.getAttribute(attributeName) );
		if( mandatory && value == null ){
			throw new NullPointerException( "<" + e.getNodeName() + ">: attribute \"" + attributeName + "\" expected.");
		}
		return value;
	}

	public static String getStringAttribute( Element e, String attributeName, String defaultValue ){
		String value = normalized( e.getAttribute(attributeName) );
		return (value == null ? defaultValue : value );
	}

	public static String getAttribute( Element e, String attributeName ){
		return getAttribute(e, attributeName, false);
	}

	/**
	 * Get the color attribute. The attribute returned is a 
	 * {@link Color} instance (or <code>null</code> if it is not
	 *  possible to decode).
	 *  
	 *  <p>
	 *  A color is analysed based on the color's name (yellow, cyan,
	 *  dark, etc.) or by the "<code>#</code>" character followed
	 *  by the color expressed in hexadecimal using the form RRGGBB.
	 *  For example, "<code>#00ff00</code>" is used for the green color.
	 *  </p>
	 * 
	 * @param e the XML element.
	 * @param attributeName the attribute name for the color.
	 * @return <code>null</code> is the attribute is empty or
	 * 		does not exist or we are not able to decode the color,
	 * 		the color value.
	 */
	public static Color getColorAttribute( Element e, String attributeName ){
		Color color = null;
		String s = getAttribute(e, attributeName, false);
		if( s != null && s.length() > 0 ){
			if( s.charAt(0) == '#' ){
				// RVB color...
				try {
					int rgb = Integer.parseInt( s.substring(1).trim(), 16 );
					color = new Color(rgb);
				}
				catch( NumberFormatException ex ){
					// Error displayed below.
				}
			}
			else {
				// Use the color array.
				color = COLORS.get(s.toLowerCase());
				if( color == null ){
					// Last hope before we cancel!
					color = Color.getColor(s);
				}
			}
			if( color == null ){
				System.err.println("<" + e.getTagName() + " " + attributeName + "= " + JavaCode.toParam(s) + ">: can not convert to a color.");
			}
		}
		return color;
	}

	
	/**
	 * Retrieve the class name for the tag or the defaulted one.
	 * When an object is instantied, we use a default class 
	 * (a &lt;label&gt; will create a <code>JLabel</code> obejct)
	 * but you can override the class with the "class" attribute
	 * in the tag. This is a solution to provide more power
	 * to an object.
	 *  
	 * @param e the tag.
	 * @param clazz the default class to use.
	 * @return the class to be declared in the JAVA code.
	 * 
	 */
	public static String getClassName( Element e, Class<?> clazz ){
		String className = getAttribute(e, CLASS_ATTRIBUTE);
		return( className == null ? clazz.getName() : className );		
	}

	/**
	 * Get the name of the the object. When a tag declares a
	 * new object, this object can have a name (in this case, it
	 * can be accessed by a derived class) or anonymous (the name
	 * is created based on the <i>rootName</> parameter). In many
	 * case, it is not necessary to declare a name except you have
	 * to access the object in the derived class.
	 * 
	 * @param e the tag.
	 * @param rootName the default root naming in case the name is
	 * 		created as an anomymous.
	 * @return the name of the object.
	 * @deprecated do not use anymore because you do not know if 
	 * 		the variable is anonymous or not!
	 * @see #addDeclaration(JavaClass, Element, Class) as replacement.
	 */
	public static String getVariableName( Element e, String rootName ){
		String varName = getAttribute(e, ID_ATTRIBUTE);
		if( varName == null ){
			varName = Parser.getUniqueId(rootName);
		}
		return varName;		
	}

	public static String normalized( String s ){
		if( s == null ) return null;
		s = s.trim();
		return ( s.length() == 0 ? null : s );
	}
	
	private static String cleanOf( String source ){
		int len = source.length();
		StringBuilder buf = new StringBuilder(len);
		boolean lastCharIsSpace = true;
		for( int i = 0; i < len; i++ ){
			char c = source.charAt(i);
			boolean isSpace = Character.isWhitespace(c);
			if( !isSpace ){
				lastCharIsSpace = false;
				buf.append(c);
			}
			else if(!lastCharIsSpace){
				buf.append(' ');
				lastCharIsSpace = true;
			}
		}
		return buf.toString().trim();
	}

	/**
	 * Get the text contents for a tag. Retrieve the text
	 * enclosed between the beginning and the end of the
	 * tag <i>excluding</i> the text of inner tags.
	 * 
	 * <p>
	 *  &lt;p&gt;This is an &lt;emph>emphazed&lt;/p&gt; text.&lt;/code&gt;
	 *  will return "This is an  text."
	 * </p>
	 * 
	 * @param e the tag.
	 * @return the text stored in the tag.
	 */
	public static String getTextContents( Element e){
		StringBuilder buf = new StringBuilder();
		NodeList list = e.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ ){
			Node node = list.item(i);
			switch( node.getNodeType() ){
				case Node.TEXT_NODE :
					buf.append( cleanOf( node.getNodeValue() ) );
					break;
				case Node.CDATA_SECTION_NODE :
					buf.append( node.getNodeValue() );
					break;
				case Node.ELEMENT_NODE :
					Element elem = (Element)node;
					if( elem.getNodeName().equalsIgnoreCase("br") ){
						buf.append("\n");
					}
					break;
				default:
					break;
			}
		}
		return buf.toString();
	}

	/**
	 * Get the boolean attribute.
	 * 
	 * @param e the XML element.
	 * @param attributeName the attribute name.
	 * @param defaultValue the default value returned if the attribute does not
	 * 		exists or is not set to <code>true</code> or <code>false</code>.
	 * @return the value of the attribute or the default value.
	 */
	public static boolean getBooleanAttribute( Element e, String attributeName, boolean defaultValue ){
		Boolean b = getBooleanAttribute(e, attributeName);
		if( b == null ){
			return defaultValue;
		}
		return b.booleanValue();
	}

	public static Boolean getBooleanAttribute( Element e, String attributeName ){
		String s = Parser.getAttribute(e, attributeName);
		if( s == null ) return null;
		if( s.equalsIgnoreCase(Boolean.toString(true)) ) return true;
		if( s.equalsIgnoreCase(Boolean.toString(false)) ) return false;
		System.out.println( "<" + e.getNodeName() + "> is expected to have true/false for the attribute \"" + attributeName + "\" instead of \"" + s + "\"" );
		return null;
	}
	
	public static Integer getIntegerAttribute( Element e, String attributeName ){
		Integer ret = null;
		String s = Parser.getAttribute(e, attributeName);
		if( s == null ) return null;
		try {
			ret = Integer.parseInt(s);
		}
		catch( NumberFormatException ex ){
			System.out.println("<" + e.getTagName() + " " + attributeName + "= " + JavaCode.toParam(s) + ">: can not convert to an integer.");
		}
		return ret;
	}

	public static int getIntegerAttribute( Element e, String attributeName, int defaultValue ){
		Integer ret = getIntegerAttribute(e,attributeName);
		return( ret == null ? defaultValue : ret.intValue() );
	}

	/**
	 * Get the percentage value. The percentage value can be expressed
	 * directly (with 0.250 for example) or with the percentage
	 * character (25%) at your convenience.
	 * 
	 * @param e the XML element.
	 * @param attributeName the attribute to extract.
	 * @return the value expressed as a double (a range from
	 * 		0.0 to 1.0� or <code>null</code> if no data available.
	 * 
	 */
	public static Double getPercentageAttribute( Element e, String attributeName ){
		double factor = 1;
		Double ret = null;
		String s = Parser.getAttribute(e, attributeName);
		if( s == null ) return null;
		try {
			if( s.endsWith("%") ){
				factor = 0.01;
				s = s.substring(0, s.indexOf("%") ).trim();
			}
			ret = new Double( Double.parseDouble(s) * factor);
		}
		catch( NumberFormatException ex ){
			System.out.println("<" + e.getTagName() + " " + attributeName + "= " + JavaCode.toParam(s) + ">: can not convert to an integer.");
		}
		return ret;
	}

	public static String addDeclaration( JavaClass jclass, Element e, Class<?> clazz ){
		String className = getClassName( e, clazz, jclass );
		return addDeclaration(jclass, e, className, ID_ATTRIBUTE );
	}
	
	public static String getSimpleName( String fullName ){
		int pos = fullName.lastIndexOf(".");
		if( pos > 0 ){
			return fullName.substring(pos+1);
		}
		return fullName;
	}

	public static String addDeclaration( JavaClass jclass, Element e, String className, String attributeName ){
		String modifier = "protected";
		
		String varName = (e == null ? null : getAttribute(e, attributeName));
		if( varName == null ){
			varName = Parser.getUniqueId( getSimpleName(className).toLowerCase() );
			modifier = "private";
		}

		jclass.addAnonymousDeclaration( modifier + " " + className + " " + varName + " = new " + className + "();" );
		jclass.register( varName, className );
		return varName;
	}

	/**
	 * Get the class name.
	 * 
	 * @param e the element.
	 * @param clazz the default class, this value is overrided if
	 *    a attribute {@link #CLASS_ATTRIBUTE} is found.
	 * @param jclass the class on which we declare the import. 
	 * @return the name of the class to use to initialize the variables
	 * 		in the sepcified class.
	 */
	public static String getClassName( Element e, Class<?> clazz, JavaClass jclass ){
		String className = (e == null ? null : Parser.getAttribute(e, CLASS_ATTRIBUTE));
		if( className == null ){
			// Only import standard classes. For classes defined by the user,
			// we must avoid use the import process to avoid issues on names.
			jclass.addImport(clazz);
			className = clazz.getSimpleName();		
		}
		return className;
	}

	public static void setDefaultAttributeValue( Element e, String attributeName, String defaultValue ){
		String value = getAttribute(e, attributeName);
		if( value == null ){
			e.setAttribute(attributeName, defaultValue);
		}
	}
}
