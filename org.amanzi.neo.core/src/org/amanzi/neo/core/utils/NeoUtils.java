/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.core.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Utility class that provides common methods for work with neo nodes
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NeoUtils {
    private NeoUtils() {

    }

    /**
     * delete all incoming reference
     * 
     * @param node
     */
    public static void deleteIncomingRelations(Node node) {
        for (Relationship relation : node.getRelationships(Direction.INCOMING)) {
            relation.delete();
        }
    }

    /**
     * gets node name
     * 
     * @param node node
     * @param defValue default value
     * @return node name or defValue
     */
    public static String getNodeType(Node node, String... defValue) {
        String def = defValue == null || defValue.length < 1 ? null : defValue[0];
        return (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, def);
    }

    /**
     * Gets node type
     * 
     * @param node node
     * @return node type or empty string
     */
    public static String getNodeName(Node node) {
        String type = node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").toString();
        if (type.equals(INeoConstants.MP_TYPE_NAME)) {
            return node.getProperty(INeoConstants.PROPERTY_TIME_NAME, "").toString();

        } else if (type.equals(INeoConstants.HEADER_MS)) {
            return node.getProperty(INeoConstants.PROPERTY_CODE_NAME, "").toString();

        }
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();
    }

    /**
     * check node by type
     * 
     * @param node node
     * @return true if node is file node
     */
    public static boolean isFileNode(Node node) {
        return node != null && INeoConstants.FILE_TYPE_NAME.equals(getNodeType(node, ""));
    }

    /**
     * gets stop evaluator with necessary depth
     * 
     * @param depth - depth
     * @return stop evaluator
     */
    public static final StopEvaluator getStopEvaluator(final int depth) {
        return new StopEvaluator() {
            @Override
            public boolean isStopNode(TraversalPosition currentPosition) {
                return currentPosition.depth() >= depth;
            }
        };
    }

    /**
     * finds gis node by name
     * 
     * @param gisName name of gis node
     * @return gis node or null
     */
    public static Node findGisNode(final String gisName) {
        if (gisName == null || gisName.isEmpty()) {
            return null;
        }
        Node root = NeoServiceProvider.getProvider().getService().getReferenceNode();
        Iterator<Node> gisIterator = root.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return isGisNode(node) && getNodeName(node).equals(gisName);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        return gisIterator.hasNext() ? gisIterator.next() : null;
    }

    /**
     * check node by type
     * 
     * @param node node
     * @return true if node is gis node
     */
    public static boolean isGisNode(Node node) {
        return node != null && INeoConstants.GIS_TYPE_NAME.equals(getNodeType(node, ""));
    }

    /**
     * check node by type
     * 
     * @param node node
     * @return true if node is gis node
     */
    public static boolean isNeighbourNode(Node node) {
        return node != null && INeoConstants.NEIGHBOUR_TYPE_NAME.equals(getNodeType(node, ""));
    }

    /**
     *Delete gis node if it do not have outcoming relations
     */
    public static void deleteEmptyGisNodes() {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            Node root = NeoServiceProvider.getProvider().getService().getReferenceNode();
            Traverser gisTraverser = root.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return isGisNode(node) && !node.hasRelationship(Direction.OUTGOING);
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
            for (Node gisNode : gisTraverser.getAllNodes()) {
                NeoCorePlugin.getDefault().getProjectService().deleteNode(gisNode);
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Finds gis node by child
     * 
     * @param childNode child
     * @return gis node or null
     */
    public static Node findGisNodeByChild(Node childNode) {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            Iterator<Node> gisIterator = childNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    new ReturnableEvaluator() {

                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            Node node = currentPos.currentNode();
                            return isGisNode(node);
                        }
                    }, NetworkRelationshipTypes.CHILD, Direction.INCOMING, GeoNeoRelationshipTypes.NEXT, Direction.INCOMING)
                    .iterator();
            tx.success();
            return gisIterator.hasNext() ? gisIterator.next() : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * @throws MalformedURLException
     */
    public static void resetGeoNeoService() {
        try {
            // TODO gets service URL from static field
            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(
                    new URL("file://" + databaseLocation));
            for (IService service : services) {
                System.out.println("Found catalog service: " + service);
                if (catalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor()) != null) {
                    catalog.replace(service.getIdentifier(), service);
                } else {
                    catalog.add(service);
                }
            }

        } catch (MalformedURLException e) {
            // TODO Handle MalformedURLException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * begins new neo4j transaction
     * 
     * @return transaction
     */
    public static Transaction beginTransaction(){
        return NeoServiceProvider.getProvider().getService().beginTx();
        
    }

    /**
     *Finds Neighbour of network
     * 
     * @param network - network GIS node
     * @param name - Neighbour name
     * @return Neighbour node or null;
     */
    public static Node findNeighbour(Node network, final String name) {
        if (network == null || name == null) {
            return null;
        }
        Transaction tx = beginTransaction();
        try {
            Iterator<Node> iterator = network.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return isNeighbourNode(node) && name.equals(node.getProperty(INeoConstants.PROPERTY_NAME_NAME, ""));
                }
            }, NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets array of Numeric Fields
     * 
     * @param node - node
     * @return array or null if properties do not exist
     */
    public static String[] getNumericFields(Node node) {
        Transaction tx = beginTransaction();
        try {
            return (String[])node.getProperty(INeoConstants.LIST_NUMERIC_PROPERTIES, null);
        } finally {
            tx.finish();
        }
    }
}
