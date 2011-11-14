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

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

/**
 * Selection model
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class SelectionModel extends AbstractModel implements ISelectionModel {
    private static Logger LOGGER = Logger.getLogger(SelectionModel.class);

    /*
     * Key for Selection List indexes
     */
    private static final String SELECTION_LIST_INDEXES = "selection_list_links";

    static NewNetworkService networkService = NeoServiceFactory.getInstance().getNewNetworkService();

    private static Index<Relationship> selectionLinkIndex = null;

    private int selectedNodesCount = 0;

    /**
     * Creates SelectionModel from existing Node
     * 
     * @param rootSelectionList root node of SelectionList structure
     */
    public SelectionModel(Node rootSelectionList) {
        super(NetworkElementNodeType.SELECTION_LIST_ROOT);
        
        // check input parameters
        if (rootSelectionList == null) {
            LOGGER.error("Input RootSelectionList node is null");
            throw new IllegalArgumentException("Input RootSelectionList node is null");
        }

        this.rootNode = rootSelectionList;
        this.name = (String)rootSelectionList.getProperty(NewAbstractService.NAME);
        this.selectedNodesCount = (Integer)rootSelectionList.getProperty(NewNetworkService.SELECTED_NODES_COUNT);

        LOGGER.info("Selection Model <" + name + "> created by existing node");
    }

    /**
     * Creates Selection Model by it's Name
     * 
     * @param networkNode parent Network node
     * @param selectionListName name of selection list
     * @throws AWEException
     */
    public SelectionModel(Node networkNode, String selectionListName) throws AWEException {
        super(NetworkElementNodeType.SELECTION_LIST_ROOT);
        
        // check input parameters
        if (networkNode == null) {
            LOGGER.error("Input NetworkModel is null");
            throw new IllegalArgumentException("Input NetworkModel is null");
        }
        if ((selectionListName == null) || (selectionListName.equals(StringUtils.EMPTY))) {
            LOGGER.error("Input Selection List Name is null or empty");
            throw new IllegalArgumentException("Input Selection List Name is null or empty");
        }

        this.name = selectionListName;
        this.rootNode = networkService.findSelectionList(networkNode, selectionListName);
        if (rootNode == null) {
            rootNode = networkService.createSelectionList(networkNode, selectionListName);
        }

        LOGGER.info("Selection Model <" + name + "> created");
    }

    @Override
    public void linkToSector(IDataElement element) throws AWEException {
        LOGGER.debug("start linkToSector(<" + element + ">)");

        // check input
        if (element == null) {
            LOGGER.error("Input element is null");
            throw new IllegalArgumentException("Input element is null");
        }
        DataElement dataElement = (DataElement)element;
        if (dataElement.getNode() == null) {
            LOGGER.error("Underlying node in input Element is null");
            throw new IllegalArgumentException("Underlying node in input Element is null");
        }

        // create a link
        try {
            networkService.createSelectionLink(getRootNode(), dataElement.getNode(), getSelectionLinkIndexes());
            selectedNodesCount++;
        } catch (Exception e) {
            LOGGER.error("Error on creating link from SelectionList <" + this + "> to Element <" + element + ">.");
            throw new DatabaseException(e);
        }

        LOGGER.debug("finish linkToSector()");
    }

    @Override
    public boolean isExistSelectionLink(IDataElement element) {
        if (element == null) {
            LOGGER.error("IDataElement is null");
            throw new IllegalArgumentException("IDataElement is null");
        }
        DataElement dataElement = (DataElement)element;
        if (networkService.findSelectionLink(getRootNode(), dataElement.getNode(), getSelectionLinkIndexes()) != null) {
            return true;
        }
        return false;
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

    private Index<Relationship> getSelectionLinkIndexes() {
        if (selectionLinkIndex == null) {
            selectionLinkIndex = DatabaseManagerFactory.getDatabaseManager().
                    getDatabaseService().index().forRelationships(SELECTION_LIST_INDEXES);
        }

        return selectionLinkIndex;
    }

    @Override
    public IProjectModel getProject() {
        return null;
    }

    @Override
    public void deleteSelectionLink(IDataElement element) {
        LOGGER.debug("start deleteSelectionLink(<" + element + ">)");
        if (element == null) {
            LOGGER.error("Input element is null");
            throw new IllegalArgumentException("Input element is null");
        }
        DataElement dataElement = (DataElement)element;
        if (dataElement.getNode() == null) {
            LOGGER.error("Underlying node in input Element is null");
            throw new IllegalArgumentException("Underlying node in input Element is null");
        }
        try {
            networkService.deleteSelectionLink(getRootNode(), dataElement.getNode(), getSelectionLinkIndexes());
        } catch (AWEException e) {
            LOGGER.error("Error on deleting link from SelectionList <" + this + "> to Element <" + element + ">.");
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        LOGGER.debug("finish deleteSelectionLink()");
    }

}
