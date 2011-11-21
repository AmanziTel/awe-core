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

package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.log4j.Logger;

/**
 * @author Kondratneko_Vladislav
 */
public class NewNeighboursSaver extends AbstractCSVSaver<NetworkModel> {
    private Long lineCounter = 0l;
    private INetworkModel networkModel;
    private INodeToNodeRelationsModel n2nModel;
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    /*
     * neighbours
     */
    public final static String NEIGHBOUR_SECTOR_CI = "neigh_sector_ci";
    public final static String NEIGHBOUR_SECTOR_LAC = "neigh_sector_lac";
    public final static String NEIGHBOUR_SECTOR_NAME = "neigh_sector_name";
    public final static String SERVING_SECTOR_CI = "serv_sector_ci";
    public final static String SERVING_SECTOR_LAC = "serv_sector_lac";
    public final static String SERVING_SECTOR_NAME = "serv_sector_name";
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaver.class);

    protected NewNeighboursSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data) {
        preferenceStoreSynonyms = preferenceManager.getNeighbourSynonyms();
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        if (model != null) {
            n2nModel = model;
            if (networkModel == null) {
                try {
                    this.networkModel = getActiveProject().getNetwork(
                            data.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
                    networkModel = this.networkModel;
                } catch (AWEException e) {
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
            } else {
                this.networkModel = networkModel;
            }
        } else {
            init(data, null);
        }

    }

    /**
     * 
     */
    public NewNeighboursSaver() {
        super();
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getNeighbourSynonyms();
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(NewAbstractService.NAME,
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            networkModel = getActiveProject().getNetwork(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            n2nModel = networkModel.getNodeToNodeModel(N2NRelTypes.NEIGHBOUR, configuration.getFilesToLoad().get(0).getName(),
                    NetworkElementNodeType.SECTOR);
            modelMap.put(networkModel.getName(), networkModel);
            modelMap.put(n2nModel.getName(), n2nModel);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                createNeighbour(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

    /**
     * try create a neighbour relationship between sectors
     * 
     * @param value
     * @throws DatabaseException
     */
    private void createNeighbour(List<String> row) throws AWEException {
        String neighbSectorName = getValueFromRow(NEIGHBOUR_SECTOR_NAME, row);
        String serviceNeighName = getValueFromRow(SERVING_SECTOR_NAME, row);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        properties.put(NewAbstractService.NAME, neighbSectorName);
        IDataElement findedNeighSector = networkModel.findElement(properties);
        properties.put(NewAbstractService.NAME, serviceNeighName);
        IDataElement findedServiceSector = networkModel.findElement(properties);
        for (String head : headers) {
            if (!fileSynonyms.containsValue(head)) {
                properties.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
            }
        }
        if (findedNeighSector != null && findedServiceSector != null) {
            n2nModel.linkNode(findedServiceSector, findedNeighSector, properties);
        } else {
            LOGGER.warn("cann't find service or neighbour sector on line " + lineCounter);
        }
    }

}
