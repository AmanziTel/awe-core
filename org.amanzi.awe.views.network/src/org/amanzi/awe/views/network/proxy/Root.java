package org.amanzi.awe.views.network.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Proxy class that provides access for Neo-database
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class Root extends NeoNode {

    protected NeoServiceProvider serviceProvider;
    
    /**
     * Constructor that creates Root
     * 
     * @param serviceProvider serviceProvider
     */
    
    public Root(NeoServiceProvider serviceProvider) {
        super(serviceProvider.getService().getReferenceNode());
        this.serviceProvider = serviceProvider;
    }
    
    public LinkedHashMap<Node,List<Node>> search(final String text) {
        LinkedHashMap<Node,List<Node>> results = new LinkedHashMap<Node,List<Node>>();
        NeoService service = serviceProvider.getService();
        Transaction transaction = service.beginTx();
        try {
            for(NeoNode firstLevel:getChildren()){
                ReturnableEvaluator returnMatches = new ReturnableEvaluator(){
    
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return currentPos.currentNode().getProperty("name", "").toString().toLowerCase().contains(text);
                    }};
                for(Node node:firstLevel.getNode().traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, returnMatches, NetworkRelationshipTypes.CHILD, Direction.OUTGOING)){
                    ArrayList<Node> path = new ArrayList<Node>();
                    Relationship relationship = null;
                    Node startNode = node;
                    while((relationship=startNode.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING))!=null) {
                        startNode = relationship.getStartNode();
                        path.add(0,startNode);
                        if(startNode==firstLevel.getNode()) break;
                    }
                    path.add(node);
                    results.put(node,path);
                }
            }
            transaction.success();
        } finally {
            transaction.finish();
        }
        return results;
    }
    
    /**
     * Returns all Network Nodes of database
     */
    
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> networkNodes = new ArrayList<NeoNode>();
        
        NeoService service = serviceProvider.getService();
        
        Transaction transaction = service.beginTx();
        try {
            Node reference = service.getReferenceNode();
            
            for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Node gisNode = null;
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.GIS_TYPE_NAME.toString()) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {
                    gisNode = node;                    
                }
                if (gisNode != null) {
                    for (Relationship gisRelationship : gisNode.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) {
                        node = gisRelationship.getEndNode();
                        if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NetworkElementTypes.NETWORK.toString()) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)) {
                            networkNodes.add(new NeoNode(node));
                        }
                    }
                }
            }           
            
            transaction.success();
        } finally {
            transaction.finish();
        }
        
        if (networkNodes.isEmpty()) {
            return NO_NODES;
        }
        else if (networkNodes.size() == 1) {
            return networkNodes.get(0).getChildren();
        }
        else {
            Collections.sort(networkNodes, new NeoNodeComparator());
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
