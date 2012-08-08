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
import java.util.jar.JarFile;

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
    private static final String JRUBY_BUNDLE_NAME = "org.jruby";
    private static final String NEO4J_BUNDLE_NAME = "org.neo4j";
    private static final String POSTFIX_JAR = ".jar!/";
    private static final String PREFIX_FILE = "file:";
    private static final String LIB_PATH = "lib/ruby/";
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
                jRubyHome = getPluginRoot(JRUBY_BUNDLE_NAME);
            }
        } catch (ScriptingException e) {
            LOGGER.error("Can't ensure jruby.home", e);
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
            LOGGER.error("Can't ensure jruby.version", e);
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
        LOGGER.info("Neo4J scripts folder set to < " + neoRubyGemDir + " >");
        String neo4j = getPluginRoot(NEO4J_BUNDLE_NAME);

        loadPath.add(path);
        loadPath.add(jRubyHome + LIB_PATH + "site_ruby/" + jRubyVersion);
        loadPath.add(jRubyHome + LIB_PATH + "site_ruby");
        loadPath.add(jRubyHome + LIB_PATH + jRubyVersion);
        loadPath.add(jRubyHome + LIB_PATH + jRubyVersion + "/java");
        loadPath.add(jRubyHome + "lib");

        loadPath.add(neoRubyGemDir + "/lib");
        loadPath.add(neoRubyGemDir + "/lib/relations");
        loadPath.add(neoRubyGemDir + "/lib/mixins");
        loadPath.add(neoRubyGemDir + "/lib/jars");
        loadPath.add(neoRubyGemDir + "/examples/imdb");

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
    private String findJRubyVersion(String jRubyHome) throws ScriptingException, IOException {
        String result = null;
        JarFile jarFile = null;
        if (jRubyHome.startsWith(PREFIX_FILE) && jRubyHome.endsWith(POSTFIX_JAR)) {
            String path = prepareJarPath(jRubyHome);
            jarFile = new JarFile(path);
        }
        for (String version : JRUBY_VERSIONS) {
            if (checkFileExisting(jarFile, jRubyHome, LIB_PATH + version)) {
                result = version;
                break;
            }
        }
        LOGGER.info("Jruby version set to < " + result + " >");
        return result;
    }

    /**
     * first check for version folder in jar file. if jar folder not exist -> check for directory in
     * folderPath
     * 
     * @param jarFile
     * @param version
     * @param version2
     */
    private boolean checkFileExisting(JarFile jarFile, String folderPath, String versionFolder) {
        if (jarFile == null) {
            if ((new File(folderPath + versionFolder)).isDirectory()) {
                return true;
            }
        } else if (jarFile.getEntry(versionFolder) != null) {
            return true;
        }
        return false;
    }

    /**
     * @param jRubyHome2
     * @return
     */
    private String prepareJarPath(String jrubyHomePath) {
        jrubyHomePath = jrubyHomePath.substring(PREFIX_FILE.length(), jrubyHomePath.length() - 2);
        LOGGER.info("Prepared Path < " + jrubyHomePath + " >");
        return jrubyHomePath;
    }

    /**
     * Returns path to plugin that can be handled by JRuby
     * 
     * @param pluginName name of plugin
     * @return path to plugin
     * @throws IOException throws Exception if path cannot be resolved
     * @author Kondratenko_Vladislav
     */
    public String getPluginRoot(String pluginName) throws ScriptingException {
        try {
            URL rubyLocationURL = Platform.getBundle(pluginName).getEntry("/");
            LOGGER.info("Plugin URL < " + rubyLocationURL + " >");
            String rubyLocation = FileLocator.resolve(rubyLocationURL).getPath();
            LOGGER.info(" Location < " + rubyLocation + " >");
            if (rubyLocation.startsWith(PREFIX_FILE) && !rubyLocation.endsWith(POSTFIX_JAR)) {
                rubyLocation = rubyLocation.substring(PREFIX_FILE.length());
            }
            LOGGER.info("File Location < " + rubyLocation + " >");

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
        LOGGER.info("< Start searching script" + scriptName + " in destination  " + destination.getAbsolutePath() + " children "
                + destination.list() + " >");
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
