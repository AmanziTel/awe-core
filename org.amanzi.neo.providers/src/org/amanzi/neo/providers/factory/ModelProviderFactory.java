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

package org.amanzi.neo.providers.factory;

import org.amanzi.neo.nodeproperties.NodePropertiesFactory;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.impl.ProjectModelProvider;
import org.amanzi.neo.services.factory.ServiceFactory;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class ModelProviderFactory {

    private static volatile ModelProviderFactory instance;

    private volatile IProjectModelProvider projectModelProvider;

    private ServiceFactory serviceFactory;

    private NodePropertiesFactory nodePropertiesFactory;

    private ModelProviderFactory() {
        serviceFactory = ServiceFactory.getInstance();
        nodePropertiesFactory = NodePropertiesFactory.getInstance();
    }

    public static ModelProviderFactory getInstance() {
        if (instance == null) {
            synchronized (ModelProviderFactory.class) {
                if (instance == null) {
                    instance = new ModelProviderFactory();
                }
            }
        }

        return instance;
    }

    public IProjectModelProvider getProjectModelProvider() {
        if (projectModelProvider == null) {
            synchronized (ModelProviderFactory.class) {
                if (projectModelProvider == null) {
                    projectModelProvider = new ProjectModelProvider(serviceFactory.getNodeService(), nodePropertiesFactory.getGeneralNodeProperties());
                }

            }
        }

        return projectModelProvider;
    }

}
