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

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.preferences.PreferenceStore;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.enums.IDriveType;

/**
 * @author Kondratenko_Vladislav
 */
public class DataLoadPreferenceManager {
    private static DataLoadPreferenceInitializer preferenceInitializer;
    public final static String INFO_SEPARATOR = "_";
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
    /*
     * drive constants
     */
    public final static String BCCH = "bcch" + INFO_SEPARATOR + "TEMS";
    public static final String TCH = "tch" + INFO_SEPARATOR + "ROMES";
    public static final String SC = "sc" + INFO_SEPARATOR + "TEMS";
    public static final String PN = "PN" + INFO_SEPARATOR;
    public static final String ECIO = "ecio" + INFO_SEPARATOR;
    public static final String RSSI = "rssi" + INFO_SEPARATOR;
    /*
     * synonyms map
     */
    private static Map<String, String[]> subTypeSynonyms;
    private static Map<String, String[]> networkMap;
    private static Map<String, String[]> driveMap;

    // private static Map<String, String[]> countMap;

    public static void intializeDefault() {
        if (preferenceInitializer == null) {
            preferenceInitializer = new DataLoadPreferenceInitializer();
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
            driveMap.put(BCCH, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
            driveMap.put(ECIO, getPossibleHeaders(DataLoadPreferences.DR_EcIo));
            driveMap.put(PN, getPossibleHeaders(DataLoadPreferences.DR_PN));
            driveMap.put(RSSI, getPossibleHeaders(DataLoadPreferences.DR_RSSI));
            driveMap.put(SC, getPossibleHeaders(DataLoadPreferences.DR_SC));
            driveMap.put(TCH, getPossibleHeaders(DataLoadPreferences.DR_TCH));
            driveMap.put(INeoConstants.PROPERTY_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.DR_CI));
            driveMap.put(INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.DR_LATITUDE));
            driveMap.put(INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));
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
            networkMap.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
            networkMap.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
            networkMap.put(AZIMUTH, getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH));
            networkMap.put(BEAMWITH, getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH));
            networkMap.put(INeoConstants.PROPERTY_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_CI));
            networkMap.put(INeoConstants.PROPERTY_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_LAC));
            networkMap.put(INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.NH_LATITUDE));
            networkMap.put(INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.NH_LONGITUDE));
            networkMap.put(DataLoadPreferences.MO, getPossibleHeaders(DataLoadPreferences.MO));
            networkMap.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
            networkMap.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
            networkMap.put(DataLoadPreferences.CHGR, getPossibleHeaders(DataLoadPreferences.CHGR));
            networkMap.put(DataLoadPreferences.FHOP, getPossibleHeaders(DataLoadPreferences.FHOP));
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

    /**
     * @param subtype
     * @return
     */
    private Map<String, String[]> getDriveSubtype(IDriveType subtype) {
        for (String key : driveMap.keySet()) {
            String[] keys = key.split(INFO_SEPARATOR);
            if (keys.length > 1) {
                if (keys[1].equals(((DriveTypes)subtype).name())) {
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
