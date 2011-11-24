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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for romves data
 * 
 * @author Vladislav_Kondratenko
 */
public class RomesSaver extends AbstractDriveSaver {
    private static final Logger LOGGER = Logger.getLogger(RomesSaver.class);
    /**
     * collection of new created locations element
     */
    private Set<IDataElement> locationDataElements = new HashSet<IDataElement>();

    protected RomesSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            useableModels.add(model);
        }
    }

    /**
     * create class instants
     */
    public RomesSaver() {
        super();
        DRIVE_TYPE = DriveTypes.ROMES.name();
    }

    @Override
    protected void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        parametrizedModel.addFile(file);
    }

    @Override
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
        addSynonyms(parametrizedModel, params);
        IDataElement existedLocation = checkForSameLocation(params);
        if (existedLocation != null) {
            params.remove(IDriveModel.LATITUDE);
            params.remove(IDriveModel.LONGITUDE);
        }
        IDataElement createdElement = parametrizedModel.addMeasurement(fileName, params);
        if (existedLocation != null) {
            List<IDataElement> locList = new LinkedList<IDataElement>();
            locList.add(existedLocation);
            linkWithLocationElement(createdElement, locList);
        } else {
            locationDataElements.add(parametrizedModel.getLocation(createdElement));
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

    /**
     * check for same located element
     * 
     * @param params
     * @return
     */
    private IDataElement checkForSameLocation(Map<String, Object> params) {
        for (IDataElement location : locationDataElements) {
            if (location.get(IDriveModel.LATITUDE) == params.get(IDriveModel.LATITUDE)
                    && location.get(IDriveModel.LONGITUDE) == params.get(IDriveModel.LONGITUDE)) {
                return location;
            }
        }
        return null;
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        parametrizedModel = getActiveProject().getDataset(
                configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.ROMES);

    }
}
