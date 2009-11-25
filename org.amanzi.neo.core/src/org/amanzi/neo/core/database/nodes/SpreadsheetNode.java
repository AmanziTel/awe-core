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
	 * Index of Cells 
	 */
	private HilbertIndex index;
	
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
	
	private void addToColumn(CellNode cell) {
        int column = cell.getCellColumn();
        
        ColumnHeaderNode columnHeader = getColumnHeader(column);
        if (columnHeader == null) {
            columnHeader = new ColumnHeaderNode(neoService.createNode(), column);
            index.addNode(columnHeader.getUnderlyingNode());
        }
        
        columnHeader.addNextCell(cell);
    }
	
	private void addToRow(CellNode cell) {
        int row = cell.getCellRow();
        
        RowHeaderNode rowHeader = getRowHeader(row);
        if (rowHeader == null) {
            rowHeader = new RowHeaderNode(neoService.createNode(), row);
            index.addNode(rowHeader.getUnderlyingNode());
        }
        
        rowHeader.addNextCell(cell);
    }
}
