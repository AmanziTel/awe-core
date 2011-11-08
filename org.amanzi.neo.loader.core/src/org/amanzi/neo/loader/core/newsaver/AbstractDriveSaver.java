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

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.apache.log4j.Logger;

/**
 * class contains common operation for tems romes saver
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractDriveSaver extends AbstractSaver<NetworkModel, CSVContainer, ConfigurationDataImpl> {
    // constants
    protected final String LATITUDE = "lat";
    protected final String LONGITUDE = "lon";
    protected final String SECTOR_ID = "sector_id";
    protected final String TIME = "time";
    protected final String TIMESTAMP = "timestamp";
    protected final String EVENT = "event";

    protected static String BCCH = "bcch";
    protected final String TCH = "tch";
    protected final String SC = "sc";
    protected final String PN = "PN";
    protected final String ECIO = "ecio";
    protected final String RSSI = "rssi";
    protected final String MS = "ms";
    protected final String MESSAGE_TYPE = "message_type";
    protected final String ALL_RXLEV_FULL = "all_rxlev_full";
    protected final String ALL_RXLEV_SUB = "all_rxlev_sub";
    protected final String ALL_RXQUAL_FULL = "all_rxqual_full";
    protected final String ALL_RXQUAL_SUB = "all_rxqual_sub";
    protected final String ALL_SQI = "all_sqi";
    protected final String ALL_SQI_MOS = "all_sqi_mos";
    protected final String ALL_PILOT_SET_COUNT = "all_pilot_set_count";
    protected final String CHANNEL = "channel";
    protected final String CODE = "code";
    protected final String MW = "mw";
    protected final String DBM = "dbm";
    // 12 posible headers
    protected final String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    protected final String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    protected final String ALL_PILOT_SET_PN = "all_pilot_set_pn_";

    private static Logger LOGGER = Logger.getLogger(AbstractDriveSaver.class);
    protected Integer hours;
    protected Calendar workDate = Calendar.getInstance();
    /**
     * contains appropriation of header synonyms and name inDB
     * <p>
     * <b>key</b>- name in db ,<br>
     * <b>value</b>-file header key
     * </p>
     */
    protected Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    protected Map<String, Integer> columnSynonyms = new HashMap<String, Integer>();
    protected Map<String, Object> params = new HashMap<String, Object>();
    protected List<String> headers;

    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    protected final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }

    /**
     * Gets the longitude.
     * 
     * @param stringValue the string value
     * @return the longitude
     */
    protected Double getLongitude(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            Pattern p = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
            Matcher m = p.matcher(stringValue);
            if (m.matches()) {
                try {
                    return Double.valueOf(m.group(1));
                } catch (NumberFormatException e2) {
                    LOGGER.error(String.format("Can't get Longitude from: %s", stringValue));
                }
            }
        }
        return null;
    }

    /**
     * Gets the latitude.
     * 
     * @param stringValue the string value
     * @return the latitude
     */
    protected Double getLatitude(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            Pattern p = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
            Matcher m = p.matcher(stringValue);
            if (m.matches()) {
                try {
                    return Double.valueOf(m.group(1));
                } catch (NumberFormatException e2) {
                    LOGGER.error(String.format("Can't get Latitude from: %s", stringValue));
                }
            }
        }
        return null;
    }

    /**
     * remove empty or null values from params Map
     * 
     * @param params2
     */
    protected void removeEmpty(Map<String, Object> params2) {
        List<String> keyToDelete = new LinkedList<String>();
        for (String key : params.keySet()) {
            if (!isCorrect(params.get(key))) {
                keyToDelete.add(key);
            }
        }
        for (String key : keyToDelete) {
            params.remove(key);
        }
    }

    /**
     * get synonym row value and autoparse it
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected Object getSynonymValuewithAutoparse(String synonym, List<String> value) {
        return isCorrect(synonym, value) ? autoParse(synonym, getValueFromRow(synonym, value)) : null;
    }

    /**
     * get value from row without autoparse (like a string)
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected String getValueFromRow(String synonym, List<String> value) {
        return isCorrect(synonym, value) ? value.get(columnSynonyms.get(fileSynonyms.get(synonym))) : null;
    }

    /**
     * check if row value is correct
     * 
     * @param synonymName
     * @param row
     * @return
     */
    protected boolean isCorrect(String synonymName, List<String> row) {
        return fileSynonyms.get(synonymName) != null
                && isCorrect(row.get(columnSynonyms.get(fileSynonyms.get(synonymName))) != null);
    }

    /**
     * get header name by synonymVale
     * 
     * @param synonymName
     * @return
     */
    protected String getHeaderBySynonym(String synonymName) {
        return headers.get(columnSynonyms.get((fileSynonyms.get(synonymName))));
    }

    /**
     * check value for null or empty
     * 
     * @param value
     * @return
     */
    protected boolean isCorrect(Object value) {
        if (value == null || value.toString().isEmpty() || value.toString().equals("?")
                || value.toString().equalsIgnoreCase("NULL")) {
            return false;
        }
        return true;
    }

    /**
     * Get row value by header
     * 
     * @param row
     * @param synonym
     * @return synonym value
     */
    protected String getSynonymValue(List<String> row, String propertyName) {
        return row.get(columnSynonyms.get(propertyName));
    }

    protected int getHeaderId(String header) {
        return headers.indexOf(header);
    }
}
