package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;

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
}
