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
import org.amanzi.neo.services.IProjectService;
import org.amanzi.neo.services.impl.NodeService;
import org.amanzi.neo.services.impl.ProjectService;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class ServiceFactory {

    private static volatile ServiceFactory instance;

    private volatile INodeService nodeService;

    private volatile IProjectService projectService;

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

    public IProjectService getProjectService() {
        if (projectService == null) {
            synchronized (ServiceFactory.class) {
                if (projectService == null) {
                    projectService = createProjectService();
                }
            }
        }

        return projectService;
    }

    private INodeService createNodeService() {
        return new NodeService(getDbService(), NodePropertiesFactory.getInstance().getGeneralNodeProperties());
    }

    private IProjectService createProjectService() {
        return new ProjectService(getDbService());
    }

    private GraphDatabaseService getDbService() {
        return DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
    }
}
