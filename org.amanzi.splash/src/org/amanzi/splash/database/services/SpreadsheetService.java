/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.splash.database.services;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.exception.LoopInCellReferencesException;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.ChartItemNode;
import org.amanzi.neo.core.database.nodes.ChartNode;
import org.amanzi.neo.core.database.nodes.ColumnHeaderNode;
import org.amanzi.neo.core.database.nodes.PieChartItemNode;
import org.amanzi.neo.core.database.nodes.PieChartNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SplashFormatNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.enums.CellRelationTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.Messages;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

import com.eteks.openjeks.format.CellFormat;

/**
 * Service class for working with Neo4j-Spreadsheet
 * 
 * @author Lagutko_N
 */

public class SpreadsheetService {

	/**
	 * <p>
	 * Returnable Evaluator - return cells, that have reference: CellRelationTypes.REFERENCED,
	 * Direction.INCOMING
	 * </p>
	 * 
	 * @author Cinkel_A
	 * @since 1.0.0
	 */
	private final class ReferencedCell implements ReturnableEvaluator {
		@Override
		public boolean isReturnableNode(TraversalPosition traversalposition) {
			if (traversalposition.isStartNode()) {
				return false;
			}
			return traversalposition.currentNode().hasRelationship(CellRelationTypes.REFERENCED, Direction.INCOMING);
		}
	}

	/*
	 * Default value of Cell Value
	 */
	private static final String DEFAULT_VALUE = "";

	/*
	 * Default value of Cell Definition
	 */
	private static final String DEFAULT_DEFINITION = "";

	/*
	 * NeoService Provider
	 */
	private NeoServiceProvider provider;

	/*
	 * NeoService
	 */
	protected NeoService neoService;

	/*
	 * Project Service
	 */
	protected AweProjectService projectService;

	private SplashFormatNode defaultSFNode;
	
	/**
	 * Constructor of Service.
	 * 
	 * Initializes NeoService and create a Root Element
	 */
	public SpreadsheetService() {
		provider = NeoServiceProvider.getProvider();
		neoService = provider.getService();

		projectService = NeoCorePlugin.getDefault().getProjectService();

		defaultSFNode = new SplashFormatNode(neoService.createNode());
		setSplashFormat(defaultSFNode, new CellFormat());
		
	}

	/**
	 * Creates a Spreadsheet by given name
	 * 
	 * @param root
	 *            root node for Spreadsheet
	 * @param name
	 *            name of Spreadsheet
	 * @return create Spreadsheet
	 * @throws SplashDatabaseException
	 *             if Spreadsheet with given name already exists
	 */

	public SpreadsheetNode createSpreadsheet(RubyProjectNode root, String name) throws SplashDatabaseException {
		if (projectService.findSpreadsheet(root, name) != null) {
			String message = SplashDatabaseExceptionMessages.getFormattedString(
					SplashDatabaseExceptionMessages.Duplicate_Spreadsheet, name);
			throw new SplashDatabaseException(message);
		} else {
			Transaction transaction = neoService.beginTx();

			try {
				SpreadsheetNode spreadsheet = new SpreadsheetNode(neoService.createNode(),name);

				root.addSpreadsheet(spreadsheet);

				transaction.success();

				return spreadsheet;
			} finally {
				transaction.finish();
			}
		}
	}

