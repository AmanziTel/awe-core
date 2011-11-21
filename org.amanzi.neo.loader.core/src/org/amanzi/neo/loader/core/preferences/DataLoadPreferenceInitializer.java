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

import java.nio.charset.Charset;

import org.amanzi.neo.loader.core.internal.DataLoadPluginSynonyms;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.geotools.referencing.CRS;

/**
 * <p>
 * Preference initializer
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DataLoadPreferenceInitializer extends AbstractPreferenceInitializer {
    private static class StoreWrapper {
        PreferenceStore pref = PreferenceStore.getPreferenceStore();
        PreferenceStore store = PreferenceStore.getPreferenceStore();

        /**
         * Sets the default value for the double-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param value the new default value for the preference
         */
        public void setDefault(String name, double value) {
            pref.setDefault(name, value);
            store.setProperty(name, value);
        }

        /**
         * Sets the default value for the float-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param value the new default value for the preference
         */
        public void setDefault(String name, float value) {
            pref.setDefault(name, value);
            store.setProperty(name, value);
        }

        /**
         * Sets the default value for the integer-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param value the new default value for the preference
         */
        public void setDefault(String name, int value) {
            pref.setDefault(name, value);
            store.setProperty(name, value);
        }

        /**
         * Sets the default value for the long-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param value the new default value for the preference
         */
        public void setDefault(String name, long value) {
            pref.setDefault(name, value);
            store.setProperty(name, value);
        }

        /**
         * Sets the default value for the string-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param defaultObject the new default value for the preference
         */
        public void setDefault(String name, String defaultObject) {
            pref.setDefault(name, defaultObject);
            store.setProperty(name, defaultObject);
        }

        /**
         * Sets the default value for the boolean-valued preference with the given name.
         * <p>
         * Note that the current value of the preference is affected if the preference's current
         * value was its old default value, in which case it changes to the new default value. If
         * the preference's current is different from its old default value, its current value is
         * unaffected. No property change events are reported by changing default values.
         * </p>
         * 
         * @param name the name of the preference
         * @param value the new default value for the preference
         */
        public void setDefault(String name, boolean value) {
            pref.setDefault(name, value);
            store.setProperty(name, value);
        }
    }

    /**
     * constructor
     */
    public DataLoadPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        StoreWrapper pref = new StoreWrapper();
        pref.setDefault(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER, "");
        pref.setDefault(DataLoadPreferences.REMOVE_SITE_NAME, false);
        pref.setDefault(DataLoadPreferences.NETWORK_COMBINED_CALCULATION, true);
        pref.setDefault(DataLoadPreferences.ZOOM_TO_LAYER, true);
        pref.setDefault(DataLoadPreferences.ONE_NETWORK_PER_PROJECT, false);
        pref.setDefault(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP, true);
        pref.setDefault(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP, true);
        pref.setDefault(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP, true);

        pref.setDefault(DataLoadPreferences.NH_CITY, DataLoadPluginSynonyms.NH_CITY);
        pref.setDefault(DataLoadPreferences.NH_MSC, DataLoadPluginSynonyms.NH_MSC);
        pref.setDefault(DataLoadPreferences.NH_BSC, DataLoadPluginSynonyms.NH_BSC);
        pref.setDefault(DataLoadPreferences.NH_SITE, DataLoadPluginSynonyms.NH_SITE);
        pref.setDefault(DataLoadPreferences.NH_SECTOR, DataLoadPluginSynonyms.NH_SECTOR);
        pref.setDefault(DataLoadPreferences.NH_SECTOR_CI, DataLoadPluginSynonyms.NH_SECTOR_CI);
        pref.setDefault(DataLoadPreferences.NH_SECTOR_LAC, DataLoadPluginSynonyms.NH_SECTOR_LAC);
        pref.setDefault(DataLoadPreferences.NH_LATITUDE, DataLoadPluginSynonyms.NH_LATITUDE);
        pref.setDefault(DataLoadPreferences.NH_LONGITUDE, DataLoadPluginSynonyms.NH_LONGITUDE);

        pref.setDefault(DataLoadPreferences.DR_LATITUDE, DataLoadPluginSynonyms.DR_LATITUDE);
        pref.setDefault(DataLoadPreferences.DR_LONGITUDE, DataLoadPluginSynonyms.DR_LONGITUDE);
        pref.setDefault(DataLoadPreferences.DR_BCCH, DataLoadPluginSynonyms.DR_BCCH);
        pref.setDefault(DataLoadPreferences.DR_CI, DataLoadPluginSynonyms.DR_CI);
        pref.setDefault(DataLoadPreferences.DR_EcIo, DataLoadPluginSynonyms.DR_EcIo);
        pref.setDefault(DataLoadPreferences.DR_PN, DataLoadPluginSynonyms.DR_PN);
        pref.setDefault(DataLoadPreferences.DR_RSSI, DataLoadPluginSynonyms.DR_RSSI);
        pref.setDefault(DataLoadPreferences.DR_SC, DataLoadPluginSynonyms.DR_SC);
        pref.setDefault(DataLoadPreferences.DR_TCH, DataLoadPluginSynonyms.DR_TCH);

        pref.setDefault(DataLoadPreferences.DR_MS, DataLoadPluginSynonyms.DR_MS);
        pref.setDefault(DataLoadPreferences.DR_EVENT, DataLoadPluginSynonyms.DR_EVENT);
        pref.setDefault(DataLoadPreferences.DR_SECTOR_ID, DataLoadPluginSynonyms.DR_SECTOR_ID);
        pref.setDefault(DataLoadPreferences.DR_ALL_RXLEV_FULL, DataLoadPluginSynonyms.DR_ALL_RXLEV_FULL);
        pref.setDefault(DataLoadPreferences.DR_ALL_RXLEV_SUB, DataLoadPluginSynonyms.DR_ALL_RXLEV_SUB);
        pref.setDefault(DataLoadPreferences.DR_ALL_RXQUAL_FULL, DataLoadPluginSynonyms.DR_ALL_RXQUAL_FULL);
        pref.setDefault(DataLoadPreferences.DR_ALL_RXQUAL_SUB, DataLoadPluginSynonyms.DR_ALL_RXQUAL_SUB);
        pref.setDefault(DataLoadPreferences.DR_ALL_PILOT_SET_COUNT, DataLoadPluginSynonyms.DR_ALL_PILOT_SET_COUNT);
        pref.setDefault(DataLoadPreferences.DR_ALL_SQI, DataLoadPluginSynonyms.DR_ALL_SQI);
        pref.setDefault(DataLoadPreferences.DR_ALL_SQI_MOS, DataLoadPluginSynonyms.DR_ALL_SQI_MOS);
        pref.setDefault(DataLoadPreferences.DR_TIME, DataLoadPluginSynonyms.DR_TIME);
        pref.setDefault(DataLoadPreferences.DR_MESSAGE_TYPE, DataLoadPluginSynonyms.DR_MESSAGE_TYPE);

        for (int i = 0; i <= 12; i++) {
            pref.setDefault(DataLoadPreferences.DR_ALL_PILOT_SET_EC_IO + i,
                    DataLoadPluginSynonyms.getResourceBundle().getString(DataLoadPreferences.DR_ALL_PILOT_SET_EC_IO + i));
            pref.setDefault(DataLoadPreferences.DR_ALL_PILOT_SET_CHANNEL + i,
                    DataLoadPluginSynonyms.getResourceBundle().getString(DataLoadPreferences.DR_ALL_PILOT_SET_CHANNEL + i));
            pref.setDefault(DataLoadPreferences.DR_ALL_PILOT_SET_PN + i,
                    DataLoadPluginSynonyms.getResourceBundle().getString(DataLoadPreferences.DR_ALL_PILOT_SET_PN + i));
        }

        pref.setDefault(DataLoadPreferences.PR_NAME, DataLoadPluginSynonyms.PR_NAME);
        pref.setDefault(DataLoadPreferences.PR_TYPE, DataLoadPluginSynonyms.PR_TYPE);
        pref.setDefault(DataLoadPreferences.PR_LATITUDE, DataLoadPluginSynonyms.PR_LATITUDE);
        pref.setDefault(DataLoadPreferences.PR_LONGITUDE, DataLoadPluginSynonyms.PR_LONGITUDE);

        pref.setDefault(DataLoadPreferences.NH_AZIMUTH, DataLoadPluginSynonyms.NH_AZIMUTH);
        pref.setDefault(DataLoadPreferences.NH_BEAMWIDTH, DataLoadPluginSynonyms.NH_BEAMWIDTH);

        pref.setDefault(DataLoadPreferences.NE_SRV_CI, DataLoadPluginSynonyms.NE_SRV_CI);
        pref.setDefault(DataLoadPreferences.NE_SRV_NAME, DataLoadPluginSynonyms.NE_SRV_NAME);
        pref.setDefault(DataLoadPreferences.NE_SRV_LAC, DataLoadPluginSynonyms.NE_SRV_LAC);
        pref.setDefault(DataLoadPreferences.NE_SRV_CO, DataLoadPluginSynonyms.NE_SRV_CO);
        pref.setDefault(DataLoadPreferences.NE_SRV_ADJ, DataLoadPluginSynonyms.NE_SRV_ADJ);

        pref.setDefault(DataLoadPreferences.NE_NBR_CI, DataLoadPluginSynonyms.NE_NBR_CI);
        pref.setDefault(DataLoadPreferences.NE_NBR_NAME, DataLoadPluginSynonyms.NE_NBR_NAME);
        pref.setDefault(DataLoadPreferences.NE_NBR_LAC, DataLoadPluginSynonyms.NE_NBR_LAC);

        pref.setDefault(DataLoadPreferences.TR_SITE_ID_SERV, DataLoadPluginSynonyms.TR_SITE_ID_SERV);
        pref.setDefault(DataLoadPreferences.TR_SITE_NO_SERV, DataLoadPluginSynonyms.TR_SITE_NO_SERV);
        pref.setDefault(DataLoadPreferences.TR_ITEM_NAME_SERV, DataLoadPluginSynonyms.TR_ITEM_NAME_SERV);

        pref.setDefault(DataLoadPreferences.TR_SITE_ID_NEIB, DataLoadPluginSynonyms.TR_SITE_ID_NEIB);
        pref.setDefault(DataLoadPreferences.TR_SITE_NO_NEIB, DataLoadPluginSynonyms.TR_SITE_NO_NEIB);

        pref.setDefault(DataLoadPreferences.RADIO_HEAD, DataLoadPluginSynonyms.RADIO);
        pref.setDefault(DataLoadPreferences.XCEIVER_HEAD, DataLoadPluginSynonyms.X_CEIVER);
        pref.setDefault(DataLoadPreferences.ADMINISTRATION_HEAD, DataLoadPluginSynonyms.ADMINISTRATION);
        pref.setDefault(DataLoadPreferences.MO, DataLoadPluginSynonyms.MO);
        pref.setDefault(DataLoadPreferences.CHGR, DataLoadPluginSynonyms.CHGR);
        pref.setDefault(DataLoadPreferences.FHOP, DataLoadPluginSynonyms.FHOP);
        // pref.setDefault(DataLoadPreferences.PROPERY_LISTS,
        // "dbm--DELIMETER--dbm--DELIMETER--dbm+mw--DELIMETER--dbm,mw");
        pref.setDefault(DataLoadPreferences.DEFAULT_CHARSET, Charset.defaultCharset().name());
        StringBuilder def;
        try {
            def = new StringBuilder(CRS.decode("EPSG:4326").toWKT()).append(DataLoadPreferences.CRS_DELIMETERS)
                    .append(CRS.decode("EPSG:31467").toWKT()).append(DataLoadPreferences.CRS_DELIMETERS)
                    .append(CRS.decode("EPSG:3021").toWKT());
        } catch (Exception e) {
            e.printStackTrace();
            def = null;
        }
        pref.setDefault(DataLoadPreferences.COMMON_CRS_LIST, def.toString());
        pref.setDefault(DataLoadPreferences.SELECTED_DATA, "");

        pref.setDefault(DataLoadPreferences.REMOTE_SERVER_URL, "http://explorer.amanzitel.com/geoptima");
    }
}
