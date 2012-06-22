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

package org.amanzi.neo.services.impl.internal;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractService implements IDatabaseEventListener {

    protected GraphDatabaseService graphDb;

    protected AbstractService(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        DatabaseManagerFactory.getDatabaseManager().addDatabaseEventListener(this);
    }

    @Override
    public void onDatabaseEvent(DatabaseEvent event) {
        switch (event.getEventType()) {
        case BEFORE_SHUTDOWN:
            graphDb = null;
            break;
        case AFTER_STARTUP:
            graphDb = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
            break;
        }
    }

}
