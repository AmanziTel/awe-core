package org.amanzi.splash.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * User interface plug-in for mini-spreadsheet editor.
 */
public class SplashPlugin extends AbstractUIPlugin {
	static private SplashPlugin plugin;
	
	/**
	 * Constructor for SplashPlugin.
	 */
	public SplashPlugin() {
		super();
		plugin = this;
		//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	 * Return the plug-in ID.
	 */	
	public static String getId() {
		return plugin.getBundle().getSymbolicName();
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static SplashPlugin getDefault() {
		return plugin;
	}	
}