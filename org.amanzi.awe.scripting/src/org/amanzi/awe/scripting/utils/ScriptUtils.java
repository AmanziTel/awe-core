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

package org.amanzi.awe.scripting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ScriptUtils {
    /*
     * logger initialization
     */
    private final static Logger LOGGER = Logger.getLogger(AbstractScriptingPlugin.class);

    /*
     * static fields;
     */
    private static final ScriptUtils INSTANCE = new ScriptUtils();
    private static final String JRUBY_PLUGIN_NAME = "org.jruby";

    private String jRubyHome;
    private String jRubyVersion;

    /**
     * @return
     */
    public static ScriptUtils getInstance() {
        return INSTANCE;
    }

    /**
     * @return
     */
    public String getJRubyHome() {
        return ensureJRubyHome();
    }

    /** return JRubyHome, searching for it if necessary */
    private String ensureJRubyHome() {
        try {
            if (jRubyHome == null) {
                jRubyHome = findJRubyHome(System.getProperty("jruby.home"));
            }
        } catch (Exception e) {
            LOGGER.error("Cann't ensure jruby.home", e);
        }
        return jRubyHome;
    }

    /** return JRubyVersion, searching for it if necessary */
    private String ensureJRubyVersion() {
        try {
            if (jRubyVersion == null) {
                jRubyVersion = findJRubyVersion(ensureJRubyHome(), System.getProperty("jruby.version"));
            }
        } catch (Exception e) {
            LOGGER.error("Cann't ensure jruby.version", e);
        }
        return (jRubyVersion);
    }

    /**
     * @param absolutePath
     * @return
     */
    public List<String> makeLoadPath(String absolutePath) {
        ensureJRubyHome();
        ensureJRubyVersion();
        if (jRubyHome == null || jRubyVersion == null) {
            LOGGER.error("cann't ensure necessary variables jruby.home=" + jRubyHome + "jruby.version=" + jRubyVersion);
        }
        List<String> loadPath = new ArrayList<String>();
        if (absolutePath != null) {
            loadPath.add(absolutePath);
        }
        loadPath.add(jRubyHome + "/lib/ruby/site_ruby/" + jRubyVersion);
        loadPath.add(jRubyHome + "/lib/ruby/site_ruby");
        loadPath.add(jRubyHome + "/lib/ruby/" + jRubyVersion);
        loadPath.add(jRubyHome + "/lib/ruby/" + jRubyVersion + "/java");
        loadPath.add(jRubyHome + "/lib");

        loadPath.add("lib/ruby/" + jRubyVersion);
        loadPath.add(".");
        return loadPath;
    }

    /**
     * search for jruby home, starting with passed value, if any
     * 
     * @return
     */
    private String findJRubyHome(String suggested) {
        String jRubyHome = null;
        // Lagutko, 22.06.2009, since now we search ruby home only in org.jruby plugin
        try {
            jRubyHome = getPluginRoot(JRUBY_PLUGIN_NAME);
        } catch (IOException e) {
            LOGGER.error("Cannon't instantiate ruby.home");
            jRubyHome = null;
        }
        return jRubyHome;
    }

    /** try determine ruby version jruby.version property was not set. Default to "1.8" */
    private String findJRubyVersion(String jRubyHome, String jRubyVersion) {
        if (jRubyVersion == null) {
            for (String version : new String[] {"1.8", "1.9", "2.0", "2.1"}) {
                String path = jRubyHome + "/lib/ruby/" + version;
                try {
                    if ((new java.io.File(path)).isDirectory()) {
                        jRubyVersion = version;
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process possible JRuby path '" + path + "': " + e.getMessage());
                }
            }
        }
        if (jRubyVersion == null) {
            jRubyVersion = "1.8";
        }
        return jRubyVersion;
    }

    /**
     * Returns path to plugin that can be handled by JRuby
     * 
     * @param pluginName name of plugin
     * @return path to plugin
     * @throws IOException throws Exception if path cannot be resolved
     * @author Lagutko_N
     */
    public String getPluginRoot(String pluginName) throws IOException {
        URL rubyLocationURL = Platform.getBundle(pluginName).getEntry("/");
        String rubyLocation = FileLocator.resolve(rubyLocationURL).getPath();
        if (rubyLocation.startsWith("jar:file:")) {
            rubyLocation = rubyLocation.substring(9);
            if (!rubyLocation.startsWith(File.separator)) {
                rubyLocation = File.separator + rubyLocation;
            }
            rubyLocation = "file:" + rubyLocation;
        } else if (rubyLocation.startsWith("file:")) {
            rubyLocation = rubyLocation.substring(5);
        }

        return rubyLocation;
    }

    /**
     * get content of file with @param scriptName
     * 
     * @param scriptName
     * @param destination
     * @return
     */
    public String getScript(String scriptName, File destination) {
        File requiredFile = null;
        for (File script : destination.listFiles()) {
            if (script.getName().equals(scriptName)) {
                requiredFile = script;
            }
        }
        if (requiredFile == null) {
            return StringUtils.EMPTY;
        }
        String result = StringUtils.EMPTY;
        try {
            result = inputStreamToString(new FileInputStream(requiredFile));
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found: " + requiredFile.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.error("Error while getting script ", e);
        }
        return result;

    }

    /**
     * get content of sciptFile
     * 
     * @param scriptFile
     * @return
     */
    public String getScript(File scriptFile) {
        String result = StringUtils.EMPTY;
        if (scriptFile == null) {
            return result;
        }
        try {
            result = inputStreamToString(new FileInputStream(scriptFile));
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found: " + scriptFile.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.error("Error while getting script ", e);
        }
        return result;
    }

    /**
     * return project folder content
     * 
     * @param projectName
     */
    public static List<File> getScriptFilesForProject(String projectName) {
        File projectFolder = new File(AbstractScriptingPlugin.WORKSPACE_FOLDER + File.separator
                + AbstractScriptingPlugin.PROJECT_FOLDER + File.separator + projectName);
        if (!projectFolder.exists()) {
            LOGGER.info("project folder " + projectName + "doesn't exist");
            return null;
        }
        return Arrays.asList(projectFolder.listFiles());
    }

    /**
     * put file content into string
     * 
     * @param stream
     * @return
     * @throws IOException
     */
    private String inputStreamToString(InputStream stream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        reader.close();
        return buffer.toString();
    }
}
