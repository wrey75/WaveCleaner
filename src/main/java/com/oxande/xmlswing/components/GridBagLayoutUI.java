package com.oxande.xmlswing.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;

import org.w3c.dom.Element;

import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * The GridBagLayout is a layout manager, not a
 * component but it is managed as a JPanel component.
 * This layout is driven by the &lt;table&gt; tag as
 * for a table in HTML.
 * 
 * @author William R
 * @version $Rev$
 *
 */
public class GridBagLayoutUI extends JPanelUI {
	
	public static final String TABLE_TAG = "table";
	public static final String TR_TAG = "tr";
	public static final String TD_TAG = "td";
	public static final String ROWSPAN_ATTRIBUTE = "rowspan";
	public static final String COLSPAN_ATTRIBUTE = "colspan";
	public static final String FILL_ATTRIBUTE = "fill";
	
	public static final String FILL_BOTH = "GridBagConstraints.BOTH";
	public static final String FILL_VERTICAL = "GridBagConstraints.VERTICAL";
	public static final String FILL_HORIZONTAL = "GridBagConstraints.HORIZONTAL";
	public static final String FILL_NONE = "GridBagConstraints.NONE";
	
	public static final String HALIGN_ATTRIBUTE = "align";
	public static final String VALIGN_ATTRIBUTE = "valign";
	
	/**
	 * The number of rows for a table is limited to
	 * MAX_ROWS.
	 * 
	 */
	public static final int MAX_ROWS = 100;

	/**
	 * The number of columns for a table is limited to
	 * MAX_COLS.
	 * 
	 */
	public static final int MAX_COLS = 100;

	
	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		
		int maxCols = 0; // Maximum number of columns
		int maxRows = 0; // Maximum number of rows

		// Pre-count the total number of columns/rows
		int nbRows = 0;
		List<Element> rows = Parser.getChildElements(root, TR_TAG);
		for(Element row : rows ){
			List<Element> cols = Parser.getChildElements(row, TD_TAG);
			int nbCols = 0;
			for(Element e : cols ){
				int rowspan = Parser.getIntegerAttribute(e, ROWSPAN_ATTRIBUTE, 1);
				int colspan = Parser.getIntegerAttribute(e, COLSPAN_ATTRIBUTE, 1);
				maxRows = Math.max(maxRows, nbRows + rowspan);
				maxCols = Math.max(maxCols, nbCols + colspan);
				nbCols++;
			}
			nbRows++;
		}
		
		// Now, we can set the grid size.
		byte grid[][] = new byte[maxRows][maxCols];
		jclass.addImport( GridBagLayout.class );
		jclass.addImport( GridBagConstraints.class );
		varName = Parser.addDeclaration( jclass, root, JPanel.class );
		String tableFiller = getFiller(root, FILL_NONE );
		String hAlignTable = Parser.getAttribute(root, HALIGN_ATTRIBUTE );
		String vAlignTable = Parser.getAttribute(root, VALIGN_ATTRIBUTE );

		String gridName = Parser.getUniqueId("layout");
		String cName = Parser.getUniqueId("c"); // for the constraints.
		initMethod.addCode( "GridBagLayout " + gridName + " = new GridBagLayout();" );
		initMethod.addCode( "GridBagConstraints " + cName + " = new GridBagConstraints();" );

		initMethod.addCall( varName + ".setLayout", gridName );
		CONTROLLER.addToMethod(initMethod, root, varName);
		addBorder(root, initMethod, varName);
			
