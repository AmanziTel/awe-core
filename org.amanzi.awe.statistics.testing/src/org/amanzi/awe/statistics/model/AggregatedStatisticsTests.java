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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.neo.services.DatasetService;
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
public class AggregatedStatisticsTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(AggregatedStatisticsTests.class);

    private StatisticsLevel firstLevel;
    private StatisticsLevel secondLevel;

    @Before
    public void setUp() {
        super.setUp();
        try {
            firstLevel = mockLevel(DimensionTypes.NETWORK, FIRST_LEVEL_NAME);
            secondLevel = mockLevel(DimensionTypes.TIME, SECOND_LEVEL_NAME);
        } catch (DatabaseException e) {
            LOGGER.error("can't mock levels", e);
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
        LOGGER.info("testGetGroup started ");
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics stat = new AggregatedStatistics(levelsRoot);
        Node sgroupNode = getMockedGroup(SGROUP_NAME);
        when(statisticsService.findNodeInChain(eq(levelsRoot), eq(DatasetService.NAME), eq(SGROUP_NAME))).thenReturn(sgroupNode);
        stat.getSGroup(SGROUP_NAME);
        verify(statisticsService, never()).createSGroup(eq(levelsRoot), eq(SGROUP_NAME), eq(Boolean.FALSE));
    }

    @Test
    public void testGetGroupIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetGroup started ");
        Node levelsRoot = getAggregatedRoot();
        Node mockedGroup = getMockedGroup(SGROUP_NAME);
        AggregatedStatistics stat = new AggregatedStatistics(levelsRoot);
        when(statisticsService.findNodeInChain(eq(levelsRoot), eq(DatasetService.NAME), eq(SGROUP_NAME))).thenReturn(null);
        when(statisticsService.createSGroup(eq(levelsRoot), eq(SGROUP_NAME), eq(Boolean.FALSE))).thenReturn(mockedGroup);
        stat.getSGroup(SGROUP_NAME);
        verify(statisticsService, atLeastOnce()).createSGroup(eq(levelsRoot), eq(SGROUP_NAME), eq(Boolean.FALSE));
    }

    /**
     * @param type
     * @param name
     * @return
     * @throws DatabaseException
     */
    private StatisticsLevel mockLevel(DimensionTypes type, String name) throws DatabaseException {
        Node mockedLevel = getMockedLevelWithDimension(name, true, type);
        return new StatisticsLevel(mockedLevel);
    }

}
