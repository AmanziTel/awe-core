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
import java.util.List;

import org.amanzi.neo.db.internal.DatabasePlugin;
import org.amanzi.neo.db.manager.IDatabaseManager;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.DatabaseEvent.EventType;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * Creates default location for database and returns it's path
     * 
     * @return default path to database location
     */
    public static String getDefaultDatabaseLocation() {

        String location = DatabasePlugin.getInstance().getPreferenceStore()
                .getString(DatabasePlugin.PREFERENCE_KEY_DATABASE_LOCATION);
        if (!StringUtils.isEmpty(location)) {
            return location;
        }

        String userHome = System.getProperty("user.home");

        File databaseDirectory = new File(userHome);
        for (String subDirectory : DEFAULT_DATABASE_LOCATION) {
            databaseDirectory = new File(databaseDirectory, subDirectory);
        }

        if (!databaseDirectory.mkdirs()) {
            LOGGER.fatal("Database directory <" + databaseDirectory + "> was not created");
        }
        location = databaseDirectory.getAbsolutePath();
        // DatabasePlugin.getInstance().getPreferenceStore().setValue(DatabasePlugin.PREFERENCE_KEY_DATABASE_LOCATION,
        // location);
        return location;
    }

    /*
     * Map of Transactions-per-Thread
     */
    private final ThreadLocal<Transaction> transactionMap = new ThreadLocal<Transaction>();

    private final ThreadLocal<Integer> transactionStack = new ThreadLocal<Integer>();
    /*
     * Listeners for Database Events
     */
    private final List<IDatabaseEventListener> listeners = new ArrayList<IDatabaseEventListener>();

    private boolean isAlreadyUsed = false;

    public AbstractDatabaseManager() {

    }

    @Override
    public void addDatabaseEventListener(final IDatabaseEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void commitThreadTransaction() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Commiting Transaction for Thread <" + Thread.currentThread() + ">");
        }

        // commiting current transaction
        Transaction tx = transactionMap.get();
        tx.success();
        tx.finish();

        // creating new one
        tx = getDatabaseService().beginTx();
        transactionMap.set(tx);
    }

    @Override
    public void finishThreadTransaction() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Finishing Transaction for Thread <" + Thread.currentThread() + ">");
        }

        Integer stack = transactionStack.get();

        if (--stack == 0) {
            // commiting current transaction

            Transaction tx = transactionMap.get();
            tx.success();
            tx.finish();

            transactionMap.remove();
        }
        transactionStack.set(stack);
    }

    /**
     * Fires database event for listeners
     * 
     * @param eventType type of event
     */
    protected void fireEvent(final EventType eventType) {
        DatabaseEvent event = new DatabaseEvent(eventType);
        for (IDatabaseEventListener listener : listeners) {
            listener.onDatabaseEvent(event);
        }
    }

    /**
     * @return Returns the isAlreadyUsed.
     */
    @Override
    public boolean isAlreadyUsed() {
        try {
            getDatabaseService();
            isAlreadyUsed = false;
        } catch (java.lang.IllegalStateException e) {
            isAlreadyUsed = true;
        }

        return isAlreadyUsed;
    }

    @Override
    public void removeDatabaseEventListener(final IDatabaseEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void rollbackThreadTransaction() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rolling back Transaction for Thread <" + Thread.currentThread() + ">");
        }

        // commiting current transaction
        Transaction tx = transactionMap.get();
        tx.failure();
        tx.finish();

        // creating new one
        tx = getDatabaseService().beginTx();
        transactionMap.set(tx);
    }

    /**
     * @param isAlreadyUsed The isAlreadyUsed to set.
     */
    protected void setAlreadyUsed(final boolean isAlreadyUsed) {
        this.isAlreadyUsed = isAlreadyUsed;
    }

    @Override
    public void startThreadTransaction() {
        Integer stack = transactionStack.get();
        if (stack == null) {
            stack = 0;
        }

        if (transactionMap.get() != null) {
            LOGGER.error("Transaction for Thread <" + Thread.currentThread() + "> already exists");
        } else {
            // if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating Transaction for Thread <" + Thread.currentThread() + ">");
            // }
            transactionMap.set(getDatabaseService().beginTx());
        }
        transactionStack.set(++stack);
    }

}
