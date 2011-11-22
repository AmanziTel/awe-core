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

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * class contains common operation for tems romes saver
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractDriveSaver extends AbstractCSVSaver<DriveModel> {
    private static final Logger LOGGER = Logger.getLogger(AbstractDriveSaver.class);
    protected String DRIVE_TYPE_NAME = "";
    protected String fileName;
    // constants
    protected final static String SECTOR_ID = "sector_id";
    protected final static String TIME = "time";
    protected final static String TIMESTAMP = "timestamp";
    protected final static String EVENT = "event";
    protected IDriveModel driveModel;
    protected final static String TCH = "tch";
    protected final static String SC = "sc";
    protected final static String PN = "PN";
    protected final static String ECIO = "ecio";
    protected final static String RSSI = "rssi";
    protected final static String MS = "ms";
    protected final static String MESSAGE_TYPE = "message_type";
    protected final static String ALL_RXLEV_FULL = "all_rxlev_full";
    protected final static String ALL_RXLEV_SUB = "all_rxlev_sub";
    protected final static String ALL_RXQUAL_FULL = "all_rxqual_full";
    protected final static String ALL_RXQUAL_SUB = "all_rxqual_sub";
    protected final static String ALL_SQI = "all_sqi";
    protected final static String ALL_SQI_MOS = "all_sqi_mos";
    protected final static String ALL_PILOT_SET_COUNT = "all_pilot_set_count";
    protected final static String CHANNEL = "channel";
    protected final static String CODE = "code";
    protected final static String MW = "mw";
    protected final static String DBM = "dbm";
    // 12 posible headers
    protected final static String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    protected final static String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    protected final static String ALL_PILOT_SET_PN = "all_pilot_set_pn_";
    protected Integer hours;
    protected Calendar workDate = Calendar.getInstance();

    /**
     * @param service
     */
    protected AbstractDriveSaver(GraphDatabaseService service) {
        super(service);
    }

    protected void resetSynonymsMaps() {
        if (!fileSynonyms.isEmpty()) {
            fileSynonyms.clear();
            columnSynonyms.clear();
            headers.clear();
        }
    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    protected void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase()) || header.toLowerCase().equals(mask.toLowerCase())) {
                        for (String key : posibleHeader.split(DataLoadPreferenceManager.INFO_SEPARATOR)) {
                            if (key.equalsIgnoreCase(DRIVE_TYPE_NAME)) {
                                isAppropriation = true;
                                String name = posibleHeader.substring(0,
                                        posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                                if (!fileSynonyms.containsKey(name))
                                    fileSynonyms.put(name, header);
                            }
                        }

                    }
                    if (isAppropriation) {
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

    /**
     * 
     */
    public AbstractDriveSaver() {
        super();
    }

    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    protected final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }

    void collectRemainProperties(Map<String, Object> properties, List<String> row) {
        for (String head : headers) {
            if (isCorrect(head, row)) {
                if (!properties.containsKey(head)) {
                    if ((fileSynonyms.containsValue(head) && properties.containsKey(getSynonymForHeader(head)))) {
                        continue;
                    }
                    properties.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
                }
            }
        }
        
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

    protected abstract void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException;

    protected void checkForNewFile(CSVContainer dataElement) throws DatabaseException, DuplicateNodeNameException {
        if ((fileName != null && !fileName.equals(dataElement.getFile().getName())) || (fileName == null)) {
            fileName = dataElement.getFile().getName();
            addedNewFileToModels(dataElement.getFile());
            resetSynonymsMaps();
            lineCounter = 0l;
        }
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

}
