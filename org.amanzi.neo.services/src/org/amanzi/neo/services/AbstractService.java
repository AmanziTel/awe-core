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

package org.amanzi.neo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * New implementation of base class for services
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractService implements IDatabaseEventListener {
    public final static String TYPE = "type";
    public final static String NAME = "name";
    public static final String DATASET_ID = "dataset";
    public static final String NETWORK_ID = "network";

    private static Logger LOGGER = Logger.getLogger(AbstractService.class);
    /**
     * Traversal description for child elements
     */
    protected final TraversalDescription CHILD_ELEMENT_TRAVERSAL_DESCRIPTION = Traversal.description().depthFirst()
            .relationships(DatasetRelationTypes.CHILD, Direction.OUTGOING);
    protected GraphDatabaseService graphDb;
    
    private List<String> indexedProperties = new ArrayList<String>();
    
    private void fillIndexedProperties() {
        indexedProperties.add(AbstractService.NAME);
        indexedProperties.add(NetworkService.BCCH);
        indexedProperties.add(NetworkService.BSIC);
        indexedProperties.add(NetworkService.CELL_INDEX);
        indexedProperties.add(NetworkService.LOCATION_AREA_CODE);;
    }

    /**
     * Sets service to use default <code>GraphDatabaseService</code> of the running application
     */
    public AbstractService() {
        // TODO: get database service
        graphDb = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
        DatabaseManagerFactory.getDatabaseManager().addDatabaseEventListener(this);
        fillIndexedProperties();
    }

    /**
     * Method show whether is property in indexed property
     *
     * @param name Indexed name of property
     * @return
     */
    public boolean isIndexedProperties(String name) {
        return indexedProperties.contains(name);
    }
    
    public static String getNodeType(Node node) {
        return (String)node.getProperty(TYPE, StringUtils.EMPTY);
    }

    /**
     * Creates a node and sets it's type property
     * 
     * @param nodeType - the new node type
     * @return - the newly created node
     */
    public Node createNode(INodeType nodeType) throws DatabaseException {
        // validate parameters
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }
        // create node
        Node result = null;
        Transaction tx = graphDb.beginTx();
        try {
            result = graphDb.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, nodeType.getId());
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * Manage index names for current model.
     * 
     * @param type the type of node to index
     * @param rootNode Root-node
     * @return the index name
     * @throws DatabaseException
     */
    private Index<Node> getIndex(INodeType type, Node rootNode, Map<INodeType, Index<Node>> indexMap) throws DatabaseException {

        Index<Node> result = indexMap.get(type.getId());
        if (result == null) {
            result = getIndex(rootNode, type);
            if (result != null) {
                indexMap.put(type, result);
            }
        }
        return result;
    }

    /**
     * Method delete node and all sub-nodes if they related with CHILD-relationships
     * 
     * @param nodeToDelete Node which need delete
     * @throws DatabaseException
     * @throws InvalidStatisticsParameterException
     */
    public void deleteOneNode(Node nodeToDelete, Node rootNode, Map<INodeType, Index<Node>> indexMap) throws AWEException {
        LOGGER.debug("start method deleteOneNode(Node nodeToDelete)");

        if (nodeToDelete == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter nodeToDelete = null");
            throw new InvalidStatisticsParameterException("nodeToDelete", nodeToDelete);
        }

        Transaction tx = graphDb.beginTx();
        try {
            Iterable<Relationship> childRelationships = nodeToDelete.getRelationships(Direction.OUTGOING);

            for (Relationship rel : childRelationships) {
                Node childNode = rel.getEndNode();
                if (childNode != null) {
                    rel.delete();
                    removeIndexFromNode(childNode, rootNode, indexMap);
                    childNode.delete();
                }
            }
            Iterable<Relationship> incomingRelationships = nodeToDelete.getRelationships(Direction.INCOMING);
            for (Relationship incomingRelationship : incomingRelationships) {
                incomingRelationship.delete();
            }
            removeIndexFromNode(nodeToDelete, rootNode, indexMap);
            nodeToDelete.delete();
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not delete node from database", e);
            tx.failure();
            throw new DatabaseException(e);

        } finally {
            tx.finish();

        }
        LOGGER.debug("finish method deleteOneNode(Node nodeToDelete)");
    }

    public Node createNode(Map<String, Object> params) throws DatabaseException {
        INodeType type = NodeTypeManager.getType(params.get(TYPE).toString());
        Node result = createNode(type);
        setProperties(result, params);
        return result;
    }

    /**
     * Creates a node of the defined <code>nodeType</code>, creates a relationship of type
     * <code>relType</code> from <code>parent</code> to the resulting node.
     * 
     * @param parent
     * @param relType
     * @param nodeType
     * @return the new node
     * @throws DatabaseException
     */
    public Node createNode(Node parent, RelationshipType relType, INodeType nodeType) throws DatabaseException {
        // TODO:test
        // validate parameters
        if (relType == null) {
            throw new IllegalArgumentException("Relationship type is null.");
        }
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }

        Node result = createNode(nodeType);
        Transaction tx = graphDb.beginTx();
        try {
            parent.createRelationshipTo(result, relType);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return result;
    }

    public Relationship createRelationship(Node parent, Node child, RelationshipType relType) throws DatabaseException {
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child is null.");
        }
        if (relType == null) {
            throw new IllegalArgumentException("Relationship type is null.");
        }
        Relationship result = null;
        Transaction tx = graphDb.beginTx();
        try {
            result = parent.createRelationshipTo(child, relType);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * This method generates a string identifier for index for a specific network and a specific
     * type of nodes
     * 
     * @param root a model root
     * @param nodeType type of nodes
     * @return a string specifying index name
     */
    public static String getIndexKey(Node root, INodeType nodeType) {
        // validate parameters
        if (root == null) {
            throw new IllegalArgumentException("Root cannot be null");
        }
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type cannot be null");
        }

        return "" + root.getId() + "@" + nodeType.getId();
    }

    /**
     * Generates a node index for the defined <code>root</code> node and <code>nodeType</code>. Your
     * code should call this method only once for each pair of parameters, to minimize calls to
     * database.
     * 
     * @param root a model root
     * @param nodeType type of nodes
     * @return <code>Index<Node></code> for the defined parameters
     * @throws DatabaseException
     */
    public Index<Node> getIndex(Node root, INodeType nodeType) throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        Index<Node> result = null;
        try {
            result = graphDb.index().forNodes(getIndexKey(root, nodeType));
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * Sets any property at <code>node</code>
     *
     * @param node
     * @param propertyName Name of property
     * @param propertyValue Value of property
     * @throws IllegalNodeDataException Exception throws if propertyName is null or empty
     * @throws DatabaseException
     */
    public void setAnyProperty(Node node, String propertyName, Object propertyValue) throws IllegalNodeDataException, DatabaseException {
        // validate parameters
        if ((propertyName == null) || propertyName.equals(StringUtils.EMPTY)) {
            throw new IllegalNodeDataException(propertyName + " cannot be empty.");
        }
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        Transaction tx = graphDb.beginTx();
        try {
            node.setProperty(propertyName, propertyValue);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set " + propertyName, e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Adds <code>node</code> to <code> indexName</code>. Does not validate arguments.
     * 
     * @param node
     * @param indexName
     * @param propertyName
     * @param propertyValue
     * @throws DatabaseException if something went wrong
     */
    public Index<Node> addNodeToIndex(Node node, String indexName, String propertyName, Object propertyValue)
            throws DatabaseException {
        Index<Node> index = null;
        Transaction tx = graphDb.beginTx();
        try {
            if (!indexedProperties.contains(propertyName)) {
                indexedProperties.add(propertyName);
            }
            index = graphDb.index().forNodes(indexName);
            index.add(node, propertyName, propertyValue);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return index;
    }

    /**
     * Adds <code>node</code> to <code> indexName</code>. Does not validate arguments.
     * 
     * @param node
     * @param indexName
     * @param propertyName
     * @param propertyValue
     * @throws DatabaseException if something went wrong
     */
    public Index<Node> removeNodeFromIndex(Node node, Index<Node> index, String propertyName, Object propertyValue)
            throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        try {
            index.remove(node, propertyName, propertyValue);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return index;
    }

    private void removeIndexFromNode(Node node, Node rootNode, Map<INodeType, Index<Node>> indexMap) throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        try {
            INodeType nodeType = NodeTypeManager.getType(node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString());
            Index<Node> index = getIndex(nodeType, rootNode, indexMap);
            index.remove(node, NAME, node.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    public Index<Node> addNodeToIndex(Node node, Index<Node> index, String propertyName, Object propertyValue)
            throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        try {
            if (!indexedProperties.contains(propertyName)) {
                indexedProperties.add(propertyName);
            }
            index.add(node, propertyName, propertyValue);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return index;
    }

    /**
     * <p>
     * An evaluator that filters nodes with defined name and type. To be used in traversals
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public class NameTypeEvaluator extends FilterNodesByType {
        private String name;

        /**
         * Constructor
         * 
         * @param name
         * @param type
         */
        public NameTypeEvaluator(String name, INodeType type) {
            super(type);
            this.name = name;

        }

        @Override
        public Evaluation evaluate(Path path) {
            if (super.evaluate(path).includes()) {
                if (path.endNode().getProperty(AbstractService.NAME, StringUtils.EMPTY).equals(name)) {
                    return Evaluation.INCLUDE_AND_CONTINUE;
                }
                return Evaluation.EXCLUDE_AND_CONTINUE;
            }

            return Evaluation.EXCLUDE_AND_CONTINUE;

        }
    }

    /**
     * <p>
     * this class choose nodes by type
     * </p>
     * 
     * @author Kruglik_A
     * @since 1.0.0
     */
    public static class FilterNodesByType implements Evaluator {
        /**
         * constructor for filter nodes by type
         * 
         * @param type - nodes type
         */
        public FilterNodesByType(INodeType type) {
            this.type = type;
        }

        private INodeType type;

        @Override
        public Evaluation evaluate(Path arg0) {
            boolean includes = false;
            if (getNodeType(arg0.endNode()).equals(type.getId()))
                includes = true;
            return Evaluation.ofIncludes(includes);
        }

    }

    /**
     * Sets the <code>node</code> properties, using keys and values from <code>params</code> map.
     * Property is not set, if its value is <code>null</code>. Property values should be of types,
     * that are accepted by the database (primitives or <code>String</code>).
     * 
     * @param dbElement the object to set properties (<code>Node</code> or <code>Relationship</code>
     *        )
     * @param params notice that you may also pass a <code>DataElement</code> object.
     * @throws DatabaseException
     */
    public void setProperties(PropertyContainer dbElement, Map<String, Object> params) throws DatabaseException {
        // validate
        if (dbElement == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Data element is null.");
        }
        Transaction tx = graphDb.beginTx();
        try {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    dbElement.setProperty(key, value);
                }
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set node properties.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * This method looks through <code>Direction.OUTGOING</code> relationships with TYPE
     * <code>relType</code> of <code>parent</code>, to find a node with the defined NAME and TYPE.
     * 
     * @param parent
     * @param relType
     * @param name
     * @param nodeType
     * @return the found node or <code>null</code>
     */
    public Node findNode(Node parent, RelationshipType relType, String name, INodeType nodeType) {
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
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

        Node result = null;
        for (Relationship rel : parent.getRelationships(relType, Direction.OUTGOING)) {
            Node node = rel.getEndNode();
            if ((name.equals(node.getProperty(NAME, StringUtils.EMPTY))) && (nodeType.getId().equals(node.getProperty(TYPE, null)))) {
                result = node;
                break;
            }
        }
        return result;
    }

    /**
     * Safely get a node that is linked to <code>startNode</code> with the defined relationship.
     * Assumed, that <code>startNode</code> has only one relationship of that kind
     * 
     * @param startNode
     * @param relationship
     * @param direction
     * @return the node on the other end of relationship from <code>startNode</code>
     * @throws DatabaseException if there are more than one relationships
     */
    protected Node getNextNode(Node startNode, RelationshipType relationship, Direction direction) throws DatabaseException {
        Node result = null;

        Iterator<Relationship> rels = startNode.getRelationships(relationship, direction).iterator();
        if (rels.hasNext()) {
            result = rels.next().getOtherNode(startNode);
        }
        if (rels.hasNext()) {
            // result is ambiguous
            throw new DatabaseException("Errors exist in database structure");
        }

        return result;
    }

    @Override
    public void onDatabaseEvent(DatabaseEvent event) {
        switch (event.getEventType()) {
        case BEFORE_SHUTDOWN:
            graphDb = null;
            break;
        case AFTER_STARTUP:
            graphDb = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
            break;
        }
    }
}
