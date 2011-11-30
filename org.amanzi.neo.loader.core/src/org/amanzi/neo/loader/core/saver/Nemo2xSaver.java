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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.loader.core.saver.drive.DriveEvents;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDriveModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for 2.01 data
 * 
 * @author Vladislav_Kondratenko
 */
public class Nemo2xSaver extends AbstractDriveSaver {
    private static final Logger LOGGER = Logger.getLogger(Nemo2xSaver.class);

    // Constants
    protected static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    protected static final String REPLACEMENT_PATTERN_1 = "[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+";
    protected static final String REPLACEMENT_PATTERN_2 = "[^\\w]+";
    protected static final String REPLACEMENT_PATTERN_4 = "\\_$";
    protected static final String REPLACEMENT_PATTERN_3 = "_+";
    protected static final String UNDERSCORE = "_";
    protected String EVENT_TYPE = "event_type";
    private static final String GPS_EVENT = "GPS";
    private static final String NEMO_V2_VERSION = "2.01";
    /*
     * index number
     */
    private static final int EVENT_ID_INDEX = 0;
    private static final int TIME_INDEX = 1;
    private static final int NUMBER_CONTEXT_ID_INDEX = 2;
    private static final int FIRST_PARAMETER_INDEX = 3;

    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.S");

    protected Calendar workDate;
    protected DriveEvents driveEvents;
    protected List<Map<String, Object>> subNodes;

    /**
     * virtual model for parameterizedModel
     */
    protected IDriveModel virtualModel;

    /**
     * create instance of nemo2xsaver
     */
    public Nemo2xSaver() {
        super();
    }

