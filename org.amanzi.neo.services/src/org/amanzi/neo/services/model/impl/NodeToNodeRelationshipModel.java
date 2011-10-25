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

package org.amanzi.neo.services.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NodeToNodeTypes;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.IllegalNameException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * This class covers node to node relationships of exactly one <code>INodeToNodeRelationsType</code>
 * between the nodes of exactly one <code>INodeType</code>, that belong to the same network.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NodeToNodeRelationshipModel extends PropertyStatisticalModel implements INodeToNodeRelationsModel {

    public static final String RELATION_TYPE = "rel_type";

    private static Logger LOGGER = Logger.getLogger(NodeToNodeRelationshipModel.class);

    private INodeToNodeRelationsType relType;

    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();
    private NewNetworkService networkServ = NeoServiceFactory.getInstance().getNewNetworkService();

    /**
     * <p>
     * This enum describes relationships between nodes.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum N2NRelTypes implements INodeToNodeRelationsType {
        NEIGHBOUR, INTERFERENCE_MATRIX, TRIANGULATION, SHADOW;

        @Override
        public String getId() {
            return name();
        }

    }

    /**
     * One node2node model handles one <code>relType</code> and <code>nodeType</code>, and is
     * identified by <code>name</code>.
     * 
     * @param parent basically, a network root
     * @param relType
     * @param name
     * @param nodeType
     * @throws AWEException
     */
    public NodeToNodeRelationshipModel(IDataElement parent, INodeToNodeRelationsType relType, String name, INodeType nodeType)
            throws AWEException {
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        Node parentNode = ((DataElement)parent).getNode();
        if (parentNode == null) {
            throw new IllegalArgumentException("Parent node is null.");
        }
        if (relType == null) {
            throw new IllegalArgumentException("Relationship type is null.");
        }
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty.");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        this.nodeType = nodeType;
        this.relType = relType;
        this.name = name;
        Node root = dsServ.findNode(parentNode, relType, name, NodeToNodeTypes.NODE2NODE);
        if (root != null) {
            this.rootNode = root;
        } else {
            this.rootNode = dsServ.createNode(parentNode, relType, NodeToNodeTypes.NODE2NODE);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(NewNetworkService.NETWORK_ID, parentNode.getId());
            params.put(NewNetworkService.NAME, this.name);
            params.put(NewNetworkService.TYPE, NodeToNodeTypes.NODE2NODE.getId());
            params.put(RELATION_TYPE, this.relType.getId());
            params.put(PRIMARY_TYPE, nodeType.getId());
            dsServ.setProperties(rootNode, params);
        }

        initializeStatistics();
    }

    NodeToNodeRelationshipModel(Node n2nRoot) throws AWEException {
        // validate
        if (n2nRoot == null) {
            throw new IllegalArgumentException("Node2node root is null.");
        }

        this.rootNode = n2nRoot;
        this.nodeType = NodeTypeManager.getType(n2nRoot.getProperty(PRIMARY_TYPE).toString());
        this.relType = N2NRelTypes.valueOf(n2nRoot.getProperty(RELATION_TYPE).toString());
        this.name = n2nRoot.getProperty(NewNetworkService.NAME).toString();

        initializeStatistics();
    }

    @Override
    public void linkNode(IDataElement source, IDataElement target, Map<String, Object> params) throws AWEException {
        // validate parameters
        if (source == null) {
            throw new IllegalNameException("Source is null.");
        }
        Node sourceNode = ((DataElement)source).getNode();
        if (sourceNode == null) {
            throw new IllegalNodeDataException("Source node is null.");
        }
        if (target == null) {
            throw new IllegalNameException("Target is null.");
        }
        Node targetNode = ((DataElement)target).getNode();
        if (targetNode == null) {
            throw new IllegalNodeDataException("Target node is null.");
        }

        Node proxy1 = getProxy(sourceNode);
        Node proxy2 = getProxy(targetNode);

        if (!related(proxy1, proxy2)) {
            Relationship rel = dsServ.createRelationship(proxy1, proxy2, relType);
            if (params != null) {
                dsServ.setProperties(rel, params);
                indexProperty(nodeType, params);
            }
        }
    }

    /**
     * Checks whether there already exists relationship between the two proxies
     * 
     * @param proxy1
     * @param proxy2
     * @return
     */
    private boolean related(Node proxy1, Node proxy2) {
        for (Relationship rel : proxy1.getRelationships(relType, Direction.OUTGOING)) {
            if (rel.getEndNode().equals(proxy2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds or creates a proxy for the defined <code>sourceNode</code>.
     * 
     * @param sourceNode
     * @return the resulting proxy
     * @throws AWEException
     */
    public Node getProxy(Node sourceNode) throws AWEException {
        Node result = findProxy(sourceNode);
        if (result == null) {
            result = networkServ.createProxy(sourceNode, rootNode, N2NRelTypes.NEIGHBOUR);
        }
        return result;
    }

    /**
     * Looks for a proxy for the defined <code>sourceNode</code>.
     * 
     * @param sourceNode
     * @return the found proxy node or <code>null</code>
     */
    private Node findProxy(Node sourceNode) {
        Node result = null;
        Relationship rel = sourceNode.getSingleRelationship(this.relType, Direction.OUTGOING);
        if (rel != null) {
            result = rel.getEndNode();
        }
        return result;
    }

    @Override
    public Iterable<IDataElement> getN2NRelatedElements(IDataElement source) {
        // validate parameters
        if (source == null) {
            throw new IllegalArgumentException("Source is null.");
        }
        Node sourceNode = ((DataElement)source).getNode();
        if (sourceNode == null) {
            throw new IllegalArgumentException("Source node is null.");
        }

        Node proxy = findProxy(sourceNode);
        if (proxy != null) {
            return new DataElementIterable(dsServ.findN2NRelationships(proxy, relType));
        } else {
            return new DataElementIterable(dsServ.emptyTraverser(sourceNode));
        }
    }

    @Override
    public INodeToNodeRelationsType getNodeToNodeRelationsType() {
        return this.relType;
    }

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(dsServ.findAllN2NElements(getRootNode(), elementType, relType));
    }

}
