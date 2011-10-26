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
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NewNetworkSaver extends AbstractSaver<NetworkModel, CSVContainer, ConfigurationDataImpl> {
    private static Logger LOGGER = Logger.getLogger(NewNetworkSaver.class);
    private Long lineCounter = 0l;
    private INetworkModel model;
    private final String CI_LAC = "CI_LAC";
    private IDataElement rootDataElement;
    private Map<String, Integer> columnSynonyms;
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private final String CITY = "city";
    private final String BSC = "bsc";
    private final String MSC = "msc";
    private final String SECTOR = "sector";
    private final String SITE = "site";
    /**
     * contains appropriation of header synonyms and name inDB</br> <b>key</b>- name in db ,
     * <b>value</b>-file header key
     */
    private Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    private List<String> headers;

    private Map<String, String[]> preferenceStoreSynonyms;

    protected NewNetworkSaver(INetworkModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.model = model;
            rootDataElement = new DataElement(model.getRootNode());
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    /**
     * 
     */
    public NewNetworkSaver() {
        super();
    }

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * BSC->CITY->SITE->SECTOR structure
     */
    private void createMSC(List<String> row) throws AWEException {
        Map<String, Object> mscProperty = new HashMap<String, Object>();
        if (fileSynonyms.get(MSC) == null || row.get(columnSynonyms.get(fileSynonyms.get(MSC))) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(MSC))).equals("")) {
            createBSC(null, row);
            return;
        }

        mscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.MSC.getId());
        mscProperty.put(INeoConstants.PROPERTY_NAME_NAME, autoParse(row.get(columnSynonyms.get(fileSynonyms.get(MSC)))));

        IDataElement findedElement;
        findedElement = model.findElement(mscProperty);

        if (findedElement == null) {
            findedElement = model.createElement(rootDataElement, mscProperty);
            addedDatasetSynonyms(model, NetworkElementNodeType.MSC, INeoConstants.PROPERTY_NAME_NAME,
                    headers.get(getHeaderId(fileSynonyms.get(MSC))));
        }
        row.set(columnSynonyms.get(fileSynonyms.get(MSC)), null);
        createBSC(findedElement, row);
    }

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * CITY->SITE->SECTOR structure
     * 
     * @param row
     * @throws AWEException
     */
    private void createBSC(IDataElement root, List<String> row) throws AWEException {
        Map<String, Object> bscProperty = new HashMap<String, Object>();
        if (fileSynonyms.get(BSC) == null || row.get(columnSynonyms.get(fileSynonyms.get(BSC))) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(BSC))).equals("")) {
            if (root == null) {
                createCity(null, row);
            } else {
                createCity(root, row);
            }
            return;
        }
        bscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.BSC.getId());
        bscProperty.put(INeoConstants.PROPERTY_NAME_NAME, autoParse(row.get(columnSynonyms.get(fileSynonyms.get(BSC)))));
        IDataElement findedElement;
        findedElement = model.findElement(bscProperty);

        if (findedElement == null) {
            if (root != null) {
                findedElement = model.createElement(root, bscProperty);
            } else {
                findedElement = model.createElement(rootDataElement, bscProperty);
            }
            addedDatasetSynonyms(model, NetworkElementNodeType.BSC, INeoConstants.PROPERTY_NAME_NAME,
                    headers.get(getHeaderId(fileSynonyms.get(BSC))));
        }
        row.set(columnSynonyms.get(fileSynonyms.get(BSC)), null);
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
     * @throws AWEException
     */
    private void createCity(IDataElement root, List<String> row) throws AWEException {
        if (fileSynonyms.get(CITY) == null || row.get(columnSynonyms.get(fileSynonyms.get(CITY))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(CITY).toString())))) {
            if (root == null) {
                createSite(rootDataElement, row);
                return;
            } else {
                createSite(root, row);
                return;
            }
        }

        Map<String, Object> cityPropMap = new HashMap<String, Object>();

        cityPropMap.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.CITY.getId());
        cityPropMap.put(INeoConstants.PROPERTY_NAME_NAME, autoParse(row.get(columnSynonyms.get(fileSynonyms.get(CITY)))));

        IDataElement findedElement;
        findedElement = model.findElement(cityPropMap);

        if (findedElement == null) {
            if (root == null) {
                findedElement = model.createElement(rootDataElement, cityPropMap);
            } else {
                findedElement = model.createElement(root, cityPropMap);
            }
            addedDatasetSynonyms(model, NetworkElementNodeType.CITY, INeoConstants.PROPERTY_NAME_NAME,
                    headers.get(getHeaderId(fileSynonyms.get(CITY))));
        }
        row.set(columnSynonyms.get(fileSynonyms.get(CITY)), null);

        createSite(findedElement, row);
    }

    /**
     * find or create site node and pass the action down the chain for creation sector nodes.
     * 
     * @param city node
     * @param row
     * @throws AWEException
     */
    private void createSite(IDataElement root, List<String> row) throws AWEException {
        if ((fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME) == null || row.get(columnSynonyms.get(fileSynonyms
                .get(INeoConstants.PROPERTY_LAT_NAME))) == null)
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME).toString())))
                || (fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME) == null || row.get(columnSynonyms.get(fileSynonyms
                        .get(INeoConstants.PROPERTY_LON_NAME))) == null)
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME).toString())))) {
            LOGGER.info("Missing site name on line:" + lineCounter);
            return;
        }

        Map<String, Object> siteMap = new HashMap<String, Object>();
        siteMap.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.SITE.getId());
        siteMap.put(INeoConstants.PROPERTY_LON_NAME,
                autoParse(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME)))));
        siteMap.put(INeoConstants.PROPERTY_LAT_NAME,
                autoParse(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME)))));
        String siteName;
        if (fileSynonyms.get(SITE) == null || row.get(columnSynonyms.get(fileSynonyms.get(SITE))).equals(StringUtils.EMPTY)) {
            if (fileSynonyms.get(SECTOR) != null
                    && !row.get(columnSynonyms.get(fileSynonyms.get(SECTOR))).equals(StringUtils.EMPTY)) {
                siteName = row.get(columnSynonyms.get(fileSynonyms.get(SECTOR)));
                siteMap.put(INeoConstants.PROPERTY_NAME_NAME, autoParse(siteName.substring(0, siteName.length() - 1)));
            } else {
                LOGGER.info("Missing site name based on SectorName on line:" + lineCounter);
                return;
            }

        } else {
            siteName = headers.get(getHeaderId((fileSynonyms.get(SITE))));
            siteMap.put(INeoConstants.PROPERTY_NAME_NAME, autoParse(row.get(columnSynonyms.get(fileSynonyms.get(SITE)))));
            row.set(columnSynonyms.get(fileSynonyms.get(SITE)), null);
        }

        IDataElement findedElement;
        findedElement = model.findElement(siteMap);

        if (findedElement == null) {
            findedElement = model.createElement(root, siteMap);
            addedDatasetSynonyms(
                    model,
                    NetworkElementNodeType.SITE,
                    INeoConstants.PROPERTY_NAME_NAME,
                    columnSynonyms.get(fileSynonyms.get(SITE)) == null ? SITE : headers.get(columnSynonyms.get(fileSynonyms
                            .get(SITE))));
            addedDatasetSynonyms(model, NetworkElementNodeType.SITE, INeoConstants.PROPERTY_LON_NAME,
                    headers.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))));
            addedDatasetSynonyms(model, NetworkElementNodeType.SITE, INeoConstants.PROPERTY_LAT_NAME,
                    headers.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))));
        }
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME)), null);
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME)), null);
        createSector(findedElement, row);
    }

    /**
     * close the chain with creation of sector if sector was found - pass to next line
     * 
     * @param findedElement site node
     * @param row
     * @throws AWEException
     */
    private void createSector(IDataElement root, List<String> row) throws AWEException {
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        if (fileSynonyms.get(SECTOR) == null) {
            return;
        }
        for (String head : headers) {
            if (row.get(columnSynonyms.get(head)) != null && !StringUtils.isEmpty(row.get(columnSynonyms.get(head)))
                    && head != fileSynonyms.get(SECTOR)) {
                sectorMap.put(head.toLowerCase(), autoParse(row.get(columnSynonyms.get(head))));
                if (fileSynonyms.containsValue(head)) {
                    for (String key : fileSynonyms.keySet()) {
                        if (head.equals(fileSynonyms.get(key))) {
                            addedDatasetSynonyms(model, NetworkElementNodeType.SECTOR, key, head);
                        }
                    }
                }
            }
        }

        String sectorName = row.get(columnSynonyms.get(fileSynonyms.get(SECTOR))) != null ? row.get(
                columnSynonyms.get(fileSynonyms.get(SECTOR))).toString() : "";
        String ci = sectorMap.containsKey("ci") ? sectorMap.get("ci").toString() : "";
        String lac = sectorMap.containsKey("lac") ? sectorMap.get("lac").toString() : "";
        if ((ci == null || StringUtils.isEmpty(ci)) && (lac == null || StringUtils.isEmpty(lac))
                && (sectorName == null || StringUtils.isEmpty(sectorName))) {
            LOGGER.info("Sector should have Name or CI + LAC properties on line: " + lineCounter);
            return;
        }
        if (fileSynonyms.containsKey(SECTOR)) {
            sectorMap.put(INeoConstants.PROPERTY_NAME_NAME, sectorName);
        }
        sectorMap.put(INeoConstants.PROPERTY_TYPE_NAME, (NetworkElementNodeType.SECTOR.getId()));
        IDataElement findedElement = model.findElement(sectorMap);
        if (findedElement == null) {
            model.createElement(root, sectorMap);
            addedDatasetSynonyms(model, NetworkElementNodeType.SECTOR, INeoConstants.PROPERTY_NAME_NAME,
                    headers.get(getHeaderId(fileSynonyms.get(SECTOR))));
        } else {
            LOGGER.info("sector " + sectorMap.get(CI_LAC.toLowerCase()) + " is already exist; line: " + lineCounter);
        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            model = getActiveProject().getNetwork(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            rootDataElement = new DataElement(model.getRootNode());
            modelMap.put(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                createMSC(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
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
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        String name = posibleHeader.substring(0, posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                        fileSynonyms.put(name, header);
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
