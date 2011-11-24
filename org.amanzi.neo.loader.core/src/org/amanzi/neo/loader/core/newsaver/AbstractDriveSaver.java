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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.log4j.Logger;

/**
 * class contains common operation for tems romes saver
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractDriveSaver extends AbstractCSVSaver<IDriveModel> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDriveSaver.class);

    // Drive type name
    protected String DRIVE_TYPE = null;
    // Name of handling file
    protected String fileName;
    // constants
    protected final static String SECTOR_ID = "sector_id";
    protected final static String TIME = "time";
    protected final static String TIMESTAMP = "timestamp";
    protected final static String EVENT = "event";
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

    /**
     * regular expression pattern for converting lattitude and longitude
     */
    protected final static Pattern LONGITUDE_PATTERN = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
    protected final static Pattern LATITUDE_PATTERN = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");

    /*
     * date format patterns
     */
    protected final static SimpleDateFormat DATE_FORMAT_WITH_TIME_DATE = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
    protected final static SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm:ss");
    // 12 posible headers
    protected final static String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    protected final static String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    protected final static String ALL_PILOT_SET_PN = "all_pilot_set_pn_";
    protected Integer hours;
    /**
     * calendar instance
     */
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

    /**
     * create class instance
     */
    public AbstractDriveSaver() {
        super();
    }

    /**
     * reset synonyms maps
     */
    protected void resetSynonymsMaps() {
        if (!fileSynonyms.isEmpty()) {
            fileSynonyms.clear();
            columnSynonyms.clear();
            headers.clear();
        }
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

    /**
     * collect not handled properties
     * 
     * @param properties
     * @param row
     */
    protected void collectRemainProperties(Map<String, Object> properties, List<String> row) {
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
            Matcher m = LONGITUDE_PATTERN.matcher(stringValue);
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
            // TODO: LN: to constant

            Matcher m = LATITUDE_PATTERN.matcher(stringValue);
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
     * added file element to models
     * 
     * @param file
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    protected abstract void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException;

    /**
     * check if there is new file handling reset prepare synonymsand add new file to model, also
     * reset lineCounter
     * 
     * @param dataElement
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    protected void checkForNewFile(CSVContainer dataElement) throws DatabaseException, DuplicateNodeNameException {
        if ((fileName != null && !fileName.equals(dataElement.getFile().getName())) || (fileName == null)) {
            fileName = dataElement.getFile().getName();
            addedNewFileToModels(dataElement.getFile());
            resetSynonymsMaps();
            lineCounter = 0l;
        }
    }

    /**
     * try to parse time string to timestamp
     * 
     * @param workDate
     * @param time
     * @return
     */
    @SuppressWarnings("deprecation")
    protected Long defineTimestamp(Calendar workDate, String time) {
        if (time == null) {
            return null;
        }
        try {
            Date datetime = DATE_FORMAT_WITH_TIME_DATE.parse(time);
            return datetime.getTime();
        } catch (ParseException e1) {
            try {
                // TODO: Lagutko: refactor to not use DEPRECATED methods
                Date nodeDate = DATE_FORMAT_TIME.parse(time);
                final int nodeHours = nodeDate.getHours();
                if (hours != null && hours > nodeHours) {
                    // next day
                    workDate.add(Calendar.DAY_OF_MONTH, 1);
                    this.workDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                hours = nodeHours;
                // TODO: LN: we have method for this
                workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
                workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
                workDate.set(Calendar.SECOND, nodeDate.getSeconds());
                return workDate.getTimeInMillis();

            } catch (Exception e) {
                LOGGER.error(String.format("Can't parse time: %s", time));

            }
        }
        return 0l;

    }

    @Override
    protected Map<String, String[]> initializeSynonyms() {
        return preferenceManager.getSynonyms(DatasetTypes.DRIVE);

    }

    @Override
    protected void commonLinePreparationActions(CSVContainer dataElement) throws Exception {
        checkForNewFile(dataElement);
    }

    /**
     * link new created element with existed location element;
     * 
     * @param createdElement
     * @param locList
     * @throws DatabaseException
     */
    protected void linkWithLocationElement(IDataElement createdElement, Iterable<IDataElement> locList) throws DatabaseException {
        parametrizedModel.linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
    }

    /**
     * remove incorrect values from params Map
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

    @Override
    protected String getSubType() {
        return DRIVE_TYPE;
    }
}
