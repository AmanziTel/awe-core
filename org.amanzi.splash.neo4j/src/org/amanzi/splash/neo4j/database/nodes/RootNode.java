package org.amanzi.splash.neo4j.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Root Node of Spreadsheets
 * 
 * @author Lagutko_N
 */

public class RootNode extends AbstractNode {
    
    /**
     * Constructor. Wraps a Node that will be parent node for Spreadsheets
     * 
     * @param node
     */
    public RootNode(Node node) {
        super(node);
        //Lagutko, 28.07.2009, for now RootNode is reference not and we don't set here any type and name properties
    }
    
    /**
     * Returns all Spreadsheets for this Root Node
     *
     * @return
     */
    
    public Iterator<SpreadsheetNode> getSpreadsheets() {
        return new SpreadsheetIterator();
    }
    
    /**
     * Adds a Spreadsheets
     *
     * @param spreadsheet wrapper of Spreadsheet
     */
    
    public void addSpreadsheet(SpreadsheetNode spreadsheet) {
        addRelationship(SplashRelationshipTypes.SPREADSHEET, spreadsheet.getUnderlyingNode());
    }
    
    /**
     * Iterator for Spreadsheets in the Root
     * 
     * @author Lagutko_N
     */
    
    private class SpreadsheetIterator extends AbstractIterator<SpreadsheetNode> {
        
        /**
         * Constructor. Creates a Traverser that will look for all Spreadsheets
         *  
         */        
        public SpreadsheetIterator() {
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
                                   StopEvaluator.DEPTH_ONE, 
                                   ReturnableEvaluator.ALL_BUT_START_NODE, 
                                   SplashRelationshipTypes.SPREADSHEET,
                                   Direction.OUTGOING).iterator();
        }

        @Override
        protected SpreadsheetNode wrapNode(Node node) {
            return new SpreadsheetNode(node);
        }
        
    }

}
