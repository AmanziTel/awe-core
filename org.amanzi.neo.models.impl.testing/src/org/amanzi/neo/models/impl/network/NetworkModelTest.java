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

package org.amanzi.neo.models.impl.network;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeoNodeProperties;
import org.amanzi.neo.nodeproperties.impl.NetworkNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
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
public class NetworkModelTest extends AbstractMockitoTest {

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = new GeneralNodeProperties();

    private static final IGeoNodeProperties GEO_NODE_PROPERTIES = new GeoNodeProperties();

    private static final String DEFAULT_ELEMENT_NAME = "element name";

    private static final INetworkElementType DEFAULT_ELEMENT_TYPE = NetworkElementType.SITE;

    private INodeService nodeService;

    private IPropertyStatisticsModel statisticsModel;

    private IIndexModel indexModel;

    private NetworkModel networkModel;

    private DataElement parentElement;

    private Node elementNode;

    private Node parentNode;

    private Node rootNode;

    @Before
    public void setUp() {
        nodeService = mock(INodeService.class);
        statisticsModel = mock(IPropertyStatisticsModel.class);
        indexModel = mock(IIndexModel.class);

        rootNode = getNodeMock();

        networkModel = spy(new NetworkModel(nodeService, GENERAL_NODE_PROPERTIES, GEO_NODE_PROPERTIES, new NetworkNodeProperties()));
        networkModel.setIndexModel(indexModel);
        networkModel.setPropertyStatisticsModel(statisticsModel);
        doReturn(rootNode).when(networkModel).getRootNode();

        parentNode = getNodeMock();
        elementNode = getNodeMock();

        parentElement = mock(DataElement.class);
        when(parentElement.getNode()).thenReturn(parentNode);
    }

    @Test
    public void testCheckActivityOnFindElementExistingInIndex() throws Exception {
        when(indexModel.getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME))
                .thenReturn(elementNode);

        networkModel.findElement(DEFAULT_ELEMENT_TYPE, DEFAULT_ELEMENT_NAME);

        verify(indexModel).getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);
        verifyNoMoreInteractions(nodeService);
    }

    @Test
    public void testCheckActivityOnFindElementNotExistingInIndex() throws Exception {
        when(indexModel.getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME))
                .thenReturn(null);

        networkModel.findElement(DEFAULT_ELEMENT_TYPE, DEFAULT_ELEMENT_NAME);

        verify(indexModel).getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);
        verifyNoMoreInteractions(nodeService);
    }

    @Test
    public void testCheckResultOnFindElementExistingInIndex() throws Exception {
        when(indexModel.getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME))
                .thenReturn(elementNode);

        IDataElement result = networkModel.findElement(DEFAULT_ELEMENT_TYPE, DEFAULT_ELEMENT_NAME);

        assertNotNull("result should not be null", result);
        assertEquals("Unexpected node", elementNode, ((DataElement)result).getNode());
    }

    @Test
    public void testCheckResultOnFindElementNotExistingInIndex() throws Exception {
        when(indexModel.getSingleNode(DEFAULT_ELEMENT_TYPE, GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME))
                .thenReturn(null);

        IDataElement result = networkModel.findElement(DEFAULT_ELEMENT_TYPE, DEFAULT_ELEMENT_NAME);

        assertNull("result should be null", result);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckExceptionOnEmtpyNameForFindElement() throws Exception {
        networkModel.findElement(DEFAULT_ELEMENT_TYPE, StringUtils.EMPTY);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckExceptionOnNullNameForFindElement() throws Exception {
        networkModel.findElement(DEFAULT_ELEMENT_TYPE, null);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckExceptionOnNullTypeForFindElement() throws Exception {
        networkModel.findElement(null, StringUtils.EMPTY);
    }

    @Test
    public void testCheckActivityOnCreatingNewElement() throws Exception {
        Map<String, Object> properties = getProperties();

        networkModel.createElement(DEFAULT_ELEMENT_TYPE, parentElement, DEFAULT_ELEMENT_NAME, properties);

        verify(networkModel).findElement(DEFAULT_ELEMENT_TYPE, DEFAULT_ELEMENT_NAME);
        verify(nodeService).createNode(parentNode, DEFAULT_ELEMENT_TYPE, NodeServiceRelationshipType.CHILD, DEFAULT_ELEMENT_NAME,
                properties);
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("property1", "value1");
        result.put("property2", "value2");

        return result;
    }
}
