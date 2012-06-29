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

package org.amanzi.neo.providers.internal;

import org.amanzi.neo.providers.IModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.IProviderContext;
import org.amanzi.neo.providers.IProviderContext.ContextException;
import org.amanzi.neo.providers.context.ProviderContextImpl;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractProviderPlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(AbstractProviderPlugin.class);

    private static final String PROJECT_MODEL_PROVIDER_ID = "org.amanzi.providers.ProjectModelProvider";

    private static class ProviderContextHolder {
        private static volatile IProviderContext context = new ProviderContextImpl();
    }

    protected IProviderContext getContext() {
        return ProviderContextHolder.context;
    }

    private <T extends IModelProvider< ? , ? >> T getModelProvider(String id) {
        try {
            return getContext().get(id);
        } catch (ContextException e) {
            logError(e);

            PlatformUI.getWorkbench().close();
        }

        return null;
    }

    private void logError(ContextException e) {
        String message = "An error occured on initialization Provider context";

        LOGGER.fatal(message, e);

        getLog().log(new Status(Status.ERROR, getPluginId(), message, e));
    }

    public IProjectModelProvider getProjectModelProvider() {
        return getModelProvider(PROJECT_MODEL_PROVIDER_ID);
    }

    public abstract String getPluginId();
}
