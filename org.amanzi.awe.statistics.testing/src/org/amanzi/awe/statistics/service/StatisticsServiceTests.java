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

package org.amanzi.awe.statistics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * tests for statistics service
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsServiceTests extends AbstractNeoServiceTest {

    private static Logger LOGGER = Logger.getLogger(StatisticsServiceTests.class);
    private static final String DATASET_NAME = "dataset";
    private static final String STATISTIC_ROOT_NAME = DATASET_NAME + " Statistics";
    private static final DatasetTypes DATASET_TYPE = DatasetTypes.DRIVE;
    private static final String SCELL_NAME = "scell name";
    private static Node datasetNode = null;
    private Node projectNode;
    private StatisticsService statisticsService;
    private DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
    private static final int ARRAYS_SIZE = 5;

    @BeforeClass
    public static final void beforeClass() {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
    }

    @AfterClass
    public static final void afterClass() {
        stopDb();
        clearDb();
    }

    @Before
    public void before() {
        statisticsService = StatisticsService.getInstance();
        initProjectNode();
    }

    @After
    public final void after() throws DatabaseException {
        try {
            cleanUpReferenceNode();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStatisticModelRootIfParentNull() throws DatabaseException {
        LOGGER.info("testCreateStatisticModelRootIfParentNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        statisticsService.createStatisticsModelRoot(null, STATISTIC_ROOT_NAME, false);
    }

    @Test(expected = DatabaseException.class)
    public void testCreateStatisticModelRootIfNameIsNull() throws DatabaseException {
        LOGGER.info("testCreateStatisticModelRootIfNameIsNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        statisticsService.createStatisticsModelRoot(datasetNode, null, false);
    }

    @Test
    public void testCreateStatisticModelRoot() throws DatabaseException {
        LOGGER.info("testCreateStatisticModelRoot started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        statisticsService.createStatisticsModelRoot(datasetNode, STATISTIC_ROOT_NAME, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindStatisticModelIfParentNull() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfParentNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        statisticsService.findStatistic(null, STATISTIC_ROOT_NAME);
    }

    @Test
    public void testFindStatisticModelIfExist() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statisticsModelRoot = createStatisticsRoot(datasetNode);
        Node result = statisticsService.findStatistic(datasetNode, STATISTIC_ROOT_NAME);
        Assert.assertEquals("Different result detected. The same expected", statisticsModelRoot, result);
    }

    @Test
    public void testFindStatisticModelIfNotExist() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node result = statisticsService.findStatistic(datasetNode, STATISTIC_ROOT_NAME);
        Assert.assertNull("Null expected. Node must not be found", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourceIfOneOfParameterNull() throws DatabaseException {
        LOGGER.info("testAddSourceIfOneOfParameterNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        statisticsService.addSource(null, null);
    }

    @Test
    public void testAddSource() throws DatabaseException {
        LOGGER.info("testAddSource started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node periodH = createLevelNode(statRoot, Period.HOURLY.getId());
        Node periodD = createLevelNode(statRoot, Period.DAILY.getId());
        statisticsService.addSource(periodD, periodH);
        Assert.assertNotNull("SOURCE relationship not exists",
                periodD.getSingleRelationship(StatisticsRelationshipTypes.SOURCE, Direction.OUTGOING));
    }

    @Test
    public void testGetSources() throws DatabaseException {
        LOGGER.info("testGetSources started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node periodH = createLevelNode(statRoot, Period.HOURLY.getId());
        Node periodD = createLevelNode(statRoot, Period.DAILY.getId());
        datasetService.createRelationship(periodD, periodH, StatisticsRelationshipTypes.SOURCE);

        Iterator<Node> sources = statisticsService.getSources(periodD).iterator();
        Assert.assertEquals("Source not found", periodH, sources.next());
    }

    @Test
    public void testFindNodeInChain() {
        LOGGER.info("testFindNodeInChain started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node periodH = createLevelNode(statRoot, Period.HOURLY.getId());
        List<Node> chainList = createChildNextChain(periodH, ARRAYS_SIZE, StatisticsNodeTypes.S_ROW);
        List<Node> innerBranch = createChildNextChain(chainList.get(3), ARRAYS_SIZE, StatisticsNodeTypes.S_ROW);
        for (Node storedNode : chainList) {
            Node findedNode = statisticsService.findNodeInChain(periodH, DriveModel.TIMESTAMP,
                    storedNode.getProperty(DriveModel.TIMESTAMP));
            Assert.assertEquals("Unexpected nodes.", storedNode, findedNode);
        }
        Node innerBranchNode = innerBranch.get(3);
        Node findedNode = statisticsService.findNodeInChain(periodH, DriveModel.TIMESTAMP,
                innerBranchNode.getProperty(DriveModel.TIMESTAMP));
        Assert.assertNull(findedNode);
    }

    @Test
    public void testCreateSRowIfNotExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCreateSRowIfNotExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node periodH = createLevelNode(statRoot, Period.HOURLY.getId());
        Long timestamp = (long)(Math.random() * 100000);
        statisticsService.createSRow(periodH, timestamp, true);
        Iterator<Node> srows = datasetService.getChildrenChainTraverser(periodH).iterator();
        Node existed = srows.next();
        Assert.assertEquals("Unexpected timestamp found", existed.getProperty(DriveModel.TIMESTAMP), timestamp);
        Assert.assertEquals("Unexpected type found", existed.getProperty(DatasetService.TYPE), StatisticsNodeTypes.S_ROW.getId());
    }

    @Test(expected = DatabaseException.class)
    public void testCreateSRowIfExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCreateSRowIfExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node periodH = createLevelNode(statRoot, Period.HOURLY.getId());
        Long timestamp = (long)(Math.random() * 100000);
        statisticsService.createSRow(periodH, timestamp, true);
        statisticsService.createSRow(periodH, timestamp, true);
    }

    @Test
    public void testCreateSCellIfNotExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCreateSCell started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node dimension = createDimensionNode(statRoot, DimensionTypes.TIME);
        Node periodH = createLevelNode(dimension, Period.HOURLY.getId());
        Long timestamp = (long)(Math.random() * 100000);
        statisticsService.createSRow(periodH, timestamp, true);
        Iterator<Node> srows = datasetService.getChildrenChainTraverser(periodH).iterator();
        Node existedSrow = srows.next();
        statisticsService.createSCell(existedSrow, SCELL_NAME, true);
        Iterator<Node> scells = datasetService.getChildrenChainTraverser(existedSrow).iterator();
        Node existedSCell = scells.next();
        Assert.assertEquals("Unexpected name found", existedSCell.getProperty(DatasetService.NAME), SCELL_NAME);
        Assert.assertEquals("Unexpected type found", existedSCell.getProperty(DatasetService.TYPE),
                StatisticsNodeTypes.S_CELL.getId());
    }

    @Test(expected = DatabaseException.class)
    public void testCreateSCellIfExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCreateSCell started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node dimension = createDimensionNode(statRoot, DimensionTypes.TIME);
        Node periodH = createLevelNode(dimension, Period.HOURLY.getId());
        Long timestamp = (long)(Math.random() * 100000);
        statisticsService.createSRow(periodH, timestamp, true);
        Iterator<Node> srows = datasetService.getChildrenChainTraverser(periodH).iterator();
        Node existedSrow = srows.next();
        statisticsService.createSCell(existedSrow, SCELL_NAME, true);
        statisticsService.createSCell(existedSrow, SCELL_NAME, true);
    }

    @Test
    public void testGetFirstRelationsipsNodes() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetAllPeriods started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node dimension = createDimensionNode(statRoot, DimensionTypes.TIME);
        Node periodH = createLevelNode(dimension, Period.HOURLY.getId());
        Node periodD = createLevelNode(dimension, Period.DAILY.getId());
        Node periodW = createLevelNode(dimension, Period.WEEKLY.getId());
        List<Node> expectedNodes = new ArrayList<Node>();
        expectedNodes.add(periodH);
        expectedNodes.add(periodD);
        expectedNodes.add(periodW);
        datasetService.createRelationship(periodD, periodH, StatisticsRelationshipTypes.SOURCE);
        datasetService.createRelationship(periodW, periodD, StatisticsRelationshipTypes.SOURCE);
        Iterable<Node> periods = statisticsService.getFirstRelationsipsNodes(dimension, DatasetRelationTypes.CHILD);
        Assert.assertNotNull("Periods count cann't be null", periods);
        Iterator<Node> periodsIterator = periods.iterator();

        while (periodsIterator.hasNext()) {
            Assert.assertTrue("Node not exist is expected nodes list", expectedNodes.contains(periodsIterator.next()));
        }
    }

    @Test
    public void testGetHighestPeriod() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetHighestPeriod started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node dimension = createDimensionNode(statRoot, DimensionTypes.TIME);
        Node periodH = createLevelNode(dimension, Period.HOURLY.getId());
        Node periodD = createLevelNode(dimension, Period.DAILY.getId());
        Node periodW = createLevelNode(dimension, Period.WEEKLY.getId());
        List<Node> expectedNodes = new ArrayList<Node>();
        expectedNodes.add(periodH);
        expectedNodes.add(periodD);
        expectedNodes.add(periodW);
        datasetService.createRelationship(periodD, periodH, StatisticsRelationshipTypes.SOURCE);
        datasetService.createRelationship(periodW, periodD, StatisticsRelationshipTypes.SOURCE);
        Iterable<Node> periods = statisticsService.getFirstRelationsipsNodes(dimension, DatasetRelationTypes.CHILD);
        Assert.assertNotNull("Periods count cann't be null", periods);
        Node highestPeriod = statisticsService.getHighestPeriod(periods);
        Assert.assertEquals("Unexpected highest period ", periodW, highestPeriod);
    }

    @Test
    public void testFindDimension() {
        LOGGER.info("testFindDimension started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node knownDimension = createDimensionNode(statRoot, DimensionTypes.TIME);
        Node existedNode = statisticsService.findDimension(statRoot, DimensionTypes.TIME);
        Assert.assertEquals("Expected node not found", knownDimension, existedNode);
    }

    @Test
    public void testCreateDimension() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testFindDimension started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node expectedDimensionTime = statisticsService.createDimension(statRoot, DimensionTypes.TIME, false);
        Node expectedDimensionNetwork = statisticsService.createDimension(statRoot, DimensionTypes.NETWORK, false);
        Iterable<Node> dimensions = datasetService.getChildrenTraverser(statRoot);
        Assert.assertNotNull("Unexpected dimensionsList", dimensions);
        for (Node dimension : dimensions) {
            if (!dimension.equals(expectedDimensionTime) && !dimension.equals(expectedDimensionNetwork)) {
                Assert.fail("unexpected dimension");
            }
        }
    }

    @Test(expected = DatabaseException.class)
    public void testCreateLevelIfExists() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testFindDimension started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Node dimensionTime = statisticsService.createDimension(statRoot, DimensionTypes.TIME, false);
        createLevelNode(dimensionTime, Period.HOURLY.getId());
        statisticsService.createStatisticsLevelNode(dimensionTime, Period.HOURLY.getId(), true);
    }

    @Test
    public void testGetParentNode() throws DatabaseException {
        LOGGER.info("testGetParentNode started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        Assert.assertEquals(datasetNode, statisticsService.getParentNode(statRoot));
    }

    @Test
    public void testGetParentLevel() {
        LOGGER.info("testGetParentLevel started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statRoot = createStatisticsRoot(datasetNode);
        List<Node> chainList = createChildNextChain(statRoot, ARRAYS_SIZE, StatisticsNodeTypes.S_ROW);
        Node parentToFind = chainList.get(ARRAYS_SIZE - NumberUtils.INTEGER_ONE);
        chainList = createChildNextChain(parentToFind, ARRAYS_SIZE, StatisticsNodeTypes.S_CELL);
        Node searchable = statisticsService.getParentLevelNode(chainList.get(ARRAYS_SIZE - NumberUtils.INTEGER_ONE));
        Assert.assertEquals("Unexpected node found", parentToFind, searchable);
    }

    /**
     * create CHILD->NEXT typed nodes chain size of count
     * 
     * @param rootNode
     * @param count
     * @param type
     * @return
     */
    private List<Node> createChildNextChain(Node rootNode, int count, INodeType type) {
        List<Node> chainList = new ArrayList<Node>();
        Node newNode;
        try {
            for (int i = 0; i < count; i++) {
                newNode = datasetService.createNode(type);
                Long timestamp = (long)(Math.random() * 100000);
                datasetService.setAnyProperty(newNode, DriveModel.TIMESTAMP, timestamp);
                datasetService.setAnyProperty(newNode, DatasetService.NAME, DatasetService.NAME + timestamp);
                datasetService.addChild(rootNode, newNode, null);
                chainList.add(newNode);
            }
        } catch (Exception e) {
            LOGGER.error("unexpectable exception thrown when try to create a chain of nodes", e);
        }
        return chainList;
    }

    private Node createLevelNode(Node statisticNode, String name) {
        Node periodNode = null;
        try {
            periodNode = datasetService.createNode(statisticNode, DatasetRelationTypes.CHILD, StatisticsNodeTypes.LEVEL);
            datasetService.setAnyProperty(periodNode, DatasetService.NAME, name);
        } catch (Exception e) {
            LOGGER.error("cann't create period statistic root");
        }
        return periodNode;
    }

    /**
     * create dimension node
     * 
     * @param type
     * @return
     */
    private Node createDimensionNode(Node parent, DimensionTypes type) {
        Node dimension = null;
        try {
            dimension = datasetService.createNode(parent, DatasetRelationTypes.CHILD, StatisticsNodeTypes.DIMENSION);
            datasetService.setAnyProperty(dimension, DatasetService.NAME, type.getId());
        } catch (Exception e) {
            LOGGER.error("cann't create statistics root");
        }
        return dimension;
    }

    /**
     * @param datasetNode2
     * @return
     */
    private Node createStatisticsRoot(Node datasetNode) {
        Node statRot = null;
        try {
            statRot = datasetService.createNode(datasetNode, StatisticsRelationshipTypes.STATISTICS,
                    StatisticsNodeTypes.STATISTICS_MODEL);
            datasetService.setAnyProperty(statRot, DatasetService.NAME, STATISTIC_ROOT_NAME);
        } catch (Exception e) {
            LOGGER.error("cann't create statistics root");
        }
        return statRot;
    }

    /**
     * create Project node in database
     */
    private void initProjectNode() {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            projectNode = graphDatabaseService.createNode();
            graphDatabaseService.getReferenceNode().createRelationshipTo(projectNode, DatasetRelationTypes.PROJECT);
            projectNode.setProperty(DatasetService.NAME, "project");
            tx.success();
        } finally {
            tx.finish();
            tx = null;
        }
    }

    /**
     * create Dataset node in database
     * 
     * @param name
     * @param type
     * @param driveType
     * @return datasetNode
     */
    private Node initDatasetNode(long minTimestamp, long maxTimestamp) {
        try {
            datasetNode = datasetService.createDataset(projectNode, DATASET_NAME, DATASET_TYPE, DriveTypes.TEMS,
                    StatisticsNodeTypes.STATISTICS_MODEL);
            Map<String, Object> propertyMap = new HashMap<String, Object>();
            propertyMap.put(DriveModel.MIN_TIMESTAMP, minTimestamp);
            propertyMap.put(DriveModel.MAX_TIMESTAMP, maxTimestamp);
            datasetService.setProperties(datasetNode, propertyMap);
        } catch (Exception e) {
            LOGGER.error("unexpectable exception thrown", e);
        }
        return datasetNode;
    }
}
