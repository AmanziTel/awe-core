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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.internal.DatabasePlugin;
import org.amanzi.neo.db.manager.events.DatabaseEvent.EventType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

/**
 * Database manager that give access directly to Neo4j without Neoclipse layer
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class Neo4jDatabaseManager extends AbstractDatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(Neo4jDatabaseManager.class);

    /**
     * Default Memory Mapping - empty map
     */
    public static final Map<String, String> DEFAULT_MEMORY_MAPPING = new HashMap<String, String>(0);

    /*
     * Location of database
     */
    private final String databaseLocation;

    /*
     * Access Type of database
     */
    private final AccessType accessType;

    /*
     * Memory mapping parameters
     */
    private final Map<String, String> memoryMapping;

    /*
     * Graph Database Service
     */
    private GraphDatabaseService dbService;

    /**
     * Default constructor All parameters set as a default
     */
    public Neo4jDatabaseManager() {
        this(null, AccessType.getDefaulAccessType(), DEFAULT_MEMORY_MAPPING);
    }

    /**
     * Custom constructor - need only access type of database connection
     * 
     * @param accessType access type of database connection
     */
    public Neo4jDatabaseManager(final AccessType accessType) {
        this(null, accessType);
    }

    /**
     * Custom constructor - set default database location
     * 
     * @param accessType access type for connection
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(final AccessType accessType, final Map<String, String> memoryMapping) {
        this(null, accessType, memoryMapping);
    }

    /**
     * Custom constructor - need only memory mapping parameters
     * 
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(final Map<String, String> memoryMapping) {
        this(null, AccessType.getDefaulAccessType(), memoryMapping);
    }

    /**
     * Custom constructor - need only database location
     * 
     * @param databaseLocation location of database
     */
    public Neo4jDatabaseManager(final String databaseLocation) {
        this(databaseLocation, AccessType.getDefaulAccessType());
    }

    /**
     * Custom constructor - set default Memory Mappings
     * 
     * @param databaseLocation location of database
     * @param accessType access type for connection
     */
    public Neo4jDatabaseManager(final String databaseLocation, final AccessType accessType) {
        this(databaseLocation, accessType, DEFAULT_MEMORY_MAPPING);
    }

    /**
     * Full constructor - need on input all parameters of Database
     * 
     * @param databaseLocation location of database
     * @param accessType access type for connection
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(final String databaseLocation, final AccessType accessType, final Map<String, String> memoryMapping) {
        this.databaseLocation = StringUtils.isEmpty(databaseLocation) ? getDefaultDatabaseLocation() : databaseLocation;
        this.accessType = accessType;
        this.memoryMapping = memoryMapping;
        DatabasePlugin.getInstance().getPreferenceStore()
                .setValue(DatabasePlugin.PREFERENCE_KEY_DATABASE_LOCATION, this.databaseLocation);
        LOGGER.info("Neo4j Database Manager was created with parameters: " + "databaseLocation = <" + databaseLocation + ">, "
                + "accessType = <" + accessType + ">");
    }

    /**
     * Custom constructor - set default Access Type for connection
     * 
     * @param databaseLocation location of database
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(final String databaseLocation, final Map<String, String> memoryMapping) {
        this(databaseLocation, AccessType.getDefaulAccessType(), memoryMapping);
    }

    @Override
    public void commitMainTransaction() {
        LOGGER.info("Commit with Database Manager");

        fireEvent(EventType.BEFORE_FULL_COMMIT);
        // do nothing - Neo4jDatabaseManager have no main transaction.
        // Handling on transaction should be controlled by user
        fireEvent(EventType.AFTER_FULL_COMMIT);
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public synchronized GraphDatabaseService getDatabaseService() {
        if (dbService == null) {
            initializeDb();
        }
        return dbService;
    }

    @Override
    public String getDefaultLocation() {
        return computeDefaultLocation();
    }

    @Override
    public String getLocation() {
        return databaseLocation;
    }

    @Override
    public Map<String, String> getMemoryMapping() {
        return memoryMapping;
    }

    /**
     * Initializes DB connection
     */
    private void initializeDb() {
        LOGGER.info("Initializing Neo4j Database Manager with parameters: " + "databaseLocation = <" + databaseLocation + ">, "
                + "accessType = <" + accessType + ">");

        fireEvent(EventType.BEFORE_STARTUP);
        switch (accessType) {
        case READ_ONLY:
            dbService = new EmbeddedReadOnlyGraphDatabase(databaseLocation, memoryMapping);
            break;
        case READ_WRITE:
        default:
            dbService = new EmbeddedGraphDatabase(databaseLocation, memoryMapping);
            break;
        }

        fireEvent(EventType.AFTER_STARTUP);
    }

    @Override
    public void rollbackMainTransaction() {
        LOGGER.info("Commit with Database Manager");

        fireEvent(EventType.BEFORE_FULL_ROLLBACK);
        // do nothing - Neo4jDatabaseManager have no main transaction.
        // Handling on transaction should be controlled by user
        fireEvent(EventType.AFTER_FULL_ROLLBACK);
    }

    @Override
    public synchronized void setDatabaseService(final GraphDatabaseService service) {
        shutdown();

        fireEvent(EventType.BEFORE_STARTUP);
        dbService = service;
        fireEvent(EventType.AFTER_STARTUP);
    }

    @Override
    public synchronized void shutdown() {
        if (dbService != null) {
            LOGGER.info("Database shutted down");

            fireEvent(EventType.BEFORE_SHUTDOWN);

            dbService.shutdown();

            fireEvent(EventType.AFTER_SHUTDOWN);
        }

        dbService = null;
    }

}
