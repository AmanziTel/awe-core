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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModelTests extends AbstractStatisticsModelTests {

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
