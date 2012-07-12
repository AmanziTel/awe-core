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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsModelTests extends AbstractStatisticsModelTests {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIfParentIsNull() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException {
        new StatisticsModel(null, null);
    }

    @Test
    public void testConstructorIfStatisticsNotExist() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException {
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(null);
        when(statisticsService.createStatisticsModelRoot(eq(parentNode), any(String.class), eq(Boolean.FALSE))).thenReturn(
                statisticModelNode);
        StatisticsModel model = new StatisticsModel(parentNode, MODEL_NAME);
        assertEquals("Unexpected model root", statisticModelNode, model.getRootNode());

    }

    @Test
    public void testGetAllDimensions() throws IllegalArgumentException, DatabaseException {
        // (String)statisticService.getNodeProperty(dimension, DatasetService.NAME)
        Node dimensionMocked = getMockedNode();
        when(statisticsService.getNodeProperty(eq(dimensionMocked), eq(DatasetService.NAME))).thenReturn(DimensionTypes.TIME);
        StatisticsModel model = new StatisticsModel(parentNode, MODEL_NAME);
        Iterable<Dimension> dimensions = model.getAllDimensions();
        Assert.assertTrue(dimensions.iterator().hasNext());
        Assert.assertEquals(dimensionMocked, dimensions.iterator().next().getRootNode());
    }

    @Test
    public void testGetDimensionIfFound() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException {
        Node dimensionMocked = getMockedNode();
        when(statisticsService.getNodeProperty(eq(dimensionMocked), eq(DatasetService.NAME))).thenReturn(DimensionTypes.TIME);
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(dimensionMocked);
        StatisticsModel model = new StatisticsModel(parentNode, MODEL_NAME);
        Dimension dimension = model.getDimension(DimensionTypes.TIME);
        verify(statisticsService, never()).createDimension(any(Node.class), any(DimensionTypes.class), eq(false));
        Assert.assertEquals(dimensionMocked, dimension.getRootNode());
    }
}
