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

package org.amanzi.neo.services.factory;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.nodeproperties.NodePropertiesFactory;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.NodeService;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    private INodeService nodeService;

    private ServiceFactory() {
        // do nothing
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }

        return instance;
    }

    public INodeService getNodeService() {
        if (nodeService == null) {
            synchronized (ServiceFactory.class) {
                if (nodeService == null) {
                    nodeService = createNodeService();
                }
            }
        }

        return nodeService;
    }

    private INodeService createNodeService() {
        return new NodeService(getDbService(), NodePropertiesFactory.getInstance().getGeneralNodeProperties());
    }

    private GraphDatabaseService getDbService() {
        return DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
    }
}
