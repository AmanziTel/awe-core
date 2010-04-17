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
package org.amanzi.neo.preferences;

import java.nio.charset.Charset;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
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

    /**
     * constructor
     */
    public DataLoadPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore pref = NeoLoaderPlugin.getDefault().getPreferenceStore();
        pref.setDefault(DataLoadPreferences.REMOVE_SITE_NAME, true);
        pref.setDefault(DataLoadPreferences.NETWORK_COMBINED_CALCULATION, true);
        pref.setDefault(DataLoadPreferences.ZOOM_TO_LAYER, true);

        pref.setDefault(DataLoadPreferences.NH_CITY, "City, Town, Ort, SIT_City");
        pref.setDefault(DataLoadPreferences.NH_MSC, "MSC, MSC_NAME, MSC Name");
        pref.setDefault(DataLoadPreferences.NH_BSC, "BSC, BSC_NAME, RNC, BSC Name");
        pref.setDefault(DataLoadPreferences.NH_SITE, "Site, Name, Site Name, SiteName, SITE_ID");
        pref.setDefault(DataLoadPreferences.NH_SECTOR, "Sector, SectorName, Cell, BTS_Name, CELL_NAME, GSM Sector ID");
        pref.setDefault(DataLoadPreferences.NH_SECTOR_CI, "CI,CI_s,CellId");
        pref.setDefault(DataLoadPreferences.NH_SECTOR_LAC, "LAC,LAC_s,La_lacId");
        pref.setDefault(DataLoadPreferences.NH_LATITUDE, "lat.*, y_wert.*, SIT.*Y, northing");
        pref.setDefault(DataLoadPreferences.NH_LONGITUDE, "long.*, x_wert.*, SIT.*X, easting");
        
        pref.setDefault(DataLoadPreferences.PR_NAME, "Probe");
        pref.setDefault(DataLoadPreferences.PR_TYPE, "Type");
        pref.setDefault(DataLoadPreferences.PR_LATITUDE, "lat.*, .*latitude.*");
        pref.setDefault(DataLoadPreferences.PR_LONGITUDE, "long.*, .*longitude.*");
        
        pref.setDefault(DataLoadPreferences.NH_AZIMUTH, ".*azimuth.*");
        pref.setDefault(DataLoadPreferences.NH_BEAMWIDTH, ".*beamwidth.*, beam, hbw");
        
        pref.setDefault(DataLoadPreferences.NE_CI, "CI,CI_s");
        pref.setDefault(DataLoadPreferences.NE_BTS, "BTS_NAME,BTS_Name_s");
        pref.setDefault(DataLoadPreferences.NE_LAC, "LAC,LAC_s");
        
        pref.setDefault(DataLoadPreferences.NE_ADJ_CI, "ADJ_CI,CI_t");
        pref.setDefault(DataLoadPreferences.NE_ADJ_BTS, "ADJ_BTS_NAME,BTS_Name_t");
        pref.setDefault(DataLoadPreferences.NE_ADJ_LAC, "ADJ_LAG,LAC_t");
        
        pref.setDefault(DataLoadPreferences.TR_SITE_ID_SERV, "Site ID, Near end Name");
        pref.setDefault(DataLoadPreferences.TR_SITE_NO_SERV, "Site No, Near End Site No");
        pref.setDefault(DataLoadPreferences.TR_ITEM_NAME_SERV, "ITEM_Name");
        
        pref.setDefault(DataLoadPreferences.TR_SITE_ID_NEIB, "Site ID, Far end Name");
        pref.setDefault(DataLoadPreferences.TR_SITE_NO_NEIB, "Site No, Far End Site No");
        pref.setDefault(DataLoadPreferences.TR_ITEM_NAME_NEIB, "ITEM_Name");
        
        pref.setDefault(DataLoadPreferences.PROPERY_LISTS, "dbm--DELIMETER--dbm--DELIMETER--dbm+mw--DELIMETER--dbm,mw");
        pref.setDefault(DataLoadPreferences.DEFAULT_CHARSET, Charset.defaultCharset().name());
        StringBuilder def;
        try {
            def = new StringBuilder(CRS.decode("EPSG:4326").toWKT()).append(DataLoadPreferences.CRS_DELIMETERS).append(CRS.decode("EPSG:31467").toWKT()).append(
                    DataLoadPreferences.CRS_DELIMETERS).append(CRS.decode("EPSG:3021").toWKT());
        } catch (Exception e) {
            NeoLoaderPlugin.exception(e);
            def = null;
        }
        pref.setDefault(DataLoadPreferences.COMMON_CRS_LIST, def.toString());
    }

}
