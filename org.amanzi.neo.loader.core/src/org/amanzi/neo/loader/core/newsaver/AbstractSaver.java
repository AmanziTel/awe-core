package org.amanzi.neo.loader.core.newsaver;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.model.IModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public abstract class AbstractSaver<T1 extends IModel, T2 extends IData, T3 extends IConfiguration> implements ISaver<T1, T2, T3> {
    public static final String CONFIG_VALUE_PROJECT = "Project";
    public static final String CONFIG_VALUE_NETWORK = "Network";
    public static final String CONFIG_VALUE_DATASET = "Dataset";
    public static final String PROJECT_PROPERTY = "project";
    public static final String CONFIG_VALUE_CALLS = "Calls";
    public static final String CONFIG_VALUE_PESQ = "PESQ";

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
    private int currentTxCount;

    protected void setDbInstance() {
        database = NeoServiceProvider.getProvider().getService();
    }

    protected void increaseTxCount() {
        currentTxCount++;
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
     * if current tx==null create new instance finish current transaction if placebo transaction
     * count more than commitTxCount and open new;
     */
    protected void openOrReopenTx() {
        if (currentTxCount > commitTxCount) {
            tx.finish();
            tx = null;
            currentTxCount = 0;
        }
        if (tx == null) {
            tx = database.beginTx();
        }

    }

    protected void finishTx() {
        tx.finish();
    }

    /**
     * mark transaction as success
     */
    protected void markTxAsSuccess() {
        tx.success();
    }

    /**
     * mar tx as failure
     */
    protected void markTxAsFailure() {
        tx.failure();
    }

    @Override
    public void finishUp() {
        tx.finish();
        NeoServiceProvider.getProvider().commit();
        currentTxCount = 0;
    }
}
