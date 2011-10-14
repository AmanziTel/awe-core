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
import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.DatasetService;
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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * contains common methods for all savers
 * 
 * @author Kondratenko_Vladislav
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
public abstract class AbstractSaver<T1 extends IModel, T2 extends IData, T3 extends IConfiguration> implements ISaver<T1, T2, T3> {
    public static final String CONFIG_VALUE_PROJECT = "Project";
    public static final String CONFIG_VALUE_NETWORK = "Network";
    public static final String CONFIG_VALUE_DATASET = "Dataset";
    public static final String PROJECT_PROPERTY = "project";
    public static final String CONFIG_VALUE_CALLS = "Calls";
    public static final String CONFIG_VALUE_PESQ = "Pesq";
    protected final static ExportSynonymsManager exportManager = ExportSynonymsManager.getManager();
    protected static DataLoadPreferenceManager preferenceManager = new DataLoadPreferenceManager();
    protected Map<String, String[]> preferenceStoreSynonyms;
    protected Map<String, IModel> modelMap = new HashMap<String, IModel>();
    protected Map<IModel, ExportSynonyms> synonymsMap = new HashMap<IModel, ExportSynonyms>();

    protected void createExportSynonymsForModels() {
        try {
            for (String key : modelMap.keySet()) {
                synonymsMap.put(modelMap.get(key), exportManager.createExportSynonym(modelMap.get(key), ExportSynonymType.DATASET));
            }
        } catch (DatabaseException e) {
            // TODO Handle DatabaseException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    protected void addedDatasetSynonyms(IDataModel model, INodeType nodeType, String propertyName, String synonym) {
        synonymsMap.get(model).addSynonym(nodeType, propertyName, synonym);
    }

    /**
     * save synonyms into database
     */
    private void saveSynonym() {
        if (synonymsMap.isEmpty()) {
            return;
        }
        for (String key : modelMap.keySet()) {
            try {
                exportManager.saveDatasetExportSynonyms(modelMap.get(key), synonymsMap.get(modelMap.get(key)),
                        ExportSynonymType.DATASET);
            } catch (DatabaseException e) {
                // TODO Handle DatabaseException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

    /**
     * action threshold for commit
     */
    private int commitTxCount;
    /**
     * graph database instance
     */
    private GraphDatabaseService database;
    /**
     * top level trasnaction
     */
    private Transaction tx;
    /**
     * transactions count
     */
    private int actionCount;

    /**
     * Initialize database;
     */
    protected void setDbInstance() {
        database = NeoServiceProvider.getProvider().getService();
    }

    /**
     * increase action counter in current tx;
     */
    protected void increaseActionCount() {
        actionCount++;
    }

    /**
     * dataset service instance
     */
    protected static DatasetService datasetService;

    /**
     * initialize dataset service
     * 
     * @return
     */
    protected void getDatasetService() {
        if (datasetService == null) {
            datasetService = new DatasetService();
        }
    }

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
    protected void openOrReopenTx() {
        if ((actionCount > commitTxCount) || (tx != null && actionCount == 0)) {
            tx.finish();
            tx = null;
            actionCount = 0;
        }
        if (tx == null) {
            tx = database.beginTx();
        }

    }

    protected void finishTx() {
        actionCount = 0;
        tx.finish();
        tx = null;
    }

    /**
     * mark transaction as success
     */
    protected void markTxAsSuccess() {
        tx.success();
    }

    /**
     * mark tx as failure
     */
    protected void markTxAsFailure() {
        tx.failure();
    }

    @Override
    public void finishUp() {
        saveSynonym();
        tx.finish();
        NeoServiceProvider.getProvider().commit();
        actionCount = 0;
    }

    protected IProjectModel getActiveProject() throws AWEException {
        return ProjectModel.getCurrentProjectModel();

    }
}
