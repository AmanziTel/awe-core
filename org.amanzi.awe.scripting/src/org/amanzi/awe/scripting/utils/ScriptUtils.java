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
import java.util.List;

import org.amanzi.awe.scripting.AbstractScriptingPlugin;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * <p>
 * ScriptUtils - common functionality for jruby paths definition (such as jruby.home, jruby.version)
 * also contain methods for converting script files to string.
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ScriptUtils {
    /*
     * logger initialization
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractScriptingPlugin.class);

    /*
     * static fields;
     */
    private static final ScriptUtils INSTANCE = new ScriptUtils();
    private static final String JRUBY_PLUGIN_NAME = "org.jruby";
    private static final String PREFIX_JAR_FILE = "jar:file:";
    private static final String PREFIX_FILE = "file:";
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
     * @throws Exception
     */
    public String getJRubyHome() throws IOException {
        return ensureJRubyHome();
    }

    /**
     * return JRubyHome, searching for it if necessary
     * 
     * @throws Exception
     */
    private String ensureJRubyHome() throws IOException {
        try {
            if (jRubyHome == null) {
                jRubyHome = getPluginRoot(JRUBY_PLUGIN_NAME);
            }
        } catch (IOException e) {
            LOGGER.error("Cann't ensure jruby.home", e);
            throw e;
        }
        return jRubyHome;
    }

    /** return JRubyVersion, searching for it if necessary */
    private String ensureJRubyVersion() throws IOException {
        try {
            if (jRubyVersion == null) {
                jRubyVersion = findJRubyVersion(ensureJRubyHome());
            }
        } catch (IOException e) {
            LOGGER.error("Cann't ensure jruby.version", e);
            throw e;
        }
        return jRubyVersion;
    }

    /**
     * @param absolutePath
     * @return
     * @throws Exception
     */
    public List<String> makeLoadPath(String absolutePath) throws IOException {
        try {
            ensureJRubyHome();
            ensureJRubyVersion();
        } catch (IOException e) {
            LOGGER.error("cann't ensure necessary variables jruby.home=" + jRubyHome + "jruby.version=" + jRubyVersion);
            throw e;
        }

        List<String> loadPath = new ArrayList<String>();
        if (absolutePath != null) {
            loadPath.add(absolutePath);
        }
        return loadPath;
    }

    /**
     * try determine ruby version jruby.version property was not set. Default to "1.8"
     * 
     * @throws IOException
     */
    private String findJRubyVersion(String jRubyHome) throws IOException {
        String result = null;
        for (String version : new String[] {"1.8", "1.9", "2.0", "2.1"}) {
            String path = jRubyHome + "/lib/ruby/" + version;
            if ((new File(path)).isDirectory()) {
                result = version;
                break;
            }
        }
        return result;
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
        if (rubyLocation.startsWith(PREFIX_JAR_FILE)) {
            rubyLocation = rubyLocation.substring(PREFIX_JAR_FILE.length());
            if (!rubyLocation.startsWith(File.separator)) {
                rubyLocation = File.separator + rubyLocation;
            }
            rubyLocation = PREFIX_FILE + rubyLocation;
        } else if (rubyLocation.startsWith(PREFIX_FILE)) {
            rubyLocation = rubyLocation.substring(PREFIX_FILE.length());
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
