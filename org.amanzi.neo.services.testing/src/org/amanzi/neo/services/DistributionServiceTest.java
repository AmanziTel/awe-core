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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.impl.DistributionBar;
import org.amanzi.neo.model.distribution.xml.schema.Bar;
import org.amanzi.neo.model.distribution.xml.schema.Bars;
import org.amanzi.neo.model.distribution.xml.schema.Data;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.model.distribution.xml.schema.Filter;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.DistributionService.DistributionRelationshipTypes;
import org.amanzi.neo.services.DistributionService.UserDefinedDistrRelTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang.StringUtils;
import org.geotools.brewer.color.BrewerPalette;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * Tests on Distribution Service
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionServiceTest extends AbstractNeoServiceTest {

    /*
     * Name of test distribution
     */
    private final static String DISTRIBUTION_NAME = "distribution";

    private final static int NUMBER_OF_ROOT_AGGREGATIONS = 5;

    private final static int NUMBER_OF_BARS = 10;

    private final static Color DEFAULT_BAR_COLOR = Color.BLACK;

    private final static int DEFAULT_BAR_COUNT = 100;

    private final static INodeType DEFAULT_NODE_TYPE = NetworkElementNodeType.SECTOR;

    private final static String DEFAULT_BAR_NAME = "bar_name";

    private final static Color UPDATED_BAR_COLOR = Color.WHITE;

    private final static String UPDATED_BAR_NAME = "new_bar_name";

    private final static String DEFAULT_PROPERTY_NAME = "property";

    private final static int UPDATED_BAR_COUNT = 500;

    private final static Color DEFAULT_LEFT_COLOR = Color.WHITE;

    private final static Color DEFAULT_RIGHT_COLOR = Color.YELLOW;

    private final static Color DEFAULT_SELECTED_COLOR = Color.BLACK;

    private final static BrewerPalette DEFAULT_PALETTE = PlatformGIS.getColorBrewer().getPalettes()[PlatformGIS.getColorBrewer()
            .getPalettes().length / 2];

    private static final String[] USER_DEFINED_DISTR_NAMES = new String[] {"distr1", "distr2", "distr3"};

    private static final String DEF_DATA_DATA_TYPE = "NETWORK";
    private static final String DEF_DATA_NAME = "test_distr";
    private static final String DEF_DATA_NODE_TYPE = "SECTOR";
    private static final String DEF_DATA_PROP_NAME = "azimut";
    private static final String DEF_BAR_NAME = "BAR NAME";
    private static final int DEF_COLOR_RED = 100;
    private static final int DEF_COLOR_GREEN = 150;
    private static final int DEF_COLOR_BLUE = 200;
    private static final String DEF_FILTER_EXP_TYPE = "OR";
    private static final String DEF_FILTER_TYPE = "EQUALS";
    private static final String DEF_FILTER_PROP_NAME = "azimut";
    private static final String DEF_FILTER_VALUE = "13.44";
    private static final String DEF_FILTER_NODE_TYPE = "SECTOR";
    private static final String DEF_UNDERFILTER_TYPE = "MORE";
    private static final String DEF_UNDERFILTER_NODE_TYPE = "SECTOR";
    private static final String DEF_UNDERFILTER_PROP_NAME = "azimut2";
    private static final String DEF_UNDERFILTER_VALUE = "20";

    /*
     * Distribution service
     */
    private DistributionService distributionService;

    private IDistribution< ? > distribution;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        distributionService = NeoServiceFactory.getInstance().getDistributionService();
        distribution = getDistribution(DISTRIBUTION_NAME);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        cleanUpReferenceNode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodesWithoutParentNode() throws Exception {
        distributionService.findRootAggregationNodes(null);
    }
    
    public void checkFindRootAggregationNodesCount() throws Exception {
        Node parentNode = getParentNode();
        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i ++) {
            createRootAggregationNode(parentNode, DISTRIBUTION_NAME + i);
        }
        
        List<Node> res = distributionService.findRootAggregationNodes(parentNode);
        
        assertEquals("Search found incorrect count of nodes", NUMBER_OF_ROOT_AGGREGATIONS, res.size());        
    }

    public void checkFindRootAggregationNodes() throws Exception {
        Node parentNode = getParentNode();
        Set<Node> nodes = new HashSet<Node>();
        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i ++) {
            nodes.add(createRootAggregationNode(parentNode, DISTRIBUTION_NAME + i));
        }
        
        List<Node> res = distributionService.findRootAggregationNodes(parentNode);
        for (Node node : res) {
            assertTrue("Search found incorrect node", nodes.contains(node));
            nodes.remove(node);
        }        
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodeWithoutParentNode() throws Exception {
        distributionService.findRootAggregationNode(null, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodeWithoutName() throws Exception {
        distributionService.findRootAggregationNode(getParentNode(), null);
    }

    @Test
    public void checkSingleResultOfSearch() throws Exception {
        Node parentNode = getParentNode();
        Node rootAggregation = createRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        Node result = distributionService.findRootAggregationNode(parentNode, distribution);

        assertNotNull("Result of search cannot be null", result);
        assertEquals("Search found incorrect node", rootAggregation, result);
    }

    @Test
    public void checkObjectNotExists() throws Exception {
        Node parentNode = getParentNode();
        createRootAggregationNode(parentNode, DISTRIBUTION_NAME + DISTRIBUTION_NAME);

        Node result = distributionService.findRootAggregationNode(parentNode, distribution);

        assertNull("Result of search should be null", result);
    }

    @Test
    public void checkMultipleSearch() throws Exception {
        Node parentNode = getParentNode();

        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i++) {
            createRootAggregationNode(parentNode, DISTRIBUTION_NAME + i);
        }

        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i++) {
            Node result = distributionService.findRootAggregationNode(parentNode, getDistribution(DISTRIBUTION_NAME + i));

            assertNotNull("Result should not be null", result);

            assertEquals("Incorrect type of node", DistributionNodeTypes.ROOT_AGGREGATION,
                    NodeTypeManager.getType(DistributionService.getNodeType(result)));
            assertEquals("Incorrect count of node", NUMBER_OF_BARS, result.getProperty(DistributionService.COUNT));
            assertEquals("Incorrect name of node", DISTRIBUTION_NAME + i, result.getProperty(DistributionService.NAME));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAggregationRootWithoutParent() throws Exception {
        distributionService.createRootAggregationNode(null, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAggregationRootWithoutName() throws Exception {
        distributionService.createRootAggregationNode(getParentNode(), null);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void createDuplicatedRoot() throws Exception {
        Node parentNode = getParentNode();

        distributionService.createRootAggregationNode(parentNode, distribution);
        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test
    public void checkNoExceptionsOnCreate() throws Exception {
        distributionService.createRootAggregationNode(getParentNode(), distribution);
    }

    @Test
    public void checkPropertiesOfCreatedRootAggregationNode() throws Exception {
        Node result = distributionService.createRootAggregationNode(getParentNode(), distribution);

        assertNotNull("Result should not be null", result);

        assertEquals("Incorrect name of Root Aggregation Name", DISTRIBUTION_NAME, result.getProperty(DistributionService.NAME));
        assertEquals("Incorrect count of Root Aggregation Name", 0, result.getProperty(DistributionService.COUNT));
        assertEquals("Incorrect type of Root Aggregation Name", DistributionNodeTypes.ROOT_AGGREGATION.getId(),
                result.getProperty(DistributionService.TYPE));
    }

    @Test
    public void checkRelationshipOfCreatedRootAggregationNode() throws Exception {
        Node result = distributionService.createRootAggregationNode(getParentNode(), distribution);

        Iterator<Relationship> relationships = result.getRelationships().iterator();

        assertTrue("No relationships found", relationships.hasNext());
        relationships.next();
        assertFalse("Too much relationships found", relationships.hasNext());
    }

    @Test
    public void checkRootAggregationRelationshipProperties() throws Exception {
        Node parentNode = getParentNode();
        Node result = distributionService.createRootAggregationNode(parentNode, distribution);

        Iterator<Relationship> relationships = result.getRelationships().iterator();
        Relationship relationship = relationships.next();

        assertEquals("Invalid type of reationship", DistributionRelationshipTypes.ROOT_AGGREGATION.name(), relationship.getType().name());
        assertEquals("Invalid Direction of relationship", result, relationship.getEndNode());
        assertEquals("Invalid start node of relationship", parentNode, relationship.getStartNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindAggregationBarsWithoutRootNode() throws Exception {
        distributionService.findAggregationBars(null);
    }

    @Test
    public void chechNumberOfAggregationBarsInEmptyDB() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Iterable<Node> aggregationBars = distributionService.findAggregationBars(rootNode);

        assertNotNull("Result should not be empy", aggregationBars);

        assertFalse("Result should not contain any nodes", aggregationBars.iterator().hasNext());
    }

    @Test
    public void checkNumberOfExistingBars() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        createAggregationBars(rootNode);

        Iterator<Node> aggregationBars = distributionService.findAggregationBars(rootNode).iterator();

        int count = 0;
        while (aggregationBars.hasNext()) {
            count++;
            aggregationBars.next();
        }

        assertEquals("Unexpected size of Result", NUMBER_OF_BARS, count);
    }

    @Test
    public void checkPropertiesOfBars() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        List<Node> rawBars = createAggregationBars(rootNode);

        Iterable<Node> aggregationBars = distributionService.findAggregationBars(rootNode);

        int i = 0;

        for (Node bar : aggregationBars) {
            Node rawBar = rawBars.get(i);

            assertNotNull("Bar Node cannot be null", bar);
            assertEquals("Unexpected order of bars", rawBar, bar);

            assertEquals("Unexpected type of Bar", DistributionNodeTypes.AGGREGATION_BAR,
                    NodeTypeManager.getType(DistributionService.getNodeType(bar)));
            if (i != 0) {
                assertTrue("Unexpected color of Bar",
                        Arrays.equals(getColorArray(new Color(i)), (int[])bar.getProperty(DistributionService.BAR_COLOR)));
            } else {
                assertFalse("Unexpected color property", bar.hasProperty(DistributionService.BAR_COLOR));
            }
            assertEquals("Unexpected name of Bar", DISTRIBUTION_NAME + i, bar.getProperty(DistributionService.NAME));
            assertEquals("Unexpected count of Bar", i, bar.getProperty(DistributionService.COUNT));

            i++;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationBarWithoutRootNode() throws Exception {
        distributionService.createAggregationBarNode(null, getDistributionBar(DEFAULT_BAR_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationBarWithoutBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationBarWithoutName() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationBarWithEmptyName() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(StringUtils.EMPTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationBarWithNegativeCount() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(DISTRIBUTION_NAME, -DEFAULT_BAR_COUNT));
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void tryToCreateBarWithDuplicatedName() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));
    }

    @Test
    public void checkNoExceptionOnBarCreation() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));
    }

    @Test
    public void checkPropertiesOfCreatedBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Node barNode = distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));

        assertNotNull("Result of creation should not be null", barNode);

        assertEquals("Invalid type of Bar Node", DistributionNodeTypes.AGGREGATION_BAR,
                NodeTypeManager.getType(DistributionService.getNodeType(barNode)));
        assertEquals("Invalid name of Bar Node", DEFAULT_BAR_NAME, barNode.getProperty(DistributionService.NAME));
        assertEquals("Invalid count of Bar Node", DEFAULT_BAR_COUNT, barNode.getProperty(DistributionService.COUNT));
    }

    @Test
    public void checkRelationshipCountOfSingleBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Node barNode = distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));

        Iterator<Relationship> relationships = barNode.getRelationships().iterator();

        assertTrue("No relationships found", relationships.hasNext());
        relationships.next();
        assertFalse("Too much relationships found", relationships.hasNext());
    }

    @Test
    public void checkRelationshipTypeOfSingleBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Node barNode = distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME));

        Iterator<Relationship> relationships = barNode.getRelationships().iterator();
        Relationship relationship = relationships.next();

        assertEquals("Invalid type of reationship", DatasetRelationTypes.CHILD.name(), relationship.getType().name());
        assertEquals("Invalid Direction of relationship", barNode, relationship.getEndNode());
        assertEquals("Invalid start node of relationship", rootNode, relationship.getStartNode());
    }

    @Test
    public void checkRelationshipsForMultiBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Node[] barNodes = new Node[NUMBER_OF_BARS];
        for (int i = 0; i < NUMBER_OF_BARS; i++) {
            barNodes[i] = distributionService.createAggregationBarNode(rootNode, getDistributionBar(DEFAULT_BAR_NAME + i));
        }

        for (int i = 0; i < NUMBER_OF_BARS; i++) {
            Node previousNode = null;
            Node nextNode = null;
            RelationshipType incoming = null;
            RelationshipType outgoing = null;

            if (i == 0) {
                // first element - need to check CHILD incoming and NEXT outgoing relationship
                previousNode = rootNode;
                nextNode = barNodes[i + 1];
                incoming = DatasetRelationTypes.CHILD;
                outgoing = DatasetRelationTypes.NEXT;
            } else if (i == NUMBER_OF_BARS - 1) {
                // last element - need to check only incoming NEXT relationship
                previousNode = barNodes[i - 1];
                incoming = DatasetRelationTypes.NEXT;
            } else {
                // need to check incoming and outgoing NEXT relationship
                previousNode = barNodes[i - 1];
                nextNode = barNodes[i + 1];
                incoming = DatasetRelationTypes.NEXT;
                outgoing = DatasetRelationTypes.NEXT;
            }

            Node barNode = barNodes[i];

            // check incoming
            Relationship incomingLink = barNode.getSingleRelationship(incoming, Direction.INCOMING);
            assertNotNull("No incoming <" + incoming + "> relationship", incoming);
            // check previous node
            Node previous = incomingLink.getStartNode();
            assertEquals("Invalid previous Node", previousNode, previous);

            // check outgoing
            if (outgoing != null) {
                Relationship outgoingLink = barNode.getSingleRelationship(outgoing, Direction.OUTGOING);
                assertNotNull("No outgoing <" + outgoing + "> relationship", outgoingLink);
                // check next node
                Node next = outgoingLink.getEndNode();
                assertEquals("Invalid next Node", nextNode, next);
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationWithoutSourceNode() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNode = createAggregationBars(rootNode);

        distributionService.createAggregation(barNode.get(0), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateAggregationWithoutBarNode() throws Exception {
        distributionService.createAggregation(null, createSourceNode());
    }

    @Test
    public void checkRelationshipBetweenBarAndSource() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        Node sourceNode = createSourceNode();

        distributionService.createAggregation(barNode, sourceNode);

        assertTrue("BarNode should have at least one outgouing relationship",
                barNode.hasRelationship(DistributionRelationshipTypes.AGGREGATED, Direction.OUTGOING));
    }

    @Test
    public void checkRelationshipBetweenSourceNodeAndBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        Node sourceNode = createSourceNode();

        distributionService.createAggregation(barNode, sourceNode);

        assertTrue("BarNode should have at least one outgouing relationship",
                sourceNode.hasRelationship(DistributionRelationshipTypes.AGGREGATED, Direction.INCOMING));
    }

    @Test
    public void checkNodeOfAggregationRelationship() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        Node sourceNode = createSourceNode();

        distributionService.createAggregation(barNode, sourceNode);

        Relationship relFromBar = barNode.getSingleRelationship(DistributionRelationshipTypes.AGGREGATED, Direction.OUTGOING);
        Relationship relToSource = sourceNode.getSingleRelationship(DistributionRelationshipTypes.AGGREGATED, Direction.INCOMING);

        assertTrue("Incorrect incoming and outgoing relationship for Aggregation", relFromBar.equals(relToSource));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdateDistributionBarWithoutNode() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        distributionService.updateDistributionBar(rootNode, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdateDistirubitonBarWithoutNode() throws Exception {
        distributionService.updateDistributionBar(null, getDistributionBarInstance(null, UPDATED_BAR_NAME, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdateBarFromIncorrectDataStructure() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);

        rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME + UPDATED_BAR_NAME);

        IDistributionBar bar = getDistributionBarInstance(barNodes.get(1), UPDATED_BAR_NAME, true);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSkipNameOfBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        IDistributionBar bar = getDistributionBarInstance(barNode, null, true);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetEmptyNameForBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        IDistributionBar bar = getDistributionBarInstance(barNode, StringUtils.EMPTY, true);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdatePropertyWithoutRootElementNode() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        createAggregationBars(rootNode);

        IDistributionBar bar = getDistributionBarInstance(null, UPDATED_BAR_NAME, true);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdatePropertyWithoutRootElement() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        createAggregationBars(rootNode);

        IDistributionBar bar = getDistributionBarInstance(null, UPDATED_BAR_NAME, true);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetNegativeCountForBar() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        IDistributionBar bar = getDistributionBarInstance(barNode, UPDATED_BAR_NAME, true, -UPDATED_BAR_COUNT);

        distributionService.updateDistributionBar(rootNode, bar);
    }

    @Test
    public void checkUpdatedProperties() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
        List<Node> barNodes = createAggregationBars(rootNode);
        Node barNode = barNodes.get(0);

        IDistributionBar bar = getDistributionBarInstance(barNode, UPDATED_BAR_NAME, true);

        distributionService.updateDistributionBar(rootNode, bar);

        assertEquals("incorrect name of bar", UPDATED_BAR_NAME, barNode.getProperty(AbstractService.NAME));
        assertEquals("incorrect name of bar", UPDATED_BAR_COUNT, barNode.getProperty(DistributionService.COUNT));
        assertTrue("incorrect color of bar",
                Arrays.equals(getColorArray(UPDATED_BAR_COLOR), (int[])barNode.getProperty(DistributionService.BAR_COLOR)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdateModelCountWithoutRootNode() throws Exception {
        distributionService.updateDistributionModelCount(null, UPDATED_BAR_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdateModelCountWithoutCount() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateDistributionModelCount(rootNode, null);
    }

    @Test
    public void checkUpdatedCount() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateDistributionModelCount(rootNode, UPDATED_BAR_COUNT);

        assertEquals("Incorrect updated count", UPDATED_BAR_COUNT, rootNode.getProperty(DistributionService.COUNT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetNegativeCount() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateDistributionModelCount(rootNode, -UPDATED_BAR_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetModelAsCurrentWithoutAnalyzedModelRoo() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.setCurrentDistributionModel(null, rootNode);
    }

    @Test
    public void setModelAsCurrent() throws Exception {
        Node parentNode = getParentNode();
        Node rootNode = createRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        distributionService.setCurrentDistributionModel(parentNode, rootNode);

        assertEquals("Unexpected name of current distribution", DISTRIBUTION_NAME,
                parentNode.getProperty(DistributionService.CURRENT_DISTRIBUTION_MODEL));
    }

    @Test
    public void skipCurrentModel() throws Exception {
        Node parentNode = getParentNode();
        Node rootNode = createRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        distributionService.setCurrentDistributionModel(parentNode, rootNode);
        distributionService.setCurrentDistributionModel(parentNode, null);

        assertFalse("Parent should not have such property", parentNode.hasProperty(DistributionService.CURRENT_DISTRIBUTION_MODEL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindDistributionWithoutName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(null);
        distributionService.findRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindDistributionWithEmptyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(StringUtils.EMPTY);
        distributionService.findRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindDistributionWithoutPropertyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getPropertyName()).thenReturn(null);

        distributionService.findRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindDistributionWithEmptyPropertyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getPropertyName()).thenReturn(StringUtils.EMPTY);

        distributionService.findRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindDistributionWithoutNodeType() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getNodeType()).thenReturn(null);

        distributionService.findRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithoutName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(null);
        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithEmptyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(StringUtils.EMPTY);
        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithPropertyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getPropertyName()).thenReturn(null);

        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithEmptyPropertyName() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getPropertyName()).thenReturn(StringUtils.EMPTY);

        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateDistributionWithoutNodeType() throws Exception {
        Node parentNode = getParentNode();
        IDistribution< ? > distribution = getDistribution(DEFAULT_PROPERTY_NAME);
        when(distribution.getNodeType()).thenReturn(null);

        distributionService.createRootAggregationNode(parentNode, distribution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetColorsWhenRootNodeIsNull() throws Exception {
        distributionService.updateSelectedBarColors(null, DEFAULT_LEFT_COLOR, DEFAULT_RIGHT_COLOR, DEFAULT_SELECTED_COLOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetColorsWhenLeftColorIsNull() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateSelectedBarColors(rootNode, null, DEFAULT_RIGHT_COLOR, DEFAULT_SELECTED_COLOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetColorsWhenRightColorIsNull() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateSelectedBarColors(rootNode, DEFAULT_LEFT_COLOR, null, DEFAULT_SELECTED_COLOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToSetColorsWhenSelectedColorIsNull() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateSelectedBarColors(rootNode, DEFAULT_LEFT_COLOR, DEFAULT_RIGHT_COLOR, null);
    }

    @Test
    public void checkColorPropertiesOfNode() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updateSelectedBarColors(rootNode, DEFAULT_LEFT_COLOR, DEFAULT_RIGHT_COLOR, DEFAULT_SELECTED_COLOR);

        assertTrue("Unexpected Left Color",
                Arrays.equals(getColorArray(DEFAULT_LEFT_COLOR), (int[])rootNode.getProperty(DistributionService.LEFT_COLOR)));
        assertTrue("Unexpected Right Color",
                Arrays.equals(getColorArray(DEFAULT_RIGHT_COLOR), (int[])rootNode.getProperty(DistributionService.RIGHT_COLOR)));
        assertTrue(
                "Unexpected Selected Color",
                Arrays.equals(getColorArray(DEFAULT_SELECTED_COLOR),
                        (int[])rootNode.getProperty(DistributionService.SELECTED_COLOR)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdatePaletteWithoutRootNode() throws Exception {
        distributionService.updatePalette(null, DEFAULT_PALETTE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdatePaletteWithoutPalette() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updatePalette(rootNode, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToUpdatePaletteWithUnknownPalette() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updatePalette(rootNode, new BrewerPalette());
    }

    @Test
    public void checkPaletteChanges() throws Exception {
        Node rootNode = createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        distributionService.updatePalette(rootNode, DEFAULT_PALETTE);

        assertEquals("Unexpected name of Palette", DEFAULT_PALETTE.getName(), rootNode.getProperty(DistributionService.PALETTE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryCreateUserDefinedNullDistribution() throws Exception {
        distributionService.createUserDefinedDistribution(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryCreateNotUniqueUserDefinedDistribution() throws Exception {
        Distribution distribution = createXmlDistribution();
        distributionService.createUserDefinedDistribution(distribution);
        distributionService.createUserDefinedDistribution(distribution);
    }

    @Test
    public void checkCreateUserDefinedDistributionRelations() throws Exception {
        Distribution distribution = createXmlDistribution();

        Node res = distributionService.createUserDefinedDistribution(distribution);
        assertNotNull("Relationship DISTRIBUTION not found",
                res.getRelationships(UserDefinedDistrRelTypes.DISTRIBUTION, Direction.INCOMING));
        assertNotNull("Relationship DATA not found", res.getSingleRelationship(UserDefinedDistrRelTypes.DATA, Direction.OUTGOING));
        Relationship barsRel = res.getSingleRelationship(UserDefinedDistrRelTypes.BARS, Direction.OUTGOING);
        assertNotNull("Relationship BARS not found", barsRel);
        Relationship barRel = barsRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.BAR, Direction.OUTGOING);
        assertNotNull("Relationship BAR not found", barRel);
        if (distribution.getBars().getBar().get(0).getColor() != null) {
            assertNotNull("Relationship COLOR not found",
                    barRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.COLOR, Direction.OUTGOING));
        } else {
            assertNull("Relationship COLOR found",
                    barRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.COLOR, Direction.OUTGOING));
        }
        Relationship filterRel = barRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING);
        assertNotNull("Relationship FILTER from BAR Node not found", filterRel);
        if (distribution.getBars().getBar().get(0).getFilter().getUnderlyingFilter() != null) {
            assertNotNull("Relationship FILTER from FILTER Node not found",
                    filterRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING));
        } else {
            assertNull("Relationship FILTER from FILTER Node found",
                    filterRel.getEndNode().getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING));
        }
    }

    @Test
    public void checkCreateUserDefinedDistributionProperties() throws Exception {
        Distribution distribution = createXmlDistribution();

        Node res = distributionService.createUserDefinedDistribution(distribution);
        Node refNode = res.getSingleRelationship(UserDefinedDistrRelTypes.DISTRIBUTION, Direction.INCOMING).getStartNode();
        assertEquals("Reference node not found", refNode, distributionService.getReferenceNode());
        Node data = res.getSingleRelationship(UserDefinedDistrRelTypes.DATA, Direction.OUTGOING).getEndNode();

        checkProperty(DistributionService.UD_NAME, data, distribution.getData().getName(), true);
        checkProperty(DistributionService.UD_DATA_TYPE, data, distribution.getData().getDataType(), true);
        checkProperty(DistributionService.UD_NODE_TYPE, data, distribution.getData().getNodeType(), true);
        checkProperty(DistributionService.UD_PROPERTY_NAME, data, distribution.getData().getPropertyName(), false);
        checkProperty(DistributionService.UD_DRIVE_TYPE, data, distribution.getData().getDriveType(), false);

        Node barNode = res.getSingleRelationship(UserDefinedDistrRelTypes.BARS, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(UserDefinedDistrRelTypes.BAR, Direction.OUTGOING).getEndNode();
        Bar bar = distribution.getBars().getBar().get(0);

        checkProperty(DistributionService.UD_BAR_NAME, barNode, bar.getName(), true);
        if (bar.getColor() != null) {
            Node colorNode = barNode.getSingleRelationship(UserDefinedDistrRelTypes.COLOR, Direction.OUTGOING).getEndNode();
            checkProperty(DistributionService.UD_BAR_COLOR_RED, colorNode, bar.getColor().getRed(), true);
            checkProperty(DistributionService.UD_BAR_COLOR_GREEN, colorNode, bar.getColor().getGreen(), true);
            checkProperty(DistributionService.UD_BAR_COLOR_BLUE, colorNode, bar.getColor().getBlue(), true);
        }

        Node filterNode = barNode.getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING).getEndNode();
        Filter filter = bar.getFilter();

        checkUserDefinedDistributionFilter(filterNode, filter);
    }

    private void checkUserDefinedDistributionFilter(Node filterNode, Filter filter) {
        checkProperty(DistributionService.UD_FILTER_TYPE, filterNode, filter.getFilterType(), false);
        checkProperty(DistributionService.UD_FILTER_EXP_TYPE, filterNode, filter.getExpressionType(), false);
        checkProperty(DistributionService.UD_FILTER_NODE_TYPE, filterNode, filter.getNodeType(), true);
        checkProperty(DistributionService.UD_PROPERTY_NAME, filterNode, filter.getPropertyName(), true);
        checkProperty(DistributionService.UD_FILTER_VALUE, filterNode, filter.getValue(), false);
        if (filter.getUnderlyingFilter() != null) {
            Node underFilterNode = filterNode.getSingleRelationship(UserDefinedDistrRelTypes.FILTER, Direction.OUTGOING)
                    .getEndNode();
            checkUserDefinedDistributionFilter(underFilterNode, filter.getUnderlyingFilter());
        }
    }

    @Test
    public void checkUserDefinedDistributionsSize() throws Exception {
        for (String name : USER_DEFINED_DISTR_NAMES) {
            Distribution distribution = createXmlDistribution(name);
            distributionService.createUserDefinedDistribution(distribution);
        }

        List<Distribution> distributions = distributionService.findUserDefinedDistributions();

        assertEquals("Incorrect User Defined Distributions count", USER_DEFINED_DISTR_NAMES.length, distributions.size());
    }

    @Test
    public void checkUserDefinedDistributions() throws Exception {
        Distribution distrBefore = createXmlDistribution();
        distributionService.createUserDefinedDistribution(distrBefore);

        List<Distribution> distributions = distributionService.findUserDefinedDistributions();

        Distribution distrAfter = distributions.get(0);
        assertNotNull("Distribution should be not null", distrAfter);
        Data dataBefore = distrBefore.getData();
        Data dataAfter = distrAfter.getData();
        assertNotNull("Data of Distribution should be not null", distrAfter.getData());
        checkProperty(DistributionService.UD_NAME, dataBefore.getName(), dataAfter.getName(), true);
        checkProperty(DistributionService.UD_DATA_TYPE, dataBefore.getDataType(), dataAfter.getDataType(), true);
        checkProperty(DistributionService.UD_NODE_TYPE, dataBefore.getNodeType(), dataAfter.getNodeType(), true);
        checkProperty(DistributionService.UD_PROPERTY_NAME, dataBefore.getPropertyName(), dataAfter.getPropertyName(), false);
        checkProperty(DistributionService.UD_DRIVE_TYPE, dataBefore.getDriveType(), dataAfter.getDriveType(), false);

        assertNotNull("Bars of Distribution should be not null", distrAfter.getBars());
        assertEquals("Incorrect Bar of Distribution count", distrBefore.getBars().getBar().size(), distrAfter.getBars().getBar()
                .size());

        Bar barBefore = distrBefore.getBars().getBar().get(0);
        Bar barAfter = distrAfter.getBars().getBar().get(0);
        checkProperty(DistributionService.UD_BAR_NAME, barBefore.getName(), barAfter.getName(), true);
        if (barBefore.getColor() != null) {
            assertNotNull("Bar Color should be not null", barAfter.getColor());
            checkProperty(DistributionService.UD_BAR_COLOR_RED, barBefore.getColor().getRed(), barAfter.getColor().getRed(), true);
            checkProperty(DistributionService.UD_BAR_COLOR_GREEN, barBefore.getColor().getGreen(), barAfter.getColor().getGreen(),
                    true);
            checkProperty(DistributionService.UD_BAR_COLOR_BLUE, barBefore.getColor().getBlue(), barAfter.getColor().getBlue(),
                    true);
        }

        assertNotNull("Filter should be not null", barAfter.getFilter());
        checkUserDefinedDistributionFilter(barBefore.getFilter(), barAfter.getFilter());
    }

    private void checkUserDefinedDistributionFilter(Filter filterBefore, Filter filterAfter) {
        checkProperty(DistributionService.UD_FILTER_TYPE, filterBefore.getFilterType(), filterAfter.getFilterType(), false);
        checkProperty(DistributionService.UD_FILTER_EXP_TYPE, filterBefore.getExpressionType(), filterAfter.getExpressionType(),
                false);
        checkProperty(DistributionService.UD_FILTER_NODE_TYPE, filterBefore.getNodeType(), filterAfter.getNodeType(), true);
        checkProperty(DistributionService.UD_PROPERTY_NAME, filterBefore.getPropertyName(), filterAfter.getPropertyName(), true);
        checkProperty(DistributionService.UD_FILTER_VALUE, filterBefore.getValue(), filterAfter.getValue(), false);
        if (filterBefore.getUnderlyingFilter() != null) {
            assertNotNull("Underlying Filter should be not null", filterAfter.getUnderlyingFilter());
            checkUserDefinedDistributionFilter(filterBefore.getUnderlyingFilter(), filterAfter.getUnderlyingFilter());
        }
    }

    private void checkProperty(String propertyName, Object valueBefore, Object valueAfter, boolean mandatory) {
        if (mandatory || valueBefore != null) {
            assertNotNull(propertyName + " should be not null", valueAfter);
            assertEquals("Incorrect " + propertyName, valueBefore, valueAfter);
        } else {
            assertNull(propertyName + " should be null", valueAfter);
        }
    }

    public void checkProperty(String propertyName, Node node, Object value, boolean mandatory) {
        if (mandatory || value != null) {
            assertNotNull(propertyName + " should be not null", node.getProperty(propertyName, null));
            assertEquals("Incorrect " + propertyName, node.getProperty(propertyName), value);
        } else {
            assertNull(propertyName + " should be null", node.getProperty(propertyName, null));
        }
    }

    private IDistributionBar getDistributionBarInstance(Node barNode, String name, boolean createRootElement, int count) {
        DistributionBar result = getDistributionBarInstance(barNode, name, createRootElement);

        result.setCount(count);

        return result;
    }

    /**
     * Creates IDistributionBar
     * 
     * @param barNode
     * @param name
     * @return
     */
    private DistributionBar getDistributionBarInstance(Node barNode, String name, boolean createRootElement) {
        DistributionBar result = new DistributionBar(mock(IDistributionModel.class));

        if (createRootElement) {
            result.setRootElement(new DataElement(barNode));
        }
        result.setColor(UPDATED_BAR_COLOR);
        result.setName(name);
        result.setCount(UPDATED_BAR_COUNT);

        return result;
    }

    /**
     * Creates source node for Aggregation
     * 
     * @return
     */
    private Node createSourceNode() throws Exception {
        Node node = null;

        Transaction tx = graphDatabaseService.beginTx();
        try {
            node = graphDatabaseService.createNode();
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw e;
        } finally {
            tx.finish();
        }

        return node;
    }

    /**
     * Creates list of aggregation bars
     * 
     * @param rootNode
     * @return
     * @throws Exception
     */
    private List<Node> createAggregationBars(Node rootNode) throws Exception {
        List<Node> result = new ArrayList<Node>();

        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node previousBar = null;
            for (int i = 0; i < NUMBER_OF_BARS; i++) {
                Node bar = graphDatabaseService.createNode();

                bar.setProperty(DistributionService.TYPE, DistributionNodeTypes.AGGREGATION_BAR.getId());
                bar.setProperty(DistributionService.COUNT, i);
                bar.setProperty(DistributionService.NAME, DISTRIBUTION_NAME + i);

                if (previousBar == null) {
                    rootNode.createRelationshipTo(bar, DatasetRelationTypes.CHILD);
                } else {
                    previousBar.createRelationshipTo(bar, DatasetRelationTypes.NEXT);
                }

                if (i != 0) {
                    bar.setProperty(DistributionService.BAR_COLOR, getColorArray(new Color(i)));
                }

                result.add(bar);

                previousBar = bar;
            }

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw e;
        } finally {
            tx.finish();
        }

        return result;
    }

    /**
     * Creates Root aggregation Node
     * 
     * @param parentNode
     * @param name
     * @return
     * @throws Exception
     */
    private Node createRootAggregationNode(Node parentNode, String name) throws Exception {
        Transaction tx = graphDatabaseService.beginTx();

        Node result = null;
        try {
            result = graphDatabaseService.createNode();

            parentNode.createRelationshipTo(result, DistributionRelationshipTypes.ROOT_AGGREGATION);

            result.setProperty(AbstractService.TYPE, DistributionNodeTypes.ROOT_AGGREGATION.getId());
            result.setProperty(AbstractService.NAME, name);
            result.setProperty(DistributionService.NODE_TYPE, DEFAULT_NODE_TYPE.getId());
            result.setProperty(DistributionService.PROPERTY_NAME, DEFAULT_PROPERTY_NAME);
            result.setProperty(DistributionService.COUNT, NUMBER_OF_BARS);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw e;
        } finally {
            tx.finish();
        }

        return result;
    }

    /**
     * Creates parent node for Distribution structure
     * 
     * @return
     * @throws Exception
     */
    private Node getParentNode() throws Exception {
        Transaction tx = graphDatabaseService.beginTx();

        Node result = null;

        try {
            result = graphDatabaseService.createNode();
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw e;
        } finally {
            tx.finish();
        }

        return result;
    }

    private IDistributionBar getDistributionBar(String name, int count) {
        DistributionBar result = getDistributionBar(name);

        result.setCount(count);

        return result;
    }

    private DistributionBar getDistributionBar(String name) {
        DistributionBar bar = new DistributionBar(mock(IDistributionModel.class));

        bar.setColor(DEFAULT_BAR_COLOR);
        bar.setCount(DEFAULT_BAR_COUNT);
        bar.setName(name);

        return bar;
    }

    @SuppressWarnings("rawtypes")
    private IDistribution< ? > getDistribution(String name) {
        IDistribution result = mock(IDistribution.class);

        when(result.getName()).thenReturn(name);
        when(result.getNodeType()).thenReturn(DEFAULT_NODE_TYPE);
        when(result.getPropertyName()).thenReturn(DEFAULT_PROPERTY_NAME);

        return result;
    }

    protected Distribution createXmlDistribution() {
        return createXmlDistribution(null);
    }

    protected Distribution createXmlDistribution(String name) {
        Distribution distr = new Distribution();
        Data data = new Data();
        data.setDataType(DEF_DATA_DATA_TYPE);
        data.setNodeType(DEF_DATA_NODE_TYPE);
        data.setPropertyName(DEF_DATA_PROP_NAME);
        data.setName(StringUtils.isEmpty(name) ? DEF_DATA_NAME : name);
        distr.setData(data);
        Bars bars = new Bars();
        Bar bar = new Bar();
        bar.setName(DEF_BAR_NAME);
        bar.setColor(new org.amanzi.neo.model.distribution.xml.schema.Color(DEF_COLOR_RED, DEF_COLOR_GREEN, DEF_COLOR_BLUE));
        Filter filter = new Filter();
        filter.setExpressionType(DEF_FILTER_EXP_TYPE);
        filter.setFilterType(DEF_FILTER_TYPE);
        filter.setPropertyName(DEF_FILTER_PROP_NAME);
        filter.setNodeType(DEF_FILTER_NODE_TYPE);
        filter.setValue(DEF_FILTER_VALUE);
        Filter underFilter = new Filter();
        underFilter.setFilterType(DEF_UNDERFILTER_TYPE);
        underFilter.setPropertyName(DEF_UNDERFILTER_PROP_NAME);
        underFilter.setNodeType(DEF_UNDERFILTER_NODE_TYPE);
        underFilter.setValue(DEF_UNDERFILTER_VALUE);
        filter.setUnderlyingFilter(underFilter);
        bar.setFilter(filter);
        bars.getBar().add(bar);
        distr.setBars(bars);

        return distr;
    }

}
