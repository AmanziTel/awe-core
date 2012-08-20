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

package org.amanzi.neo.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicatedNodeException;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeService extends AbstractService implements INodeService {

    /**
     * TODO Purpose of
     * <p>
     * </p>
     * 
     * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
     * @since 1.0.0
     */
    private static final class ChainEvaluator implements Evaluator {
        /** Node node field */
        private final Node node;

        /**
         * @param node
         */
        private ChainEvaluator(final Node node) {
            this.node = node;
        }

        @Override
        public Evaluation evaluate(final Path path) {
            boolean toContinue = true;
            boolean include = true;
            if (path.lastRelationship() != null) {
                Relationship relation = path.lastRelationship();
                if (relation.getStartNode().getId() == node.getId()) {
                    toContinue = relation.isType(NodeServiceRelationshipType.CHILD);
                } else {
                    toContinue = relation.isType(NodeServiceRelationshipType.NEXT);
                }
                include = toContinue;
            } else {
                include = false;
            }

            return Evaluation.of(include, toContinue);
        }
    }

    public enum NodeServiceRelationshipType implements RelationshipType {
        CHILD, NEXT;
    }

    public enum MeasurementRelationshipType implements RelationshipType {
        LOCATION;
    }

    private static final TraversalDescription OUTGOING_LEVEL_1_TRAVERSAL = Traversal.description().breadthFirst()
            .evaluator(Evaluators.atDepth(1));

    private static final TraversalDescription CHAIN_TRAVERSAL = Traversal.description().depthFirst()
            .evaluator(Evaluators.excludeStartPosition()).relationships(NodeServiceRelationshipType.CHILD, Direction.OUTGOING)
            .relationships(NodeServiceRelationshipType.NEXT, Direction.OUTGOING);

    /**
     * @param graphDb
     */
    public NodeService(final GraphDatabaseService graphDb, final IGeneralNodeProperties generalNodeProperties) {
        super(graphDb, generalNodeProperties);
    }

    @Override
    public String getNodeName(final Node node) throws ServiceException {
        return (String)getNodeProperty(node, getGeneralNodeProperties().getNodeNameProperty(), null, true);
    }

    @Override
    public INodeType getNodeType(final Node node) throws ServiceException, NodeTypeNotExistsException {
        String nodeType = (String)getNodeProperty(node, getGeneralNodeProperties().getNodeTypeProperty(), null, true);

        return NodeTypeManager.getInstance().getType(nodeType);
    }

    @Override
    public Node getParent(final Node child, final RelationshipType relationshipType) throws ServiceException {
        assert child != null;

        Node parent = null;

        try {
            Relationship relToParent = child.getSingleRelationship(relationshipType, Direction.INCOMING);

            if (relToParent != null) {
                parent = relToParent.getStartNode();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return parent;
    }

    @Override
    public Node getChildByName(final Node parentNode, final String name, final INodeType nodeType) throws ServiceException {
        return getChildByName(parentNode, name, nodeType, NodeServiceRelationshipType.CHILD);
    }

    @Override
    public Node getChildByName(final Node parentNode, final String name, final INodeType nodeType,
            final RelationshipType relationshipType) throws ServiceException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(name);
        assert nodeType != null;

        Node result = null;

        boolean throwDuplicatedException = false;

        try {
            Iterator<Node> nodes = getChildrenTraversal(nodeType, relationshipType)
                    .evaluator(new PropertyEvaluator(getGeneralNodeProperties().getNodeNameProperty(), name)).traverse(parentNode)
                    .nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        if (throwDuplicatedException) {
            throw new DuplicatedNodeException(getGeneralNodeProperties().getNodeNameProperty(), name);
        }

        return result;
    }

    @Override
    public Node getReferencedNode() throws ServiceException {
        try {
            return getGraphDb().getReferenceNode();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public TraversalDescription getChildrenTraversal(final INodeType nodeType, final RelationshipType relationshipType) {
        return getDownlinkTraversal().relationships(relationshipType, Direction.OUTGOING).evaluator(
                getPropertyEvaluatorForType(nodeType));
    }

    protected TraversalDescription getChildrenTraversal(final RelationshipType relationshipType) {
        return getDownlinkTraversal().relationships(relationshipType, Direction.OUTGOING);
    }

    protected TraversalDescription getChildrenChainTraversal(final INodeType nodeType, final Node node) {
        return getChildrenChainTraversal(node).evaluator(getPropertyEvaluatorForType(nodeType));
    }

    protected TraversalDescription getChildrenChainTraversal(final Node node) {
        return CHAIN_TRAVERSAL.evaluator(new ChainEvaluator(node));
    }

    protected TraversalDescription getDownlinkTraversal() {
        return OUTGOING_LEVEL_1_TRAVERSAL;
    }

    /**
     * Returns a Single Property of Node
     * 
     * @note do not use this method to get multiple properties of a node
     * @note will throw assertion error if throwExceptionIfNotExist is true and defaultValue not
     *       null
     * @param node Node
     * @param propertyName name of Property
     * @param defaultValue default value of Property
     * @param throwExceptionIfNotExist should method throw Exception
     * @return
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Object> T getNodeProperty(final Node node, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(propertyName);

        assert !(throwExceptionIfNotExist && (defaultValue != null));

        boolean throwPropertyNotFoundException = false;

        T result = null;

        try {
            boolean exists = node.hasProperty(propertyName);
            if (throwExceptionIfNotExist && !exists) {
                throwPropertyNotFoundException = true;
            } else {
                if (exists) {
                    result = (T)node.getProperty(propertyName);
                } else {
                    result = (T)node.getProperty(propertyName, defaultValue);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        if (throwPropertyNotFoundException) {
            throw new PropertyNotFoundException(propertyName, node);
        }

        return result;
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert relationshipType != null;

        Map<String, Object> properties = new HashMap<String, Object>();
        return createNode(parentNode, nodeType, relationshipType, properties);
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType,
            final String name) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);
        assert relationshipType != null;

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNode(parentNode, nodeType, relationshipType, properties);
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType,
            final Map<String, Object> parameters) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert parameters != null;
        assert relationshipType != null;

        Transaction tx = getGraphDb().beginTx();

        Node result = null;

        try {
            result = getGraphDb().createNode();

            result.setProperty(getGeneralNodeProperties().getNodeTypeProperty(), nodeType.getId());

            for (Entry<String, Object> property : parameters.entrySet()) {
                result.setProperty(property.getKey(), property.getValue());
            }

            parentNode.createRelationshipTo(result, relationshipType);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    @Override
    public Node getSingleChild(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;

        boolean throwDuplicatedException = false;

        Node result = null;

        try {
            Iterator<Node> nodes = getDownlinkTraversal().relationships(relationshipType, Direction.OUTGOING)
                    .evaluator(getPropertyEvaluatorForType(nodeType)).traverse(parentNode).nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        if (throwDuplicatedException) {
            throw new DuplicatedNodeException("child", nodeType.getId());
        }

        return result;
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType,
            final String name, final Map<String, Object> parameters) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert relationshipType != null;
        assert !StringUtils.isEmpty(name);
        assert parameters != null;

        parameters.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNode(parentNode, nodeType, relationshipType, parameters);
    }

    @Override
    public void updateProperty(final Node node, final String propertyName, final Object newValue) throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(propertyName);
        assert newValue != null;

        boolean shouldWrite = !node.hasProperty(propertyName) || !node.getProperty(propertyName).equals(newValue);

        if (shouldWrite) {
            Transaction tx = getGraphDb().beginTx();

            try {
                node.setProperty(propertyName, newValue);
                tx.success();
            } catch (Exception e) {
                tx.failure();
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        }
    }

    @Override
    public void removeNodeProperty(final Node node, final String propertyName, final boolean throwExceptionIfNotExist)
            throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(propertyName);

        boolean exists = node.hasProperty(propertyName);

        if (exists) {
            Transaction tx = getGraphDb().beginTx();
            try {
                node.removeProperty(propertyName);
                tx.success();
            } catch (Exception e) {
                tx.failure();
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        } else if (throwExceptionIfNotExist) {
            throw new PropertyNotFoundException(propertyName, node);
        }

    }

    @Override
    public void renameNodeProperty(final Node node, final String oldPropertyName, final String newPropertyName,
            final boolean throwExceptionIfNotExist) throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(newPropertyName);
        assert !StringUtils.isEmpty(oldPropertyName);

        Object value = getNodeProperty(node, oldPropertyName, null, false);

        if (value != null) {
            removeNodeProperty(node, oldPropertyName, false);
            updateProperty(node, newPropertyName, value);
        } else if (throwExceptionIfNotExist) {
            throw new PropertyNotFoundException(oldPropertyName, node);
        }
    }

    @Override
    public Iterator<Node> getChildren(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType)
            throws ServiceException {
        assert parentNode != null;

        try {
            return getChildrenTraversal(nodeType, relationshipType).traverse(parentNode).nodes().iterator();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Iterator<Node> getChildren(final Node parentNode, final INodeType nodeType) throws ServiceException {
        return getChildren(parentNode, nodeType, NodeServiceRelationshipType.CHILD);
    }

    @Override
    public Iterator<Node> getChildren(final Node parentNode, final RelationshipType relationshipType) throws ServiceException {
        assert parentNode != null;

        try {
            return getChildrenTraversal(relationshipType).traverse(parentNode).nodes().iterator();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Iterator<Node> getChildren(final Node parentNode) throws ServiceException {
        return getChildren(parentNode, NodeServiceRelationshipType.CHILD);
    }

    @Override
    public Iterator<Node> getChildrenChain(final Node parentNode) throws ServiceException {
        assert parentNode != null;

        try {
            return getChildrenChainTraversal(parentNode).traverse(parentNode).nodes().iterator();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    protected Node addToChain(final Node parentNode, final Node childNode) throws ServiceException {
        RelationshipType relationType = NodeServiceRelationshipType.CHILD;
        Node previousNode = parentNode;

        Long lastChildId = getNodeProperty(parentNode, getGeneralNodeProperties().getLastChildID(), null, false);

        Transaction tx = getGraphDb().beginTx();
        try {
            if (lastChildId != null) {
                previousNode = getGraphDb().getNodeById(lastChildId);
                relationType = NodeServiceRelationshipType.NEXT;
            }

            previousNode.createRelationshipTo(childNode, relationType);
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        updateProperty(parentNode, getGeneralNodeProperties().getLastChildID(), childNode.getId());
        updateProperty(childNode, getGeneralNodeProperties().getParentIDProperty(), parentNode.getId());

        return childNode;
    }

    @Override
    public Node getChildInChainByName(final Node parentNode, final String name, final INodeType nodeType) throws ServiceException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(name);
        assert nodeType != null;

        Node result = null;

        boolean throwDuplicatedException = false;

        try {
            Iterator<Node> nodes = getChildrenChainTraversal(nodeType, parentNode)
                    .evaluator(new PropertyEvaluator(getGeneralNodeProperties().getNodeNameProperty(), name)).traverse(parentNode)
                    .nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        if (throwDuplicatedException) {
            throw new DuplicatedNodeException(getGeneralNodeProperties().getNodeNameProperty(), name);
        }

        return result;
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;

        Map<String, Object> properties = new HashMap<String, Object>();
        return createNodeInChain(parentNode, nodeType, properties);
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType, final String name) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNodeInChain(parentNode, nodeType, properties);
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType, final Map<String, Object> parameters)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert parameters != null;

        Transaction tx = getGraphDb().beginTx();

        Node result = null;

        try {
            result = getGraphDb().createNode();

            result.setProperty(getGeneralNodeProperties().getNodeTypeProperty(), nodeType.getId());

            for (Entry<String, Object> property : parameters.entrySet()) {
                result.setProperty(property.getKey(), property.getValue());
            }

            addToChain(parentNode, result);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType, final String name,
            final Map<String, Object> parameters) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);
        assert parameters != null;

        parameters.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNodeInChain(parentNode, nodeType, parameters);
    }

    /*
     * (non-Javadoc)
     * @see org.amanzi.neo.services.INodeService#linkNodes(org.neo4j.graphdb.Node,
     * org.neo4j.graphdb.Node, org.neo4j.graphdb.RelationshipType)
     */
    @Override
    public void linkNodes(final Node startNode, final Node endNode, final RelationshipType relationshipType)
            throws ServiceException {
        assert startNode != null;
        assert endNode != null;
        assert relationshipType != null;

        Transaction tx = getGraphDb().beginTx();
        try {
            startNode.createRelationshipTo(endNode, relationshipType);
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    @Override
    public Node getChainParent(final Node node) throws ServiceException {
        assert node != null;

        long parentId = getNodeProperty(node, getGeneralNodeProperties().getParentIDProperty(), null, true);

        return getGraphDb().getNodeById(parentId);
    }
}
