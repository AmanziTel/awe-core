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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
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

/**
 * @author Vladislav_Kondratenko
 */
public class NewTemsSaver extends AbstractDriveSaver {
    // constants
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String SECTOR_ID = "sector_id";
    private static final String TIME = "time";
    private static final String TIMESTAMP = "timestamp";
    public static final String EVENT = "event";

    public final static String BCCH = "bcch";
    public static final String TCH = "tch";
    public static final String SC = "sc";
    public static final String PN = "PN";
    public static final String ECIO = "ecio";
    public static final String RSSI = "rssi";
    public static final String MS = "ms";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String ALL_RXLEV_FULL = "all_rxlev_full";
    public static final String ALL_RXLEV_SUB = "all_rxlev_sub";
    public static final String ALL_RXQUAL_FULL = "all_rxqual_full";
    public static final String ALL_RXQUAL_SUB = "all_rxqual_sub";
    public static final String ALL_SQI = "all_sqi";
    public static final String ALL_SQI_MOS = "all_sqi_mos";
    public static final String ALL_PILOT_SET_COUNT = "all_pilot_set_count";
    public static final String CHANNEL = "channel";
    public static final String CODE = "code";
    public static final String MW = "mw";
    public static final String DBM = "dbm";
    // 12 posible headers
    public static final String ALL_PILOT_SET_EC_IO = "all_pilot_set_ec_io_";
    public static final String ALL_PILOT_SET_CHANNEL = "all_pilot_set_channel_";
    public static final String ALL_PILOT_SET_PN = "all_pilot_set_pn_";

    // Saver constants
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private static Logger LOGGER = Logger.getLogger(NewTemsSaver.class);
    //
    private Map<String, Integer> columnSynonyms;

    private IDriveModel model;
    private IDriveModel virtualModel;

    private Long lineCounter = 0l;
    private String fileName;

    private String previous_ms = null;
    private String previous_time = null;
    private int previous_pn_code = -1;
    private IDataElement location;
    private Map<String, Object> params = new HashMap<String, Object>();
    /**
     * contains appropriation of header synonyms and name inDB
     * <p>
     * <b>key</b>- name in db ,<br>
     * <b>value</b>-file header key
     * </p>
     */
    private Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    private List<String> headers;

