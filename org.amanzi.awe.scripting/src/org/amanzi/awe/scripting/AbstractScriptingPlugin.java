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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.scripting.utils.ScriptUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.jcodings.specific.UTF8Encoding;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.osgi.framework.Bundle;
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
    private static final String SCRIPTS_FOLDER = "awe-scripts";
    private static final String RUBY_SCRIPT_FOLDER = "ruby";
    private static final String COMMON_SCRIPTS_FOLDER = "common";
    public static final String PLUGIN_ID = "org.amanzi.awe.scripting";
    private static final String PATH_SEPARATOR = "/";
    private static final String NEO4J_ENTRY = "neo4j";
    private static final String NEO4J_RB_PATH = "/lib/neo4j.rb";
    /**
     * wrapper for runtime instance
     */
    private JRubyRuntimeWrapper runtimeWrapper;
    private final ScriptingManager manager = new ScriptingManager();

    /**
     * initialize plugin
     */
    protected abstract void initPlugin();

    /**
     * initialize runtime with required scripts from ruby/common folder
     * 
     * @param runtime
     * @throws ScriptingException
     * @throws IOException
     */
    protected void initDefaultScript(Bundle bundle, JRubyRuntimeWrapper runtime) throws ScriptingException, IOException {
        URL workspaceName = bundle.getEntry(RUBY_SCRIPT_FOLDER + PATH_SEPARATOR + COMMON_SCRIPTS_FOLDER);
        if (workspaceName == null) {
            LOGGER.info("nothing to initialize in bundle " + bundle.getSymbolicName());
            return;
        }
        URL scripts = FileLocator.toFileURL(workspaceName);
        File scriptsFolder = new File(scripts.getPath());
        for (File script : scriptsFolder.listFiles()) {
            LOGGER.info("Initialize  bundle " + bundle.getSymbolicName() + " with script " + script.getName());
            runtime.executeScript(script);
        }
    }

    @Override
    public void start(BundleContext context) throws ScriptingException {
        try {
            super.start(context);
            manager.initWorkspace();
            initScriptManager(context);
        } catch (Exception e) {
            LOGGER.error("error when trying to initialize default ruby scripts", e);
            throw new ScriptingException(e);
        }
    }

    /**
     * get list of project folder content
     * 
     * @param projectName
     * @return project folder not exist, in other case return list of files
     */
    public List<File> getScriptsForProject(String projectName) {
        File projectFolder = new File(AbstractScriptingPlugin.WORKSPACE_FOLDER + File.separator
                + AbstractScriptingPlugin.SCRIPTS_FOLDER + File.separator + projectName);
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
        try {
            LOGGER.info("Start scripts processing. for plugin" + context.getBundle().getSymbolicName());
            URL workspaceName = context.getBundle().getEntry(RUBY_SCRIPT_FOLDER);
            LOGGER.info("Scripts folder founded in" + workspaceName.getPath());
            URL workspaceLocator = FileLocator.toFileURL(workspaceName);
            LOGGER.info("Start workspace initializing");
            LOGGER.info("Start file copying");
            manager.copySources(workspaceLocator);
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
        initRuntime(manager.getScriptsFolder());
    }

    /**
     * get all available scripts
     * 
     * @return
     */
    public Map<String, File> getAllScripts() {
        return manager.getAllWorkspaceScripts(ScriptingManager.FILE_FILTER_TO_LOAD);
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
            runtimeWrapper = new JRubyRuntimeWrapper(runtime, manager.getScriptsFolder());
            initRuntimeWithDefaultScripts();
        } catch (Exception e) {
            LOGGER.error("Error in runtime initialisation", e);
            throw new ScriptingException(e);
        }
    }

    /**
     * @param runtime
     * @throws ScriptingException
     */
    private void initRuntimeWithDefaultScripts() throws ScriptingException {

        URL scripts;
        try {
            scripts = FileLocator.toFileURL(Platform.getBundle(PLUGIN_ID).getEntry(NEO4J_ENTRY));
            File file = new File(scripts.getPath() + NEO4J_RB_PATH);
            runtimeWrapper.executeScript(file);
            initDefaultScript(Platform.getBundle(PLUGIN_ID), runtimeWrapper);
            initDefaultScript(getBundle(), runtimeWrapper);
        } catch (Exception e) {
            throw new ScriptingException("Unable to initialize runtime with default scripts", e);
        }
    }

    /**
     * @return Returns the runtimeWrapper.
     * @throws ScriptingException
     */
    public JRubyRuntimeWrapper getRuntimeWrapper() throws ScriptingException {
        if (runtimeWrapper == null) {
            initRuntime();
        }
        return runtimeWrapper;
    }

    /**
     * return class loader for activator plugin
     * 
     * @return
     */
    private ClassLoader getClassLoader() {
        return getClass().getClassLoader();
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
        private File scriptsFolder;
        private static final String SCRIPT_NAME_FORMAT = "%s:%s";
        private static final FileFilter ALL_RUBY_FILES = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                final String name = pathname.getName();
                return name.endsWith(".rb") || name.endsWith(".t");
            }
        };
        private static final FileFilter FILE_FILTER_TO_LOAD = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                final String name = pathname.getName();
                return name.endsWith(".t");
            }
        };

        /**
         * initialize scripts workspace;
         * 
         * @param rubyScriptingFolder
         * @return false if workspace is already exist, true- if newly created
         * @throws IOException
         */
        public void initWorkspace() throws IOException {
            scriptsFolder = new File(WORKSPACE_FOLDER + File.separator + SCRIPTS_FOLDER);
            if (!scriptsFolder.exists()) {
                FileUtils.forceMkdir(scriptsFolder);
            }
        }

        public void copySources(URL rubyScriptingFolder) throws IOException {
            File rubyFolder = new File(rubyScriptingFolder.getPath());
            for (File source : rubyFolder.listFiles()) {
                if (!source.isDirectory() || source.getName().equals(COMMON_SCRIPTS_FOLDER)) {
                    continue;
                }
                String scriptFolderName = source.getAbsolutePath();
                scriptFolderName = scriptFolderName.substring(0, scriptFolderName.length());
                scriptFolderName = scriptFolderName.substring(scriptFolderName.lastIndexOf(File.separator) + 1,
                        scriptFolderName.length());
                File destination;
                String createScriptFolder = scriptsFolder.getAbsolutePath() + File.separator + scriptFolderName;
                destination = new File(createScriptFolder);
                FileUtils.forceMkdir(destination);
                FileUtils.copyDirectory(source, destination, ALL_RUBY_FILES);
            }
        }

        /**
         * @return Returns the scriptsFolder.
         */
        public File getScriptsFolder() {
            return scriptsFolder;
        }

        /**
         * return all workspace file list
         * 
         * @return
         */
        public Map<String, File> getAllWorkspaceScripts(FileFilter filter) {
            File projectFolder = new File(WORKSPACE_FOLDER + File.separator + SCRIPTS_FOLDER);
            File[] modules = projectFolder.listFiles();
            Map<String, File> fileList = new HashMap<String, File>();
            for (File module : modules) {
                File[] scripts = module.listFiles(filter);
                if (scripts.length > NumberUtils.INTEGER_ZERO) {
                    for (File script : scripts) {
                        fileList.put(String.format(SCRIPT_NAME_FORMAT, module.getName(), script.getName()), script);
                    }
                }
            }
            return fileList;
        }

    }
}
