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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * tests for period statistics model
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PeriodStatisticsModelTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(PeriodStatisticsModel.class);

    @Test
    public void testCounstructorIfEverythingIsOk() throws DatabaseException {
        LOGGER.info("testCounstructorIfEverythingIsOk started ");
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        PeriodStatisticsModel model = new PeriodStatisticsModel(statisticModelNode, Period.HOURLY);
        assertEquals("Unexpected result", mockedNode, model.getRootNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCounstructorIfOneParameterIsNul() throws DatabaseException {
        LOGGER.info("testCounstructorIfOneParameterIsNul started ");
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        PeriodStatisticsModel model = new PeriodStatisticsModel(null, Period.HOURLY);
        assertEquals("Unexpected result", mockedNode, model.getRootNode());
    }
}
