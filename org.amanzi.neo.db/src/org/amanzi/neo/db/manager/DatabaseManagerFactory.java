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

import org.amanzi.neo.db.manager.impl.Neo4jDatabaseManager;
import org.amanzi.neo.db.manager.impl.NeoclipseDatabaseManager;
import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DatabaseManagerFactory {

    private static IDatabaseManager dbManager = null;

    public static IDatabaseManager getDatabaseManager() {
        if (dbManager == null) {
            if (Platform.getBundle("org.neo4j.neoclipse") == null) {
                dbManager = new Neo4jDatabaseManager();
            } else {
                dbManager = new NeoclipseDatabaseManager();
            }
        }
        return dbManager;
    }
}
