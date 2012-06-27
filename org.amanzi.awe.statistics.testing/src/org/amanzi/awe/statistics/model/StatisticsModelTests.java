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

package org.amanzi.awe.statistics.model;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.testing.AbstractTest;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModelTests extends AbstractTest {

    private static StatisticsService statisticsService;
    private static final String PARENT_NAME = "model";
    private static final String MODEL_NAME = "model";
    private static Node parentNode;
    private static Node statisticModelNode;

    @BeforeClass
    public static void setUp() {
        statisticsService = getMockedService();
        initMockedParentNode();
        initMockedStatisticsRootModel();
        StatisticsModel.setStatisticsService(statisticsService);
    }

    /**
     *
     */
    private static void initMockedStatisticsRootModel() {
        statisticModelNode = getMockedNode();
        when(statisticModelNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(MODEL_NAME);
        when(statisticModelNode.getProperty(eq(DatasetService.NAME))).thenReturn(MODEL_NAME);
    }

    /**
     * @return
     */
    private static void initMockedParentNode() {
        parentNode = getMockedNode();
        when(parentNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(PARENT_NAME);
    }

    /**
     * @return
     */
    private static StatisticsService getMockedService() {
        statisticsService = mock(StatisticsService.class);
        return statisticsService;
    }

    /**
     * mock timestamp property in parent node
     * 
     * @param min
     * @param max
     */
    private void mockTimestampParent(Long min, Long max) {
        when(parentNode.getProperty(eq(DriveModel.MIN_TIMESTAMP))).thenReturn(min);
        when(parentNode.getProperty(eq(DriveModel.MAX_TIMESTAMP))).thenReturn(max);
    }

    private static Node getMockedNode() {
        Node node = mock(Node.class);
        return node;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfParentIsNull() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {
        new StatisticsModel(null);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void testConstructorIfStatisticsExist() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(getMockedNode());
        new StatisticsModel(parentNode);
    }

    @Test
    public void testConstructorIfStatisticsNotExist() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {

        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(null);
        when(statisticsService.createStatisticsModelRoot(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        StatisticsModel model = new StatisticsModel(parentNode);
        assertEquals("Unexpected model root", statisticModelNode, model.getRootNode());

    }

    @Test
    public void testPeriodModelInitialization() {
        PeriodRange range = generatePeriod(Period.HOURLY);
        mockTimestampParent(range.getMin(), range.getMax());
    }

    /**
     * generate range period
     * 
     * @param monthly
     */
    private PeriodRange generatePeriod(Period period) {
        PeriodRange range = null;
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        switch (period) {
        case HOURLY:
            min.set(Calendar.MINUTE, NumberUtils.INTEGER_ZERO);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.MINUTE, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case DAILY:
            min.set(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.HOUR_OF_DAY, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case WEEKLY:
            min.set(Calendar.DATE, NumberUtils.INTEGER_ONE);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.DATE, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case MONTHLY:
            min.set(Calendar.DATE, NumberUtils.INTEGER_ONE);
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.DATE, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case YEARLY:
            min.set(Calendar.MONTH, Calendar.JANUARY);
            max.setTimeInMillis(min.getTimeInMillis());
            max.set(Calendar.MONTH, Calendar.FEBRUARY);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        case ALL:
            max.setTimeInMillis(min.getTimeInMillis());
            max.add(Calendar.YEAR, NumberUtils.INTEGER_ONE);
            range = new PeriodRange(min.getTimeInMillis(), max.getTimeInMillis());
            break;
        default:
            break;
        }
        return range;
    }

    /**
     * just storage for test of min max timestamp TODO Purpose of StatisticsModelTests
     * <p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static class PeriodRange {
        private long min;
        private long max;

        /**
         * @return Returns the min.
         */
        public long getMin() {
            return min;
        }

        /**
         * @return Returns the max.
         */
        public long getMax() {
            return max;
        }

        PeriodRange(long min, long max) {
            this.min = min;
            this.max = max;
        }

    }
}
