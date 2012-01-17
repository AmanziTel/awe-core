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
import java.util.List;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.log4j.Logger;

/**
 * saver for romves data
 * 
 * @author Vladislav_Kondratenko
 */
public class RomesSaver extends AbstractDriveSaver {
    private static final Logger LOGGER = Logger.getLogger(RomesSaver.class);

    /**
     * create saver instance
     */
    public RomesSaver() {
        super();
    }

    /**
     * constuctor for testing
     * 
     * @param model
     * @param config
     */
    RomesSaver(IDriveModel model, ConfigurationDataImpl config) {
        preferenceStoreSynonyms = initializeSynonyms();

        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            useableModels.add(model);
        }
    }

    @Override
    protected void addNewFileToModels(File file) throws AWEException {
        parametrizedModel.addFile(file);
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        params.clear();
        if (value.size() < MINIMUM_COLUMN_NUMBER) {
            AweConsolePlugin.info(String.format("Line %s is empty (nothing to save).", lineCounter));
            LOGGER.info(String.format("Line %s is empty (nothing to save).", lineCounter));
            return;
        }
        String time = getValueFromRow(TIME, value);
        Long timestamp = defineTimestamp(workDate, time);
        String message_type = getValueFromRow(MESSAGE_TYPE, value);
        latitude = getCoordinate(getValueFromRow(IDriveModel.LATITUDE, value));
        longitude = getCoordinate(getValueFromRow(IDriveModel.LONGITUDE, value));
        String event = getValueFromRow(EVENT, value);
        String sector_id = getValueFromRow(SECTOR_ID, value);
        if (time == null || latitude == null || longitude == null || timestamp == null) {
            AweConsolePlugin.info(String.format("Line %s not saved.", lineCounter));
            LOGGER.info(String.format("Line %s not saved.", lineCounter));
            return;
        }
        collectMElement(time, timestamp, message_type, latitude, longitude, event, sector_id);

        removeEmpty(params);
        collectRemainProperties(params, value);
        addSynonyms(parametrizedModel, params);
        addMeasurement(parametrizedModel, params);
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
        params.put(AbstractService.NAME, time);
        params.put(SECTOR_ID, sector_id);
        params.put(AbstractService.TYPE, DriveNodeTypes.M.getId());
    }

    @Override
    protected IDriveType getDriveType() {
        return DriveTypes.ROMES;
    }
}
