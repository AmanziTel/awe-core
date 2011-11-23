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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.saver.DriveEvents;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for 2.01 data
 * 
 * @author Vladislav_Kondratenko
 */
public class Nemo2xSaver extends AbstractDriveSaver {
    private static final Logger LOGGER = Logger.getLogger(Nemo2xSaver.class);
    
    //TODO: LN: comments
    //TODO: LN: string to const
    protected static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    protected static final String TIME_FORMAT = "HH:mm:ss.S";
    protected Calendar workDate;
    protected IDriveModel model;
    protected final int MAX_TX_BEFORE_COMMIT = 1000;

    protected DriveEvents driveEvents;
    protected List<Map<String, Object>> subNodes;
    protected SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
    protected Set<IDataElement> locationDataElements = new HashSet<IDataElement>();
    protected String EVENT_TYPE = "event_type";
    protected IDataElement location;

    protected Nemo2xSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.model = model;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    //TODO: LN: comments
    /**
     * 
     */
    public Nemo2xSaver() {
        super();
    }

    //TODO: LN: comments
    protected void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        model.addFile(file);
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        super.init(configuration, dataElement);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        
        try {
            model = getActiveProject().getDataset(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME),
                    DriveTypes.TEMS);
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            //TODO: LN: do not use RuntimeException
            throw new RuntimeException(e);
        }
    }

    //TODO: LN: comments
    protected IDriveModel getVirtualModel() throws AWEException {
        return model.getVirtualDataset(model.getName(), DriveTypes.MS);
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        try {
            commitTx();
            CSVContainer container = dataElement;
            checkForNewFile(dataElement);
            if (!container.getHeaders().isEmpty() && container.getValues().isEmpty()) {
                saveLine(container.getHeaders());
            } else if (!container.getHeaders().isEmpty() && !container.getValues().isEmpty()) {
                saveLine(container.getValues());
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            //TODO: LN: do not throw Runtime
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
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
                propertyMap.put(NewAbstractService.NAME, eventId);
                getVirtualModel().getFile(fileName);
                IDataElement createdElement = getVirtualModel().addMeasurement(fileName, propertyMap);
                List<IDataElement> locList = new LinkedList<IDataElement>();
                locList.add(location);
                //TODO: LN: same as for Nemo1xSaver
                getVirtualModel().linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void saveLine(List<String> headers) throws AWEException {
        //TODO: LN: magic numbers
        String eventId = headers.get(0);
        NemoEvents event = NemoEvents.getEventById(eventId);
        String time = headers.get(1);
        String numberContextId = headers.get(2);
        List<Integer> contextId = new ArrayList<Integer>();
        Integer firstParamsId = 3;
        if (!numberContextId.isEmpty()) {
            int numContext = Integer.parseInt(numberContextId);
            for (int i = 1; i <= numContext; i++) {
                int value = 0;
                String field = headers.get(firstParamsId++);
                if (!field.isEmpty()) {
                    try {
                        value = Integer.parseInt(field);
                    } catch (NumberFormatException e) {
                        // TODO Handle NumberFormatException
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
        Map<String, Object> parsedParameters = analyseKnownParameters(headers, event, contextId, parameters);
        if (parsedParameters == null) {
            return;
        }
        long timestamp;
        try {
            timestamp = getTimeStamp(1, timeFormat.parse(time));
        } catch (ParseException e) {
            // some parameters do not have time
            // NeoLoaderPlugin.error(e.getLocalizedMessage());
            timestamp = 0;
        }
        parsedParameters.put(NewAbstractService.NAME, eventId);
        parsedParameters.put(EVENT_TYPE, eventId);
        parsedParameters.put(TIMESTAMP, timestamp);
        removeEmpty(parsedParameters);
        boolean isAlreadyCreated = false;
        //TODO: LN: string to const
        if ("GPS".equalsIgnoreCase(eventId)) {
            Double longitude = (Double)parsedParameters.get(IDriveModel.LONGITUDE);
            Double latitude = (Double)parsedParameters.get(IDriveModel.LATITUDE);
            if (isCorrect(latitude) && latitude != 0d && isCorrect(longitude) && longitude != 0d) {
                location = checkSameLocation(parsedParameters);
                if (location != null) {
                    parsedParameters.remove(IDriveModel.LATITUDE);
                    parsedParameters.remove(IDriveModel.LONGITUDE);
                }
                IDataElement createdElement = model.addMeasurement(fileName, parsedParameters);
                isAlreadyCreated = true;
                if (location != null) {
                    List<IDataElement> locList = new LinkedList<IDataElement>();
                    locList.add(location);
                    //TODO: LN: see Nemo1xSaver
                    model.linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
                } else {
                    IDataElement location = model.getLocation(createdElement);
                    if (location != null) {
                        locationDataElements.add(model.getLocation(createdElement));
                    }
                }
            }
        }
        if (!isAlreadyCreated) {
            IDataElement createdElement = model.addMeasurement(fileName, parsedParameters);
            IDataElement location = model.getLocation(createdElement);
            if (location != null) {
                locationDataElements.add(model.getLocation(createdElement));
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
            return null;
        }

        if (event == null) {
            return null;
        }
        Map<String, Object> parParam;
        try {
            parParam = event.fill(getVersion(), parameters);
        } catch (Exception e1) {
            LOGGER.error(String.format("Line %s not parsed", element.toString()));
            //TODO: LN: do not use printStackTrace
            e1.printStackTrace();
            // exception(e1);
            return null;
        }
        if (parParam.isEmpty()) {
            return null;
        }
        driveEvents = (DriveEvents)parParam.remove(NemoEvents.DRIVE_EVENTS);
        subNodes = (List<Map<String, Object>>)parParam.remove(NemoEvents.SUB_NODES);
        // TODO check documentation
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
                //TODO: LN: string to const
                date = EVENT_DATE_FORMAT.parse((String)parParam.get("Date"));

            } catch (Exception e) {
                LOGGER.error("Wrong time format" + e.getLocalizedMessage());
                date = new Date();
            }
            workDate.setTime(date);
        }
        // Pechko_E make property names Ruby-compatible
        Set<Entry<String, Object>> entrySet = parParam.entrySet();
        // TODO Check may be a new map is unnecessary and we can use parsedParameters
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
        //TODO: LN: string to const
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_")
                .replaceAll("\\_$", "").toLowerCase();
    }

    /**
     * get Timestamp of nodeDate
     * 
     * @param nodeDate date of node
     * @return long (0 if nodeDate==null)
     */
    @SuppressWarnings("deprecation")
    protected long getTimeStamp(Integer key, Date nodeDate) {
        if (nodeDate == null || workDate == null) {
            return 0L;
        }
        //TODO: LN: do not use deprecated methods
        final int nodeHours = nodeDate.getHours();
        workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
        workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
        workDate.set(Calendar.SECOND, nodeDate.getSeconds());
        final long timestamp = workDate.getTimeInMillis();
        return timestamp;
    }

    protected IDataElement checkSameLocation(Map<String, Object> params) {
        //TODO: LN: you can use 'equals' for double
        //use delta, for example it should be less 0.00001
        for (IDataElement location : locationDataElements) {
            if (location.get(IDriveModel.LATITUDE).equals(params.get(IDriveModel.LATITUDE)) && location.get(IDriveModel.LONGITUDE).equals(params.get(IDriveModel.LONGITUDE))) {
                return location;
            }
        }
        return null;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    protected String getVersion() {
        return "2.01";
    }
}
