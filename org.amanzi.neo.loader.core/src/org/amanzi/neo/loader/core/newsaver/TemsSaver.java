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

import static org.amanzi.neo.services.NewNetworkService.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.NewNetworkService;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * tems data saver
 * 
 * @author Vladislav_Kondratenko
 */
public class TemsSaver extends AbstractDriveSaver {

    //TODO: LN: comments
    private static final Logger LOGGER = Logger.getLogger(TemsSaver.class);

    private IDriveModel virtualModel;

    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private IDataElement location;

    protected TemsSaver(IDriveModel model, IDriveModel virtualModel, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        DRIVE_TYPE_NAME = DriveTypes.TEMS.name();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.driveModel = model;
            this.virtualModel = virtualModel;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    //TODO: LN: comments
    /**
     * 
     */
    public TemsSaver() {
        super();
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        params.clear();
        Object time = getSynonymValueWithAutoparse(TIME, value);
        if (time == null) {
            LOGGER.error("There is no time value in row" + value);
            return;
        }

        Long timestamp = defineTimestamp(workDate, time.toString());
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        Double latitude = getLatitude(getValueFromRow(IDriveModel.LATITUDE, value));
        Double longitude = getLongitude(getValueFromRow(IDriveModel.LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String ms = getValueFromRow(MS, value);

        collectMElement(time, latitude, longitude, message_type, timestamp, value, ms, event);

        removeEmpty(params);
        collectRemainProperties(params, value);
        IDataElement createdMeasurment = addMeasurement(driveModel, params);
        location = driveModel.getLocation(createdMeasurment);
        commitTx();
        addSynonyms(driveModel, params);
        createVirtualModelElement(value, ms, time.toString(), event, timestamp);

    }

    /**
     * collect properties map for M element, from required values;
     * 
     * @param time
     * @param latitude
     * @param longitude
     * @param message_type
     * @param timestamp
     * @param ms
     * @param event
     */
    private void collectMElement(Object time, Double latitude, Double longitude, String message_type, Long timestamp,
            List<String> value, String ms, String event) {
        params.put(TIME, time);
        params.put(NewNetworkService.NAME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(IDriveModel.LATITUDE, latitude);
        params.put(IDriveModel.LONGITUDE, longitude);
        params.put(MESSAGE_TYPE, message_type);
        params.put(NewNetworkService.TYPE, DriveNodeTypes.M.getId());
        params.put(EVENT, event);
        params.put(BCCH, getSynonymValueWithAutoparse(BCCH, value));
        params.put(TCH, getSynonymValueWithAutoparse(TCH, value));
        params.put(SC, getSynonymValueWithAutoparse(SC, value));
        params.put(PN, getSynonymValueWithAutoparse(PN, value));
        params.put(ECIO, getSynonymValueWithAutoparse(ECIO, value));
        params.put(RSSI, getSynonymValueWithAutoparse(RSSI, value));
        params.put(NewNetworkService.CELL_INDEX, getSynonymValueWithAutoparse(NewNetworkService.CELL_INDEX, value));
        params.put(SECTOR_ID, getSynonymValueWithAutoparse(SECTOR_ID, value));
        params.put(MS, ms);
    }

    //TODO: LN: duplicated with Romes
    /**
     * Define timestamp.
     * 
     * @param workDate the work date
     * @param time the time
     * @return the long
     */
    @SuppressWarnings("deprecation")
    protected Long defineTimestamp(Calendar workDate, String time) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dfn = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        try {
            Date datetime = dfn.parse(time);
            return datetime.getTime();
        } catch (ParseException e1) {
            dfn = new SimpleDateFormat("HH:mm:ss.S");
            try {
                Date nodeDate = dfn.parse(time);
                final int nodeHours = nodeDate.getHours();
                if (hours != null && hours > nodeHours) {
                    // next day
                    workDate.add(Calendar.DAY_OF_MONTH, 1);
                    this.workDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                hours = nodeHours;
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

    /**
     * collect and build element for virtual model
     * 
     * @throws AWEException
     */
    private void createVirtualModelElement(List<String> value, String ms, String time, String event, Long timestamp)
            throws AWEException {

        int channel = 0;
        int pn_code = 0;
        int ec_io = 0;
        int measurement_count = 0;
        try {
            //TODO: LN: what is '+1'
            channel = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_CHANNEL + 1, value);
            pn_code = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_PN + 1, value);
            ec_io = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_EC_IO + 1, value);
            measurement_count = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_COUNT, value);
        } catch (Exception e) {
            LOGGER.error("Failed to parse a field on line " + lineCounter + ": " + e.getMessage());
            return;
        }
        //TODO: LN: what is '12'? 
        if (measurement_count > 12) {
            LOGGER.error("Measurement count " + measurement_count + " > 12");
            measurement_count = 12;
        }
        boolean changed = false;
        if (!ms.equals(this.previous_ms)) {
            changed = true;
            this.previous_ms = ms;
        }
        if (!time.equals(this.previous_time)) {
            changed = true;
            this.previous_time = time;
        }
        if (pn_code != this.previous_pn_code) {
            if (this.previous_pn_code >= 0) {
                LOGGER.info("SERVER CHANGED");
            }
            changed = true;
            this.previous_pn_code = pn_code;
        }
        //TODO: LN: make a class for Signals 
        //it's very hard to understand logic
        HashMap<String, float[]> signals = new HashMap<String, float[]>();
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_EC_IO + i, value);
                    channel = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_CHANNEL + i, value);
                    pn_code = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_PN + i, value);
                    String chan_code = StringUtils.EMPTY + channel + "\t" + pn_code;
                    if (!signals.containsKey(chan_code))
                        signals.put(chan_code, new float[2]);
                    signals.get(chan_code)[0] += Math.pow(10.0, ((ec_io) / 10.0));
                    signals.get(chan_code)[1] += 1;
                } catch (Exception e) {
                    //TODO: LN: why there is no exception in log?
                    LOGGER.error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage());
                }
            }
        }
        if (!signals.isEmpty()) {
            params.clear();
            TreeMap<Float, String> sorted_signals = new TreeMap<Float, String>();
            for (String chanCode : signals.keySet()) {
                float[] signal = signals.get(chanCode);
                sorted_signals.put(signal[1] / signal[0], chanCode);
            }
            for (Map.Entry<Float, String> entry : sorted_signals.entrySet()) {
                String chanCode = entry.getValue();
                float[] signal = signals.get(chanCode);
                double mw = signal[0] / signal[1];
                String[] cc = chanCode.split("\\t");

                float dbm = mw2dbm(mw);
                collectMsElement(cc, dbm, mw, timestamp);
                IDataElement virtualMeasurment = addMeasurement(virtualModel, params);

                if (location != null) {
                    List<IDataElement> locationList = new LinkedList<IDataElement>();
                    locationList.add(location);
                    virtualModel.linkNode(virtualMeasurment, locationList, DriveRelationshipTypes.LOCATION);
                }
                commitTx();
            }
        }
    }

    /**
     * collect ms element in virual model
     * 
     * @param cc
     * @param dbm
     * @param mw
     * @param timestamp
     */
    private void collectMsElement(String[] cc, float dbm, double mw, Long timestamp) {
        params.put(NewAbstractService.TYPE, DriveNodeTypes.MS.getId());
        params.put(CHANNEL, autoParse(CHANNEL, cc[0]));
        params.put(CODE, autoParse(CODE, cc[1]));
        params.put(NewAbstractService.NAME, autoParse(NewAbstractService.NAME, cc[1]));
        params.put(DBM, dbm);
        params.put(MW, mw);
        params.put(TIMESTAMP, timestamp);
    }

    private IDataElement addMeasurement(IDriveModel model, Map<String, Object> properties) throws AWEException {
        return model.addMeasurement(fileName, properties);
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        //TODO: LN: it can be moved to constructor
        DRIVE_TYPE_NAME = DriveTypes.TEMS.name();
        //TODO: LN: again root element
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME,
                    configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME));
            driveModel = getActiveProject().getDataset(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.TEMS);
            virtualModel = driveModel.getVirtualDataset(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.MS);
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), driveModel);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        driveModel.addFile(file);
        virtualModel.addFile(file);
    }

    //TODO: LN: duplicated logic in saveElement
    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            checkForNewFile(dataElement);
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                saveLine(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            //TODO: LN: runtime exception
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

}
