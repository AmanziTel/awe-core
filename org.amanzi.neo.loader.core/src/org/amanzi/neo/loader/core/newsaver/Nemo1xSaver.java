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
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * saver for nemo 1.86 data
 * 
 * @author Vladislav_Kondratenko
 */
public class Nemo1xSaver extends Nemo2xSaver {

    private static final Logger LOGGER = Logger.getLogger(Nemo1xSaver.class);
    /*
     * constants
     */
    private static final String NEMO_FIRST_LINE_SPLIT = "     ";
    private static final String NEMO_V1_VERSION = "1.86";
    private static final String START_SYMBOL = "#";


    private static final int EVENT_ID_INDEX = 0;
    private static final int LATITUDE_INDEX = 2;
    private static final int LONGITUDE_INDEX = 1;
    private static final int TIME_INDEX = 8;

    private static final int FIRST_PARAMETER_INDEX = 9;
    private static final double INCORRECT_LAT_LON_VALUE = 0;
    
    public Nemo1xSaver() {
        super();
    }

    /**
     * Constructor for testing 
     * 
     * @param model
     * @param config
     * @param service
     */
    protected Nemo1xSaver(IDriveModel model, ConfigurationDataImpl config, GraphDatabaseService service) {
        super(model, config, service);
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        columnSynonyms = new HashMap<String, Integer>();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        if (model != null) {
            this.parametrizedModel = model;
            useableModels.add(model);
        }
    }

    @Override
    protected void commonLinePreparationActions(CSVContainer dataElement) throws Exception {
        checkForNewFile(dataElement);
        checkForNewFile(dataElement);
        if (workDate == null) {
            workDate = new GregorianCalendar();
            Date date;
            try {
                date = EVENT_DATE_FORMAT.parse(dataElement.getFirstLine().split(NEMO_FIRST_LINE_SPLIT)[2]);
            } catch (Exception e) {
                LOGGER.error("Wrong time format\n" + e.getLocalizedMessage(), e);
                date = new Date();
            }
            workDate.setTime(date);
        }

    }

    @Override
    protected boolean handleHeaders(CSVContainer dataElement) throws Exception {
        return true;
    }

    @Override
    protected void handleLine(CSVContainer dataElement) throws AWEException {
        if (!dataElement.getValues().isEmpty()) {
            if (dataElement.getValues().get(0).startsWith(START_SYMBOL)) {
                return;
            }
            lineCounter++;
            List<String> value = dataElement.getValues();
            saveLine(value);
        }
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    protected String getVersion() {
        return NEMO_V1_VERSION;
    }

    @Override
    protected void saveLine(List<String> value) throws AWEException {
        String eventId = value.get(EVENT_ID_INDEX);
        Object longitude = autoParse(IDriveModel.LONGITUDE, value.get(LONGITUDE_INDEX));
        Object latitude = autoParse(IDriveModel.LATITUDE, value.get(LATITUDE_INDEX));
        String time = value.get(TIME_INDEX);
        NemoEvents event = NemoEvents.getEventById(eventId);
        List<Integer> contextId = new ArrayList<Integer>();
        ArrayList<String> parameters = new ArrayList<String>();

        for (int i = FIRST_PARAMETER_INDEX; i < value.size(); i++) {
            parameters.add(value.get(i));
        }
        Map<String, Object> parsedParameters = analyseKnownParameters(value, event, contextId, parameters);
        if (parsedParameters == null) {
            return;
        }
        if (isCorrect(latitude) && (Double)latitude != INCORRECT_LAT_LON_VALUE && isCorrect(longitude)
                && (Double)longitude != INCORRECT_LAT_LON_VALUE) {
            parsedParameters.put(IDriveModel.LATITUDE, latitude);
            parsedParameters.put(IDriveModel.LONGITUDE, longitude);
        }
        parsedParameters.put(NewAbstractService.NAME, eventId);

        long timestamp;
        try {
            timestamp = getTimeStamp(timeFormat.parse(time));
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
        IDataElement createdElement = parametrizedModel.addMeasurement(fileName, parsedParameters);
        if (location != null) {
            List<IDataElement> locList = new LinkedList<IDataElement>();
            locList.add(location);
            linkWithLocationElement(createdElement, locList);
        } else {
            IDataElement location = parametrizedModel.getLocation(createdElement);
            if (location != null) {
                locationDataElements.add(parametrizedModel.getLocation(createdElement));
            }
        }
        createSubNodes(eventId, subNodes, timestamp);
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        parametrizedModel = getActiveProject().getDataset(
                configuration.getDatasetNames().get(ConfigurationDataImpl.DATASET_PROPERTY_NAME), DriveTypes.NEMO_V1);

    }
}
