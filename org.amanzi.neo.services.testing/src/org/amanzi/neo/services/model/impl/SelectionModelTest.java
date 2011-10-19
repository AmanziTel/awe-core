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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.services.AbstractNeoServiceTest;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

/**
 * Tests for Selection Model
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class SelectionModelTest extends AbstractNeoServiceTest {
    
    private static final String SELECTION_LIST_NAME = "selection_list";
    
    private static final int DEFAULT_COUNT = 5;
    
    private static Index<Relationship> linkIndexes;

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
        
        linkIndexes = graphDatabaseService.index().forRelationships(SELECTION_LIST_NAME);
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
        new SelectionModel(getNetworkNode(), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateSelectionModelWithEmptyName() throws Exception {
        new SelectionModel(getNetworkNode(), StringUtils.EMPTY);
    }
    
    @Test
    public void checkConstructorFromNodeActions() throws Exception {
        NewNetworkService networkService = getNetworkService(true, false);
        SelectionModel.networkService = networkService;
        
        Node selectionModelNode = getSelectionModelNode();
        new SelectionModel(selectionModelNode);
        
        verify(selectionModelNode).getProperty(NewNetworkService.NAME);
        verify(selectionModelNode).getProperty(NewNetworkService.SELECTED_NODES_COUNT);
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
        NewNetworkService networkService = getNetworkService(true, false);
        SelectionModel.networkService = networkService;
        
        new SelectionModel(getNetworkNode(), SELECTION_LIST_NAME);
        
        verify(networkService).findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
        verify(networkService, never()).createSelectionList(any(Node.class), any(String.class));
    }
    
    @Test
    public void checkConstructorThatCreatesNewNode() throws Exception {
        NewNetworkService networkService = getNetworkService(false, false);
        SelectionModel.networkService = networkService;
        
        new SelectionModel(getNetworkNode(), SELECTION_LIST_NAME);
        
        verify(networkService).findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
        verify(networkService).createSelectionList(any(Node.class), eq(SELECTION_LIST_NAME));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToLinkWithoutDataElement() throws Exception {
        SelectionModel selectionModel = getSelectionModel();
        
        selectionModel.linkToSector(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void tryToLinkWithEmptyDataElement() throws Exception {
        SelectionModel selectionModel = getSelectionModel();
        DataElement dataElement = new DataElement(new HashMap<String, String>());
        
        
        selectionModel.linkToSector(dataElement);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void checkLinkActions() throws Exception {
        Node sectorNode = getSectorNode();
        DataElement element = new DataElement(sectorNode);
        
        NewNetworkService networkService = getNetworkService(true, false);
        SelectionModel.networkService = networkService;
        
        Node selectionRootNode = getSelectionModelNode();
        SelectionModel selectionModel = new SelectionModel(selectionRootNode);
        
        selectionModel.linkToSector(element);
        
        verify(networkService).createSelectionLink(eq(selectionRootNode), eq(sectorNode), any(linkIndexes.getClass()));
    }
    
    @Test
    public void checkCounterIncreased() throws Exception {
        SelectionModel selectionModel = getSelectionModel();
        
        int initialCount = selectionModel.getSelectedNodesCount();
        
        for (int i = 0; i < DEFAULT_COUNT; i++) {
            Node sectorNode = getSectorNode();
            DataElement element = new DataElement(sectorNode);
            
            selectionModel.linkToSector(element);
        }
        
        assertEquals("Incorret increasing of count", initialCount + DEFAULT_COUNT, selectionModel.getSelectedNodesCount());
    }
    
    @Test(expected = AWEException.class)
    public void checkUnderlyingException() throws Exception {
        NewNetworkService networkService = getNetworkService(true, true);
        SelectionModel.networkService = networkService;
        
        Node sector = getSectorNode();
        DataElement element = new DataElement(sector);
        
        getSelectionModel().linkToSector(element);
    }
    
    @Test
    public void checkCounterNotIncreased() throws Exception {
        NewNetworkService networkService = getNetworkService(true, true);
        SelectionModel.networkService = networkService;
        
        Node sector = getSectorNode();
        DataElement element = new DataElement(sector);
        
        SelectionModel selectionModel = getSelectionModel();
        
        int prevCounter = selectionModel.getSelectedNodesCount();
        
        try {
            selectionModel.linkToSector(element);
        } catch (Exception e) {
            //do nothing
        }
        
        assertEquals("Counter should not increase in case of exception", prevCounter, selectionModel.getSelectedNodesCount());
    }
    
    /**
     * Creates Mockes NetworkService
     * 
     * @param shouldFind should SelectionNode exists in DB or not
     * @return
     */
    @SuppressWarnings("unchecked")
    private NewNetworkService getNetworkService(boolean shouldFind, boolean shouldThrow) throws AWEException {
        NewNetworkService service = mock(NewNetworkService.class);
        
        Node selectionNode = null;
        if (shouldFind) {
            selectionNode = getSelectionModelNode();
        } else {
            Node createdNode = getSelectionModelNode();
            when(service.createSelectionList(any(Node.class), eq(SELECTION_LIST_NAME))).thenReturn(createdNode);
        }
        when(service.findSelectionList(any(Node.class), eq(SELECTION_LIST_NAME))).thenReturn(selectionNode);
        
        if (shouldThrow) {
            doThrow(new DatabaseException("lalala")).when(service).createSelectionLink(any(Node.class), any(Node.class), any(linkIndexes.getClass()));
        }
        
        return service;
    }
    
    /**
     * Returns Mock for Network Node
     *
     * @return
     */
    private Node getNetworkNode() {
        Node result = mock(Node.class);
        
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
    
    /**
     * Returns Selection Model
     *
     * @return
     */
    private SelectionModel getSelectionModel() {
        return new SelectionModel(getSelectionModelNode());
    }
    
    /**
     * Returns mocked node for Sector
     *
     * @return
     */
    private Node getSectorNode() {
        Node result = mock(Node.class);
        
        return result;
    }

}