    /**
     * constructor for tests
     * 
     * @param model
     * @param config
     * @param service
     */
    Nemo2xSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            parametrizedModel = model;
            useableModels.add(model);
        }
    }

    @Override
    protected void addNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        parametrizedModel.addFile(file);
    }

    /**
     * return virtual model for parametrizedModel
     * 
     * @return
     * @throws AWEException
     */
    private IDriveModel getVirtualModel() throws AWEException {
        return parametrizedModel.getVirtualDataset(parametrizedModel.getName(), DriveTypes.MS);
    }

    @Override
    protected boolean handleHeaders(CSVContainer dataElement) throws Exception {
        if (!dataElement.getHeaders().isEmpty() && dataElement.getValues().isEmpty()) {
            saveLine(dataElement.getHeaders());
            return false;
        }
        return true;
    }

    @Override
    protected void handleLine(CSVContainer dataElement) throws AWEException {
        if (!dataElement.getHeaders().isEmpty() && !dataElement.getValues().isEmpty()) {
            saveLine(dataElement.getValues());
            lineCounter++;
        }
    }

    /**
     * Creates the sub nodes.
     * 
     * @param eventId the event id
     * @param subNodes the sub nodes
     * @param timestamp the timestamp
     */
    protected void createSubNodes(String eventId, List<Map<String, Object>> subNodes, long timestamp) {
        if (subNodes == null) {
            return;
        }
        for (Map<String, Object> propertyMap : subNodes) {
            Iterator<Entry<String, Object>> iter = propertyMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Object> entry = iter.next();
                if (entry.getValue() == null) {
                    iter.remove();
                }
            }
            if (propertyMap.isEmpty()) {
                continue;
            }
            try {
                if (propertyMap.containsKey(IDriveModel.LATITUDE)) {
                    propertyMap.remove(IDriveModel.LATITUDE);
                }
                if (propertyMap.containsKey(IDriveModel.LONGITUDE)) {
                    propertyMap.remove(IDriveModel.LONGITUDE);
                }
                propertyMap.put(TIMESTAMP, timestamp);
                propertyMap.put(AbstractService.NAME, eventId);
                virtualModel.getFile(fileName);
                addMeasurement(virtualModel, propertyMap);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void saveLine(List<String> headers) throws AWEException {
        params.clear();
        String eventId = headers.get(EVENT_ID_INDEX);
        NemoEvents event = NemoEvents.getEventById(eventId);
        String time = headers.get(TIME_INDEX);
        String numberContextId = headers.get(NUMBER_CONTEXT_ID_INDEX);
        List<Integer> contextId = new ArrayList<Integer>();
        Integer firstParamsId = FIRST_PARAMETER_INDEX;
        if (!numberContextId.isEmpty()) {
            int numContext = Integer.parseInt(numberContextId);
            for (int i = 1; i <= numContext; i++) {
                int value = 0;
                String field = headers.get(firstParamsId++);
                if (!field.isEmpty()) {
                    try {
                        value = Integer.parseInt(field);
                    } catch (NumberFormatException e) {
                        LOGGER.error("Wrong context id:" + field);
                        value = 0;
                    }
                }
                contextId.add(value);
            }
        }
        ArrayList<String> parameters = new ArrayList<String>();
        for (int i = firstParamsId; i < headers.size(); i++) {
            parameters.add(headers.get(i));
        }
        // analyse
        params = analyseKnownParameters(headers, event, contextId, parameters);
        if (params.isEmpty()) {
            return;
        }
        long timestamp;
        try {
            timestamp = getTimeStamp(timeFormat.parse(time));
        } catch (ParseException e) {
            timestamp = 0;
        }
        params.put(AbstractService.NAME, eventId);
        params.put(EVENT_TYPE, eventId);
        params.put(TIMESTAMP, timestamp);
        removeEmpty(params);
        if (GPS_EVENT.equalsIgnoreCase(eventId)) {
            longitude = (Double)params.get(IDriveModel.LONGITUDE);
            latitude = (Double)params.get(IDriveModel.LATITUDE);
            if (isCorrect(latitude) && latitude != 0d && isCorrect(longitude) && longitude != 0d) {
                addMeasurement(parametrizedModel, params);
            }
        }
        createSubNodes(eventId, subNodes, timestamp);
    }

    /**
     * make appropriation with received event value row and properties name
     * 
     * @param element recieved row
     * @param event defined event
     * @param contextId
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> analyseKnownParameters(List<String> element, NemoEvents event, List<Integer> contextId,
            ArrayList<String> parameters) {
        if (parameters.isEmpty()) {
            return params;
        }

        if (event == null) {
            return params;
        }
        Map<String, Object> parParam;
        try {
            parParam = event.fill(getVersion(), parameters);
        } catch (Exception e1) {
            LOGGER.error(String.format("Line %s not parsed", element.toString()));
            return params;
        }
        if (parParam.isEmpty()) {
            return params;
        }
        driveEvents = (DriveEvents)parParam.remove(NemoEvents.DRIVE_EVENTS);
        subNodes = (List<Map<String, Object>>)parParam.remove(NemoEvents.SUB_NODES);
        if (subNodes != null) {
            // store in parameters like prop1,prop2...
            int i = 0;
            for (Map<String, Object> oneSet : subNodes) {
                i++;
                for (Map.Entry<String, Object> entry : oneSet.entrySet()) {
                    parParam.put(new StringBuilder(entry.getKey()).append(i).toString(), entry.getValue());
                }
            }
            subNodes.clear();
        }
        // add context field
        if (parParam.containsKey(NemoEvents.FIRST_CONTEXT_NAME)) {
            List<String> contextName = (List<String>)parParam.get(NemoEvents.FIRST_CONTEXT_NAME);
            parParam.remove(NemoEvents.FIRST_CONTEXT_NAME);
            if (contextId != null) {
                for (int i = 0; i < contextId.size() && i < contextName.size(); i++) {
                    if (contextId.get(i) != 0) {
                        parParam.put(contextName.get(i), contextId.get(i));
                    }
                }
            }
        }
        if (workDate == null && event == NemoEvents.START) {
            workDate = new GregorianCalendar();
            Date date;
            try {
                // TODO: LN: string to const
                date = EVENT_DATE_FORMAT.parse((String)parParam.get("Date"));

            } catch (Exception e) {
                LOGGER.error("Wrong time format" + e.getLocalizedMessage());
                date = new Date();
            }
            workDate.setTime(date);
        }
        // Pechko_E make property names Ruby-compatible
        Set<Entry<String, Object>> entrySet = parParam.entrySet();
        Map<String, Object> parsedParameters = new HashMap<String, Object>(parParam.size());
        for (Entry<String, Object> entry : entrySet) {
            parsedParameters.put(cleanHeader(entry.getKey()), entry.getValue());
        }
        return parsedParameters;
    }

    /**
     * Converts to lower case and replaces all illegal characters with '_' and removes trailing '_'.
     * This is useful for creating a version of a header or property name that can be used as a
     * variable or method name in programming code, notably in Ruby DSL code.
     * 
     * @param original header String
     * @return edited String
     */
    protected final static String cleanHeader(String header) {
        return header.replaceAll(REPLACEMENT_PATTERN_1, UNDERSCORE).replaceAll(REPLACEMENT_PATTERN_2, UNDERSCORE)
                .replaceAll(REPLACEMENT_PATTERN_3, UNDERSCORE).replaceAll(REPLACEMENT_PATTERN_4, StringUtils.EMPTY).toLowerCase();
    }

    /**
     * get Timestamp of nodeDate
     * 
     * @param nodeDate date of node
     * @return long (0 if nodeDate==null)
     */
    @SuppressWarnings("deprecation")
    protected long getTimeStamp(Date nodeDate) {
        if (nodeDate == null || workDate == null) {
            return 0L;
        }
        // TODO: LN: do not use deprecated methods
        final int nodeHours = nodeDate.getHours();
        workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
        workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
        workDate.set(Calendar.SECOND, nodeDate.getSeconds());
        final long timestamp = workDate.getTimeInMillis();
        return timestamp;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    protected String getVersion() {
        return NEMO_V2_VERSION;
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        super.initializeNecessaryModels();
        virtualModel = getVirtualModel();
    }

    @Override
    protected IDriveType getDriveType() {
        return DriveTypes.NEMO_V2;
    }
}
