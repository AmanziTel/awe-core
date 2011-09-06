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

import org.amanzi.neo.db.manager.IDatabaseManager;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Database manager that give access directly to Neo4j without Neoclipse layer
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class Neo4jDatabaseManager implements IDatabaseManager {
	
	/*package*/ static final Map<String, String> DEFAULT_MEMORY_MAPPING = new HashMap<String, String>(0);
	
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
		this(getDefaultDatabaseLocation(), AccessType.getDefaulAccessType());
	}
	
	/**
	 * Default constructor
	 * 
	 * All parameters set as a default
	 */
	public Neo4jDatabaseManager() {
		this(getDefaultDatabaseLocation(), AccessType.getDefaulAccessType(), DEFAULT_MEMORY_MAPPING);
	}

    @Override
    public GraphDatabaseService getDatabaseService() {
        return null;
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
    public void commit() {
    }

    @Override
    public void rollback() {
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

	@Override
	public void setDatabaseService(GraphDatabaseService service) {
		// TODO Auto-generated method stub
		
	}
	
	/*package*/ static String getDefaultDatabaseLocation() {
		return null;
	}

}
