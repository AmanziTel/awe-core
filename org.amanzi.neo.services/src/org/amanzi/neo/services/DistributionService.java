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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.xml.schema.Bar;
import org.amanzi.neo.model.distribution.xml.schema.Bars;
import org.amanzi.neo.model.distribution.xml.schema.Data;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.model.distribution.xml.schema.Filter;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.brewer.color.BrewerPalette;
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
 * Service to work with Distribution Structure
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionService extends NewAbstractService {

    private static final Logger LOGGER = Logger.getLogger(DistributionService.class);

    protected final TraversalDescription DISTRIBUTION_TRAVERSAL_DESCRIPTION = Traversal.description()
            .relationships(UserDefinedDistrRelTypes.DISTRIBUTION, Direction.OUTGOING).evaluator(Evaluators.atDepth(1))
            .evaluator(Evaluators.excludeStartPosition());

    protected final TraversalDescription DISTRIBUTION_DATA_TRAVERSAL_DESCRIPTION = Traversal.description()
            .relationships(UserDefinedDistrRelTypes.DISTRIBUTION, Direction.OUTGOING)
            .relationships(UserDefinedDistrRelTypes.DATA, Direction.OUTGOING).evaluator(Evaluators.atDepth(2))
            .evaluator(Evaluators.excludeStartPosition());

    protected final TraversalDescription DISTRIBUTION_BARS_TRAVERSAL_DESCRIPTION = Traversal.description()
            .relationships(UserDefinedDistrRelTypes.BARS, Direction.OUTGOING)
            .relationships(UserDefinedDistrRelTypes.BAR, Direction.OUTGOING).evaluator(Evaluators.atDepth(2))
            .evaluator(Evaluators.excludeStartPosition());

    /*
     * Property of Root Aggregation Node. Name of Property to analyse
     */
    public static final String PROPERTY_NAME = "property_name";

    /*
     * Property for name of Current Distribution Model of Analyzed Model
     */
    public static final String CURRENT_DISTRIBUTION_MODEL = "current_distribution_model";

    /*
     * Color property of Distribution Bar node
     */
    public static final String BAR_COLOR = "color";

    /*
     * Count property of Distribution Bar node
     */
    public static final String COUNT = "count";

    /*
     * NodeType property of Distribution Bar node
     */
    public static final String NODE_TYPE = "node_type";

    /*
     * Left Color property of Distribution Root node
     */
    public static final String LEFT_COLOR = "left_color";

    /*
     * Right Color property of Distribution Root node
     */
    public static final String RIGHT_COLOR = "right_color";

    /*
     * Selected Color property of Distribution Root node
     */
    public static final String SELECTED_COLOR = "selected_color";

    /*
     * Selected Palette property of Distribution Root node
     */
    public static final String PALETTE = "palette";

    /*
     * Name property of User Defined Distribution
     */
    public static final String UD_NAME = "name";

    /*
     * DataType property of User Defined Distribution
     */
    public static final String UD_DATA_TYPE = "data_type";

    /*
     * NodeType property of User Defined Distribution
     */
    public static final String UD_NODE_TYPE = "node_type";

    /*
     * PropertyName property of User Defined Distribution
     */
    public static final String UD_PROPERTY_NAME = "property_name";

    /*
     * DriveType property of User Defined Distribution
     */
    public static final String UD_DRIVE_TYPE = "drive_type";

    /*
     * Name property of User Defined Distribution Bar
     */
    public static final String UD_BAR_NAME = "drive_type";

    /*
     * Red property of User Defined Distribution Bar Color
     */
    public static final String UD_BAR_COLOR_RED = "red";

    /*
     * Green property of User Defined Distribution Bar Color
     */
    public static final String UD_BAR_COLOR_GREEN = "green";

    /*
     * Blue property of User Defined Distribution Bar Color
     */
    public static final String UD_BAR_COLOR_BLUE = "blue";

    /*
     * FilterType property of User Defined Distribution
     */
    public static final String UD_FILTER_TYPE = "filter_type";

    /*
     * ExpressionType property of User Defined Distribution Filter
     */
    public static final String UD_FILTER_EXP_TYPE = "expression_type";

    /*
     * NodeType property of User Defined Distribution Filter
     */
    public static final String UD_FILTER_NODE_TYPE = "node_type";

    /*
     * PropertyName property of User Defined Distribution Filter
     */
    public static final String UD_FILTER_PROPERTY_NAME = "property_name";

    /*
     * Value property of User Defined Distribution Filter
     */
    public static final String UD_FILTER_VALUE = "value";

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

    public enum UserDefinedDistrRelTypes implements RelationshipType {
        DISTRIBUTION, DATA, BARS, BAR, COLOR, FILTER;
    }

    public enum UserDefinedDistrNodeTypes implements INodeType {
        DISTRIBUTION, DATA, BARS, BAR, COLOR, FILTER;

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

    private NewDatasetService datasetService;

    /**
     * Default constructor
     */
    public DistributionService() {
        super();
        datasetService = NeoServiceFactory.getInstance().getNewDatasetService();
    }

    /**
     * Searches for a Root Aggregation Node
     * 
     * @param parentNode parent node of Distribution
     * @param distributionName name of Distribution
     * @return
     */
    public Node findRootAggregationNode(Node parentNode, IDistribution< ? > distribution) {
        LOGGER.debug("start findRootAggregationNode(<" + parentNode + ">, <" + distribution + ">)");

        // validate input
        if (parentNode == null) {
            LOGGER.error("Parent Node cannot be null");
            throw new IllegalArgumentException("Parent Node cannot be null");
        }
        if (distribution == null) {
            LOGGER.error("Distribuiton cannot be null or empty");
            throw new IllegalArgumentException("Distribuiton cannot be null or empty");
        }
        if (StringUtils.isEmpty(distribution.getName())) {
            LOGGER.error("Name of Distribution cannot be null or empty");
            throw new IllegalArgumentException("Name of Distribution cannot be null or empty");
        }
        if (StringUtils.isEmpty(distribution.getPropertyName())) {
            LOGGER.error("PropertyName of Distribution cannot be null or empty");
            throw new IllegalArgumentException("PropertyName of Distribution cannot be null or empty");
        }
        if (distribution.getNodeType() == null) {
            LOGGER.error("NodeType of Distribution cannot be null or empty");
            throw new IllegalArgumentException("NodeType of Distribution cannot be null or empty");
        }
        if (StringUtils.isEmpty(distribution.getPropertyName())) {
            LOGGER.error("PropertyName of Distribution cannot be null or empty");
            throw new IllegalArgumentException("PropertyName of Distribution cannot be null or empty");
        }
        if (distribution.getNodeType() == null) {
            LOGGER.error("NodeType of Distribution cannot be null or empty");
            throw new IllegalArgumentException("NodeType of Distribution cannot be null or empty");
        }

        Node result = null;

        for (Relationship aggregationRelationships : parentNode.getRelationships(DistributionRelationshipTypes.ROOT_AGGREGATION,
                Direction.OUTGOING)) {
            Node candidateRoot = aggregationRelationships.getEndNode();

            // check properties
            if (candidateRoot.getProperty(NAME, StringUtils.EMPTY).equals(distribution.getName())
                    && candidateRoot.getProperty(PROPERTY_NAME, StringUtils.EMPTY).equals(distribution.getPropertyName())
                    && candidateRoot.getProperty(NODE_TYPE, StringUtils.EMPTY).equals(distribution.getNodeType().getId())) {
                result = candidateRoot;
                break;
            }
        }

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
    public Node createRootAggregationNode(Node parentNode, IDistribution< ? > distribution) throws DuplicateNodeNameException,
            DatabaseException {
        LOGGER.debug("start createRootAggregationNode(<" + parentNode + ">, <" + distribution + ">)");

        // validate input
        if (parentNode == null) {
            LOGGER.error("Parent node cannot be null");
            throw new IllegalArgumentException("Parent node cannot be null");
        }
        if (distribution == null) {
            LOGGER.error("Distribution cannot be null or empty");
            throw new IllegalArgumentException("Distribution cannot be null or empty");
        }
        if (StringUtils.isEmpty(distribution.getName())) {
            LOGGER.error("Name of Distribution cannot be null or empty");
            throw new IllegalArgumentException("Name of Distribution cannot be null or empty");
        }
        if (StringUtils.isEmpty(distribution.getPropertyName())) {
            LOGGER.error("PropertyName of Distribution cannot be null or empty");
            throw new IllegalArgumentException("PropertyName of Distribution cannot be null or empty");
        }
        if (distribution.getNodeType() == null) {
            LOGGER.error("NodeType of Distribution cannot be null or empty");
            throw new IllegalArgumentException("NodeType of Distribution cannot be null or empty");
        }

        // check duplicated name
        if (findRootAggregationNode(parentNode, distribution) != null) {
            LOGGER.error("Root Aggregation Node <" + parentNode + ", " + distribution + "> already exists in Database");
            throw new DuplicateNodeNameException(distribution.getName(), DistributionNodeTypes.ROOT_AGGREGATION);
        }

        // create new node
        Transaction tx = graphDb.beginTx();

        Node result = null;

        try {
            result = createNode(parentNode, DistributionRelationshipTypes.ROOT_AGGREGATION, DistributionNodeTypes.ROOT_AGGREGATION);

            result.setProperty(NAME, distribution.getName());
            result.setProperty(PROPERTY_NAME, distribution.getPropertyName());
            result.setProperty(NODE_TYPE, distribution.getNodeType().getId());
            result.setProperty(COUNT, 0);

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

        // validate input
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
    public Node createAggregationBarNode(Node rootAggregationNode, IDistributionBar bar) throws DuplicateNodeNameException,
            DatabaseException {
        LOGGER.debug("start createAggregationBarNode(<" + rootAggregationNode + ">, <" + bar + ">)");

        // validate input
        if (rootAggregationNode == null) {
            LOGGER.error("Root Aggregation node should not be null");
            throw new IllegalArgumentException("Root Aggregation node should not be null");
        }
        if (bar == null) {
            LOGGER.error("Distribution Bar should not be null");
            throw new IllegalArgumentException("Distribution Bar should not be null");
        }
        // validate bar
        if (bar.getName() == null || bar.getName().isEmpty()) {
            LOGGER.error("Name of Bar cannot be null or empty");
            throw new IllegalArgumentException("Name of Bar cannot be null or empty");
        }
        if (bar.getCount() < 0) {
            LOGGER.error("Count of Bar cannot be less than zero");
            throw new IllegalArgumentException("Count of Bar cannot be less than zero");
        }

        // check unicality
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
        Iterator<Node> aggrBarNodes = datasetService.DATASET_ELEMENT_TRAVERSAL_DESCRIPTION
                .evaluator(new NameTypeEvaluator(name, DistributionNodeTypes.AGGREGATION_BAR)).traverse(rootAggregationNode)
                .nodes().iterator();

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

        // check input
        if (barNode == null) {
            LOGGER.error("Input barNode cannot be null");
            throw new IllegalArgumentException("Input barNode cannot be null");
        }
        if (sourceNode == null) {
            LOGGER.error("Input sourceNode cannot be null");
            throw new IllegalArgumentException("Input sourceNode cannot be null");
        }

        // link nodes
        Transaction tx = graphDb.beginTx();
        try {
            barNode.createRelationshipTo(sourceNode, DistributionRelationshipTypes.AGGREGATED);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating Aggregation Relationship " + "between <" + barNode + "> and <" + sourceNode + ">", e);
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

        // check input
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
        if (bar.getCount() < 0) {
            LOGGER.error("Count of Bar cannot be less than zero");
            throw new IllegalArgumentException("Count of Bar cannot be less than zero");
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

        // check root node and bar node
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

        // update properties
        Transaction tx = graphDb.beginTx();
        try {
            if (bar.getColor() != null) {
                rootBarNode.setProperty(BAR_COLOR, convertColorToArray(bar.getColor()));
            }

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
    public void updateDistributionModelCount(Node rootAggregationNode, Integer count) throws DatabaseException {
        LOGGER.debug("start updateDistributionModelCount(<" + rootAggregationNode + ">, <" + count + ">");

        // validate input
        if (rootAggregationNode == null) {
            LOGGER.error("Input rootAggregationNode cannot be null");
            throw new IllegalArgumentException("Input rootAggregationNode cannot be null");
        }
        if (count == null) {
            LOGGER.error("Input count cannot be null");
            throw new IllegalArgumentException("Input count cannot be null");
        }
        if (count < 0) {
            LOGGER.error("Input count cannot be less than zero");
            throw new IllegalArgumentException("Input count cannot be less than zero");
        }

        // update property
        Transaction tx = graphDb.beginTx();
        try {
            rootAggregationNode.setProperty(COUNT, count);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on updating count of Distribution Model", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish updateDistributionModelCount()");
    }

    /**
     * Sets distribution model as current for analyzed model
     * 
     * @param analyzedModelRoot root of Analyzed Model
     * @param distributionModelRoot root of Distribution Model - can be null, and in this case
     *        current distribution model will be skipped
     */
    public void setCurrentDistributionModel(Node analyzedModelRoot, Node distributionModelRoot) throws DatabaseException {
        LOGGER.debug("start setCurrentDistributionModel(<" + analyzedModelRoot + ">, <" + distributionModelRoot + ">)");

        // check input
        if (analyzedModelRoot == null) {
            LOGGER.error("Input analyzedModelRoot cannot be null");
            throw new IllegalArgumentException("Input analyzedModelRoot cannot be null");
        }

        // make changes
        Transaction tx = graphDb.beginTx();
        try {

            if (distributionModelRoot == null) {
                analyzedModelRoot.removeProperty(CURRENT_DISTRIBUTION_MODEL);
            } else {
                analyzedModelRoot.setProperty(CURRENT_DISTRIBUTION_MODEL, distributionModelRoot.getProperty(NAME));
            }

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on setting current Distribution Model");
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish setCurrentDistributionModel()");
    }

    /**
     * Updates colors for Selected Bars of this Model
     * 
     * @param rootAggregationNode
     * @param leftBarColor
     * @param rightBarColor
     * @param selectedBarColor
     * @throws DatabaseException
     */
    public void updateSelectedBarColors(Node rootAggregationNode, Color leftBarColor, Color rightBarColor, Color selectedBarColor)
            throws DatabaseException {
        LOGGER.debug("start updateSelectedBarColors(<" + rootAggregationNode + ">, <" + leftBarColor + ">, <" + rightBarColor
                + ">, <" + selectedBarColor + ">)");

        // check input
        if (rootAggregationNode == null) {
            LOGGER.error("Input rootAggregationNode cannot be null");
            throw new IllegalArgumentException("Input rootAggregationNode cannot be null");
        }
        if (leftBarColor == null) {
            LOGGER.error("Input leftBarColor cannot be null");
            throw new IllegalArgumentException("Input leftBarColor cannot be null");
        }
        if (rightBarColor == null) {
            LOGGER.error("Input rightBarColor cannot be null");
            throw new IllegalArgumentException("Input rightBarColor cannot be null");
        }
        if (selectedBarColor == null) {
            LOGGER.error("Input selectedBarColor cannot be null");
            throw new IllegalArgumentException("Input selectedBarColor cannot be null");
        }

        // update properties
        Transaction tx = graphDb.beginTx();
        try {
            rootAggregationNode.setProperty(LEFT_COLOR, convertColorToArray(leftBarColor));
            rootAggregationNode.setProperty(RIGHT_COLOR, convertColorToArray(rightBarColor));
            rootAggregationNode.setProperty(SELECTED_COLOR, convertColorToArray(selectedBarColor));

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Exception on updating color properties of Distribution <" + rootAggregationNode + ">", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish updateSelectedBarColors()");
    }

    /**
     * Updates Palette of selected Distribution Model
     * 
     * @param rootAggregationNode
     * @param palette
     */
    public void updatePalette(Node rootAggregationNode, BrewerPalette palette) throws DatabaseException {
        LOGGER.debug("start updatePalette(<" + rootAggregationNode + ">, <" + palette + ">)");

        // check input
        if (rootAggregationNode == null) {
            LOGGER.error("Input rootAggregationNode cannot be null");
            throw new IllegalArgumentException("Input rootAggregationNode cannot be null");
        }
        if (palette == null) {
            LOGGER.error("Input palette cannot be null");
            throw new IllegalArgumentException("Input palette cannot be null");
        }

        // validate palette
        boolean isValid = false;
        for (BrewerPalette gisPalette : PlatformGIS.getColorBrewer().getPalettes()) {
            if (gisPalette.getName().equals(palette.getName())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            LOGGER.error("There is not such palette <" + palette + ">");
            throw new IllegalArgumentException("There is not such palette <" + palette + ">");
        }

        // update property
        Transaction tx = graphDb.beginTx();
        try {

            rootAggregationNode.setProperty(PALETTE, palette.getName());

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on updating Palette property of Node", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish updatePalette()");
    }

    /**
     * Create nodes and relations for User Defined distribution
     * 
     * @param distribution
     * @throws DatabaseException
     */
    public Node createUserDefinedDistribution(final Distribution distribution) throws DatabaseException {
        LOGGER.debug("start createUserDefinedDistribution(<" + distribution + ">)");

        // validate input
        if (distribution == null) {
            LOGGER.error("Distribution cannot be null");
            throw new IllegalArgumentException("Distribution cannot be null");
        }

        Iterable<Node> nodes = DISTRIBUTION_DATA_TRAVERSAL_DESCRIPTION.evaluator(new Evaluator() {

            @Override
            public Evaluation evaluate(Path arg0) {
                if (!arg0.endNode().hasProperty(UD_NAME) || arg0.endNode().getProperty(UD_NAME).equals(distribution.getData().getName())) {
                    return Evaluation.INCLUDE_AND_CONTINUE;
                } else {
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }

            }
        }).traverse(getReferenceNode()).nodes();

        if (nodes.iterator().hasNext()) {
            throw new IllegalArgumentException("Distribution with same name already exists");
        }
        Node result = null;

        Transaction tx = graphDb.beginTx();
        try {

            result = createNode(getReferenceNode(), UserDefinedDistrRelTypes.DISTRIBUTION, UserDefinedDistrNodeTypes.DISTRIBUTION);
            Node dataNode = createNode(result, UserDefinedDistrRelTypes.DATA, UserDefinedDistrNodeTypes.DATA);
            dataNode.setProperty(UD_NAME, distribution.getData().getName());
            dataNode.setProperty(UD_DATA_TYPE, distribution.getData().getDataType());
            dataNode.setProperty(UD_NODE_TYPE, distribution.getData().getNodeType());
            if (!StringUtils.isEmpty(distribution.getData().getPropertyName()))
                dataNode.setProperty(UD_PROPERTY_NAME, distribution.getData().getPropertyName());
            if (!StringUtils.isEmpty(distribution.getData().getDriveType()))
                dataNode.setProperty(UD_DRIVE_TYPE, distribution.getData().getDriveType());

            Node barsNode = createNode(result, UserDefinedDistrRelTypes.BARS, UserDefinedDistrNodeTypes.BARS);

            for (Bar bar : distribution.getBars().getBar()) {
                Node barNode = createNode(barsNode, UserDefinedDistrRelTypes.BAR, UserDefinedDistrNodeTypes.BAR);
                barNode.setProperty(UD_BAR_NAME, bar.getName());
                if (bar.getColor() != null) {
                    Node colorNode = createNode(barNode, UserDefinedDistrRelTypes.COLOR, UserDefinedDistrNodeTypes.COLOR);
                    colorNode.setProperty(UD_BAR_COLOR_RED, bar.getColor().getRed());
                    colorNode.setProperty(UD_BAR_COLOR_GREEN, bar.getColor().getGreen());
                    colorNode.setProperty(UD_BAR_COLOR_BLUE, bar.getColor().getBlue());
                }
                createFilterNode(barNode, bar.getFilter());
            }

            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("Error on creating User Defined Distribution", e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        LOGGER.debug("finish createUserDefinedDistribution()");

        return result;
    }

    /**
     * Find all user defined distributions in database
     * 
     * @return
     */
    public List<Distribution> findUserDefinedDistributions() {
        LOGGER.debug("start findUserDefinedDistributions()");

        List<Distribution> res = new ArrayList<Distribution>();
        Iterable<Node> nodes = DISTRIBUTION_TRAVERSAL_DESCRIPTION.traverse(getReferenceNode()).nodes();
        for (Node distrNode : nodes) {
            res.add(getDistributionByNode(distrNode));
        }

        LOGGER.debug("finish findUserDefinedDistributions()");

        return res;
    }

    private Distribution getDistributionByNode(Node distrNode) {
        Distribution distr = new Distribution();
        Node dataNode = distrNode.getSingleRelationship(UserDefinedDistrRelTypes.DATA, Direction.OUTGOING).getEndNode();

        Data data = new Data();
        data.setName(dataNode.getProperty(UD_NAME).toString());
        data.setNodeType(dataNode.getProperty(UD_NODE_TYPE).toString());
        data.setDataType(dataNode.getProperty(UD_DATA_TYPE).toString());
        data.setDriveType(getNonMandatoryProperty(dataNode, UD_DRIVE_TYPE));
        data.setPropertyName(getNonMandatoryProperty(dataNode, UD_PROPERTY_NAME));
        distr.setData(data);

        Bars bars = new Bars();
        Iterable<Node> barNodes = DISTRIBUTION_BARS_TRAVERSAL_DESCRIPTION.traverse(distrNode).nodes();
        for (Node barNode : barNodes) {
            Bar bar = new Bar();
            bar.setName(barNode.getProperty(UD_BAR_NAME).toString());
            if (barNode.hasRelationship(UserDefinedDistrRelTypes.COLOR)) {
                Node colorNode = barNode.getSingleRelationship(UserDefinedDistrRelTypes.COLOR, Direction.OUTGOING).getEndNode();
                org.amanzi.neo.model.distribution.xml.schema.Color color = new org.amanzi.neo.model.distribution.xml.schema.Color();
                color.setRed((Integer)colorNode.getProperty(UD_BAR_COLOR_RED));
                color.setGreen((Integer)colorNode.getProperty(UD_BAR_COLOR_GREEN));
                color.setBlue((Integer)colorNode.getProperty(UD_BAR_COLOR_BLUE));
                bar.setColor(color);
            }
            bar.setFilter(getFilterByNode(barNode.getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING)
                    .getEndNode()));
            bars.getBar().add(bar);
        }
        distr.setBars(bars);
        return distr;
    }

    private Filter getFilterByNode(Node filterNode) {
        Filter res = new Filter();
        res.setNodeType(filterNode.getProperty(UD_FILTER_NODE_TYPE).toString());
        res.setPropertyName(filterNode.getProperty(UD_FILTER_PROPERTY_NAME).toString());
        res.setFilterType(getNonMandatoryProperty(filterNode, UD_FILTER_TYPE));
        res.setExpressionType(getNonMandatoryProperty(filterNode, UD_FILTER_EXP_TYPE));
        res.setValue(getNonMandatoryProperty(filterNode, UD_FILTER_VALUE));
        if (filterNode.hasRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING)) {
            res.setUnderlyingFilter(getFilterByNode(filterNode.getSingleRelationship(UserDefinedDistrRelTypes.FILTER,
                    Direction.OUTGOING).getEndNode()));
        }
        return res;
    }

    private String getNonMandatoryProperty(Node node, String propertyName) {
        if (node.hasProperty(propertyName))
            return node.getProperty(propertyName).toString();
        return null;
    }

    private void createFilterNode(Node parentNode, Filter filter) throws DatabaseException {
        Node filterNode = createNode(parentNode, UserDefinedDistrRelTypes.FILTER, UserDefinedDistrNodeTypes.FILTER);
        filterNode.setProperty(UD_FILTER_NODE_TYPE, filter.getNodeType());
        filterNode.setProperty(UD_FILTER_PROPERTY_NAME, filter.getPropertyName());
        if (!StringUtils.isEmpty(filter.getFilterType()))
            filterNode.setProperty(UD_FILTER_TYPE, filter.getFilterType());
        if (!StringUtils.isEmpty(filter.getExpressionType()))
            filterNode.setProperty(UD_FILTER_EXP_TYPE, filter.getExpressionType());
        if (!StringUtils.isEmpty(filter.getValue()))
            filterNode.setProperty(UD_FILTER_VALUE, filter.getValue());
        if (filter.getUnderlyingFilter() != null)
            createFilterNode(filterNode, filter.getUnderlyingFilter());
    }

    public Node getReferenceNode() {
        return graphDb.getReferenceNode();
    }
}
