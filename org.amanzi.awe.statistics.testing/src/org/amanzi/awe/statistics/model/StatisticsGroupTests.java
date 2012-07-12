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

import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * test for statistics group
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsGroupTests extends AbstractStatisticsModelTests {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroupTests.class);

    @Test
    public void testGetSRowIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfNotFounded started ");
        Node sGroup = getMockedGroup(SGROUP_NAME);
        Node srow = getMockedSrow(Long.MIN_VALUE);
        StatisticsGroup group = new StatisticsGroup(sGroup);
        when(statisticsService.findNodeInChain(eq(sGroup), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(null);
        when(statisticsService.createSRow(eq(sGroup), eq(Long.MIN_VALUE), eq(Boolean.FALSE))).thenReturn(srow);
        group.getSRow(Long.MIN_VALUE);
        verify(statisticsService, atLeastOnce()).createSRow(eq(sGroup), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }

    @Test
    public void testGetSRowIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfFounded started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE);
        Node sGroup = getMockedGroup(SGROUP_NAME);
        StatisticsGroup group = new StatisticsGroup(sGroup);
        when(statisticsService.findNodeInChain(eq(sGroup), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(mockedSrow);
        group.getSRow(Long.MIN_VALUE);
        verify(statisticsService, never()).createSRow(eq(sGroup), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }
}
