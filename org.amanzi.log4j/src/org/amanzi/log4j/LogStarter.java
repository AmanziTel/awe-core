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

package org.amanzi.log4j;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;

/**
 * Plugin that controls work with Log4j 
 * 
 * @author gerzog
 * @since 1.0.0
 */
/**
 * The activator class controls the plug-in life cycle
 */
public class LogStarter implements IStartup {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.log4j";

    private void initializeLogger(Bundle log4jBundle) throws IOException {
        //using different Log4j configs for different modes
        if (Platform.inDevelopmentMode()) {        
            DOMConfigurator.configure(FileLocator.toFileURL(log4jBundle.getEntry("/log4j-development.xml")));
        }
        else if (Platform.inDebugMode()) {
            DOMConfigurator.configure(FileLocator.toFileURL(log4jBundle.getEntry("/log4j-debug.xml")));
        }
        else {
            DOMConfigurator.configure(FileLocator.toFileURL(log4jBundle.getEntry("/log4j-production.xml")));
        }
    }

    @Override
    public void earlyStartup() {
        Bundle log4jBundle = Platform.getBundle(PLUGIN_ID);
        
        try {
            initializeLogger(log4jBundle);
        }
        catch (IOException e) {
            System.err.println("Log4j was not initialized");
        }
        
        Logger.getLogger(this.getClass()).info("Log4j was successfully initialized");
    }

}

