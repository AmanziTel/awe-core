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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeoNodeProperties;
import org.amanzi.neo.nodeproperties.impl.NetworkNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import com.vividsolutions.jts.geom.Envelope;

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

    private static final INetworkElementType DEFAULT_ELEMENT_TYPE = NetworkElementType.BSC;

    private static final INetworkNodeProperties NETWORK_NODE_PROPERTIES = new NetworkNodeProperties();

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
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        networkModel.createElement(DEFAULT_ELEMENT_TYPE, parentElement, DEFAULT_ELEMENT_NAME, properties);

        verify(nodeService).createNode(parentNode, DEFAULT_ELEMENT_TYPE, NodeServiceRelationshipType.CHILD, DEFAULT_ELEMENT_NAME,
                properties);
    }

    @Test
    public void testCheckCreateSiteSuccess() throws ModelException {
        parentElement = new DataElement(getNodeMock());

        Map<String, Object> properties = getProperties();
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        DataElement mockedSite = new DataElement(getNodeMock(properties));

        doReturn(mockedSite).when(networkModel).createDefaultElement(NetworkElementType.SITE, parentElement, DEFAULT_ELEMENT_NAME,
                properties);

        networkModel.createSite(parentElement, DEFAULT_ELEMENT_NAME, Double.MAX_VALUE, Double.MIN_VALUE, properties);
        verify(indexModel).indexInMultiProperty(NetworkElementType.SITE, mockedSite.getNode(), Double.class,
                GEO_NODE_PROPERTIES.getLatitideProperty(), GEO_NODE_PROPERTIES.getLongitudeProperty());
        verify(networkModel).createDefaultElement(NetworkElementType.SITE, parentElement, DEFAULT_ELEMENT_NAME, properties);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreateSiteIfLatIsNull() throws ModelException {
        parentElement = new DataElement(getNodeMock());

        Map<String, Object> properties = getProperties();
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        networkModel.createSite(parentElement, DEFAULT_ELEMENT_NAME, null, Double.MIN_NORMAL, properties);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreateSiteIfLonIsNull() throws ModelException {
        parentElement = new DataElement(getNodeMock());
        Map<String, Object> properties = getProperties();
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        networkModel.createSite(parentElement, DEFAULT_ELEMENT_NAME, Double.MIN_NORMAL, null, properties);
    }

    @Test
    public void testCheckCreateSectorSuccess() throws ModelException {
        parentElement = new DataElement(getNodeMock());

        Map<String, Object> properties = getProperties();
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        DataElement mockedSector = new DataElement(getNodeMock(properties));

        doReturn(mockedSector).when(networkModel).createDefaultElement(NetworkElementType.SECTOR, parentElement,
                DEFAULT_ELEMENT_NAME, properties);

        networkModel.createSector(parentElement, DEFAULT_ELEMENT_NAME, Integer.MAX_VALUE, Integer.MIN_VALUE, properties);
        verify(networkModel).createDefaultElement(NetworkElementType.SECTOR, parentElement, DEFAULT_ELEMENT_NAME, properties);
        verify(indexModel).index(NetworkElementType.SECTOR, mockedSector.getNode(), NETWORK_NODE_PROPERTIES.getLACProperty(),
                Integer.MAX_VALUE);
        verify(indexModel).index(NetworkElementType.SECTOR, mockedSector.getNode(), NETWORK_NODE_PROPERTIES.getCIProperty(),
                Integer.MIN_VALUE);
    }

    @Test
    public void testCheckCreateElementIfSectorDefinedSuccess() throws ModelException {

        Map<String, Object> properties = getProperties();
        properties.put(NETWORK_NODE_PROPERTIES.getCIProperty(), Integer.MIN_VALUE);
        properties.put(NETWORK_NODE_PROPERTIES.getLACProperty(), Integer.MAX_VALUE);
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        DataElement mockedSector = new DataElement(getNodeMock(properties));

        doReturn(mockedSector).when(networkModel).createDefaultElement(NetworkElementType.SECTOR, parentElement,
                DEFAULT_ELEMENT_NAME, properties);

        networkModel.createElement(NetworkElementType.SECTOR, parentElement, DEFAULT_ELEMENT_NAME, properties);
        verify(networkModel).createSector(parentElement, DEFAULT_ELEMENT_NAME,
                (Integer)properties.get(NETWORK_NODE_PROPERTIES.getLACProperty()),
                (Integer)properties.get(NETWORK_NODE_PROPERTIES.getCIProperty()), properties);

    }

    @Test
    public void testCheckCreateElementIfSiteDefinedSuccess() throws ModelException {
        parentElement = new DataElement(getNodeMock());

        Map<String, Object> properties = getProperties();
        properties.put(GEO_NODE_PROPERTIES.getLatitideProperty(), Double.MIN_VALUE);
        properties.put(GEO_NODE_PROPERTIES.getLongitudeProperty(), Double.MAX_VALUE);
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), DEFAULT_ELEMENT_NAME);

        DataElement mockedSite = new DataElement(getNodeMock(properties));

        doReturn(mockedSite).when(networkModel).createDefaultElement(NetworkElementType.SITE, parentElement, DEFAULT_ELEMENT_NAME,
                properties);

        networkModel.createElement(NetworkElementType.SITE, parentElement, DEFAULT_ELEMENT_NAME, properties);
        verify(networkModel).createSite(parentElement, DEFAULT_ELEMENT_NAME,
                (Double)properties.get(GEO_NODE_PROPERTIES.getLatitideProperty()),
                (Double)properties.get(GEO_NODE_PROPERTIES.getLongitudeProperty()), properties);

    }

    @Test(expected = FatalException.class)
    public void testCheckCreateDefaultElementIfExceptionWasThrowsn() throws ModelException, ServiceException {
        Map<String, Object> properties = getProperties();
        DatabaseException exception = new DatabaseException(new Exception());

        when(
                nodeService.createNode(parentNode, NetworkElementType.SECTOR, NodeServiceRelationshipType.CHILD,
                        DEFAULT_ELEMENT_NAME, properties)).thenThrow(exception);

        networkModel.createDefaultElement(NetworkElementType.SECTOR, parentElement, DEFAULT_ELEMENT_NAME, getProperties());
    }

    @Test
    public void testCheckFindSectorIfNameIsEmpty() throws ModelException {
        Map<String, Object> properties = getProperties();
        properties.put(NETWORK_NODE_PROPERTIES.getCIProperty(), Integer.MIN_VALUE);
        properties.put(NETWORK_NODE_PROPERTIES.getLACProperty(), Integer.MAX_VALUE);
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), StringUtils.EMPTY);

        DataElement mockedSector = new DataElement(getNodeMock(properties));
        List<Node> iterableSectors = new ArrayList<Node>();
        iterableSectors.add(mockedSector.getNode());
        doReturn(iterableSectors.iterator()).when(indexModel).getNodes(NetworkElementType.SECTOR,
                NETWORK_NODE_PROPERTIES.getLACProperty(), Integer.MAX_VALUE);
        doReturn(iterableSectors.iterator()).when(indexModel).getNodes(NetworkElementType.SECTOR,
                NETWORK_NODE_PROPERTIES.getCIProperty(), Integer.MIN_VALUE);
        DataElement sector = (DataElement)networkModel.findSector(StringUtils.EMPTY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals("Expected equals valus", mockedSector.getNode(), sector.getNode());
    }

    @Test
    public void testCheckFindSectorIfNameIsEmptyAndLacIsNull() throws ModelException {
        Map<String, Object> properties = getProperties();
        properties.put(NETWORK_NODE_PROPERTIES.getCIProperty(), Integer.MIN_VALUE);
        properties.put(NETWORK_NODE_PROPERTIES.getLACProperty(), Integer.MAX_VALUE);
        properties.put(GENERAL_NODE_PROPERTIES.getNodeNameProperty(), StringUtils.EMPTY);

        DataElement mockedSector = new DataElement(getNodeMock(properties));
        List<Node> iterableSectors = new ArrayList<Node>();
        iterableSectors.add(mockedSector.getNode());
        doReturn(iterableSectors.iterator()).when(indexModel).getNodes(NetworkElementType.SECTOR,
                NETWORK_NODE_PROPERTIES.getCIProperty(), Integer.MIN_VALUE);
        DataElement sector = (DataElement)networkModel.findSector(StringUtils.EMPTY, Integer.MIN_VALUE, null);
        assertEquals("Expected equals valus", mockedSector.getNode(), sector.getNode());
    }

    @Test
    public void testCheckFindSectorIfNameNotEmpty() throws ModelException {
        DataElement mockedSector = new DataElement(getNodeMock());
        doReturn(mockedSector).when(networkModel).findElement(NetworkElementType.SECTOR, DEFAULT_ELEMENT_NAME);
        DataElement sector = (DataElement)networkModel.findSector(DEFAULT_ELEMENT_NAME, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertEquals("Expected equals valus", mockedSector.getNode(), sector.getNode());

    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindSectorIfNameIsEmptyAndCiAndLacNull() throws ModelException {
        networkModel.findSector(StringUtils.EMPTY, null, null);

    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindSectorIfNameIsEmptyAndCiNull() throws ModelException {
        networkModel.findSector(StringUtils.EMPTY, null, Integer.MAX_VALUE);

    }

    @Test
    public void testCheckGetElement() throws ModelException {
        Envelope env = new Envelope(Double.MIN_EXPONENT, Double.MAX_EXPONENT, Double.MIN_NORMAL, Double.MAX_VALUE);
        List<Node> nodes = new ArrayList<Node>();
        Map<String, Object> properties = getProperties();
        properties.put(GEO_NODE_PROPERTIES.getLatitideProperty(), Double.MIN_VALUE);
        properties.put(GEO_NODE_PROPERTIES.getLongitudeProperty(), Double.MAX_VALUE);
        Node mockedNode = getNodeMock(properties);
        nodes.add(mockedNode);
        Double[] min = new Double[] {env.getMinY(), env.getMinX()};
        Double[] max = new Double[] {env.getMaxY(), env.getMaxX()};
        doReturn(nodes.iterator()).when(indexModel).getNodes(NetworkElementType.SITE, Double.class, min, max,
                GEO_NODE_PROPERTIES.getLatitideProperty(), GEO_NODE_PROPERTIES.getLongitudeProperty());

    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("property1", "value1");
        result.put("property2", "value2");

        return result;
    }
}
