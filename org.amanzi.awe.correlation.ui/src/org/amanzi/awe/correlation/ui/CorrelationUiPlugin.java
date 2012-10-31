package org.amanzi.awe.correlation.ui;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class CorrelationUiPlugin extends Plugin {
    private static final String PLUGIN_ID = "org.amanzi.awe.correlation.ui";
    private static CorrelationUiPlugin plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static CorrelationUiPlugin getDefault() {
        return plugin;
    }

    /**
     * The constructor
     */
    public CorrelationUiPlugin() {
    }

    public String getPluginId() {
        return PLUGIN_ID;
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

}
