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
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for traffic data
 * 
 * @author Vladislav_Kondratenko
 */
public class TrafficSaver extends AbstractCSVSaver<NetworkModel> {
    //TODO: LN: comments
    private static final Logger LOGGER = Logger.getLogger(TrafficSaver.class);
    private static final String SECTOR = "sector";
    private static final String TRAFFIC = "traffic";
    private static Map<String, Object> SECTOR_MAP = new HashMap<String, Object>();

    protected TrafficSaver(INetworkModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.networkModel = model;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    //TODO: LN: comments
    public TrafficSaver() {
        super();
    }

    protected void saveLine(List<String> value) throws AWEException {
        if (!isCorrect(SECTOR, value)) {
            LOGGER.error("cant find sector column on line: " + lineCounter);
            return;
        }
        SECTOR_MAP.clear();
        collectSector(value);
        //TODO: LN: use findElementByPropertyValue
        IDataElement findedSector = networkModel.findElement(SECTOR_MAP);
        if (findedSector == null) {
            LOGGER.error("cann't find sector " + SECTOR_MAP);
            return;
        }
        if (isCorrect(TRAFFIC, value)) {
            SECTOR_MAP.put(TRAFFIC, getSynonymValueWithAutoparse(TRAFFIC, value));
            networkModel.completeProperties(findedSector, SECTOR_MAP, true);
            addSynonyms(networkModel, SECTOR_MAP);
        } else {
            LOGGER.error("traffic property not found on line:" + lineCounter);
        }
    }

    /**
     * collect sector to find
     */
    private void collectSector(List<String> value) {
        SECTOR_MAP.put(NewAbstractService.NAME, getSynonymValueWithAutoparse(SECTOR, value));
        SECTOR_MAP.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
    }
}
