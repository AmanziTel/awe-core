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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
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

    public enum NodeServiceRelationshipType implements RelationshipType {
        CHILD;
    }

    private static final TraversalDescription OUTGOING_LEVEL_1_TRAVERSAL = Traversal.description().breadthFirst()
            .evaluator(Evaluators.atDepth(1));

    private static final TraversalDescription CHILDREN_TRAVERSAL = OUTGOING_LEVEL_1_TRAVERSAL.relationships(
            NodeServiceRelationshipType.CHILD, Direction.OUTGOING);

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
    public INodeType getNodeType(final Node node) throws ServiceException {
        String nodeType = (String)getNodeProperty(node, getGeneralNodeProperties().getNodeTypeProperty(), null, true);

        return NodeTypeManager.getInstance().getType(nodeType);
    }

    @Override
    public Node getParent(final Node child) throws ServiceException {
        assert child != null;

        Node parent = null;

        try {
            Relationship relToParent = child.getSingleRelationship(NodeServiceRelationshipType.CHILD, Direction.INCOMING);

            if (relToParent != null) {
                parent = relToParent.getStartNode();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return parent;
    }

    @Override
    public Iterator<Node> getChildren(final Node parentNode) throws ServiceException {
        assert parentNode != null;
        try {
            return getChildrenTraversal().traverse(parentNode).nodes().iterator();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Node getChildByName(final Node parentNode, final String name, final INodeType nodeType) throws ServiceException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(name);
        assert nodeType != null;

        Node result = null;

        boolean throwDuplicatedException = false;

        try {
            Iterator<Node> nodes = getChildrenTraversal().evaluator(getPropertyEvaluatorForType(nodeType))
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

    protected TraversalDescription getChildrenTraversal() {
        return CHILDREN_TRAVERSAL;
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

        assert throwExceptionIfNotExist || (defaultValue != null);

        boolean throwPropertyNotFoundException = false;

        T result = null;

        try {
            if (throwExceptionIfNotExist && !node.hasProperty(propertyName)) {
                throwPropertyNotFoundException = true;
            } else {
                if (defaultValue == null) {
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
    public Node getSingleChild(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException {
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
    public Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name,
            Map<String, Object> parameters) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert relationshipType != null;
        assert !StringUtils.isEmpty(name);
        assert parameters != null;

        parameters.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNode(parentNode, nodeType, relationshipType, parameters);
    }

    @Override
    public void updateProperty(Node node, String propertyName, Object newValue) throws ServiceException {
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
    public void removeNodeProperty(Node node, String propertyName, boolean throwExceptionIfNotExists) throws ServiceException {
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
        } else if (throwExceptionIfNotExists) {
            throw new PropertyNotFoundException(propertyName, node);
        }

    }

    @Override
    public void renameNodeProperty(Node node, String oldPropertyName, String newPropertyName, boolean throwExceptionIfNotExists)
            throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(newPropertyName);
        assert !StringUtils.isEmpty(oldPropertyName);
    }
}