	/**
	 * Creates a Chart in Spreadsheet by given ID
	 * @deprecated
	 * 
	 */
	public ChartNode createChart(SpreadsheetNode spreadsheet, String id) {
		Transaction transaction = neoService.beginTx();

		try {
			ChartNode chartNode = spreadsheet.getChart(id);

			if (chartNode == null) {
				chartNode = new ChartNode(neoService.createNode());
				chartNode.setChartIndex(id);
				spreadsheet.addChart(chartNode);
			}

			transaction.success();

			return chartNode;
		} catch (SplashDatabaseException e) {
			transaction.failure();
			String message = SplashDatabaseExceptionMessages.getFormattedString(
					SplashDatabaseExceptionMessages.Service_Method_Exception, "createChart");
			SplashPlugin.error(message, e);
			return null;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Creates a Chart in Spreadsheet by given ID
	 * 
	 * 
	 */
	public PieChartNode createPieChart(SpreadsheetNode spreadsheet, String id) {
		Transaction transaction = neoService.beginTx();

		try {
			PieChartNode chartNode = spreadsheet.getPieChart(id);

			if (chartNode == null) {
				chartNode = new PieChartNode(neoService.createNode());
				chartNode.setPieChartIndex(id);
				spreadsheet.addPieChart(chartNode);
			}

			transaction.success();

			return chartNode;
		} catch (SplashDatabaseException e) {
			transaction.failure();
			String message = SplashDatabaseExceptionMessages.getFormattedString(
					SplashDatabaseExceptionMessages.Service_Method_Exception, "createChart");
			SplashPlugin.error(message, e);
			return null;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Creates a Chart in Spreadsheet by given ID
	 * @deprecated
	 * 
	 */
	public ChartItemNode createChartItem(ChartNode chartNode, String id) throws SplashDatabaseException {
		Transaction transaction = neoService.beginTx();

		try {
			ChartItemNode ChartItemNode = chartNode.getChartItem(id);

			if (ChartItemNode == null) {
				ChartItemNode = new ChartItemNode(neoService.createNode());
				ChartItemNode.setChartItemIndex(id);
				chartNode.addChartItem(ChartItemNode);
			}

			transaction.success();

			return ChartItemNode;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Creates a Pie Chart in Spreadsheet by given ID
	 * 
	 * 
	 */
	public PieChartItemNode createPieChartItem(PieChartNode chartNode, String id) throws SplashDatabaseException {
		Transaction transaction = neoService.beginTx();

		try {
			PieChartItemNode ChartItemNode = chartNode.getPieChartItem(id);

			if (ChartItemNode == null) {
				ChartItemNode = new PieChartItemNode(neoService.createNode());
				ChartItemNode.setPieChartItemIndex(id);
				chartNode.addPieChartItem(ChartItemNode);
			}

			transaction.success();

			return ChartItemNode;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Creates a Cell in Spreadsheet by given ID
	 * 
	 * @param spreadsheet
	 *            spreadsheet
	 * @param id
	 *            id of Cell
	 * @return created Cell
	 */
	public CellNode createCell(SpreadsheetNode spreadsheet, int row, int column) {
	    Transaction transaction = neoService.beginTx();

		try {
			CellNode cell = new CellNode(neoService.createNode());
			//SplashFormatNode sfNode = new SplashFormatNode(neoService.createNode());
			
			cell.setCellColumn(column + 1);
			cell.setCellRow(row + 1);
			cell.setSpreadsheetId(spreadsheet.getUnderlyingNode().getId());
			
			spreadsheet.addCell(cell);

			transaction.success();

			return cell;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Updates only Cell values
	 * 
	 * @param sheet
	 *            spreadsheet
	 * @param cell
	 *            Cell for update
	 * @return updated Cell
	 */
	public CellNode updateCell(SpreadsheetNode sheet, Cell cell) {
	    CellNode node = getCellNode(sheet, cell.getRow(), cell.getColumn());

		if (node == null) {
			node = createCell(sheet, cell.getRow(), cell.getColumn());
		}

		Transaction transaction = neoService.beginTx();

		try {
			node.setValue(cell.getValue());
			node.setDefinition((String) cell.getDefinition());

			if (cell.hasReference()) {
				node.setScriptURI(cell.getScriptURI());
			}

			CellFormat format = cell.getCellFormat();
			SplashFormatNode sfNode = node.getSplashFormat();
			if (format != null){

				if (isFormatChanged(sfNode, format)==true){
				    NeoSplashUtil.logn("Format has been changed...");

					NeoSplashUtil.logn("Deleting reference to old SplashFormatNode");
					
					//node.getUnderlyingNode().hasRelationship();
					Iterator<Relationship> relationships = node.getUnderlyingNode().getRelationships(SplashRelationshipTypes.SPLASH_FORMAT, Direction.INCOMING).iterator();
			        
			        while (relationships.hasNext()) {
			            Relationship relationship = relationships.next();
		                relationship.delete();
			        }

			        //Lagutko, 27.10.2009, create new SplashFormatNode only if not a default format
			        if (!format.equals(new CellFormat())) {
			            NeoSplashUtil.logn("Adding reference to new SplashFormatNode");

			            SplashFormatNode newSFNode = new SplashFormatNode(neoService.createNode());

			            setSplashFormat(newSFNode, format);

			            newSFNode.addCell(node);
			        }
				}
			}


			//if (format != null && !format.isDefaultFormat()) {



//			SplashFormatNode sfNode = null;
//			if (sfRel != null){
//			sfRel.delete();
//			sfNode = new SplashFormatNode(neoService.createNode());
//			}else{
//			sfNode = node.getSplashFormat();
//			}

//			sfNode.setBackgroundColorB(format.getBackgroundColor().getBlue());
//			sfNode.setBackgroundColorG(format.getBackgroundColor().getGreen());
//			sfNode.setBackgroundColorR(format.getBackgroundColor().getRed());

//			sfNode.setFontColorB(format.getFontColor().getBlue());
//			sfNode.setFontColorG(format.getFontColor().getGreen());
//			sfNode.setFontColorR(format.getFontColor().getRed());

//			sfNode.setFontName(format.getFontName());
//			sfNode.setFontSize(format.getFontSize());
//			sfNode.setFontStyle(format.getFontStyle());
//			sfNode.setVerticalAlignment(format.getVerticalAlignment());
//			sfNode.setHorizontalAlignment(format.getHorizontalAlignment());

			//}

			transaction.success();

			return node;
		} finally {
			transaction.finish();
		}
	}
	
	public void setSplashFormat(SplashFormatNode sfNode, CellFormat format){
	    sfNode.setBackgroundColorB(format.getBackgroundColor().getBlue());
		sfNode.setBackgroundColorG(format.getBackgroundColor().getGreen());
		sfNode.setBackgroundColorR(format.getBackgroundColor().getRed());
		sfNode.setFontColorB(format.getFontColor().getBlue());
		sfNode.setFontColorG(format.getFontColor().getGreen());
		sfNode.setFontColorR(format.getFontColor().getRed());
		sfNode.setFontName(format.getFontName());
		sfNode.setFontSize(format.getFontSize());
		sfNode.setFontStyle(format.getFontStyle());
		sfNode.setVerticalAlignment(format.getVerticalAlignment());
		sfNode.setHorizontalAlignment(format.getHorizontalAlignment());
		//Lagutko, 5.10.2009, also store a Data Format of Cell
		sfNode.setFormat(format.getFormat());
	}

	/**
	 * Returns Pie Chart Node by given ID
	 * 
	 * @param sheet
	 *            spreadsheet
	 * 
	 */
	private PieChartNode getPieChartNode(SpreadsheetNode sheet, String id) {
		Transaction transaction = neoService.beginTx();

		try {
			PieChartNode result = sheet.getPieChartNode(id);

			transaction.success();

			return result;
		} finally {
			transaction.finish();
		}
	}

	private boolean isFormatChanged(SplashFormatNode sfNode, CellFormat newCF){
	    if (sfNode == null) {
	        return true;
	    }
	    Integer bgColorB = sfNode.getBackgroundColorB();
		Integer bgColorG = sfNode.getBackgroundColorG();
		Integer bgColorR = sfNode.getBackgroundColorR();

		Integer fontColorB = sfNode.getFontColorB();
		Integer fontColorG = sfNode.getFontColorG();
		Integer fontColorR = sfNode.getFontColorR();
		String fontName = sfNode.getFontName();
		Integer fontSize = sfNode.getFontSize();
		Integer fontStyle = sfNode.getFontStyle();
		Integer hAllign = sfNode.getHorizontalAlignment();
		Integer vAllign = sfNode.getVerticalAlignment();

		try{

			if (bgColorB != newCF.getBackgroundColor().getBlue()) {
				NeoSplashUtil.logn("bgColorB changed");
				return true;
			}
			if (bgColorG != newCF.getBackgroundColor().getGreen()) {
				NeoSplashUtil.logn("bgColorG changed");
				return true;
			}
			if (bgColorR != newCF.getBackgroundColor().getRed()) {
				NeoSplashUtil.logn("bgColorR changed");
				return true;
			}

			if (fontColorB != newCF.getFontColor().getBlue()) {
				NeoSplashUtil.logn("fontColorB changed");
				return true;
			}
			if (fontColorG != newCF.getFontColor().getGreen()) {
				NeoSplashUtil.logn("fontColorG changed");
				return true;
			}
			if (fontColorR != newCF.getFontColor().getRed()) {
				NeoSplashUtil.logn("fontColorR changed");
				return true;
			}

			if (!fontName.equals(newCF.getFontName())) {
				NeoSplashUtil.logn("fontName changed");
				return true;
			}
			if (fontSize != newCF.getFontSize()) {
				NeoSplashUtil.logn("fontSize changed");
				return true;
			}
			if (fontStyle != newCF.getFontStyle()) {
				NeoSplashUtil.logn("fontStyle changed");
				return true;
			}
			if (hAllign != newCF.getHorizontalAlignment()) {
				NeoSplashUtil.logn("hAllign changed");
				return true;
			}
			if (vAllign != newCF.getVerticalAlignment()) {
				NeoSplashUtil.logn("vAllign changed");
				return true;
			}
		}catch (Exception ex){
			return false;
		}

		return false;
	}

	/**
	 * Converts CellNode to Cell
	 * 
	 * @param node
	 *            CellNode
	 * @return Cell
	 */
    public Cell convertNodeToCell(CellNode node, Integer rowIndex, Integer columnIndex) {
	    if (rowIndex == null) {
			rowIndex = node.getCellRow() - 1;
		}

		if (columnIndex == null) {			
			columnIndex = node.getCellColumn() - 1;
		}

		CellFormat cellFormat = new CellFormat();
		
		SplashFormatNode sfNode = node.getSplashFormat();
		if (sfNode != null) {
		    //Lagutko, 5.10.2009, get a Data Format from Node
		    cellFormat.setFormat(sfNode.getFormat());
		
		    Integer bgColorB = sfNode.getBackgroundColorB();
		    Integer bgColorG = sfNode.getBackgroundColorG();
		    Integer bgColorR = sfNode.getBackgroundColorR();

		    if ((bgColorB != null) && (bgColorG != null) && (bgColorR != null)) {
		        Color color = new Color(bgColorR, bgColorG, bgColorB);
		        cellFormat.setBackgroundColor(color);
		    }

		    Integer fontColorB = sfNode.getFontColorB();
		    Integer fontColorG = sfNode.getFontColorG();
		    Integer fontColorR = sfNode.getFontColorR();

		    if ((fontColorB != null) && (fontColorG != null) && (fontColorR != null)) {
		        Color color = new Color(fontColorR, fontColorG, fontColorB);
		        cellFormat.setFontColor(color);
		    }

		    cellFormat.setFontName(sfNode.getFontName());
		    cellFormat.setFontSize(sfNode.getFontSize());
		    cellFormat.setFontStyle(sfNode.getFontStyle());
		    cellFormat.setHorizontalAlignment(sfNode.getHorizontalAlignment());
		    cellFormat.setVerticalAlignment(sfNode.getVerticalAlignment());
		}
		
		Object value = node.getValue();
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (node.isCyclic()) {
			value = Cell.CELL_CYLIC_ERROR;
		}

		String definition = node.getDefinition();
		if (definition == null) {
			definition = DEFAULT_DEFINITION;
		}

		Cell result = new Cell(rowIndex, columnIndex, definition, value, cellFormat);
		result.setScriptURI(node.getScriptURI());
		
		return result;
	}


	/**
	 * Returns Cell by given ID
	 * 
	 * @param sheet
	 *            spreadsheet
	 * @param id
	 *            cell ID
	 * @return converted Cell from Database
	 */
	public Cell getCell(SpreadsheetNode sheet, int row, int column) {
	    CellNode node = getCellNode(sheet, row, column);
	    
	    if (node != null) {
	        //Lagutko, 6.10.2009, convertNodeToCell use access to database and should be wrapped in transaction
	    	Transaction transaction = neoService.beginTx();
	    	try {
                return convertNodeToCell(node, row, column);
	    	}
	    	finally {
	    		transaction.success();
	    		transaction.finish();
	    	}
	    }

	    return new Cell(row, column, DEFAULT_DEFINITION, DEFAULT_VALUE, new CellFormat());
	}

	/**
	 * Returns CellNode by given ID
	 * 
	 * @param sheet
	 *            spreadsheet
	 * @param id
	 *            id of Cell
	 * @return CellNode by ID or null if Cell doesn't exists
	 */
	public CellNode getCellNode(SpreadsheetNode sheet, int row, int column) {
	    Transaction transaction = neoService.beginTx();

		try {
			CellNode result = sheet.getCell(row, column);

			transaction.success();

			return result;
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Returns ChartNode by given ID
	 * 
	 * @param sheet
	 *            spreadsheet
	 * @param id
	 *            id of Cell
	 * @return CellNode by ID or null if Cell doesn't exists
	 */
	private ChartNode getChartNode(SpreadsheetNode sheet, String id) {
		Transaction transaction = neoService.beginTx();

		try {
			ChartNode result = sheet.getChartNode(id);

			transaction.success();

			return result;
		} finally {
			transaction.finish();
		}
	}


	/**
	 * Returns RFD Cells of Cell by given ID
	 * 
	 * @param sheet
	 *            Spreadsheet
	 * @param cellID
	 *            id of Cell
	 * @return RFD cells of Cell
	 */
	public ArrayList<Cell> getDependentCells(SpreadsheetNode sheet, int row, int column) {
		CellNode currentNode = getCellNode(sheet, row, column);

		Iterator<CellNode> rfdNodes = currentNode.getDependedNodes();

		ArrayList<Cell> result = new ArrayList<Cell>(0);

		while (rfdNodes.hasNext()) {
            result.add(convertNodeToCell(rfdNodes.next(), null, null));
		}

		return result;
	}

	/**
	 * Deletes the Cell from Spreadsheet
	 * 
	 * @param sheet
	 *            Spreadsheet Node
	 * @param id
	 *            ID of Cell to delete
	 * @return is Cell was successfully deleted
	 */
	public boolean deleteCell(SpreadsheetNode sheet, int row, int column) {
		CellNode cell = getCellNode(sheet, row, column);

		if (cell != null) {
			// check if there are cells that are dependent on this cell
			if (cell.getDependedNodes().hasNext()) {
				// we can't delete Cell on which other Cell depends
				return false;
			}

			cell.delete();			
		}

		return true;
	}

	/**
	 * Returns all Charts of Spreadsheet
	 * 
	 * @param sheet Spreadsheet
	 * @return all Cells of given Spreadsheet
	 * @deprecated
	 */

	public List<ChartItemNode> getAllChartItems(ChartNode chartNode) {
		ArrayList<ChartItemNode> chartItemsList = new ArrayList<ChartItemNode>(0);

//		Iterator<ChartItemNode> chartItems = chartNode.getAllChartItems();
//
//		while (chartItems.hasNext()) {
//			ChartItemNode chartItem = chartItems.next();
//			String chartItemIndex = chartItem.getChartItemIndex();
//
//			chartItemsList.add(chartItem);
//		}

		return chartItemsList;
	}

	/**
	 * Returns all Pie Charts of Spreadsheet
	 * 
	 * @param sheet Spreadsheet
	 * @return all Cells of given Spreadsheet
	 */

	public List<PieChartItemNode> getAllPieChartItems(PieChartNode chartNode) {
		ArrayList<PieChartItemNode> chartItemsList = new ArrayList<PieChartItemNode>(0);

		Iterator<PieChartItemNode> chartItems = chartNode.getAllPieChartItems();

		while (chartItems.hasNext()) {
			PieChartItemNode chartItem = chartItems.next();
			String chartItemIndex = chartItem.getPieChartItemIndex();

			chartItemsList.add(chartItem);
		}

		return chartItemsList;
	}

	/**
	 * Updates References of Cell
	 * 
	 * @param sheet Spreadsheet of Cell
	 * @param cellID ID of Cell
	 * @param array Array with IDs of referenced Cells
	 */
	public void updateCellReferences(SpreadsheetNode sheet, String cellID, RubyArray array) {
		List<String> referencedIds = new ArrayList<String>(0);
		for (IRubyObject rubyString : array.toJavaArray()) {
			referencedIds.add(rubyString.toString());
		}

		CellID updatedId = new CellID(cellID);
		CellNode updatedNode = getCellNode(sheet, updatedId.getRowIndex(), updatedId.getColumnIndex());

		if (updatedNode == null) {
			updatedNode = updateCell(sheet, new Cell(updatedId.getRowIndex(), updatedId.getColumnIndex(), Cell.DEFAULT_DEFINITION,
					Cell.DEFAULT_VALUE, new CellFormat()));
		}
		updatedNode.setCyclic(false);

		Transaction transaction = neoService.beginTx();
		try {
			Iterator<CellNode> dependentCells = updatedNode.getReferencedNodes();

			ArrayList<CellNode> nodesToDelete = new ArrayList<CellNode>(0);

			while (dependentCells.hasNext()) {
				CellNode dependentCell = dependentCells.next();
				CellID id = new CellID(dependentCell.getCellRow(), dependentCell.getCellColumn());

				if (!referencedIds.contains(id)) {
					nodesToDelete.add(dependentCell);
					referencedIds.remove(id);
				}
			}

			updatedNode.deleteReferenceFromNode(nodesToDelete);

			for (String ID : referencedIds) {
				CellID id = new CellID(ID);

				CellNode node = getCellNode(sheet, id.getRowIndex(), id.getColumnIndex());

				if (node == null) {
					node = updateCell(sheet, new Cell(id.getRowIndex(), id.getColumnIndex(), DEFAULT_VALUE, DEFAULT_DEFINITION,
							new CellFormat()));
				}

				try {
					updatedNode.addDependedNode(node);
				} catch (LoopInCellReferencesException e) {
					updatedNode.setCyclic(true);
				}
			}

			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Get cells FullId
	 * 
	 * @param cell cell node
	 * @return FullId
	 */
	public String getFullId(CellNode cell) {
		Transaction transaction = neoService.beginTx();
		try {
			CellID cellId = new CellID(cell.getCellRow(), cell.getCellColumn());
			transaction.success();
			return cellId.getFullID();
		} finally {
			transaction.finish();
		}
	}

	/**
	 * Insert row
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index row index (begin index: 0)
	 */
	public void insertRow(SpreadsheetNode spreadsheet, final int index) {
//		Transaction transaction = neoService.beginTx();
//		try {
//			Iterator<Node> rowIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					return Integer.parseInt(position.lastRelationshipTraversed().getEndNode()
//							.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()) > index;
//				}
//
//			}, SplashRelationshipTypes.ROW, Direction.OUTGOING).iterator();
//			TreeSet<RowNode> rows = new TreeSet<RowNode>(new Comparator<RowNode>() {
//				@Override
//				public int compare(RowNode o1, RowNode o2) {
//					return Integer.parseInt(o2.getRowIndex()) - (Integer.parseInt(o1.getRowIndex()));
//				}
//			});
//			while (rowIterator.hasNext()) {
//				rows.add(RowNode.fromNode(rowIterator.next()));
//			}
//			for (RowNode rowNode : rows) {
//				Node row = rowNode.getUnderlyingNode();
//				int rowIndex = Integer.parseInt(rowNode.getRowIndex());
//				Iterator<Node> cellIterator = row.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReferencedCell(), SplashRelationshipTypes.ROW_CELL, Direction.OUTGOING).iterator();
//				while (cellIterator.hasNext()) {
//					Node cell = (Node) cellIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					String columnName = cellNode.getColumn().getColumnName();
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = (CellNode) referencedNodes.next();
//						String formula = refCell.getDefinition();
//						if (formula != null && formula.length() > 0) {
//							formula = updatingFormula(formula, rowIndex, columnName, rowIndex + 1, columnName);
//							refCell.setDefinition(formula);
//							URI scriptURI = refCell.getScriptURI();
//							if (scriptURI != null) {
//								updateScript(scriptURI, formula);
//							}
//						}
//					}
//				}
//				rowNode.setRowIndex(String.valueOf(rowIndex + 1));
//			}
//			transaction.success();
//		} finally {
//			transaction.finish();
//		}
	}

	/**
	 * Updates script
	 * 
	 * @param scriptURI script URI
	 * @param formula new formula
	 * @return true if no error
	 */
	private boolean updateScript(URI scriptURI, String formula) {
		File file = new File(scriptURI);
		FileWriter fr;
		try {
			fr = new FileWriter(file);
			fr.write(formula);
			fr.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Updating formula
	 * 
	 * @param formula formula to update
	 * @param rowIndex old row index
	 * @param columnName old column name
	 * @param newRowIndex new row index
	 * @param newColumnName new column name
	 * @return new formula
	 */
	private String updatingFormula(String formula, int rowIndex, String columnName, int newRowIndex, String newColumnName) {

		String oldCellName = columnName.toLowerCase() + rowIndex;
		String newCellName = newColumnName.toLowerCase() + newRowIndex;
		if (oldCellName.equalsIgnoreCase(newCellName)) {
			return formula;
		}
		String regexp = "([^a-zA-Z0-9])" + "(" + oldCellName + ")" + "(([^a-zA-Z0-9])|($))";
		Pattern p = Pattern.compile(regexp);
		StringBuffer result = new StringBuffer();
		Matcher m = p.matcher(formula);
		while (m.find()) {
			m.appendReplacement(result, "$1" + newCellName + "$3");
			m.appendTail(result);
			m = p.matcher(result);
			result = new StringBuffer();
		}
		m.appendTail(result);
		return result.toString();
	}

	/**
	 * Deleting row
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index row index (begin index: 0)
	 * @return true if all ok.
	 */
	public boolean deleteRow(SpreadsheetNode spreadsheet, final int index) {
//		Transaction transaction = neoService.beginTx();
//		try {
//			String indexRow = String.valueOf(index + 1);
//			Iterator<Node> cellIterator;
//			RowNode rowNod = spreadsheet.getRow(indexRow);
//			if (rowNod != null) {
//				cellIterator = rowNod.getUnderlyingNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
//						new ReferencedCell(), SplashRelationshipTypes.ROW_CELL, Direction.OUTGOING).iterator();
//				if (cellIterator.hasNext()) {
//					transaction.success();
//					return false;
//				}
//				deleteRow(rowNod);
//			}
//			Iterator<Node> rowIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					return Integer.parseInt(position.lastRelationshipTraversed().getEndNode()
//							.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()) > index;
//				}
//
//			}, SplashRelationshipTypes.ROW, Direction.OUTGOING).iterator();
//			TreeSet<RowNode> rows = new TreeSet<RowNode>(new Comparator<RowNode>() {
//				@Override
//				public int compare(RowNode o1, RowNode o2) {
//					return Integer.parseInt(o1.getRowIndex()) - (Integer.parseInt(o2.getRowIndex()));
//				}
//			});
//			while (rowIterator.hasNext()) {
//				rows.add(RowNode.fromNode(rowIterator.next()));
//			}
//			for (RowNode rowNode : rows) {
//				int rowIndex = Integer.parseInt(rowNode.getRowIndex());
//				cellIterator = rowNode.getUnderlyingNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
//						new ReferencedCell(), SplashRelationshipTypes.ROW_CELL, Direction.OUTGOING).iterator();
//				while (cellIterator.hasNext()) {
//					Node cell = (Node) cellIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					String columnName = cellNode.getColumn().getColumnName();
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = (CellNode) referencedNodes.next();
//						String formula = refCell.getDefinition();
//						if (formula != null && formula.length() > 0) {
//							formula = updatingFormula(formula, rowIndex, columnName, rowIndex - 1, columnName);
//							refCell.setDefinition(formula);
//							URI scriptURI = refCell.getScriptURI();
//							if (scriptURI != null) {
//								updateScript(scriptURI, formula);
//							}
//						}
//					}
//				}
//				rowNode.setRowIndex(String.valueOf(rowIndex - 1));
//			}
//
//			transaction.success();
//			return true;
//		} catch (SplashDatabaseException e) {
//			transaction.failure();
//			return false;
//		} finally {
//			transaction.finish();
//		}
	    return true;
	}

//	/**
//	 * Delete row
//	 * 
//	 * @param rowNode row to delete
//	 */
//	private void deleteRow(RowNode rowNode) {
//		Iterator<CellNode> allCells = rowNode.getAllCells();
//		while (allCells.hasNext()) {
//			CellNode cellNode = (CellNode) allCells.next();
//			deleteNode(cellNode.getUnderlyingNode());
//		}
//		deleteNode(rowNode.getUnderlyingNode());
//	}

//	/**
//	 * Delete column
//	 * 
//	 * @param columnNode row to delete
//	 */
//	private void deleteColumn(ColumnNode columnNode) {
//		Iterator<CellNode> allCells = columnNode.getAllCells();
//		while (allCells.hasNext()) {
//			CellNode cellNode = (CellNode) allCells.next();
//			deleteNode(cellNode.getUnderlyingNode());
//		}
//		deleteNode(columnNode.getUnderlyingNode());
//	}

	/**
	 * Delete node
	 * 
	 * @param node node to delete
	 */
	private void deleteNode(Node node) {
		Iterable<Relationship> relationships = node.getRelationships();
		for (Relationship relationship : relationships) {
			relationship.delete();
		}
		node.delete();
	}

	/**
	 * Insert column
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index row index (begin index: 0)
	 */
	public void insertColumn(SpreadsheetNode spreadsheet, final int index) {
	    ColumnHeaderNode column = spreadsheet.getColumnHeader(index);
	    
	    for (CellNode cellInColumn : column.getAllCellsFromThis(true)) {
	        for (CellNode cellInRow : cellInColumn.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_ROW, true)) {
	            cellInRow.setCellColumn(cellInRow.getCellColumn() + 1);
	            //TODO: Lagutko: reindex	            
	        }
	    }
	    
//		Transaction transaction = neoService.beginTx();
//		try {
//			Iterator<Node> columnIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					boolean result = CellID
//					.getColumnIndexFromCellID(ColumnNode.fromNode(position.currentNode()).getColumnName()) >= index;
//					System.out.println(result);
//					return result;
//				}
//
//			}, SplashRelationshipTypes.COLUMN, Direction.OUTGOING).iterator();
//			List<ColumnNode> columns = new ArrayList<ColumnNode>();
//			while (columnIterator.hasNext()) {
//				columns.add(ColumnNode.fromNode(columnIterator.next()));
//			}
//			Collections.sort(columns, new Comparator<ColumnNode>() {
//				@Override
//				public int compare(ColumnNode o1, ColumnNode o2) {
//					return CellID.getColumnIndexFromCellID(o2.getColumnName())
//					- CellID.getColumnIndexFromCellID(o1.getColumnName());
//				}
//			});
//			for (ColumnNode columnNode : columns) {
//				Node column = columnNode.getUnderlyingNode();
//				String colName = columnNode.getColumnName();
//				int colIndex = CellID.getColumnIndexFromCellID(colName);
//				Iterator<Node> cellIterator = column.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReferencedCell(),
//						SplashRelationshipTypes.COLUMN_CELL, Direction.OUTGOING).iterator();
//				String newColumnLetter = CellID.getColumnLetter(colIndex + 1);
//				while (cellIterator.hasNext()) {
//					Node cell = (Node) cellIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					int rowIndex = Integer.parseInt(cellNode.getRow().getRowIndex());
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = (CellNode) referencedNodes.next();
//						String formula = refCell.getDefinition();
//						if (formula != null && formula.length() > 0) {
//							formula = updatingFormula(formula, rowIndex, colName, rowIndex, newColumnLetter);
//							refCell.setDefinition(formula);
//							URI scriptURI = refCell.getScriptURI();
//							if (scriptURI != null) {
//								updateScript(scriptURI, formula);
//							}
//						}
//					}
//				}
//				columnNode.setColumnName(newColumnLetter);
//			}
//			transaction.success();
//		} finally {
//			transaction.finish();
//		}
	}

	/**
	 * Deleting column
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index column index (begin index: 0)
	 * @return true if all ok.
	 */
	public boolean deleteColumn(SpreadsheetNode spreadsheet, final int index) {
//		Transaction transaction = neoService.beginTx();
//		try {
//			String indexColumn = CellID.getColumnLetter(index);
//			Iterator<Node> cellIterator;
//			ColumnNode columnNod = spreadsheet.getColumn(indexColumn);
//			if (columnNod != null) {
//				cellIterator = columnNod.getUnderlyingNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
//						new ReferencedCell(), SplashRelationshipTypes.COLUMN_CELL, Direction.OUTGOING).iterator();
//				if (cellIterator.hasNext()) {
//					transaction.success();
//					return false;
//				}
//				deleteColumn(columnNod);
//			}
//			// find columns for change of their number
//			Iterator<Node> columnIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					boolean result = CellID
//					.getColumnIndexFromCellID(ColumnNode.fromNode(position.currentNode()).getColumnName()) > index;
//					System.out.println(result);
//					return result;
//				}
//
//			}, SplashRelationshipTypes.COLUMN, Direction.OUTGOING).iterator();
//			List<ColumnNode> columns = new ArrayList<ColumnNode>();
//			while (columnIterator.hasNext()) {
//				columns.add(ColumnNode.fromNode(columnIterator.next()));
//			}
//			Collections.sort(columns, new Comparator<ColumnNode>() {
//				@Override
//				public int compare(ColumnNode o1, ColumnNode o2) {
//					return CellID.getColumnIndexFromCellID(o2.getColumnName())
//					- CellID.getColumnIndexFromCellID(o1.getColumnName());
//				}
//			});
//			for (ColumnNode columnNode : columns) {
//				Node column = columnNode.getUnderlyingNode();
//				String colName = columnNode.getColumnName();
//				int colIndex = CellID.getColumnIndexFromCellID(colName);
//				Iterator<Node> celIterator = column.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReferencedCell(),
//						SplashRelationshipTypes.COLUMN_CELL, Direction.OUTGOING).iterator();
//				String newColumnLetter = CellID.getColumnLetter(colIndex - 1);
//				// change formula for all referenced cells
//				while (celIterator.hasNext()) {
//					Node cell = (Node)celIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					int rowIndex = Integer.parseInt(cellNode.getRow().getRowIndex());
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = (CellNode)referencedNodes.next();
//						String formula = refCell.getDefinition();
//						if (formula != null && formula.length() > 0) {
//							formula = updatingFormula(formula, rowIndex, colName, rowIndex, newColumnLetter);
//							refCell.setDefinition(formula);
//							URI scriptURI = refCell.getScriptURI();
//							if (scriptURI != null) {
//								updateScript(scriptURI, formula);
//							}
//						}
//					}
//				}
//				columnNode.setColumnName(newColumnLetter);
//			}
//
//			transaction.success();
//			return true;
//		} catch (SplashDatabaseException e) {
//			transaction.failure();
//			return false;
//		} finally {
//			transaction.finish();
//		}
	    return true;

	}

	/**
	 * Swap rows in database
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index1 row1 index
	 * @param index2 row2 index
	 */
	public void swapRows(SpreadsheetNode spreadsheet, final int index1, final int index2) {
//		Transaction transaction = neoService.beginTx();
//		try {
//			// find necessary rows in database
//			Iterator<Node> rowIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					int rowIndex = Integer.parseInt(position.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()) - 1;
//					return index1 == rowIndex || index2 == rowIndex;
//				}
//
//			}, SplashRelationshipTypes.ROW, Direction.OUTGOING).iterator();
//			List<RowNode> rows = new ArrayList<RowNode>();
//			while (rowIterator.hasNext()) {
//				rows.add(RowNode.fromNode(rowIterator.next()));
//			}
//			Set<Long> cells = new HashSet<Long>();
//			for (RowNode rowNode : rows) {
//				Node row = rowNode.getUnderlyingNode();
//				int rowIndex = Integer.parseInt(rowNode.getRowIndex()) - 1;
//				Iterator<Node> cellIterator = row.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReferencedCell(),
//						SplashRelationshipTypes.ROW_CELL, Direction.OUTGOING).iterator();
//				int newRowIndex = index1 == rowIndex ? index2 : index1;
//				// change formula for all referenced cells
//				while (cellIterator.hasNext()) {
//					Node cell = (Node) cellIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = referencedNodes.next();
//						long id = refCell.getUnderlyingNode().getId();
//						if (!cells.contains(id)) {
//							cells.add(id);
//							String formula = refCell.getDefinition();
//							if (formula != null && formula.length() > 0) {
//								formula = swapRows(formula, index1, index2);
//								refCell.setDefinition(formula);
//								URI scriptURI = refCell.getScriptURI();
//								if (scriptURI != null) {
//									updateScript(scriptURI, formula);
//								}
//							}
//						}
//					}
//				}
//
//				rowNode.setRowIndex(String.valueOf(newRowIndex + 1));
//			}
//			transaction.success();
//		} finally {
//			transaction.finish();
//		}
	}

	/**
	 * Swap row numbers in formula
	 * 
	 * @param formula formula
	 * @param index1 row1 index
	 * @param index2 row2 index
	 * @return
	 */
	private String swapRows(String formula, int index1, int index2) {
		String rowIndex1 = String.valueOf(index1 + 1);
		String rowIndex2 = String.valueOf(index2 + 1);
		String regexp = "([^a-zA-Z0-9])" + "(([a-z]{1,3})([0-9]{1,6}))" + "(([^a-zA-Z0-9])|($))";
		Pattern p = Pattern.compile(regexp);
		StringBuffer result = new StringBuffer();
		Matcher m = p.matcher(formula);
		int i = 0;
		while (m.find(i)) {
			String rowInd = m.group(4);
			String newCell;
			if (rowIndex1.equals(rowInd)) {
				newCell = "$1" + m.group(3) + rowIndex2 + "$5";
			} else if (rowIndex2.equals(rowInd)) {
				newCell = "$1" + m.group(3) + rowIndex1 + "$5";
			} else {
				newCell = m.group(0);
			}
			m.appendReplacement(result, newCell);
			i = result.length() - 1;
			m.appendTail(result);
			m = p.matcher(result);
			result = new StringBuffer();
		}
		m.appendTail(result);
		return result.toString();
	}

	/**
	 * Swap columns in database
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index1 column1 index
	 * @param index2 column2 index
	 */
	public void swapColumns(SpreadsheetNode spreadsheet, final int index1, final int index2) {
//		final String column1Name = CellID.getColumnLetter(index1);
//		final String column2Name = CellID.getColumnLetter(index2);
//		Transaction transaction = neoService.beginTx();
//		try {
//			// find necessary column in database
//			Iterator<Node> colIterator = spreadsheet.getUnderlyingNode().traverse(Traverser.Order.BREADTH_FIRST,
//					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//				public boolean isReturnableNode(TraversalPosition position) {
//					if (position.isStartNode()) {
//						return false;
//					}
//					String columnName = (String) position.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME);
//					return column1Name.equals(columnName) || column2Name.equals(columnName);
//				}
//
//			}, SplashRelationshipTypes.COLUMN, Direction.OUTGOING).iterator();
//			List<ColumnNode> columns = new ArrayList<ColumnNode>();
//			while (colIterator.hasNext()) {
//				columns.add(ColumnNode.fromNode(colIterator.next()));
//			}
//			Set<Long> cells = new HashSet<Long>();
//			for (ColumnNode colNode : columns) {
//				Node column = colNode.getUnderlyingNode();
//				String colName = colNode.getColumnName();
//				Iterator<Node> cellIterator = column.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReferencedCell(),
//						SplashRelationshipTypes.COLUMN_CELL, Direction.OUTGOING).iterator();
//				String newColName = column1Name.equals(colName) ? column2Name : column1Name;
//				// change formula for all referenced cells
//				while (cellIterator.hasNext()) {
//					Node cell = (Node) cellIterator.next();
//					CellNode cellNode = new CellNode(cell);
//					Iterator<CellNode> referencedNodes = cellNode.getDependedNodes();
//					while (referencedNodes.hasNext()) {
//						CellNode refCell = referencedNodes.next();
//						long id = refCell.getUnderlyingNode().getId();
//						if (!cells.contains(id)) {
//							cells.add(id);
//							String formula = refCell.getDefinition();
//							if (formula != null && formula.length() > 0) {
//								formula = swapColumns(formula, column1Name, column2Name);
//								refCell.setDefinition(formula);
//								URI scriptURI = refCell.getScriptURI();
//								if (scriptURI != null) {
//									updateScript(scriptURI, formula);
//								}
//							}
//						}
//					}
//				}
//
//				colNode.setColumnName(newColName);
//			}
//			transaction.success();
//		} finally {
//			transaction.finish();
//		}
	}

	/**
	 * Swap columns in formula
	 * 
	 * @param formula formula
	 * @param column1Name name of column1
	 * @param column2Name name of column2
	 * @return
	 */
	private String swapColumns(String formula, String column1Name, String column2Name) {
		column1Name = column1Name.toLowerCase();
		column2Name = column2Name.toLowerCase();
		String regexp = "([^a-zA-Z0-9])" + "(([a-z]{1,3})([0-9]{1,6}))" + "(([^a-zA-Z0-9])|($))";
		Pattern p = Pattern.compile(regexp);
		StringBuffer result = new StringBuffer();
		Matcher m = p.matcher(formula);
		int i = 0;
		while (m.find(i)) {
			String colName = m.group(3);
			String newCell;
			if (column1Name.equals(colName)) {
				newCell = "$1" + column2Name + m.group(4) + "$5";
			} else if (column2Name.equals(colName)) {
				newCell = "$1" + column1Name + m.group(4) + "$5";
			} else {
				newCell = m.group(0);
			}
			m.appendReplacement(result, newCell);
			i = result.length() - 1;
			m.appendTail(result);
			m = p.matcher(result);
			result = new StringBuffer();
		}
		m.appendTail(result);
		return result.toString();
	}

}
