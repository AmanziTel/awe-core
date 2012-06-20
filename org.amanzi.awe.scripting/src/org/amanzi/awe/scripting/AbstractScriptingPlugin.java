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

package org.amanzi.awe.scripting;

import java.io.IOException;
import java.net.URL;

import org.amanzi.awe.scripting.manager.ScriptingManager;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * activate JrubyScripting
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractScriptingPlugin extends Plugin {
    private URL workspaceName;
    private final static String rubyScriptFolder = "/ruby";
    private ScriptingManager manager;

    /**
     * should be invoked to define script folder
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        initScriptManager(context);
    }

    /**
     * initialise script manager
     * 
     * @param context
     * @throws IOException
     */
    public void initScriptManager(BundleContext context) throws Exception {
        if (rubyScriptFolder.equalsIgnoreCase(getScriptPath())) {
            throw new IOException("undefined project folder");
        }
        workspaceName = context.getBundle().getEntry(getScriptPath());
        URL workspaceLocator = FileLocator.toFileURL(workspaceName);
        manager = new ScriptingManager(workspaceLocator);
        manager.copyScripts();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    /**
     * script folder path; by default @return ruby;
     * 
     * @return
     */
    public String getScriptPath() {
        return rubyScriptFolder;
    }
}
