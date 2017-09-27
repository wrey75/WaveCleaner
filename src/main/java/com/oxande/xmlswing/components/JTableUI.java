package com.oxande.xmlswing.components;

import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.w3c.dom.Element;

import com.oxande.xmlswing.AttributeDefinition;
import com.oxande.xmlswing.AttributesController;
import com.oxande.xmlswing.Parser;
import com.oxande.xmlswing.AttributeDefinition.ClassType;
import com.oxande.xmlswing.UnexpectedTag;
import com.oxande.xmlswing.jcode.JavaClass;
import com.oxande.xmlswing.jcode.JavaCode;
import com.oxande.xmlswing.jcode.JavaMethod;


/**
 * The JTable implementation.
 * 
 * @author wrey75
 * @version $Rev: 47 $
 *
 */
public class JTableUI extends JComponentUI {

	public static final AttributeDefinition[] PROPERTIES = {
		new AttributeDefinition( "autoResizeMode", "setAutoResizeMode", ClassType.JTABLE_AUTO_RESIZE ),
		new AttributeDefinition( "cellSelectionEnabled", "setCellSelectionEnabled", ClassType.BOOLEAN ),
		DRAGGABLE_ATTRIBUTE_DEF,
		new AttributeDefinition( "gridColor", "setGridColor", ClassType.COLOR ),
		new AttributeDefinition( "cellspacing", "setIntercellSpacing", ClassType.DIMENSION ),
		new AttributeDefinition( "rowHeight", "setRowHeight", ClassType.INTEGER ),
		new AttributeDefinition( "rowMargin", "setRowMargin", ClassType.INTEGER ),
		new AttributeDefinition( "backgroundColor", "setSelectionBackground", ClassType.COLOR ),
		new AttributeDefinition( "foregroundColor", "setSelectionForeground", ClassType.COLOR ),
		new AttributeDefinition( "selectionMode", "setSelectionMode", ClassType.JLIST_SELECT_MODE, "single" ),
		new AttributeDefinition( "gridVisible", "setShowGrid", ClassType.BOOLEAN ),
		new AttributeDefinition( "horizontalLinesVisible", "setShowHorizontalLines", ClassType.BOOLEAN ),
		new AttributeDefinition( "verticalLinesVisible", "setShowVerticalLines", ClassType.BOOLEAN ),
		new AttributeDefinition( "surrendersFocusOnKeystroke", null, ClassType.BOOLEAN ),
	};

	public static final AttributesController CONTROLLER = new AttributesController( JComponentUI.CONTROLLER, PROPERTIES );

