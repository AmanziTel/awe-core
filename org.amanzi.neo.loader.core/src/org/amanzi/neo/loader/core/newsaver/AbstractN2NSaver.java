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
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractN2NSaver extends AbstractCSVSaver<NetworkModel> {
    protected static Logger LOGGER = Logger.getLogger(AbstractN2NSaver.class);
    protected INetworkModel networkModel;
    protected INodeToNodeRelationsModel n2nModel;
    protected final int MAX_TX_BEFORE_COMMIT = 1000;

    protected AbstractN2NSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data,
            GraphDatabaseService service) {
        super(service);
        initSynonyms();
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
    public AbstractN2NSaver() {
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        initSynonyms();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
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
            throw new RuntimeException(e);
        }
    }

    protected abstract INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException;

    protected abstract void initSynonyms();
}
