package org.amanzi.neo.core.database.nodes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.exception.LoopInCellReferencesException;
import org.amanzi.neo.core.enums.CellRelationTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;

/**
 * Wrapper of Spreadsheet Cell Node
 * 
 * @author Lagutko_N
 */

public class CellNode extends AbstractNode {
    
    
    /*
     * Cell Definition property.
     */
    private static final String CELL_DEFINITION = "cell_definition";

    /*
     * Name of Value property
     */    
    private static final String CELL_VALUE = "value";
    
    /*
     * Name of Script URI property
     */
    
    private static final String SCRIPT_URI = "script_uri";
    
    /*
     * Type of this Node
     */
    private static final String CELL_NODE_TYPE = "spreadsheet_cell";
    
    /*
     * Name of Cyclic Dependencies property
     */
    private static final String CELL_CYCLIC = "Cell cyclic dependencies";
    
    /**
     * Constructor. Wraps a Node from database and sets type and name of Node
     * 
     * @param node database node
     */
    public CellNode(Node node) {
        super(node);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, CELL_NODE_TYPE);
    }
    
    public void addSplashFormat(SplashFormatNode sfNode){
    	addRelationship(SplashRelationshipTypes.SPLASH_FORMAT, sfNode.getUnderlyingNode());
    }
    
    public SplashFormatNode getSplashFormat(){
    	return SplashFormatNode.fromNode(node.getSingleRelationship(SplashRelationshipTypes.SPLASH_FORMAT, Direction.INCOMING).getStartNode());
    }
    
    /**
     * Computes the Row of Cell
     *
     * @return Row of Cell
     */    
    public RowNode getRow() {
        return RowNode.fromNode(node.getSingleRelationship(SplashRelationshipTypes.ROW_CELL, Direction.INCOMING).getStartNode());
    }
    
    /**
     * Computes the Column of Cell
     *
     * @return Column of Cell
     */
    
    public ColumnNode getColumn() {
        return ColumnNode.fromNode(node.getSingleRelationship(SplashRelationshipTypes.COLUMN_CELL, Direction.INCOMING).getStartNode());
    }
    
    /**
     * Sets the value of Cell
     *
     * @param value value of Cell
     */
    
    public void setValue(String value) {
        setParameter(CELL_VALUE, value);
    }
    
    /**
     * Returns the value of Cell
     *
     * @return value of Cell
     */
    
    public String getValue() {
        return (String)getParameter(CELL_VALUE);
    }
    
    /**
     * Returns Definition of Cell
     *
     * @return cell's definition
     */
    public String getDefinition() {
        return (String)getParameter(CELL_DEFINITION);
    }
    
    /**
     * Sets Definition for Cell
     *
     * @param definition cell's definition
     */
    public void setDefinition(String definition) {
        setParameter(CELL_DEFINITION, definition);
    }
    
  
    /**
     * Sets Script URI of Cell
     *
     * @param scriptURI
     */    
    public void setScriptURI(URI scriptURI) {
        if (scriptURI != null) {
            
            setParameter(SCRIPT_URI, scriptURI.toString());
        }
    }
    
    /**
     * Sets is this cell has cyclic dependencies
     *
     * @param isCyclic
     */
    public void setCyclic(boolean isCyclic) {
        setParameter(CELL_CYCLIC, isCyclic);
    }
    
    /**
     * Is this Cell has Cyclic dependencies?
     *
     * @return
     */
    public boolean isCyclic() {
        if (!(Boolean)node.hasProperty(CELL_CYCLIC)) {
            return false;
        }
        return (Boolean)getParameter(CELL_CYCLIC);
    }
    
    /**
     * Returns Script URI for Cell
     *
     * @return cell's script URI
     */
    public URI getScriptURI() {
        String uri = (String)getParameter(SCRIPT_URI);
        if (uri != null) {
            try {
                return new URI(uri);
            }
            catch (URISyntaxException e) {
                NeoCorePlugin.error(null, e);
                return null;
            }
        }
        return null;
    }
    
    /**
     * Adds a RFD Cell
     *
     * @param rfdNode wrapped RFD Cell
     */    
    public void addDependedNode(CellNode rfdNode) throws LoopInCellReferencesException {
        Relationship relationship = null;
        
        try {
            relationship = addRelationship(CellRelationTypes.REFERENCED, rfdNode.getUnderlyingNode());
            checkLoops(rfdNode);
        }
        catch (IllegalArgumentException e) {
            //Lagutko: the IllegalArgumentException will be thrown if we try to create Relationship to same Node
            throw new LoopInCellReferencesException(new CellID(rfdNode.getRow().getRowIndex(), rfdNode.getColumn().getColumnName()));
        }
        catch (LoopInCellReferencesException e) {
            //Lagutko: LoopInCellReferencesException will be thrown 
            relationship.delete();
            throw e;
        }
    }
    
    /**
     * Checks a Loops in Cell's References
     *
     * @param newDependedNode
     * @throws LoopInCellReferencesException
     */
    private void checkLoops(CellNode newDependedNode) throws LoopInCellReferencesException {
        Iterator<CellNode> lastNode = new LoopDependencyIterator(newDependedNode);
        
        if (lastNode.hasNext()) {
            throw new LoopInCellReferencesException(new CellID(newDependedNode.getRow().getRowIndex(), newDependedNode.getColumn().getColumnName()));
        }
    }
    
    /**
     * Delete a list of Cells from dependent
     *
     * @param rfdNodes list of Cells
     */
    public void deleteReferenceFromNode(List<CellNode> rfdNodes) {
        ArrayList<Node> underlyingNodes = new ArrayList<Node>(rfdNodes.size());
        for (CellNode node : rfdNodes) {
            underlyingNodes.add(node.getUnderlyingNode());
        }
        
        Iterator<Relationship> relationships = node.getRelationships(CellRelationTypes.REFERENCED, Direction.OUTGOING).iterator();
        
        while (relationships.hasNext()) {
            Relationship relationship = relationships.next();
            
            if (underlyingNodes.contains(relationship.getEndNode())) {
                relationship.delete();
            }
        }
    }
    
    /**
     * Returns Iterator for Cells that depended on this Cell
     *
     * @return iterator of depended Cells
     */
    public Iterator<CellNode> getDependedNodes() {        
        return new DependedNodesIterator();
    }
    
    /**
     * Returns Iterator for Cells that references on this Cell
     *
     * @return iterator of referenced Cells
     */
    public Iterator<CellNode> getReferencedNodes() {        
        return new ReferencedNodesIterator();
    }
    
    /**
     * Iterator for RFD cells;
     * 
     * @author Lagutko_N
     */
    private class DependedNodesIterator extends AbstractIterator<CellNode> {
        
        public DependedNodesIterator() {
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
                                          StopEvaluator.DEPTH_ONE,
                                          ReturnableEvaluator.ALL_BUT_START_NODE,
                                          CellRelationTypes.REFERENCED,
                                          Direction.INCOMING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {
            return new CellNode(node);
        }
    }
    
    /**
     * Iterator for RFD cells;
     * 
     * @author Lagutko_N
     */
    private class ReferencedNodesIterator extends AbstractIterator<CellNode> {
        
        public ReferencedNodesIterator() {
            this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
                                          StopEvaluator.DEPTH_ONE,
                                          ReturnableEvaluator.ALL_BUT_START_NODE,
                                          CellRelationTypes.REFERENCED,
                                          Direction.OUTGOING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {
            return new CellNode(node);
        }
    }
    
    /**
     * Special Traverser for detecting Loops in Cell References
     * 
     * @author Lagutko_N
     */
    private class LoopDependencyIterator extends AbstractIterator<CellNode> {
        
        
        public LoopDependencyIterator(CellNode endNode) {
            final Node end = endNode.getUnderlyingNode();
            
            this.iterator = node.traverse(Traverser.Order.DEPTH_FIRST, 
                                          StopEvaluator.END_OF_GRAPH,
                                          new ReturnableEvaluator(){
                                              
                                            
                                              public boolean isReturnableNode(TraversalPosition arg0) {
                                                  if (arg0.depth() > 0) {
                                                      return arg0.currentNode().equals(end);
                                                  }
                                                  else {
                                                      return false;
                                                  }
                                              }
                                          },
                                          CellRelationTypes.REFERENCED,
                                          Direction.INCOMING).iterator();
        }

        @Override
        protected CellNode wrapNode(Node node) {
            return new CellNode(node);
        }
        
    }
}
