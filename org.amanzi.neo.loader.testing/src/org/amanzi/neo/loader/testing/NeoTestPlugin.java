package org.amanzi.neo.loader.testing;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeoTestPlugin extends AbstractUIPlugin {
    // database postxix
    public static final String DB_POSTFIX = "_test";
	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.neo.loader.testing";

	// The shared instance
	private static NeoTestPlugin plugin;
    private String location;
	
	/**
	 * The constructor
	 */
	public NeoTestPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        String databaseName = "neo_test";
        location = checkDirs(new String[] {System.getProperty("user.home"), ".amanzi", databaseName}).getPath();;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

    public String getDatabaseLocation() {
        return location;
    }
    
    public String getDatabaseLocationWithCheck() {
    	String databaseName = "neo_test";
    	location = checkDirs(new String[] {System.getProperty("user.home"), ".amanzi", databaseName}).getPath();;
        return location;
    }
    
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NeoTestPlugin getDefault() {
		return plugin;
	}

    // todo checl if this method is actually needed
    private File checkDirs(String[] path) {
        File dir = new File(path[0]);
        for (int i = 1; i < path.length; i++) {
            dir = checkDirs(dir, path[i]);
        }
        return dir;
    }

    private File checkDirs(File root, String dirName) {
        File dir = new File(root, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            // TODO system.err shouldn't be used!
            System.err.println(dir.getPath() + " is not a directory");
        }
        return dir;
    }
}
