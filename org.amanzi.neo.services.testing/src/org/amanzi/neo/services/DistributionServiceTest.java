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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.impl.DistributionBar;
import org.amanzi.neo.services.DistributionService.DistributionNodeTypes;
import org.amanzi.neo.services.DistributionService.DistributionRelationshipTypes;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.apache.commons.lang.StringUtils;
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

    private final static String DEFAULT_BAR_NAME = "bar_name";

    /*
     * Distribution service
     */
    private DistributionService distributionService;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        clearServices();
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
        distributionService = new DistributionService(graphDatabaseService);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        cleanUpReferenceNode();
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodeWithoutParentNode() throws Exception {
        distributionService.findRootAggregationNode(null, DISTRIBUTION_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodeWithoutName() throws Exception {
        distributionService.findRootAggregationNode(getParentNode(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToFindRootAggregationNodeWithEmptyName() throws Exception {
        distributionService.findRootAggregationNode(getParentNode(), StringUtils.EMPTY);
    }

    @Test
    public void checkSingleResultOfSearch() throws Exception {
        Node parentNode = getParentNode();
        Node rootAggregation = createRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        Node result = distributionService.findRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        assertNotNull("Result of search cannot be null", result);
        assertEquals("Search found incorrect node", rootAggregation, result);
    }

    @Test
    public void checkObjectNotExists() throws Exception {
        Node parentNode = getParentNode();
        createRootAggregationNode(parentNode, DISTRIBUTION_NAME + DISTRIBUTION_NAME);

        Node result = distributionService.findRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        assertNull("Result of search should be null", result);
    }

    @Test
    public void checkMultipleSearch() throws Exception {
        Node parentNode = getParentNode();

        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i++) {
            createRootAggregationNode(parentNode, DISTRIBUTION_NAME + i);
        }

        for (int i = 0; i < NUMBER_OF_ROOT_AGGREGATIONS; i++) {
            Node result = distributionService.findRootAggregationNode(parentNode, DISTRIBUTION_NAME + i);

            assertNotNull("Result should not be null", result);

            assertEquals("Incorrect type of node", DistributionNodeTypes.ROOT_AGGREGATION,
                    NodeTypeManager.getType(DistributionService.getNodeType(result)));
            assertEquals("Incorrect count of node", NUMBER_OF_BARS, result.getProperty(DistributionService.COUNT));
            assertEquals("Incorrect name of node", DISTRIBUTION_NAME + i, result.getProperty(DistributionService.NAME));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAggregationRootWithoutParent() throws Exception {
        distributionService.createRootAggregationNode(null, DISTRIBUTION_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAggregationRootWithoutName() throws Exception {
        distributionService.createRootAggregationNode(getParentNode(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAggregationRootWithEmptyName() throws Exception {
        distributionService.createRootAggregationNode(getParentNode(), StringUtils.EMPTY);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void createDuplicatedRoot() throws Exception {
        Node parentNode = getParentNode();

        distributionService.createRootAggregationNode(parentNode, DISTRIBUTION_NAME);
        distributionService.createRootAggregationNode(parentNode, DISTRIBUTION_NAME);
    }

    @Test
    public void checkNoExceptionsOnCreate() throws Exception {
        distributionService.createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);
    }

    @Test
    public void checkPropertiesOfCreatedRootAggregationNode() throws Exception {
        Node result = distributionService.createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        assertNotNull("Result should not be null", result);

        assertEquals("Incorrect name of Root Aggregation Name", DISTRIBUTION_NAME, result.getProperty(DistributionService.NAME));
        assertEquals("Incorrect count of Root Aggregation Name", 0, result.getProperty(DistributionService.COUNT));
        assertEquals("Incorrect type of Root Aggregation Name", DistributionNodeTypes.ROOT_AGGREGATION.getId(),
                result.getProperty(DistributionService.TYPE));
    }

    @Test
    public void checkRelationshipOfCreatedRootAggregationNode() throws Exception {
        Node result = distributionService.createRootAggregationNode(getParentNode(), DISTRIBUTION_NAME);

        Iterator<Relationship> relationships = result.getRelationships().iterator();

        assertTrue("No relationships found", relationships.hasNext());
        relationships.next();
        assertFalse("Too much relationships found", relationships.hasNext());
    }

    @Test
    public void checkRootAggregationRelationshipProperties() throws Exception {
        Node parentNode = getParentNode();
        Node result = distributionService.createRootAggregationNode(parentNode, DISTRIBUTION_NAME);

        Iterator<Relationship> relationships = result.getRelationships().iterator();
        Relationship relationship = relationships.next();

        assertEquals("Invalid type of reationship", DistributionRelationshipTypes.ROOT_AGGREGATION, relationship.getType());
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

        assertEquals("Invalid type of reationship", DatasetRelationshipTypes.CHILD.name(), relationship.getType().name());
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
                incoming = DatasetRelationshipTypes.CHILD;
                outgoing = DatasetRelationshipTypes.NEXT;
            } else if (i == NUMBER_OF_BARS - 1) {
                // last element - need to check only incoming NEXT relationship
                previousNode = barNodes[i - 1];
                incoming = DatasetRelationshipTypes.NEXT;
            } else {
                // need to check incoming and outgoing NEXT relationship
                previousNode = barNodes[i - 1];
                nextNode = barNodes[i + 1];
                incoming = DatasetRelationshipTypes.NEXT;
                outgoing = DatasetRelationshipTypes.NEXT;
            }
            
            Node barNode = barNodes[i];
            
            //check incoming
            Relationship incomingLink = barNode.getSingleRelationship(incoming, Direction.INCOMING);
            assertNotNull("No incoming <" + incoming + "> relationship", incoming);
            //check previous node
            Node previous = incomingLink.getStartNode();
            assertEquals("Invalid previous Node", previousNode, previous);
            
            //check outgoing
            if (outgoing != null) {
                Relationship outgoingLink = barNode.getSingleRelationship(outgoing, Direction.OUTGOING);
                assertNotNull("No outgoing <" + outgoing + "> relationship", outgoingLink);
                //check next node
                Node next = outgoingLink.getEndNode();
                assertEquals("Invalid next Node", nextNode, next);
            }
        }
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
                    rootNode.createRelationshipTo(bar, DatasetRelationshipTypes.CHILD);
                } else {
                    previousBar.createRelationshipTo(bar, DatasetRelationshipTypes.NEXT);
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

            result.setProperty(NewAbstractService.TYPE, DistributionNodeTypes.ROOT_AGGREGATION.getId());
            result.setProperty(NewAbstractService.NAME, name);
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

    private IDistributionBar getDistributionBar(String name) {
        DistributionBar bar = new DistributionBar();

        bar.setColor(DEFAULT_BAR_COLOR);
        bar.setCount(DEFAULT_BAR_COUNT);
        bar.setName(name);

        return bar;
    }
}
