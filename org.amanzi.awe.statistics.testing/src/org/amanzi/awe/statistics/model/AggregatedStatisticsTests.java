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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.StringUtils;
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
    private static final String SCELL_NAME = "scell";
    private static final String SGROUP_NAME = "sgroup";
    private StatisticsLevel firstLevel;
    private StatisticsLevel secondLevel;
    private static final String FIRST_LEVEL_NAME = "test";
    private static final String SECOND_LEVEL_NAME = "hourly";
    private static final String NAME_FORMAT = "%s, %s";

    @Before
    public void setUp() {
        super.setUp();
        firstLevel = mockLevel(DimensionTypes.NETWORK, FIRST_LEVEL_NAME);
        secondLevel = mockLevel(DimensionTypes.TIME, SECOND_LEVEL_NAME);
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

    /**
     * @param type
     * @param name
     * @return
     */
    private StatisticsLevel mockLevel(DimensionTypes type, String name) {
        StatisticsLevel level = mock(StatisticsLevel.class);
        Node mockedDimension = mock(Node.class);
        Node mockedRoot = getMockedNode();
        when(statisticsService.getNodeProperty(eq(mockedDimension), eq(DatasetService.NAME))).thenReturn(type.getId());
        when(level.getParentNode()).thenReturn(mockedDimension);
        when(level.getName()).thenReturn(name);
        when(level.getRootNode()).thenReturn(mockedRoot);
        return level;
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
    public void testGetSRowIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfNotFounded started ");
        Node sGroup = getMockedGroup(SGROUP_NAME);
        when(statisticsService.findNodeInChain(eq(sGroup), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(null);
        AggregatedStatistics level = new AggregatedStatistics(firstLevel, secondLevel);
        level.getSRow(new DataElement(sGroup), Long.MIN_VALUE);
        verify(statisticsService, atLeastOnce()).createSRow(eq(sGroup), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }

    /**
     * @return
     */
    private Node getMockedGroup(String name) {
        Node mockedGroup = getMockedNode();
        when(mockedGroup.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(name);
        when(mockedGroup.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_GROUP.getId());
        return mockedGroup;
    }

    @Test
    public void testGetSRowIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfFounded started ");
        Node mockedSrow = getMockedNode();
        Node levelsRoot = getAggregatedRoot();
        Node sGroup = getMockedGroup(SGROUP_NAME);
        when(statisticsService.findNodeInChain(eq(levelsRoot), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(mockedSrow);
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        level.getSRow(new DataElement(sGroup), Long.MIN_VALUE);
        verify(statisticsService, never()).createSRow(eq(levelsRoot), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }

    @Test
    public void testGetScellIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfNotFounded started ");
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_ROW.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);

        Node levelsRoot = getAggregatedRoot();
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(null);
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        level.getSCell(srowDataElement, SCELL_NAME);
        verify(statisticsService, atLeastOnce()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test
    public void testGetSCellIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfFounded started ");
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_ROW.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        Node mockedScell = getMockedNode();
        Node levelsRoot = getAggregatedRoot();
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(mockedScell);

        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);

        level.getSCell(srowDataElement, SCELL_NAME);
        verify(statisticsService, never()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfSrowNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfSrowNull started ");
        Node levelsRoot = getAggregatedRoot();

        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);

        level.getSCell(null, SCELL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfSrowHasIncorrectType() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfSrowHasIncorrectType started ");
        Node levelsRoot = getAggregatedRoot();
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);

        level.getSCell(srowDataElement, SCELL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfScellNameIsIncorrect() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfScellNameIsIncorrect started ");
        Node levelsRoot = getAggregatedRoot();
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        level.getSCell(srowDataElement, StringUtils.EMPTY);
    }

    @Test
    public void testAddSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSources started ");
        Node levelsRoot = getAggregatedRoot();
        Node mockedScell = getMockedNode();
        when(mockedScell.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        when(mockedScell.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(SCELL_NAME);
        DataElement scellDataElement = new DataElement(mockedScell);
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        level.addSources(scellDataElement, generatedSources);
        verify(statisticsService, times(listSize)).addSource(eq(mockedScell), any(Node.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSCellNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSCellNull started ");
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        level.addSources(null, generatedSources);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSourcesNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSourcesNull started ");
        Node mockedScell = getMockedNode();
        when(mockedScell.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        when(mockedScell.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(SCELL_NAME);
        DataElement scellDataElement = new DataElement(mockedScell);

        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        level.addSources(scellDataElement, null);
    }

    @Test
    public void testGetSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSources started ");
        Node mockedScell = getMockedNode();
        when(mockedScell.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        when(mockedScell.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(SCELL_NAME);
        DataElement scellDataElement = new DataElement(mockedScell);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        List<Node> generatedSourcesNodes = new ArrayList<Node>();
        for (IDataElement dataElement : generatedSources) {
            Node nodeSource = ((DataElement)dataElement).getNode();
            generatedSourcesNodes.add(nodeSource);
        }

        when(statisticsService.getSources(eq(mockedScell))).thenReturn(generatedSourcesNodes);
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        Iterable<IDataElement> elements = level.getSources(scellDataElement);
        Assert.assertEquals("Expected the same sources list", generatedSources, elements);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSourcesIfParentIsNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSources started ");
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        level.getSources(null);
    }

    public void testGetGroupIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetGroup started ");
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        Node sgroupNode = getMockedGroup(SGROUP_NAME);
        when(statisticsService.findNodeInChain(eq(levelsRoot), eq(DatasetService.NAME), eq(SGROUP_NAME))).thenReturn(sgroupNode);
        level.getSGroup(SGROUP_NAME);
        verify(statisticsService, never()).createSGroup(eq(levelsRoot), eq(SGROUP_NAME), eq(Boolean.FALSE));
    }

    public void testGetGroupIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetGroup started ");
        Node levelsRoot = getAggregatedRoot();
        AggregatedStatistics level = new AggregatedStatistics(levelsRoot);
        when(statisticsService.findNodeInChain(eq(levelsRoot), eq(DatasetService.NAME), eq(SGROUP_NAME))).thenReturn(null);
        level.getSGroup(SGROUP_NAME);
        verify(statisticsService, atLeastOnce()).createSGroup(eq(levelsRoot), eq(SGROUP_NAME), eq(Boolean.FALSE));
    }

    /**
     * @param size
     * @return
     */
    private List<IDataElement> generateSources(int size) {
        List<IDataElement> dataElements = new ArrayList<IDataElement>();
        for (int i = 0; i < size; i++) {
            Node sourceNode = getMockedNode();
            DataElement element = new DataElement(sourceNode);
            dataElements.add(element);
        }
        return dataElements;
    }
}
