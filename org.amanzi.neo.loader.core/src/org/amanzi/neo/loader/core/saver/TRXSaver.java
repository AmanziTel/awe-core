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
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NetworkService.NetworkRelationshipTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * 
 * TODO Purpose of 
 * <p>
 *  Saver for trx data
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class TRXSaver extends AbstractNetworkSaver<INetworkModel, ConfigurationDataImpl> {

    /*
     * Name of Dataset Synonyms
     */
    private static final String SYNONYMS_DATASET_TYPE = "network";
    
    /*
     * Map for collect elements
     */
    private Map<String, Object> trxMap = new HashMap<String, Object>();
    private Map<String, Object> frequencyMap = new HashMap<String, Object>();
    
    /*
     * params for collect elements
     */
    private static final String TRXID = "trx_id";
    private static final String SUBCELL = "subcell";
    private static final String BAND = "band";
    private static final String EXTENDED = "extended";
    private static final String HOPPING_TYPE = "hopping_type";
    private static final String IS_BCCH = "isBcch";
    private static final String HSN = "hsn";
    private static final String MAIO = "maio";
    private static final String ARFCN = "arfcn";
    private static final int ARFCN_ARRAY_SIZE = 63;
    /**
     * calculated arfcn
     */
    private Integer[] arfcnArray;
    
    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        Map<String, Object> values = getDataElementProperties(getMainModel(), null, dataElement, true);

        IDataElement trxSector = getNetworkElement(getSectorNodeType(), "sector_name", values);
        
        Iterable<IDataElement> sectorsTrx = networkModel.getChildren(trxSector);
        IDataElement findedTrx = null;
        String trxId = values.get("trx_id").toString();
        for (IDataElement trx : sectorsTrx) {
            if (trx.get(TRXID).equals(trxId)) {
                findedTrx = trx;
            }
        }
        collectTRXElement(values);
        if (findedTrx == null) {
            findedTrx = networkModel.createElement(trxSector, trxMap);
        } else {
            networkModel.completeProperties(findedTrx, trxMap, true);
        }
        createOrUpdateFrequencyElement(findedTrx, values);
    }
    
    /**
     * find or create frequecny element for required trx and update relationship between them;
     * 
     * @param findedTrx
     * @throws AWEException
     */
    private void createOrUpdateFrequencyElement(IDataElement findedTrx,Map<String, Object> values) throws AWEException {
        Iterable<IDataElement> frequencyElements = networkModel
                .getRelatedNodes(findedTrx, NetworkRelationshipTypes.ENTRY_PLAN);
        Iterator<IDataElement> frequencyElement = null;
        if (frequencyElements != null) {
            frequencyElement = frequencyElements.iterator();
        }
        collectFrequencyElement(values);
        if (frequencyElement != null && !frequencyElement.hasNext()) {
            networkModel.createElement(findedTrx, frequencyMap, NetworkRelationshipTypes.ENTRY_PLAN);
        } else {
            networkModel.completeProperties(frequencyElement.next(), frequencyMap, true);
        }
    }

    
    private void collectFrequencyElement(Map<String,Object> values){
        frequencyMap.put(HSN, values.get("hsn"));
        frequencyMap.put(MAIO, values.get("maio"));
        Integer arfcn = null;
        for (int i = 1; i < 64; i++) {
            arfcn = (Integer)values.get("arfcn"+i);
            if (arfcn != null) {
                arfcnArray[i-1] = arfcn;
            }
        }
        frequencyMap.put(ARFCN, arfcnArray);
    }
    
    private void collectTRXElement(Map<String,Object> values){
        trxMap = new HashMap<String, Object>();
        trxMap.put(SUBCELL, values.get("subcell"));
        trxMap.put(TRXID, values.get("trx_id"));
        trxMap.put(BAND, values.get("band"));
        trxMap.put(EXTENDED, values.get("extended"));
        trxMap.put(IS_BCCH, values.get("bcch"));
        trxMap.put(HOPPING_TYPE, values.get("hopping_type"));
        trxMap.put(AbstractService.NAME, values.get("trx_id").toString());
        trxMap.put(AbstractService.TYPE, NetworkElementNodeType.TRX.getId());
    }
    
    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INetworkModel createMainModel(ConfigurationDataImpl configuration) throws AWEException {
        networkModel = getActiveProject().getNetwork(
                configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
        return networkModel;
    }

    
    @Override
    protected String getDatasetType() {
        return SYNONYMS_DATASET_TYPE;
    }

    @Override
    protected String getSubType() {
        return NetworkElementNodeType.TRX.getId();
    }
}
