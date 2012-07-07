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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.neo.services.impl.statistics.PropertyStatisticsService.PropertyStatisticsRelationshipType;
import org.amanzi.neo.services.impl.statistics.internal.NodeTypeVault;
import org.amanzi.neo.services.impl.statistics.internal.PropertyVault;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
import org.amanzi.neo.services.util.AbstractServiceTest;
import org.apache.commons.lang3.math.NumberUtils;
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

    private static final String PROPERTY_NAME = "property";

    private PropertyStatisticsService service;

    private INodeService nodeService;

    private StatisticsVault vault;

    private NodeTypeVault nodeTypeVault;

    private PropertyVault propertyVault;

    private Node rootNode;

    private Node statNode;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        rootNode = getNodeMock();
        statNode = getNodeMock();

        vault = mock(StatisticsVault.class);
        nodeTypeVault = mock(NodeTypeVault.class);
        propertyVault = mock(PropertyVault.class);

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
    public void testCheckActivityIfVaultChanged() throws Exception {
        NodeTypeVault subVault1 = createNodeTypeVault(true);
        NodeTypeVault subVault2 = createNodeTypeVault(true);

        List<NodeTypeVault> subVaults = new ArrayList<NodeTypeVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(vault.isChanged()).thenReturn(true);
        when(vault.getAllNodeTypeVaults()).thenReturn(subVaults);
        doNothing().when(service).saveNodeTypeVault(statNode, subVault1);
        doNothing().when(service).saveNodeTypeVault(statNode, subVault2);

        service.saveStatisticsVault(statNode, vault);

        verify(service).updateStatisticsInfo(statNode, vault);
        verify(service).saveNodeTypeVault(statNode, subVault1);
        verify(service).saveNodeTypeVault(statNode, subVault2);
    }

    @Test
    public void testCheckActivityIfNodeTypeVaultChanged() throws Exception {
        NodeTypeVault subVault1 = createNodeTypeVault(true);
        NodeTypeVault subVault2 = createNodeTypeVault(false);

        List<NodeTypeVault> subVaults = new ArrayList<NodeTypeVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(vault.isChanged()).thenReturn(true);
        when(vault.getAllNodeTypeVaults()).thenReturn(subVaults);
        doNothing().when(service).saveNodeTypeVault(statNode, subVault1);
        doNothing().when(service).saveNodeTypeVault(statNode, subVault2);

        service.saveStatisticsVault(statNode, vault);

        verify(service, never()).saveNodeTypeVault(statNode, subVault2);
        verify(service).saveNodeTypeVault(statNode, subVault1);
    }

    @Test
    public void testCheckActivityOnUpdateStatisticsVault() throws Exception {
        when(vault.getCount()).thenReturn(5);

        service.updateStatisticsInfo(statNode, vault);

        verify(nodeService).updateProperty(statNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), 5);
    }

    @Test
    public void testCheckActivityOnUpdateNodeTypeVault() throws Exception {
        Node nodeTypeNode = getNodeMock();

        when(nodeTypeVault.getCount()).thenReturn(5);
        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeService.getChildByName(statNode, TestNodeType.TEST1.getId(), PropertyStatisticsNodeType.STATISTICS_VAULT))
                .thenReturn(null);
        when(
                nodeService.createNode(statNode, PropertyStatisticsNodeType.STATISTICS_VAULT, NodeServiceRelationshipType.CHILD,
                        TestNodeType.TEST1.getId())).thenReturn(nodeTypeNode);

        service.saveNodeTypeVault(statNode, nodeTypeVault);

        verify(nodeService).updateProperty(nodeTypeNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), 5);
        verify(nodeTypeVault, atLeast(2)).getNodeType();
        verify(nodeService).getChildByName(statNode, TestNodeType.TEST1.getId(), PropertyStatisticsNodeType.STATISTICS_VAULT);
        verify(nodeService).createNode(statNode, PropertyStatisticsNodeType.STATISTICS_VAULT, NodeServiceRelationshipType.CHILD,
                TestNodeType.TEST1.getId());
    }

    @Test
    public void testCheckActivityOnUpdateNodeTypeVaultWithExistingNode() throws Exception {
        Node nodeTypeNode = getNodeMock();

        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeService.getChildByName(statNode, TestNodeType.TEST1.getId(), PropertyStatisticsNodeType.STATISTICS_VAULT))
                .thenReturn(nodeTypeNode);
        doReturn(null).when(nodeService).createNode(eq(statNode), eq(PropertyStatisticsNodeType.STATISTICS_VAULT),
                eq(NodeServiceRelationshipType.CHILD), eq(TestNodeType.TEST1.getId()));

        service.saveNodeTypeVault(statNode, nodeTypeVault);

        verify(nodeTypeVault).getNodeType();
        verify(nodeService).getChildByName(statNode, TestNodeType.TEST1.getId(), PropertyStatisticsNodeType.STATISTICS_VAULT);
        verify(nodeService, never()).createNode(statNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                NodeServiceRelationshipType.CHILD, TestNodeType.TEST1.getId());
    }

    @Test
    public void testCheckNoActivityIfNodeVaultChanged() throws Exception {
        Node nodeTypeNode = getNodeMock();

        PropertyVault subVault1 = createPropertyVault(true);
        PropertyVault subVault2 = createPropertyVault(true);

        List<PropertyVault> subVaults = new ArrayList<PropertyVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeTypeVault.getAllPropertyVaults()).thenReturn(subVaults);
        doReturn(nodeTypeNode).when(service).updateNodeTypeVault(statNode, nodeTypeVault);
        doNothing().when(service).savePropertyStatistics(nodeTypeNode, subVault1);
        doNothing().when(service).savePropertyStatistics(nodeTypeNode, subVault2);

        service.saveNodeTypeVault(statNode, nodeTypeVault);

        verify(nodeTypeVault).getAllPropertyVaults();
        verify(service).savePropertyStatistics(nodeTypeNode, subVault1);
        verify(service).savePropertyStatistics(nodeTypeNode, subVault2);
    }

    @Test
    public void testCheckNoActivityIfNodeVaultChangedWithNotChangedProperties() throws Exception {
        Node nodeTypeNode = getNodeMock();

        PropertyVault subVault1 = createPropertyVault(true);
        PropertyVault subVault2 = createPropertyVault(false);

        List<PropertyVault> subVaults = new ArrayList<PropertyVault>();
        subVaults.add(subVault1);
        subVaults.add(subVault2);

        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeTypeVault.getAllPropertyVaults()).thenReturn(subVaults);
        doReturn(nodeTypeNode).when(service).updateNodeTypeVault(statNode, nodeTypeVault);
        doNothing().when(service).savePropertyStatistics(nodeTypeNode, subVault1);
        doNothing().when(service).savePropertyStatistics(nodeTypeNode, subVault2);

        service.saveNodeTypeVault(statNode, nodeTypeVault);

        verify(nodeTypeVault).getAllPropertyVaults();
        verify(service).savePropertyStatistics(nodeTypeNode, subVault1);
        verify(service, never()).savePropertyStatistics(nodeTypeNode, subVault2);
    }

    @Test
    public void testCheckServiceActivityOnSavingPropertyToNewNode() throws Exception {
        Node nodeTypeVaultNode = getNodeMock();
        Node propertyVaultNode = getNodeMock();

        when(propertyVault.getPropertyName()).thenReturn(PROPERTY_NAME);
        when(nodeService.getChildByName(nodeTypeVaultNode, PROPERTY_NAME, PropertyStatisticsNodeType.STATISTICS_VAULT)).thenReturn(
                null);
        when(
                nodeService.createNode(nodeTypeVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                        NodeServiceRelationshipType.CHILD, PROPERTY_NAME)).thenReturn(propertyVaultNode);

        doNothing().when(service).updatePropertyVault(propertyVaultNode, propertyVault);

        service.savePropertyStatistics(nodeTypeVaultNode, propertyVault);

        verify(propertyVault, atLeast(2)).getPropertyName();
        verify(nodeService).getChildByName(nodeTypeVaultNode, PROPERTY_NAME, PropertyStatisticsNodeType.STATISTICS_VAULT);
        verify(nodeService).createNode(nodeTypeVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                NodeServiceRelationshipType.CHILD, PROPERTY_NAME);
        verify(service).updatePropertyVault(propertyVaultNode, propertyVault);
    }

    @Test
    public void testCheckServiceActivityOnSavingPropertyToExistingNode() throws Exception {
        Node nodeTypeVaultNode = getNodeMock();
        Node propertyVaultNode = getNodeMock();

        when(propertyVault.getPropertyName()).thenReturn(PROPERTY_NAME);
        when(nodeService.getChildByName(nodeTypeVaultNode, PROPERTY_NAME, PropertyStatisticsNodeType.STATISTICS_VAULT)).thenReturn(
                propertyVaultNode);
        doNothing().when(service).updatePropertyVault(propertyVaultNode, propertyVault);

        service.savePropertyStatistics(nodeTypeVaultNode, propertyVault);

        verify(propertyVault).getPropertyName();
        verify(nodeService).getChildByName(nodeTypeVaultNode, PROPERTY_NAME, PropertyStatisticsNodeType.STATISTICS_VAULT);
        verify(nodeService, never()).createNode(nodeTypeVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                NodeServiceRelationshipType.CHILD, PROPERTY_NAME);
        verify(service).updatePropertyVault(propertyVaultNode, propertyVault);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVault() throws Exception {
        Node propertyVaultNode = initializeMockedPropertyVaultNode(0);

        when(nodeService.getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), 0, false)).thenReturn(0);

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService).updateProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), "some class");
        verify(nodeService).updateProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), 0);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithValues() throws Exception {
        Integer[] counts = new Integer[] {2, 3, 4};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(0, counts);

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService).getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), NumberUtils.INTEGER_ZERO,
                false);

        verify(nodeService).updateProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), "some class");

        verify(nodeService, atLeast(counts.length)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix()), contains(PROPERTY_NAME));

        verify(nodeService, atLeast(counts.length)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithUnchangedValues() throws Exception {
        Integer[] counts = new Integer[] {2, 3, 4};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, atLeast(counts.length)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithAddedValues() throws Exception {
        Integer[] counts = new Integer[] {2, 3, 4, 5};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length - 1, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, atLeast(counts.length)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));

        verify(nodeService).updateProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + (counts.length - 1), PROPERTY_NAME + (counts.length - 1));
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedValues() throws Exception {
        Integer[] counts = new Integer[] {2, 3, 4, 5};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < (counts.length - 1); i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, atLeast(counts.length - 1)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));

        verify(nodeService).removeNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + (counts.length - 1), false);
        verify(nodeService).removeNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + (counts.length - 1), false);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedInMiddleValues() throws Exception {
        Integer[] counts = new Integer[] {2, 3, 4, 5};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            if (i != 1) {
                when(
                        nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i,
                                null, true)).thenReturn(PROPERTY_NAME + i);
            }
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, atLeast(counts.length - 1)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));

        verify(nodeService).removeNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + 1, false);
        verify(nodeService).removeNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + 1, false);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedInMiddleValuesWithRenaming() throws Exception {
        Integer[] counts = new Integer[] {2, null, null, 5};

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);

        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, atLeast(counts.length - 1)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));

        verify(nodeService, atLeast(2)).removeNodeProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix()), eq(Boolean.FALSE));
        verify(nodeService, atLeast(2)).removeNodeProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), eq(Boolean.FALSE));
        verify(nodeService).renameNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + 3,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + 1, false);
        verify(nodeService).renameNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + 3,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + 1, false);
    }

    private Node initializeMockedPropertyVaultNode(int size, Integer... counts) throws Exception {
        Node propertyVaultNode = getNodeMock();
        Map<Object, Integer> values = getValuesMap(counts);

        when(propertyVault.getClassName()).thenReturn("some class");
        when(propertyVault.getValuesMap()).thenReturn(values);

        when(nodeService.getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), 0, false)).thenReturn(size);

        return propertyVaultNode;
    }

    private Map<Object, Integer> getValuesMap(Integer... counts) {
        Map<Object, Integer> result = new HashMap<Object, Integer>();

        int j = 0;
        for (Integer i : counts) {
            result.put(PROPERTY_NAME + j++, i);
        }

        return result;
    }

    private PropertyVault createPropertyVault(boolean isChanged) {
        PropertyVault vault = mock(PropertyVault.class);

        when(vault.isChanged()).thenReturn(isChanged);

        return vault;
    }

    private NodeTypeVault createNodeTypeVault(boolean isChanged) {
        NodeTypeVault result = mock(NodeTypeVault.class);

        when(result.isChanged()).thenReturn(isChanged);

        return result;
    }
}
