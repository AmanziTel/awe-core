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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.amanzi.neo.core.database.nodes.RowHeaderNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SplashFormatNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;
import org.rubypeople.rdt.refactoring.core.renamelocal.LocalVariableRenamer;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;

import com.eteks.openjeks.format.CellFormat;

/**
 * Service class for working with Neo4j-Spreadsheet
 * 
 * @author Lagutko_N
 */

public class SpreadsheetService {

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
        Transaction tx = neoService.beginTx();
        try {
            projectService = NeoCorePlugin.getDefault().getProjectService();
            defaultSFNode = new SplashFormatNode(neoService.createNode());
            setSplashFormat(defaultSFNode, new CellFormat());
        } finally {
            tx.finish();
        }
		
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
	public ChartItemNode createChartItem(ChartNode chartNode, Integer id) throws SplashDatabaseException {
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
	 * @param rowIndex row index (begin index: 0)
	 */
	public void insertRow(SpreadsheetNode spreadsheet, int rowIndex) {
		Transaction transaction = neoService.beginTx();
		
		try {
			//update column index to use in HilbertIndexes
			rowIndex = rowIndex + 1;
			RowHeaderNode row = spreadsheet.getRowHeader(rowIndex);
	    
			for (CellNode cellInRow : row.getAllCellsFromThis(true)) {
				for (CellNode cellInColumn : cellInRow.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_COLUMN, true)) {
					cellInColumn.setCellRow(cellInColumn.getCellRow() + 1);
					spreadsheet.updateCellIndex(cellInColumn);
					
					Iterator<CellNode> referencedNode = cellInColumn.getReferencedNodes();
					String formula = cellInColumn.getDefinition();
					
					while (referencedNode.hasNext()) {
						CellNode nodeToUpdate = referencedNode.next();
						
						int oldRow = nodeToUpdate.getCellRow();
						int newRow = oldRow + 1;
						int column = nodeToUpdate.getCellColumn();						
												
						formula = updatingFormula(formula, oldRow, column, newRow, column);
					}
					if (formula != null) {
						cellInColumn.setDefinition(formula);
					}
				}
				int columnIndex = cellInRow.getCellColumn();	        
				spreadsheet.clearCellIndex(rowIndex, columnIndex);
			}
			transaction.success();
		}
		catch (Exception e) {
			transaction.failure();
			e.printStackTrace();
		}
		finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
	}

	/**
	 * Deleting row
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index row index (begin index: 0)
	 * @return true if all ok.
	 */
	public boolean deleteRow(SpreadsheetNode spreadsheet, int rowIndex) {
		Transaction transaction = neoService.beginTx();
		
		try {
			//update column index to use in HilbertIndexes
			rowIndex = rowIndex + 1;
			RowHeaderNode row = spreadsheet.getRowHeader(rowIndex);
	    
			for (CellNode cellInRow : row.getAllCellsFromThis(true)) {				
				for (CellNode cellInColumn : cellInRow.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_COLUMN, false)) {
					cellInColumn.setCellRow(cellInColumn.getCellRow() - 1);
					spreadsheet.updateCellIndex(cellInColumn);
				}
				int columnIndex = cellInRow.getCellColumn();        
				spreadsheet.clearCellIndex(rowIndex, columnIndex);
			}
			
			spreadsheet.deleteRow(row);
			
			transaction.success();
		}
		catch (Exception e) {
			transaction.failure();
			SplashPlugin.error(null, e);
			return false;
		}
		finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
		
