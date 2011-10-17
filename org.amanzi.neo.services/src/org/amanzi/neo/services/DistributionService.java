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

import java.awt.Color;
import java.util.Iterator;

import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * Service to work with Distribution Structure
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionService extends NewAbstractService {

    private static final Logger LOGGER = Logger.getLogger(DistributionService.class);
    
    /*
     * Property of Root Aggregation Node. Name of Property to analyse
     */
    public static final String PROPERTY_NAME = "property_name";
    
    /**
     * Node Types for Distribution Database Structure
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum DistributionNodeTypes implements INodeType {
        /*
         * Marks Root Node of Disribution Structure
         */
        ROOT_AGGREGATION, 
        /*
         * Marks Bar Node of Distribution Structure
         */
        AGGREGATION_BAR;
        
        static {
            NodeTypeManager.registerNodeType(DistributionNodeTypes.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
        }
        
    }
    
    /**
     * Relationship types used in Distribution Database Structure
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum DistributionRelationshipTypes implements RelationshipType {
        /*
         * Link between Root Node of Analyzed structure and Root Node of Distribution Structure
         */
        ROOT_AGGREGATION,
        
        /*
         * Link between Bar Node and Source node of Analyzed structure
         */
        AGGREGATED;
        
    }

    /*
     * Color property of Distribution Bar node 
     */
    public static final String BAR_COLOR = "color";

    /*
     * Count property of Distribution Bar node
     */
    public static final String COUNT = "count";
    
    private NewDatasetService datasetService;
    
    /**
     * Default constructor
     */
    public DistributionService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
    }
    
    /**
     * Constructor for testing
     * 
     * @param service test DB service
     */
    public DistributionService(GraphDatabaseService service) {
        super(service);
        datasetService = new NewDatasetService(service);
    }
    
    /**
     * Searches for a Root Aggregation Node
     *
     * @param parentNode parent node of Distribution
     * @param distributionName name of Distribution
     * @return
     */
    public Node findRootAggregationNode(Node parentNode, String distributionName) {
        LOGGER.debug("start findRootAggregationNode(<" + parentNode + ">, <" + distributionName + ">)");
        
        //validate input
        if (parentNode == null) {
            LOGGER.error("Parent Node cannot be null");
            throw new IllegalArgumentException("Parent Node cannot be null");
        }
        if ((distributionName == null) || (distributionName.equals(StringUtils.EMPTY))) {
            LOGGER.error("Distribuiton Name cannot be null or empty");
            throw new IllegalArgumentException("Distribuiton Name cannot be null or empty");
        }
        
        Node result = findNode(parentNode, DistributionRelationshipTypes.ROOT_AGGREGATION, distributionName, DistributionNodeTypes.ROOT_AGGREGATION);
        
        LOGGER.debug("finish findRootAggregationNode()");
        
        return result;
    }
    
    /**
     * Creates new Root Aggregation Node
     *
     * @param parentNode parent node of Distribution 
     * @param distributionName name of Distribution
     * @return
     */
    public Node createRootAggregationNode(Node parentNode, String distributionName) throws DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("start createRootAggregationNode(<" + parentNode + ">, <" + distributionName + ">)");
        
        //validate input
        if (parentNode == null) {
            LOGGER.error("Parent node cannot be null");
            throw new IllegalArgumentException("Parent node cannot be null");
        }
        if ((distributionName == null) || (distributionName.isEmpty())) {
            LOGGER.error("Distribution name cannot be null or empty");
            throw new IllegalArgumentException("Distribution name cannot be null or empty");
        }
        
        //check duplicated name
        if (findRootAggregationNode(parentNode, distributionName) != null) {
            LOGGER.error("Root Aggregation Node <" + parentNode + ", " + distributionName + "> already exists in Database");
            throw new DuplicateNodeNameException(distributionName, DistributionNodeTypes.ROOT_AGGREGATION);
        }
        
        //create new node
        Transaction tx = graphDb.beginTx();
        
        Node result = null;
        
        try {
            result = createNode(parentNode, DistributionRelationshipTypes.ROOT_AGGREGATION, DistributionNodeTypes.ROOT_AGGREGATION);
            
            result.setProperty(DistributionService.NAME, distributionName);
            result.setProperty(DistributionService.COUNT, 0);
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating Root Aggregation Node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        
        LOGGER.debug("finish createRootAggregationNode()");
        
        return result;
    }
    
    /**
     * Searches for Aggregation Bars in Database
     *
     * @param rootAggregationNode
     * @return
     */
    public Iterable<Node> findAggregationBars(Node rootAggregationNode) {
        LOGGER.debug("start findAggregationBars(<" + rootAggregationNode + ">)");
        
        //validate input
        if (rootAggregationNode == null) {
            LOGGER.error("Root Aggregation Node cannot be null");
            throw new IllegalArgumentException("Root Aggregation Node cannot be null");
        }
        
        Iterable<Node> result = datasetService.getChildrenChainTraverser(rootAggregationNode);
        
        LOGGER.debug("finish findAggregationBars()");
        
        return result;
    }
    
    /**
     * Creates new Aggregation Bar Node in Database
     *
     * @param rootAggregationNode root node of Distribution Structure
     * @param bar info about Bar
     */
    public Node createAggregationBarNode(Node rootAggregationNode, IDistributionBar bar) throws DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("start createAggregationBarNode(<" + rootAggregationNode + ">, <" + bar + ">)");
        
        //validate input
        if (rootAggregationNode == null) {
            LOGGER.error("Root Aggregation node should not be null");
            throw new IllegalArgumentException("Root Aggregation node should not be null");
        }
        if (bar == null) {
            LOGGER.error("Distribution Bar should not be null");
            throw new IllegalArgumentException("Distribution Bar should not be null");
        }
        //validate bar
        if (bar.getName() == null || bar.getName().isEmpty()) {
            LOGGER.error("Name of Bar cannot be null or empty");
            throw new IllegalArgumentException("Name of Bar cannot be null or empty");
        }
        
        //check unicality
        if (findAggregationBarNode(rootAggregationNode, bar.getName()) != null) {
            LOGGER.error("Bar with name <" + bar.getName() + "> already exists");
            throw new DuplicateNodeNameException(bar.getName(), DistributionNodeTypes.AGGREGATION_BAR);
        }
        
        Transaction tx = graphDb.beginTx();
        Node result = null;
        
        try {
            result = createNode(DistributionNodeTypes.AGGREGATION_BAR);
            
            datasetService.addChild(rootAggregationNode, result, null);
            
            result.setProperty(NAME, bar.getName());
            result.setProperty(COUNT, bar.getCount());
            result.setProperty(BAR_COLOR, convertColorToArray(bar.getColor()));
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creaing Distribution Bar <" + bar.getName() + ">", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        
        LOGGER.debug("finish createAggregationBarNode()");
        
        return result;
    }
    
    /**
     * Converts Color to Array from 3 elements - Right, Green and Blue components
     *
     * @param color
     * @return
     */
    private int[] convertColorToArray(Color color) {
        return new int[] {color.getRed(), color.getGreen(), color.getBlue()};
    }
    
    /**
     * Searches Aggregation Bar Node in Chain by it's name
     *
     * @param rootAggregationNode root node for Aggregation
     * @param name name of bar
     * @return
     */
    private Node findAggregationBarNode(Node rootAggregationNode, String name) throws DuplicateNodeNameException {
        Iterator<Node> aggrBarNodes = datasetService.DATASET_ELEMENT_TRAVERSAL_DESCRIPTION.
                evaluator(new NameTypeEvaluator(name, DistributionNodeTypes.AGGREGATION_BAR)).
                traverse(rootAggregationNode).nodes().iterator();
        
        if (aggrBarNodes.hasNext()) {
            Node result = aggrBarNodes.next();
            
            if (aggrBarNodes.hasNext()) {
                LOGGER.error("Found Bar Node with duplicated name <" + name + ">");
                throw new DuplicateNodeNameException(name, DistributionNodeTypes.AGGREGATION_BAR);
            }
            
            return result;
        }
        
        return null;
    }
    
    /**
     * Creates aggregation between bar and Source node
     *
     * @param barNode node of distribution bar
     * @param sourceNode source node
     */
    public void createAggregation(Node barNode, Node sourceNode) throws DatabaseException {
        LOGGER.debug("start createAggregation(<" + barNode + ">, <" + sourceNode + ">");
        
        //check input
        if (barNode == null) {
            LOGGER.error("Input barNode cannot be null");
            throw new IllegalArgumentException("Input barNode cannot be null");
        }
        if (sourceNode == null) {
            LOGGER.error("Input sourceNode cannot be null");
            throw new IllegalArgumentException("Input sourceNode cannot be null");
        }
        
        //link nodes
        Transaction tx = graphDb.beginTx();
        try {
            barNode.createRelationshipTo(sourceNode, DistributionRelationshipTypes.AGGREGATED);
            
            tx.success();
        } catch (Exception e) { 
            tx.failure();
            LOGGER.error("Error on creating Aggregation Relationship " +
            		"between <" + barNode + "> and <" + sourceNode + ">", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        
        LOGGER.debug("finish createAggregation()");
    }
    
    /**
     * Updates Distribution Bar Properties in Database
     *
     * @param rootAggregationNode root node of distribution structure
     * @param bar bar to update
     */
    public void updateDistributionBar(Node rootAggregationNode, IDistributionBar bar) throws DatabaseException {
        LOGGER.debug("start updateDistributionBar(<" + rootAggregationNode + ">, <" + bar + ">)");
        
        //check input
        if (rootAggregationNode == null) {
            LOGGER.error("Input rootAggregationNode cannot be null");
            throw new IllegalArgumentException("Input rootAggregationNode cannot be null");
        }
        if (bar == null) {
            LOGGER.error("Input bar cannot be null");
            throw new IllegalArgumentException("Input bar cannot be null");
        }
        if (bar.getName() == null || bar.getName().isEmpty()) {
            LOGGER.error("New name of bar cannot be null or empty");
            throw new IllegalArgumentException("New name of bar cannot be null or empty");
        }
        if (bar.getRootElement() == null) {
            LOGGER.error("RootElement of Bar cannot be null");
            throw new IllegalArgumentException("RootElement of Bar cannot be null");
        }
        
        DataElement rootElement = (DataElement)bar.getRootElement();
        if (rootElement.getNode() == null) {
            LOGGER.error("RootElement Node of Bar cannot be null");
            throw new IllegalArgumentException("RootElement Node of Bar cannot be null");
        }
        
        //check root node and bar node
        boolean found = false;
        Node rootBarNode = rootElement.getNode();
        for (Node barNode : findAggregationBars(rootAggregationNode)) {
            if (barNode.equals(rootBarNode)) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            LOGGER.error("Bar <" + bar + "> is from incorrect Distribution structure");
            throw new IllegalArgumentException("Bar <" + bar + "> is from incorrect Distribution structure");
        }
        
        //update properties
        Transaction tx = graphDb.beginTx();
        try {
            rootBarNode.setProperty(BAR_COLOR, convertColorToArray(bar.getColor()));
            rootBarNode.setProperty(COUNT, bar.getCount());
            rootBarNode.setProperty(NAME, bar.getName());
            
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on updating properties of Distribution Bar <" + bar + ">", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        
        LOGGER.debug("finish updateDistributionBar()");
    }
    
    /**
     * Updates number of Bars of Distribution
     *
     * @param rootAggregationNode root node of distribution structure 
     * @param count new count
     */
    public void updateDistributionModelCount(Node rootAggregationNode, Integer count) {
        
    }
    
}