		int rowNum = 0;
		int maxColumns = 0;
		for( Element row : rows ){
			String rowFiller = getFiller(row, tableFiller);
			String hAlignRow = Parser.getStringAttribute(row, HALIGN_ATTRIBUTE, hAlignTable );
			String vAlignRow = Parser.getStringAttribute(row, VALIGN_ATTRIBUTE, vAlignTable );

			List<Element> columns = Parser.getChildElements(row, TD_TAG );
			if( columns.size() >= MAX_COLS ){
				new UnexpectedTag(root, "Too many columnss for this table. Maximum is " + MAX_COLS );
			}

			int colNum = 0;
			maxColumns = columns.size();
			for( Element col : columns ){
				String filler = getFiller(col, rowFiller);
				initMethod.addCode("");

				// Now parse inside the cell...
				String name;
				List<Element> list = Parser.getChildElementsExcept(col, "br");
				if( list.size() == 0 ){
					// No tag, means the text of the cell is displayed as a label...
					// Note the fill is set to BOTH => valign & align should work
					// better in this case.
					JLabelUI label = new JLabelUI();
					name = label.parse(jclass, initMethod, col);
					// filler = FILL_BOTH;
				}
				else if( list.size() == 1 ){
					// One element, then create it.
					Element e = list.get(0);
					name = JComponentUI.parseComponent(jclass, initMethod, e);
				}
				else {
					// A flow layout to be created...
					name = FlowLayoutUI.parseFlow(jclass, initMethod, col);
				}
				initMethod.addCode( cName + ".gridy = " + rowNum + ";" );
				while( grid[rowNum][colNum] != 0 ){
					// If spanned, you must jump the used parts. This
					// is a very annoying issue for the GridBagLayout but
					// thanks to this class to solve evrything...
					colNum++;
					maxColumns++;
				}
				initMethod.addCode( cName + ".gridx = " + colNum + ";" );

				// ROWSPAN...
				int rowspan = Parser.getIntegerAttribute(col,ROWSPAN_ATTRIBUTE,1);
				initMethod.addCode( cName + ".gridheight = " + rowspan + ";" );

				// COLSPAN...
				int colspan = Parser.getIntegerAttribute(col,COLSPAN_ATTRIBUTE,1);
				initMethod.addCode( cName + ".gridwidth = " + colspan + ";" );
				initMethod.addCode( cName + ".anchor = " + getAnchor( col, hAlignRow, vAlignRow ) + ";" );
				initMethod.addCode( cName + ".fill = " + filler + ";" );

				// Update the grid with the colspan/rowspan
				for (int i=0; i < rowspan; i++){
					for (int j=0; j < colspan; j++){
						grid[rowNum+i][colNum+j] = 1; // used.
					}
				}
				
				colNum += colspan;
				maxColumns += colspan - 1;
//				if( colNum == maxColumns ){
//					initMethod.addCode( cName + ".weightx = GridBagConstraints.REMAINDER;" );
//				}
//				else {
					initMethod.addCode( cName + ".weightx = " + colspan + ";" );
//				}

				initMethod.addCall( gridName + ".setConstraints", name, cName );
				initMethod.addCall( varName+".add", name );
			}
			rowNum++;
		}
		return varName;
	}
	
	private static final Properties POSITIONS = new Properties();
	static {
		POSITIONS.setProperty("left/top", "GridBagConstraints.NORTHWEST" );
		POSITIONS.setProperty("center/top", "GridBagConstraints.NORTH" );
		POSITIONS.setProperty("right/top", "GridBagConstraints.NORTHEAST" );
		POSITIONS.setProperty("left/center", "GridBagConstraints.WEST" );
		POSITIONS.setProperty("center/center", "GridBagConstraints.CENTER" );
		POSITIONS.setProperty("right/center", "GridBagConstraints.EAST" );
		POSITIONS.setProperty("left/bottom", "GridBagConstraints.SOUTHWEST" );
		POSITIONS.setProperty("center/bottom", "GridBagConstraints.SOUTH" );
		POSITIONS.setProperty("right/bottom", "GridBagConstraints.SOUTHEAST" );
	}

	/**
	 * Get the anchor for the cell.
	 * @param cell the cell element.
	 * @param hAlign the default horizontal alignment (inherited from the
	 * 		row defeinition).
	 * @param hAlign the default vertical alignment (inherited from the
	 * 		row defeinition).
	 * @return
	 * @throws UnexpectedTag 
	 */
	private String getAnchor(Element cell, String hAlign, String vAlign ) throws UnexpectedTag{
		String h = Parser.getStringAttribute(cell, HALIGN_ATTRIBUTE, hAlign);
		String v = Parser.getStringAttribute(cell, VALIGN_ATTRIBUTE, vAlign);
		if (h == null) h = "left";
		if (v == null) v = "center";
		String position = (h.trim() + "/" + v.trim()).toLowerCase();
		String anchor = POSITIONS.getProperty(position);
		if(anchor == null ){
			throw new UnexpectedTag(cell, "<td align=\"" + h + "\" valign=\"" + v + "\">: at least one is invalid.");
		}
		return anchor;
	}
	
	private String getFiller(Element e, String defaultFiller ){
		String filler = Parser.getAttribute(e, FILL_ATTRIBUTE);
		if( filler != null ){
			if( filler.equalsIgnoreCase("none")){
				filler = FILL_NONE;
			}
			else if( filler.equalsIgnoreCase("HORIZONTAL")
						|| filler.equalsIgnoreCase("LEFT")
						|| filler.equalsIgnoreCase("RIGHT") ){
				filler = FILL_HORIZONTAL;
				
			}
			else if( filler.equalsIgnoreCase("VERTICAL")
					|| filler.equalsIgnoreCase("TOP")
					|| filler.equalsIgnoreCase("BOTTOM") ){
				filler = FILL_VERTICAL;
			}
			else if( filler.equalsIgnoreCase("both") ){
				filler = FILL_BOTH;
			}
		}
		return defaultFiller;
	}
}
