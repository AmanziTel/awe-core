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

package org.amanzi.awe.statistics.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.statistics.entities.impl.AggregatedStatistics;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.awe.statistics.exceptions.StatisticsException;
import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.testing.AbstractAWEDBTest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsManagerTests extends AbstractAWEDBTest {
    private IDriveModel driveModel;
    private HashSet<String> generatedModels;
    private Map<String, List<Long>> cellsPeriods;
    private static final Logger LOGGER = Logger.getLogger(StatisticsManagerTests.class);
    private static final StatisticsManager MANAGER = StatisticsManager.getInstance();
    private static IProjectModel PROJECT_MODEL;
    private static final String DRIVE_MODEL_NAME = "drive";
    private static final String FILE_NAME = "fileName";
    private static final String TEMPLATE_NAME = "netview:imei.t";
    private static final String SIGNAL_STRENGTH_PROPERTY = "signal_strength";
    private static final String CALL_STATUS_PROPERTY = "call_status";
    private static final String TRAFIC_RX_BYTES = "trafficcount_rxbytes";
    private static final String ANSWERING = "answering";
    private static final String PROJECT_NAME = "project";
    private static final String MODEL = "model";
    private static final String[] MODELS = {"Model 2", "Model 1", "Desire HD", "NX-A899", "GT-P1000", "GT-S5360", "GT-I8150",
            "GT-55830", "GT-I9100", "GT-I9210", "GT-N7000", "Galaxy Nexus", "HTC Desire HD A9191"};
    private static final String[] CELLS = {"Desire HD", "NX-A899", "GT-P1000", "GT-S5360", "GT-I8150", "GT-55830", "GT-I9100",
            "GT-I9210", "GT-N7000", "Galaxy Nexus", "HTC Desire HD A9191"};

    private static Calendar startTime = Calendar.getInstance();
    private static DatasetService service;
    private List<Long> expectedRows;

    @BeforeClass
    public static void prepareDatabase() throws AWEException {
        clearDb();
        initializeDb();
        service = NeoServiceFactory.getInstance().getDatasetService();
        PROJECT_MODEL = ProjectModel.setActiveProject(PROJECT_NAME);
        new LogStarter().earlyStartup();
    }

    @Before
    public void prepareDrive() throws AWEException {
        resetStartTime();
        clearBase();
        driveModel = PROJECT_MODEL.getDriveModel(DRIVE_MODEL_NAME, DriveTypes.TEMS);
        driveModel.addFile(new File(FILE_NAME));
        generatedModels = new HashSet<String>();
        cellsPeriods = new HashMap<String, List<Long>>();
        expectedRows = new ArrayList<Long>();
    }

    /**
     *
     */
    private void resetStartTime() {
        startTime.set(Calendar.MONTH, NumberUtils.INTEGER_ZERO);
        startTime.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getFirstDayOfWeek());
        startTime.set(Calendar.WEEK_OF_YEAR, NumberUtils.INTEGER_ONE);
        startTime.set(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ZERO);

    }

    /**
     * clear databse
     */
    private void clearBase() {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node projectNode = PROJECT_MODEL.getRootNode();
            for (Relationship rel : projectNode.getRelationships(Direction.OUTGOING)) {
                rel.delete();
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }

    }

    @Test
    public void testStatisticsCreation() throws StatisticsException {
        LOGGER.info("testStatisticsCreation started");
        final int MEASUREMENT_SIZE = 1;
        buildDriveModel(MEASUREMENT_SIZE, Period.HOURLY);
        AggregatedStatistics statistics = MANAGER.processStatistics(TEMPLATE_NAME, driveModel, MODEL, Period.HOURLY,
                new NullProgressMonitor());
        Assert.assertNotNull(statistics);
        Iterator<Node> statisticsModels = service.getFirstRelationTraverser(driveModel.getRootNode(),
                StatisticsRelationshipTypes.STATISTICS, Direction.OUTGOING).iterator();
        Assert.assertTrue(statisticsModels.hasNext());
        Node statisticsNode = statisticsModels.next();
        Node dimensionNode;
        Iterator<Node> dimensions = service.getChildrenTraverser(statisticsNode).iterator();
        Assert.assertTrue(dimensions.hasNext());
        dimensionNode = dimensions.next();
        Node level;
        Iterator<Node> levels = service.getChildrenTraverser(dimensionNode).iterator();
        Assert.assertTrue(levels.hasNext());
        level = levels.next();
        Iterator<Node> aggregations = service.getChildrenTraverser(level).iterator();
        Assert.assertTrue(aggregations.hasNext());
        Assert.assertEquals(aggregations.next(), statistics.getRootNode());

    }

    @Test
    public void testStatisticsHourlyStructure() throws StatisticsException {
        LOGGER.info("testStatisticsStructure started");
        final int MEASUREMENT_SIZE = 10;
        buildDriveModel(MEASUREMENT_SIZE, Period.HOURLY);
        AggregatedStatistics statistics = MANAGER.processStatistics(TEMPLATE_NAME, driveModel, MODEL, Period.HOURLY,
                new NullProgressMonitor());
        Collection<StatisticsGroup> groups = statistics.getAllChild();
        LOGGER.info("check groups ");
        Assert.assertEquals(groups.size(), generatedModels.size());
        for (StatisticsGroup group : groups) {
            LOGGER.info("check group " + group.getName());
            Assert.assertTrue(group.getName() + "is out of expected list " + generatedModels,
                    generatedModels.contains(group.getName()));
            LOGGER.info("check Rows in group " + group.getName());
            String message = checkRows(group.getName(), expectedRows, group.getAllChild());
            if (!message.isEmpty()) {
                LOGGER.error(message);
                Assert.fail(message);
            }

        }
    }

    @Test(expected = StatisticsException.class)
    public void testStatisticsCreationIfTamplateNameEmptyOrNull() throws StatisticsException {
        LOGGER.info("testStatisticsStructure started");
        final int MEASUREMENT_SIZE = 1;
        buildDriveModel(MEASUREMENT_SIZE, Period.HOURLY);
        MANAGER.processStatistics(StringUtils.EMPTY, driveModel, MODEL, Period.HOURLY, new NullProgressMonitor());

    }

    @Test(expected = StatisticsException.class)
    public void testStatisticsCreationIfDriveModelIsNull() throws StatisticsException {
        LOGGER.info("testStatisticsStructure started");
        MANAGER.processStatistics(TEMPLATE_NAME, null, MODEL, Period.HOURLY, new NullProgressMonitor());

    }

    @Test(expected = StatisticsException.class)
    public void testStatisticsCreationIfPeriodIsNulll() throws StatisticsException {
        LOGGER.info("testStatisticsStructure started");
        final int MEASUREMENT_SIZE = 1;
        buildDriveModel(MEASUREMENT_SIZE, Period.HOURLY);
        MANAGER.processStatistics(TEMPLATE_NAME, driveModel, MODEL, null, new NullProgressMonitor());

    }

    /**
     * @param groupName
     * @param timestamps
     * @param allChild
     */
    private String checkRows(String groupName, List<Long> timestamps, Collection<StatisticsRow> allChild) {
        for (StatisticsRow row : allChild) {
            LOGGER.info("check row " + row.getName());
            if (row.isSummaryRow()) {
                LOGGER.info("summury row skiped");
                continue;
            }
            Long timestamp = row.getTimestamp();
            if (!timestamps.contains(timestamp)) {
                return "row with timestamp " + timestamp + " not exists in expected list " + timestamps;
            }
            String message = checkCells(groupName, row);
            if (!message.isEmpty()) {
                return message;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @param groupName
     * @param row
     */
    private String checkCells(String groupName, StatisticsRow row) {
        for (StatisticsCell cell : row.getAllChild()) {
            boolean inCells = checkIfCellNameInTemplateColumns(groupName);
            Number value = cell.getValue();
            if (!inCells && value != null) {
                return "cell " + cell.getName() + " in group " + groupName + " contain unexpected value";
            } else if (value != null) {
                String message = checkSources(row, cell);
                if (!message.isEmpty()) {
                    return message;
                }
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * @param row
     * @param cell
     */
    private String checkSources(StatisticsRow row, StatisticsCell cell) {
        Iterable<IDataElement> sources = cell.getSources();
        List<Long> sourcePeriods = cellsPeriods.get(cell.getName());
        for (IDataElement source : sources) {
            Long periodTimestamp = (Long)source.get(DriveModel.TIMESTAMP);
            if (!sourcePeriods.contains(periodTimestamp)) {
                return "timestamp " + periodTimestamp + " doesn't exist in known timestamps for " + cell.getName() + " :"
                        + sourcePeriods;
            }
            if (periodTimestamp < row.getTimestamp()) {
                return "timestamp " + periodTimestamp + "has incorrect row";
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @return
     */
    private boolean checkIfCellNameInTemplateColumns(String cellName) {
        for (String cell : CELLS) {
            if (cell.equals(cellName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param measurementSize
     * @throws AWEException
     */
    private void buildDriveModel(int measurementSize, Period period) {
        try {
            for (int i = 0; i < measurementSize; i++) {
                Map<String, Object> measurement = generateMeasurement(period, false);

                driveModel.addMeasurement(FILE_NAME, measurement);
            }
            driveModel.finishUp();
        } catch (AWEException e) {
            LOGGER.info("Exception while building drive model");
            return;
        }
        makeExpectedRowsList(period);
        LOGGER.info("Drive model had been builded with size " + measurementSize + " and models " + generatedModels);
    }

    /**
     *
     */
    private void makeExpectedRowsList(Period period) {
        long currentStartTime = period.getStartTime(driveModel.getMinTimestamp());
        long nextStartTime = getNextStartDate(period, driveModel.getMaxTimestamp(), currentStartTime);
        do {
            expectedRows.add(currentStartTime);
            currentStartTime = nextStartTime;
            nextStartTime = getNextStartDate(period, driveModel.getMaxTimestamp(), currentStartTime);
        } while (currentStartTime < driveModel.getMaxTimestamp());

    }

    /**
     * return next required time cut in according to period
     * 
     * @param period
     * @param endDate
     * @param currentStartDate
     * @return
     */
    private long getNextStartDate(Period period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if (!period.equals(Period.HOURLY) && (nextStartDate > endDate)) {
            nextStartDate = endDate;
        }
        return nextStartDate;
    }

    /**
     * @param b
     * @param period
     * @return
     */
    private Map<String, Object> generateMeasurement(Period period, boolean b) {
        Map<String, Object> measurement = new HashMap<String, Object>();
        measurement.put(DriveModel.LONGITUDE, generateDouble());
        measurement.put(DriveModel.LATITUDE, generateDouble());
        Long timestamp = generateNextTime(period);
        measurement.put(DriveModel.TIMESTAMP, timestamp);
        String modelName = getRandomModel();
        measurement.put(MODEL, modelName);
        measurement.put(CALL_STATUS_PROPERTY, ANSWERING);
        measurement.put(SIGNAL_STRENGTH_PROPERTY, generateDouble());
        measurement.put(TRAFIC_RX_BYTES, generateLong());
        generatedModels.add(modelName);
        List<Long> modelsPeriod = cellsPeriods.get(modelName);
        if (modelsPeriod == null) {
            modelsPeriod = new ArrayList<Long>();
            cellsPeriods.put(modelName, modelsPeriod);
        }
        modelsPeriod.add(timestamp);
        return measurement;
    }

    /**
     * @param period
     * @return
     */
    private Long generateNextTime(Period period) {
        switch (period) {
        case HOURLY:
            startTime.add(Calendar.MINUTE, 5);
            break;
        case DAILY:
            startTime.add(Calendar.HOUR, NumberUtils.INTEGER_ONE);
            break;
        case WEEKLY:
            startTime.add(Calendar.DAY_OF_WEEK, NumberUtils.INTEGER_ONE);
            break;
        case MONTHLY:
            startTime.add(Calendar.WEEK_OF_MONTH, NumberUtils.INTEGER_ONE);
            break;
        default:
            break;
        }
        // TODO Auto-generated method stub
        return startTime.getTimeInMillis();
    }

    /**
     * @return
     */
    private String getRandomModel() {
        int size = MODELS.length;
        int index = (int)(Math.random() * size);
        return MODELS[index];
    }

    /**
     * @return
     */
    private static Long generateLong() {
        return (long)(Math.random() * 10000);
    }

    /**
     * @return
     */
    private static Double generateDouble() {
        return Math.random() * 100;
    }
}
