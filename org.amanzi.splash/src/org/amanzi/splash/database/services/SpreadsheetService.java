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

import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.SplashRelationshipTypes;
import org.amanzi.neo.services.nodes.CellID;
import org.amanzi.neo.services.nodes.CellNode;
import org.amanzi.neo.services.nodes.ChartItemNode;
import org.amanzi.neo.services.nodes.ChartNode;
import org.amanzi.neo.services.nodes.ColumnHeaderNode;
import org.amanzi.neo.services.nodes.LoopInCellReferencesException;
import org.amanzi.neo.services.nodes.PieChartItemNode;
import org.amanzi.neo.services.nodes.PieChartNode;
import org.amanzi.neo.services.nodes.RowHeaderNode;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.amanzi.neo.services.nodes.SplashDatabaseException;
import org.amanzi.neo.services.nodes.SplashDatabaseExceptionMessages;
import org.amanzi.neo.services.nodes.SplashFormatNode;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

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
	private final NeoServiceProviderUi provider;


	/*
	 * Project Service
	 */
	protected AweProjectService projectService;

	private SplashFormatNode defaultSFNode;

    private GraphDatabaseService graphDatabaseService;
	
	/**
	 * Constructor of Service.
	 * 
	 * Initializes NeoService and create a Root Element
	 */
	public SpreadsheetService() {

        provider = NeoServiceProviderUi.getProvider();
        graphDatabaseService = provider.getService();
        Transaction tx = graphDatabaseService.beginTx();
        try {
            projectService = NeoServiceFactory.getInstance().getProjectService();
            defaultSFNode = new SplashFormatNode(graphDatabaseService.createNode());
            setSplashFormat(defaultSFNode, new CellFormat());
        } finally {
            tx.finish();
        }
		
	}
	
	/**
     * Constructor of Service.
     * 
     * Initializes NeoService and create a Root Element
     */
    public SpreadsheetService(GraphDatabaseService neo){

        provider = NeoServiceProviderUi.getProvider();
        graphDatabaseService = neo;
        Transaction tx = graphDatabaseService.beginTx();
        try {
            projectService = NeoServiceFactory.getInstance().getProjectService();
            defaultSFNode = new SplashFormatNode(graphDatabaseService.createNode());
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
			Transaction transaction = graphDatabaseService.beginTx();

			try {
				SpreadsheetNode spreadsheet = new SpreadsheetNode(graphDatabaseService.createNode(),name,graphDatabaseService);

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
	@Deprecated
    public ChartNode createChart(SpreadsheetNode spreadsheet, String id) {
		Transaction transaction = graphDatabaseService.beginTx();

		try {
			ChartNode chartNode = spreadsheet.getChart(id);

			if (chartNode == null) {
				chartNode = new ChartNode(graphDatabaseService.createNode());
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
		Transaction transaction = graphDatabaseService.beginTx();

		try {
			PieChartNode chartNode = spreadsheet.getPieChart(id);

			if (chartNode == null) {
				chartNode = new PieChartNode(graphDatabaseService.createNode());
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
	@Deprecated
    public ChartItemNode createChartItem(ChartNode chartNode, Integer id) throws SplashDatabaseException {
		Transaction transaction = graphDatabaseService.beginTx();

		try {
			ChartItemNode ChartItemNode = chartNode.getChartItem(id);

			if (ChartItemNode == null) {
				ChartItemNode = new ChartItemNode(graphDatabaseService.createNode());
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
		Transaction transaction = graphDatabaseService.beginTx();

		try {
			PieChartItemNode ChartItemNode = chartNode.getPieChartItem(id);

			if (ChartItemNode == null) {
				ChartItemNode = new PieChartItemNode(graphDatabaseService.createNode());
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
	    Transaction transaction = graphDatabaseService.beginTx();

		try {
			CellNode cell = new CellNode(graphDatabaseService.createNode());
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

		Transaction transaction = graphDatabaseService.beginTx();

		try {
			node.setValue(cell.getValue());
			node.setDefinition(cell.getDefinition());

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

			            SplashFormatNode newSFNode = new SplashFormatNode(graphDatabaseService.createNode());

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
	    	Transaction transaction = graphDatabaseService.beginTx();
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
	    Transaction transaction = graphDatabaseService.beginTx();

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
		CellNode lastCell;
		Transaction tx = graphDatabaseService.beginTx();
        try {
    		if (cell != null) {
    			// check if there are cells that are dependent on this cell
    			if (cell.getDependedNodes().hasNext()) {
    				// we can't delete Cell on which other Cell depends
    				return false;
    			}
    			lastCell = cell;
    		}else{
    		    RowHeaderNode rowHeader = sheet.getRowHeader(row);
                if(rowHeader==null){
    		        return true; // not need to shift cells if row is empty
    		    }
    		    lastCell = rowHeader;
    		}
    		while (lastCell.getNextCellInRow()!=null) {
                lastCell = lastCell.getNextCellInRow();
            }
    		int lastCol = lastCell.getCellColumn()-1;
            int currCol = column;
            if(lastCol<currCol){
                return true;
            }
            while(lastCol>currCol){
                int nextCol = currCol+1;
                CellNode next = getCellNode(sheet, row, nextCol);
                if(next==null){
                    if(cell!=null){
                        sheet.updateCellColumn(cell,nextCol);
                    }
                    cell = next;
                }
                else{
                    if(cell==null){
                        sheet.updateCellColumn(next,currCol);
                    }
                    else{
                        cell.setValue(next.getValue());
                        cell = next;
                    }                    
                }                
                currCol++;
                //TODO update formula
            }
            if (cell!=null) {
                sheet.deleteCell(cell);
            }else{
                sheet.clearCellIndex(row+1, currCol+1);
            }
            tx.success();
        } finally {
            tx.finish();
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

		Transaction transaction = graphDatabaseService.beginTx();
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
		Transaction transaction = graphDatabaseService.beginTx();
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
        Transaction transaction = graphDatabaseService.beginTx();
        
        try {
            // update column index to use in HilbertIndexes
            RowHeaderNode row = spreadsheet.getRowHeaderMoreEquals(rowIndex+1);
            CellNode lastNode = row;
            if(row == null){
                ColumnHeaderNode zeroRow = spreadsheet.getColumnHeader(0);
                if(zeroRow == null){
                    return;
                }
                lastNode = zeroRow;
            }
            while(lastNode.getNextCellInColumn()!=null){
                lastNode = lastNode.getNextCellInColumn();
            }
            int lastRowInd = lastNode.getCellRow()-1;
            if(lastRowInd<rowIndex){
                return;
            }
            int currRowInd = lastRowInd;
            while(currRowInd>=rowIndex){
                swapRows(spreadsheet, currRowInd, currRowInd+1);
                currRowInd--;
            }
          //TODO update formula
            transaction.success();
        } catch (Exception e) {
            transaction.failure();
            e.printStackTrace();
        } finally {
            transaction.finish();
            // commit changes to database
            NeoServiceProviderUi.getProvider().commit();
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
        Transaction transaction = graphDatabaseService.beginTx();

        try {
            // update column index to use in HilbertIndexes
            rowIndex = rowIndex + 1;
            RowHeaderNode row = spreadsheet.getRowHeader(rowIndex);
            CellNode lastRow = row;
            if (row ==null){
                RowHeaderNode zeroHeader = spreadsheet.getRowHeader(0);
                if(zeroHeader==null){
                    return true;
                }
                lastRow = zeroHeader;                              
            }
            while(lastRow.getNextCellInColumn()!=null){
                lastRow = lastRow.getNextCellInColumn();
            }
            int lastRowInd = lastRow.getCellRow()-1;
            int currRowInd = rowIndex-1;
            if(lastRowInd<currRowInd){
                return true;
            }
            while(lastRowInd>currRowInd){
                int nextRowInd = currRowInd+1;
                swapRows(spreadsheet, currRowInd, nextRowInd);
                row = spreadsheet.getRowHeader(nextRowInd+1);
                currRowInd++;
            }
            if (row!=null) {
                spreadsheet.deleteRow(row);
            }
          //TODO update formula
            transaction.success();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.failure();
			SplashPlugin.error(null, e);
			return false;
		}
		finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProviderUi.getProvider().commit();
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
        Transaction transaction = graphDatabaseService.beginTx();

        try {
            // update column index to use in HilbertIndexes
            ColumnHeaderNode column = spreadsheet.getColumnHeader(columnIndex+1);
            CellNode lastNode = column;
            if(column == null){
                ColumnHeaderNode zeroColumn = spreadsheet.getColumnHeader(0);
                if(zeroColumn == null){
                    return;
                }
                lastNode = zeroColumn;
            }
            while(lastNode.getNextCellInRow()!=null){
                lastNode = lastNode.getNextCellInRow();
            }
            int lastColInd = lastNode.getCellColumn()-1;
            if(lastColInd<columnIndex){
                return;
            }
            int currColInd = lastColInd;
            while(currColInd>=columnIndex){
                swapColumns(spreadsheet, currColInd, currColInd+1);
                currColInd--;
            }
          //TODO update formula
            transaction.success();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.failure();
        } finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProviderUi.getProvider().commit();
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
		Transaction transaction = graphDatabaseService.beginTx();
		
		try {
			//update column index to use in HilbertIndexes
			columnIndex = columnIndex + 1;
			ColumnHeaderNode column = spreadsheet.getColumnHeader(columnIndex);
			CellNode lastColumn = column;
			if(column == null){
			    ColumnHeaderNode zeroNode = spreadsheet.getColumnHeader(0);
			    if(zeroNode==null){
			        return true;
			    }
			    lastColumn = zeroNode;
			}
			while(lastColumn.getNextCellInRow()!=null){
			    lastColumn = lastColumn.getNextCellInRow();
			}
			int lastColInd = lastColumn.getCellColumn()-1;
			int currColInd = columnIndex-1;
			if(lastColInd<currColInd){
			    return true;
			}
            while(lastColInd>currColInd){
                int nextColInd = currColInd+1;
                swapColumns(spreadsheet, currColInd, nextColInd);
                column = spreadsheet.getColumnHeader(nextColInd+1);
                currColInd++;
            }			
			if (column!=null) {
                spreadsheet.deleteColumn(column);
            }
			//TODO update formula
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
			NeoServiceProviderUi.getProvider().commit();
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
		Transaction transaction = graphDatabaseService.beginTx();
		try {
			//update indexes to use in Hilbert Indexes
			index1++;
			index2++;
			
			RowHeaderNode row1 = spreadsheet.getRowHeader(index1);
			RowHeaderNode row2 = spreadsheet.getRowHeader(index2);
			
			if(row1==null&&row2==null){
			    return;
			}
			if(row1==null||row2==null){
			    RowHeaderNode movedRow = row1==null?row2:row1;
			    int newIndex = (row1==null?index1:index2)-1;
			    for (CellNode cellInRow : movedRow.getAllCellsFromThis(false)) {
                    spreadsheet.updateCellRow(cellInRow, newIndex);
                }
			    spreadsheet.deleteCell(movedRow);
			    return;
			}
			int currColumn=0;
			boolean hasNext1 = true;
			boolean hasNext2 = true;
			while(hasNext1||hasNext2){
			    CellNode cell1 = spreadsheet.getCell(index1-1, currColumn);
			    CellNode cell2 = spreadsheet.getCell(index2-1, currColumn);
			    if(cell1 == null){
			        if(cell2!=null){
			            hasNext2 = cell2.getNextCellInRow()!=null;
			            spreadsheet.updateCellRow(cell2, index1-1);			            
			        }
			    }else{
			        hasNext1 = cell1.getNextCellInRow()!=null;
			        if(cell2 == null){
			            spreadsheet.updateCellRow(cell1,index2-1);
			        }else{
			            hasNext2 = cell2.getNextCellInRow()!=null;
			            Object value = cell1.getValue();
			            cell1.setValue(cell2.getValue());
			            cell2.setValue(value);			            
			        }			        
			    }
			    currColumn++;
			}
		}
		catch (Exception e) {
			transaction.failure();
			SplashPlugin.error(null, e);
		} finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProviderUi.getProvider().commit();
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
		Transaction transaction = graphDatabaseService.beginTx();
		try {
			//update indexes to use in Hilbert Indexes
			index1++;
			index2++;
			
			ColumnHeaderNode column1 = spreadsheet.getColumnHeader(index1);
			ColumnHeaderNode column2 = spreadsheet.getColumnHeader(index2);
			
			if(column1==null&&column2==null){
                return;
            }
            if(column1==null||column2==null){
                ColumnHeaderNode movedColumn = column1==null?column2:column1;
                int newIndex = (column1==null?index1:index2)-1;
                for (CellNode cellInRow : movedColumn.getAllCellsFromThis(false)) {
                    spreadsheet.updateCellColumn(cellInRow, newIndex);
                }
                spreadsheet.deleteCell(movedColumn);
                return;
            }
            int currRow=0;
            boolean hasNext1 = true;
            boolean hasNext2 = true;
            while(hasNext1||hasNext2){
                CellNode cell1 = spreadsheet.getCell(currRow,index1-1);
                CellNode cell2 = spreadsheet.getCell(currRow,index2-1);
                if(cell1 == null){
                    if(cell2!=null){
                        hasNext2 = cell2.getNextCellInColumn()!=null;
                        spreadsheet.updateCellColumn(cell2, index1-1);                     
                    }
                }else{
                    hasNext1 = cell1.getNextCellInColumn()!=null;
                    if(cell2 == null){
                        spreadsheet.updateCellColumn(cell1,index2-1);
                    }else{
                        hasNext2 = cell2.getNextCellInColumn()!=null;
                        Object value = cell1.getValue();
                        cell1.setValue(cell2.getValue());
                        cell2.setValue(value);                      
                    }                   
                }
                currRow++;
            }	
		}
		catch (Exception e) {
			transaction.failure();
			e.printStackTrace();
		} finally {
			transaction.finish();
			//commit changes to database
			NeoServiceProviderUi.getProvider().commit();
		}
	}
}
