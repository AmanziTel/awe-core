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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public class NewRomesSaver extends AbstractDriveSaver {
    // Saver constants
    private final int MAX_TX_BEFORE_COMMIT = 1000;
    private static Logger LOGGER = Logger.getLogger(NewRomesSaver.class);

    private Set<IDataElement> locationDataElements = new HashSet<IDataElement>();
    private IDriveModel model;
    private Long lineCounter = 0l;
    private String fileName;

    protected NewRomesSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
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

    /**
     * 
     */
    public NewRomesSaver() {
        super();
    }

    private void addedSynonyms() {
        for (String key : params.keySet()) {
            if (fileSynonyms.containsKey(key)
                    && (key != NewAbstractService.NAME && key != NewAbstractService.TYPE && key != TIMESTAMP)) {
                addedDatasetSynonyms(model, DriveNodeTypes.M, key, getHeaderBySynonym(key));
            }
        }
        addedDatasetSynonyms(model, DriveNodeTypes.M, NewAbstractService.NAME, getHeaderBySynonym(TIME));
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
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
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if ((fileName != null && !fileName.equals(dataElement.getFile().getName())) || (fileName == null)) {
                fileName = dataElement.getFile().getName();
                fileSynonyms.clear();
                addedNewFileToModels(dataElement.getFile());
                lineCounter = 0l;
            }
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
                locationDataElements.clear();
            } else if (container.getValues().size() == headers.size()) {
                lineCounter++;
                List<String> value = container.getValues();
                buildModel(value);
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

    /**
     * @param value
     * @throws AWEException
     */
    private void buildModel(List<String> value) throws AWEException {
        String time = getValueFromRow(TIME, value);
        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        Double latitude = getLatitude(getValueFromRow(LATITUDE, value));
        Double longitude = getLongitude(getValueFromRow(LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String sector_id = getValueFromRow(SECTOR_ID, value);
        if (time == null || latitude == null || longitude == null || timestamp == null) {
            LOGGER.info(String.format("Line %s not saved.", lineCounter));
            return;
        }
        params.put(TIME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(MESSAGE_TYPE, message_type);
        params.put(LATITUDE, latitude);
        params.put(LONGITUDE, longitude);
        params.put(EVENT, event);
        params.put(NewAbstractService.NAME, time);
        params.put(SECTOR_ID, sector_id);
        for (String header : headers) {
            if (fileSynonyms.containsValue(header)) {
                continue;
            }
            String rowValue = getSynonymValue(value, header);
            if (isCorrect(rowValue)) {
                params.put(header, autoParse(header, rowValue));
            }
        }
        addedSynonyms();
        removeEmpty(params);
        IDataElement existedLocation = checkSameLocation(params);
        if (existedLocation != null) {
            params.remove(LATITUDE);
            params.remove(LONGITUDE);
        }
        IDataElement createdElement = model.addMeasurement(fileName, params);
        if (existedLocation != null) {
            List<IDataElement> locList = new LinkedList<IDataElement>();
            locList.add(existedLocation);
            model.linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
        } else {
            locationDataElements.add(model.getLocation(createdElement));
        }
    }

    private IDataElement checkSameLocation(Map<String, Object> params) {
        for (IDataElement location : locationDataElements) {
            if (location.get(LATITUDE).equals(params.get(LATITUDE)) && location.get(LONGITUDE).equals(params.get(LONGITUDE))) {
                return location;
            }
        }
        return null;
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
                    if (header.toLowerCase().matches(mask.toLowerCase()) || header.toLowerCase().equals(mask.toLowerCase())) {
                        for (String key : posibleHeader.split(DataLoadPreferenceManager.INFO_SEPARATOR)) {
                            if (key.equalsIgnoreCase(DriveTypes.ROMES.name())) {
                                isAppropriation = true;
                                String name = posibleHeader.substring(0,
                                        posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                                fileSynonyms.put(name, header);
                            }
                        }

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
     * @param workDate
     * @param time
     * @return
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
            dfn = new SimpleDateFormat("HH:mm:ss");
            try {
                // TODO: Lagutko: refactor to not use DEPRECATED methods
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

}
