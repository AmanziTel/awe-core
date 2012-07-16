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

package org.amanzi.awe.statistics.entities;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.AbstractMockedTests;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
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
public class StatisticsGroupTests extends AbstractMockedTests {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroupTests.class);


    @Test
    public void testGetSRowIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfNotFounded started ");
        Node sGroup = getMockedGroup(SGROUP_NAME);
        Node level = getMockedLevel(FIRST_LEVEL_NAME, Boolean.TRUE);
        StatisticsGroup group = new StatisticsGroup(level, sGroup);
        when(statisticsService.getChildrenChainTraverser(eq(sGroup))).thenReturn(null);
        StatisticsRow row = group.findChildByName(SROW_NAME);
        Assert.assertNull("Unexpected root node", row);
    }

    @Test
    public void testGetSRowIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfNotFounded started ");
        Node sGroup = getMockedGroup(SGROUP_NAME);
        Node srow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node level = getMockedLevel(FIRST_LEVEL_NAME, Boolean.TRUE);
        StatisticsGroup group = new StatisticsGroup(level, sGroup);
        List<Node> rows = new ArrayList<Node>();
        rows.add(srow);
        when(statisticsService.getChildrenChainTraverser(eq(sGroup))).thenReturn(rows);
        StatisticsRow row = group.findChildByName(SROW_NAME);
        Assert.assertEquals("Unexpected root node", srow, row.getRootNode());
    }
}
