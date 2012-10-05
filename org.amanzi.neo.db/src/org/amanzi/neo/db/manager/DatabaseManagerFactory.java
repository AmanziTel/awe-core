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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public final class DatabaseManagerFactory {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerFactory.class);

    private static final String NEOCLIPSE_MANAGER_CLASS_NAME = "org.amanzi.neo.db.manager.impl.NeoclipseDatabaseManager";

    private static IDatabaseManager dbManager = null;

    private DatabaseManagerFactory() {
    }

    public static synchronized IDatabaseManager getDatabaseManager() {
        return getDatabaseManager(null, false);
    }

    public static synchronized IDatabaseManager getDatabaseManager(String path, boolean isNeedToReset) {
        if ((dbManager == null) || isNeedToReset) {
            try {
                dbManager = (IDatabaseManager)Class.forName(NEOCLIPSE_MANAGER_CLASS_NAME).newInstance();
            } catch (ClassNotFoundException e) {
                if (StringUtils.isEmpty(path)) {
                    dbManager = new Neo4jDatabaseManager();
                } else {
                    dbManager = new Neo4jDatabaseManager(path);
                }
            } catch (Exception e) {
                LOGGER.fatal("Cannot instantiane Database Manager", e);
            }
        }

        return dbManager;
    }

}
