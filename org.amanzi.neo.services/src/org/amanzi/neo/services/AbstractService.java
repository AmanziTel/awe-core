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

package org.amanzi.neo.services;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.IDatabaseChangeListener;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.index.IndexService;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractService implements IDatabaseChangeListener {

    protected GraphDatabaseService databaseService;
    
    public AbstractService() {
        DatabaseManager.getInstance().addDatabaseChangeListener(this);
        this.databaseService = DatabaseManager.getInstance().getCurrentDatabaseService();
    }
    
    public AbstractService(GraphDatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public void onDatabaseAccessChange() {
        this.databaseService = DatabaseManager.getInstance().getCurrentDatabaseService();
    }
    
    public IndexService getIndexService() {
        return DatabaseManager.getInstance().getIndexService();
    }
    
}
