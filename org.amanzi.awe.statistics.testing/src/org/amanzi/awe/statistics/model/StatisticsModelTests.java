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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.AbstractStatisticsTest;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModelTests extends AbstractStatisticsTest {

    private static StatisticsService statisticsService;
    private static final String PARENT_NAME = "model";
    private static final String MODEL_NAME = "model";
    private static Node parentNode;
    private static Node statisticModelNode;

    @Before
    public void setUp() {
        statisticsService = getMockedService();
        initMockedParentNode();
        initMockedStatisticsRootModel();
        StatisticsModel.setStatisticsService(statisticsService);
        PeriodStatisticsModel.setStatisticsService(statisticsService);
    }

    /**
     * @param hourly
     * @return
     */
    private Node getMockedPeriodNode(Period hourly) {
        String id = hourly.getId();
        Node period = getMockedNode();
        when(period.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(id);
        return period;
    }

    /**
     *
     */
    private void initMockedStatisticsRootModel() {
        statisticModelNode = getMockedNode();
        when(statisticModelNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(MODEL_NAME);
        when(statisticModelNode.getProperty(eq(DatasetService.NAME))).thenReturn(MODEL_NAME);
    }

    /**
     * @return
     */
    private void initMockedParentNode() {
        parentNode = getMockedNode();
        when(parentNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(PARENT_NAME);
    }

    /**
     * @return
     */
    private StatisticsService getMockedService() {
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

    private Node getMockedNode() {
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
    public void testPeriodModelInitializationHourly() throws DatabaseException, IllegalArgumentException,
            DuplicateNodeNameException {
        PeriodRange range = generatePeriod(Period.HOURLY);
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(null);
        when(statisticsService.createStatisticsModelRoot(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        mockTimestampParent(range.getMin(), range.getMax());
        new StatisticsModel(parentNode);
    }

    @Test
    public void testPeriodModelInitializationMonthly() throws DatabaseException, IllegalArgumentException,
            DuplicateNodeNameException {
        PeriodRange range = generatePeriod(Period.MONTHLY);
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(null);
        when(statisticsService.createStatisticsModelRoot(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        mockedNode = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.DAILY))).thenReturn(mockedNode);
        mockedNode = getMockedPeriodNode(Period.WEEKLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.WEEKLY))).thenReturn(mockedNode);
        mockedNode = getMockedPeriodNode(Period.MONTHLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.MONTHLY))).thenReturn(mockedNode);

        mockTimestampParent(range.getMin(), range.getMax());
        new StatisticsModel(parentNode);

        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.HOURLY));
        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.DAILY));
        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.WEEKLY));
        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.MONTHLY));
    }

    @Test
    public void testPeriodModelInitializationWeekly() throws DatabaseException, IllegalArgumentException,
            DuplicateNodeNameException {
        PeriodRange range = generatePeriod(Period.WEEKLY);
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(null);
        when(statisticsService.createStatisticsModelRoot(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        mockedNode = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.DAILY))).thenReturn(mockedNode);
        mockedNode = getMockedPeriodNode(Period.WEEKLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.WEEKLY))).thenReturn(mockedNode);

        mockTimestampParent(range.getMin(), range.getMax());
        new StatisticsModel(parentNode);

        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.HOURLY));
        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.DAILY));
        verify(statisticsService, atLeastOnce()).getPeriod(eq(statisticModelNode), eq(Period.WEEKLY));
    }

}
