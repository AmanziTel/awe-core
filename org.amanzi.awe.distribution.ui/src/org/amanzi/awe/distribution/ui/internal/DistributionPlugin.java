package org.amanzi.awe.distribution.ui.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DistributionPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.amanzi.awe.views.distribution";
    // The shared instance
    private static DistributionPlugin plugin;

    /**
     * The constructor
     */
    public DistributionPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static DistributionPlugin getDefault() {
        return plugin;
    }
}
