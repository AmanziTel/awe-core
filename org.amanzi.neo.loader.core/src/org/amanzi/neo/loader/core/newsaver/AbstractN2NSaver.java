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
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

//TODO: LN: comments
/**
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractN2NSaver extends AbstractCSVSaver<NetworkModel> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractN2NSaver.class);
    // TODO: LN: comments
    protected INetworkModel networkModel;
    protected INodeToNodeRelationsModel n2nModel;

    protected AbstractN2NSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data,
            GraphDatabaseService service) {
        super(service);
        initSynonyms();
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

    // TODO: LN: comments
    /**
     * 
     */
    public AbstractN2NSaver() {
    }

    /**
     * try create a neighbour relationship between sectors
     * 
     * @param value
     * @throws DatabaseException
     */
    @Override
    protected void saveLine(List<String> row) throws AWEException {
        String neighbSectorName = getValueFromRow(getNeighborElementName(), row);
        String serviceNeighName = getValueFromRow(getSourceElementName(), row);
        
        Map<String, Object> properties = new HashMap<String, Object>();
        
        properties.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        properties.put(NewAbstractService.NAME, neighbSectorName);
        //TODO: LN: why not use networkModel.findElementByPropertyValue? 
        //creation of map takes much more memory and time
        IDataElement findedNeighSector = networkModel.findElement(properties);
        
        properties.put(NewAbstractService.NAME, serviceNeighName);
        IDataElement findedServiceSector = networkModel.findElement(properties);
        
        for (String head : headers) {
            if (fileSynonyms.containsValue(head)) {
                properties.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
            }
        }
        if (findedNeighSector != null && findedServiceSector != null) {
            n2nModel.linkNode(findedServiceSector, findedNeighSector, properties);
        } else {
            LOGGER.warn("cann't find service or neighbour sector on line " + lineCounter);
        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        super.init(configuration, dataElement);
        Map<String, Object> rootElement = new HashMap<String, Object>();
        initSynonyms();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            rootElement.put(NewAbstractService.NAME,
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            networkModel = getActiveProject().getNetwork(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            n2nModel = getNode2NodeModel(configuration.getFilesToLoad().get(0).getName());
            modelMap.put(networkModel.getName(), networkModel);
            modelMap.put(n2nModel.getName(), n2nModel);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            //TODO: LN: do not throw RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * @return name of source element
     */
    protected abstract String getSourceElementName();

    /**
     * @return name of neighbor element
     */
    protected abstract String getNeighborElementName();

    //TODO: LN: comments
    protected abstract INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException;

    //TODO: LN: comments
    //TODO: LN: this method is never used 
    protected abstract void initSynonyms();
}
