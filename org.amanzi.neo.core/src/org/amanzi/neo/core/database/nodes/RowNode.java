package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Wrapper of Spreadsheet Row
 * 
 * @author Lagutko_N
 */

public class RowNode extends AbstractNode {
    
    /*
     * Type of this Node
     */
    private static final String ROW_NODE_TYPE = "spreadsheet_row";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public RowNode(Node node, String row) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, ROW_NODE_TYPE);
        setRowIndex(row);
    }

    /**
     * Constructor for wrapping existing row nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private RowNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(ROW_NODE_TYPE)) throw new RuntimeException("Expected existing Splash Row Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing row project
     * @return RowNode from existing Node
     */
    public static RowNode fromNode(Node node) {
        return new RowNode(node);
    }

    /**
     * Returns Index of Row
     *
     * @return index of Row
     */
    
    public String getRowIndex() {
        return (String)getParameter(INeoConstants.PROPERTY_NAME_NAME);
    }
    
    /**
     * Sets index of Row
     *
     * @param newRowIndex index of Row
     */    
    public void setRowIndex(String newRowIndex) {
        setParameter(INeoConstants.PROPERTY_NAME_NAME, newRowIndex);
    }
    
    /**
     * Add Cell to Row
     *
     * @param cell cell
     */
    public void addCell(CellNode cell) {
        addRelationship(SplashRelationshipTypes.ROW_CELL, cell.getUnderlyingNode());
    }
    
    /**
     * Returns the Cell of this Row by given Column
     *
     * @param columnName name of Column
     * @return cell in column
     * @throws SplashDatabaseException if more than one cell in this column
     */
    public CellNode getCellByColumn(String columnName) throws SplashDatabaseException {
        Iterator<CellNode> iterator = new CellIterator(columnName);
        
        if (iterator.hasNext()) {
            CellNode result = iterator.next();
            if (iterator.hasNext()) {
                String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Not_Single_Cell_by_ID, columnName + getRowIndex());
                throw new SplashDatabaseException(message);
            }
            
            return result;
        }
        
        return null;
    }
    
    /**
     * Iterator for searching Cells of this Row by given column
     * 
     * @author Lagutko_N 
     */
    
    private class CellIterator extends AbstractIterator<CellNode> {
        
        public CellIterator(final String columnName) {
            this.iterator = node.traverse(Order.DEPTH_FIRST,
                                          StopEvaluator.DEPTH_ONE,
                                          new ReturnableEvaluator() {

                                              public boolean isReturnableNode(TraversalPosition position) {
                                                  if (position.depth() == 1) {                                                    
                                                      Node column = position.currentNode().getSingleRelationship(SplashRelationshipTypes.COLUMN_CELL, Direction.INCOMING).getStartNode();
                                                      return column.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(columnName);
                                                  }
                                                  return false;
                                              }                                                
                                            },
                                            SplashRelationshipTypes.ROW_CELL,
                                            Direction.OUTGOING,
                                            SplashRelationshipTypes.COLUMN_CELL,
                                            Direction.INCOMING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {            
            return new CellNode(node);
        }
    }
    
    /**
     * Returns number of Cells in this Column
     *
     * @return number of Cells
     */
    public int getCellCount() {        
        Iterator<Relationship> iterator = node.getRelationships(SplashRelationshipTypes.ROW_CELL, Direction.OUTGOING).iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
        }        
        return result;
    }
    
    /**
     * Returns Iterator with all Cells of this Row 
     *
     * @return all Cells of this Row
     */    
    public Iterator<CellNode> getAllCells() {
        return new AllCellsIterator();
    }
    
    /**
     * Iterator for searching all Cells in this Row
     * 
     * @author Lagutko_N 
     */
    
    private class AllCellsIterator extends AbstractIterator<CellNode> {
        
        public AllCellsIterator() {
            this.iterator = node.traverse(Order.BREADTH_FIRST,
                                          StopEvaluator.DEPTH_ONE,
                                          ReturnableEvaluator.ALL_BUT_START_NODE,
                                          SplashRelationshipTypes.ROW_CELL,
                                          Direction.OUTGOING,
                                          SplashRelationshipTypes.COLUMN_CELL,
                                          Direction.INCOMING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {            
            return new CellNode(node);
        }
    }
}
