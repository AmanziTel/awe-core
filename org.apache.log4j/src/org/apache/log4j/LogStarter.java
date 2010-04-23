package org.apache.log4j;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LogStarter extends AbstractUIPlugin {
    private static final Logger LOGGER = Logger.getLogger(LogStarter.class);
	// The plug-in ID
	public static final String PLUGIN_ID = "org.apache.log4j";

	// The shared instance
	private static LogStarter plugin;
	
	/**
	 * The constructor
	 */
	public LogStarter() {

	}
	
    private void initializeLogger(){
        PropertyConfigurator.configure("/log4j.xml");
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	    initializeLogger();
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
	public static LogStarter getDefault() {
		return plugin;
	}

}
