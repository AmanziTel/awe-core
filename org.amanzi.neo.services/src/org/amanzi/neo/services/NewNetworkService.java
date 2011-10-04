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

import java.util.Iterator;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * This class manages access to network elements
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkService extends NewAbstractService {

    public final static String CELL_INDEX = "ci";
    public final static String LOCATION_AREA_CODE = "lac";

    public final static String SELECTED_NODES_COUNT = "selected_nodes_count";

    private static Logger LOGGER = Logger.getLogger(NewNetworkService.class);

    private NewDatasetService datasetService;

    /**
     * <p>
     * This enum describes types of network elements.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum NetworkElementNodeType implements INodeType {
        NETWORK, BSC, SITE, SECTOR, CITY, MSC, SELECTION_LIST_ROOT;

        static {
            NodeTypeManager.registerNodeType(NetworkElementNodeType.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
        }

    }

    /**
     * Enum with RelationshipTypes specific for Network Structure
     * 
     * @author gerzog
     * @since 1.0.0
     */
    public enum NetworkRelationshipTypes implements RelationshipType {
        SELECTION_LIST;
    }

    /*
     * Traversal Description to find out all Selection List Nodes
     */
    protected final static TraversalDescription ALL_SELECTION_LISTS_TRAVERSER = Traversal.description().breadthFirst()
            .relationships(NetworkRelationshipTypes.SELECTION_LIST).evaluator(Evaluators.excludeStartPosition());

    public NewNetworkService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
    }

    public NewNetworkService(GraphDatabaseService graphDb) {
        super(graphDb);
        datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
    }

    /**
     * Creates a new network element of defined <code>elementType</code>, sets its <code>name</code>
     * property, adds this element to index <code>indexName</code>, and attaches this element to
     * <code>parent</code> node.
     * 
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return the newly created network element node
     */
    public Node createNetworkElement(Node parent, Index<Node> index, String name, INodeType elementType)
            throws IllegalNodeDataException, DatabaseException {
        LOGGER.debug("start createNetworkElement(Node parent, String indexName, String name, INodeType elementType)");

        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (index == null) {
            throw new IllegalArgumentException("Index is null.");
        }
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalNodeDataException("Name cannot be empty");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (elementType.equals(NetworkElementNodeType.SECTOR)) {
            throw new IllegalArgumentException("To create a sector use method createSector()");
        }

        Transaction tx = graphDb.beginTx();
        Node result = null;
        try {
            result = createNode(elementType);
            datasetService.addChild(parent, result);
            setNameProperty(result, name);
            addNodeToIndex(result, index, NAME, name);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create network element.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * Finds a network element by <code>name</code> in index <code>indexName</code>
     * 
     * @param indexName name of index, where to look for the element. Call
     *        {@link NewAbstractService#getIndexKey(Node, INodeType)} to generate this name.
     * @param name the value of NAME property to look for
     * @return a network element node or <code>null</code>, if nothing found
     */
    public Node findNetworkElement(Index<Node> index, String name) {
        LOGGER.debug("start findNetworkElement(String indexName, String name)");

        // validate parameters
        if (index == null) {
            throw new IllegalArgumentException("Index is null.");
        }
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Find element by index
        Node result = index.get(NAME, name).getSingle();
        return result;
    }

    /**
     * Looks for a network element in <code>indexName</code> by <code>name</code>, or creates a new
     * network element and attaches it to <code>parent</code> node, and adds to index
     * 
     * @param parent is used only if the element is not found in index
     * @param indexName name of index
     * @param name the value of indexed NAME property
     * @param elementType is used only if element was not found
     * @return found or created node
     */
    public Node getNetworkElement(Node parent, Index<Node> index, String name, INodeType elementType)
            throws IllegalNodeDataException, DatabaseException {
        LOGGER.debug("start getNetworkElement(Node parent, String indexName, String name, INodeType elementType)");

        Node result = findNetworkElement(index, name);
        if (result == null) {
            result = createNetworkElement(parent, index, name, elementType);
        }
        return result;
    }

    /**
     * Creates a sector node with specified parameters, attaches it with CHILD relationship to
     * <code>parent</code>, sets its properties, and adds it to index
     * 
     * @param parent
     * @param indexName
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return the newly created sector node
     */
    public Node createSector(Node parent, Index<Node> index, String name, String ci, String lac) throws IllegalNodeDataException,
            DatabaseException {
        LOGGER.debug("start createSector(Node parent, String indexName, String name, String ci, String lac)");

        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (!NetworkElementNodeType.SITE.getId().equals(parent.getProperty(TYPE, null))) {
            throw new IllegalArgumentException("Parent node must be of type SITE.");
        }
        if (index == null) {
            throw new IllegalArgumentException("Index is null.");
        }

        // TODO: LN: incorrect condition - you have now <Name> AND <CI+LAC>, but should have OR
        // AG: !(A|B) = !(A)&!(B)
        if (((name == null) || (name.equals(StringUtils.EMPTY)))
                && ((ci == null) || (ci.equals(StringUtils.EMPTY)) || (lac == null) || (lac.equals(StringUtils.EMPTY)))) {
            throw new IllegalNodeDataException("Name or CI+LAC must be set");
        }

        Transaction tx = graphDb.beginTx();
        Node result = null;
        try {
            result = createNode(NetworkElementNodeType.SECTOR);
            datasetService.addChild(parent, result);
            // set properties and index node
            if ((name != null) && (!name.equals(StringUtils.EMPTY))) {
                setNameProperty(result, name);
                addNodeToIndex(result, index, NAME, name);
            }
            if ((ci != null) && (!ci.equals(StringUtils.EMPTY))) {
                result.setProperty(CELL_INDEX, ci);
                addNodeToIndex(result, index, CELL_INDEX, ci);
            }
            if ((lac != null) && (!lac.equals(StringUtils.EMPTY))) {
                result.setProperty(LOCATION_AREA_CODE, lac);
                addNodeToIndex(result, index, LOCATION_AREA_CODE, lac);
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return result;

    }

    /**
     * Looks for a sector in <code>indexName</code> by the specified parameters. At least one
     * parameter must be not <code>null</code>
     * 
     * @param indexName the name of index
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return a sector node or <code>null</code> if nothing was found
     */
    public Node findSector(Index<Node> index, String name, String ci, String lac) {
        LOGGER.debug("start findSector(String indexName, String name, String ci, String lac)");
        // validate parameters
        if (index == null) {
            throw new IllegalArgumentException("Index is null.");
        }
        if (((name == null) || (name.equals(StringUtils.EMPTY)))
                && ((ci == null) || (ci.equals(StringUtils.EMPTY)) || (lac == null) || (lac.equals(StringUtils.EMPTY)))) {
            throw new IllegalArgumentException("Name or CI+LAC must be set");
        }

        // Find element by index
        Node result = null;

        if (!((ci == null) || (ci.equals(StringUtils.EMPTY)))) {
            IndexHits<Node> cis = index.get(CELL_INDEX, ci);
            for (Node node : cis) {
                if (lac.equals(node.getProperty(LOCATION_AREA_CODE, null))) {
                    result = node;
                    break;
                }
            }
        }
        if (result == null) {
            result = index.get(NAME, name).getSingle();
        }
        return result;
    }

    /**
     * Looks up for a sector by the defined parameters, creates a new one if nothing was found,
     * indexes its properties and attaches it to <code>parent</code>
     * 
     * @param used only if sector was not found
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property@param indexName
     * @return found or created sector
     */
    public Node getSector(Node parent, Index<Node> index, String name, String ci, String lac) throws DatabaseException,
            IllegalNodeDataException {
        LOGGER.debug("start getSector(Node parent, String indexName, String name, String ci, String lac)");
        Node result = findSector(index, name, ci, lac);
        if (result == null) {
            result = createSector(parent, index, name, ci, lac);
        }
        return result;
    }

    /**
     * Traverses database to find all network elements of defined type
     * 
     * @param elementType
     * @return an <code>Iterable</code> over found nodes
     */
    public Iterable<Node> findAllNetworkElements(Node parent, INodeType elementType) {
        LOGGER.debug("start findAllNetworkElements(Node parent, INodeType elementType)");
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }

        return CHILD_ELEMENT_TRAVERSAL_DESCRIPTION.evaluator(new FilterNodesByType(elementType)).traverse(parent).nodes();
    }

    /**
     * Creates Node for a Selection List structure
     * 
     * @param networkNode root Network Node
     * @param selectionListName name of selection list
     * @return created root node for selection list structure
     */
    public Node createSelectionList(Node networkNode, String selectionListName) throws DuplicateNodeNameException,
            DatabaseException {
        LOGGER.debug("start createSelectionList(<" + networkNode + ">, <" + selectionListName + ">)");

        if (networkNode == null) {
            LOGGER.error("Network Node is null");
            throw new IllegalArgumentException("NetworkNode is null");
        }

        if ((selectionListName == null) || (selectionListName.equals(StringUtils.EMPTY))) {
            LOGGER.error("SelectionListName is null");
            throw new IllegalArgumentException("SelectionList Name is null or empty");
        }

        if (findSelectionList(networkNode, selectionListName) != null) {
            LOGGER.error("Seleciton List already exists");
            throw new DuplicateNodeNameException(selectionListName, NetworkElementNodeType.SELECTION_LIST_ROOT);
        }

        Node result = null;
        Transaction tx = graphDb.beginTx();
        try {
            result = createNode(networkNode, NetworkRelationshipTypes.SELECTION_LIST, NetworkElementNodeType.SELECTION_LIST_ROOT);
            result.setProperty(SELECTED_NODES_COUNT, 0);
            result.setProperty(NAME, selectionListName);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating SelectionNode", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish createSelectionList()");

        return result;
    }

    /**
     * Searches for a Selection List root Node
     * 
     * @param networkNode root Network Node
     * @param selectionListName name of Selection List to search
     * @return root node of selection list structure or null if it's not found
     */
    public Node findSelectionList(Node networkNode, String selectionListName) throws DuplicateNodeNameException {
        LOGGER.debug("start findSelectionList(<" + networkNode + ">, <" + selectionListName + ">)");

        if (networkNode == null) {
            LOGGER.error("Network Node is null");
            throw new IllegalArgumentException("NetworkNode is null");
        }

        if ((selectionListName == null) || (selectionListName.equals(StringUtils.EMPTY))) {
            LOGGER.error("SelectionListName is null");
            throw new IllegalArgumentException("SelectionList Name is null or empty");
        }

        Iterator<Node> selectionListIterator = ALL_SELECTION_LISTS_TRAVERSER
                .evaluator(new NameTypeEvaluator(selectionListName, NetworkElementNodeType.SELECTION_LIST_ROOT))
                .traverse(networkNode).nodes().iterator();

        Node result = null;

        if (selectionListIterator.hasNext()) {
            result = selectionListIterator.next();

            if (selectionListIterator.hasNext()) {
                LOGGER.error("Problem with DB - duplicated selection lists by name <" + selectionListName + ">.");
                throw new DuplicateNodeNameException(selectionListName, NetworkElementNodeType.SELECTION_LIST_ROOT);
            }
        }

        LOGGER.debug("finish findSelectionList()");

        return result;
    }

    /**
     * Returns all Selection Nodes related to Network
     * 
     * @param networkNode Network node
     * @return
     */
    public Iterable<Node> getAllSelectionModelsOfNetwork(Node networkNode) {
        LOGGER.debug("start getAllSelectionModelsOfNetwork(<" + networkNode + ">)");

        if (networkNode == null) {
            LOGGER.error("Network Node is null");
            throw new IllegalArgumentException("NetworkNode is null");
        }

        Iterable<Node> result = ALL_SELECTION_LISTS_TRAVERSER.traverse(networkNode).nodes();

        LOGGER.debug("finish getAllSelectionModelsOfNetwork()");

        return result;
    }

    /**
     * @param newParentnodem
     * @param currentNode
     */
    public void changeRelationship(Node newParentNode, Node curentNode, RelationshipType type, Direction direction) {
        curentNode.getSingleRelationship(type, direction).delete();
        newParentNode.createRelationshipTo(curentNode, type);
    }
}
