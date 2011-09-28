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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.IDataElement;
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
public class SelectionModel implements ISelectionModel {
    private static Logger LOGGER = Logger.getLogger(SelectionModel.class);
    private static NewNetworkService networkServ = NeoServiceFactory.getInstance().getNewNetworkService();
    private static NewDatasetService datasetServ = NeoServiceFactory.getInstance().getNewDatasetService();
    private Node networkRootNode;
    private Node selectionRootNode;
    private NetworkModel networkModel;

    /**
     * should to get DataElement with network Name also should get ProjectNode
     * 
     * @param project node
     * @param rootElement
     * @throws DuplicateNodeNameException
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     */
    public SelectionModel(IDataElement dataElement) throws InvalidDatasetParameterException, DatasetTypeParameterException,
            DuplicateNodeNameException {

        Node projectNode;
        projectNode = (Node)dataElement.get("project");

        networkModel = new NetworkModel(datasetServ.findDataset(projectNode, dataElement.get(INeoConstants.PROPERTY_NAME_NAME)
                .toString(), DatasetTypes.NETWORK));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(INeoConstants.PROPERTY_NAME_NAME, " Slection Model");
        params.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.SELECTION_LIST_ROOT.getId());
        try {
            selectionRootNode = networkServ.createNode(params);
            networkServ.createRelationship(networkModel.getRootNode(), selectionRootNode, NetworkRelationshipTypes.SELECTION);
        } catch (AWEException e) {
            LOGGER.error("could not create selection model", e);
            e.printStackTrace();
        }
    }

    /**
     * try to find sector by name. if find- return iterator of nodes else null
     */
    private IDataElement findElementByName(String name) throws AWEException {
        if (StringUtils.isEmpty(name) || name == null) {
            throw new IllegalArgumentException("Sector name is null.");
        }
        Map<String, Object> sectorElement = new HashMap<String, Object>();
        sectorElement.put(INeoConstants.PROPERTY_NAME_NAME, name);
        sectorElement.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.SECTOR.getId());
        return networkModel.findElement(new DataElement(sectorElement));
    }

    @Override
    public void linkToSector(String name) {

        IDataElement findedNodes;
        try {
            findedNodes = findElementByName(name);
        } catch (AWEException e) {
            LOGGER.error("Error while searching sector", e);
            return;
        }
        try {
            if (findedNodes == null) {
                LOGGER.error("There is no sector with name " + name);
                return;
            }
            networkServ.createRelationship(selectionRootNode, ((DataElement)findedNodes).getNode(),
                    NetworkRelationshipTypes.SELECTED);
            LOGGER.info("Linking compleate for sector " + name);
        } catch (AWEException e) {
            e.printStackTrace();
            LOGGER.error("Cann't make relation between selection model and sector", e);
        }
    }

    /**
     * @return Returns the selectionRootNode.
     */
    public Node getRoot() {
        return selectionRootNode;
    }

}
