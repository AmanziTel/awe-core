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

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.model.IModel;
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
    public static final String CONFIG_VALUE_PESQ = "PESQ";

    /**
     * action threshold for commit
     */
    private int txBeforeCommit;
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
        txBeforeCommit = count;
    }

    /**
     * if current transaction==null create new transaction instance;Also finish current transaction
     * if actions in current transaction more than <code>txBeforeCommit</code> after closing - open
     * new transaction;
     */
    protected void txCommit() {
        if (actionCount > txBeforeCommit) {
            tx.success();
            tx.finish();
            tx = null;
            actionCount = 0;
        }
        if (tx == null) {
            tx = database.beginTx();
        }

    }

    /**
     * mark transaction as failure and finish it
     */
    protected void txRollBack() {
        tx.failure();
        tx.finish();
        tx = null;
    }

    @Override
    public void finishUp() {
        tx.success();
        tx.finish();
        NeoServiceProvider.getProvider().commit();
        actionCount = 0;
    }
}
