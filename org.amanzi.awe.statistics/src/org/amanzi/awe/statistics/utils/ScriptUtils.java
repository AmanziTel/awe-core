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

package org.amanzi.awe.statistics.utils;

import java.util.ArrayList;

import org.amanzi.awe.statistics.engine.KpiBasedHeader;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.awe.statistics.template.Condition;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.template.Template.DataType;
import org.amanzi.awe.statistics.template.Threshold;
import org.amanzi.awe.views.kpi.KPIPlugin;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Utility class to work with scripts
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ScriptUtils {
    /**
     * Creates a template which includes all formulas from the script file
     * @param scriptFile script file with formulas
     * @param module module name
     * @param templateName template name
     * @param dataType data type (org.amanzi.awe.statistics.template.Template.DataType)
     * 
     * @return
     */
    public static Template createTemplateForScript(String scriptFile, String module, String templateName, DataType dataType) {
        KPIPlugin.getDefault().loadScript(scriptFile);
        Ruby ruby = KPIPlugin.getDefault().getRubyRuntime();
        Object[] kpisFound = ((RubyArray)ruby.evalScriptlet(module
                + ".singleton_methods.sort.select{|m| !(Annotations.hidden_methods.include? m)}")).toArray();
        ArrayList<String> kpis = new ArrayList<String>(kpisFound.length);
        for (Object kpi : kpisFound) {
            kpis.add(kpi.toString());
        }
        ArrayList<String> displayNames = new ArrayList<String>(kpis.size());

        Template template = new Template(templateName, dataType);

        System.out.println("kpis found: " + kpis);
        for (String kpi : kpis) {
            String kpiDisplayName = kpi;
            Number threshold = null;
            AggregationFunctions function= null;
            final String fullKpiName = module + "."+kpi;
            final IRubyObject res = ruby.evalScriptlet(module + ".get_annotation(:" + kpi + ")");
            if (res instanceof RubyHash) {
                RubyHash result = (RubyHash)res;
                System.out.println(String.format("Found %s annotations for '%s':", result.keySet().size(), kpi));
                for (Object key : result.keySet()) {
                    String strKey = key.toString();
                    String strResult = result.get(key).toString();
                    if ("name".equalsIgnoreCase(strKey)) {
                        kpiDisplayName = strResult;
                    }
                    if ("threshold".equalsIgnoreCase(strKey)) {
                        threshold = Double.parseDouble(strResult);
                        System.out.println("Threshold for '" + kpiDisplayName + "': " + threshold);
                    }
                    if ("function".equalsIgnoreCase(strKey)) {
                        function = AggregationFunctions.getFunctionByName(strResult);
                        System.out.println("Function for '" + kpiDisplayName + "': " + function);
                    }
                    System.out.println("\t" + key + ":\t" + result.get(key));
                }
            }
            displayNames.add(kpiDisplayName);
            KpiBasedHeader kpiBasedHeader = new KpiBasedHeader(fullKpiName, kpiDisplayName);
            template.add(kpiBasedHeader,function==null? AggregationFunctions.AVERAGE : function, threshold == null ? null : new Threshold(threshold,
                    Condition.LT), kpiDisplayName);

        }
        return template;
    }

}
