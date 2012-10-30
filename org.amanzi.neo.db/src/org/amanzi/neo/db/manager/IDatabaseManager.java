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

import java.util.Map;

import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Interface that represents methods for access to Database
 * 
 * @author gerzog
 * @since 1.0.0
 */
public interface IDatabaseManager {

    /**
     * Access Type of database connection
     * 
     * @author gerzog
     */
    public enum AccessType {
        /*
         * Read-only access
         */
        READ_ONLY,

        /*
         * Read-write access
         */
        READ_WRITE;

        /**
         * Returns default Access Type
         * 
         * @return READ_WRITE as default AccessType
         */
        public static AccessType getDefaulAccessType() {
            return READ_WRITE;
        }
    }

    /**
     * Add new database event listener
     */
    void addDatabaseEventListener(IDatabaseEventListener listener);

    /**
     * Commits all Transactions
     */
    void commitMainTransaction();

    /**
     * Commit transaction in current Thread
     */
    void commitThreadTransaction();

    /**
     * Finishes transaction in current Thread
     */
    void finishThreadTransaction();

    /**
     * Returns type of Database Connection
     * 
     * @return
     */
    AccessType getAccessType();

    /**
     * Returns Database Service
     * 
     * @return
     */
    GraphDatabaseService getDatabaseService();

    String getDefaultLocation();

    /**
     * Returns location of Database
     * 
     * @return
     */
    String getLocation();

    /**
     * Returns Memory Mapping parameters
     * 
     * @return
     */
    Map<String, String> getMemoryMapping();

    /**
     * return true if database location is already used
     * 
     * @return
     */
    boolean isAlreadyUsed();

    /**
     * Removes new database event listener
     */
    void removeDatabaseEventListener(IDatabaseEventListener listener);

    /**
     * Rolls back all Transactions
     */
    void rollbackMainTransaction();

    /**
     * Rollsback transaction in current Thread
     */
    void rollbackThreadTransaction();

    /**
     * Set a Graph Database Service to manager
     * 
     * @param service new graph database service
     */
    void setDatabaseService(GraphDatabaseService service);

    /**
     * Finish up connection to Database
     */
    void shutdown();

    /**
     * Start transaction in current Thread
     */
    void startThreadTransaction();
}
