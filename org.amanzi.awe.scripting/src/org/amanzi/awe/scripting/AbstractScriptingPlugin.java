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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.scripting.utils.ScriptUtils;
import org.amanzi.awe.scripting.utils.ScriptingException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.jcodings.specific.UTF8Encoding;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
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
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractScriptingPlugin.class);
    /*
     * constants definition
     */
    private static final String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath();
    private static final String PROJECT_FOLDER = "awe-scripts";
    private static final String RUBY_SCRIPT_FOLDER = "/ruby";

    /**
     * wrapper for runtime instance
     */
    private JRubyRuntimeWrapper runtimeWrapper;
    private ScriptingManager manager = new ScriptingManager();

    /**
     * initialize plugin
     */
    protected abstract void initPlugin();

    /**
     * get list of project folder content
     * 
     * @param projectName
     * @return project folder not exist, in other case return list of files
     */
    public List<File> getScriptsForProject(String projectName) {
        File projectFolder = new File(AbstractScriptingPlugin.WORKSPACE_FOLDER + File.separator
                + AbstractScriptingPlugin.PROJECT_FOLDER + File.separator + projectName);
        if (!projectFolder.exists()) {
            LOGGER.info("project folder " + projectName + " doesn't exist");
            return null;
        }
        return Arrays.asList(projectFolder.listFiles());
    }

    /**
     * initialize script manager
     * 
     * @param context
     * @throws IOException
     */
    public void initScriptManager(BundleContext context) throws IOException {
        if (RUBY_SCRIPT_FOLDER.equalsIgnoreCase(getScriptPath())) {
            LOGGER.error("undefined project folder", new IOException(" undefined project folder "));
        }
        try {
            URL workspaceName = context.getBundle().getEntry(getScriptPath());
            URL workspaceLocator = FileLocator.toFileURL(workspaceName);
            LOGGER.info("Start workspace initializing");
            manager.initWorkspace(workspaceLocator);
            LOGGER.info("Start file copying");
            manager.copyScripts();
        } catch (IOException e) {
            LOGGER.error("Error in wokspace preparator", e);
            throw e;
        }
    }

    /**
     * initialize runtime with default plugin workspace folder
     * 
     * @throws ScriptingException
     */
    public void initRuntime() throws ScriptingException {
        initRuntime(manager.getDestination());
    }

    /**
     * initialize ruby runtime
     * 
     * @throws IOException
     */
    public void initRuntime(File scriptFolder) throws ScriptingException {
        try {
            Ruby runtime;
            RubyInstanceConfig config = new RubyInstanceConfig() {
                {
                    setJRubyHome(ScriptUtils.getInstance().getJRubyHome());
                    setObjectSpaceEnabled(true);
                    setLoader(getClassLoader());
                }
            };
            runtime = Ruby.newInstance(config);
            runtime.setDefaultExternalEncoding(UTF8Encoding.INSTANCE);
            runtime.setDefaultInternalEncoding(UTF8Encoding.INSTANCE);
            runtime.getLoadService().init(ScriptUtils.getInstance().makeLoadPath(scriptFolder.getAbsolutePath()));
            runtimeWrapper = new JRubyRuntimeWrapper(runtime, scriptFolder);
        } catch (Exception e) {
            LOGGER.error("Error in runtime initialisation", e);
            throw new ScriptingException(e);
        }
    }

    /**
     * @return Returns the runtimeWrapper.
     * @throws ScriptingException
     */
    public JRubyRuntimeWrapper getRuntimeWrapper() throws ScriptingException {
        initRuntime();
        return runtimeWrapper;
    }

    /**
     * return class loader for activator plugin
     * 
     * @return
     */
    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    /**
     * script folder path; by default @return ruby;
     * 
     * @return
     */
    public String getScriptPath() {
        return RUBY_SCRIPT_FOLDER;
    }

    /**
     * <p>
     * Script management utils - purposed for control of script file and definition script
     * directories
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static class ScriptingManager {
        private File source = null;
        private File destination = null;

        /**
         * initialize scripts workspace;
         * 
         * @param rubyScriptingFolder
         * @return false if workspace is already exist, true- if newly created
         * @throws IOException
         */
        public boolean initWorkspace(URL rubyScriptingFolder) throws IOException {
            source = null;
            destination = null;

            File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
            if (!projectFolder.exists()) {
                FileUtils.forceMkdir(projectFolder);
            }
            source = new File(rubyScriptingFolder.getPath());
            String scriptFolderName = rubyScriptingFolder.getFile();
            scriptFolderName = scriptFolderName.substring(0, scriptFolderName.length() - 1);
            scriptFolderName = scriptFolderName.substring(scriptFolderName.lastIndexOf(File.separator) + 1,
                    scriptFolderName.length());
            boolean isExist = false;
            for (File existingName : projectFolder.listFiles()) {
                if (existingName.getName().equals(scriptFolderName)) {
                    destination = existingName;
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                return false;
            }
            String createScriptFolder = projectFolder.getAbsolutePath() + File.separator + scriptFolderName;
            destination = new File(createScriptFolder);
            FileUtils.forceMkdir(destination);
            return true;
        }

        /**
         * copy directory from source to
         * 
         * @throws IOException
         */
        public void copyScripts() throws IOException {
            FileFilter fileFilter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    final String name = pathname.getName();
                    return name.endsWith(".rb");
                }
            };
            FileUtils.copyDirectory(source, destination, fileFilter);
        }

        /**
         * @return Returns the destination.
         */
        public File getDestination() {
            return destination;
        }
    }
}
