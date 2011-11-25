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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NewNetworkService.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.Band;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.log4j.Logger;

/**
 * saver for trx data
 * 
 * @author Vladislav_Kondratenko
 */
public class TRXSaver extends AbstractNetworkSaver {
    private static final Logger LOGGER = Logger.getLogger(TRXSaver.class);
    /*
     * constants
     */
    private static final String SECTOR = "sector";
    private static final String SUBCELL = "subcell";
    private static final String BAND = "band";
    private static final String TRXID = "trx_id";
    private static final String EXTENDED = "extended";
    private static final String HOPPING_TYPE = "hopping_type";
    private static final String IS_BCCH = "isBcch";
    private static final String HSN = "hsn";
    private static final String MAIO = "maio";
    private static final String ARFCN = "arfcn";
    private static final int ARFCN_ARRAY_SIZE = 63;

    private Integer[] arfcnArray;
    private IDataElement frequency_rootElement;

    /*
     * maps for collecting properties values;
     */
    private Map<String, Object> SECTOR_ELEMENT = new HashMap<String, Object>();
    private Map<String, Object> TRX_ELEMENT = new HashMap<String, Object>();
    private Map<String, Object> FREQUENCY_ELEMENT = new HashMap<String, Object>();

    protected TRXSaver(INetworkModel model, ConfigurationDataImpl config) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            parametrizedModel = model;
            useableModels.add(model);
        }
    }

    /**
     * create class instance
     */
    public TRXSaver() {
        super();
    }

    protected void saveLine(List<String> value) throws AWEException {
        if (frequency_rootElement == null) {
            Map<String, Object> frequency = new HashMap<String, Object>();
            frequency.put(NewAbstractService.NAME, parametrizedModel.getName());
            frequency.put(NewAbstractService.TYPE, NetworkElementNodeType.FREQUENCY_ROOT.getId());
            frequency_rootElement = parametrizedModel.createElement(null, FREQUENCY_ELEMENT,
                    NetworkRelationshipTypes.FREQUENCY_ROOT);
        }
        clearElementMaps();
        arfcnArray = new Integer[ARFCN_ARRAY_SIZE];
        String sectorName = (String)getSynonymValueWithAutoparse(SECTOR, value);
        collectSectorElement(sectorName);

        String subcell = (String)getSynonymValueWithAutoparse(SUBCELL, value);
        Integer trxId = (Integer)getSynonymValueWithAutoparse(TRXID, value);
        String band = getSynonymValueWithAutoparse(BAND, value).toString();
        String extended = null;

        if (band.equals(Band.BAND_900.getId())) {
            extended = (String)getSynonymValueWithAutoparse(EXTENDED, value);

        }
        Boolean isBcch = false;
        Integer hoppingType = (Integer)getSynonymValueWithAutoparse(HOPPING_TYPE, value);
        Object bcchByte = getSynonymValueWithAutoparse(IS_BCCH, value);
        if (bcchByte instanceof Integer) {
            if ((Integer)bcchByte == 1) {
                isBcch = true;
            }
        } else {
            isBcch = bcchByte.toString().equalsIgnoreCase(Boolean.TRUE.toString()) ? true : false;
        }

        String hsn = (String)getSynonymValueWithAutoparse(HSN, value);
        Integer maio = (Integer)getSynonymValueWithAutoparse(MAIO, value);

        Integer arfcn = null;
        for (int i = 0; i < 63; i++) {
            arfcn = (Integer)getSynonymValueWithAutoparse(ARFCN + " " + (i + 1), value);
            if (arfcn != null) {
                arfcnArray[i] = arfcn;
            }
        }
        collectFrequencyElement(hsn, maio, arfcnArray);
        collectTRXElement(subcell, trxId, band, extended, isBcch, hoppingType);
        removeEmpty(TRX_ELEMENT);
        removeEmpty(FREQUENCY_ELEMENT);
        createOrUpdateTRX();

    }

    /**
     * collect frequency element from required properties
     * 
     * @param hsn
     * @param maio
     * @param arfcnArray2
     */
    private void collectFrequencyElement(String hsn, Integer maio, Integer[] arfcnArray2) {
        FREQUENCY_ELEMENT.put(HSN, hsn);
        FREQUENCY_ELEMENT.put(MAIO, maio);
        FREQUENCY_ELEMENT.put(ARFCN, arfcnArray2);
    }

    /**
     * try to find trx and complete it with new properties i
     * 
     * @throws AWEException
     */
    private void createOrUpdateTRX() throws AWEException {
        IDataElement findedSector = parametrizedModel.findElement(SECTOR_ELEMENT);
        if (findedSector == null) {
            LOGGER.warn("Cann't find sector on line " + lineCounter);
        }
        Iterable<IDataElement> sectorsTrx = parametrizedModel.getChildren(findedSector);
        IDataElement findedTrx = null;
        for (IDataElement trx : sectorsTrx) {
            if (trx.get(TRXID).equals(TRX_ELEMENT.get(TRXID))) {
                findedTrx = trx;
            }
        }
        if (findedTrx == null) {
            findedTrx = parametrizedModel.createElement(findedSector, TRX_ELEMENT);
        } else {
            parametrizedModel.completeProperties(findedTrx, TRX_ELEMENT, true);
        }
        createOrUpdateFrequencyElement(findedTrx);
        addSynonyms(parametrizedModel, TRX_ELEMENT);
    }

    /**
     * find or create frequecny element for required trx and update relationship between them;
     * 
     * @param findedTrx
     * @throws AWEException
     */
    private void createOrUpdateFrequencyElement(IDataElement findedTrx) throws AWEException {
        Iterable<IDataElement> frequencyElements = parametrizedModel
                .getRelatedNodes(findedTrx, NetworkRelationshipTypes.ENTRY_PLAN);
        Iterator<IDataElement> frequencyElement = null;
        if (frequencyElements != null) {
            frequencyElement = frequencyElements.iterator();
        }
        if (frequencyElement != null && !frequencyElement.hasNext()) {
            parametrizedModel.createElement(findedTrx, FREQUENCY_ELEMENT, NetworkRelationshipTypes.ENTRY_PLAN);
        } else {
            parametrizedModel.completeProperties(frequencyElement.next(), FREQUENCY_ELEMENT, true);
        }
        addSynonyms(parametrizedModel, FREQUENCY_ELEMENT);
    }

    /**
     * remove empty or null values from params Map
     * 
     * @param params2
     */
    protected void removeEmpty(Map<String, Object> params) {
        List<String> keyToDelete = new LinkedList<String>();
        for (String key : params.keySet()) {
            if (!isCorrect(params.get(key))) {
                keyToDelete.add(key);
            }
        }
        for (String key : keyToDelete) {
            params.remove(key);
        }
    }

    /**
     * collect sector from required element
     * 
     * @param sectorName
     */
    private void collectSectorElement(String sectorName) {
        SECTOR_ELEMENT.put(NewAbstractService.NAME, sectorName);
        SECTOR_ELEMENT.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());

    }

    /**
     * collect trx element from required values;
     * 
     * @param subcell2
     * @param trxId2
     * @param band2
     * @param extended2
     * @param hsn2
     * @param isBcch
     * @param hoppingType
     * @param maio2
     * @param arfcn2
     */
    private void collectTRXElement(String subcell, Integer trxId, String band, String extended, boolean isBcch, Integer hoppingType) {
        TRX_ELEMENT.put(SUBCELL, subcell);
        TRX_ELEMENT.put(TRXID, trxId);
        TRX_ELEMENT.put(BAND, band);
        TRX_ELEMENT.put(EXTENDED, extended);
        TRX_ELEMENT.put(IS_BCCH, isBcch);
        TRX_ELEMENT.put(HOPPING_TYPE, hoppingType);
        TRX_ELEMENT.put(NewAbstractService.NAME, trxId.toString());
        TRX_ELEMENT.put(NewAbstractService.TYPE, NetworkElementNodeType.TRX.getId());
    }

    /**
     *
     */
    private void clearElementMaps() {
        SECTOR_ELEMENT.clear();
        TRX_ELEMENT.clear();
        FREQUENCY_ELEMENT.clear();
    }
}
