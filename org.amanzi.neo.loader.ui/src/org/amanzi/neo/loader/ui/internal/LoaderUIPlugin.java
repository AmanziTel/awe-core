package org.amanzi.neo.loader.ui.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoaderUIPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.neo.loader.ui"; //$NON-NLS-1$

    private static final String DEFAULT_LOAD_PATH = "default_load_path";

    // The shared instance
    private static LoaderUIPlugin plugin;

    /**
     * The constructor
     */
    public LoaderUIPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static LoaderUIPlugin getDefault() {
        return plugin;
    }

    public String getDefaultLoadPath() {
        return getPreferenceStore().getString(DEFAULT_LOAD_PATH);
    }

    public void setDefaultLoadPath(String path) {
        getPreferenceStore().setValue(DEFAULT_LOAD_PATH, path);
    }
}
