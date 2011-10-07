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

/**
 * @author Kondratenko_Vladislav
 */
public class DataLoadPreferenceManager {
    private static DataLoadPreferenceInitializer preferenceInitializer;
    public final static String CITY = "city";
    public final static String BSC = "bsc";
    public final static String MSC = "msc";
    public final static String SECTOR = "sector";
    public final static String SITE = "site";
    public final static String AZIMUTH = "azimuth";
    public final static String BEAMWITH = "beamwidth";

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

    public Map<String, String[]> getSynonyms(DatasetTypes type) {
        switch (type) {
        case NETWORK:
            return getNetworkPosibleValues();
        case DRIVE:
        case COUNTERS:
        default:
            return null;
        }
    }

    private Map<String, String[]> getNetworkPosibleValues() {
        Map<String, String[]> posibleValues = new HashMap<String, String[]>();
        posibleValues.put(CITY, getPossibleHeaders(DataLoadPreferences.NH_CITY));
        posibleValues.put(MSC, getPossibleHeaders(DataLoadPreferences.NH_MSC));
        posibleValues.put(BSC, getPossibleHeaders(DataLoadPreferences.NH_BSC));
        posibleValues.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
        posibleValues.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
        posibleValues.put(AZIMUTH, getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH));
        posibleValues.put(BEAMWITH, getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH));
        posibleValues.put(INeoConstants.PROPERTY_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_CI));
        posibleValues.put(INeoConstants.PROPERTY_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_LAC));
        posibleValues.put(INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.NH_LATITUDE));
        posibleValues.put(INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.NH_LONGITUDE));
        posibleValues.put(DataLoadPreferences.MO, getPossibleHeaders(DataLoadPreferences.MO));
        posibleValues.put(SITE, getPossibleHeaders(DataLoadPreferences.NH_SITE));
        posibleValues.put(SECTOR, getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
        posibleValues.put(DataLoadPreferences.CHGR, getPossibleHeaders(DataLoadPreferences.CHGR));
        posibleValues.put(DataLoadPreferences.FHOP, getPossibleHeaders(DataLoadPreferences.FHOP));
        return posibleValues;
    }

}
