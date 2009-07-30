package org.amanzi.splash.neo4j.database.nodes;

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
     * ColumnName property of Node
     */    
    public static final String COLUMN_NAME = "column_name";
    
    /*
     * Type of this Node
     */
    private static final String COLUMN_NODE_TYPE = "Spreadsheet_Column";
    
    /*
     * Name of this Node
     */
    private static final String COLUMN_NODE_NAME = "Spreadsheet Column";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public ColumnNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, COLUMN_NODE_TYPE);
        setParameter(INeoConstants.PROPERTY_NAME_NAME, COLUMN_NODE_NAME);
    }
    
    /**
     * Returns Column name
     *
     * @return name of Column
     */    
    public String getColumnName() {
        return (String)getParameter(COLUMN_NAME);
    }
    
    /**
     * Sets the name of Column
     *
     * @param columnName name of Column
     */    
    public void setColumnName(String columnName) {
        setParameter(COLUMN_NAME, columnName);
    }
    
    /**
     * Adds a Cell to Column
     *
     * @param cell Cell
     */    
    public void addCell(CellNode cell) {
        addRelationship(SplashRelationshipTypes.COLUMN_CELL, cell.getUnderlyingNode());
    }
    
    /**
     * Returns number of Cells in this Row
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
}
