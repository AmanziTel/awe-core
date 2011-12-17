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

import static org.amanzi.neo.services.NetworkService.BCCH;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * tems data saver
 * 
 * @author Vladislav_Kondratenko
 */
public class TemsSaver extends AbstractDriveSaver {

    private static final Logger LOGGER = Logger.getLogger(TemsSaver.class);

    /**
     * virtual model(contain calculated data)
     */
    private IDriveModel virtualModel;
    /*
     * constants
     */
    private final static int MAXIMUM_MEASURMENT_COUNT = 12;
    private final static int INDEX_FIRST_PILOT_ELEMNT = 1;
    /*
     * stored previous handled information
     */
    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;

    /**
     * create saver instance
     */
    public TemsSaver() {
        super();
    }

    /**
     * constructor for tests
     * 
     * @param model
     * @param virtualModel
     * @param config
     */
    TemsSaver(IDriveModel model, IDriveModel virtualModel, ConfigurationDataImpl config) {
        preferenceStoreSynonyms = initializeSynonyms();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            this.virtualModel = virtualModel;
            useableModels.add(parametrizedModel);
        }
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        params.clear();
        String time = getValueFromRow(TIME, value);
        if (time == null) {
            AweConsolePlugin.error("There is no time value in row" + value);
            LOGGER.error("There is no time value in row" + value);
            return;
        }

        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        latitude = getCoordinate(getValueFromRow(IDriveModel.LATITUDE, value));
        longitude = getCoordinate(getValueFromRow(IDriveModel.LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String ms = getValueFromRow(MS, value);

        collectMElement(time, latitude, longitude, message_type, timestamp, value, ms, event);

        removeEmpty(params);
        collectRemainProperties(params, value);
        addMeasurement(parametrizedModel, params);
        commitTx();
        addSynonyms(parametrizedModel, params);
        createVirtualModelElement(value, ms, time.toString(), event, timestamp);
        addSynonyms(parametrizedModel, params);
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
        params.put(NetworkService.NAME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(IDriveModel.LATITUDE, latitude);
        params.put(IDriveModel.LONGITUDE, longitude);
        params.put(MESSAGE_TYPE, message_type);
        params.put(NetworkService.TYPE, DriveNodeTypes.M.getId());
        params.put(EVENT, event);
        params.put(BCCH, getSynonymValueWithAutoparse(BCCH, value));
        params.put(TCH, getSynonymValueWithAutoparse(TCH, value));
        params.put(SC, getSynonymValueWithAutoparse(SC, value));
        params.put(PN, getSynonymValueWithAutoparse(PN, value));
        params.put(ECIO, getSynonymValueWithAutoparse(ECIO, value));
        params.put(RSSI, getSynonymValueWithAutoparse(RSSI, value));
        params.put(NetworkService.CELL_INDEX, getSynonymValueWithAutoparse(NetworkService.CELL_INDEX, value));
        params.put(SECTOR_ID, getSynonymValueWithAutoparse(SECTOR_ID, value));
        params.put(MS, ms);
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
            channel = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_CHANNEL + INDEX_FIRST_PILOT_ELEMNT, value);
            pn_code = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_PN + INDEX_FIRST_PILOT_ELEMNT, value);
            ec_io = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_EC_IO + INDEX_FIRST_PILOT_ELEMNT, value);
            measurement_count = (Integer)getSynonymValueWithAutoparse(ALL_PILOT_SET_COUNT, value);
        } catch (Exception e) {
            AweConsolePlugin.error("Failed to parse a field on line " + lineCounter + ": " + e.getMessage());
            LOGGER.error("Failed to parse a field on line " + lineCounter + ": " + e.getMessage());
            return;
        }

        if (measurement_count > MAXIMUM_MEASURMENT_COUNT) {
            AweConsolePlugin.error("Measurement count " + measurement_count + " > " + MAXIMUM_MEASURMENT_COUNT);
            LOGGER.error("Measurement count " + measurement_count + " > " + MAXIMUM_MEASURMENT_COUNT);
            measurement_count = MAXIMUM_MEASURMENT_COUNT;
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
                LOGGER.error("SERVER CHANGED");
            }
            changed = true;
            this.previous_pn_code = pn_code;
        }
        List<Signal> signals = new LinkedList<Signal>();
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_EC_IO + i, value);
                    channel = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_CHANNEL + i, value);
                    pn_code = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_PN + i, value);
                    String chan_code = StringUtils.EMPTY + channel + "\t" + pn_code;
                    Signal tempSignal = new Signal();
                    tempSignal.setChanCode(chan_code);
                    if (!signals.contains(tempSignal)) {
                        signals.add(tempSignal);
                    }
                    tempSignal.getChanarray()[0] += Math.pow(10.0, ((ec_io) / 10.0));
                    tempSignal.getChanarray()[1] += 1;
                } catch (Exception e) {
                    AweConsolePlugin.error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage());
                    LOGGER.error("Error parsing column " + i + " for EC/IO, Channel or PN: " + e.getMessage(), e);
                }
            }
        }
        if (!signals.isEmpty()) {
            params.clear();
            TreeMap<Float, Signal> sorted_signals = new TreeMap<Float, Signal>();
            for (Signal signal : signals) {
                sorted_signals.put(signal.getChanarray()[1] / signal.getChanarray()[0], signal);
            }
            for (Map.Entry<Float, Signal> entry : sorted_signals.entrySet()) {
                Signal signal = entry.getValue();
                float[] signal_chan_array = entry.getValue().getChanarray();
                double mw = signal_chan_array[0] / signal_chan_array[1];
                String[] cc = signal.getChanCode().split("\\t");

                float dbm = mw2dbm(mw);
                collectMsElement(cc, dbm, mw, timestamp);
                addMeasurement(virtualModel, params);
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
        params.put(AbstractService.TYPE, DriveNodeTypes.MS.getId());
        params.put(CHANNEL, autoParse(CHANNEL, cc[0]));
        params.put(CODE, autoParse(CODE, cc[1]));
        params.put(AbstractService.NAME, autoParse(AbstractService.NAME, cc[1]));
        params.put(DBM, dbm);
        params.put(MW, mw);
        params.put(TIMESTAMP, timestamp);
    }

    @Override
    protected void addNewFileToModels(File file) throws AWEException {
        parametrizedModel.addFile(file);
        virtualModel.addFile(file);
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        super.initializeNecessaryModels();
        virtualModel = parametrizedModel.getVirtualDataset(
                configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.MS);

    }

    @Override
    protected IDriveType getDriveType() {
        return DriveTypes.TEMS;
    }
}
