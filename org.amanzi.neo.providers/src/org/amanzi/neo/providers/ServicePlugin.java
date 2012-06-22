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

package org.amanzi.neo.providers;

import java.util.List;

import org.amanzi.neo.models.IModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ServicePlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(ServicePlugin.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.neo.services";

    // The shared instance
    private static ServicePlugin plugin;

    /**
     * The constructor
     */
    public ServicePlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        initializeModelProviders();

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ServicePlugin getDefault() {
        return plugin;
    }

    public String getName() {
        return PLUGIN_ID;
    }

    protected void initializeModelProviders() {
        Job job = new Job("Initialize ModelProviders for plugin <" + getName() + ">") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                for (Class<IModelProvider< ? extends IModel, IModel>> singleClass : getModelProviderClasses()) {
                    try {
                        ModelProviderFactory.getInstance().registerModelProvider(singleClass);
                    } catch (Exception e) {
                        LOGGER.fatal("Can't initialize ModelProvider <" + singleClass.getSimpleName() + ">", e);

                        getLog().log(
                                new Status(Status.ERROR, PLUGIN_ID, "Can't initialize ModelProvider <"
                                        + singleClass.getSimpleName() + ">", e));
                    }
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

    protected synchronized List<Class<IModelProvider< ? extends IModel, IModel>>> getModelProviderClasses() {
        return null;
    }
}
