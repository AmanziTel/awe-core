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

package org.amanzi.neo.loader.core.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.enums.IDriveType;

/**
 * @author Kondratenko_Vladislav
 */
public class DataLoadPreferenceManager {
    private static DataLoadPreferenceInitializer preferenceInitializer;
    public final static String INFO_SEPARATOR = "//.";
    /*
     * network constants
     */
    public final static String CITY = "city" + INFO_SEPARATOR;
    public final static String BSC = "bsc" + INFO_SEPARATOR;
    public final static String MSC = "msc" + INFO_SEPARATOR;
    public final static String SECTOR = "sector" + INFO_SEPARATOR;
    public final static String SITE = "site" + INFO_SEPARATOR;
    public final static String AZIMUTH = "azimuth" + INFO_SEPARATOR;
    public final static String BEAMWITH = "beamwidth" + INFO_SEPARATOR;
    public final static String BCCH = "bcch" + INFO_SEPARATOR;;
    /*
     * drive constants
     */
    public static final String TEMS = "TEMS";
    public static final String ROMES = "ROMES";

    public static final String EVENT = "event" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;
    public static final String LATITUDE = "lat" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;
    public static final String LONGITUDE = "lon" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;
    public static final String SECTOR_ID = "sector_id" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;
    public static final String TIME = "time" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;
    public static final String MESSAGE_TYPE = "message_type" + INFO_SEPARATOR + TEMS + INFO_SEPARATOR + ROMES;

    public static final String TCH = "tch" + INFO_SEPARATOR + TEMS;
    public static final String SC = "sc" + INFO_SEPARATOR + TEMS;
    public static final String PN = "PN" + INFO_SEPARATOR + TEMS;
    public static final String ECIO = "ecio" + INFO_SEPARATOR + TEMS;
    public static final String RSSI = "rssi" + INFO_SEPARATOR + TEMS;
    public static final String MS = "ms" + INFO_SEPARATOR + TEMS;
    public static final String ALL_RXLEV_FULL = "all_rxlev_full" + INFO_SEPARATOR + TEMS;
    public static final String ALL_RXLEV_SUB = "all_rxlev_sub" + INFO_SEPARATOR + TEMS;
    public static final String ALL_RXQUAL_FULL = "all_rxqual_full" + INFO_SEPARATOR + TEMS;
    public static final String ALL_RXQUAL_SUB = "all_rxqual_sub" + INFO_SEPARATOR + TEMS;
    public static final String ALL_SQI = "all_sqi" + INFO_SEPARATOR + TEMS;
    public static final String ALL_SQI_MOS = "all_sqi_mos" + INFO_SEPARATOR + TEMS;
    public static final String ALL_PILOT_SET_COUNT = "all_pilot_set_count" + INFO_SEPARATOR + TEMS;
    public static final String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    public static final String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    public static final String ALL_PILOT_SET_PN = "all_pilot_set_pn_";

    /*
     * neighbours
     */
    public final static String NEIGHBOUR_SECTOR_CI = "neigh_sector_ci" + INFO_SEPARATOR;
    public final static String NEIGHBOUR_SECTOR_LAC = "neigh_sector_lac" + INFO_SEPARATOR;
    public final static String NEIGHBOUR_SECTOR_NAME = "neigh_sector_name" + INFO_SEPARATOR;
    public final static String SERVING_SECTOR_CI = "serv_sector_ci" + INFO_SEPARATOR;
    public final static String SERVING_SECTOR_LAC = "serv_sector_lac" + INFO_SEPARATOR;
    public final static String SERVING_SECTOR_NAME = "serv_sector_name" + INFO_SEPARATOR;

    /*
     * synonyms map
     */
    private static Map<String, String[]> subTypeSynonyms;
    private static Map<String, String[]> networkMap;
    private static Map<String, String[]> driveMap;
    private static Map<String, String[]> neighMap;
    // private static Map<String, String[]> countMap;

    public static Map<String, PossibleTypes> predifinedPropertyType = new HashMap<String, PossibleTypes>();

    public static void intializeDefault() {

        if (preferenceInitializer == null) {
            preferenceInitializer = new DataLoadPreferenceInitializer();
            predifinedPropertyType.put("lat", PossibleTypes.DOUBLE);
            predifinedPropertyType.put("lon", PossibleTypes.DOUBLE);
            predifinedPropertyType.put("beam", PossibleTypes.DOUBLE);
            predifinedPropertyType.put("azimuth", PossibleTypes.DOUBLE);
        }
        preferenceInitializer.initializeDefaultPreferences();
    }

