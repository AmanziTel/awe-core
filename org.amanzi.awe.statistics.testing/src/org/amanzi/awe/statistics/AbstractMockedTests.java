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

package org.amanzi.awe.statistics;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.junit.Before;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * common action for model tests
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractMockedTests extends AbstractStatisticsTest {
    protected static StatisticsService statisticsService;
    protected static final String PARENT_NAME = "model";
    protected static final String MODEL_NAME = "model";
    protected static Node parentNode;
    protected static Node statisticModelNode;
    protected static final int ARRAYS_SIZE = 5;
    protected static final String SCELL_NAME = "scell";
    protected static final String SGROUP_NAME = "sgroup";
    protected static final String FIRST_LEVEL_NAME = "test";
    protected static final String SECOND_LEVEL_NAME = "hourly";
    protected static final String LEVEL_NAME = "levelName";
    protected static final String NAME_FORMAT = "%s, %s";
    protected static final String AGGREGATED_STATISTICS_NAME = "aggregation";
    protected static final String SROW_NAME = "srow";

    @Before
    public void setUp() {
        statisticsService = getMockedService();
        initMockedParentNode();
        initMockedStatisticsRootModel();
    }

    /**
     * @param hourly
     * @return
     */
    protected Node getMockedAggregatedStatistics(String name) {
        Node aggregation = getMockedNode();
        when(statisticsService.getNodeProperty(eq(aggregation), eq(DatasetService.NAME))).thenReturn(name);
        when(statisticsService.getType(eq(aggregation))).thenReturn(StatisticsNodeTypes.STATISTICS.getId());
        return aggregation;
    }

    /**
     *
     */
    protected void initMockedStatisticsRootModel() {
        statisticModelNode = getMockedNode();
        when(statisticModelNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(MODEL_NAME);
        when(statisticModelNode.getProperty(eq(DatasetService.NAME))).thenReturn(MODEL_NAME);
    }

    /**
     * @return
     */
    protected void initMockedParentNode() {
        parentNode = getMockedNode();
        when(parentNode.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(PARENT_NAME);
    }

    /**
     * @return
     */
    protected StatisticsService getMockedService() {
        statisticsService = mock(StatisticsService.class);
        return statisticsService;
    }

    /**
     * mock timestamp property in parent node
     * 
     * @param min
     * @param max
     */
    protected void mockTimestampParent(Long min, Long max) {
        when(parentNode.getProperty(eq(DriveModel.MIN_TIMESTAMP))).thenReturn(min);
        when(parentNode.getProperty(eq(DriveModel.MAX_TIMESTAMP))).thenReturn(max);
    }

    protected Node getMockedNode() {
        Node node = mock(Node.class);
        return node;
    }

    /**
     * return dimension root
     * 
     * @param fakeDimensionType
     * @return
     */
    protected Node getMockedNodeWithNameAndType(String type, String name) {
        Node dimension = getMockedNode();
        when(statisticsService.getType(eq(dimension))).thenReturn(type);
        when(statisticsService.getNodeProperty(eq(dimension), eq(DatasetService.NAME))).thenReturn(name);
        return dimension;
    }

    /**
     * return mocked dimension node
     */
    protected Node getMockedDimension(DimensionTypes type) {
        return getMockedNodeWithNameAndType(StatisticsNodeTypes.DIMENSION.getId(), type.getId());
    }

    /**
     * create mocked level
     * 
     * @param name
     * @return
     */
    protected Node getMockedLevel(String name, boolean mockFoundation) {
        Node statRoot = getMockedNode();
        if (mockFoundation) {
            when(statisticsService.findStatisticsLevelNode(any(Node.class), eq(name))).thenReturn(statRoot);
        }
        when(statisticsService.getType(eq(statRoot))).thenReturn(StatisticsNodeTypes.LEVEL.getId());
        when(statisticsService.getNodeProperty(eq(statRoot), eq(DatasetService.NAME))).thenReturn(name);
        return statRoot;
    }

    /**
     * get group root with mocked services
     * 
     * @return
     */
    protected Node getMockedGroup(String name) {
        Node mockedGroup = getMockedNode();
        when(statisticsService.getNodeProperty(eq(mockedGroup), eq(DatasetService.NAME))).thenReturn(name);
        when(statisticsService.getType(eq(mockedGroup))).thenReturn(StatisticsNodeTypes.S_GROUP.getId());
        return mockedGroup;
    }

    /**
     * return mocked sRow
     * 
     * @param timstamp
     * @return
     */
    protected Node getMockedSrow(Long timstamp, String name) {
        Node mockedSrow = getMockedNode();
        when(statisticsService.getType(eq(mockedSrow))).thenReturn(StatisticsNodeTypes.S_ROW.getId());
        when(statisticsService.getNodeProperty(eq(mockedSrow), eq(DriveModel.TIMESTAMP))).thenReturn(timstamp);
        when(statisticsService.getNodeProperty(eq(mockedSrow), eq(StatisticsService.NAME))).thenReturn(name);
        return mockedSrow;
    }

    /**
     * mocked sCell
     * 
     * @param name
     * @return
     */
    protected Node getMockedScell(String name) {
        Node mockedScell = getMockedNode();
        when(statisticsService.getNodeProperty(eq(mockedScell), eq(DatasetService.NAME))).thenReturn(name);
        when(statisticsService.getType(eq(mockedScell))).thenReturn(StatisticsNodeTypes.S_CELL.getId());
        return mockedScell;

    }

    /**
     * @param size
     * @return
     */
    protected List<IDataElement> generateSources(int size) {
        List<IDataElement> dataElements = new ArrayList<IDataElement>();
        for (int i = 0; i < size; i++) {
            Node sourceNode = getMockedNode();
            DataElement element = new DataElement(sourceNode);
            dataElements.add(element);
        }
        return dataElements;
    }
}
