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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.model.Dimension;
import org.amanzi.awe.statistics.model.StatisticsLevel;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * tests for Dimension
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DimensionTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(StatisticsLevelTests.class);
    private static final String LEVEL_NAME = "level";

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfStatisticsModelNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfStatisticsModelNull start");
        new Dimension(null, DimensionTypes.NETWORK);
    }

    @Test
    public void testConstructorIfNotExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfEverythingIsOk start");
        Node existedDimension = getMockedDimension(DimensionTypes.TIME);
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(existedDimension);
        new Dimension(statisticModelNode, DimensionTypes.TIME);
        verify(statisticsService, never()).createDimension(eq(statisticModelNode), eq(DimensionTypes.TIME), eq(Boolean.FALSE));
    }

    @Test
    public void testConstructorIfExist() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testConstructorIfExist start");
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(null);
        new Dimension(statisticModelNode, DimensionTypes.TIME);
        verify(statisticsService, atLeastOnce())
                .createDimension(eq(statisticModelNode), eq(DimensionTypes.TIME), eq(Boolean.FALSE));
    }

    @Test
    public void testGetStatisticsLevelIfFound() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetStatisticsLevelIfFound start");
        Node existedDimension = getMockedDimension(DimensionTypes.TIME);
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        when(statisticsService.findStatisticsLevelNode(eq(existedDimension), eq(LEVEL_NAME))).thenReturn(mockedLevel);
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(existedDimension);
        List<Node> levelNodes = new ArrayList<Node>();
        levelNodes.add(mockedLevel);
        when(statisticsService.getFirstRelationTraverser(eq(existedDimension), eq(DatasetRelationTypes.CHILD))).thenReturn(
                levelNodes);
        Dimension dimension = new Dimension(statisticModelNode, existedDimension);
        StatisticsLevel level = dimension.getLevel(LEVEL_NAME);
        Assert.assertEquals("Unexpected root node", mockedLevel, level.getRootNode());
    }

    @Test
    public void testGetStatisticsLevelIfNotFound() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetStatisticsLevelIfNotFound start");
        Node existedDimension = getMockedDimension(DimensionTypes.TIME);
        Node mockedLevel = getMockedLevel(LEVEL_NAME, Boolean.TRUE);
        when(statisticsService.findStatisticsLevelNode(eq(existedDimension), eq(LEVEL_NAME))).thenReturn(mockedLevel);
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(existedDimension);
        when(statisticsService.getFirstRelationTraverser(eq(existedDimension), eq(DatasetRelationTypes.CHILD))).thenReturn(null);
        Dimension dimension = new Dimension(statisticModelNode, existedDimension);
        StatisticsLevel level = dimension.getLevel(LEVEL_NAME);
        Assert.assertNull("Unexpected node value", level);
    }

    @Test
    public void testGetAllLevelsIfNotFound() throws DatabaseException {
        Node existedDimension = getMockedDimension(DimensionTypes.TIME);
        when(statisticsService.getFirstRelationTraverser(eq(existedDimension), eq(DatasetRelationTypes.CHILD))).thenReturn(null);
        Dimension dimension = new Dimension(statisticModelNode, existedDimension);
        Assert.assertFalse("Expected empty node list", dimension.getAllLevels().iterator().hasNext());
    }

    @Test
    public void testGetAllLevelsIfFound() throws DatabaseException {
        Node existedDimension = getMockedDimension(DimensionTypes.TIME);
        List<Node> mockedLevels = new ArrayList<Node>();
        for (int i = NumberUtils.INTEGER_ZERO; i < ARRAYS_SIZE; i++) {
            mockedLevels.add(getMockedLevel(LEVEL_NAME + i, Boolean.TRUE));
        }
        when(statisticsService.getFirstRelationTraverser(eq(existedDimension), eq(DatasetRelationTypes.CHILD))).thenReturn(
                mockedLevels);
        Dimension dimension = new Dimension(statisticModelNode, existedDimension);
        Iterable<StatisticsLevel> levels = dimension.getAllLevels();
        Assert.assertTrue("Expected empty node list", dimension.getAllLevels().iterator().hasNext());
        for (StatisticsLevel level : levels) {
            boolean isFound = Boolean.FALSE;
            for (Node expectedLevel : mockedLevels) {
                if (expectedLevel.equals(level.getRootNode())) {
                    isFound = Boolean.TRUE;
                    break;
                }
            }
            Assert.assertTrue("level Not found", isFound);
        }
    }
}
