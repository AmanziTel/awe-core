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

package org.amanzi.neo.services.impl.statistics;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.statistics.PropertyStatisticsService.PropertyStatisticsRelationshipType;
import org.amanzi.neo.services.impl.statistics.internal.NodeTypeVault;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
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

    private static final IPropertyStatisticsNodeProperties PROPERTY_STATISTICS_NODE_PROPERTIES = new PropertyStatisticsNodeProperties();

    private PropertyStatisticsService service;

    private INodeService nodeService;

    private StatisticsVault vault;

    private Node rootNode;

    private Node statNode;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        rootNode = getNodeMock();
        statNode = getNodeMock();

        vault = mock(StatisticsVault.class);
        nodeService = mock(INodeService.class);

        service = spy(new PropertyStatisticsService(getService(), GENERAL_NODE_PROPERTIES, nodeService,
                PROPERTY_STATISTICS_NODE_PROPERTIES));

        setReadOnly(true);
    }

    @Test
    public void testCheckServiceActivityOnLoadWithoutStatistics() throws Exception {
        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(null);
        when(
                nodeService.createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.loadStatistics(rootNode);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(service).loadStatisticsVault(statNode);
    }

    @Test
    public void testCheckServiceActivityOnSaveWithoutStatistics() throws Exception {
        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(null);
        when(
                nodeService.createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.saveStatistics(rootNode, vault);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(service).saveStatisticsVault(statNode, vault);
    }

    @Test
    public void testCheckServiceActivityOnLoadWithStatistics() throws Exception {
        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.loadStatistics(rootNode);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService, never()).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(service).loadStatisticsVault(statNode);
    }

    @Test
    public void testCheckServiceActivityOnSaveWithStatistics() throws Exception {
        when(
                nodeService.getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                        PropertyStatisticsRelationshipType.PROPERTY_STATISTICS)).thenReturn(statNode);

        service.saveStatistics(rootNode, vault);

        verify(nodeService).getSingleChild(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(nodeService, never()).createNode(rootNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        verify(service).saveStatisticsVault(statNode, vault);
    }

    @Test
    public void testCheckNoActivityIfVaultNotChanged() throws Exception {
        when(vault.isChanged()).thenReturn(false);

        service.saveStatisticsVault(rootNode, vault);

        verify(service, never()).updateStatisticsInfo(rootNode, vault);
        verify(service, never()).saveNodeTypeVault(any(Node.class), any(NodeTypeVault.class));
    }

    @Test
    public void testCheckNoActivityIfVaultChanged() throws Exception {
        NodeTypeVault subVault1 = createNodeTypeVault(true);
        NodeTypeVault subVault2 = createNodeTypeVault(true);

        List<NodeTypeVault> subVaults = new ArrayList<NodeTypeVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(vault.isChanged()).thenReturn(true);
        when(vault.getAllNodeTypeVaults()).thenReturn(subVaults);

        service.saveStatisticsVault(rootNode, vault);

        verify(service).updateStatisticsInfo(rootNode, vault);
        verify(service).saveNodeTypeVault(rootNode, subVault1);
        verify(service).saveNodeTypeVault(rootNode, subVault2);
    }

    @Test
    public void testCheckNoActivityIfNodeTypeVaultChanged() throws Exception {
        NodeTypeVault subVault1 = createNodeTypeVault(true);
        NodeTypeVault subVault2 = createNodeTypeVault(false);

        List<NodeTypeVault> subVaults = new ArrayList<NodeTypeVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(vault.isChanged()).thenReturn(true);
        when(vault.getAllNodeTypeVaults()).thenReturn(subVaults);

        service.saveStatisticsVault(rootNode, vault);

        verify(service, never()).saveNodeTypeVault(rootNode, subVault2);
        verify(service).saveNodeTypeVault(rootNode, subVault1);
    }

    @Test
    public void testCheckActivityOnUpdateStatisticsVault() throws Exception {
        setReadOnly(false);

        when(vault.getCount()).thenReturn(5);

        service.updateStatisticsInfo(statNode, vault);

        verify(statNode).setProperty(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), 5);
    }

    private NodeTypeVault createNodeTypeVault(boolean isChanged) {
        NodeTypeVault result = mock(NodeTypeVault.class);

        when(result.isChanged()).thenReturn(isChanged);

        return result;
    }
}
