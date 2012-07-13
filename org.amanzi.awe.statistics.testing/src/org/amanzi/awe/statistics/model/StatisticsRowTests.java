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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

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
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node group = getMockedGroup(SGROUP_NAME);
        StatisticsRow row = new StatisticsRow(group, mockedSrow);
        when(statisticsService.getChildrenChainTraverser(eq(mockedSrow))).thenReturn(null);
        StatisticsCell cell = row.getSCell(SCELL_NAME);
        Assert.assertNull("Unexpected root node", cell);
    }

    @Test
    public void testGetSCellIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfNotFounded started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node group = getMockedGroup(SGROUP_NAME);
        StatisticsRow row = new StatisticsRow(group, mockedSrow);
        Node scell = getMockedScell(SCELL_NAME);
        List<Node> rows = new ArrayList<Node>();
        rows.add(scell);
        when(statisticsService.getChildrenChainTraverser(eq(mockedSrow))).thenReturn(rows);
        StatisticsCell cell = row.getSCell(SCELL_NAME);
        Assert.assertEquals("Unexpected root node", scell, cell.getRootNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfScellNameIsIncorrect() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfScellNameIsIncorrect started ");
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node group = getMockedGroup(SGROUP_NAME);
        StatisticsRow row = new StatisticsRow(group, mockedSrow);
        Node scell = getMockedScell(SCELL_NAME);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(scell);
        row.getSCell(StringUtils.EMPTY);
    }
}
