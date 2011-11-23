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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveRelationshipTypes;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for nemo 1.86 data
 * 
 * @author Vladislav_Kondratenko
 */
public class Nemo1xSaver extends Nemo2xSaver {

    private static final Logger LOGGER = Logger.getLogger(Nemo1xSaver.class);

    protected Nemo1xSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(model, config, service);
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
    public Nemo1xSaver() {
        super();
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        super.init(configuration, dataElement);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            model = getActiveProject().getDataset(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME),
                    DriveTypes.NEMO_V1);
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            //TODO: LN: do not throw Runtime Exception
            throw new RuntimeException(e);
        }
    }

    //TODO: LN: do not re-write super-method, make it configurable to 
    //have no code duplication
    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            checkForNewFile(dataElement);
            if (workDate == null) {
                workDate = new GregorianCalendar();
                Date date;
                try {
                    //TODO: LN: date to Constrant
                    //TODO: LN: SimpleDataFormat to constant
                    //TODO: LN: WTF in split? 
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(container.getFirstLine().split("     ")[2]);
                } catch (Exception e) {
                    LOGGER.error("Wrong time format\n" + e.getLocalizedMessage(), e);
                    date = new Date();
                }
                workDate.setTime(date);
            }
            if (!container.getValues().isEmpty()) {
                //TODO: LN: start symbol to constant
                if (container.getValues().get(0).startsWith("#")) {
                    return;
                }
                lineCounter++;
                List<String> value = container.getValues();
                saveLine(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            //TODO: LN: do not use Runtime
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    protected String getVersion() {
        //TODO: LN: to const 
        return "1.86";
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        //TODO: LN: what is this magic numbers? 0, 1, 2, 8, 9
        String eventId = value.get(0);
        Object longitude = autoParse(IDriveModel.LONGITUDE, value.get(1));
        Object latitude = autoParse(IDriveModel.LATITUDE, value.get(2));
        String time = value.get(8);
        NemoEvents event = NemoEvents.getEventById(eventId);
        List<Integer> contextId = new ArrayList<Integer>();
        ArrayList<String> parameters = new ArrayList<String>();
        for (int i = 9; i < value.size(); i++) {
            parameters.add(value.get(i));
        }
        Map<String, Object> parsedParameters = analyseKnownParameters(value, event, contextId, parameters);
        if (parsedParameters == null) {
            return;
        }
        //TODO: LN: what is 0d? 
        if (isCorrect(latitude) && (Double)latitude != 0d && isCorrect(longitude) && (Double)longitude != 0d) {
            parsedParameters.put(IDriveModel.LATITUDE, latitude);
            parsedParameters.put(IDriveModel.LONGITUDE, longitude);
        }
        parsedParameters.put(NewAbstractService.NAME, eventId);

        long timestamp;
        try {
            //TODO: LN: what is '1'? it dind't used in getTimeStamp method
            timestamp = getTimeStamp(1, timeFormat.parse(time));
        } catch (ParseException e) {
            timestamp = 0;
        }
        parsedParameters.put(TIMESTAMP, timestamp);
        removeEmpty(parsedParameters);

        location = checkSameLocation(parsedParameters);
        if (location != null) {
            parsedParameters.remove(IDriveModel.LATITUDE);
            parsedParameters.remove(IDriveModel.LONGITUDE);
        }
        IDataElement createdElement = model.addMeasurement(fileName, parsedParameters);
        if (location != null) {
            List<IDataElement> locList = new LinkedList<IDataElement>();
            locList.add(location);
            model.linkNode(createdElement, locList, DriveRelationshipTypes.LOCATION);
        } else {
            IDataElement location = model.getLocation(createdElement);
            if (location != null) {
                locationDataElements.add(model.getLocation(createdElement));
            }
        }
        createSubNodes(eventId, subNodes, timestamp);
    }

}
