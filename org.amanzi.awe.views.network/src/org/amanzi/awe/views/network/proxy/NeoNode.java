package org.amanzi.awe.views.network.proxy;

import java.util.ArrayList;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;

/**
 * Proxy class that provides access for Node, it's children and properties
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NeoNode {
    
    /*
     * Constans for empty array of Nodes
     */
    protected static final NeoNode[] NO_NODES = new NeoNode[0];
    
    /*
     * Node
     */
    private Node node;
    
    /*
     * Name of Node
     */
    private String name;
    
    /**
     * Creates a proxy element for given Node
     * 
     * @param node node
     */
    
    public NeoNode(Node node) {
        this.node = node;
        
        if (node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {
            name = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        }
    }
    
    /**
     * Is this node has children
     *
     * @return is this node has children 
     */
    
    public boolean hasChildren() {        
        return node.hasRelationship(NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
    }
    
    /**
     * Return String representation of given Node
     */
    
    public String toString() {
        return name;
    }
    
    /**
     * Returns children of this node
     *
     * @return
     */
    
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> children = new ArrayList<NeoNode>();
        
        for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.CHILD,Direction.OUTGOING)){
            children.add(new NeoNode(relationship.getEndNode()));
        }
        
        return children.toArray(NO_NODES);
    }
    
    /**
     * Returns Type of node
     *
     * @return type of Node
     */
    
    public String getType() {
        return (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME);
    }
    
    /**
     * Returns the Node
     *
     * @return node
     */
    
    public Node getNode() {
        return node;
    }

}
