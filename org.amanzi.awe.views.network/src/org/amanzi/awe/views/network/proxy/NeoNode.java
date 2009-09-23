package org.amanzi.awe.views.network.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * Proxy class that provides access for Node, it's children and properties
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NeoNode {
    
    /*
     * Constant for empty array of Nodes
     */
    protected static final NeoNode[] NO_NODES = new NeoNode[0];
    
    /*
     * Node
     */
    protected Node node;
    
    /*
     * Name of Node
     */
    protected String name;

    private ArrayList<NeoNode> children;
    
    /**
     * Creates a proxy element for given Node
     * 
     * @param node node
     */

    public NeoNode(Node node) {
        this.node = node;
        this.name = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
    }
    
    /**
     * Is this node has children
     *
     * @return is this node has children 
     */
    
    public boolean hasChildren() {
        return getChildren().length>0;
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
        if(children==null) {
            children = new ArrayList<NeoNode>();
            for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.CHILD,Direction.OUTGOING)){
                children.add(new NeoNode(relationship.getEndNode()));
            }
            if(children.size()==0) {
                for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.MISSING,Direction.OUTGOING)){
                    children.add(new NeoNode(relationship.getEndNode()));
                }
                for(Relationship relationship:node.getRelationships(NetworkRelationshipTypes.DIFFERENT,Direction.OUTGOING)){
                    children.add(new NeoNode(relationship.getEndNode()));
                }
            }
            Collections.sort(children, new NeoNodeComparator());
        }
        return children.toArray(NO_NODES);
    }
    
    /**
     * Returns Type of node
     *
     * @return type of Node
     */
    
    public String getType() {
        return (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null);
    }
    
    /**
     * Returns the Node
     *
     * @return node
     */
    
    public Node getNode() {
        return node;
    }

    /**
     * <p>
     * Comparator of NeoNode
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class NeoNodeComparator implements Comparator<NeoNode> {

        @Override
        public int compare(NeoNode o1, NeoNode o2) {
            return o1 == null ? -1 : o2 == null ? 1 : o1.name.compareTo(o2.name);
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof NeoNode))
            return false;
        NeoNode other = (NeoNode)obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        return true;
    }

    /**
     * Sets node name
     * 
     * @param value new name
     */
    public void setName(String value) {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            value = value == null ? "" : value.trim();
            if (node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {

                Object oldName = node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                if (oldName.equals(value)) {
                    return;
                }
                node.setProperty(INeoConstants.PROPERTY_NAME_NAME, value);
                name = value;
                node.setProperty(INeoConstants.PROPERTY_OLD_NAME, oldName.toString());
                tx.success();
                NeoServiceProvider.getProvider().commit();
            }
        } finally {
            tx.finish();
        }

    }

}
