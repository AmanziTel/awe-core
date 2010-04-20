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

package org.amanzi.neo.data_generator.utils;

import java.util.HashSet;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Utility class for work with neo4j
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NeoDataUtils {

    /**
     * Instantiates a new neo data utils.
     */
    private NeoDataUtils() {
        //hide constructor
    }
    
    /**
     * Creates the site.
     *
     * @param parent the parent
     * @param name the name
     * @param latitude the latitude
     * @param longitude the longitude
     * @param service the service
     * @return the node
     */
    public static Node createSite(Node parent,String name,Double latitude, Double longitude,GraphDatabaseService service){
        Transaction tx = service.beginTx();
        try{
            Node resultSite=service.createNode();
            resultSite.setProperty("type","site");
            setPropertyIfNotNull(resultSite,"name",name);
            setPropertyIfNotNull(resultSite,"lat",latitude);
            setPropertyIfNotNull(resultSite,"lon",longitude); 
            createRelationship(parent,resultSite,"CHILD");
            tx.success();
            return resultSite;
        }finally{
            tx.finish();
        }
    }
    
    /**
     * Creates the node.
     *
     * @param properties the properties
     * @param service the service
     * @return the node
     */
    public static Node createNode(Map<String,Object> properties,GraphDatabaseService service){
        Transaction tx = service.beginTx();
        try{
            Node result=service.createNode();
            for (Map.Entry<String,Object>  entry : properties.entrySet()) {
                setPropertyIfNotNull(result, entry.getKey(), entry.getValue());
            }
            tx.success();
            return result;
        }finally{
            tx.finish();
        }       
    }

    /**
     * Creates the relationship.
     *
     * @param parent the parent
     * @param child the child
     * @param relationName the relation name
     */
    public static void createRelationship(Node parent, Node child, final String relationName) {
        parent.createRelationshipTo(child, new RelationshipType() {
            
            @Override
            public String name() {
                return relationName;
            }
        });
    }
    
    /**
     * Sets the property if not null.
     *
     * @param node the node
     * @param propertyName the property name
     * @param value the value
     */
    private static void setPropertyIfNotNull(Node node, String propertyName, Object value) {
        if (value == null) {
            return;
        }
        node.setProperty(propertyName, value);
    }

    public static void compareNet(CompareResult result, Node net, Node etalonNet, GraphDatabaseService service,
            Object... relationshipTypesAndDirections) {
        Transaction tx = service.beginTx();
        try {
            if (net.equals(etalonNet)) {
                result.setEquals(true);
                return;
            }
            HashSet<Node> etalonSet = new HashSet<Node>(etalonNet.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL_BUT_START_NODE, relationshipTypesAndDirections).getAllNodes());
            HashSet<Node> netSet = new HashSet<Node>(net.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL_BUT_START_NODE, relationshipTypesAndDirections).getAllNodes());
            result.compareNodeSets(etalonSet, netSet, relationshipTypesAndDirections);
        } finally {
            tx.finish();
        }
    }
}