    public DataLoadPreferenceManager() {
        intializeDefault();
    }

    private String[] getPossibleHeaders(String key) {
        String text = PreferenceStore.getPreferenceStore().getValue(key);
        if (text == null) {
            return new String[0];
        }
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * update existing synonyms or create new
     * 
     * @param type
     * @param newSynonyms
     */
    public void updateSynonyms(DatasetTypes type, Map<String, String[]> newSynonyms) {
        switch (type) {
        case NETWORK:
            networkMap = getNetworkPosibleValues();
            updateSynonyms(networkMap, newSynonyms);
            break;
        case DRIVE:
            driveMap = getDrivePosibleValues();
            updateSynonyms(driveMap, newSynonyms);
        case COUNTERS:
        }
    }

    public void removeSynonym(DatasetTypes type, String key) {
        switch (type) {
        case NETWORK:
            networkMap = getNetworkPosibleValues();
            removeSynonym(networkMap, key);
            break;
        case DRIVE:
            driveMap = getDrivePosibleValues();
            removeSynonym(driveMap, key);
        case COUNTERS:
        }
    }

    /**
     * @param posibleValues
     * @param newSynonyms
     */
    private void updateSynonyms(Map<String, String[]> posibleValues, Map<String, String[]> newSynonyms) {
        for (String newKey : newSynonyms.keySet()) {
            PreferenceStore.getPreferenceStore().setProperty(newKey, newSynonyms.get(newKey));
            posibleValues.put(newKey, newSynonyms.get(newKey));
        }
    }

    public Map<String, String[]> getSynonyms(DatasetTypes type) {
        switch (type) {
        case NETWORK:
            return getNetworkPosibleValues();
        case DRIVE:
            return getDrivePosibleValues();
        case COUNTERS:
            break;
        }
        return null;
    }

    /**
     * return drive synonyms
     * 
     * @return
     */
    private Map<String, String[]> getDrivePosibleValues() {
        if (driveMap == null) {
            driveMap = new HashMap<String, String[]>();
        }
        if (driveMap.isEmpty()) {
            driveMap.put(BCCH + INFO_SEPARATOR + TEMS, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
            driveMap.put(ECIO, getPossibleHeaders(DataLoadPreferences.DR_EcIo));
            driveMap.put(PN, getPossibleHeaders(DataLoadPreferences.DR_PN));
            driveMap.put(RSSI, getPossibleHeaders(DataLoadPreferences.DR_RSSI));
            driveMap.put(SC, getPossibleHeaders(DataLoadPreferences.DR_SC));
            driveMap.put(TCH, getPossibleHeaders(DataLoadPreferences.DR_TCH));
            driveMap.put(NewNetworkService.CELL_INDEX + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.DR_CI));
            driveMap.put(LATITUDE, getPossibleHeaders(DataLoadPreferences.DR_LATITUDE));
            driveMap.put(LONGITUDE, getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));
            driveMap.put(MS, getPossibleHeaders(DataLoadPreferences.DR_MS));
            driveMap.put(EVENT, getPossibleHeaders(DataLoadPreferences.DR_EVENT));
            driveMap.put(SECTOR_ID, getPossibleHeaders(DataLoadPreferences.DR_SECTOR_ID));
            driveMap.put(ALL_RXLEV_FULL, getPossibleHeaders(DataLoadPreferences.DR_ALL_RXLEV_FULL));
            driveMap.put(ALL_RXLEV_SUB, getPossibleHeaders(DataLoadPreferences.DR_ALL_RXLEV_SUB));
            driveMap.put(ALL_RXQUAL_FULL, getPossibleHeaders(DataLoadPreferences.DR_ALL_RXQUAL_FULL));
            driveMap.put(ALL_RXQUAL_SUB, getPossibleHeaders(DataLoadPreferences.DR_ALL_RXQUAL_SUB));
            driveMap.put(ALL_PILOT_SET_COUNT, getPossibleHeaders(DataLoadPreferences.DR_ALL_PILOT_SET_COUNT));
            driveMap.put(ALL_SQI, getPossibleHeaders(DataLoadPreferences.DR_ALL_SQI));
            driveMap.put(ALL_SQI_MOS, getPossibleHeaders(DataLoadPreferences.DR_ALL_SQI_MOS));
            driveMap.put(TIME, getPossibleHeaders(DataLoadPreferences.DR_TIME));
            driveMap.put(MESSAGE_TYPE, getPossibleHeaders(DataLoadPreferences.DR_MESSAGE_TYPE));
            for (int i = 0; i <= 12; i++) {
                driveMap.put(ALL_PILOT_SET_EC_IO + i + INFO_SEPARATOR + TEMS,
                        getPossibleHeaders(DataLoadPreferences.DR_ALL_PILOT_SET_EC_IO + i));
                driveMap.put(ALL_PILOT_SET_CHANNEL + i + INFO_SEPARATOR + TEMS,
                        getPossibleHeaders(DataLoadPreferences.DR_ALL_PILOT_SET_CHANNEL + i));
                driveMap.put(ALL_PILOT_SET_PN + i + INFO_SEPARATOR + TEMS,
                        getPossibleHeaders(DataLoadPreferences.DR_ALL_PILOT_SET_PN + i));
            }
        }
        return driveMap;
    }

