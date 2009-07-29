package org.amanzi.splash.neo4j.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseException;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseExceptionMessages;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
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
     * Index property of Row 
     */
    
    public static final String ROW_INDEX = "row_index";

    /*
     * Type of this Node
     */
    private static final String ROW_NODE_TYPE = "Spreadsheet_Row";
    
    /*
     * Name of this Node
     */
    private static final String ROW_NODE_NAME = "Spreadsheet Row";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public RowNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, ROW_NODE_TYPE);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, ROW_NODE_NAME);
    }
    
    /**
     * Returns Index of Row
     *
     * @return index of Row
     */
    
    public String getRowIndex() {
        return (String)getParameter(ROW_INDEX);
    }
    
    /**
     * Sets index of Row
     *
     * @param newRowIndex index of Row
     */    
    public void setRowIndex(String newRowIndex) {
        setParameter(ROW_INDEX, newRowIndex);
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
                                                      return column.getProperty(ColumnNode.COLUMN_NAME).equals(columnName);
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
}
