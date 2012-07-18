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

import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.scripting.utils.ScriptUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jruby.Ruby;
import org.jruby.RubyNumeric;
import org.jruby.java.proxies.JavaProxy;
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

    private Ruby runtime;
    private File destination;

    /**
     * @param runtime
     * @param destination
     */
    protected JRubyRuntimeWrapper(Ruby runtime, File destination) {
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
    public Object executeScriptByName(String scriptId) throws FileNotFoundException, ScriptingException {
        if (!scriptId.contains(NAME_SEPARATOR)) {
            LOGGER.error(scriptId + " has incorrect format. Correct format is <MODULE>:<SCRIPT_NAME>");
        }
        String[] splittedName = scriptId.split(NAME_SEPARATOR);
        String project = splittedName[NumberUtils.INTEGER_ZERO];
        File destination = checkForExistance(project);
        if (destination == null) {
            LOGGER.error("Module " + project + " doesn't exists in script folder");
            throw new FileNotFoundException("Module " + project + " doesn't exists in script folder");
        }
        String scriptName = splittedName[NumberUtils.INTEGER_ONE];
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
    public Object executeScript(File file) throws ScriptingException {
        String script = ScriptUtils.getInstance().getScript(file);
        return executeScript(script);
    }

    /**
     * check inner folder for existence
     * 
     * @param name
     */
    private File checkForExistance(String name) {
        File[] existedModules = destination.listFiles();
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
    public Object executeScript(String script) throws ScriptingException {
        try {
            IRubyObject object = runtime.evalScriptlet(script);
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
    private Object defineJavaObject(IRubyObject object) {
        Object unwrapped;
        if (object instanceof JavaProxy) {
            unwrapped = ((JavaProxy)object).unwrap();
        } else if (object instanceof RubyNumeric) {
            unwrapped = ((RubyNumeric)object).getDoubleValue();
        } else {
            unwrapped = object.asString().getValue();
        }
        return unwrapped;
    }
}
