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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.scripting.utils.ScriptUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.java.proxies.JavaProxy;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * <p>
 * wrap Ruby runtime
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class JRubyRuntimeWrapper {
    private static final Logger LOGGER = Logger.getLogger(JRubyRuntimeWrapper.class);
    private static final String NAME_SEPARATOR = ":";
    private static final int MODULE_ELEMENT_INDEX = NumberUtils.INTEGER_ZERO;
    private static final int SCRIPT_NAME_ELEMENT_INDEX = NumberUtils.INTEGER_ONE;

    private final Ruby runtime;
    private final File destination;

    /**
     * @param runtime
     * @param destination
     */
    protected JRubyRuntimeWrapper(final Ruby runtime, final File destination) {
        this.runtime = runtime;
        this.destination = destination;
    }

    /**
     * execute string by script name
     * 
     * @param scriptId
     * @throws ScriptingException
     * @throws FileNotFoundException
     */
    public Object executeScriptByName(final String scriptId) throws FileNotFoundException, ScriptingException {
        if (StringUtils.isEmpty(scriptId) && !scriptId.contains(NAME_SEPARATOR)) {
            LOGGER.error(scriptId + " has incorrect format. Correct format is <MODULE>:<SCRIPT_NAME>");
        }
        String[] splittedName = scriptId.split(NAME_SEPARATOR);
        String moduleName = splittedName[MODULE_ELEMENT_INDEX];
        File destination = getModuleFolder(moduleName);
        if (destination == null) {
            LOGGER.error("Module " + moduleName + " doesn't exists in script folder");
            throw new FileNotFoundException("Module " + moduleName + " doesn't exists in script folder "
                    + this.destination.getAbsolutePath());
        }
        String scriptName = splittedName[SCRIPT_NAME_ELEMENT_INDEX];
        String script = ScriptUtils.getInstance().getScript(scriptName, destination);
        return executeScript(script);
    }

    /**
     * execute string from file
     * 
     * @param file
     * @return
     * @throws ScriptingException
     */
    public Object executeScript(final File file) throws ScriptingException {
        String script = ScriptUtils.getInstance().getScript(file);
        return executeScript(script);
    }

    /**
     * check inner folder for existence
     * 
     * @param name
     */
    private File getModuleFolder(final String name) {
        File[] existedModules = destination.listFiles();
        LOGGER.info("< Start searching " + name + " in destination  " + destination.getAbsolutePath() + " children "
                + destination.list() + " >");
        for (File module : existedModules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    /**
     * execute script
     * 
     * @param script
     * @return
     */
    public Object executeScript(final String script) throws ScriptingException {
        try {
            IRubyObject object = runtime.evalScriptlet(script);
            return defineJavaObject(object);
        } catch (Exception e) {
            LOGGER.error("Can't execute script " + script + "because of", e);
            throw new ScriptingException("Can't execute script " + script + "because of", e);
        }

    }

    public Object executeScript(final String script, final File fileName) throws ScriptingException {
        try {
            IRubyObject object = runtime.getArgsFile();
            return defineJavaObject(object);
        } catch (Exception e) {
            LOGGER.error("Can't execute script " + script + "because of", e);
            throw new ScriptingException("Can't execute script " + script + "because of", e);
        }
    }

    /**
     * try to define ruby object as java object
     * 
     * @param object
     * @return
     */
    private Object defineJavaObject(final IRubyObject object) {
        Object unwrapped;
        if (object instanceof JavaProxy) {
            unwrapped = ((JavaProxy)object).unwrap();
        } else if (object instanceof RubyNumeric) {
            unwrapped = ((RubyNumeric)object).getDoubleValue();
        } else if (object instanceof RubyHash) {
            unwrapped = convertToHashMap((RubyHash)object);
        } else if (object instanceof RubyString) {
            unwrapped = object.asString().getValue();
        } else {
            unwrapped = object;
        }
        return unwrapped;
    }

    /**
     * @param entrySet
     * @return
     */
    private Map<Object, Object> convertToHashMap(final RubyHash rubyMap) {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        for (Object key : rubyMap.keySet()) {
            map.put(key, rubyMap.get(key));
        }
        return map;
    }

    public IRubyObject wrap(final Object javaObject) {
        return JavaEmbedUtils.javaToRuby(runtime, javaObject);
    }

    public <K extends Object, V extends Object> Map<RubySymbol, V> toSymbolMap(final Map<K, V> originalMap) {
        HashMap<RubySymbol, V> result = new HashMap<RubySymbol, V>();

        for (Entry<K, V> entry : originalMap.entrySet()) {
            RubySymbol symbol = RubySymbol.newSymbol(runtime, entry.getKey().toString());

            result.put(symbol, entry.getValue());
        }

        return result;
    }
}
