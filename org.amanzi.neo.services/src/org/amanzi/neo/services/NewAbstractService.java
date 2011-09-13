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

import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * <p>
 * New implementation of base class for services
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class NewAbstractService {
    public final static String TYPE = "type";
    public final static String NAME = "name";

    private static Logger LOGGER = Logger.getLogger(NewAbstractService.class);

    protected GraphDatabaseService graphDb;
    private Transaction tx;

    /**
     * Sets service to use default <code>GraphDatabaseService</code> of the running application
     */
    public NewAbstractService() {
        // TODO: get database service
        graphDb = NeoServiceProvider.getProvider().getService();
    }

    /**
     * Sets service to use the defined <code>GraphDatabaseService</code>
     * 
     * @param graphDb - <code>GraphDatabaseService</code> to use
     */
    public NewAbstractService(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public String getNodeType(Node node) {
        return (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "");
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
        tx = graphDb.beginTx();
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
     * This method generates a string identifier for index for a specific network and a specific
     * type of nodes
     * 
     * @param root a network node
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
     * Sets NAME property at <code>node</code>
     * 
     * @param node
     * @param name
     * @throws IllegalNodeDataException if name is null or empty
     */
    protected void setNameProperty(Node node, String name) throws IllegalNodeDataException, DatabaseException {
        // validate parameters
        if ((name == null) || name.equals("")) {
            throw new IllegalNodeDataException("Name cannot be empty.");
        }
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        tx = graphDb.beginTx();
        try {
            node.setProperty(NAME, name);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set name", e);
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
    protected void addNodeToIndex(Node node, String indexName, String propertyName, Object propertyValue) throws DatabaseException {
        tx = graphDb.beginTx();
        try {
            Index<Node> index = graphDb.index().forNodes(indexName);
            index.add(node, propertyName, propertyValue);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not index node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
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
                if (path.endNode().getProperty(NewAbstractService.NAME, "").equals(name)) {
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
    public class FilterNodesByType implements Evaluator {
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
     * @param node
     * @param element
     * @throws DatabaseException
     */
    public void setProperties(Node node, Map<String, Object> element) throws DatabaseException {
        // validate
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        if (element == null) {
            throw new IllegalArgumentException("Data element is null.");
        }
        tx = graphDb.beginTx();
        try {
            for (String key : ((DataElement)element).keySet()) {
                node.setProperty(key, element.get(key));
            }
            tx.success();
        } catch (Exception e) {
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

}
