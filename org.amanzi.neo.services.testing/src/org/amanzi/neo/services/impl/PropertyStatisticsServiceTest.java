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

package org.amanzi.neo.services.impl;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.IPropertyStatisticsService;
import org.amanzi.neo.services.impl.PropertyStatisticsService.PropertyStatisticsRelationshipType;
import org.amanzi.neo.services.impl.statistics.IPropertyStatistics;
import org.amanzi.neo.services.util.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyStatisticsServiceTest extends AbstractServiceTest {

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private IPropertyStatisticsService service;

    private INodeService nodeService;

    private IPropertyStatistics vault;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        vault = mock(IPropertyStatistics.class);
        nodeService = mock(INodeService.class);

        service = new PropertyStatisticsService(getService(), GENERAL_NODE_PROPERTIES, nodeService);

        setReadOnly(true);
    }

    @Test
    public void testCheckServiceActivityOnLoadWithoutStatistics() throws Exception {
        Node rootNode = getNodeMock();

        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(null);

        service.loadStatistics(rootNode);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
    }

    @Test
    public void testCheckServiceActivityOnSaveWithoutStatistics() throws Exception {
        Node rootNode = getNodeMock();

        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(null);

        service.saveStatistics(rootNode, vault);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
    }

    @Test
    public void testCheckServiceActivityOnLoadWithStatistics() throws Exception {
        Node statNode = getNodeMock();
        Node rootNode = getNodeMock();

        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.loadStatistics(rootNode);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService, never()).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
    }

    @Test
    public void testCheckServiceActivityOnSaveWithStatistics() throws Exception {
        Node rootNode = getNodeMock();
        Node statNode = getNodeMock();

        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.saveStatistics(rootNode, vault);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService, never()).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
    }
}
