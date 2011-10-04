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

package org.amanzi.neo.services.model.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * Tests for Selection Model
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class SelectionModelTest extends AbstractNeoServiceTest {
    
    private static final String SELECTION_LIST_NAME = "selection_list";
    
    private static final String NETWORK_NAME = "network_name";
    
    private static final int DEFAULT_COUNT = 5;

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        clearServices();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        stopDb();
        clearDb();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = IllegalArgumentException.class) 
    public void tryToCreateSelectionModelWithoutNode() throws Exception {
        new SelectionModel(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateSelectionModelWithoutNetworkNode() throws Exception {
        new SelectionModel(null, SELECTION_LIST_NAME);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateSelectionModelWithoutName() throws Exception {
        new SelectionModel(getNetworkModel(), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateSelectionModelWithEmptyName() throws Exception {
        new SelectionModel(getNetworkModel(), StringUtils.EMPTY);
    }
    
    @Test
    public void checkConstructorFromNodeActions() throws Exception {
        NewNetworkService networkService = getNetworkService(true);
        SelectionModel.networkService = networkService;
        
        Node selectionModelNode = getSelectionModelNode();
        new SelectionModel(selectionModelNode);
        
        verify(selectionModelNode).getProperty(NewNetworkService.NAME);
        verify(selectionModelNode).getProperty(NewNetworkService.SELECTED_NODES_COUNT);
        verify(networkService).getNetworkOfSelectionListRootNode(selectionModelNode);
    }
    
    @Test
    public void checkConstructorFromNodeResult() throws Exception {
        Node selectionModelNode = getSelectionModelNode();
        SelectionModel model = new SelectionModel(selectionModelNode);
        
        assertEquals("Unexpected name of Model", SELECTION_LIST_NAME, model.getName());
        assertEquals("Unexpected root Node of Model", selectionModelNode, model.getRootNode());
        assertEquals("Unexpected count of Model", DEFAULT_COUNT, model.getSelectedNodesCount());
    }
    
    @Test
    public void checkConstructorThatFindNodeInDatabase() throws Exception {
        NewNetworkService networkService = getNetworkService(true);
        SelectionModel.networkService = networkService;
        
        new SelectionModel(getNetworkModel(), SELECTION_LIST_NAME);
        
        verify(networkService).findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
        verify(networkService, never()).createSelectionList(any(Node.class), any(String.class));
    }
    
    @Test
    public void checkConstructoreThatCreatesNewNode() throws Exception {
        NewNetworkService networkService = getNetworkService(false);
        SelectionModel.networkService = networkService;
        
        new SelectionModel(getNetworkModel(), SELECTION_LIST_NAME);
        
        verify(networkService).findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
        verify(networkService).createSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
    }
    
    /**
     * Creates Mockes NetworkService
     * 
     * @param shouldFind should SelectionNode exists in DB or not
     * @return
     */
    private NewNetworkService getNetworkService(boolean shouldFind) throws AWEException {
        NewNetworkService service = mock(NewNetworkService.class);
        
        Node selectionNode = null;
        if (shouldFind) {
            selectionNode = getSelectionModelNode();
        } else {
            Node createdNode = getSelectionModelNode();
            when(service.createSelectionList(any(Node.class), eq(SELECTION_LIST_NAME))).thenReturn(createdNode);
        }
        when(service.findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME))).thenReturn(selectionNode);
        
        Node networkNode = getNetworkNode();
        when(service.getNetworkOfSelectionListRootNode(any(Node.class))).thenReturn(networkNode);
        
        return service;
    }
    
    /**
     * Returns Mock for Network Node
     *
     * @return
     */
    private INetworkModel getNetworkModel() {
        INetworkModel result = mock(INetworkModel.class);
        
        Node networkNode = getNetworkNode();
        when(result.getRootNode()).thenReturn(networkNode);
        
        return result;
    }
    
    private Node getNetworkNode() {
        Node result = mock(Node.class);
        
        String type = NetworkElementNodeType.NETWORK.getId();
        when(result.getProperty(NewAbstractService.TYPE, null)).thenReturn(type);
        when(result.getProperty(NewAbstractService.NAME, StringUtils.EMPTY)).thenReturn(NETWORK_NAME);
        
        return result;
    }
    
    /**
     * Returns Mock for already created Selection Model
     *
     * @return
     */
    private Node getSelectionModelNode() {
        Node result = mock(Node.class);
        
        when(result.getProperty(NewNetworkService.NAME)).thenReturn(SELECTION_LIST_NAME);
        when(result.getProperty(NewNetworkService.SELECTED_NODES_COUNT)).thenReturn(DEFAULT_COUNT);
        
        return result;
    }

}
