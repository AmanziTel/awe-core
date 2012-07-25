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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.amanzi.awe.statistics.AbstractMockedTests;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.exceptions.UnableToModifyException;
import org.amanzi.awe.statistics.functions.Average;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * tests for statistics cell
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsCellTests extends AbstractMockedTests {
    private static final Logger LOGGER = Logger.getLogger(StatisticsCellTests.class);
    private static final String VALUE_PROPERTY = "value";

    @Test
    public void testAddSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSources started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        scell.addSources(generatedSources);
        verify(statisticsService, times(listSize)).addSource(eq(mockedScell), any(Node.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourcesIfSourcesNull() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testAddSourcesIfSourcesNull started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.addSources(null);
    }

    @Test
    public void testGetSources() throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("testGetSources started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        int listSize = (int)(Math.random() * 100);
        List<IDataElement> generatedSources = generateSources(listSize);
        List<Node> generatedSourcesNodes = new ArrayList<Node>();
        for (IDataElement dataElement : generatedSources) {
            Node nodeSource = ((DataElement)dataElement).getNode();
            generatedSourcesNodes.add(nodeSource);
        }

        when(statisticsService.getSources(eq(mockedScell))).thenReturn(generatedSourcesNodes);
        Iterable<IDataElement> elements = scell.getSources();
        Assert.assertEquals("Expected the same sources list", generatedSources, elements);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSingleSourceIfNull() throws DatabaseException {
        LOGGER.info("testAddSingleSourceIfNull started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.addSingleSource(null);
    }

    @Test
    public void testAddSingleSource() throws DatabaseException {
        LOGGER.info("testAddSingleSourceIfNull started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        IDataElement source = new DataElement(getMockedNode());
        scell.addSingleSource(source);
    }

    @Test
    public void testAddSourceCell() throws DatabaseException {
        LOGGER.info("testAddSourceCell started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node mockedScellSource = getMockedScell(SCELL_NAME + NumberUtils.INTEGER_ONE);
        Node mockedSrowSource = getMockedSrow(Long.MIN_VALUE, SROW_NAME + NumberUtils.INTEGER_ONE);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        StatisticsCell scellSource = new StatisticsCell(mockedSrowSource, mockedScellSource);
        scell.addSourceCell(scellSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSourceCellIfNull() throws DatabaseException {
        LOGGER.info("testAddSourceCell started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.addSourceCell(null);
    }

    @Test
    public void testDefaultSelection() throws DatabaseException {
        LOGGER.info("testAddSourceCell started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        Assert.assertFalse(scell.isSelected());
    }

    @Test
    public void testSetSelection() throws DatabaseException {
        LOGGER.info("testAddSourceCell started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        Assert.assertFalse(scell.isSelected());
        scell.setSelected(Boolean.TRUE);
        Assert.assertTrue(scell.isSelected());
    }

    @Test(expected = UnableToModifyException.class)
    public void testUpdateValueIfFunctionIsNull() throws DatabaseException, UnableToModifyException, IllegalNodeDataException {
        LOGGER.info("testUpdateValueIfFunctionIsNull started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.updateValue(ARRAYS_SIZE);
    }

    @Test
    public void testUpdateValue() throws DatabaseException, UnableToModifyException, IllegalNodeDataException {
        LOGGER.info("testUpdateValue started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        scell.setFunction(new Average());
        scell.updateValue(ARRAYS_SIZE);
        verify(statisticsService).setAnyProperty(eq(mockedScell), eq(VALUE_PROPERTY), eq(new Double(ARRAYS_SIZE)));
    }

    @Test
    public void testGetParent() throws DatabaseException, UnableToModifyException, IllegalNodeDataException {
        LOGGER.info("testGetParent started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        Node mockedSGroup = getMockedGroup(SGROUP_NAME);
        when(statisticsService.getParentLevelNode(eq(mockedSrow))).thenReturn(mockedSGroup);
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        StatisticsRow row = scell.getParent();
        Assert.assertEquals("Unexpected nodes", mockedSrow, row.getRootNode());
    }

    @Test
    public void testValue() throws DatabaseException, UnableToModifyException, IllegalNodeDataException {
        LOGGER.info("testGetParent started ");
        Node mockedScell = getMockedScell(SCELL_NAME);
        Node mockedSrow = getMockedSrow(Long.MIN_VALUE, SROW_NAME);
        when(statisticsService.getNodeProperty(eq(mockedScell), eq(VALUE_PROPERTY))).thenReturn(new Double(ARRAYS_SIZE));
        StatisticsCell scell = new StatisticsCell(mockedSrow, mockedScell);
        Assert.assertEquals("Unexpected values", scell.getValue(), new Double(ARRAYS_SIZE));
    }
}
