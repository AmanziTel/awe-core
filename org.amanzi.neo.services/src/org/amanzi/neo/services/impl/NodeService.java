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

    private static final TraversalDescription CHILDREN_TRAVERSAL = Traversal.description().breadthFirst()
            .relationships(NodeServiceRelationshipType.CHILD, Direction.OUTGOING).evaluator(Evaluators.atDepth(1));

    /**
     * @param graphDb
     */
    public NodeService(GraphDatabaseService graphDb, IGeneralNodeProperties generalNodeProperties) {
        super(graphDb, generalNodeProperties);
    }

    @Override
    public String getNodeName(Node node) throws ServiceException {
        return (String)getNodeProperty(node, getGeneralNodeProperties().getNodeNameProperty(), null, true);
    }

    @Override
    public INodeType getNodeType(Node node) throws ServiceException {
        String nodeType = (String)getNodeProperty(node, getGeneralNodeProperties().getNodeTypeProperty(), null, true);

        return NodeTypeManager.getType(nodeType);
    }

    @Override
    public Node getParent(Node child) throws ServiceException {
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
    public Iterator<Node> getChildren(Node parentNode) throws ServiceException {
        assert parentNode != null;
        try {
            return getChildrenTraversal().traverse(parentNode).nodes().iterator();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Node getChildByName(Node parentNode, String name, INodeType nodeType) throws ServiceException {
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
    private Object getNodeProperty(Node node, String propertyName, String defaultValue, boolean throwExceptionIfNotExist)
            throws ServiceException {
        assert node != null;
        assert !StringUtils.isEmpty(propertyName);

        assert throwExceptionIfNotExist && (defaultValue == null);

        boolean throwPropertyNotFoundException = false;

        Object result = null;

        try {
            if (throwExceptionIfNotExist && !node.hasProperty(propertyName)) {
                throwPropertyNotFoundException = true;
            } else {
                result = node.getProperty(propertyName, defaultValue);
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
    public Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType) throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert relationshipType != null;

        Map<String, Object> properties = new HashMap<String, Object>();
        return createNode(parentNode, nodeType, relationshipType, properties);
    }

    @Override
    public Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, String name)
            throws ServiceException {
        assert parentNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(name);
        assert relationshipType != null;

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(getGeneralNodeProperties().getNodeNameProperty(), name);

        return createNode(parentNode, nodeType, relationshipType, properties);
    }

    @Override
    public Node createNode(Node parentNode, INodeType nodeType, RelationshipType relationshipType, Map<String, Object> parameters)
            throws ServiceException {
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
}
