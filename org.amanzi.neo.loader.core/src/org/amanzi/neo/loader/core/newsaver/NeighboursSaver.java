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
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Kondratneko_Vladislav
 */
public class NeighboursSaver extends AbstractSaver<NetworkModel, CSVContainer, ConfigurationDataImpl> {
    private Long lineCounter = 0l;
    private INetworkModel model;
    private INodeToNodeRelationsModel n2nModel;
    private IDataElement rootDataElement;
    private Map<String, Integer> columnSynonyms;
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
    /**
     * contains appropriation of header synonyms and name inDB</br> <b>key</b>- name in db ,
     * <b>value</b>-file header key
     */
    private Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    private List<String> headers;
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaver.class);
    private Map<String, String[]> preferenceStoreSynonyms;

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getNeighbourSynonyms();
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        openOrReopenTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            model = getActiveProject().getNetwork(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            rootDataElement = new DataElement(model.getRootNode());
//            n2nModel = new NodeToNodeRelationshipModel(rootDataElement, N2NRelTypes.NEIGHBOUR, configuration
//                    .getFilesToLoad().get(0).getName(),NetworkElementNodeType.SECTOR);
            modelMap.put(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK), model);
            createExportSynonymsForModels();
            markTxAsSuccess();
        } catch (AWEException e) {
            markTxAsSuccess();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        } finally {
            finishTx();
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        openOrReopenTx();
        CSVContainer container = dataElement;
        if (fileSynonyms.isEmpty()) {
            headers = container.getHeaders();
            makeAppropriationWithSynonyms(headers);
            makeIndexAppropriation();
            lineCounter++;
        } else {
            lineCounter++;
            List<String> value = container.getValues();
            // createMSC(value);
            markTxAsSuccess();
            increaseActionCount();
        }
    }

    private void makeIndexAppropriation() {
        for (String synonyms : fileSynonyms.keySet()) {
            columnSynonyms.put(fileSynonyms.get(synonyms), getHeaderId(fileSynonyms.get(synonyms)));
        }
        for (String head : headers) {
            if (!columnSynonyms.containsKey(head)) {
                columnSynonyms.put(head, getHeaderId(head));
            }
        }
    }

    private int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    private void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        String[] posibleHeadersArray = posibleHeader.split(DataLoadPreferenceManager.INFO_SEPARATOR);
                        fileSynonyms.put(posibleHeadersArray != null ? posibleHeadersArray[0] : posibleHeader, header);

                        break;
                    }
                }
                if (isAppropriation) {
                    isAppropriation = false;
                    break;
                }
            }
        }
    }

}
