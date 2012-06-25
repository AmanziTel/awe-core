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

package org.amanzi.awe.scripting.testing;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;
import org.amanzi.awe.scripting.utils.ScriptingException;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

/**
 * Fake activator for testing
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TestActivator extends AbstractScriptingPlugin {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(TestActivator.class);

    public static final String ID = "org.amanzi.awe.scripting.testing";
    public static final String SCRIPT_PATH = "ruby/netview/";
    private static TestActivator plugin;

    private static void initPlugin(TestActivator plug) {
//        if (plugin == null) {
            plugin = plug;
//        }
    }

    @Override
    public void start(BundleContext context) throws ScriptingException {
        try {
            super.start(context);
            initPlugin(this);
            initScriptManager(context);
        } catch (Exception e) {
            LOGGER.error("Activator starting problem ", e);
            throw new ScriptingException(e);
        }
    }

    @Override
    public String getScriptPath() {
        return SCRIPT_PATH;
    }

    public static TestActivator getDefault() {
        return plugin;
    }

    @Override
    protected void initPlugin() {

    }

}
