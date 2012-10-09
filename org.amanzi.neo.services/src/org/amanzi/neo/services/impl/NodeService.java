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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.neo4j.graphdb.PropertyContainer;
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
                final Relationship relation = path.lastRelationship();
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

    private static final TraversalDescription OUTGOUIN_NODES_TRAVERSAL = Traversal.description().breadthFirst();

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
        final String nodeType = (String)getNodeProperty(node, getGeneralNodeProperties().getNodeTypeProperty(), null, true);

        return NodeTypeManager.getInstance().getType(nodeType);
    }

    @Override
    public Node getParent(final Node child, final RelationshipType relationshipType) throws ServiceException {
        assert child != null;

        Node parent = null;

        try {
            final Relationship relToParent = child.getSingleRelationship(relationshipType, Direction.INCOMING);

            if (relToParent != null) {
                parent = relToParent.getStartNode();
            }
        } catch (final Exception e) {
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
            final Iterator<Node> nodes = getChildrenTraversal(nodeType, relationshipType)
                    .evaluator(new PropertyEvaluator(getGeneralNodeProperties().getNodeNameProperty(), name)).traverse(parentNode)
                    .nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (final Exception e) {
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
        } catch (final Exception e) {
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

    @Override
    public TraversalDescription getChildrenChainTraversal(final Node node) {
        return CHAIN_TRAVERSAL.evaluator(new ChainEvaluator(node));
    }

    protected TraversalDescription getDownlinkTraversal() {
        return OUTGOING_LEVEL_1_TRAVERSAL;
    }

    protected TraversalDescription getAllDownlinkTraversal() {
        return OUTGOUIN_NODES_TRAVERSAL;
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
    @Override
    public <T extends Object> T getNodeProperty(final Node node, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException {
        return getContainerProperty(node, propertyName, defaultValue, throwExceptionIfNotExist);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Object> T getContainerProperty(final PropertyContainer container, final String propertyName,
            final T defaultValue, final boolean throwExceptionIfNotExist) throws ServiceException {
        assert container != null;
        assert !StringUtils.isEmpty(propertyName);

        assert !(throwExceptionIfNotExist && (defaultValue != null));

        boolean throwPropertyNotFoundException = false;

        T result = null;

        try {
            final boolean exists = container.hasProperty(propertyName);
            if (throwExceptionIfNotExist && !exists) {
                throwPropertyNotFoundException = true;
            } else {
                if (exists) {
                    result = (T)container.getProperty(propertyName);
                } else {
                    result = (T)container.getProperty(propertyName, defaultValue);
                }
            }
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }

        if (throwPropertyNotFoundException) {
            throw new PropertyNotFoundException(propertyName, container);
        }

        return result;
    }

    @Override
    public <T> T getRelationshipProperty(final Relationship relationship, final String propertyName, final T defaultValue,
            final boolean throwExceptionIfNotExist) throws ServiceException {
        return getContainerProperty(relationship, propertyName, defaultValue, throwExceptionIfNotExist);
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert relationshipType != null;

        final Map<String, Object> properties = new HashMap<String, Object>();
        return createNode(parentNode, nodeType, relationshipType, properties);
    }

    @Override
    public Node createNode(final Node parentNode, final INodeType nodeType, final RelationshipType relationshipType,
            final String name) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);
        assert relationshipType != null;

        final Map<String, Object> properties = new HashMap<String, Object>();
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

        final Transaction tx = getGraphDb().beginTx();

        Node result = null;

        try {
            result = getGraphDb().createNode();

            result.setProperty(getGeneralNodeProperties().getNodeTypeProperty(), nodeType.getId());

            for (final Entry<String, Object> property : parameters.entrySet()) {
                result.setProperty(property.getKey(), property.getValue());
            }

            parentNode.createRelationshipTo(result, relationshipType);

            tx.success();
        } catch (final Exception e) {
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
            final Iterator<Node> nodes = getDownlinkTraversal().relationships(relationshipType, Direction.OUTGOING)
                    .evaluator(getPropertyEvaluatorForType(nodeType)).traverse(parentNode).nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (final Exception e) {
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
        updateContainerPropety(node, propertyName, newValue);
    }

    protected void updateContainerPropety(final PropertyContainer container, final String propertyName, final Object newValue)
            throws DatabaseException {
        assert container != null;
        assert !StringUtils.isEmpty(propertyName);
        assert newValue != null;

        final boolean shouldWrite = !container.hasProperty(propertyName) || !container.getProperty(propertyName).equals(newValue);

        if (shouldWrite) {
            final Transaction tx = getGraphDb().beginTx();

            try {
                container.setProperty(propertyName, newValue);
                tx.success();
            } catch (final Exception e) {
                tx.failure();
                throw new DatabaseException(e);
            } finally {
                tx.finish();
            }
        }
    }

    @Override
    public void updateProperty(final Relationship relationship, final String propertyName, final Object newValue)
            throws ServiceException {
        updateContainerPropety(relationship, propertyName, newValue);
    }

    @Override
    public void removeNodeProperty(final Node node, final String propertyName, final boolean throwExceptionIfNotExist)
            throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(propertyName);

        final boolean exists = node.hasProperty(propertyName);

        if (exists) {
            final Transaction tx = getGraphDb().beginTx();
            try {
                node.removeProperty(propertyName);
                tx.success();
            } catch (final Exception e) {
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

        final Object value = getNodeProperty(node, oldPropertyName, null, false);

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
        } catch (final Exception e) {
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
        } catch (final Exception e) {
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
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }
    }

    protected Node addToChain(final Node parentNode, final Node childNode) throws ServiceException {
        RelationshipType relationType = NodeServiceRelationshipType.CHILD;
        Node previousNode = parentNode;

        final Long lastChildId = getNodeProperty(parentNode, getGeneralNodeProperties().getLastChildID(), null, false);

        final Transaction tx = getGraphDb().beginTx();
        try {
            if (lastChildId != null) {
                previousNode = getGraphDb().getNodeById(lastChildId);
                relationType = NodeServiceRelationshipType.NEXT;
            }

            previousNode.createRelationshipTo(childNode, relationType);
            tx.success();
        } catch (final Exception e) {
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
            final Iterator<Node> nodes = getChildrenChainTraversal(nodeType, parentNode)
                    .evaluator(new PropertyEvaluator(getGeneralNodeProperties().getNodeNameProperty(), name)).traverse(parentNode)
                    .nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (final Exception e) {
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

        final Map<String, Object> properties = new HashMap<String, Object>();
        return createNodeInChain(parentNode, nodeType, properties);
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType, final String name) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNodeInChain(parentNode, nodeType, properties);
    }

    @Override
    public Node createNodeInChain(final Node parentNode, final INodeType nodeType, final Map<String, Object> parameters)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert parameters != null;

        final Transaction tx = getGraphDb().beginTx();

        Node result = null;

        try {
            result = getGraphDb().createNode();

            result.setProperty(getGeneralNodeProperties().getNodeTypeProperty(), nodeType.getId());

            for (final Entry<String, Object> property : parameters.entrySet()) {
                result.setProperty(property.getKey(), property.getValue());
            }

            addToChain(parentNode, result);

            tx.success();
        } catch (final Exception e) {
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
    public Relationship linkNodes(final Node startNode, final Node endNode, final RelationshipType relationshipType)
            throws ServiceException {
        assert startNode != null;
        assert endNode != null;
        assert relationshipType != null;

        Relationship result = null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            result = startNode.createRelationshipTo(endNode, relationshipType);
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    @Override
    public Node getChainParent(final Node node) throws ServiceException {
        assert node != null;

        final long parentId = getNodeProperty(node, getGeneralNodeProperties().getParentIDProperty(), null, true);

        return getGraphDb().getNodeById(parentId);
    }

    @Override
    public Iterator<Node> getChildrenChain(final Node parentNode, final INodeType nodeType) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;

        try {
            return CHAIN_TRAVERSAL
                    .evaluator(new PropertyEvaluator(getGeneralNodeProperties().getNodeTypeProperty(), nodeType.getId()))
                    .traverse(parentNode).nodes().iterator();
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteChain(final Node root) throws DatabaseException {
        assert root != null;
        final Transaction tx = getGraphDb().beginTx();
        try {
            deleteNode(root);
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * @param root
     */
    private void deleteNode(final Node root) {
        final List<Node> nextNode = new ArrayList<Node>();
        for (final Relationship rel : root.getRelationships(Direction.OUTGOING)) {
            nextNode.add(rel.getOtherNode(root));
            rel.delete();
        }
        for (final Relationship rel : root.getRelationships()) {
            rel.delete();
        }
        root.delete();
        for (final Node node : nextNode) {
            deleteNode(node);
        }
    }

    @Override
    public Iterator<Node> getAllChildren(final Node node) throws ServiceException {
        return getAllDownlinkTraversal().traverse(node).nodes().iterator();
    }

    @Override
    public void deleteSingleNode(final Node node) throws ServiceException {
        final Transaction tx = getGraphDb().beginTx();
        try {
            for (final Relationship relationship : node.getRelationships()) {
                relationship.delete();
            }
            System.out.println(node.getRelationships());
            node.delete();
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

    }

    @Override
    public Relationship findLinkBetweenNodes(final Node startNode, final Node endNode, final RelationshipType relationshipType,
            final Direction direction) throws ServiceException {
        assert startNode != null;
        assert endNode != null;
        assert relationshipType != null;
        assert direction != null;
        assert !startNode.equals(endNode);

        Relationship result = null;

        try {
            for (final Relationship singleRelation : startNode.getRelationships(direction, relationshipType)) {
                if (singleRelation.getOtherNode(startNode).equals(endNode)) {
                    result = singleRelation;
                    break;
                }
            }
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }

        return result;
    }

    @Override
    public void deleteRelationship(final Relationship relation) throws ServiceException {
        assert relation != null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            relation.delete();

            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }
}
