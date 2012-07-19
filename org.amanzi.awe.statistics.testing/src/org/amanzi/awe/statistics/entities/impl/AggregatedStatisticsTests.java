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

package org.amanzi.awe.statistics.entities.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.AbstractMockedTests;
import org.amanzi.awe.statistics.entities.impl.AggregatedStatistics;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsLevel;
import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.junit.Before;
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
public class AggregatedStatisticsTests extends AbstractMockedTests {
    private static final Logger LOGGER = Logger.getLogger(AggregatedStatisticsTests.class);

    private StatisticsLevel firstLevel;
    private StatisticsLevel secondLevel;

    @Before
    public void setUp() {
        super.setUp();
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node mockedCorrelatedLevel = getMockedLevel(LEVEL_NAME + Math.PI, Boolean.TRUE);
        Node networkD = getMockedDimension(DimensionTypes.NETWORK);
        Node timeD = getMockedDimension(DimensionTypes.TIME);
        try {
            firstLevel = new StatisticsLevel(networkD, mockedLevel);
            secondLevel = new StatisticsLevel(timeD, mockedCorrelatedLevel);
        } catch (DatabaseException e) {
            // TODO Handle DatabaseException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * return generated aggregated root + services mocked
     * 
     * @return
     */
    private Node getAggregatedRoot() {
        String name = String.format(NAME_FORMAT, FIRST_LEVEL_NAME, SECOND_LEVEL_NAME);
        Node aggr = getMockedAggregatedStatistics(name);
        Node firstRoot = firstLevel.getRootNode();
        Node secondRoot = secondLevel.getRootNode();
        try {
            when(statisticsService.createAggregatedStatistics(eq(firstRoot), eq(secondRoot), eq(name))).thenReturn(aggr);
        } catch (Exception e) {
            LOGGER.error("unexpectable exception");
        }
        return aggr;
    }

    @Test
    public void testCounstructorIfEverythingIsOk() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCounstructorIfEverythingIsOk started ");
        new AggregatedStatistics(firstLevel, secondLevel);
        Node firstRoot = firstLevel.getRootNode();
        Node secondRoot = secondLevel.getRootNode();
        String name = String.format(NAME_FORMAT, firstLevel.getName(), secondLevel.getName());
        verify(statisticsService, atLeastOnce()).createAggregatedStatistics(eq(firstRoot), eq(secondRoot), eq(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCounstructorIfOneOfParametersIsNul() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testCounstructorIfOneParameterIsNul started ");
        new AggregatedStatistics(null, secondLevel);
    }

    @Test
    public void testGetGroupIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetGroupIfFounded started ");
        Node aggregation = getAggregatedRoot();
        AggregatedStatistics stat = new AggregatedStatistics(aggregation);
        Node sgroupNode = getMockedGroup(SGROUP_NAME);
        List<Node> groups = new ArrayList<Node>();
        groups.add(sgroupNode);
        when(statisticsService.getChildrenChainTraverser(eq(aggregation))).thenReturn(groups);
        StatisticsGroup group = stat.findChildByName(SGROUP_NAME);
        Assert.assertEquals("Unexpected root node", sgroupNode, group.getRootNode());
    }

    @Test
    public void testGetGroupIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetGroupIfNotFounded started ");
        Node aggregation = getAggregatedRoot();
        AggregatedStatistics stat = new AggregatedStatistics(aggregation);
        when(statisticsService.getChildrenChainTraverser(eq(aggregation))).thenReturn(null);
        StatisticsGroup group = stat.findChildByName(SGROUP_NAME);
        Assert.assertNull("Unexpected root node", group);
    }
}
