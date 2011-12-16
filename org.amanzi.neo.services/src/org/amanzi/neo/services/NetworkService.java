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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
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
public class NetworkService extends AbstractService {

    public final static String CELL_INDEX = "ci";
    public final static String LOCATION_AREA_CODE = "lac";
    public final static String BSIC = "bsic";
    public final static String BCCH = "bcch";

    public final static String SELECTION_RELATIONSHIP_INDEX = "selection_relationship";

    public final static String SELECTED_NODES_COUNT = "selected_nodes_count";
    public final static String SOURCE_NAME = "source name";
    /*
     * name of property that contains array with network structure
     */
    public final static String NETWORK_STRUCTURE = "network_structure";

    private static Logger LOGGER = Logger.getLogger(NetworkService.class);

    private DatasetService datasetService;

    /**
     * <p>
     * This enum describes types of network elements.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum NetworkElementNodeType implements INodeType {
        BSC, SITE, SECTOR, CITY, MSC, SELECTION_LIST_ROOT, TRX, CHANNEL_GROUP, FREQUENCY_ROOT, FREQUENCY_PLAN;

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
        SELECTION_LIST, SELECTED, CHANNEL, TRX, FREQUENCY_ROOT, ENTRY_PLAN;
    }

    /*
     * Traversal Description to find out all Selection List Nodes of sector
     */
    protected final static TraversalDescription ALL_SELECTION_LISTS_OF_SECTOR_TRAVERSER = Traversal.description().breadthFirst()
            .relationships(NetworkRelationshipTypes.SELECTED).evaluator(Evaluators.atDepth(1));

    /*
     * Traversal Description to find out all Selection List Nodes
     */
    protected final static TraversalDescription ALL_SELECTION_LISTS_TRAVERSER = Traversal.description().breadthFirst()
            .relationships(NetworkRelationshipTypes.SELECTION_LIST).evaluator(Evaluators.excludeStartPosition());

    /*
     * Traversal Description to find all node2node relationship root nodes
     */
    protected final static TraversalDescription N2N_ROOT_TRAVERSER = Traversal.description().breadthFirst()
            .relationships(N2NRelTypes.NEIGHBOUR).evaluator(Evaluators.excludeStartPosition()).relationships(N2NRelTypes.EXCEPTION)
            .relationships(N2NRelTypes.FREQUENCY_SPECTRUM).relationships(N2NRelTypes.ILLEGAL_FREQUENCY)
            .relationships(N2NRelTypes.INTERFERENCE_MATRIX).relationships(N2NRelTypes.NEIGHBOUR).relationships(N2NRelTypes.SHADOW)
            .relationships(N2NRelTypes.TRANSMISSION).relationships(N2NRelTypes.TRIANGULATION)
            .evaluator(Evaluators.excludeStartPosition());

    public static final String SECTOR_COUNT = "sector_count";

