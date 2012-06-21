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
    private final static Logger LOGGER = Logger.getLogger(AbstractScriptingPlugin.class);
    /*
     * constants definition
     */
    public final static String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath().toString();
    public final static String PROJECT_FOLDER = "awe-scripts";
    public final static String RUBY_SCRIPT_FOLDER = "/ruby";

    private ScriptingManager manager;
    private Ruby runtime;

    /**
     * should be invoked to define script folder
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        try {
            initScriptManager(context);
            initRuntime();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while initialize jruby runtime", e);
            throw new Exception(e);

        }
    }

    /**
     * initialize script manager
     * 
     * @param context
     * @throws IOException
     */
    public void initScriptManager(BundleContext context) throws Exception {
        if (RUBY_SCRIPT_FOLDER.equalsIgnoreCase(getScriptPath())) {

            LOGGER.error("undefined project folder", new IOException("undefined project folder"));
        }
        URL workspaceName = context.getBundle().getEntry(getScriptPath());
        URL workspaceLocator = FileLocator.toFileURL(workspaceName);
        LOGGER.info("Start workspace initializing");
        manager = new ScriptingManager(workspaceLocator);
        LOGGER.info("Start file copying");
        manager.copyScripts();
    }

    /**
     * initialize ruby runtime
     * 
     * @throws IOException
     */
    private void initRuntime() throws IOException {
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
        runtime.getLoadService().init(ScriptUtils.getInstance().makeLoadPath(manager.getDestination().getAbsolutePath()));
    }

    /**
     * return class loader for activator plugin
     * 
     * @return
     */
    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    /**
     * } /* (non-Javadoc)
     * 
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
        return RUBY_SCRIPT_FOLDER;
    }

    /**
     * TODO Purpose of AbstractScriptingPlugin
     * <p>
     * Script managment utils
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private class ScriptingManager {
        private File source;
        private File destination;

        /**
         * @param rubyScriptingFolder
         */
        public ScriptingManager(URL rubyScriptingFolder) {
            initWorkspace(rubyScriptingFolder);
        }

        /**
         * initialise scripts workspace;
         * 
         * @param rubyScriptingFolder
         * @return false if workspace is already exist, true- if newly created
         */
        public boolean initWorkspace(URL rubyScriptingFolder) {
            File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
            if (!projectFolder.exists()) {
                projectFolder.mkdirs();
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
            destination.mkdir();
            return true;
        }

        /**
         * copy directory from source to
         */
        public void copyScripts() {
            FileFilter fileFilter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    final String name = pathname.getName();
                    return name.endsWith(".rb") || name.endsWith(".t");
                }
            };
            List<String> destinationContent = Arrays.asList(destination.list());
            byte[] buf = new byte[1024];
            for (File sourceFile : source.listFiles(fileFilter)) {
                File destFile = new File(destination.getPath() + File.separator + sourceFile.getName());
                if (!destinationContent.contains(sourceFile.getName())) {
                    copyFile(sourceFile, destFile, buf);

                }
            }
        }

        /**
         * copy file content from @param sourceFile to @param destFile
         * 
         * @param sourceFile
         * @param destFile
         * @param buf bufferSize;
         */
        public void copyFile(File sourceFile, File destFile, byte[] buf) {

            try {
                FileUtils.copyFile(sourceFile, destFile, false);
            } catch (IOException e) {
                LOGGER.error("Cann't copy file", e);
            }

        }

        /**
         * @return Returns the destination.
         */
        public File getDestination() {
            return destination;
        }
    }
}
