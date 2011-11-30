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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.log4j.Logger;

/**
 * saver for frequency constraint data
 * 
 * @author Vladislav_Kondratenko
 */
public class FrequencyConstraintSaver extends AbstractN2NSaver {

    /** String ALL_TRX_SYMBOL field */
    private static final String ALL_TRX_SYMBOL = "*";
    private static final Logger LOGGER = Logger.getLogger(FrequencyConstraintSaver.class);
    /*
     * FREQUENCY constraints
     */
    private static final String FR_TRX_ID = "trx_id";
    private static final String FR_CH_TYPE = "channel_type";
    private static final String FR_FREQUENCY = "frequency";
    private static final String FR_PENALTY = "penalty";
    private static final String FR_SCALLING_FACTOR = "scalling_factor";
    private static final String SECTOR = "sector";

    /*
     * collections of elements properties
     */
    private Map<String, Object> TRX_MAP = new HashMap<String, Object>();
    private Map<String, Object> RELATIONS_PROPERTIES = new HashMap<String, Object>();

    /**
     * create saver instance
     */
    public FrequencyConstraintSaver() {
        super();
    }

    /**
     * constructor for tests
     * 
     * @param model
     * @param networkModel
     * @param data
     */
    FrequencyConstraintSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data) {
        super(model, networkModel, data);
    }

    @Override
    protected void saveLine(List<String> row) throws AWEException {
        if (!isCorrect(SECTOR, row)) {
            LOGGER.error("Sector name not found on line: " + lineCounter);
            return;
        }
        clearTemporalyDataMaps();
        Object sectorName = getSynonymValueWithAutoparse(SECTOR, row);
        if (sectorName == null) {
            LOGGER.error("Incorrect sector name on line: " + lineCounter);
            return;
        }
        if (!isCorrect(FR_TRX_ID, row)) {
            LOGGER.error("TRX id  not found on line: " + lineCounter);
            return;
        }

        collectTrxMap(row);
        Object trxId = TRX_MAP.get(FR_TRX_ID);
        Set<IDataElement> findedSector = parametrizedModel.findElementByPropertyValue(NetworkElementNodeType.SECTOR,
                AbstractService.NAME, sectorName);
        if (findedSector.isEmpty()) {
            LOGGER.error("sector " + sectorName + " not found");
        }

        // link trx elements and frequency spectrum element
        List<IDataElement> listTRX = getRequiredTrxs(trxId, findedSector.iterator().next());
        if (listTRX.size() == 0) {
            LOGGER.info("There are no trx for sector " + sectorName);
            return;
        }
        for (IDataElement trx : listTRX) {
            IDataElement frNode = n2nModel.getFrequencyElement((Integer)TRX_MAP.get(FR_FREQUENCY));
            collectRelationsProperties(row);
            n2nModel.linkNode(trx, frNode, RELATIONS_PROPERTIES);
        }
    }

    /**
     * find required sectors trxs
     * 
     * @param trxId
     * @param findedSector
     * @return
     */
    private List<IDataElement> getRequiredTrxs(Object trxId, IDataElement findedSector) {
        Iterable<IDataElement> listTRX = parametrizedModel.getChildren(findedSector);
        List<IDataElement> requiredTrx = new LinkedList<IDataElement>();
        for (IDataElement trx : listTRX) {
            if (trxId.equals(ALL_TRX_SYMBOL) || trxId == trx.get(FR_TRX_ID)) {
                requiredTrx.add(trx);
            }
        }
        return requiredTrx;
    }

    /**
     * Clears temporary maps
     */
    private void clearTemporalyDataMaps() {
        TRX_MAP.clear();
        RELATIONS_PROPERTIES.clear();
    }

    /**
     * collect properties for trx element
     * 
     * @param row
     */
    private void collectTrxMap(List<String> row) {
        TRX_MAP.put(FR_FREQUENCY, getSynonymValueWithAutoparse(FR_FREQUENCY, row));
        TRX_MAP.put(FR_TRX_ID, getSynonymValueWithAutoparse(FR_TRX_ID, row));
    }

    /**
     * collect relationsProperties
     */
    private void collectRelationsProperties(List<String> row) {
        RELATIONS_PROPERTIES.put(FR_CH_TYPE, getSynonymValueWithAutoparse(FR_CH_TYPE, row));
        RELATIONS_PROPERTIES.put(FR_PENALTY, getSynonymValueWithAutoparse(FR_PENALTY, row));
        RELATIONS_PROPERTIES.put(FR_SCALLING_FACTOR, getSynonymValueWithAutoparse(FR_SCALLING_FACTOR, row));
    }

    @Override
    protected String getSourceElementName() {
        return null;
    }

    @Override
    protected String getNeighborElementName() {
        return null;
    }

    @Override
    protected N2NRelTypes getN2NType() {
        return N2NRelTypes.FREQUENCY_SPECTRUM;
    }

    @Override
    protected INodeType getN2NNodeType() {
        return NetworkElementNodeType.SECTOR;
    }

}
