package org.amanzi.awe.ui;

import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.osgi.framework.BundleContext;

//TODO: LN: 09.08.2012, remove from exported in this plugin
public class AWEUIPlugin extends AbstractProviderPlugin {

    private static final String PLUGIN_ID = "org.amanzi.awe.ui";

    private static AWEUIPlugin instance;

    public AWEUIPlugin() {

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        instance = this;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        instance = null;
        super.stop(bundleContext);
    }

    public static AWEUIPlugin getDefault() {
        return instance;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
