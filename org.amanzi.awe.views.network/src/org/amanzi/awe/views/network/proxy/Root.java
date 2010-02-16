/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe.views.network.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
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
    
    @Override
    public NeoNode[] getChildren() {
        ArrayList<NeoNode> networkNodes = new ArrayList<NeoNode>();
        HashMap<String,NeoNode> deltaNodes = new HashMap<String,NeoNode>();

        NeoService service = serviceProvider.getService();

        Transaction transaction = service.beginTx();
        try {
            Node reference = service.getReferenceNode();

            for (Relationship relationship : reference.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Node gisNode = null;
                if (node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.GIS.getId())) {
                    gisNode = node;
                }
                if (gisNode != null) {
                    for (Relationship gisRelationship : gisNode.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)) {
                        node = gisRelationship.getEndNode();
                        if (node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.NETWORK.getId())) {
                            networkNodes.add(new NeoNode(node));
                            for (Relationship deltaRelationship : node.getRelationships(NetworkRelationshipTypes.DELTA_REPORT, Direction.INCOMING)) {
                                Node deltaNode = deltaRelationship.getStartNode();
                                String deltaName = (String)deltaNode.getProperty("name",null);
                                if (!deltaNodes.containsKey(deltaName)) {
                                    deltaNodes.put(deltaName,new NeoNode(deltaNode));
                                }
                            }
                        }
                    }
                }
            }           
            
            transaction.success();
        } finally {
            transaction.finish();
        }
        
        if (networkNodes.isEmpty() && deltaNodes.isEmpty()) {
            return NO_NODES;
        } else {
            Collections.sort(networkNodes, new NeoNodeComparator());
            //Collections.sort(deltaNodes, new NeoNodeComparator());
            ArrayList<NeoNode> allNodes = new ArrayList<NeoNode>();
            allNodes.addAll(networkNodes);
            allNodes.addAll(deltaNodes.values());
            return allNodes.toArray(NO_NODES);
        }
    }
    
    /**
     * String representation of Root
     */
    
    @Override
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
    
    @Override
    public boolean hasChildren() {
        return true;
    }
    
}
