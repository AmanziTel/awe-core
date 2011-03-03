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

package org.amanzi.awe.statistics.template;

import java.net.URL;

import org.amanzi.awe.statistics.StatisticPlugin;
import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.java.proxies.JavaProxy;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Ruby-based template Builder
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class TemplateBuilder {
    private static TemplateBuilder INSTANCE = new TemplateBuilder();
    private static Ruby ruby;

    private TemplateBuilder() {
        initializeRuby();

    }
    public static TemplateBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Builds a template for a script given
     * 
     * @param script script to build a template
     * @return template built
     */
    public Template build(String script) {
        IRubyObject result = ruby.evalScriptlet(script);
        if (result instanceof Template) {
            return (Template)result;
        } else if (result instanceof JavaProxy) {
            Object unwrapped = ((JavaProxy)result).unwrap();
            if (unwrapped instanceof Template) {
                return (Template)unwrapped;
            }
        } else {
            System.out.println("Result of script evaluating is: " + result);
        }
        return null;
    }

    private void initializeRuby() {
        try {
            // URL statPluginPath =
            // FileLocator.resolve(StatisticPlugin.getDefault().getBundle().getEntry("/"));
            ruby = KPIPlugin.getDefault().getRubyRuntime();
            // ruby.evalScriptlet("$LOAD_PATH<<\""+statPluginPath.getFile()+"\";puts
            // $LOAD_PATH.join(\"\n\")");
            URL fileURL = FileLocator.toFileURL(StatisticPlugin.getDefault().getBundle().getEntry("ruby/builder.rb"));
            ruby.evalScriptlet(ScriptUtils.getScriptContent(fileURL.getPath()));
        } catch (Exception e) {
            // TODO Handle IOException
            e.printStackTrace();
        }
    }
}