		return true;
	}

	/**
	 * Insert column
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param columnIndex row index (begin index: 0)
	 */
	public void insertColumn(SpreadsheetNode spreadsheet, int columnIndex) {
		Transaction transaction = neoService.beginTx();
		
		try {
			//update column index to use in HilbertIndexes
			columnIndex = columnIndex + 1;
			ColumnHeaderNode column = spreadsheet.getColumnHeader(columnIndex);
	    
			for (CellNode cellInColumn : column.getAllCellsFromThis(true)) {
				for (CellNode cellInRow : cellInColumn.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_ROW, true)) {
					cellInRow.setCellColumn(cellInRow.getCellColumn() + 1);
					spreadsheet.updateCellIndex(cellInRow);
				}
				int rowIndex = cellInColumn.getCellRow();	        
				spreadsheet.clearCellIndex(rowIndex, columnIndex);
			}
			transaction.success();
		}
		catch (Exception e) {
			transaction.failure();
		}
		finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
	}

	/**
	 * Deleting column
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index column index (begin index: 0)
	 * @return true if all ok.
	 */
	public boolean deleteColumn(SpreadsheetNode spreadsheet, int columnIndex) {
		Transaction transaction = neoService.beginTx();
		
		try {
			//update column index to use in HilbertIndexes
			columnIndex = columnIndex + 1;
			ColumnHeaderNode column = spreadsheet.getColumnHeader(columnIndex);
	    
			for (CellNode cellInColumn : column.getAllCellsFromThis(true)) {
				for (CellNode cellInRow : cellInColumn.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_ROW, true)) {
					cellInRow.setCellColumn(cellInRow.getCellColumn() - 1);
					spreadsheet.updateCellIndex(cellInRow);
				}
				int rowIndex = cellInColumn.getCellRow();	        
				spreadsheet.clearCellIndex(rowIndex, columnIndex);
			}
			
			spreadsheet.deleteColumn(column);
			
			transaction.success();
		}
		catch (Exception e) {
			transaction.failure();
			SplashPlugin.error(null, e);
			return false;
		}
		finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
		
		return true;

	}

	/**
	 * Swap rows in database
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index1 row1 index
	 * @param index2 row2 index
	 */
	public void swapRows(SpreadsheetNode spreadsheet, int index1, int index2) {
		Transaction transaction = neoService.beginTx();
		try {
			//update indexes to use in Hilbert Indexes
			index1++;
			index2++;
			
			RowHeaderNode row1 = spreadsheet.getRowHeader(index1);
			RowHeaderNode row2 = spreadsheet.getRowHeader(index2);
			
			//swap row header
			if (row1 != null) {
				row1.setIndex(index2);
				spreadsheet.updateCellIndex(row1);
			
				//swap cells
				for (CellNode cellInRow : row1.getAllCellsFromThis(false)) {
					if (row2 == null) {
						spreadsheet.clearCellIndex(cellInRow.getCellRow(), cellInRow.getCellColumn());
					}
					cellInRow.setCellRow(index2);
					spreadsheet.updateCellIndex(cellInRow);
				}
			}
			else {
				spreadsheet.clearCellIndex(index2, 0);
			}
			
			if (row2 != null ) {
				//if second row exists than swap row header 
				row2.setIndex(index1);
				spreadsheet.updateCellIndex(row2);
			
				//swap row cells
				for (CellNode cellInRow : row2.getAllCellsFromThis(false)) {
					if (row1 == null) {
						spreadsheet.clearCellIndex(cellInRow.getCellRow(), cellInRow.getCellColumn());
					}
					cellInRow.setCellRow(index1);
					spreadsheet.updateCellIndex(cellInRow);
				}
			
				transaction.success();
			}
			else {
				spreadsheet.clearCellIndex(index1, 0);
			}
			
			spreadsheet.swapRows(row1);
		}
		catch (Exception e) {
			transaction.failure();
			SplashPlugin.error(null, e);
		} finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
	}	

	/**
	 * Swap columns in database
	 * 
	 * @param spreadsheet spreadsheet node
	 * @param index1 column1 index
	 * @param index2 column2 index
	 */
	public void swapColumns(SpreadsheetNode spreadsheet, int index1, int index2) {
		Transaction transaction = neoService.beginTx();
		try {
			//update indexes to use in Hilbert Indexes
			index1++;
			index2++;
			
			ColumnHeaderNode column1 = spreadsheet.getColumnHeader(index1);
			ColumnHeaderNode column2 = spreadsheet.getColumnHeader(index2);
			
			//swap row header
			if (column1 != null) {
				column1.setIndex(index2);
				spreadsheet.updateCellIndex(column1);
			
				//swap cells
				for (CellNode cellInRow : column1.getAllCellsFromThis(false)) {
					if (column2 == null) {
						spreadsheet.clearCellIndex(cellInRow.getCellRow(), cellInRow.getCellColumn());
					}
					cellInRow.setCellColumn(index2);
					spreadsheet.updateCellIndex(cellInRow);
				}
			}
			else {
				spreadsheet.clearCellIndex(0, index2);
			}
			
			if (column2 != null ) {
				//if second row exists than swap row header 
				column2.setIndex(index1);
				spreadsheet.updateCellIndex(column2);
			
				//swap row cells
				for (CellNode cellInRow : column2.getAllCellsFromThis(false)) {
					if (column1 == null) {
						spreadsheet.clearCellIndex(cellInRow.getCellRow(), cellInRow.getCellColumn());
					}
					cellInRow.setCellColumn(index1);
					spreadsheet.updateCellIndex(cellInRow);
				}
			
				transaction.success();
			}
			else {
				spreadsheet.clearCellIndex(0, index1);
			}
			
			spreadsheet.swapColumns(column1);			
		}
		catch (Exception e) {
			transaction.failure();
			e.printStackTrace();
		} finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProvider.getProvider().commit();
		}
	}
	
	private String updatingFormula(String formula, int rowIndex, int columnIndex, int newRowIndex, int newColumnIndex) {
		String oldCellId = new CellID(rowIndex - 1, columnIndex - 1).getFullID().toLowerCase();
		String newCellId = new CellID(newRowIndex - 1, newColumnIndex - 1).getFullID().toLowerCase();
		String prefix = oldCellId + " = 0\n";
		
		if (formula == null) {
			return null;
		}
		
		if (!formula.contains("=")) {
			return null;
		}
		
		String formulaToEdit = prefix + formula.substring(formula.indexOf("=") + 1);
		
		StringDocumentProvider provider = new StringDocumentProvider("Cell script", formulaToEdit);
		LocalVariableRenamer renamer = new LocalVariableRenamer(provider, oldCellId, newCellId);
		DocumentProvider result = renamer.rename();
		String newFormula = result.getActiveFileContent();
		return "=" + newFormula.substring(newFormula.indexOf("\n") + 1);
	}
}
