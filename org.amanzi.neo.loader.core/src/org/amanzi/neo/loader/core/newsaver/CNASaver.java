package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CNASaver extends AbstractSaver<NetworkModel, CSVContainer, ConfigurationDataImpl> {
    private Long lineCounter = 0l;
    private INetworkModel model;
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
    private static Logger LOGGER = Logger.getLogger(CNASaver.class);

    private int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    /**
     * prepare city data element
     * 
     * @param row
     * @return prepared city IDataElement if current rows exist, or <code>null</code>
     */
    private IDataElement collectCity(List<String> row) {
        if (fileSynonyms.get(DataLoadPreferenceManager.CITY) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY).toString())))) {
            return null;
        }
        Map<String, Object> cityPropMap = new HashMap<String, Object>();
        cityPropMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.CITY);
        cityPropMap.put(INeoConstants.PROPERTY_NAME_NAME,
                row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY))));
        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.CITY)), null);
        return new DataElement(cityPropMap);
    }

    /**
     * prepare bsc data element
     * 
     * @param row
     * @return prepared BSC <code>IDataElement</code> if current rows exist, or <code>null</code>
     */
    private IDataElement collectBSC(List<String> row) {
        if (fileSynonyms.get(DataLoadPreferenceManager.BSC) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))).equals("")) {
            return null;
        }
        Map<String, Object> bscProperty = new HashMap<String, Object>();
        bscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.BSC);
        bscProperty.put(INeoConstants.PROPERTY_NAME_NAME,
                row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))));
        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC)), null);
        return new DataElement(bscProperty);
    }

    /**
     * prepare MSC data element
     * 
     * @param row
     * @return prepared MSC <code>IDataElement</code> if current rows exist, or <code>null</code>
     */
    private IDataElement collectMSC(List<String> row) {
        if (fileSynonyms.get(DataLoadPreferenceManager.MSC) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC))) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC))).equals("")) {
            return null;
        }
        Map<String, Object> mscProperty = new HashMap<String, Object>();
        mscProperty.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.MSC);
        mscProperty.put(INeoConstants.PROPERTY_NAME_NAME,
                row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC))));
        row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC)), null);
        return new DataElement(mscProperty);
    }

    /**
     * prepare Site data element
     * 
     * @param row
     * @return prepared Site <code>IDataElement</code> if current rows exist, or <code>null</code>
     */
    private IDataElement collectSite(List<String> row) {
        if (row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME).toString())))
                || row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))) == null
                || StringUtils.isEmpty(row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME).toString())))) {
            LOGGER.info("Missing site info name on line:" + lineCounter);
            return null;
        }
        Map<String, Object> siteMap = new HashMap<String, Object>();
        siteMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SITE);
        siteMap.put(INeoConstants.PROPERTY_LON_NAME, row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))));
        siteMap.put(INeoConstants.PROPERTY_LAT_NAME, row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))));
        if (fileSynonyms.get(DataLoadPreferenceManager.SITE) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE))).equals(StringUtils.EMPTY)) {

            if (fileSynonyms.get(DataLoadPreferenceManager.SECTOR) != null
                    && !row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SECTOR))).equals(StringUtils.EMPTY)) {
                String siteName = row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SECTOR)));
                siteMap.put(INeoConstants.PROPERTY_NAME_NAME, siteName.substring(0, siteName.length() - 1));
            } else {
                LOGGER.info("Missing site name based on SectorName on line:" + lineCounter);
                return null;
            }

        } else {
            siteMap.put(INeoConstants.PROPERTY_NAME_NAME,
                    row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE))));
            row.set(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE)), null);
        }
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME)), null);
        row.set(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME)), null);
        return new DataElement(siteMap);
    }

    /**
     * prepare Sector data element
     * 
     * @param row
     * @return prepared Sector <code>IDataElement</code> if current rows exist, or <code>null</code>
     */
    private IDataElement collectSector(List<String> row) {

        if (fileSynonyms.get(DataLoadPreferenceManager.SECTOR) == null) {
            return null;
        }
        Map<String, Object> sectorMap = new HashMap<String, Object>();
        for (String head : headers) {
            if (row.get(columnSynonyms.get(head)) != null && !StringUtils.isEmpty(row.get(columnSynonyms.get(head)))
                    && !row.get(columnSynonyms.get(head)).equals("NULL")) {
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
            return null;
        }
        if (fileSynonyms.containsKey(DataLoadPreferenceManager.SECTOR)) {
            sectorMap.put(INeoConstants.PROPERTY_NAME_NAME, sectorName);
        }
        sectorMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SECTOR);
        return new DataElement(sectorMap);
    }

    /**
     * find or create BSC node from row properties and pass the action down the chain, for creation
     * BSC->CITY->SITE->SECTOR structure
     */
    private void createMSC(List<String> row) {
        IDataElement msc = collectMSC(row);
        IDataElement bsc = collectBSC(row);
        IDataElement city = collectCity(row);
        IDataElement site = collectSite(row);
        IDataElement sector = collectSector(row);
        boolean isNewCreated = false;
        IDataElement findedMSC = findElement(msc);
        IDataElement findedBSC = findElement(bsc);
        IDataElement findedCity = findElement(city);
        IDataElement findedSite = findElement(site);
        IDataElement findedSector = findElement(sector);
        if (findedMSC == null && msc != null) {
            findedMSC = model.createElement(rootDataElement, msc);
            isNewCreated = true;
        }
        if (findedBSC == null && msc == null && bsc != null) {
            findedBSC = model.createElement(rootDataElement, bsc);
            isNewCreated = true;
        } else if (findedBSC == null && isNewCreated) {
            findedBSC = model.createElement(findedMSC, bsc);
            isNewCreated = true;
        } else if (findedBSC != null && findedMSC != null && isNewCreated) {
            // TODO change relationship from root to msc
            isNewCreated = false;
        }

        if (findedCity == null && bsc == null && msc == null && city != null) {
            findedCity = model.createElement(rootDataElement, city);
        } else if (findedCity == null && bsc == null && isNewCreated) {
            findedCity = model.createElement(findedMSC, city);
        } else if (findedCity == null && msc == null && isNewCreated) {
            findedCity = model.createElement(findedBSC, city);
        } else if (findedCity == null && bsc == null && msc != null) {
            findedCity = model.createElement(findedBSC, city);
        } else if (findedCity != null && msc == null && isNewCreated) {
            // TODO change relationship from root to bsc
        } else if (findedCity != null && bsc == null && isNewCreated) {
            // TODO change relationship from root to mscF
        }
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();

        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            model = getActiveProject().createNetwork(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            rootDataElement = new DataElement(model.getRootNode());
        } catch (Exception e) {
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    private IDataElement findElement(IDataElement element) {
        if (element != null) {
            return model.findElement(element);
        }
        return null;
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
                createMSC(value);

                markTxAsSuccess();
                increaseActionCount();

            }
        } catch (Exception e) {
            LOGGER.error("Error while saving element ", e);
            markTxAsFailure();
        }

    }

    /**
     * make appropriation with synonym column and index in parsed row
     */
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
