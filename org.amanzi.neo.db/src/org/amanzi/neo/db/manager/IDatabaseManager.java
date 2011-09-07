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
	 *
	 */
    public static enum AccessType {
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
     * Returns Database Service
     *
     * @return
     */
    public GraphDatabaseService getDatabaseService();
    
    /**
     * Returns location of Database
     *
     * @return
     */
    public String getLocation();
    
    /**
     * Returns Memory Mapping parameters
     *
     * @return
     */
    public Map<String, String> getMemoryMapping();
    
    /**
     * Commits all Transactions
     * 
     */
    public void commit();

    /**
     * Rolls back all Transactions
     */
    public void rollback();
    
    /**
     * Returns type of Database Connection
     *
     * @return
     */
    public AccessType getAccessType();
    
    /**
     * Set a Graph Database Service to manager
     * 
     * @param service new graph database service
     */
    public void setDatabaseService(GraphDatabaseService service);
    
    /**
     * Finish up connection to Database
     * 
     */
    public void shutdown();
    
    /**
     * Add new database event listener
     */
    public void addDatabaseEventListener(IDatabaseEventListener listener);
    
    /**
     * Removes new database event listener
     */
    public void removeDatabaseEventListener(IDatabaseEventListener listener);

}