	public String parse(JavaClass jclass, JavaMethod initMethod, Element root ) throws UnexpectedTag{
		varName = Parser.addDeclaration( jclass, root, JTable.class );
		
		CONTROLLER.addToMethod(initMethod, root, varName);

		addData(jclass, root, varName, initMethod);
		
		// Add the data model
		String dataModel = Parser.getAttribute(root, "model");
		String dataModelClass = Parser.getAttribute(root, "modelClass");
		if( dataModel != null || dataModelClass != null ){
			// If a table model class or an
			// variable to initialize, we can use...
			StringBuilder initCode = new StringBuilder(80);
			if( dataModel == null ){
				dataModel = Parser.getUniqueId( "dtm" );
				initCode.append( "private " );			
			}
			else {
				initCode.append( "protected " );
			}
			initCode.append(dataModelClass == null ? "TableModel" : dataModelClass)
					.append(" ")
					.append(dataModel)
					.append(" = new ");
			
			if( dataModelClass == null ){
				jclass.addImport(TableModel.class);
				initMethod.getComments().add("", 
						"<p><b>NOTE:</p> DO NOT FORGET TO INITIALISE <code>" + dataModel + "<code>",
						"BEFORE CALLING THIS METHOD.</p>" );
				jclass.addImport(DefaultTableModel.class);
				dataModelClass = "DefaultTableModel";
			}
			else {
				// If the class is given, there is no need
				// and the perimeter of the variable can be kept
				// anonymous...
			}
			initCode.append(dataModelClass).append("();");
			
			jclass.addAnonymousDeclaration( initCode );
			initMethod.addCall(varName + ".setModel", dataModel);
		}

		// Add the column model
		String columnModel = Parser.getAttribute(root, "columnModel");
		String columnModelClass = Parser.getAttribute(root, "columnModelClass");
		if( columnModel != null || columnModelClass != null ){
			// If a table model class or an
			// variable to initialize, we can use...
			StringBuilder initCode = new StringBuilder(80);
			if( columnModel == null ){
				columnModel = Parser.getUniqueId( "ctm" );
				initCode.append( "private " );			
			}
			else {
				initCode.append( "protected " );
			}
			initCode.append(columnModelClass == null ? "TableColumnModel" : columnModelClass)
					.append(" ")
					.append(columnModel)
					.append(" = new ");
			
			if( columnModelClass == null ){
				jclass.addImport(TableColumnModel.class);
				initMethod.getComments().add("", 
						"<p><b>NOTE:</p> DO NOT FORGET TO INITIALISE <code>" + columnModel + "<code>",
						"BEFORE CALLING THIS METHOD.</p>" );
				jclass.addImport(DefaultTableColumnModel.class);
				columnModelClass = "DefaultTableColumnModel";
			}
			else {
				// If the class is given, there is no need
				// and the perimeter of the variable can be kept
				// anonymous...
			}
			initCode.append(columnModelClass).append("();");
			
			jclass.addAnonymousDeclaration( initCode );
			initMethod.addCall(varName + ".setColumnModel", columnModel);
		}
		
		// Add renderers for data
		Element renderers = Parser.getChildElement(root, "renderers");
		if( renderers != null ){
			List<Element> children = Parser.getChildElements(root, "renderer");
			for(Element child : children){
				addRenderer(varName, child, initMethod);
			}
		}
		
		String scrollName = JTextAreaUI.addScrollPane(varName,jclass,initMethod,root);
		return (scrollName == null ? varName : scrollName );
	}
	
	private void addRenderer( String tableName, Element element, JavaMethod method ) throws UnexpectedTag{
		String className = Parser.getAttribute(element,"for");
		if( className == null ){
			throw new UnexpectedTag(element, "Attribute \"for\" expected.");
		}
		
		String implName = Parser.getAttribute(element,"class");
		if( implName == null ){
			throw new UnexpectedTag(element, "Attribute \"class\" expected.");
		}
		
		method.addCall(tableName + ".setDefaultRenderer", implName + ".class", "new" + implName + "()" );
	}
	
	private void addData( JavaClass jclass, Element root,  String varName, JavaMethod method ) throws UnexpectedTag{
		List<Element> rows = Parser.getChildElements(root, "tr");
		if( rows.size() > 0 ){
			jclass.addImport( DefaultTableModel.class );
			String defTableModel = Parser.getUniqueId( "dtm" );
			method.addCode( "DefaultTableModel " + defTableModel + " = new DefaultTableModel();");

			int rowNum = 0;
			boolean headerDefined = false;
			int nbColumns = 0;
			for( Element row : rows ){
				
				List<Element> columns = Parser.getChildElements(row);
				if( rowNum == 0 && !headerDefined && columns.size() > 0 && columns.get(0).getTagName().equals("th")){
					// Headers
					nbColumns = columns.size();
					for( Element col : columns ){
						String text = Parser.getTextContents(col);
						method.addCall( defTableModel + ".addColumn", JavaCode.toParam(text) );
					}
					headerDefined = true;
				}
				else {
					if( !headerDefined && rowNum == 0 ){
						nbColumns = columns.size();
					}
					
					jclass.addImport(Vector.class);
					String rowName = Parser.getUniqueId( "row" );
					if( jclass.getVersion() > 0x0104 ){
						method.addCode( "Vector<String> " + rowName + " = new Vector<String>();");
					}
					else {
						method.addCode( "Vector " + rowName + " = new Vector();");
					}

					int colNum = 0;
					for( Element col : columns ){
						if( colNum >= nbColumns ){
							throw new UnexpectedTag(row, "Too many columns for row " + rowNum );
						}
						if( col.getTagName().equals("td") ){
							String text = Parser.getTextContents(col);
							method.addCall(rowName + ".add", JavaCode.toParam(text) );
							colNum++;
						}
					}
					rowNum++;
					method.addCall(defTableModel + ".addRow", rowName );
				}
			}
			method.addCall(varName + ".setModel", defTableModel);
		}
	}

}
