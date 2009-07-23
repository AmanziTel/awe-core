package org.amanzi.awe.views.network.proxy;

import java.util.ArrayList;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * Proxy class that provides access for Neo-database
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class Root extends NeoNode {
    
    /*
     * NeoServiceProvider
     */
    private NeoServiceProvider serviceProvider;
    
    /**
     * Constructor that creates Root
     * 
     * @param serviceProvider serviceProvider
     */
    
    public Root(NeoServiceProvider serviceProvider) {
        super(serviceProvider.getService().getReferenceNode());
        this.serviceProvider = serviceProvider;
    }
    
    /**
     * Returns all Network Nodes of database
     */
    
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> networkNodes = new ArrayList<NeoNode>();
        
        NeoService service = serviceProvider.getService();
        
        Transaction tx = service.beginTx();
        try {
            Node reference = service.getReferenceNode();
            
            for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node node = relationship.getEndNode();                
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NetworkElementTypes.NETWORK.toString()) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME))
                    networkNodes.add(new NeoNode(node));
            }           
            
            tx.success();
        } finally {
            tx.finish();
        }
        
        if (networkNodes.isEmpty()) {
            return NO_NODES;
        }
        else if (networkNodes.size() == 1) {
            return networkNodes.get(0).getChildren();
        }
        else {
            return networkNodes.toArray(NO_NODES);
        }
    }
    
    /**
     * String representation of Root
     */
    
    public String toString() {
        return serviceProvider.getDefaultDatabaseLocation();        
    }
    
    /**
     * Returns location of Database
     *
     * @return location of Neo-database
     */
    
    public String getDatabaseLocation() {
        return serviceProvider.getDefaultDatabaseLocation();
    }
    
    public boolean hasChildren() {
        return true;
    }
    
}
