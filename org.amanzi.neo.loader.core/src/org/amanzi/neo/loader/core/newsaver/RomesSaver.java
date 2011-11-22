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
 * saver for romves data
 * 
 * @author Vladislav_Kondratenko
 */
public class RomesSaver extends AbstractDriveSaver {
    // Saver constants
    private static final Logger LOGGER = Logger.getLogger(RomesSaver.class);
    private Set<IDataElement> locationDataElements = new HashSet<IDataElement>();

    protected RomesSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(service);
        DRIVE_TYPE_NAME = DriveTypes.ROMES.name();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.driveModel = model;
            modelMap.put(model.getName(), model);
        } else {
            init(config, null);
        }
    }

    /**
     * 
     */
    public RomesSaver() {
        super();
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        DRIVE_TYPE_NAME = DriveTypes.ROMES.name();
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
    }

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
                locationDataElements.clear();
            } else if (container.getValues().size() == headers.size()) {
                lineCounter++;
                List<String> value = container.getValues();
                saveLine(value);
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
    protected void saveLine(List<String> value) throws AWEException {
        String time = getValueFromRow(TIME, value);
        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        Double latitude = getLatitude(getValueFromRow(IDriveModel.LATITUDE, value));
        Double longitude = getLongitude(getValueFromRow(IDriveModel.LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String sector_id = getValueFromRow(SECTOR_ID, value);
        if (time == null || latitude == null || longitude == null || timestamp == null) {
            LOGGER.info(String.format("Line %s not saved.", lineCounter));
            return;
        }
        collectMElement(time, timestamp, message_type, latitude, longitude, event, sector_id);

        removeEmpty(params);
        collectRemainProperties(params, value);
        addSynonyms(driveModel, params);
        IDataElement existedLocation = checkSameLocation(params);
        if (existedLocation != null) {
            params.remove(IDriveModel.LATITUDE);
            params.remove(IDriveModel.LONGITUDE);
        }
        IDataElement createdElement = driveModel.addMeasurement(fileName, params);
        if (existedLocation != null) {
            List<IDataElement> locList = new LinkedList<IDataElement>();
            locList.add(existedLocation);
            driveModel.linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
        } else {
            locationDataElements.add(driveModel.getLocation(createdElement));
        }
    }

    /**
     * collect M element from required values
     * 
     * @param time
     * @param timestamp
     * @param message_type
     * @param latitude
     * @param longitude
     * @param event
     * @param sector_id
     */
    private void collectMElement(String time, Long timestamp, String message_type, Double latitude, Double longitude, String event,
            String sector_id) {
        params.put(TIME, time);
        params.put(TIMESTAMP, timestamp);
        params.put(MESSAGE_TYPE, message_type);
        params.put(IDriveModel.LATITUDE, latitude);
        params.put(IDriveModel.LONGITUDE, longitude);
        params.put(EVENT, event);
        params.put(NewAbstractService.NAME, time);
        params.put(SECTOR_ID, sector_id);
        params.put(NewAbstractService.TYPE, DriveNodeTypes.M.getId());
    }

    private IDataElement checkSameLocation(Map<String, Object> params) {
        for (IDataElement location : locationDataElements) {
            if (location.get(IDriveModel.LATITUDE).equals(params.get(IDriveModel.LATITUDE))
                    && location.get(IDriveModel.LONGITUDE).equals(params.get(IDriveModel.LONGITUDE))) {
                return location;
            }
        }
        return null;
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
