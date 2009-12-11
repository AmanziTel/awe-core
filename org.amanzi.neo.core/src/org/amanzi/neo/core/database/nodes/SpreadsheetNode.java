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
package org.amanzi.neo.core.database.nodes;

import java.util.ArrayList;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.index.hilbert.HilbertIndex;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * Wrapper class for Spreadsheet
 * 
 * @author Lagutko_N
 */

public class SpreadsheetNode extends AbstractNode {

	/*
	 * Type of this Node
	 */
	private static final String SPREADSHEET_NODE_TYPE = "spreadsheet";
	
	/*
	 * Name of Index for cells
	 */
	public static final String CELL_INDEX = "cell_index";
	
	/*
	 * Name of property 'Has child spreadsheets'
	 */
	private static final String HAS_CHILD_SPREADSHEETS = "has_child_spreadsheets"; 
	
	/*
	 * Index of Cells 
	 */
	private HilbertIndex index;
	
	/*
	 * Neo Service
	 */
	private NeoService neoService = NeoServiceProvider.getProvider().getService();
	
	/**
	 * Constructor. Wraps a Node from database and sets type and name of Node
	 * 
	 * @param node
	 *            database node
	 */
	public SpreadsheetNode(Node node, String name) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, SPREADSHEET_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, name);
		
		index = new HilbertIndex(CELL_INDEX, 3, CellNode.CELL_COLUMN, CellNode.CELL_ROW);
		index.initialize(node);
	}

	/**
     * Constructor for wrapping existing Spreadsheet nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    protected SpreadsheetNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(SPREADSHEET_NODE_TYPE)) throw new RuntimeException("Expected existing Spreadsheet Node, but got "+node.toString());
        
        index = new HilbertIndex(CELL_INDEX, 3, CellNode.CELL_COLUMN, CellNode.CELL_ROW);
        index.initialize(node);
    }

    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Spreadsheet
     * @return SpreadsheetNode from existing Node
     */
    public static SpreadsheetNode fromNode(Node node) {
        return new SpreadsheetNode(node);
    }

	/**
	 * Sets name of Spreadsheet
	 * 
	 * @param newName
	 *            name of Spreadsheet
	 */

	public void setSpreadsheetName(String newName) {
		setParameter(INeoConstants.PROPERTY_NAME_NAME, newName);
	}

	/**
	 * Returns name of Spreadsheet
	 * 
	 * @return name of Spreadsheet
	 */
	public String getSpreadsheetName() {
		return (String) getParameter(INeoConstants.PROPERTY_NAME_NAME);
	}

	/**
	 * Adds chart to Spreadsheet
	 * 
	 * @param chart
	 * @deprecated
	 */
	public void addChart(ChartNode chart) {
		addRelationship(SplashRelationshipTypes.CHART, chart
				.getUnderlyingNode());
	}

	/**
	 * Add pie chart to Spreadsheet
	 * 
	 * @param chart
	 */
	public void addPieChart(PieChartNode chart) {
		addRelationship(SplashRelationshipTypes.PIE_CHART, chart
				.getUnderlyingNode());
	}

	/**
	 * Returns a Chart by given index
	 * 
	 * @param chartIndex
	 *            index of chart
	 * @return chart by index
	 * @throws SplashDatabaseException
	 *             if was founded more than one row by given index
	 */
	public ChartNode getChart(final String chartIndex)
			throws SplashDatabaseException {
		Iterator<ChartNode> iterator = new ChartIterator(chartIndex);

		if (iterator.hasNext()) {
			ChartNode result = iterator.next();
			if (iterator.hasNext()) {
				String message = SplashDatabaseExceptionMessages
						.getFormattedString(
								SplashDatabaseExceptionMessages.Not_Single_Chart_by_ID,
								chartIndex);
				throw new SplashDatabaseException(message);
			}
			return result;
		}

		return null;
	}

	/**
	 * Returns a Pie Chart by given index
	 * 
	 * @param chartIndex
	 *            index of chart
	 * @return chart by index
	 * @throws SplashDatabaseException
	 *             if was founded more than one row by given index
	 */
	public PieChartNode getPieChart(final String chartIndex)
			throws SplashDatabaseException {
		Iterator<PieChartNode> iterator = new PieChartIterator(chartIndex);

		if (iterator.hasNext()) {
			PieChartNode result = iterator.next();
			if (iterator.hasNext()) {
				String message = SplashDatabaseExceptionMessages
						.getFormattedString(
								SplashDatabaseExceptionMessages.Not_Single_Chart_by_ID,
								chartIndex);
				throw new SplashDatabaseException(message);
			}
			return result;
		}

		return null;
	}

	public int getChartsCount() {
		Iterator<Relationship> iterator = node.getRelationships(
				SplashRelationshipTypes.CHART, Direction.OUTGOING).iterator();
		int result = 0;
		while (iterator.hasNext()) {
			result++;
		}
		return result;
	}

	public int getPieChartsCount() {
		Iterator<Relationship> iterator = node.getRelationships(
				SplashRelationshipTypes.PIE_CHART, Direction.OUTGOING)
				.iterator();
		int result = 0;
		while (iterator.hasNext()) {
			result++;
		}
		return result;
	}

	/**
	 * Returns a Cell by Column and Row
	 * 
	 * @param rowIndex
	 *            index of Row
	 * @param columnIndex
	 *            name of Column
	 * @return cell by Column and Row
	 * @throws SplashDatabaseException
	 *             if was founded more than one row, or more than one column, or
	 *             more than one cell
	 */
	public CellNode getCell(int rowIndex, int columnIndex) {
	    
	    Node node = index.find(columnIndex + 1, rowIndex + 1);
	    
	    if (node != null) {
	        return CellNode.fromNode(node);
	    }
	    else {
	        return null;
	    }
	}
	
	public ColumnHeaderNode getColumnHeader(int columnIndex) {
	    Node node = index.find(columnIndex, 0);
	    
	    if (node != null) {
	        return new ColumnHeaderNode(node);
	    }
	    else {
	        return null;
	    }
	}
	
	public RowHeaderNode getRowHeader(int rowIndex) {
        Node node = index.find(0, rowIndex);
        
        if (node != null) {
            return new RowHeaderNode(node);
        }
        else {
            return null;
        }
    }

	/**
	 * Iterator that computes Charts by given Index
	 * 
	 * @author amabdelsalam
	 */
	private class ChartIterator extends AbstractIterator<ChartNode> {

		public ChartIterator(final String chartIndex) {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

						public boolean isReturnableNode(
								TraversalPosition position) {
							if (position.isStartNode()) {
								return false;
							}
							return position.lastRelationshipTraversed()
									.getEndNode().getProperty(
											ChartNode.CHART_INDEX).equals(
											chartIndex);
						}

					}, SplashRelationshipTypes.CHART, Direction.OUTGOING)
					.iterator();
		}

		@Override
		protected ChartNode wrapNode(Node node) {
			return new ChartNode(node);
		}
	}

	/**
	 * Iterator that computes Pie Charts by given Index
	 * 
	 * @author amabdelsalam
	 */
	private class PieChartIterator extends AbstractIterator<PieChartNode> {

		public PieChartIterator(final String chartIndex) {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
					StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

						public boolean isReturnableNode(
								TraversalPosition position) {
							if (position.isStartNode()) {
								return false;
							}
							return position.lastRelationshipTraversed()
									.getEndNode().getProperty(
											PieChartNode.PIE_CHART_INDEX)
									.equals(chartIndex);
						}

					}, SplashRelationshipTypes.PIE_CHART, Direction.OUTGOING)
					.iterator();
		}

		@Override
		protected PieChartNode wrapNode(Node node) {
			return new PieChartNode(node);
		}
	}

	public ChartNode getChartNode(String id) {
		ChartNode chart = null;
		try {
			chart = getChart(id);
		} catch (SplashDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chart;
	}

	public PieChartNode getPieChartNode(String id) {
		PieChartNode chart = null;
		try {
			chart = getPieChart(id);
		} catch (SplashDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chart;
	}

	/**
	 * Returns a RubyProjectNode of this Spreadsheet
	 *
	 * @return RubyProjectNode of Spreadsheet
	 */
	public RubyProjectNode getSpreadsheetRootProject() {
	    Relationship relationship = getUnderlyingNode().getSingleRelationship(SplashRelationshipTypes.SPREADSHEET, Direction.INCOMING);
	    return RubyProjectNode.fromNode(relationship.getStartNode());
	}
	
	/**
	 * Adds Cell to Spreadsheet
	 *
	 * @param cell cell
	 */
	public void addCell(CellNode cell) {
	    index.addNode(cell.getUnderlyingNode());
	    
	    addToColumn(cell);
	    addToRow(cell);   
	    
	    index.finishUp();
	}
	
	/**
	 * Removes index by coordinates 
	 * 
	 * @param row row of Cell
	 * @param column column of Cell
	 */
	public void clearCellIndex(int row, int column) {
		index.remove(column, row);
	}
	
	/**
	 * Updates Index for Cell
	 *  
	 * @param cell cell to update
	 */
	public void updateCellIndex(CellNode cell) {
		index.addNode(cell.getUnderlyingNode());
		index.finishUp();
	}
	
	/**
	 * Adds a Cell to Column
	 *
	 * @param cell cell to add
	 */
	private void addToColumn(CellNode cell) {
        int column = cell.getCellColumn();
        
        ColumnHeaderNode columnHeader = getColumnHeader(column);
        if (columnHeader == null) {
            columnHeader = new ColumnHeaderNode(neoService.createNode(), column);
            index.addNode(columnHeader.getUnderlyingNode());
            index.finishUp();
            addToRow(columnHeader);
        }
        
        columnHeader.addNextCell(cell);
    }
	
	/**
	 * Adds a Cell to Row
	 *
	 * @param cell cell to add
	 */
	private void addToRow(CellNode cell) {
        int row = cell.getCellRow();
        
        RowHeaderNode rowHeader = getRowHeader(row);
        if (rowHeader == null) {
            rowHeader = new RowHeaderNode(neoService.createNode(), row);
            index.addNode(rowHeader.getUnderlyingNode());
            index.finishUp();
            addToColumn(rowHeader);
        }
        
        rowHeader.addNextCell(cell);
    }
	
	/**
	 * Sets is this Spreadsheet have child Spreadsheets
	 * 
	 * @param hasChildSpreadsheets is this Spreadsheet have child sheets
	 */
	public void setHasChildSpreadsheets(boolean hasChildSpreadsheets) {
	    setParameter(HAS_CHILD_SPREADSHEETS, hasChildSpreadsheets);
	}
	
	/**
	 * Returns is this Spreadsheet have child sheets
	 * 
	 * @return is this spreadsheet have child sheets
	 */
	public boolean hasChildSpreadsheets() {
	    Boolean result = (Boolean)getParameter(HAS_CHILD_SPREADSHEETS);
	    if (result == null) {
	        result = false;
	    }
	    return result;
	}
	
	/**
	 * Adds a reference to child Spreadsheet
	 * 
	 * @param childSpreadsheet child Spreadsheet
	 */
	public void addChildSpreadsheet(SpreadsheetNode childSpreadsheet) {
	    addRelationship(SplashRelationshipTypes.CHILD_SPREADSHEET, childSpreadsheet.getUnderlyingNode());
	}
	
	/**
	 * Returns all child Spreadsheet
	 * 
	 * @return child Spreadsheet
	 */
	public ArrayList<SpreadsheetNode> getAllChildSpreadsheets() {
	    ChildSpreadsheetsIterator spreadsheetIterator = new ChildSpreadsheetsIterator(null);
	    
	    ArrayList<SpreadsheetNode> result = new ArrayList<SpreadsheetNode>();
	    while (spreadsheetIterator.hasNext()) {
	        result.add(spreadsheetIterator.next());
	    }
	    
	    return result;
	}
	
	/**
	 * Returns Child Spreadsheet by it's name
	 *
	 * @param spreadsheetName
	 * @return
	 */
	public SpreadsheetNode getChildSpreadsheet(String spreadsheetName) {
	    ChildSpreadsheetsIterator spreadsheetsIterator = new ChildSpreadsheetsIterator(spreadsheetName);
	    
	    if (spreadsheetsIterator.hasNext()) {
	        return spreadsheetsIterator.next();
	    }
	    else {
	        return null;
	    }
	}
	
	/**
	 * Iterator for child Spreadsheets
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	private class ChildSpreadsheetsIterator extends AbstractIterator<SpreadsheetNode> {
	    
	    /*
	     * Default StopEvaluator
	     */
	    private StopEvaluator allSpreadsheets = StopEvaluator.END_OF_GRAPH;
	    
	    /*
	     * Is current node returnable?
	     */
	    private boolean isReturnableNode = false;
	    
	    /*
	     * Name of Spreadsheet to search
	     */
	    private String spreadsheetName; 
	    
	    /*
	     * StopEvaluator for searching spreadsheet by name
	     */
	    private StopEvaluator spreadsheetByName = new StopEvaluator() {
            
            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                isReturnableNode = currentPos.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(spreadsheetName);
                return isReturnableNode;
            }
        };
	    
        /**
         * Constructor
         * 
         * @param spreadsheetName name of Spreadsheet to search, if null than iterate through all spreadsheets
         */
	    public ChildSpreadsheetsIterator(String spreadsheetName) {
	        StopEvaluator currentEvaluator = null;
	        if (spreadsheetName == null) {
	            currentEvaluator = allSpreadsheets; 
	        }
	        else {
	            this.spreadsheetName = spreadsheetName;
	            currentEvaluator = spreadsheetByName;
	        }
	        
	        this.iterator = node.traverse(Order.BREADTH_FIRST, 
	                                      currentEvaluator,
	                                      new ReturnableEvaluator(){
                                            
                                            @Override
                                            public boolean isReturnableNode(TraversalPosition currentPos) {
                                                if (!isReturnableNode && (currentPos.depth() == 0)) {
                                                    return false;
                                                }
                                                return true;
                                            }
                                           },
                                           SplashRelationshipTypes.CHILD_SPREADSHEET,
                                           Direction.OUTGOING).iterator();
	    }

        @Override
        protected SpreadsheetNode wrapNode(Node node) {
            return SpreadsheetNode.fromNode(node);
        }
	    
	}
}
