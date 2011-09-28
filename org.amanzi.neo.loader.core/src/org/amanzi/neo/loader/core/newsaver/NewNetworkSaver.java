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
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NewNetworkSaver extends AbstractSaver<DriveModel, CSVContainer, ConfigurationDataImpl> {
    private Long lineCounter = 0l;
    private NetworkModel model;
    private DataLoadPreferenceManager preferenceManager = new DataLoadPreferenceManager();
    private final String CI_LAC = "CI_LAC";
    private IDataElement rootDataElement;
    private Map<String, Integer> columnSynonyms;
    private final int MAX_TX_BEFORE_COMMIT = 1000;
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

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * CITY->SITE->SECTOR structure
     * 
     * @param row
     */
    private void createBSC(List<String> row) {
        Map<String, Object> bscProperty = new HashMap<String, Object>();
        if (fileSynonyms.get(DataLoadPreferenceManager.BSC) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))).equals("")) {
            createCity(null, row);
            return;
        }
        bscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.BSC);
        bscProperty.put(INeoConstants.PROPERTY_NAME_NAME,
                row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))));

        IDataElement bscElement = new DataElement(bscProperty);
        IDataElement findedElement = model.findElement(bscElement);
        if (findedElement == null) {
            findedElement = model.createElement(rootDataElement, bscElement);
        }
        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC)), null);
        createCity(findedElement, row);
    }

    private int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    /**
     * find or create city node from row properties and pass the action down the chain, for creation
     * SITE->SECTOR nodes
     * 
     * @param row
     */
    private void createCity(IDataElement root, List<String> row) {
        if (fileSynonyms.get(DataLoadPreferenceManager.CITY) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY).toString())))) {
            if (root == null) {
                LOGGER.info("Missing city name on line:" + lineCounter);
                return;
            } else {
                createSite(root, row);

                return;
            }
        }

        Map<String, Object> cityPropMap = new HashMap<String, Object>();

        cityPropMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.CITY);
        cityPropMap.put(INeoConstants.PROPERTY_NAME_NAME,
                row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY))));
        IDataElement cityElement = new DataElement(cityPropMap);
        IDataElement findedElement = model.findElement(cityElement);
        if (findedElement == null) {
            // TODO: LN: duplicated code - why not to create DataElement (or use root) and then call
            // createElement once?
            if (root == null) {
                findedElement = model.createElement(rootDataElement, cityElement);
            } else {
                findedElement = model.createElement(root, cityElement);
            }
        }

        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY)), null);

        createSite(findedElement, row);
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     */
    private void createSite(IDataElement root, List<String> row) {
        if (fileSynonyms.get(DataLoadPreferenceManager.SITE) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE).toString())))) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        siteMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SITE);
        siteMap.put(INeoConstants.PROPERTY_NAME_NAME, row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE))));
        siteMap.put(INeoConstants.PROPERTY_LON_NAME, row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))));
        siteMap.put(INeoConstants.PROPERTY_LAT_NAME, row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))));

        IDataElement siteElement = new DataElement(siteMap);
        IDataElement findedElement = model.findElement(siteElement);

        if (findedElement == null) {
            findedElement = model.createElement(root, siteElement);
        }
        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE)), null);
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME)), null);
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME)), null);
        createSector(findedElement, row);
    }

    /**
     * close the chain with creation of sector if sector was found - pass to next line
     * 
     * @param findedElement site node
     * @param row
     */
    private void createSector(IDataElement root, List<String> row) {
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        if (fileSynonyms.get(DataLoadPreferenceManager.SECTOR) == null) {
            return;
        }
        for (String head : headers) {
            if (row.get(columnSynonyms.get(head)) != null && !StringUtils.isEmpty(row.get(columnSynonyms.get(head)))) {
                sectorMap.put(head.toLowerCase(), row.get(columnSynonyms.get(head)));
            }
        }

        String sectorName = row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SECTOR))) != null ? row.get(
                columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SECTOR))).toString() : "";

        String ci = sectorMap.containsKey("ci") ? sectorMap.get("ci").toString() : "";
        String lac = sectorMap.containsKey("lac") ? sectorMap.get("lac").toString() : "";
        if ((ci == null || StringUtils.isEmpty(ci)) || (lac == null || StringUtils.isEmpty(lac))
                || (sectorName == null || StringUtils.isEmpty(sectorName))) {
            LOGGER.info("Sector haven't Name or CI + LAC properties on line: " + lineCounter);
            return;
        }
        if (fileSynonyms.containsKey(DataLoadPreferenceManager.SECTOR)) {
            sectorMap.put(INeoConstants.PROPERTY_NAME_NAME, sectorName);
        }
        sectorMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SECTOR);

        IDataElement sectorElement = new DataElement(sectorMap);
        IDataElement findedElement = model.findElement(sectorElement);
        if (findedElement == null) {
            model.createElement(root, sectorElement);
        } else {
            LOGGER.info("sector " + sectorMap.get(CI_LAC.toLowerCase()) + " is alreade exist; line: " + lineCounter);
        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();

        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        rootElement.put(PROJECT_PROPERTY,
                new DatasetService().findAweProject(configuration.getDatasetNames().get(CONFIG_VALUE_PROJECT)));
        rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));

        rootElement.put(INeoConstants.PROPERTY_TYPE_NAME, DatasetTypes.NETWORK.getId());
        
        //TODO: LN: should be used another way to create Network
        //ProjectModel.createNetwork
        //also it should be implemented static method ProjectModel.getCurrentProject
        
        model = new NetworkModel(new DataElement(rootElement));
        rootDataElement = new DataElement(model.getRootNode());
    }

    @Override
    public void saveElement(CSVContainer dataElement) {

        openOrReopenTx();

        try {
            CSVContainer container = dataElement;
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                createBSC(value);

                markTxAsSuccess();
                increaseTxCount();

            }
        } catch (Exception e) {
            e.printStackTrace();
            markTxAsFailure();
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

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    private void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceManager.getNetworkPosibleValues().keySet()) {
                for (String mask : preferenceManager.getNetworkPosibleValues().get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        fileSynonyms.put(posibleHeader, header);
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
