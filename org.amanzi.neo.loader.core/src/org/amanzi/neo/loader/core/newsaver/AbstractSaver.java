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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.synonyms.ExportSynonymsManager;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonymType;
import org.amanzi.neo.services.synonyms.ExportSynonymsService.ExportSynonyms;
import org.apache.log4j.Logger;

/**
 * contains common methods for all savers
 * 
 * @author Kondratenko_Vladislav
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
public abstract class AbstractSaver<T1 extends IModel, T2 extends IData, T3 extends IConfiguration> implements ISaver<T1, T2, T3> {
    private static final Logger LOGGER = Logger.getLogger(AbstractSaver.class);
    // constants
    protected static final String CONFIG_VALUE_CALLS = "Calls";
    protected static final String CONFIG_VALUE_PESQ = "Pesq";
    protected static final char DOT_SEPARATOR = '.';

    protected final static ExportSynonymsManager exportManager = ExportSynonymsManager.getManager();
    // instance of prefernece meneger for getting synonyms
    protected static DataLoadPreferenceManager preferenceManager = new DataLoadPreferenceManager();
    protected Map<String, String[]> preferenceStoreSynonyms;

    // variables required for export synonyms saving
    protected List<IDataModel> useableModels = new LinkedList<IDataModel>();
    protected Map<IModel, ExportSynonyms> synonymsMap = new HashMap<IModel, ExportSynonyms>();

    /*
     * Database Manager
     */
    static IDatabaseManager dbManager = DatabaseManagerFactory.getDatabaseManager();

    /**
     * create class instance
     */
    protected AbstractSaver() {

    }

    /**
     * this method try to parse String propValue if its type is unknown
     * 
     * @param propertyValue - String propValue
     * @return Object parseValue
     */
    protected Object autoParse(String propertyName, String propertyValue) {
        try {
            Object predifinedCheck = checkInPredifined(propertyName, propertyValue);
            if (predifinedCheck != null) {
                return predifinedCheck;
            }
            char separator = DOT_SEPARATOR;
            if (propertyValue.indexOf(separator) != -1) {
                Float floatValue = Float.parseFloat(propertyValue);
                if (floatValue.toString().length() < propertyValue.length()) {
                    return Double.parseDouble(propertyValue);
                } else {
                    return floatValue;
                }
            } else {
                try {
                    return Integer.parseInt(propertyValue);
                } catch (NumberFormatException e) {
                    return Long.parseLong(propertyValue);
                }
            }
        } catch (Exception e) {
            if (propertyValue.equalsIgnoreCase(Boolean.TRUE.toString())) {
                return Boolean.TRUE;
            } else if (propertyValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return Boolean.FALSE;
            }
            return propertyValue;
        }

    }

    /**
     * @param propertyName
     * @param propertyValue
     * @return
     */
    @SuppressWarnings("static-access")
    private Object checkInPredifined(String propertyName, String propertyValue) {
        Object parsedValue = null;
        if (preferenceManager.predifinedPropertyType.containsKey(propertyName)) {
            switch (preferenceManager.predifinedPropertyType.get(propertyName)) {
            case DOUBLE:
                parsedValue = Double.parseDouble(propertyValue);
                break;
            case FLOAT:
                parsedValue = Float.parseFloat(propertyValue);
                break;
            case INTEGER:
                parsedValue = Integer.parseInt(propertyValue);
                break;
            case LONG:
                parsedValue = Long.parseLong(propertyValue);
                break;
            case STRING:
                parsedValue = propertyValue;
                break;
            }
        }
        return parsedValue;
    }

    protected void createExportSynonymsForModels() throws DatabaseException {
        try {
            for (IModel model : useableModels) {
                synonymsMap.put(model, exportManager.createExportSynonym(model, ExportSynonymType.DATASET));
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while creating export synonyms for models", e);
            throw new DatabaseException(e);
        }
    }

    protected void addedDatasetSynonyms(IDataModel model, INodeType nodeType, String propertyName, String synonym) {
        if (model.getName() != null && synonym != null) {
            synonymsMap.get(model).addSynonym(nodeType, propertyName, synonym);
        }
    }

    /**
     * save synonyms into database
     * 
     * @throws DatabaseException
     */
    private void saveSynonym() throws DatabaseException {
        if (synonymsMap.isEmpty()) {
            return;
        }
        for (IModel model : useableModels) {
            try {
                exportManager.saveDatasetExportSynonyms(model, synonymsMap.get(model), ExportSynonymType.DATASET);
            } catch (DatabaseException e) {
                LOGGER.error("Error while saving export synonyms for models", e);
                throw new DatabaseException(e);
            }
        }
    }

    /**
     * action threshold for commit
     */
    private int commitTxCount;

    /**
     * transactions count
     */
    private int actionCount;

    /**
     * set how much transactions should gone before reopening
     * 
     * @param count
     */
    protected void setTxCountToReopen(int count) {
        commitTxCount = count;
    }

    /**
     * if current tx==null create new instance finish current transaction if actions in current
     * transaction more than commitTxCount and open new;
     */
    protected void commitTx() {
        if (++actionCount > commitTxCount) {
            dbManager.commitThreadTransaction();
            actionCount = 0;
        }
    }

    protected void rollbackTx() {
        dbManager.rollbackThreadTransaction();
        actionCount = 0;
    }

    @Override
    public void finishUp() throws AWEException {
        for (IDataModel dataModel : useableModels) {
            dataModel.finishUp();
        }
        saveSynonym();
        dbManager.finishThreadTransaction();
        dbManager.commitMainTransaction();
        actionCount = 0;
    }

    protected IProjectModel getActiveProject() throws AWEException {
        return ProjectModel.getCurrentProjectModel();
    }

    @Override
    public void init(T3 configuration, T2 dataElement) throws Exception {
        DatabaseManagerFactory.getDatabaseManager().startThreadTransaction();
    }
}
