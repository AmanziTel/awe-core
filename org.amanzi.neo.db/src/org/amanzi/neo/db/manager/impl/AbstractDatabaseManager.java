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

package org.amanzi.neo.db.manager.impl;

import java.io.File;
import java.util.ArrayList;

import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.amanzi.neo.db.manager.events.DatabaseEvent.EventType;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Transaction;

/**
 * Abstract DB Manager Contains methods to work with Transactions and Events
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractDatabaseManager implements IDatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(AbstractDatabaseManager.class);

    /**
     * Default Database Location "user.home"/.amanzi/neo
     */
    private static final String[] DEFAULT_DATABASE_LOCATION = new String[] {".amanzi", "neo"};

    /*
     * Map of Transactions-per-Thread
     */
    private ThreadLocal<Transaction> transactionMap = new ThreadLocal<Transaction>();

    /*
     * Listeners for Database Events
     */
    private static ArrayList<IDatabaseEventListener> listeners = new ArrayList<IDatabaseEventListener>();
    
    public AbstractDatabaseManager() {
    	
    }

    @Override
    public void startThreadTransaction() {
        LOGGER.info("Creating Transaction for Thread <" + Thread.currentThread() + ">");

        if (transactionMap.get() != null) {
            LOGGER.error("Transaction for Thread <" + Thread.currentThread() + "> alread exists");
            // TODO: LN: throw Exception
        }

        transactionMap.set(getDatabaseService().beginTx());
    }

    @Override
    public void commitThreadTransaction() {
        LOGGER.info("Commiting Transaction for Thread <" + Thread.currentThread() + ">");

        // commiting current transaction
        Transaction tx = transactionMap.get();
        tx.success();
        tx.finish();

        // creating new one
        tx = getDatabaseService().beginTx();
        transactionMap.set(tx);
    }

    @Override
    public void rollbackThreadTransaction() {
        LOGGER.info("Rolling back Transaction for Thread <" + Thread.currentThread() + ">");

        // commiting current transaction
        Transaction tx = transactionMap.get();
        tx.failure();
        tx.finish();

        // creating new one
        tx = getDatabaseService().beginTx();
        transactionMap.set(tx);
    }

    @Override
    public void finishThreadTransaction() {
        LOGGER.info("Finishing Transaction for Thread <" + Thread.currentThread() + ">");

        // commiting current transaction
        Transaction tx = transactionMap.get();
        tx.success();
        tx.finish();

        transactionMap.remove();
    }

    @Override
    public void addDatabaseEventListener(IDatabaseEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDatabaseEventListener(IDatabaseEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires database event for listeners
     * 
     * @param eventType type of event
     */
    protected void fireEvent(EventType eventType) {
        DatabaseEvent event = new DatabaseEvent(eventType);
        for (IDatabaseEventListener listener : listeners) {
            listener.onDatabaseEvent(event);
        }
    }

    /**
     * Creates default location for database and returns it's path
     * 
     * @return default path to database location
     */
    public static String getDefaultDatabaseLocation() {
        String userHome = System.getProperty("user.home");

        File databaseDirectory = new File(userHome);
        for (String subDirectory : DEFAULT_DATABASE_LOCATION) {
            databaseDirectory = new File(databaseDirectory, subDirectory);
        }

        databaseDirectory.mkdirs();

        return databaseDirectory.getAbsolutePath();
    }

}
