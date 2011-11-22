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

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public class TrafficSaver extends AbstractCSVSaver<NetworkModel> {
    private static Logger LOGGER = Logger.getLogger(TrafficSaver.class);
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    INetworkModel model;

    protected TrafficSaver(INetworkModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.model = model;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    /**
     * 
     */
    public TrafficSaver() {
        super();
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            model = getActiveProject().getNetwork(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    protected void saveLine(List<String> value) throws AWEException {
    }

}
