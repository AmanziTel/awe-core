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
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * erricson CNA saver
 * 
 * @author Vladislav_Kondratenko
 */
public class CNASaver extends AbstractSaver<NetworkModel, CSVContainer, ConfigurationDataImpl> {
    private Long lineCounter = 0l;
    private INetworkModel model;
    private DataLoadPreferenceManager preferenceManager = new DataLoadPreferenceManager();
    private final String CI_LAC = "CI_LAC";
    private IDataElement rootDataElement;
    private Map<String, Integer> columnSynonyms;
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private final int MIN_COLUMN_SIZE = 4;
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
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))).equals("")
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.BSC))).equals("NULL")) {
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
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC))).equals("")
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.MSC))).equals("NULL")) {
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
        if (fileSynonyms.get(DataLoadPreferenceManager.SITE) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SITE))).equals("NULL")) {
            LOGGER.info("Missing site info name on line:" + lineCounter);
            return null;
        }
        Map<String, Object> siteMap = new HashMap<String, Object>();
        siteMap.put(INeoConstants.PROPERTY_TYPE_NAME, DataLoadPreferenceManager.SITE);
        if (fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME) != null
                && !row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))).equals("NULL")
                && fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME) != null
                && !row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))).equals("NULL")) {
            siteMap.put(INeoConstants.PROPERTY_LON_NAME,
                    row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LON_NAME))));
            siteMap.put(INeoConstants.PROPERTY_LAT_NAME,
                    row.get(columnSynonyms.get(fileSynonyms.get(INeoConstants.PROPERTY_LAT_NAME))));
        }
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

        if (fileSynonyms.get(DataLoadPreferenceManager.SECTOR) == null
                || row.get(columnSynonyms.get(fileSynonyms.get(DataLoadPreferenceManager.SECTOR))).equals("NULL")) {
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
        if (bsc != null) {
            if (findedBSC == null) {
                if (msc == null) {
                    findedBSC = model.createElement(rootDataElement, bsc);
                    isNewCreated = true;
                } else if (findedMSC != null) {
                    findedBSC = model.createElement(findedMSC, bsc);
                }
            } else if (findedBSC != null && findedMSC != null && isNewCreated) {
                changeRelationship(findedMSC, findedBSC);
                isNewCreated = false;
            }
        }

        if (city != null) {
            if (findedCity == null) {
                if (bsc == null && msc == null) {
                    // if new city found and there is no msc and bsc;
                    findedCity = model.createElement(rootDataElement, city);
                } else if (msc != null && bsc == null && isNewCreated) {
                    // if new city found and there is no bsc
                    findedCity = model.createElement(findedMSC, city);
                } else if (city != null && msc == null && isNewCreated) {
                    // if new city found and there is no msc
                    findedCity = model.createElement(findedBSC, city);
                } else if (findedCity == null && bsc == null && msc != null) {
                    // if new
                    findedCity = model.createElement(findedBSC, city);
                }
            } else if (findedCity != null) {
                if (msc == null && isNewCreated) {
                    changeRelationship(findedBSC, findedCity);
                } else if (bsc == null && isNewCreated) {
                    changeRelationship(findedMSC, findedCity);
                }
            }
        }

        if (site != null) {
            if (findedSite == null) {
                if (bsc == null && msc == null && city == null) {
                    // if new site found and there is no msc and bsc and city;
                    findedSite = model.createElement(rootDataElement, site);
                } else if (bsc == null && msc == null) {
                    // if new site found and there is no msc and bsc ;
                    findedSite = model.createElement(findedCity, site);
                } else if (msc == null) {
                    // if new site found and there is no msc ;
                    findedSite = model.createElement(findedBSC, site);
                } else if (bsc == null) {
                    // if new site found and there is no msc ;
                    findedSite = model.createElement(findedMSC, site);
                } else if (msc != null && bsc != null) {
                    // if new site found and there is no msc ;
                    findedSite = model.createElement(findedBSC, site);
                }
            } else {
                if (bsc == null && city == null && isNewCreated) {
                    changeRelationship(findedMSC, findedSite);
                } else if (msc == null && city == null && isNewCreated) {
                    changeRelationship(findedBSC, findedSite);
                } else if (city != null && isNewCreated) {
                    changeRelationship(findedCity, findedSite);
                }
            }
        }

        if (sector != null) {
            if (findedSector == null) {
                if (findedSite != null) {
                    findedSector = model.createElement(findedSite, site);
                }
            }
        }
    }

    private void changeRelationship(IDataElement newParentElement, IDataElement currentElement) {
        model.changeRelationship(newParentElement, currentElement);
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            model = getActiveProject().getNetwork(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            rootDataElement = new DataElement(model.getRootNode());
        } catch (AWEException e) {
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
            } else if (container.getValues().size() > MIN_COLUMN_SIZE) {
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
