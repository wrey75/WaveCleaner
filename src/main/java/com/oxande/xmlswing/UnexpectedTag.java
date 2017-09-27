package com.oxande.xmlswing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A tag exception. This class should be renamed as it does
 * not stick with the real behaviour. This exception is thrown
 * each time something wrong is found in the XML file.
 * 
 * @author $author$
 * @version $Rev: 81 $
 *
 */
@SuppressWarnings("serial")
public class UnexpectedTag extends SAXException {
	public UnexpectedTag( Element e ){
		super("Tag <" + e.getTagName() + "> unexpected. XPath = " + xpath(e) );
	}

	public UnexpectedTag( String text ){
		super(text);
	}
	
	public UnexpectedTag( Element e, String text ){
		super("XPath = " + xpath(e) + ": " + text );
	}

	public static String xpath(Element root ){
		String ret = "";
		Node e = root;
		while( e != null ) {
			ret = "/" + e.getNodeName() + ret;
			e = e.getParentNode();
		}
		return ret;
	}
}
