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
 * @author Vladislav_Kondratenko
 */
public class NewTemsSaver extends AbstractDriveSaver {

    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private static Logger LOGGER = Logger.getLogger(NewTemsSaver.class);

    private IDriveModel model;
    private IDriveModel virtualModel;

    private Long lineCounter = 0l;
    private String fileName;

    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private IDataElement location;

    protected NewTemsSaver(IDriveModel model, IDriveModel virtualModel, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        DRIVE_TYPE_NAME = DriveTypes.TEMS.name();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.model = model;
            this.virtualModel = virtualModel;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    /**
     * 
     */
    public NewTemsSaver() {
        super();
    }

    /**
     * @param value
     * @throws AWEException
     */
    private void buildModels(List<String> value) throws AWEException {
        params.clear();
        Object time = getSynonymValuewithAutoparse(TIME, value);
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

        params.put(TIME, time);
        params.put(NewNetworkService.NAME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(IDriveModel.LATITUDE, latitude);
        params.put(IDriveModel.LONGITUDE, longitude);
        params.put(MESSAGE_TYPE, message_type);

        params.put(EVENT, getSynonymValuewithAutoparse(EVENT, value));
        params.put(BCCH, getSynonymValuewithAutoparse(BCCH, value));
        params.put(TCH, getSynonymValuewithAutoparse(TCH, value));
        params.put(SC, getSynonymValuewithAutoparse(SC, value));
        params.put(PN, getSynonymValuewithAutoparse(PN, value));
        params.put(ECIO, getSynonymValuewithAutoparse(ECIO, value));
        params.put(RSSI, getSynonymValuewithAutoparse(RSSI, value));
        params.put(NewNetworkService.CELL_INDEX, getSynonymValuewithAutoparse(NewNetworkService.CELL_INDEX, value));
        params.put(SECTOR_ID, getSynonymValuewithAutoparse(SECTOR_ID, value));
        params.put(MS, ms);
        removeEmpty(params);
        addedSynonyms();
        collectRemainProperties(params, value);
        IDataElement createdMeasurment = addMeasurement(model, params);
        location = model.getLocation(createdMeasurment);
        commitTx();

        createVirtualModelElement(value, ms, time.toString(), event, timestamp);

    }

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
            channel = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_CHANNEL + 1, value);
            pn_code = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_PN + 1, value);
            ec_io = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_EC_IO + 1, value);
            measurement_count = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_COUNT, value);
        } catch (Exception e) {
            LOGGER.error("Failed to parse a field on line " + lineCounter + ": " + e.getMessage());
            return;
        }
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
        HashMap<String, float[]> signals = new HashMap<String, float[]>();
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_EC_IO + i, value);
                    channel = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_CHANNEL + i, value);
                    pn_code = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_PN + i, value);
                    String chan_code = StringUtils.EMPTY + channel + "\t" + pn_code;
                    if (!signals.containsKey(chan_code))
                        signals.put(chan_code, new float[2]);
                    signals.get(chan_code)[0] += Math.pow(10.0, ((ec_io) / 10.0));
                    signals.get(chan_code)[1] += 1;
                } catch (Exception e) {
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
                params.put(NewAbstractService.TYPE, DriveNodeTypes.MS.getId());
                params.put(CHANNEL, autoParse(CHANNEL, cc[0]));
                params.put(CODE, autoParse(CODE, cc[1]));
                params.put(NewAbstractService.NAME, autoParse(NewAbstractService.NAME, cc[1]));
                float dbm = mw2dbm(mw);
                params.put(DBM, dbm);
                params.put(MW, mw);
                params.put(TIMESTAMP, timestamp);
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

    private void addedSynonyms() {
        for (String key : params.keySet()) {
            if (key != NewAbstractService.NAME && key != NewAbstractService.TYPE && key != TIMESTAMP) {
                addedDatasetSynonyms(model, DriveNodeTypes.M, key, getHeaderBySynonym(key));
            }
        }
        addedDatasetSynonyms(model, DriveNodeTypes.M, NewAbstractService.NAME, getHeaderBySynonym(TIME));
    }

    private IDataElement addMeasurement(IDriveModel model, Map<String, Object> properties) throws AWEException {
        return model.addMeasurement(fileName, properties);
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        DRIVE_TYPE_NAME = DriveTypes.TEMS.name();
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME,
                    configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME));
            model = getActiveProject().getDataset(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME),
                    DriveTypes.TEMS);
            virtualModel = model.getVirtualDataset(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.MS);
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    private void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        model.addFile(file);
        virtualModel.addFile(file);
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if ((fileName != null && !fileName.equals(dataElement.getFile().getName())) || (fileName == null)) {
                fileName = dataElement.getFile().getName();
                addedNewFileToModels(dataElement.getFile());
                resetSynonymsMaps();
            }
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                buildModels(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

}
