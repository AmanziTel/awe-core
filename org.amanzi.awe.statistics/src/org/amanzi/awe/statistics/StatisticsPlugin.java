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

package org.amanzi.awe.statistics;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsPlugin extends AbstractScriptingPlugin {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsPlugin.class);

    public static final String ID = "org.amanzi.awe.statistics";
    public static final String SCRIPT_PATH = "ruby/netview/";
    private static StatisticsPlugin plugin;

    private static void initPlugin(StatisticsPlugin plug) {
        plugin = plug;
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

    public static StatisticsPlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initPlugin() {

    }

}
