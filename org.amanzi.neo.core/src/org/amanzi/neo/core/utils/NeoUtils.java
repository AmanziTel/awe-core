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
package org.amanzi.neo.core.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.PropertyContainer;
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
 * @since 1.0.0
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
     * Gets node name
     * 
     * @param node node
     * @return node name or empty string
     */
    public static String getNodeName(Node node) {
        String type = node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").toString();
        if (type.equals(INeoConstants.MP_TYPE_NAME)) {
            return node.getProperty(INeoConstants.PROPERTY_TIME_NAME, "").toString();

        } else if (type.equals(INeoConstants.HEADER_MS)) {
            return node.getProperty(INeoConstants.PROPERTY_CODE_NAME, "").toString();

        }
        return getSimpleNodeName(node, "");
    }

    /**
     * Gets node name
     * 
     * @param node node
     * @param defValue default value
     * @return node name or empty string
     */
    public static String getSimpleNodeName(PropertyContainer node, String defValue) {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, defValue).toString();
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
     * check node by type
     * 
     * @param node node
     * @return true if node is gis node
     */
    public static boolean isTransmission(Node node) {
        return node != null && INeoConstants.TRANSMISSION_TYPE_NAME.equals(getNodeType(node, ""));
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
        return findNeighbour(network, name, NeoServiceProvider.getProvider().getService());
    }

    /**
     *Finds Neighbour of network
     * 
     * @param network - network GIS node
     * @param name - Neighbour name
     * @return Neighbour node or null;
     */
    public static Node findNeighbour(Node network, final String name, NeoService neo) {
        if (network == null || name == null) {
            return null;
        }
        Transaction tx = neo.beginTx();
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
            if(node.hasProperty(INeoConstants.LIST_NUMERIC_PROPERTIES)) {
                return (String[])node.getProperty(INeoConstants.LIST_NUMERIC_PROPERTIES);
            }
            // TODO remove this after refactoring tems loader
            if (node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME,"").equals(GisTypes.DRIVE.getHeader())) {
                List<String> result = new ArrayList<String>();
                Iterator<Node> iteratorProperties = node.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                        new ReturnableEvaluator() {

                            @Override
                            public boolean isReturnableNode(TraversalPosition traversalposition) {
                                Node curNode = traversalposition.currentNode();
                                Object type = curNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null);
                                return type != null && (INeoConstants.HEADER_MS.equals(type.toString()));
                            }
                        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
                        .iterator();
                if (iteratorProperties.hasNext()) {
                    Node propNode = iteratorProperties.next();
                    Iterator<String> iteratorProper = propNode.getPropertyKeys().iterator();
                    while (iteratorProper.hasNext()) {
                        String propName = iteratorProper.next();
                        if (propNode.getProperty(propName) instanceof Number) {
                            result.add(propName);
                        }
                    }
                }
                return result.toArray(new String[0]);
            }
            return null;
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets Neighbour name of relation
     * 
     * @param relation relation
     * @param defValue default value
     * @return name or default value if property do not exist
     */
    public static String getNeighbourName(Relationship relation, String defValue) {
        return relation.getProperty(INeoConstants.NEIGHBOUR_NAME, defValue).toString();
    }

    /**
     * Return neighbour relations of selected neighbour list
     * 
     * @param node node
     * @param neighbourName neighbour list name (if null then will returns all neighbour relations
     *        of this node)
     * @return neighbour relations of selected neighbour list
     */
    public static Iterable<Relationship> getNeighbourRelations(Node node, String neighbourName) {
        Iterable<Relationship> relationships = node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR, Direction.OUTGOING);
        if (neighbourName == null) {
            return relationships;
        }
        ArrayList<Relationship> result=new ArrayList<Relationship>();
        for (Relationship relation : relationships) {
            if (neighbourName.equals(getNeighbourName(relation, null))) {
                result.add(relation);
            }
        }
        return result;
    }

    /**
     * Return neighbour relations of selected neighbour list
     * 
     * @param node node
     * @param neighbourName neighbour list name (if null then will returns all neighbour relations
     *        of this node)
     * @return neighbour relations of selected neighbour list
     */
    public static Iterable<Relationship> getTransmissionRelations(Node node, String neighbourName) {
        Iterable<Relationship> relationships = node.getRelationships(NetworkRelationshipTypes.TRANSMISSION, Direction.OUTGOING);
        if (neighbourName == null) {
            return relationships;
        }
        ArrayList<Relationship> result = new ArrayList<Relationship>();
        for (Relationship relation : relationships) {
            if (neighbourName.equals(getNeighbourName(relation, null))) {
                result.add(relation);
            }
        }
        return result;
    }

    public static String getNeighbourPropertyName(String aNeighbourName) {
        return String.format("# '%s' neighbours", aNeighbourName);
    }

    public static String getTransmissionPropertyName(String aNeighbourName) {
        return String.format("# '%s' transmissions", aNeighbourName);
    }

    /**
     * Finds node by child
     * 
     * @param node - node
     * @param nodeType - type of finding node
     * @return node or null
     */
    public static Node findNodeByChild(Node node, final String nodeType) {
        if (nodeType == null || nodeType.isEmpty()) {
            return null;
        }
        Transaction tx = beginTransaction();
        try {
            Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return nodeType.equals(getNodeType(node, ""));
                }
            }, NetworkRelationshipTypes.CHILD, Direction.INCOMING, GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).iterator();
            tx.success();
            return iterator.hasNext() ? iterator.next() : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * Get all fields property
     * 
     * @param node - node
     * @return array of fields or null
     */
    public static String[] getAllFields(Node node) {
        Transaction tx = beginTransaction();
        try {
            String[] result = (String[])node.getProperty(INeoConstants.LIST_ALL_PROPERTIES, null);
            return result == null ? new String[0] : result;
        } finally {
            tx.finish();
        }

    }

    /**
     * @param node
     */
    public static void deleteSingleNode(Node node) {
        Transaction tx = beginTransaction();
        try {
            for (Relationship relation : node.getRelationships()) {
                relation.delete();
            }
            node.delete();
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets chart node of current node
     * 
     * @param node node
     * @param aggNode aggregation node
     * @return node or null
     */
    public static Node getChartNode(Node node, Node aggNode) {
        if (node == null || aggNode == null) {
            return null;
        }
        Transaction tx = beginTransaction();
        final long nodeId = aggNode.getId();
        try {
            Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return nodeId == (Long)currentPos.currentNode().getProperty(INeoConstants.PROPERTY_AGGR_PARENT_ID, nodeId - 1);
                }
            }, NetworkRelationshipTypes.AGGREGATE, Direction.INCOMING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * @param node
     * @return
     */
    public static Long getNodeTime(Node node) {
        if (node.hasProperty("timestamp")) {
            Object time = node.getProperty("timestamp");
            if (time instanceof Long) {
                return (Long)time;
            }
        }
        //TODO: This code only supports Romes data, we need TEMS support also (later)
        String time = (String)node.getProperty("time", null);
        if (time == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date timeD;
        try {
            timeD = df.parse(time);
        } catch (ParseException e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            return null;
        }
        return timeD.getTime();
    }

    /**
     * @param gis
     */
    public static Traverser getAllFileNodes(Node gis) {
        return gis.traverse(Order.DEPTH_FIRST, getStopEvaluator(3), new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return isFileNode(currentPos.currentNode());
            }
        }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
    }

    /**
     * @param nodeId
     * @return
     */
    public static Node getNodeById(Long nodeId) {
        return nodeId == null ? null : NeoServiceProvider.getProvider().getService().getNodeById(nodeId);
    }

    /**
     *Finds Transmission of network
     * 
     * @param network - network GIS node
     * @param name - Transmission name
     * @param neo - NeoService
     * @return Transmission node or null;
     */
    public static Node findTransmission(Node network, final String name, NeoService neo) {
        if (network == null || name == null) {
            return null;
        }
        Transaction tx = neo.beginTx();
        try {
            Iterator<Node> iterator = network.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return isTransmission(node) && name.equals(node.getProperty(INeoConstants.PROPERTY_NAME_NAME, ""));
                }
            }, NetworkRelationshipTypes.TRANSMISSION_DATA, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        } finally {
            tx.finish();
        }
    }

    /**
     * Gets name of ms childs
     * 
     * @param node - mp node
     * @param msName - property , that forms the name
     * @return cummulative name
     */
    public static String getMsNames(Node node, final String msName) {
        String delim = ", ";
        StringBuilder result = new StringBuilder("");
        Traverser traverse = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node curNode = currentPos.currentNode();
                return getNodeType(curNode, "").equals(INeoConstants.HEADER_MS) && curNode.hasProperty(msName);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
        for (Node nodeMs : traverse) {

            result.append(nodeMs.getProperty(msName).toString()).append(delim);
        }
        if (result.length() > delim.length()) {
            result.setLength(result.length() - delim.length());
        }
        return result.toString();
    }

    /**
     * finds or create if necessary
     * 
     * @param root - root node of drive network
     * @param neo - NeoService
     * @return SectorDriveRoot node
     */
    public static Node findOrCreateSectorDriveRoot(Node root, NeoService neo) {
        Transaction tx = neo.beginTx();
        try {
            Relationship relation = root.getSingleRelationship(NetworkRelationshipTypes.SECTOR_DRIVE, Direction.OUTGOING);
            if (relation != null) {
                return relation.getOtherNode(root);
            }
            Node result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.ROOT_SECTOR_DRIVE);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, INeoConstants.ROOT_SECTOR_DRIVE);
            root.createRelationshipTo(result, NetworkRelationshipTypes.SECTOR_DRIVE);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }

    }

    /**
     * @param mpNode
     * @param neo
     * @return
     */
    public static Map<String, Object> getSectorIdentificationMap(Node mpNode, NeoService neo) {
        Map<String, Object> result = new HashMap<String, Object>();
        // TODO implement
        return result;
    }


    /**
     * @param sectorDriveRoot
     * @param identifyMap
     * @param neo
     * @return
     */
    public static Node findOrCreateSectorDrive(Node sectorDriveRoot, java.util.Map<String, Object> identifyMap, NeoService neo) {
        // TODO implement
        return null;
    }

    // /**
    // * Forms Event image of mpNode
    // *
    // * @param mpNode - node
    // * @param neo - neoservice
    // * @return image or null if no event available
    // */
    // public static IconManager.EventIcons formEventImage(Node mpNode, NeoService neo) {
    // Transaction tx = neo.beginTx();
    // try{
    // Iterator<Node> iterator = mpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new
    // ReturnableEvaluator() {
    //
    // @Override
    // public boolean isReturnableNode(TraversalPosition currentPos) {
    // if (currentPos.isStartNode()) {
    // return false;
    // }
    // Node node = currentPos.currentNode();
    // boolean result = node.hasProperty(INeoConstants.PROPERTY_TYPE_EVENT);
    // return result;
    // }
    // }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
    // if (!iterator.hasNext()) {
    // return null;
    // }
    // String event = iterator.next().getProperty(INeoConstants.PROPERTY_TYPE_EVENT).toString();
    // if (true || event.toLowerCase().contains("connect")) {
    // return IconManager.EventIcons.CONNECT;
    // }
    // return null;
    // }finally{
    // tx.finish();
    // }
    //        
    // }

}
