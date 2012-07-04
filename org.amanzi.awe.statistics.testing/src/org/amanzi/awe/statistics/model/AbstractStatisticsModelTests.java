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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.amanzi.awe.statistics.AbstractStatisticsTest;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.DatasetService;
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
public abstract class AbstractStatisticsModelTests extends AbstractStatisticsTest {
    protected static StatisticsService statisticsService;
    protected static final String PARENT_NAME = "model";
    protected static final String MODEL_NAME = "model";
    protected static Node parentNode;
    protected static Node statisticModelNode;

    @Before
    public void setUp() {
        statisticsService = getMockedService();
        initMockedParentNode();
        initMockedStatisticsRootModel();
        StatisticsModel.setStatisticsService(statisticsService);
        PeriodStatisticsModel.setStatisticsService(statisticsService);
    }

    /**
     * @param hourly
     * @return
     */
    protected Node getMockedPeriodNode(Period hourly) {
        String id = hourly.getId();
        Node period = getMockedNode();
        when(period.getProperty(eq(DatasetService.NAME), any(String.class))).thenReturn(id);
        return period;
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
}
