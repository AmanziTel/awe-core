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
package org.amanzi.neo.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.core.enums.NetworkFileType;
import org.amanzi.neo.core.utils.CSVParser;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;

public class LoaderUtils {
    /**
     * return AWE project name of active map
     * 
     * @return
     */
    public static String getAweProjectName() {
        IMap map = ApplicationGIS.getActiveMap();
        return map == ApplicationGIS.NO_MAP ? ApplicationGIS.getActiveProject().getName() : map.getProject().getName();
    }

    /**
     * Convert dBm values to milliwatts
     * 
     * @param dbm
     * @return milliwatts
     */
    public static final double dbm2mw(int dbm) {
        return Math.pow(10.0, ((dbm) / 10.0));
    }

    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    public static final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }

    /**
     * get type of network files
     * 
     * @param fileName file name
     * @return Pair<NetworkFiles, Exception> : <NetworkFiles if file was correctly parsed, else
     *         null,Exception if exception appears else null>
     */
    public static Pair<NetworkFileType, Exception> getFileType(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            while ((line = reader.readLine()) != null && line.length() < 2) {
                // find header
            };
            reader.close();
            if (line == null) {
                return new Pair<NetworkFileType, Exception>(null, null);
            }
            int maxMatch = 0;
            String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
            String fieldSepRegex = "\t";
            for (String regex : possibleFieldSepRegexes) {
                String[] fields = line.split(regex);
                if (fields.length > maxMatch) {
                    maxMatch = fields.length;
                    fieldSepRegex = regex;
                }
            }
            CSVParser parser = new CSVParser(fieldSepRegex.charAt(0));
            List<String> headers = parser.parse(line);
            for (String header : getPossibleHeaders(DataLoadPreferences.PR_NAME)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.PROBE, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NE_ADJ_BTS)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.NEIGHBOUR, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NS_SITE)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.RADIO_SITE, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NH_SECTOR)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.RADIO_SECTOR, null);
                }
            }
            //TODO use preference page
            for (String header : new String[]{"Near end Name","Near End Site No","Far end Name","Far End Site No"}) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.TRANSMISSION, null);
                }
            }
            return new Pair<NetworkFileType, Exception>(null, null);
        } catch (Exception e) {
            return new Pair<NetworkFileType, Exception>(null, e);
        }
    }

    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    public static String[] getPossibleHeaders(String key) {
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
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
}
