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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
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

    /** int TEST_SIZE_VALUE field */
    private static final int TEST_SIZE_VALUE = 3;

    /** Integer[] DEFAULT_EMPTIED_COUNTS_FOR_VALUES field */
    private static final Integer[] DEFAULT_EMPTIED_COUNTS_FOR_VALUES = {2, null, null, 5};

    /** Integer[] DEFAULT_UPDATED_COUNTS_FOR_VALUES field */
    private static final Integer[] DEFAULT_UPDATED_COUNTS_FOR_VALUES = {2, 3, 4, 5};

    /** Integer[] DEFAULT_COUNTS_FOR_VALUES field */
    private static final Integer[] DEFAULT_COUNTS_FOR_VALUES = {2, 3, 4};

    /** int TEST_COUNT_VAULT field */
    private static final int TEST_COUNT_VALUE = 5;

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

        doReturn(vault).when(service).loadStatisticsVault(statNode);

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

        doReturn(vault).when(service).loadStatisticsVault(statNode);

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
        when(vault.getCount()).thenReturn(TEST_COUNT_VALUE);

        service.updateStatisticsInfo(statNode, vault);

        verify(nodeService).updateProperty(statNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), TEST_COUNT_VALUE);
    }

    @Test
    public void testCheckActivityOnUpdateNodeTypeVault() throws Exception {
        Node nodeTypeNode = getNodeMock();

        when(nodeTypeVault.getCount()).thenReturn(TEST_COUNT_VALUE);
        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeService.getChildByName(statNode, TestNodeType.TEST1.getId(), PropertyStatisticsNodeType.STATISTICS_VAULT))
                .thenReturn(null);
        when(
                nodeService.createNode(statNode, PropertyStatisticsNodeType.STATISTICS_VAULT, NodeServiceRelationshipType.CHILD,
                        TestNodeType.TEST1.getId())).thenReturn(nodeTypeNode);

        service.saveNodeTypeVault(statNode, nodeTypeVault);

        verify(nodeService).updateProperty(nodeTypeNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), TEST_COUNT_VALUE);
        verify(nodeTypeVault, times(2)).getNodeType();
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

        verify(propertyVault, times(2)).getPropertyName();
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
        Integer[] counts = DEFAULT_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(0, counts);

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService).getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), NumberUtils.INTEGER_ZERO,
                false);

        verify(nodeService).updateProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), "some class");

        for (int i = 0; i < counts.length; i++) {
            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i),
                    contains(PROPERTY_NAME));

            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i),
                    any(Integer.class));
        }
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithUnchangedValues() throws Exception {
        Integer[] counts = DEFAULT_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        for (int i = 0; i < counts.length; i++) {
            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i),
                    any(Integer.class));
        }
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithAddedValues() throws Exception {
        Integer[] counts = DEFAULT_UPDATED_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length - 1, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        for (int i = 0; i < counts.length; i++) {
            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i),
                    any(Integer.class));
        }

        verify(nodeService).updateProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + (counts.length - 1), PROPERTY_NAME + (counts.length - 1));
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedValues() throws Exception {
        Integer[] counts = DEFAULT_UPDATED_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < (counts.length - 1); i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        for (int i = 0; i < (counts.length - 1); i++) {
            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i),
                    any(Integer.class));
        }

        verify(nodeService).removeNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + (counts.length - 1), false);
        verify(nodeService).removeNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + (counts.length - 1), false);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedInMiddleValues() throws Exception {
        Integer[] counts = DEFAULT_UPDATED_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            if (i != 1) {
                when(
                        nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i,
                                null, true)).thenReturn(PROPERTY_NAME + i);
            }
        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        for (int i = 0; i < (counts.length - 1); i++) {
            verify(nodeService).updateProperty(eq(propertyVaultNode), eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i),
                    any(Integer.class));
        }

        verify(nodeService).removeNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + 1, false);
        verify(nodeService).removeNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + 1, false);
    }

    @Test
    public void testCheckServiceActivityOnUpdatingPropertyVaultWithRemovedInMiddleValuesWithRenaming() throws Exception {
        Integer[] counts = DEFAULT_EMPTIED_COUNTS_FOR_VALUES;

        Node propertyVaultNode = initializeMockedPropertyVaultNode(counts.length, counts);

        for (int i = 0; i < counts.length; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);

        }

        service.updatePropertyVault(propertyVaultNode, propertyVault);

        verify(nodeService, times(counts.length - 1)).updateProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), any(Integer.class));

        verify(nodeService, times(2)).removeNodeProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix()), eq(Boolean.FALSE));
        verify(nodeService, times(2)).removeNodeProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix()), eq(Boolean.FALSE));
        verify(nodeService).renameNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + (DEFAULT_EMPTIED_COUNTS_FOR_VALUES.length - 1),
                PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + 1, false);
        verify(nodeService).renameNodeProperty(propertyVaultNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + (DEFAULT_EMPTIED_COUNTS_FOR_VALUES.length - 1),
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + 1, false);
    }

    @Test
    public void testCheckResultOfLoadStatisticsVault() throws Exception {
        doReturn(vault).when(service).loadStatisticsVault(statNode);

        StatisticsVault result = service.loadStatisticsVault(statNode);

        assertNotNull("statistics cannot be null", result);
    }

    @Test
    public void testCheckServiceActivityOnLoadStatistics() throws Exception {
        doReturn(vault).when(service).loadStatisticsVault(statNode);
        doReturn(statNode).when(service).getStatisticsNode(rootNode);

        StatisticsVault result = service.loadStatistics(rootNode);

        assertEquals("Unexpected statistics", vault, result);

        verify(service).loadStatisticsVault(statNode);
        verify(service).getStatisticsNode(rootNode);
    }

    @Test
    public void testCheckServiceActivityOnLoadStatisticsVault() throws Exception {
        when(nodeTypeVault.getNodeType()).thenReturn(TestNodeType.TEST1);
        when(nodeService.getChildren(statNode, PropertyStatisticsNodeType.STATISTICS_VAULT)).thenReturn(
                Arrays.asList(getNodeMock(), getNodeMock()).iterator());
        doReturn(TEST_COUNT_VALUE).when(nodeService).getNodeProperty(statNode,
                PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), 0, false);
        doReturn(nodeTypeVault).when(service).loadNodeTypeVault(any(Node.class));

        vault = service.loadStatisticsVault(statNode);

        verify(nodeService).getNodeProperty(statNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), 0, false);
        verify(service, times(2)).loadNodeTypeVault(any(Node.class));

        assertEquals("Unexpected count of vault", TEST_COUNT_VALUE, vault.getCount());
    }

    @Test
    public void testCheckServiceActivityOnLoadNodeTypeVault() throws Exception {
        Node nodeTypeVaultNode = getNodeMock();

        when(nodeService.getChildren(nodeTypeVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT)).thenReturn(
                Arrays.asList(getNodeMock(), getNodeMock()).iterator());

        when(nodeService.getNodeName(nodeTypeVaultNode)).thenReturn(PropertyStatisticsNodeType.STATISTICS_VAULT.getId());
        when(nodeService.getNodeProperty(nodeTypeVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), null, true))
                .thenReturn(TEST_COUNT_VALUE);
        doReturn(PROPERTY_NAME).when(propertyVault).getPropertyName();
        doReturn(propertyVault).when(service).loadPropertyVault(any(Node.class));

        nodeTypeVault = service.loadNodeTypeVault(nodeTypeVaultNode);

        verify(nodeService).getNodeName(nodeTypeVaultNode);
        verify(nodeService).getNodeProperty(nodeTypeVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), null, true);
        verify(service, times(2)).loadPropertyVault(any(Node.class));
    }

    @Test
    public void testCheckServiceResultOnLoadNodeTypeVault() throws Exception {
        Node nodeTypeVaultNode = getNodeMock();

        when(nodeService.getChildren(nodeTypeVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT)).thenReturn(
                Arrays.asList(getNodeMock(), getNodeMock()).iterator());

        when(nodeService.getNodeName(nodeTypeVaultNode)).thenReturn(PropertyStatisticsNodeType.STATISTICS_VAULT.getId());
        when(nodeService.getNodeProperty(nodeTypeVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountProperty(), null, true))
                .thenReturn(TEST_COUNT_VALUE);
        doReturn(PROPERTY_NAME).when(propertyVault).getPropertyName();
        doReturn(propertyVault).when(service).loadPropertyVault(any(Node.class));

        NodeTypeVault result = service.loadNodeTypeVault(nodeTypeVaultNode);

        assertNotNull("result cannot be null", result);
        assertEquals("Unexpected node type", PropertyStatisticsNodeType.STATISTICS_VAULT, result.getNodeType());
        assertEquals("Unexpected count", TEST_COUNT_VALUE, result.getCount());
    }

    @Test
    public void testCheckServiceActivityOnLoadPropertyVault() throws Exception {
        Node propertyVaultNode = getNodeMock();

        when(nodeService.getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), null, true)).thenReturn(
                TEST_SIZE_VALUE);
        when(nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), null, true))
                .thenReturn("java.lang.Integer");

        for (int i = 0; i < TEST_SIZE_VALUE; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i, null,
                            true)).thenReturn(i);
        }

        service.loadPropertyVault(propertyVaultNode);

        verify(nodeService).getNodeName(propertyVaultNode);
        verify(nodeService).getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), null, true);
        verify(nodeService).getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), null, true);
        verify(nodeService, times(TEST_SIZE_VALUE)).getNodeProperty(eq(propertyVaultNode),
                contains(PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix()), eq(null), eq(true));

        for (int i = 0; i < TEST_SIZE_VALUE; i++) {
            verify(nodeService).getNodeProperty(eq(propertyVaultNode),
                    eq(PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i), eq(null), eq(true));
        }
    }

    @Test
    public void testCheckResultOnLoadPropertyVault() throws Exception {
        Node propertyVaultNode = getNodeMock();

        when(nodeService.getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), null, true)).thenReturn(
                TEST_SIZE_VALUE);
        when(nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getClassProperty(), null, true))
                .thenReturn("java.lang.Integer");

        for (int i = 0; i < TEST_SIZE_VALUE; i++) {
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getValuePrefix() + i, null,
                            true)).thenReturn(PROPERTY_NAME + i);
            when(
                    nodeService.getNodeProperty(propertyVaultNode, PROPERTY_STATISTICS_NODE_PROPERTIES.getCountPrefix() + i, null,
                            true)).thenReturn(i);
        }

        PropertyVault result = service.loadPropertyVault(propertyVaultNode);

        assertNotNull("loaded property vault cannot be null", result);
        assertEquals("unexpected class", "java.lang.Integer", result.getClassName());
        Map<Object, Integer> values = result.getValuesMap();

        assertEquals("unexpected size", TEST_SIZE_VALUE, values.size());

        for (int i = 0; i < values.size(); i++) {
            assertTrue("value should exist", values.containsKey(PROPERTY_NAME + i));
            assertEquals("unexpected count of value", i, values.get(PROPERTY_NAME + i));

        }
    }

    private Node initializeMockedPropertyVaultNode(final int size, final Integer... counts) throws ServiceException {
        Node propertyVaultNode = getNodeMock();
        Map<Object, Integer> values = getValuesMap(counts);

        when(propertyVault.getClassName()).thenReturn("some class");
        when(propertyVault.getValuesMap()).thenReturn(values);

        when(nodeService.getNodeProperty(propertyVaultNode, GENERAL_NODE_PROPERTIES.getSizeProperty(), 0, false)).thenReturn(size);

        return propertyVaultNode;
    }

    private Map<Object, Integer> getValuesMap(final Integer... counts) {
        Map<Object, Integer> result = new HashMap<Object, Integer>();

        int j = 0;
        for (Integer i : counts) {
            result.put(PROPERTY_NAME + j++, i);
        }

        return result;
    }

    private PropertyVault createPropertyVault(final boolean isChanged) {
        PropertyVault result = mock(PropertyVault.class);

        when(result.isChanged()).thenReturn(isChanged);

        return result;
    }

    private NodeTypeVault createNodeTypeVault(final boolean isChanged) {
        NodeTypeVault result = mock(NodeTypeVault.class);

        when(result.isChanged()).thenReturn(isChanged);

        return result;
    }
}
