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

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.graphdb.GraphDbServiceManager;
import org.neo4j.neoclipse.graphdb.GraphRunnable;

/**
 * Database Manager that give access to Neo4j using Neoclipse
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class NeoclipseDatabaseManager extends AbstractDatabaseManager {
    
    /**
     * Neoclipse Task to get Database Service
     * 
     * @author gerzog
     * @since 1.0.0
     */
    private class GetDatabaseTask implements GraphRunnable {

        @Override
        public void run(GraphDatabaseService graphDb) {
            databaseService = graphDb;
        }
        
    }
    
    /*
     * Neoclipse Database manager
     */
    private GraphDbServiceManager neoclipseManager;
    
    /*
     * Graph Database Service
     */
    private GraphDatabaseService databaseService;
    
    /*
     * Constructor for Database Manager
     */
    public NeoclipseDatabaseManager() {
        neoclipseManager = Activator.getDefault().getGraphDbServiceManager();
    }
	
	@Override
    public GraphDatabaseService getDatabaseService() {
        if (databaseService == null) {
        	try {
        		neoclipseManager.startGraphDbService();
        		//wait until db started
        		do {
        			//wait a little
        			Thread.sleep(100);
        		} while (!neoclipseManager.isRunning());
        		
        		neoclipseManager.executeTask(new GetDatabaseTask(), "Get Database Service");
        	} catch (Exception e) {
        		//TODO: handle error
        	}
        }
        
        return databaseService;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public Map<String, String> getMemoryMapping() {
        return null;
    }

    @Override
    public void commitMainTransaction() {
        neoclipseManager.commit();
    }

    @Override
    public void rollbackMainTransaction() {
        neoclipseManager.rollback();
    }

    @Override
    public AccessType getAccessType() {
        return null;
    }

	@Override
	public void setDatabaseService(GraphDatabaseService service) {
	    throw new UnsupportedOperationException("Neoclipse Database Manager have not possibility to set Graph Database Service");
	}

    @Override
    public void shutdown() {
        neoclipseManager.shutdownGraphDbService();
    }

}
