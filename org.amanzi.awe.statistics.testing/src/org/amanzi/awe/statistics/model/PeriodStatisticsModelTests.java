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
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
public class PeriodStatisticsModelTests extends AbstractStatisticsModelTests {
    private static final Logger LOGGER = Logger.getLogger(PeriodStatisticsModel.class);
    private static final String SCELL_NAME = "scell";

    @Test
    public void testCounstructorIfEverythingIsOk() throws DatabaseException {
        LOGGER.info("testCounstructorIfEverythingIsOk started ");
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        PeriodStatisticsModel model = new PeriodStatisticsModel(statisticModelNode, Period.HOURLY);
        assertEquals("Unexpected result", mockedNode, model.getRootNode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCounstructorIfOneOfParametersIsNul() throws DatabaseException {
        LOGGER.info("testCounstructorIfOneParameterIsNul started ");
        Node mockedNode = getMockedPeriodNode(Period.HOURLY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedNode);
        PeriodStatisticsModel model = new PeriodStatisticsModel(null, Period.HOURLY);
        assertEquals("Unexpected result", mockedNode, model.getRootNode());
    }

    @Test
    public void testAddSourcePeriodIfEverythingIsOk() throws DatabaseException {
        LOGGER.info("testAddSourcePeriodIfEverythingIsOk started ");
        Node mockedHourly = getMockedPeriodNode(Period.HOURLY);
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedHourly);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.DAILY))).thenReturn(mockedDaily);
        PeriodStatisticsModel modelHourly = new PeriodStatisticsModel(statisticModelNode, Period.HOURLY);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(statisticModelNode, Period.DAILY);
        modelDaily.addSourcePeriod(modelHourly);
        Assert.assertEquals("Unexpected source period", modelHourly, modelDaily.getSourcePeriod());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcePeriodIfOneOfParametersIsNull() throws DatabaseException {
        LOGGER.info("testAddSourcePeriodIfOneOfParametersIsNull started ");
        Node mockedHourly = getMockedPeriodNode(Period.HOURLY);
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.HOURLY))).thenReturn(mockedHourly);
        when(statisticsService.getPeriod(eq(statisticModelNode), eq(Period.DAILY))).thenReturn(mockedDaily);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(statisticModelNode, Period.DAILY);
        modelDaily.addSourcePeriod(null);
    }

    @Test
    public void testGetSRowIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfNotFounded started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.findNodeInChain(eq(mockedDaily), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(null);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        modelDaily.getSRow(Long.MIN_VALUE);
        verify(statisticsService, atLeastOnce()).createSRow(eq(mockedDaily), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }

    @Test
    public void testGetSRowIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSRowIfFounded started ");
        Node mockedSrow = getMockedNode();
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.findNodeInChain(eq(mockedDaily), eq(DriveModel.TIMESTAMP), any(Long.class))).thenReturn(mockedSrow);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        modelDaily.getSRow(Long.MIN_VALUE);
        verify(statisticsService, never()).createSRow(eq(mockedDaily), eq(Long.MIN_VALUE), eq(Boolean.FALSE));
    }

    @Test
    public void testGetScellIfNotFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfNotFounded started ");
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_ROW.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);

        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(null);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        modelDaily.getSCell(srowDataElement, SCELL_NAME);
        verify(statisticsService, atLeastOnce()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test
    public void testGetSCellIfFounded() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfFounded started ");
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_ROW.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        Node mockedScell = getMockedNode();
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        when(statisticsService.findNodeInChain(eq(mockedSrow), eq(DatasetService.NAME), any(String.class))).thenReturn(mockedScell);

        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);

        modelDaily.getSCell(srowDataElement, SCELL_NAME);
        verify(statisticsService, never()).createSCell(eq(mockedSrow), eq(SCELL_NAME), eq(Boolean.FALSE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfSrowNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfSrowNull started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);

        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);

        modelDaily.getSCell(null, SCELL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfSrowHasIncorrectType() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfSrowHasIncorrectType started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);

        modelDaily.getSCell(srowDataElement, SCELL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSCellIfScellNameIsIncorrect() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSCellIfScellNameIsIncorrect started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        Node mockedSrow = getMockedNode();
        when(mockedSrow.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        DataElement srowDataElement = new DataElement(mockedSrow);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        modelDaily.getSCell(srowDataElement, StringUtils.EMPTY);
    }

    @Test
    public void testAddSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSources started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        Node mockedScell = getMockedNode();
        when(mockedScell.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        when(mockedScell.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(SCELL_NAME);
        DataElement scellDataElement = new DataElement(mockedScell);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        modelDaily.addSources(scellDataElement, generatedSources);
        verify(statisticsService, times(listSize)).addSource(eq(mockedScell), any(Node.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSCellNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSCellNull started ");
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        modelDaily.addSources(null, generatedSources);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSourcesNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSourcesNull started ");
        Node mockedScell = getMockedNode();
        when(mockedScell.getProperty(eq(DatasetService.TYPE), eq(null))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        when(mockedScell.getProperty(eq(DatasetService.NAME), eq(null))).thenReturn(SCELL_NAME);
        DataElement scellDataElement = new DataElement(mockedScell);
        Node mockedDaily = getMockedPeriodNode(Period.DAILY);
        PeriodStatisticsModel modelDaily = new PeriodStatisticsModel(mockedDaily);
        modelDaily.addSources(scellDataElement, null);
    }

    /**
     * @param d
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
