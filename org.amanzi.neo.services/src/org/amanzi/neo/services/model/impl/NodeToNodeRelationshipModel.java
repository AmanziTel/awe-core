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

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.impl.DistributionModel;
import org.amanzi.neo.services.CorrelationService.CorrelationNodeTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
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
    public static String FREQUENCY = "frequency";
    private INodeToNodeRelationsType relType;
    private final Map<Integer, IDataElement> cache = new HashMap<Integer, IDataElement>();
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
        NEIGHBOUR, INTERFERENCE_MATRIX, TRIANGULATION, SHADOW, ILLEGAL_FREQUENCY, FREQUENCY_SPECTRUM, TRANSMISSION, EXCEPTION;

        @Override
        public String getId() {
            return name();
        }

    }

    /**
     * <p>
     * Types of nodes that are used inside of <code>NodeToNodeRelationshipModel</code> class.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum NodeToNodeTypes implements INodeType {
        NODE2NODE, PROXY, FREQUENCY;

        static {
            NodeTypeManager.registerNodeType(CorrelationNodeTypes.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
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
        super(NodeToNodeTypes.NODE2NODE);

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
        this.name = name + " " + relType.name();
        Node root = dsServ.findNode(parentNode, relType, this.name, NodeToNodeTypes.NODE2NODE);
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
            if (this.relType == N2NRelTypes.FREQUENCY_SPECTRUM) {
                loadCache();
            }
        }

        initializeStatistics();
    }

    public NodeToNodeRelationshipModel(Node n2nRoot) throws AWEException {
        super(NodeToNodeTypes.NODE2NODE);

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

    private void loadCache() {
        cache.clear();
        for (Node node : dsServ.getChildrenChainTraverser(rootNode)) {
            cache.put((Integer)node.getProperty(FREQUENCY), new DataElement(node));
        }
    }

    @Override
    public IDataElement getFrequencyElement(int frequency) throws DatabaseException {
        if (this.relType != N2NRelTypes.FREQUENCY_SPECTRUM) {
            throw new IllegalAccessError("Frequency node can be get only from spectrum model");
        }
        IDataElement result = cache.get(frequency);
        if (result != null) {
            return result;
        }
        Node newNode = dsServ.createNode(NodeToNodeTypes.FREQUENCY);
        newNode.setProperty(FREQUENCY, frequency);
        newNode.setProperty(NewAbstractService.NAME, frequency);
        dsServ.addChild(rootNode, newNode, null);
        result = new DataElement(newNode);
        cache.put(frequency, result);
        return result;
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

        if (related(proxy1, proxy2) == null) {
            Relationship rel = dsServ.createRelationship(proxy1, proxy2, relType);
            if (params != null) {
                dsServ.setProperties(rel, params);
                indexProperty(getType(), params);
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
    private Relationship related(Node proxy1, Node proxy2) {
        for (Relationship rel : proxy1.getRelationships(relType, Direction.OUTGOING)) {
            if (rel.getEndNode().equals(proxy2)) {
                return rel;
            }
        }
        return null;
    }

    @Override
    public void updateRelationship(IDataElement serviceElement, IDataElement neighbourElement, Map<String, Object> properties,
            boolean isReplace) throws AWEException {
        linkNode(serviceElement, neighbourElement, properties);
        Node serviceNode = ((DataElement)serviceElement).getNode();
        Node neighbourNode = ((DataElement)neighbourElement).getNode();
        Node serviceProxy = getProxy(serviceNode);
        Node neighbourProxy = getProxy(neighbourNode);
        Relationship rel = related(serviceProxy, neighbourProxy);
        NeoServiceFactory.getInstance().getNewNetworkService()
                .completeProperties(rel, new DataElement(properties), isReplace, null);
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
            result = networkServ.createProxy(sourceNode, rootNode, this.relType, NodeToNodeTypes.PROXY);
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
    public IDataElement getServiceElementByProxy(IDataElement proxy) {
        return new DataElement(networkServ.getServiceElementByProxy(((DataElement)proxy).getNode(), (N2NRelTypes)relType));
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }
        LOGGER.info("getAllElementsByType(" + elementType.getId() + ")");

        return new DataElementIterable(dsServ.findAllN2NElements(getRootNode(), elementType, relType));
    }

    @Override
    public IDistributionModel getDistributionModel(IDistribution< ? > distributionType) throws AWEException {
        return new DistributionModel(this, distributionType);
    }

}