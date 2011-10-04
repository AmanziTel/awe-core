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

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * Selection model
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class SelectionModel extends AbstractModel implements ISelectionModel {
    private static Logger LOGGER = Logger.getLogger(SelectionModel.class);
    
    static NewNetworkService networkService = NeoServiceFactory.getInstance().getNewNetworkService();
    
    private int selectedNodesCount = 0;
    
    private INetworkModel networkModel;
    
    /**
     * Creates SelectionModel from existing Node
     * 
     * @param rootSelectionList root node of SelectionList structure
     */
    public SelectionModel(Node rootSelectionList) throws DatabaseException {
        //check input parameters
        if (rootSelectionList == null) {
            LOGGER.error("Input RootSelectionList node is null");
            throw new IllegalArgumentException("Input RootSelectionList node is null");
        }
        
        this.rootNode = rootSelectionList;
        this.name = (String)rootSelectionList.getProperty(NewAbstractService.NAME);
        this.selectedNodesCount = (Integer)rootSelectionList.getProperty(NewNetworkService.SELECTED_NODES_COUNT);
        
        this.networkModel = new NetworkModel(networkService.getNetworkOfSelectionListRootNode(rootNode));
        
        
        LOGGER.info("Selection Model <" + name + "> created by existing node");
    }
    
    /**
     * Creates Selection Model by it's Name
     * 
     * @param networkModel parent Network node
     * @param selectionListName name of selection list 
     * @throws AWEException
     */
    public SelectionModel(INetworkModel networkModel, String selectionListName) throws AWEException {
        //check input parameters
        if (networkModel == null) {
            LOGGER.error("Input NetworkModel is null");
            throw new IllegalArgumentException("Input NetworkModel is null");
        }
        if ((selectionListName == null) || (selectionListName.equals(StringUtils.EMPTY))) {
            LOGGER.error("Input Selection List Name is null or empty");
            throw new IllegalArgumentException("Input Selection List Name is null or empty");
        }
        
        this.name = selectionListName;
        this.rootNode = networkService.findSelectionList(networkModel.getRootNode(), selectionListName);
        if (rootNode == null) {
            rootNode = networkService.createSelectionList(networkModel.getRootNode(), selectionListName);
        }
        
        this.networkModel = networkModel;
        
        LOGGER.info("Selection Model <" + name + "> created");
    }

    @Override
    public void linkToSector(String name) {
        
    }

    @Override
    public IDataElement getParentElement(IDataElement childElement) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getChildren(IDataElement parent) {
        return null;
    }

    @Override
    public Iterable<IDataElement> getAllElementsByType(INodeType elementType) {
        return null;
    }

    @Override
    public void finishUp() {
        
    }

    @Override
    public int getSelectedNodesCount() {
        return selectedNodesCount;
    }  

}
