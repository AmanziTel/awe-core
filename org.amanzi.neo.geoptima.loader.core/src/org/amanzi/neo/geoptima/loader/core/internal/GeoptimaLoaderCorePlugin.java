package org.amanzi.neo.geoptima.loader.core.internal;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

public class GeoptimaLoaderCorePlugin extends AbstractScriptingPlugin {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(GeoptimaLoaderCorePlugin.class);

    public static final String ID = "org.amanzi.neo.geoptima.loader.core";
    private static GeoptimaLoaderCorePlugin plugin;

    private static void initPlugin(final GeoptimaLoaderCorePlugin plug) {
        plugin = plug;
    }

    @Override
    public void start(final BundleContext context) throws ScriptingException {
        try {
            super.start(context);
            initPlugin(this);
        } catch (Exception e) {
            LOGGER.error("Activator starting problem ", e);
            throw new ScriptingException(e);
        }
    }

    public static GeoptimaLoaderCorePlugin getDefault() {
        return plugin;
    }

    @Override
    protected String getPluginName() {
        return ID;
    }

}
