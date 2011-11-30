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

package org.amanzi.neo.loader.core.saver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.log4j.Logger;

/**
 * saver for traffic data
 * 
 * @author Vladislav_Kondratenko
 */
public class TrafficSaver extends AbstractNetworkSaver {
    private static final Logger LOGGER = Logger.getLogger(TrafficSaver.class);
    /*
     * constants
     */
    private static final String SECTOR = "sector";
    private static final String TRAFFIC = "traffic";
    /**
     * sector element properties collection
     */
    private static Map<String, Object> SECTOR_MAP = new HashMap<String, Object>();

    /**
     * constructor for tests
     * 
     * @param model
     * @param config
     */
    TrafficSaver(INetworkModel model, ConfigurationDataImpl config) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            useableModels.add(model);
        }
    }

    /**
     * create saver instance
     */
    public TrafficSaver() {
        super();
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        if (!isCorrect(SECTOR, value)) {
            LOGGER.error("cant find sector column on line: " + lineCounter);
            return;
        }
        SECTOR_MAP.clear();
        collectSector(value);
        IDataElement findedSector = parametrizedModel.findElement(SECTOR_MAP);
        if (findedSector == null) {
            LOGGER.error("cann't find sector " + SECTOR_MAP);
            return;
        }
        if (isCorrect(TRAFFIC, value)) {
            SECTOR_MAP.put(TRAFFIC, getSynonymValueWithAutoparse(TRAFFIC, value));
            parametrizedModel.completeProperties(findedSector, SECTOR_MAP, true);
            addSynonyms(parametrizedModel, SECTOR_MAP);
        } else {
            LOGGER.error("traffic property not found on line:" + lineCounter);
        }
    }

    /**
     * collect sector to find
     */
    private void collectSector(List<String> value) {
        SECTOR_MAP.put(AbstractService.NAME, getSynonymValueWithAutoparse(SECTOR, value));
        SECTOR_MAP.put(AbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
    }
}
