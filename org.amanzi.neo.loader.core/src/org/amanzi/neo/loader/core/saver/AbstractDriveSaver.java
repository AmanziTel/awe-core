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

package org.amanzi.neo.loader.core.saver;

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

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.apache.log4j.Logger;

/**
 * class contains common operation for tems romes saver
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractDriveSaver extends AbstractCSVSaver<IDriveModel> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDriveSaver.class);

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
    private static final Double LOCATION_DELTA = 0.00001d;
    protected Double latitude;
    protected Double longitude;

    /**
     * regular expression pattern for converting lattitude and longitude
     */
    protected final static Pattern COORDINATE_PATTERN = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");

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
     * collected properties
     */
    protected Map<String, Object> params = new HashMap<String, Object>();

    protected AbstractDriveSaver() {
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
     * Gets the coordinate.
     * 
     * @param stringValue the string value
     * @return the longitude
     */
    protected Double getCoordinate(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            Matcher m = COORDINATE_PATTERN.matcher(stringValue);
            if (m.matches()) {
                try {
                    return Double.valueOf(m.group(1));
                } catch (NumberFormatException e2) {
                    AweConsolePlugin.error(String.format("Can't get Coordinate from: %s", stringValue));
                    LOGGER.error(String.format("Can't get Coordinate from: %s", stringValue));
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
    protected abstract void addNewFileToModels(File file) throws AWEException;

    /**
     * check if there is new file handling reset prepare synonymsand add new file to model, also
     * reset lineCounter
     * 
     * @param dataElement
     * @throws DatabaseException
     * @throws DuplicateNodeNameException
     */
    protected void checkForNewFile(CSVContainer dataElement) throws AWEException {
        if (!dataElement.getFile().getName().equals(fileName)) {
            fileName = dataElement.getFile().getName();
            addNewFileToModels(dataElement.getFile());
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
    protected Long defineTimestamp(Calendar workDate, String time) {
        if (time == null) {
            return null;
        }
        try {
            Date datetime = DATE_FORMAT_WITH_TIME_DATE.parse(time);
            return datetime.getTime();
        } catch (ParseException e1) {
            try {
                Date nodeDate = DATE_FORMAT_TIME.parse(time);
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTime(nodeDate);
                final int nodeHours = newCalendar.get(Calendar.HOUR_OF_DAY);
                if (hours != null && hours > nodeHours) {
                    // next day
                    workDate.add(Calendar.DAY_OF_MONTH, 1);
                    this.workDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                hours = nodeHours;
                workDate.set(Calendar.HOUR, nodeHours);
                workDate.set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE));
                workDate.set(Calendar.SECOND, newCalendar.get(Calendar.SECOND));
                return workDate.getTimeInMillis();

            } catch (Exception e) {
                AweConsolePlugin.error(String.format("Can't parse time: %s", time));
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
     * check parameter values and location list for same locations,
     * 
     * @param existedLocationSource
     * @param params
     * @return
     */
    protected IDataElement checkSameLocation(Iterable<IDataElement> existedLocationSource) {
        for (IDataElement element : existedLocationSource) {
            Double storedLat = (Double)element.get(IDriveModel.LATITUDE);
            Double storedLon = (Double)element.get(IDriveModel.LONGITUDE);
            Double deltLat = storedLat - latitude;
            Double deltaLon = storedLon - longitude;
            if ((deltLat == 0d && deltaLon == 0d) || deltLat >= Math.abs(LOCATION_DELTA) && deltaLon >= Math.abs(LOCATION_DELTA)) {
                return element;
            }
        }
        return null;
    }

    /**
     * create measurment and new location if it necessary to required model element
     * 
     * @param model
     * @param properties
     * @return
     * @throws AWEException
     */
    protected IDataElement addMeasurement(IDriveModel model, Map<String, Object> properties) throws AWEException {
        IDataElement createdMeasurment = model.addMeasurement(fileName, properties, false);
        IDataElement existedLocation = checkSameLocation(model.getLocations(createdMeasurment));
        if (existedLocation == null) {
            model.createLocationNode(createdMeasurment, latitude, longitude);
        }
        return createdMeasurment;
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
        return getDriveType().name();
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        parametrizedModel = getActiveProject().getDataset(
                configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), getDriveType());
        useableModels.add(parametrizedModel);
    }

    /**
     * Returns type of Drive data to save
     * 
     * @return
     */
    protected abstract IDriveType getDriveType();

    @Override
    protected boolean isRenderable() {
        return true;
    }
}
