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
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.events.DatabaseEvent.EventType;
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

    private final static Logger LOGGER = Logger.getLogger(Neo4jDatabaseManager.class);

    /**
     * Default Memory Mapping - empty map
     */
    public static final Map<String, String> DEFAULT_MEMORY_MAPPING = new HashMap<String, String>(0);

    /**
     * Default Database Location "user.home"/.amanzi/neo
     */
    private static final String[] DEFAULT_DATABASE_LOCATION = new String[] {".amanzi", "neo"};

    /*
     * Location of database
     */
    private String databaseLocation;

    /*
     * Access Type of database
     */
    private AccessType accessType;

    /*
     * Memory mapping parameters
     */
    private Map<String, String> memoryMapping;

    /*
     * Graph Database Service
     */
    private GraphDatabaseService dbService;
    
    /**
     * Full constructor - need on input all parameters of Database
     * 
     * @param databaseLocation location of database
     * @param accessType access type for connection
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(String databaseLocation, AccessType accessType, Map<String, String> memoryMapping) {
        this.databaseLocation = databaseLocation;
        this.accessType = accessType;
        this.memoryMapping = memoryMapping;

        LOGGER.info("Neo4j Database Manager was created with parameters: " + "databaseLocation = <" + databaseLocation + ">, " + "accessType = <" + accessType + ">");
    }

    /**
     * Custom constructor - set default Memory Mappings
     * 
     * @param databaseLocation location of database
     * @param accessType access type for connection
     */
    public Neo4jDatabaseManager(String databaseLocation, AccessType accessType) {
        this(databaseLocation, accessType, DEFAULT_MEMORY_MAPPING);
    }

    /**
     * Custom constructor - set default Access Type for connection
     * 
     * @param databaseLocation location of database
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(String databaseLocation, Map<String, String> memoryMapping) {
        this(databaseLocation, AccessType.getDefaulAccessType(), memoryMapping);
    }

    /**
     * Custom constructor - set default database location
     * 
     * @param accessType access type for connection
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(AccessType accessType, Map<String, String> memoryMapping) {
        this(getDefaultDatabaseLocation(), accessType, memoryMapping);
    }

    /**
     * Custom constructor - need only database location
     * 
     * @param databaseLocation location of database
     */
    public Neo4jDatabaseManager(String databaseLocation) {
        this(databaseLocation, AccessType.getDefaulAccessType());
    }

    /**
     * Custom constructor - need only access type of database connection
     * 
     * @param accessType access type of database connection
     */
    public Neo4jDatabaseManager(AccessType accessType) {
        this(getDefaultDatabaseLocation(), accessType);
    }

    /**
     * Custom constructor - need only memory mapping parameters
     * 
     * @param memoryMapping memory mapping parameters
     */
    public Neo4jDatabaseManager(Map<String, String> memoryMapping) {
        this(getDefaultDatabaseLocation(), AccessType.getDefaulAccessType(), memoryMapping);
    }

    /**
     * Default constructor All parameters set as a default
     */
    public Neo4jDatabaseManager() {
        this(getDefaultDatabaseLocation(), AccessType.getDefaulAccessType(), DEFAULT_MEMORY_MAPPING);
    }

    @Override
    public GraphDatabaseService getDatabaseService() {
        initializeDb();
        return dbService;
    }

    @Override
    public String getLocation() {
        return databaseLocation;
    }

    @Override
    public Map<String, String> getMemoryMapping() {
        return memoryMapping;
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
    public void rollbackMainTransaction() {
        LOGGER.info("Commit with Database Manager");
        
        fireEvent(EventType.BEFORE_FULL_ROLLBACK);
        // do nothing - Neo4jDatabaseManager have no main transaction.
        // Handling on transaction should be controlled by user
        fireEvent(EventType.AFTER_FULL_ROLLBACK);
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setDatabaseService(GraphDatabaseService service) {
        shutdown();

        fireEvent(EventType.BEFORE_STARTUP);
        dbService = service;
        fireEvent(EventType.AFTER_STARTUP);
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

    /**
     * Initializes DB connection
     */
    private void initializeDb() {
        if (dbService == null) {
            LOGGER.info("Initializing Neo4j Database Manager with parameters: " + "databaseLocation = <" + databaseLocation + ">, " + "accessType = <" + accessType + ">");
            
            fireEvent(EventType.BEFORE_STARTUP);

            switch (accessType) {
            case READ_ONLY:
                dbService = new EmbeddedReadOnlyGraphDatabase(databaseLocation, memoryMapping);
                break;
            case READ_WRITE:
                dbService = new EmbeddedGraphDatabase(databaseLocation, memoryMapping);
                break;
            }
            
            fireEvent(EventType.AFTER_STARTUP);

            // TODO: listeners???????
        }
    }

    @Override
    public void shutdown() {
        if (dbService != null) {
            LOGGER.info("Database shutted down");
            
            fireEvent(EventType.BEFORE_SHUTDOWN);
            
            dbService.shutdown();
            
            fireEvent(EventType.AFTER_SHUTDOWN);
        }

        dbService = null;
    }

    @Override
	public void startThreadTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commitThreadTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollbackThreadTransaction() {
		// TODO Auto-generated method stub
		
	}

}
