package org.amanzi.neo.loader.core.internal;

import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractProviderPlugin {

    private static final String LOADER_CORE_PLUGIN = "org.amanzi.neo.loader.core";

    private static Activator plugin;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        Activator.plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        super.stop(bundleContext);
        Activator.plugin = null;
    }

    @Override
    public String getPluginId() {
        return LOADER_CORE_PLUGIN;
    }

    public static Activator getInstance() {
        return plugin;
    }

}
