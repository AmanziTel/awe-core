package org.amanzi.splash.neo4j.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import org.amanzi.splash.neo4j.database.services.SpreadsheetService;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * User interface plug-in for mini-spreadsheet editor.
 */
public class SplashPlugin extends AbstractUIPlugin {
	static private SplashPlugin plugin;
	
	//Lagutko, 29.07.2009, additional field
	/*
	 * Field for Spreadsheet service
	 */
	private SpreadsheetService spreadsheetService;
	
	/**
	 * Constructor for SplashPlugin.
	 */
	public SplashPlugin() {
		super();
		plugin = this;
		
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//Tsinkel, add resource listener
		org.eclipse.core.resources.ResourcesPlugin.getWorkspace().addResourceChangeListener(new EditorListener(),IResourceChangeEvent.POST_CHANGE);
		//Lagutko, 29.07.2009, initialize SpreadsheetService
		spreadsheetService = new SpreadsheetService();
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
	
	public SpreadsheetService getSpreadsheetService() {
	    return spreadsheetService;
	}
	
	/**
     * Print a message and information about exception to Log
     *
     * @param message message
     * @param e exception
     */

    public static void error(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, getId(), 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }
}