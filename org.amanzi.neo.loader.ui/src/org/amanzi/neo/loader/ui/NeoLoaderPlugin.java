/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.loader.ui;

import org.amanzi.awe.console.AweConsolePlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class NeoLoaderPlugin extends AbstractUIPlugin {
    
    /*
     * Plugin variable
     */

    static private NeoLoaderPlugin plugin;
    
    /**
     * Constructor for SplashPlugin.
     */
    public NeoLoaderPlugin() {
        super();
        plugin = this;  
    }
    
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
     * Returns the shared instance.
     */
    public static NeoLoaderPlugin getDefault() {
        return plugin;
    }
    /**
     * Print debug message
     * 
     * @param line
     */
    
    public static void debug(String line) {
        AweConsolePlugin.debug(line);
    }
    
    /**
     * Print info message
     * 
     * @param line
     */
    
    public static void info(String line) {
        AweConsolePlugin.info(line);
    }
    
    /**
     * Print a notification message
     * 
     * @param line
     */
    
    public static void notify(String line) {
        AweConsolePlugin.notify(line);
    }
    
    /**
     * Print an error message
     * 
     * @param line
     */
    
    public static void error(String line) {
        AweConsolePlugin.error(line);
    }
    
    /**
     * Print an exception
     * 
     * @param line
     */
    
    public static void exception(Exception e) {
        AweConsolePlugin.exception(e);
    }
}
