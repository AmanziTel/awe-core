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
    
    private static final String NEOCLIPSE_MANAGER_CLASS_NAME = "org.amanzi.neo.db.manager.impl.NeoclipseDatabaseManager";

    public static IDatabaseManager getDatabaseManager() {
        if (dbManager == null) {
        	try {
        		dbManager = (IDatabaseManager)Class.forName(NEOCLIPSE_MANAGER_CLASS_NAME).newInstance();
        	} catch (ClassNotFoundException e) {
                dbManager = new Neo4jDatabaseManager();
            } catch (IllegalAccessException e) {
            	e.printStackTrace();
            } catch (InstantiationException e) {
            	e.printStackTrace();
            }
        }

        return dbManager;
    }

}