    /**
     * @param value
     * @throws AWEException
     */
    private void buildModels(List<String> value) throws AWEException {
        params.clear();
        String time = getValueFromRow(TIME, value);
        if (time == null) {
            LOGGER.error("There is no time value in row" + value);
            return;
        }

        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        Double latitude = getLatitude(getValueFromRow(LATITUDE, value));
        Double longitude = getLongitude(getValueFromRow(LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String ms = getValueFromRow(MS, value);

        params.put(TIME, time);
        params.put(NewNetworkService.NAME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(LATITUDE, latitude);
        params.put(LONGITUDE, longitude);
        params.put(MESSAGE_TYPE, message_type);

        params.put(EVENT, getSynonymValuewithAutoparse(EVENT, value));
        params.put(BCCH, getSynonymValuewithAutoparse(BCCH, value));
        params.put(TCH, getSynonymValuewithAutoparse(TCH, value));
        params.put(SC, getSynonymValuewithAutoparse(SC, value));
        params.put(PN, getSynonymValuewithAutoparse(PN, value));
        params.put(ECIO, getSynonymValuewithAutoparse(ECIO, value));
        params.put(RSSI, getSynonymValuewithAutoparse(RSSI, value));
        params.put(NewNetworkService.CELL_INDEX, getSynonymValuewithAutoparse(NewNetworkService.CELL_INDEX, value));
        params.put(MS, ms);
        addedSynonyms();
        IDataElement createdMeasurment = addMeasurement(model, params);
        location = model.getLocation(createdMeasurment);
        commitTx();
        createVirtualModelElement(value, ms, time, event, timestamp);

    }

    /**
     * collect and build element for virtual model
     * 
     * @throws AWEException
     */
    private void createVirtualModelElement(List<String> value, String ms, String time, String event, Long timestamp)
            throws AWEException {
        params.clear();
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
            this.previous_ms = (String)params.get(MS);
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
        HashMap<String, float[]> signals = new HashMap<String, float[]>();
        if (measurement_count > 0 && (changed || (event != null && event.length() > 0))) {
            for (int i = 1; i <= measurement_count; i++) {
                // Delete invalid data, as you can have empty ec_io
                // zero ec_io is correct, but empty ec_io is not
                try {
                    ec_io = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_CHANNEL + i, value);
                    channel = (Integer)getSynonymValuewithAutoparse(ALL_PILOT_SET_CHANNEL + i, value);
                    pn_code = (Integer)getSynonymValuewithAutoparse("ALL_PILOT_SET_PN" + i, value);
                    String chan_code = "" + channel + "\t" + pn_code;
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
                List<IDataElement> locationList = new LinkedList<IDataElement>();
                locationList.add(location);
                virtualModel.linkNode(virtualMeasurment, locationList, DriveRelationshipTypes.LOCATION);
                commitTx();
            }
        }
    }

    private void addedSynonyms() {
        addedDatasetSynonyms(model, DriveNodeTypes.M, TIME, getHeaderBySynonym(TIME));
        addedDatasetSynonyms(model, DriveNodeTypes.M, MESSAGE_TYPE, getHeaderBySynonym(MESSAGE_TYPE));
        addedDatasetSynonyms(model, DriveNodeTypes.M, LATITUDE, getHeaderBySynonym(LATITUDE));
        addedDatasetSynonyms(model, DriveNodeTypes.M, LONGITUDE, getHeaderBySynonym(LONGITUDE));
        addedDatasetSynonyms(model, DriveNodeTypes.M, EVENT, getHeaderBySynonym(EVENT));
        addedDatasetSynonyms(model, DriveNodeTypes.M, BCCH, getHeaderBySynonym(BCCH));
        addedDatasetSynonyms(model, DriveNodeTypes.M, TCH, getHeaderBySynonym(TCH));
        addedDatasetSynonyms(model, DriveNodeTypes.M, SC, getHeaderBySynonym(SC));
        addedDatasetSynonyms(model, DriveNodeTypes.M, NewNetworkService.CELL_INDEX,
                getHeaderBySynonym(NewNetworkService.CELL_INDEX));
        addedDatasetSynonyms(model, DriveNodeTypes.M, PN, getHeaderBySynonym(PN));
        addedDatasetSynonyms(model, DriveNodeTypes.M, ECIO, getHeaderBySynonym(ECIO));
        addedDatasetSynonyms(model, DriveNodeTypes.M, RSSI, getHeaderBySynonym(RSSI));
        addedDatasetSynonyms(model, DriveNodeTypes.M, MS, getHeaderBySynonym(MS));

    }

    private String getHeaderBySynonym(String synonymName) {
        return headers.get(getHeaderId(fileSynonyms.get(synonymName)));
    }

    private IDataElement addMeasurement(IDriveModel model, Map<String, Object> properties) throws AWEException {
        return model.addMeasurement(fileName, properties);
    }

    private Object getSynonymValuewithAutoparse(String synonym, List<String> value) {
        return isCorrect(synonym, value) ? autoParse(synonym, getValueFromRow(synonym, value)) : null;
    }

    private String getValueFromRow(String synonym, List<String> value) {
        return isCorrect(synonym, value) ? value.get(columnSynonyms.get(fileSynonyms.get(synonym))) : null;
    }

    private boolean isCorrect(String synonymName, List<String> row) {
        return fileSynonyms.get(synonymName) != null && row.get(columnSynonyms.get(fileSynonyms.get(synonymName))) != null
                && StringUtils.isNotEmpty(row.get(columnSynonyms.get(fileSynonyms.get(synonymName))));
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            model = getActiveProject().getDataset(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK), DriveTypes.TEMS);
            virtualModel = model.getVirtualDataset(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK), DriveTypes.MS);
            modelMap.put(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK), model);
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
            if (fileName != null && !fileName.equals(dataElement.getFile())) {
                fileName = dataElement.getFile().getName();
                fileSynonyms.clear();
                addedNewFileToModels(dataElement.getFile());
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

    private void makeIndexAppropriation() {
        for (String synonyms : fileSynonyms.keySet()) {
            columnSynonyms.put(fileSynonyms.get(synonyms), getHeaderId(fileSynonyms.get(synonyms)));
        }
        for (String head : headers) {
            if (!columnSynonyms.containsKey(head)) {
                columnSynonyms.put(head, getHeaderId(head));
            }
        }
    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    private void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        String name = posibleHeader.substring(0, posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                        fileSynonyms.put(name, header);
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

    private int getHeaderId(String header) {
        return headers.indexOf(header);
    }
}