    private void removeSynonym(Map<String, String[]> synonymsMap, String key) {
        synonymsMap.remove(key);
        PreferenceStore.getPreferenceStore().remove(key);
    }

    private Map<String, String[]> getNetworkPosibleValues() {
        if (networkMap == null) {
            networkMap = new HashMap<String, String[]>();
        }
        if (networkMap.isEmpty()) {
            networkMap.put(CITY, getPossibleHeaders(DataLoadPreferences.NH_CITY));
            networkMap.put(MSC, getPossibleHeaders(DataLoadPreferences.NH_MSC));
            networkMap.put(BSC, getPossibleHeaders(DataLoadPreferences.NH_BSC));
            networkMap.put(BCCH, getPossibleHeaders(DataLoadPreferences.NH_MSC));
            networkMap.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
            networkMap.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
            networkMap.put(AZIMUTH, getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH));
            networkMap.put(BEAMWITH, getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH));
            networkMap.put(NewNetworkService.CELL_INDEX + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_CI));
            networkMap.put(NewNetworkService.LOCATION_AREA_CODE + INFO_SEPARATOR,
                    getPossibleHeaders(DataLoadPreferences.NH_SECTOR_LAC));
            networkMap.put(INeoConstants.PROPERTY_LAT_NAME + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.NH_LATITUDE));
            networkMap.put(INeoConstants.PROPERTY_LON_NAME + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.NH_LONGITUDE));
            networkMap.put(DataLoadPreferences.MO + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.MO));
            networkMap.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
            networkMap.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
            networkMap.put(BCCH, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
            networkMap.put(DataLoadPreferences.CHGR + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.CHGR));
            networkMap.put(DataLoadPreferences.FHOP + INFO_SEPARATOR, getPossibleHeaders(DataLoadPreferences.FHOP));
        }
        return networkMap;
    }

    /**
     * get synonym for subtype only
     * 
     * @param synonymsType
     * @param subtype
     * @return
     */
    public Map<String, String[]> getSubSynonyms(DatasetTypes synonymsType, IDriveType subtype) {
        if (subTypeSynonyms == null) {
            subTypeSynonyms = new HashMap<String, String[]>();
        }
        subTypeSynonyms.clear();
        switch (synonymsType) {
        case NETWORK:
            return getNetworSubtypeSynonyms(subtype);
        case DRIVE:
            return getDriveSubtype(subtype);
        case COUNTERS:
            break;
        }
        return null;
    }

    public Map<String, String[]> getNeighbourSynonyms() {
        if (neighMap == null) {
            neighMap = new HashMap<String, String[]>();
        }
        if (neighMap.isEmpty()) {
            neighMap.put(SERVING_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NE_SRV_CI));
            neighMap.put(SERVING_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NE_SRV_LAC));
            neighMap.put(SERVING_SECTOR_NAME, getPossibleHeaders(DataLoadPreferences.NE_SRV_NAME));
            neighMap.put(NEIGHBOUR_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NE_NBR_CI));
            neighMap.put(NEIGHBOUR_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NE_NBR_LAC));
            neighMap.put(NEIGHBOUR_SECTOR_NAME, getPossibleHeaders(DataLoadPreferences.NE_NBR_NAME));
        }
        return neighMap;
    }

    /**
     * @param subtype
     * @return
     */
    private Map<String, String[]> getDriveSubtype(IDriveType subtype) {
        for (String key : driveMap.keySet()) {
            String[] keys = key.split(INFO_SEPARATOR);
            for (String iteam : keys) {
                if (iteam.equals(((DriveTypes)subtype).name())) {
                    subTypeSynonyms.put(key, driveMap.get(key));
                }
            }

        }
        return subTypeSynonyms;
    }

    /**
     * @param subtype
     * @return
     */
    private Map<String, String[]> getNetworSubtypeSynonyms(IDriveType subtype) {
        return null;
    }
}
