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

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Intereface that represents methods for access to Database
 * 
 * @author gerzog
 * @since 1.0.0
 */
public interface IDatabaseManager {
    
    public static enum AccessType {
        READ_ONLY,
        READ_WRITE;
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

}

