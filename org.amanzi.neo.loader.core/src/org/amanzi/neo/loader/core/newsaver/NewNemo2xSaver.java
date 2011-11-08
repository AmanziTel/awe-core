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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.neo.core.utils.DriveEvents;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewDatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDriveModel;
import org.apache.log4j.Logger;

/**
 * @author Vladislav_Kondratenko
 */
public class NewNemo2xSaver extends AbstractDriveSaver {
    protected static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    protected static final String TIME_FORMAT = "HH:mm:ss.S";
    protected Calendar workDate;
    protected IDriveModel model;
    protected final int MAX_TX_BEFORE_COMMIT = 1000;
    private static Logger LOGGER = Logger.getLogger(NewNemo2xSaver.class);
    protected String fileName;
    protected long lineCounter = 0l;
    protected DriveEvents driveEvents;
    protected List<Map<String, Object>> subNodes;
    protected SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
    protected Set<IDataElement> locationDataElements = new HashSet<IDataElement>();

    protected void addedNewFileToModels(File file) throws DatabaseException, DuplicateNodeNameException {
        model.addFile(file);
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.DRIVE);
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get(CONFIG_VALUE_DATASET));
            model = getActiveProject().getDataset(configuration.getDatasetNames().get(CONFIG_VALUE_DATASET), DriveTypes.TEMS);
            modelMap.put(configuration.getDatasetNames().get(CONFIG_VALUE_DATASET), model);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
    }

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
        final int nodeHours = nodeDate.getHours();
        workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
        workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
        workDate.set(Calendar.SECOND, nodeDate.getSeconds());
        final long timestamp = workDate.getTimeInMillis();
        return timestamp;
    }

    protected IDataElement checkSameLocation(Map<String, Object> params) {
        for (IDataElement location : locationDataElements) {
            if (location.get(LATITUDE).equals(params.get(LATITUDE)) && location.get(LONGITUDE).equals(params.get(LONGITUDE))) {
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
