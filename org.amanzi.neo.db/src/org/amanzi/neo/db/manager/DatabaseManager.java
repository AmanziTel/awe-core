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

package org.amanzi.neo.db.manager;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

/**
 * Manager to get Access Type for Database
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class DatabaseManager {
    
    /**
     * Type of Database Access
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
    public enum DatabaseAccessType {
        /**
         * Standard Embedded NeoService
         */
        EMBEDDED,
        
        /**
         * Service-based BatchInserter
         */
        BATCH;
    }
    private final ReadWriteLock rwl=new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    /*
     * Current Database Service
     */
    private GraphDatabaseService databaseService;
    
    /*
     * Current Index Service
     */
    private IndexService indexService; 
    
    /*
     * Batch Inserter
     */
    private BatchInserter batchInserter;    
    
    /*
     * Current type of Access
     */
    private DatabaseAccessType currentAccessType;
    
    /*
     * Instance of Manager
     */
    private static DatabaseManager instance = null;

    private static Object monitor=new Object();
    
    /*
     * Listeners for Database Access Type changes 
     */
    private ArrayList<IDatabaseChangeListener> listeners = new ArrayList<IDatabaseChangeListener>();
    private final DatabaseServiceWrapper databaseWrapper=new DatabaseServiceWrapper(null);
    
    /**
     * Return instance of manager 
     *
     * @return DatabaseManager
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance==null){
                    instance = new DatabaseManager();
                    instance.setDatabaseAccessType(DatabaseAccessType.EMBEDDED);
                }
            }
        }
        return instance;
    }
    
    /**
     * Returns current DatabaseService
     *
     * @return DatabaseService of current Access Type
     */
    public  GraphDatabaseService getCurrentDatabaseService() {
       return databaseWrapper;
    }
    
    /**
     * Changes type of Database Access
     *
     * @param type
     */
    public synchronized void setDatabaseAccessType(DatabaseAccessType type) {
        if ((currentAccessType == null) || (currentAccessType != type)) {
            w.lock();
            databaseWrapper.lockWrite();
            try {
                if (databaseService != null) {
                    if (indexService != null) {
                        indexService.shutdown();
                    }

                    // TODO: Lagutko: temporary workaround until we have another way for database
                    // access
                    if (currentAccessType.equals(DatabaseAccessType.BATCH)) {
                        for (int i = 0; i < 10000; i++) {
                            databaseService.createNode();
                        }
                        databaseService.shutdown();
                    }
                }

                String dbLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();

                switch (type) {
                case BATCH:
                    // TODO: Lagutko: temporary workaround until we have another way for database
                    // access
                    NeoServiceProvider.getProvider().stopNeo();

                    batchInserter = new BatchInserterImpl(dbLocation);
                    databaseService = batchInserter.getGraphDbService();

                    break;
                case EMBEDDED:
                    databaseService = NeoServiceProvider.getProvider().getService();
                    break;
                }
                databaseWrapper.setRealService(databaseService);
                // skip IndexService of current type
                indexService = null;
                currentAccessType = type;
            } finally {
                w.unlock();
                databaseWrapper.writeUnlock();
            }
            fireDatabaseAccessChangeEvent();
        }
    }

    /**
     * Returns IndexService for current Database Access Type
     *
     * @return Index Service
     */
    public IndexService getIndexService() {
        if (indexService == null) {
            switch (currentAccessType) {
            case EMBEDDED:
                indexService = new LuceneIndexService(databaseService);
                break;
            case BATCH:
                LuceneIndexBatchInserter batchLuceneIndex = new LuceneIndexBatchInserterImpl(batchInserter);
                indexService = batchLuceneIndex.getIndexService();
                break;
            }
        }
        
        return indexService;
    }

    /**
     * Adds new Listener for Database Access Event
     *
     * @param newListener
     */
    public void addDatabaseChangeListener(IDatabaseChangeListener newListener) {
        listeners.add(newListener);
    }
    
    /**
     * Fires event about changing Database Access Type
     */
    private void fireDatabaseAccessChangeEvent() {
        for (IDatabaseChangeListener singleListener : listeners) {
            singleListener.onDatabaseAccessChange();
        }
    }

    /**
     *
     * @return
     */
    GraphDatabaseService getRealDatabaseService() {
        r.lock();
        try{
            return databaseService;
        }finally{
            r.unlock();
        }
    }
}
