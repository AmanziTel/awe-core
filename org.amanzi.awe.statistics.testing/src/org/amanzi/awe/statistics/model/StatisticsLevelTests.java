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

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
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
    private static final String LEVEL_NAME = "levelName";
    private static final String AGGREGATED_STATISTICS_NAME = "aggregation";

    @Before
    public void setUp() {
        super.setUp();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfDimensionIsNull() throws DatabaseException {
        LOGGER.info("testConstructorIfDimensionIsNull start");
        new StatisticsLevel(null, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfDimensionHasWrongType() throws DatabaseException {
        LOGGER.info("testConstructorIfDimensionHasWrongType start");
        String fakeDimensionType = "fakeDimensionType";
        Node dimensionRoot = getMockedDimension(fakeDimensionType, fakeDimensionType);
        new StatisticsLevel(dimensionRoot, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfNameIsNull() throws DatabaseException {
        LOGGER.info("testConstructorIfNameIsNull start");
        Node dimensionRoot = getMockedDimension(DimensionTypes.TIME.getId(), Period.HOURLY.getId());
        new StatisticsLevel(dimensionRoot, null);
    }

    @Test
    public void testConstructorIfEverythingIsOk() throws DatabaseException {
        LOGGER.info("testConstructorIfEverythingIsOk start");
        Node dimensionRoot = getMockedDimension(StatisticsNodeTypes.DIMENSION.getId(), DimensionTypes.TIME.getId());
        new StatisticsLevel(dimensionRoot, LEVEL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourceIfSourceNull() throws DatabaseException {
        LOGGER.info("testAddSourceIfSourceNull start");
        Node dimensionRoot = getMockedDimension(StatisticsNodeTypes.DIMENSION.getId(), DimensionTypes.TIME.getId());
        StatisticsLevel level = new StatisticsLevel(dimensionRoot, LEVEL_NAME);
        level.addSourceLevel(null);
    }

    @Test
    public void testAddSource() throws DatabaseException {
        LOGGER.info("testAddSource start");
        Node dimensionRoot = getMockedDimension(StatisticsNodeTypes.DIMENSION.getId(), DimensionTypes.TIME.getId());
        StatisticsLevel level = new StatisticsLevel(dimensionRoot, LEVEL_NAME);
        StatisticsLevel levelSource = new StatisticsLevel(dimensionRoot, LEVEL_NAME + Math.PI);
        level.addSourceLevel(levelSource);
    }

    @Test
    public void testGetSourceLevel() throws DatabaseException {
        LOGGER.info("testGetSourceLevel start");
        Node mockedLevel = createLevelRoot(LEVEL_NAME);
        Node mockedSourceLevel = createLevelRoot(LEVEL_NAME + Math.PI);
        StatisticsLevel level = new StatisticsLevel(mockedLevel);
        StatisticsLevel sourceLevel = new StatisticsLevel(mockedSourceLevel);
        List<Node> sources = new ArrayList<Node>();
        sources.add(mockedSourceLevel);
        when(statisticsService.getSources(eq(mockedLevel))).thenReturn(sources);
        StatisticsLevel storedLevel = level.getSourceLevel();
        Assert.assertEquals("Unexpected level", sourceLevel.getRootNode(), storedLevel.getRootNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAggregateStatisticsModelIfCorrelatedIsNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetAggregateStatisticsModelIfCorrelatedIsNull start");
        Node mockedLevel = createLevelRoot(LEVEL_NAME);
        StatisticsLevel level = new StatisticsLevel(mockedLevel);
        level.getAggregateStatisticsModel(null);

    }

    public void testGetAggregateStatisticsModelIfExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetAggregateStatisticsModelIfCorrelatedIsNull start");
        Node mockedLevel = createLevelRoot(LEVEL_NAME);
        Node mockedCorrelatedLevel = createLevelRoot(LEVEL_NAME + Math.PI);
        Node mockedAggregation = getMockedAggregatedStatistics(AGGREGATED_STATISTICS_NAME);
        StatisticsLevel level = new StatisticsLevel(mockedLevel);
        StatisticsLevel correlatedLevel = new StatisticsLevel(mockedCorrelatedLevel);
        when(statisticsService.findAggregatedModel(eq(mockedLevel), eq(mockedCorrelatedLevel))).thenReturn(mockedAggregation);
        level.getAggregateStatisticsModel(correlatedLevel);
    }

    /**
     * return dimension root
     * 
     * @param fakeDimensionType
     * @return
     */
    private Node getMockedDimension(String type, String name) {
        Node dimension = getMockedNode();
        when(statisticsService.getNodeProperty(eq(dimension), eq(DatasetService.TYPE))).thenReturn(type);
        when(statisticsService.getNodeProperty(eq(dimension), eq(DatasetService.NAME))).thenReturn(name);
        return dimension;
    }

    public Node createLevelRoot(String name) {
        Node statRoot = getMockedNode();
        when(statisticsService.findStatisticsLevelNode(any(Node.class), eq(name))).thenReturn(statRoot);
        when(statisticsService.getNodeProperty(eq(statRoot), eq(DatasetService.TYPE))).thenReturn(
                StatisticsNodeTypes.STATISTICS.getId());
        when(statisticsService.getNodeProperty(eq(statRoot), eq(DatasetService.NAME))).thenReturn(name);
        return statRoot;
    }
}
