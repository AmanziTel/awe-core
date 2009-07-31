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
import org.neo4j.api.core.Traverser;

/**
 * Wrapper class for Spreadsheet
 * 
 * @author Lagutko_N 
 */

public class SpreadsheetNode extends AbstractNode {
    
    /*
     * Name property of Spreadsheet
     */
    private static final String SPREADSHEET_NAME = "spreadsheet_name";
    
    /*
     * Type of this Node
     */
    private static final String SPREADSHEET_NODE_TYPE = "Spreadsheet";
    
    /*
     * Name of this Node
     */
    private static final String SPREADSHEET_NODE_NAME = "Spreadsheet";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public SpreadsheetNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, SPREADSHEET_NODE_TYPE);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, SPREADSHEET_NODE_NAME);
    }
    
    /**
     * Sets name of Spreadsheet
     *
     * @param newName name of Spreadsheet
     */
    
    public void setSpreadsheetName(String newName) {
        setParameter(SPREADSHEET_NAME, newName);
    }
    
    /**
     * Returns name of Spreadsheet
     *
     * @return name of Spreadsheet
     */
    public String getSpreadsheetName() {
        return (String)getParameter(SPREADSHEET_NAME);
    }
    
    /**
     * Adds a Row to Spreadsheet
     *
     * @param row row wrapper
     */    
    public void addRow(RowNode row) {
        addRelationship(SplashRelationshipTypes.ROW, row.getUnderlyingNode());
    }
    
    /**
     * Returns a Row by given index
     *
     * @param rowIndex index of row
     * @return row by index
     * @throws SplashDatabaseException if was founded more than one row by given index
     */     
    public RowNode getRow(final String rowIndex) throws SplashDatabaseException {
        Iterator<RowNode> iterator = new RowIterator(rowIndex);
        
        if (iterator.hasNext()) {
            RowNode result = iterator.next();
            if (iterator.hasNext()) {
                String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Not_Single_Row_by_ID, rowIndex);
                throw new SplashDatabaseException(message);
            }
            return result;
        }
        
        return null;
    }
    
    /**
     * Returns a Column by given name
     *
     * @param columnName name of Column
     * @return column by name
     * @throws SplashDatabaseException if was founded more than one column by given name
     */
    public ColumnNode getColumn(String columnName) throws SplashDatabaseException {
        Iterator<ColumnNode> iterator = new ColumnInterator(columnName);
        
        if (iterator.hasNext()) {
            ColumnNode result = iterator.next();
            if (iterator.hasNext()) {
                String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Not_Single_Column_by_ID, columnName);
                throw new SplashDatabaseException(message);
            }
            return result;
        }
        
        return null;
    }
    
    /**
     * Returns a Cell by Column and Row
     *
     * @param rowIndex index of Row
     * @param columnName name of Column
     * @return cell by Column and Row
     * @throws SplashDatabaseException if was founded more than one row, or more than one column, or more than one cell
     */
    public CellNode getCell(String rowIndex, String columnName) throws SplashDatabaseException {
        RowNode row = getRow(rowIndex);
        if (row != null) {
            return row.getCellByColumn(columnName);
        }
        else {
            return null;
        }
    }
    
    public Iterator<RowNode> getAllRows() {
        return new AllRowIterator();
    }
    
    /**
     * Iterator that computes Rows by given Index
     * 
     * @author Lagutko_N
     */    
    private class RowIterator extends AbstractIterator<RowNode> {
        
        public RowIterator(final String rowIndex) {            
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
                    StopEvaluator.DEPTH_ONE, 
                    new ReturnableEvaluator() {

                        public boolean isReturnableNode(TraversalPosition position) {
                            if (position.isStartNode()) {
                                return false;
                            }
                            return position.lastRelationshipTraversed().getEndNode().getProperty(RowNode.ROW_INDEX).equals(rowIndex);
                        }
                
                    },
                    SplashRelationshipTypes.ROW,
                    Direction.OUTGOING).iterator();
        }

        @Override
        protected RowNode wrapNode(Node node) {            
            return new RowNode(node);
        }
    }
    
    /**
     * Iterator that computes all Rows in Spreadsheet
     * 
     * @author Lagutko_N
     */    
    private class AllRowIterator extends AbstractIterator<RowNode> {
        
        public AllRowIterator() {            
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
                    StopEvaluator.DEPTH_ONE, 
                    ReturnableEvaluator.ALL_BUT_START_NODE,
                    SplashRelationshipTypes.ROW,
                    Direction.OUTGOING).iterator();
        }

        @Override
        protected RowNode wrapNode(Node node) {            
            return new RowNode(node);
        }
    }
    
    /**
     * Iterator that computes Columns by given Name
     * 
     * @author Lagutko_N
     */
    
    private class ColumnInterator extends AbstractIterator<ColumnNode> {
        
        private static final int COLUMN_NODE_DEPTH = 3;

        public ColumnInterator(final String columnName) {    
            
            this.iterator = node.traverse(Traverser.Order.DEPTH_FIRST,
                                          new StopEvaluator() {

                                            public boolean isStopNode(TraversalPosition position) {                                                
                                                return position.depth() > COLUMN_NODE_DEPTH;
                                            }
                    
                                          },
                                          new ReturnableEvaluator() {

                                            public boolean isReturnableNode(TraversalPosition position) {
                                                if (position.depth() == COLUMN_NODE_DEPTH) {                                                    
                                                    return position.currentNode().getProperty(ColumnNode.COLUMN_NAME).equals(columnName);
                                                }
                                                return false;
                                            }
                                              
                                          },
                                          SplashRelationshipTypes.ROW,
                                          Direction.OUTGOING,
                                          SplashRelationshipTypes.ROW_CELL,
                                          Direction.OUTGOING,
                                          SplashRelationshipTypes.COLUMN_CELL,
                                          Direction.INCOMING).iterator();
                                          
        }

        @Override
        protected ColumnNode wrapNode(Node node) {
            return new ColumnNode(node);
        }
    }
}
