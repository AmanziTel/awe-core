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

import org.amanzi.neo.db.manager.IDatabaseManager;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Database Manager that give access to Neo4j using Neoclipse
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class NeoclipseDatabaseManager implements IDatabaseManager {
	
	@Override
    public GraphDatabaseService getDatabaseService() {
        return null;
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
    public void commit() {
    }

    @Override
    public void rollback() {
    }

    @Override
    public AccessType getAccessType() {
        return null;
    }

	@Override
	public void setDatabaseService(GraphDatabaseService service) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void shutdown() {
    }

}
