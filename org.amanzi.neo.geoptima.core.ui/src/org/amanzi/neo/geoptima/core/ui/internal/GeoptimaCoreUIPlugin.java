package org.amanzi.neo.geoptima.core.ui.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class GeoptimaCoreUIPlugin extends AbstractUIPlugin {

    private static final String PLUGIN_ID = "org.amanzi.neo.geoptima.core";

    private static GeoptimaCoreUIPlugin instance;

    public static GeoptimaCoreUIPlugin getDefault() {
        return instance;
    }

    public GeoptimaCoreUIPlugin() {

    }

    public String getPluginId() {
        return PLUGIN_ID;
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

}
