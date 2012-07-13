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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.model.StatisticsLevel;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
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
public class StatisticsLevelTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(StatisticsLevelTests.class);

    @Before
    public void setUp() {
        super.setUp();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfDimensionIsNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfDimensionIsNull start");
        new StatisticsLevel(null, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfDimensionHasWrongType() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfDimensionHasWrongType start");
        String fakeDimensionType = "fakeDimensionType";
        Node dimensionRoot = getMockedNodeWithNameAndType(fakeDimensionType, fakeDimensionType);
        new StatisticsLevel(dimensionRoot, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfNameIsNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfNameIsNull start");
        Node dimensionRoot = getMockedDimension(DimensionTypes.TIME);
        new StatisticsLevel(dimensionRoot, StringUtils.EMPTY);
    }

    @Test
    public void testConstructorIfEverythingIsOk() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfEverythingIsOk start");
        Node dimensionRoot = getMockedDimension(DimensionTypes.TIME);
        new StatisticsLevel(dimensionRoot, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourceIfSourceNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourceIfSourceNull start");
        Node dimensionRoot = getMockedDimension(DimensionTypes.TIME);
        StatisticsLevel level = new StatisticsLevel(dimensionRoot, LEVEL_NAME);
        level.addSourceLevel(null);
    }

    @Test
    public void testAddSource() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSource start");
        Node dimensionRoot = getMockedDimension(DimensionTypes.TIME);
        StatisticsLevel level = new StatisticsLevel(dimensionRoot, LEVEL_NAME);
        StatisticsLevel levelSource = new StatisticsLevel(dimensionRoot, LEVEL_NAME + Math.PI);
        level.addSourceLevel(levelSource);
    }

    @Test
    public void testGetSourceLevel() throws DatabaseException {
        LOGGER.info("testGetSourceLevel start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node dimension = getMockedDimension(DimensionTypes.NETWORK);
        Node mockedSourceLevel = getMockedLevel(LEVEL_NAME + Math.PI, Boolean.TRUE);
        StatisticsLevel level = new StatisticsLevel(dimension, mockedLevel);
        StatisticsLevel sourceLevel = new StatisticsLevel(dimension, mockedSourceLevel);
        List<Node> sources = new ArrayList<Node>();
        sources.add(mockedSourceLevel);
        when(statisticsService.getSources(eq(mockedLevel))).thenReturn(sources);
        StatisticsLevel storedLevel = level.getSourceLevel();
        Assert.assertEquals("Unexpected level", sourceLevel.getRootNode(), storedLevel.getRootNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAggregatedStatisticsModelIfLevelIsNull() throws DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {
        LOGGER.info("testFindAggregatedStatisticsModelIfLevelIsNull start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node dimension = getMockedDimension(DimensionTypes.NETWORK);
        StatisticsLevel level = new StatisticsLevel(dimension, mockedLevel);
        level.findAggregatedStatistics(null);
    }

    @Test(expected = DuplicateNodeNameException.class)
    public void testCreateAggregateStatisticsIfExist() throws DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {
        LOGGER.info("testCreateAggregateStatisticsIfExist start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node mockedCorrelatedLevel = getMockedLevel(LEVEL_NAME + Math.PI, Boolean.TRUE);
        Node mockedAggregation = getMockedAggregatedStatistics(AGGREGATED_STATISTICS_NAME);
        Node networkD = getMockedDimension(DimensionTypes.NETWORK);
        Node timeD = getMockedDimension(DimensionTypes.TIME);
        StatisticsLevel level = new StatisticsLevel(networkD, mockedLevel);
        StatisticsLevel correlatedLevel = new StatisticsLevel(timeD, mockedCorrelatedLevel);
        when(statisticsService.findAggregatedStatistics(eq(mockedLevel), eq(mockedCorrelatedLevel))).thenReturn(mockedAggregation);
        level.createAggregatedStatistics(correlatedLevel);
    }

    @Test
    public void testCreateAggregateStatisticsIfNotExist() throws DatabaseException, IllegalNodeDataException,
            DuplicateNodeNameException {
        LOGGER.info("testCreateAggregateStatisticsIfNotExist start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node mockedCorrelatedLevel = getMockedLevel(LEVEL_NAME + Math.PI, Boolean.TRUE);
        Node networkD = getMockedDimension(DimensionTypes.NETWORK);
        Node timeD = getMockedDimension(DimensionTypes.TIME);
        StatisticsLevel level = new StatisticsLevel(networkD, mockedLevel);
        StatisticsLevel correlatedLevel = new StatisticsLevel(timeD, mockedCorrelatedLevel);
        when(statisticsService.findAggregatedStatistics(eq(mockedLevel), eq(mockedCorrelatedLevel))).thenReturn(null);
        level.createAggregatedStatistics(correlatedLevel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAggregateStatisticsIfCorrelatedIsNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetAggregateStatisticsModelIfCorrelatedIsNull start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node networkD = getMockedDimension(DimensionTypes.NETWORK);
        StatisticsLevel level = new StatisticsLevel(networkD, mockedLevel);
        level.getAggregateStatistics(null);

    }

    @Test
    public void testGetAggregateStatisticsModelIfExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetAggregateStatisticsModelIfCorrelatedIsNull start");
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        Node mockedCorrelatedLevel = getMockedLevel(LEVEL_NAME + Math.PI, Boolean.TRUE);
        Node mockedAggregation = getMockedAggregatedStatistics(AGGREGATED_STATISTICS_NAME);
        Node networkD = getMockedDimension(DimensionTypes.NETWORK);
        Node timeD = getMockedDimension(DimensionTypes.TIME);
        StatisticsLevel level = new StatisticsLevel(networkD, mockedLevel);
        StatisticsLevel correlatedLevel = new StatisticsLevel(timeD, mockedCorrelatedLevel);
        when(statisticsService.findAggregatedStatistics(eq(mockedLevel), eq(mockedCorrelatedLevel))).thenReturn(mockedAggregation);
        level.getAggregateStatistics(correlatedLevel);
    }
}
