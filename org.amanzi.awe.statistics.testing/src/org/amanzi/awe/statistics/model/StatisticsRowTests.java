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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * statistics rows tests
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsRowTests extends AbstractStatisticsModelTests {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroupTests.class);

    @Test
    public void testGetScellIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfNotFounded started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsRow row = new StatisticsRow(mockedSrow);
        Node scell = getMockedScell(SCELL_NAME);
        when(statisticsService.createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE))).thenReturn(scell);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(null);
        row.getSCell(SCELL_NAME);
        verify(statisticsService, atLeastOnce()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test
    public void testGetSCellIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfFounded started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsRow row = new StatisticsRow(mockedSrow);
        Node scell = getMockedScell(SCELL_NAME);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(scell);
        row.getSCell(SCELL_NAME);
        verify(statisticsService, never()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfScellNameIsIncorrect() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfScellNameIsIncorrect started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        StatisticsRow row = new StatisticsRow(mockedSrow);
        Node scell = getMockedScell(SCELL_NAME);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(scell);
        row.getSCell(StringUtils.EMPTY);
    }
}
