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
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.impl.DataModel.DataElementIterable;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * This class covers node to node relationships of exactly one <code>INodeToNodeRelationsType</code>
 * between the nodes of exactly one <code>INodeType</code>, that belong to the same network.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NodeToNodeRelationshipModel extends AbstractModel implements INodeToNodeRelationsModel {

    private static Logger LOGGER = Logger.getLogger(NodeToNodeRelationshipModel.class);

    private INodeToNodeRelationsType relType;

    private NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    /**
     * <p>
     * Enum describing utility relationships in <code>NodeToNodeRelationshipModel</code> class. TODO
     * rename?
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    protected enum N2NRelationships implements RelationshipType {
        N2N_REL;
    }

    /**
     * <p>
     * This enum describes relationships between nodes.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum N2NRelTypes implements INodeToNodeRelationsType {
        NEIGHBOUR;

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
    protected enum NodeToNodeTypes implements INodeType {
        NODE2NODE, PROXY;

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
     */
    NodeToNodeRelationshipModel(IDataElement parent, INodeToNodeRelationsType relType, String name, INodeType nodeType) {
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
        try {
            Node root = dsServ.findNode(parentNode, N2NRelationships.N2N_REL, name, NodeToNodeTypes.NODE2NODE);
            if (root != null) {
                this.rootNode = root;
            } else {
                this.rootNode = dsServ.createNode(parentNode, N2NRelationships.N2N_REL, NodeToNodeTypes.NODE2NODE);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(NewNetworkService.NETWORK_ID, parentNode.getId());
                params.put(NewNetworkService.NAME, this.name);
                params.put(NewNetworkService.TYPE, NodeToNodeTypes.NODE2NODE.getId());
                params.put(PRIMARY_TYPE, this.relType.getId());
                dsServ.setProperties(rootNode, params);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Could not create root node.", e);
        }
    }

    /**
     * Creates a relationship from <code>source</code> to <code>target</code> through PROXY nodes,
     * and sets <code>params</code> properties to the relationship, if defined. Proxy nodes are
     * added to the node2node model root as C-N-N.
     * 
     * @param source
     * @param target
     * @param params can be <code>null</code>
     */
    public void linkNode(IDataElement source, IDataElement target, Map<String, Object> params) {
        // validate parameters
        if (source == null) {
            throw new IllegalArgumentException("Source is null.");
        }
        Node sourceNode = ((DataElement)source).getNode();
        if (sourceNode == null) {
            throw new IllegalArgumentException("Source node is null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target is null.");
        }
        Node targetNode = ((DataElement)target).getNode();
        if (targetNode == null) {
            throw new IllegalArgumentException("Target node is null.");
        }

        Node proxy1 = getProxy(sourceNode);
        Node proxy2 = getProxy(targetNode);

        if (!related(proxy1, proxy2)) {

            try {
                Relationship rel = dsServ.createRelationship(proxy1, proxy2, relType);

                if (params != null) {
                    dsServ.setProperties(rel, params);
                }
            } catch (DatabaseException e) {
                LOGGER.error("Could not create " + relType.getId() + " relationship.", e);
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
     */
    private Node getProxy(Node sourceNode) {
        Node result = findProxy(sourceNode);
        if (result == null) {
            try {
                result = dsServ.createNode(sourceNode, N2NRelationships.N2N_REL, NodeToNodeTypes.PROXY);
                dsServ.addChild(rootNode, result, null);
            } catch (DatabaseException e) {
                LOGGER.error("Could not create proxy node.", e);
            }
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
        Relationship rel = sourceNode.getSingleRelationship(N2NRelationships.N2N_REL, Direction.OUTGOING);
        if (rel != null) {
            result = rel.getEndNode();
        }
        return result;
    }

    /**
     * Traverses database to find elements connected to the <code>source</code> element with
     * relations, defined by current node2node model.
     * 
     * @param source
     * @return <code>IDataElement</code> traverser
     */
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
            // add relationship property evaluator
            return new DataElementIterable(getConnectedTraversalDescription().relationships(relType, Direction.OUTGOING)
                    .traverse(proxy).nodes());
        } else {
            // TODO: LN: move TraversalDescriptions to Service and make it as Constant, not a method
            return new DataElementIterable(Traversal.description().evaluator(Evaluators.fromDepth(2))
                    .evaluator(Evaluators.toDepth(1)).traverse(sourceNode).nodes());
        }
    }

    // TODO: LN: move TraversalDescriptions to Service
    protected TraversalDescription getConnectedTraversalDescription() {
        return Traversal.description().breadthFirst().relationships(N2NRelationships.N2N_REL, Direction.INCOMING)
                .evaluator(Evaluators.excludeStartPosition()).evaluator(dsServ.new FilterNodesByType(nodeType));
    }

    @Override
    public INodeToNodeRelationsType getNodeToNodeRelationsType() {

        return this.relType;
    }

}