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

package org.amanzi.awe.log4j;

import java.io.IOException;

import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin that controls work with Log4j 
 * 
 * @author gerzog
 * @since 1.0.0
 */
/**
 * The activator class controls the plug-in life cycle
 */
public class LogStarter extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.apache.log4j";

    // The shared instance
    private static LogStarter plugin;
    
    /**
     * The constructor
     */
    public LogStarter() {

    }
    
    private void initializeLogger() throws IOException {
        //using different Log4j configs for different modes
        if (Platform.inDevelopmentMode()) {        
            DOMConfigurator.configure(FileLocator.toFileURL(getBundle().getEntry("/log4j-development.xml")));
        }
        else if (Platform.inDebugMode()) {
            DOMConfigurator.configure(FileLocator.toFileURL(getBundle().getEntry("/log4j-debug.xml")));
        }
        else {
            DOMConfigurator.configure(FileLocator.toFileURL(getBundle().getEntry("/log4j-production.xml")));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        try {
            initializeLogger();
        }
        catch (IOException e) {
            getLog().log(new Status(Status.ERROR, PLUGIN_ID, "Log4j was not initialized", e));
        }
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

