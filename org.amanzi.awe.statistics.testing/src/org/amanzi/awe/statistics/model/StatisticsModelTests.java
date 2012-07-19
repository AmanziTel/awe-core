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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.entities.impl.Dimension;
import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
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
public class StatisticsModelTests extends AbstractModelTest {

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
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        Node dimensionMocked = getMockedDimension(DimensionTypes.TIME);
        List<Node> foundedNodes = new ArrayList<Node>();
        foundedNodes.add(dimensionMocked);
        when(statisticsService.getFirstRelationTraverser(eq(statisticModelNode), eq(DatasetRelationTypes.CHILD))).thenReturn(
                foundedNodes);
        StatisticsModel model = new StatisticsModel(parentNode, MODEL_NAME);
        Iterable<Dimension> dimensions = model.getAllDimensions();
        Assert.assertTrue("Unexpected empty list", dimensions.iterator().hasNext());
        Assert.assertEquals(dimensionMocked, dimensions.iterator().next().getRootNode());
    }

    @Test
    public void testGetDimensionIfFound() throws IllegalArgumentException, DatabaseException, IllegalNodeDataException {
        when(statisticsService.findStatistic(eq(parentNode), any(String.class))).thenReturn(statisticModelNode);
        Node dimensionMocked = getMockedDimension(DimensionTypes.TIME);
        when(statisticsService.getNodeProperty(eq(dimensionMocked), eq(DatasetService.NAME))).thenReturn(
                DimensionTypes.TIME.getId());
        when(statisticsService.findDimension(eq(statisticModelNode), eq(DimensionTypes.TIME))).thenReturn(dimensionMocked);
        StatisticsModel model = new StatisticsModel(parentNode, MODEL_NAME);
        Dimension dimension = model.getDimension(DimensionTypes.TIME);
        verify(statisticsService, never()).createDimension(any(Node.class), any(DimensionTypes.class), eq(false));
        Assert.assertEquals(dimensionMocked, dimension.getRootNode());
    }
}
