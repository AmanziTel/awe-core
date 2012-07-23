/**
 * 
 */
package org.amanzi.neo.loader.core.saver;

import java.io.File;
import java.util.Map;

import org.amanzi.neo.loader.core.config.NemoConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel;

/**
 * Nemo saver
 * 
 * @author Bondoronok_P
 */
public class Nemo2xSaver extends AbstractMappedDataSaver<IDriveModel, NemoConfiguration> {

    private static final String EVENT = "event_type";
    private static final String GPS_EVENT = "gps";

    private DriveModel driveModel = null;
    private IDataElement locationParentNode = null;

    @Override
    protected void saveElement(MappedData dataElement) throws AWEException {
        String eventId = dataElement.remove(EVENT).toLowerCase();
        File file = dataElement.getFile();
        DriveModel model = getDriveModel(file);
        Map<String, Object> values = getDataElementProperties(model, eventId, dataElement, true, false);
        if (GPS_EVENT.equals(eventId)) {
            // locationParentNode = model.addLocation(file.getName(), values);
        } else if (locationParentNode != null) {
            model.addMeasurement(locationParentNode, values);
        }
    }

    /**
     * Get initialized DriveModel instance
     * 
     * @param file file for parsing
     * @return model
     * @throws AWEException
     */
    private DriveModel getDriveModel(File file) throws AWEException {
        if (driveModel == null) {
            driveModel = (DriveModel)getMainModel();
            driveModel.addFile(file);
            addModel(driveModel);
        }
        return driveModel;
    }

    @Override
    protected boolean isRenderable() {
        return true;
    }

    @Override
    protected IDriveModel createMainModel(NemoConfiguration configuration) throws AWEException {
        return getActiveProject().createDriveModel(configuration.getDatasetName(), DriveTypes.NEMO_V2);
    }

    @Override
    protected String getDatasetType() {
        return DatasetTypes.DRIVE.getId();
    }

    @Override
    protected String getSubType() {
        return null;
    }

}
