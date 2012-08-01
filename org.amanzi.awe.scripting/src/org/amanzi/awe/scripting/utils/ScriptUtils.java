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
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.apache.commons.lang3.StringUtils;
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
    private static final String JRUBY_PLUGIN_NAME = "org.jruby";
    private static final String PREFIX_JAR_FILE = "jar:file:";
    private static final String PREFIX_FILE = "file:";
    private static final String LIB_PATH = "/lib/ruby/";
    private static final String[] JRUBY_VERSIONS = new String[] {"1.8", "1.9", "2.0", "2.1"};
    private String jRubyHome;
    private String jRubyVersion;

    private static final class SingletonHolder {
        public static final ScriptUtils HOLDER_INSTANCE = new ScriptUtils();

        private SingletonHolder() {
        }
    }

    public static ScriptUtils getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * return JRubyHome, searching for it if necessary
     * 
     * @throws Exception
     */
    public String getJRubyHome() throws ScriptingException {
        try {
            if (jRubyHome == null) {
                jRubyHome = getPluginRoot(JRUBY_PLUGIN_NAME);
            }
        } catch (ScriptingException e) {
            LOGGER.error("Cann't ensure jruby.home", e);
            throw new ScriptingException(e);
        }
        return jRubyHome;
    }

    /** return JRubyVersion, searching for it if necessary */
    private String getJrubyVersion() throws ScriptingException {
        try {
            if (jRubyVersion == null) {
                jRubyVersion = findJRubyVersion(getJRubyHome());
            }
        } catch (Exception e) {
            LOGGER.error("Cann't ensure jruby.version", e);
            throw new ScriptingException(e);
        }
        return jRubyVersion;
    }

    /**
     * @param scripts
     * @return
     * @throws Exception
     */
    public List<String> makeLoadPath(String path) throws ScriptingException {
        try {
            getJRubyHome();
            getJrubyVersion();
        } catch (ScriptingException e) {
            LOGGER.error("can't ensure necessary variables jruby.home=" + jRubyHome + "jruby.version=" + jRubyVersion);
            throw new ScriptingException(e);
        }

        List<String> loadPath = new ArrayList<String>();
        String neoRubyGemDir = getPluginRoot(AbstractScriptingPlugin.PLUGIN_ID) + "neo4j";
        String neo4j = getPluginRoot("org.neo4j");
        loadPath.add(path);
        loadPath.add(neoRubyGemDir + "/lib");
        loadPath.add(neoRubyGemDir + "/lib/neo4j");
        loadPath.add(neo4j);
        loadPath.add(neo4j + "/lib");
        return loadPath;
    }

    /**
     * try determine ruby version jruby.version property was not set. Default to "1.8"
     * 
     * @throws IOException
     */
    private String findJRubyVersion(String jRubyHome) throws ScriptingException {
        String result = null;
        for (String version : JRUBY_VERSIONS) {
            String path = jRubyHome + LIB_PATH + version;
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
    public String getPluginRoot(String pluginName) throws ScriptingException {
        try {
            URL rubyLocationURL = Platform.getBundle(pluginName).getEntry("/");
            LOGGER.info("Ruby Plugin URL <" + rubyLocationURL + ">");
            String rubyLocation = FileLocator.resolve(rubyLocationURL).getPath();
            LOGGER.info("Ruby Location <" + rubyLocation + ">");
            if (rubyLocation.startsWith(PREFIX_JAR_FILE)) {
                rubyLocation = rubyLocation.substring(PREFIX_JAR_FILE.length());
                if (!rubyLocation.startsWith(File.separator)) {
                    rubyLocation = File.separator + rubyLocation;
                }
                rubyLocation = PREFIX_FILE + rubyLocation;
            } else if (rubyLocation.startsWith(PREFIX_FILE)) {
                rubyLocation = rubyLocation.substring(PREFIX_FILE.length());
            }

            LOGGER.info("Ruby Location <" + rubyLocation + ">");

            return rubyLocation;
        } catch (Exception e) {
            throw new ScriptingException(e);
        }

    }

    /**
     * get content of file with @param scriptName
     * 
     * @param scriptName
     * @param destination
     * @return
     * @throws ScriptingException
     * @throws FileNotFoundException
     */
    public String getScript(String scriptName, File destination) throws FileNotFoundException, ScriptingException {
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
        result = inputStreamToString(new FileInputStream(requiredFile));
        return result;

    }

    /**
     * get content of sciptFile
     * 
     * @param scriptFile
     * @return
     * @throws ScriptingException
     */
    public String getScript(File scriptFile) throws ScriptingException {
        String result = StringUtils.EMPTY;
        if (scriptFile == null) {
            return result;
        }
        try {
            result = inputStreamToString(new FileInputStream(scriptFile));
        } catch (Exception e) {
            throw new ScriptingException(e);
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
    private String inputStreamToString(InputStream stream) throws ScriptingException {
        BufferedReader reader = null;
        try {
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();
        } catch (Exception e) {
            throw new ScriptingException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new ScriptingException(e);
            }
        }
    }
}