    public NetworkService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getDatasetService();
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
        return createNetworkElement(parent, index, name, elementType, DatasetRelationTypes.CHILD);
    }

    /**
     * Finds a network element by <code>name</code> in index <code>indexName</code>
     * 
     * @param indexName name of index, where to look for the element. Call
     *        {@link AbstractService#getIndexKey(Node, INodeType)} to generate this name.
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

    public Iterator<Node> findByIndex(Index<Node> index, String parameterName, Object parameterValue) {
        LOGGER.debug("start findNetworkElement(String indexName, String name)");
        // validate parameters
        if (index == null) {
            throw new IllegalArgumentException("Index is null.");
        }
        if ((parameterName == null) || (parameterName.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if ((parameterValue == null) || (parameterValue.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
        return index.get(parameterName, parameterValue).iterator();
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

    public int getBsicProperty(Map<String, Object> element) {
        String bcc = element.get("bcc") != null ? element.get("bcc").toString() : StringUtils.EMPTY;
        boolean notEmptyBcc = StringUtils.isNotEmpty(bcc);
        String ncc = element.get("ncc") != null ? element.get("ncc").toString() : StringUtils.EMPTY;
        boolean notEmptyNcc = StringUtils.isNotEmpty(ncc);
        int bccInt = notEmptyBcc ? Integer.valueOf(bcc) : 0;
        int nccint = notEmptyNcc ? Integer.valueOf(ncc) : 0;
        int bsic = nccint * 10 + bccInt;
        return bsic;
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
        // if (bsic == null) {
        // bsic = 0;
        // }
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

        // !(A|B) = !(A)&!(B). !(name OR (ci AND lac)) = !name AND !(ci AND lac) = !name AND (!ci OR
        // !lac)
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
                setAnyProperty(result, AbstractService.NAME, name);
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
            parent.setProperty(SECTOR_COUNT, (Integer)parent.getProperty(SECTOR_COUNT, 0) + 1);
            LOGGER.debug("site " + parent.getId() + "nodes: " + parent.getProperty(SECTOR_COUNT, 0));
            tx.success();
        } finally {
            tx.finish();
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
     * @param bsic unrequired parameter that represent index sector by bsic value if bsic value is
     *        <code>NULL</code> sector will be indexed with 0 value
     * @return the newly created sector node
     */
    public Node createSector(Node parent, Index<Node> index, String name, String ci, String lac, int bsic)
            throws IllegalNodeDataException, DatabaseException {
        LOGGER.debug("start createSector(Node parent, String indexName, String name, String ci, String lac,int bsic)");
        Transaction tx = graphDb.beginTx();
        Node result;
        try {
            result = createSector(parent, index, name, ci, lac);
            addNodeToIndex(result, index, BSIC, bsic);
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
                // TODO: LN: incorrect code!!!!!!!!!!!!!!!!!!
                if (lac.equals(node.getProperty(LOCATION_AREA_CODE, null).toString())) {
                    result = node;
                    break;
                }
            }
        }
        if (result == null) {
            try {
                result = index.get(NAME, name).getSingle();
            } catch (NullPointerException e) {
                result = null;
            }
        }
        return result;
    }

    public Node getServiceElementByProxy(Node proxy, N2NRelTypes relType) {
        Iterable<Relationship> rels = proxy.getRelationships(relType, Direction.INCOMING);
        for (Relationship rel : rels) {
            if (rel.getOtherNode(proxy).getProperty(AbstractService.TYPE).equals(NetworkElementNodeType.SECTOR.getId())) {
                return rel.getOtherNode(proxy);
            }
        }
        return null;
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
            // return all network elements
            return CHILD_ELEMENT_TRAVERSAL_DESCRIPTION.evaluator(Evaluators.excludeStartPosition()).traverse(parent).nodes();
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
     * Returns all Selection Nodes related to Sector
     * 
     * @param sectorNode Sector node
     * @return all selection models of sector
     */
    public Iterable<Node> getAllSelectionModelsOfSector(Node sectorNode) {
        LOGGER.debug("start getAllSelectionModelsOfSector(<" + sectorNode + ">)");

        if (sectorNode == null) {
            LOGGER.error("Sector Node is null");
            throw new IllegalArgumentException("SectorNode is null");
        }

        Iterable<Node> result = ALL_SELECTION_LISTS_OF_SECTOR_TRAVERSER.traverse(sectorNode).nodes();

        LOGGER.debug("finish getAllSelectionModelsOfSector()");

        return result;
    }

    /**
     * Find selection link
     * 
     * @param selectionRootNode root of selection structure
     * @param selectedNode node to find
     * @param linkIndex links of selection structure
     * @return relationship between selectionRootNode and selectedNode
     */
    public Relationship findSelectionLink(Node selectionRootNode, Node selectedNode, Index<Relationship> linkIndex) {
        LOGGER.debug("start findSelectionLink(<" + selectionRootNode + ">, <" + selectedNode + ">)");

        if (selectionRootNode == null) {
            LOGGER.error("Input selectionRootNode cannot be null");
            throw new IllegalArgumentException("Input selectionRootNode cannot be null");
        }
        if (selectedNode == null) {
            LOGGER.error("Input selectedNode cannot be null");
            throw new IllegalArgumentException("Input selectedNode cannot be null");
        }
        if (linkIndex == null) {
            LOGGER.error("Input linkIndex cannot be null");
            throw new IllegalArgumentException("Input linkIndex cannot be null");
        }

        String indexKey = Long.toString(selectionRootNode.getId());
        Object indexValue = selectedNode.getId();

        LOGGER.debug("finish findSelectionLink");

        return linkIndex.get(indexKey, indexValue).getSingle();
    }

    /**
     * Creates Selection link with node
     * 
     * @param selectionRootNode root of selection structure
     * @param selectedNode node to add in selection structure
     */
    public void createSelectionLink(Node selectionRootNode, Node selectedNode, Index<Relationship> linkIndex) throws AWEException {
        LOGGER.debug("start createSelectionLink(<" + selectionRootNode + ">, <" + selectedNode + ">)");

        // validate input
        if (selectionRootNode == null) {
            LOGGER.error("Input selectionRootNode cannot be null");
            throw new IllegalArgumentException("Input selectionRootNode cannot be null");
        }
        if (selectedNode == null) {
            LOGGER.error("Input selectedNode cannot be null");
            throw new IllegalArgumentException("Input selectedNode cannot be null");
        }
        if (linkIndex == null) {
            LOGGER.error("Input linkIndex cannot be null");
            throw new IllegalArgumentException("Input linkIndex cannot be null");
        }

        // check duplication
        String indexKey = Long.toString(selectionRootNode.getId());
        Object indexValue = selectedNode.getId();

        if (findSelectionLink(selectionRootNode, selectedNode, linkIndex) != null) {
            LOGGER.error("Link between Root Selection Node <" + selectionRootNode + "> and Node <" + selectedNode
                    + "> already exists.");
            throw new DatabaseException("Link between Root Selection Node <" + selectionRootNode + "> and Node <" + selectedNode
                    + "> already exists.");
        }

        // create a link
        Transaction tx = graphDb.beginTx();
        try {
            Relationship link = selectionRootNode.createRelationshipTo(selectedNode, NetworkRelationshipTypes.SELECTED);

            linkIndex.add(link, indexKey, indexValue);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating Selection link", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish createSelectionLink");
    }

    /**
     * Delete selection link
     * 
     * @param selectionRootNode root of selection structure
     * @param selectedNode node to delete from selection structure
     * @param linkIndex links of selection structure
     */
    public void deleteSelectionLink(Node selectionRootNode, Node selectedNode, Index<Relationship> linkIndex) throws AWEException {

        LOGGER.debug("start deleteSelectionLink(<" + selectionRootNode + ">, <" + selectedNode + ">)");

        if (selectionRootNode == null) {
            LOGGER.error("Input selectionRootNode cannot be null");
            throw new IllegalArgumentException("Input selectionRootNode cannot be null");
        }
        if (selectedNode == null) {
            LOGGER.error("Input selectedNode cannot be null");
            throw new IllegalArgumentException("Input selectedNode cannot be null");
        }
        if (linkIndex == null) {
            LOGGER.error("Input linkIndex cannot be null");
            throw new IllegalArgumentException("Input linkIndex cannot be null");
        }

        Relationship r = findSelectionLink(selectionRootNode, selectedNode, linkIndex);
        Transaction tx = graphDb.beginTx();
        try {
            r.delete();
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on deleting Selection link", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        LOGGER.debug("finish deleteSelectionLink");
    }

    /**
     * Remove required relationship from current node and create new relationship from
     * newParentElement to currentNode
     * 
     * @param newParentnodem
     * @param currentNode
     * @throws DatabaseException
     */
    public void replaceRelationship(Node newParentNode, Node curentNode, RelationshipType type, Direction direction)
            throws AWEException {
        Transaction tx = graphDb.beginTx();
        try {
            curentNode.getSingleRelationship(type, direction).delete();
            newParentNode.createRelationshipTo(curentNode, type);
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.debug("replaceRelationship end with exception");
            throw new DatabaseException(e);
        } finally {

            tx.finish();
        }
    }

    /**
     * complete properties of existed Node with new properties. If Is replaceExisted set to
     * <b>true</b> existed properties will be replaced with new values;
     * 
     * @param existedNode
     * @param dataElement
     * @param isReplaceExisted
     * @param index
     * @throws DatabaseException
     */
    public void completeProperties(PropertyContainer existedNode, DataElement dataElement, boolean isReplaceExisted,
            Index<Node> index) throws DatabaseException {
        Transaction tx = graphDb.beginTx();
        if (existedNode instanceof Node && index != null) {
            removeNodeFromIndex((Node)existedNode, index, NAME, existedNode.getProperty(NAME));
        }
        try {
            LOGGER.debug("Start completing properties in " + existedNode);
            for (String mapKey : dataElement.keySet()) {
                if (existedNode.hasProperty(mapKey) && isReplaceExisted) {
                    existedNode.setProperty(mapKey, dataElement.get(mapKey));
                } else if (!existedNode.hasProperty(mapKey)) {
                    existedNode.setProperty(mapKey, dataElement.get(mapKey));
                }
            }
            LOGGER.debug("END completing properties in " + existedNode);
            if (existedNode instanceof Node && index != null) {
                if (existedNode.getProperty(TYPE).equals(NetworkElementNodeType.SECTOR.getId())) {
                    int bsic = getBsicProperty(dataElement);
                    Integer bcch = (Integer)dataElement.get(NetworkService.BCCH);
                    if (bsic != 0) {
                        addNodeToIndex((Node)existedNode, index, BSIC, bsic);
                    }
                    if (bcch != null) {
                        addNodeToIndex((Node)existedNode, index, NetworkService.BCCH, bcch);
                    }
                }
                addNodeToIndex((Node)existedNode, index, NAME, existedNode.getProperty(NAME));
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.debug("end with exception");
            throw new DatabaseException(e);
        } finally {
            LOGGER.debug("finish replacement");
            tx.finish();
        }
    }

    /**
     * Creates a new network element of defined <code>elementType</code>, sets its <code>name</code>
     * property, adds this element to index <code>indexName</code>, and attaches this element to
     * <code>parent</code> node with defined relationship.
     * 
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return the newly created network element node
     */
    public Node createNetworkElement(Node parent, Index<Node> index, String name, INodeType elementType, RelationshipType relType)
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
            datasetService.createRelationship(parent, result, relType);
            setAnyProperty(result, AbstractService.NAME, name);
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
     * Traverses the database to find all n2n relationship root nodes, that refer to the defined
     * <code>network</code> node
     * 
     * @param network the root network node to find n2n nodes for
     * @return iterable over the found nodes
     */
    public Iterable<Node> getNodeToNodeRoots(Node network) {
        // validate
        if (network == null) {
            throw new IllegalArgumentException("Network node is null.");
        }
        return N2N_ROOT_TRAVERSER.traverse(network).nodes();
    }

    /**
     * Sets array with Network Structure to Node
     * 
     * @param networkNode
     * @param networkStructure
     */
    public void setNetworkStructure(Node networkNode, List<INodeType> networkStructure) throws DatabaseException {
        LOGGER.debug("start setNetworkStructure(<" + networkNode + ">, <" + networkStructure + ">)");

        // check input
        if (networkNode == null) {
            LOGGER.error("Input networkNode cannot be null");
            throw new IllegalArgumentException("Input networkNode cannot be null");
        }
        if (networkStructure == null) {
            LOGGER.error("Input networkStructure cannot be null");
            throw new IllegalArgumentException("Input networkStructure cannot be null");
        }

        // convert list of INodeTypes to array of Strings
        String[] structureArray = new String[networkStructure.size()];
        int i = 0;
        for (INodeType nodeType : networkStructure) {
            structureArray[i++] = nodeType.getId();
        }

        // set propery to node
        Transaction tx = graphDb.beginTx();
        try {
            networkNode.setProperty(NETWORK_STRUCTURE, structureArray);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on setting Network Structure to Node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish setNetworkStructure()");
    }

    public Node createProxy(Node sourceNode, Node rootNode, RelationshipType rel, INodeType type) throws DatabaseException {
        LOGGER.debug("start createProxy(Node sourceNode)");

        if (sourceNode == null) {
            LOGGER.error("Input sourceNode cannot be null");
            throw new IllegalArgumentException("Input sourceNode cannot be null");
        }
        if (rootNode == null) {
            LOGGER.error("Input rootNode cannot be null");
            throw new IllegalArgumentException("Input rootNode cannot be null");
        }
        Transaction tx = graphDb.beginTx();
        try {
            Node result = datasetService.createNode(sourceNode, rel, type);
            datasetService.addChild(rootNode, result, null);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(SOURCE_NAME, sourceNode.getProperty(AbstractService.NAME));
            datasetService.setProperties(result, properties);
            tx.success();
            return result;
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on setting Network Structure to Node", e);
            throw new DatabaseException(e);

        } finally {
            tx.finish();
        }
    }
}
