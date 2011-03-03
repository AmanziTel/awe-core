package org.amanzi.awe.statistics;

import java.io.IOException;
import java.net.URL;

import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class StatisticPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.statistics";

	// The shared instance
	private static StatisticPlugin plugin;
	
	/**
	 * The constructor
	 */
	public StatisticPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		initializeRuby();
	}

	private void initializeRuby() {
        try {
            URL statPluginPath = FileLocator.resolve(StatisticPlugin.getDefault().getBundle().getEntry("/"));
            System.out.println("Path to statistics plugin: "+statPluginPath.getFile());
            KPIPlugin.getDefault().getRubyRuntime().evalScriptlet("$LOAD_PATH<<\""+statPluginPath.getFile()+"\";puts $LOAD_PATH.join(\"\n\")");
            System.out.println("Path added");
            URL fileURL = FileLocator.toFileURL(StatisticPlugin.getDefault().getBundle().getEntry("ruby/builder.rb"));
            System.out.println("Path to builder file: "+fileURL.getPath());
            KPIPlugin.getDefault().getRubyRuntime().evalScriptlet(ScriptUtils.getScriptContent(fileURL.getPath()));
            System.out.println("Builder is initialized");
        } catch (Exception e) {
            // TODO Handle IOException
            e.printStackTrace();
        }
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
	public static StatisticPlugin getDefault() {
		return plugin;
	}

}
