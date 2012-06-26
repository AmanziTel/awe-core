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

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsServiceTests extends AbstractNeoServiceTest {

    private static Logger LOGGER = Logger.getLogger(StatisticsServiceTests.class);
    private static final String DATASET_NAME = "dataset";
    private static final String STATISTIC_ROOT_NAME = DATASET_NAME + " Statistics";
    private static final INodeType TYPE = DatasetTypes.DRIVE;
    private static Node datasetNode = null;
    private Node projectNode;
    private StatisticsService service;

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
        service = StatisticsService.getInstance();
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
        service.createStatisticsModelRoot(null, STATISTIC_ROOT_NAME);
    }

    @Test(expected = DatabaseException.class)
    public void testCreateStatisticModelRootIfNameIsNull() throws DatabaseException {
        LOGGER.info("testCreateStatisticModelRootIfNameIsNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        service.createStatisticsModelRoot(datasetNode, null);
    }

    @Test
    public void testCreateStatisticModelRoot() throws DatabaseException {
        LOGGER.info("testCreateStatisticModelRoot started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        service.createStatisticsModelRoot(datasetNode, STATISTIC_ROOT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindStatisticModelIfParentNull() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfParentNull started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        service.findStatistic(null, STATISTIC_ROOT_NAME);
    }

    @Test
    public void testFindStatisticModelIfExist() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node statisticsModelRoot = createStatisticsRoot(datasetNode);
        Node result = service.findStatistic(datasetNode, STATISTIC_ROOT_NAME);
        Assert.assertEquals("Different result detected. The same expected", statisticsModelRoot, result);
    }

    @Test
    public void testFindStatisticModelIfNotExist() throws DatabaseException {
        LOGGER.info("testFindStatisticModelIfExist started");
        initDatasetNode(Long.MIN_VALUE, Long.MAX_VALUE);
        Node result = service.findStatistic(datasetNode, STATISTIC_ROOT_NAME);
        Assert.assertNull("Null expected. Node must not be found", result);
    }

    /**
     * @param datasetNode2
     * @return
     */
    private Node createStatisticsRoot(Node datasetNode) {
        Transaction tx = graphDatabaseService.beginTx();
        Node statRot = null;
        try {
            statRot = graphDatabaseService.createNode();
            setPropertyToNode(statRot, DatasetService.NAME, STATISTIC_ROOT_NAME);
            setPropertyToNode(statRot, DatasetService.TYPE, StatisticsNodeTypes.STATISTICS.getId());
            datasetNode.createRelationshipTo(statRot, StatisticsRelationshipTypes.STATISTICS);
            tx.success();
        } finally {
            tx.finish();
            tx = null;
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
        datasetNode = null;
        Transaction tx = graphDatabaseService.beginTx();
        try {
            datasetNode = graphDatabaseService.createNode();
            projectNode.createRelationshipTo(datasetNode, DatasetRelationTypes.DATASET);
            setPropertyToNode(datasetNode, DatasetService.NAME, DATASET_NAME);
            setPropertyToNode(datasetNode, DatasetService.TYPE, TYPE.getId());
            setPropertyToNode(datasetNode, DriveModel.MIN_TIMESTAMP, minTimestamp);
            setPropertyToNode(datasetNode, DriveModel.MAX_TIMESTAMP, maxTimestamp);
            tx.success();

        } finally {
            tx.finish();
            tx = null;
        }
        return datasetNode;
    }

    /**
     * this methot inits properties for dataset node
     * 
     * @param datasetNode
     * @param name
     * @param type
     * @param driveType
     */
    private void setPropertyToNode(Node datasetNode, String name, Object value) {
        datasetNode.setProperty(name, value);
    }
}
