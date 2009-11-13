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
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Wrapper of Spreadsheet Column
 * 
 * @author Lagutko_N
 */

public class ColumnNode extends AbstractNode {

	/*
	 * Type of this Node
	 */
	private static final String COLUMN_NODE_TYPE = "spreadsheet_column";

	/**
	 * Constructor. Wraps a Node from database and sets type and name of Node
	 * 
	 * @param node
	 *            database node
	 */
	public ColumnNode(Node node, String col) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, COLUMN_NODE_TYPE);
		setColumnName(col);
	}

    /**
     * Constructor for wrapping existing column nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private ColumnNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(COLUMN_NODE_TYPE)) throw new RuntimeException("Expected existing Splash Column Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing column project
     * @return ColumnNode from existing Node
     */
    public static ColumnNode fromNode(Node node) {
        return new ColumnNode(node);
    }

	/**
	 * Returns Column name
	 * 
	 * @return name of Column
	 */
	public String getColumnName() {
		return (String) getParameter(INeoConstants.PROPERTY_NAME_NAME);
	}

	/**
	 * Sets the name of Column
	 * 
	 * @param columnName
	 *            name of Column
	 */
	public void setColumnName(String columnName) {
        setParameter(INeoConstants.PROPERTY_NAME_NAME, columnName);
	}

	/**
	 * Adds a Cell to Column
	 * 
	 * @param cell
	 *            Cell
	 */
	public void addCell(CellNode cell) {
		addRelationship(SplashRelationshipTypes.COLUMN_CELL, cell.getUnderlyingNode());
	}

	/**
	 * Returns number of Cells in this Column
	 * 
	 * @return number of Cells
	 */
	public int getCellCount() {
		Iterator<Relationship> iterator = node.getRelationships(SplashRelationshipTypes.COLUMN_CELL, Direction.OUTGOING).iterator();
		int result = 0;
		while (iterator.hasNext()) {
			result++;
		}
		return result;
	}

	/**
	 * Returns Iterator with all Cells of this Column
	 * 
	 * @return all Cells of this Column
	 */
	public Iterator<CellNode> getAllCells() {
		return new AllCellsIterator(node, SplashRelationshipTypes.COLUMN_CELL);
	}

	/**
	 * Iterator for searching all Cells
	 * 
	 * @author Cinkel_A
	 * 
	 */
	private class AllCellsIterator extends AbstractIterator<CellNode> {

		public AllCellsIterator(Node node, SplashRelationshipTypes columnCell) {
			super(node, columnCell);
		}

		@Override
		protected CellNode wrapNode(Node node) {
			return new CellNode(node);
		}

	}
	/**
     * Iterator for searching Cells from the given range
     * 
     * @author Pechko_E 
     */
    
    private class CellRangeIterator extends AbstractIterator<CellNode> {
        
        public CellRangeIterator(String firstRowIndex, String lastRowIndex) {
            final int first = Integer.parseInt(firstRowIndex);
            final int last = Integer.parseInt(lastRowIndex);
            this.iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE,
                                          new ReturnableEvaluator() {

                                              public boolean isReturnableNode(TraversalPosition position) {
                                                  if (position.isStartNode()) {
                                                      return false;
                                                  }
                                                  String propertyName = (String)position.currentNode().getSingleRelationship(SplashRelationshipTypes.ROW_CELL, Direction.INCOMING).getStartNode()
                                                  .getProperty(INeoConstants.PROPERTY_NAME_NAME);
                                                  int ind = Integer.parseInt(propertyName);
                                                  return ind <= last && ind >= first;
                                              }                                                
                                            },
                                            SplashRelationshipTypes.COLUMN_CELL,
                                            Direction.OUTGOING,
                                            SplashRelationshipTypes.ROW_CELL,
                                            Direction.INCOMING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {            
            return new CellNode(node);
        }
    }
    /**
     * Returns cells from the given range
     *
     * @param firstRowName name of the range first row
     * @param lastRowName name of the range last row
     * @return iterator
     */
    public Iterator<CellNode> getCells(String firstRowName,String lastRowName){
        return new CellRangeIterator(firstRowName,lastRowName);
    }
}
