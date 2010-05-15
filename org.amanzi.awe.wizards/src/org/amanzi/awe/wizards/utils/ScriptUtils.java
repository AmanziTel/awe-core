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

package org.amanzi.awe.wizards.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import org.amanzi.awe.views.kpi.KPIPlugin;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ScriptUtils {
    public static String[] getAvailableModules(final Ruby ruby, String moduleName) {
        Formatter f = new Formatter();
        String script = f.format(KPIPlugin.GET_SUBMODULES_SCRIPT, moduleName).toString();
        return evalScript(ruby, script);
    }

    public static String[] getAvailableMethods(final Ruby ruby, String moduleName) {
        Formatter f = new Formatter();
        String script = f.format(KPIPlugin.GET_METHODS_SCRIPT, moduleName).toString();
        String[] methods = evalScript(ruby, script);
        for (int i = 0; i < methods.length; i++) {
            methods[i] = methods[i].split("\\.")[1];
        }
        return methods;
    }

    private static String[] evalScript(final Ruby ruby, String script) {
        IRubyObject submodules = ruby.evalScriptlet(script);
        Object[] array = submodules.convertToArray().toArray();
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    public static String generateKPIReportScript(String kpiName,String datasetScript,String kpiScript,String aggregation) {
        Formatter f = new Formatter();
        String script = f.format(kpiScript , datasetScript,aggregation).toString();
        StringBuffer sb = new StringBuffer("report '").append(kpiName).append("' do\n  author '").append(
                System.getProperty("user.name")).append("'\n  date '")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("'\n");
        sb.append("  table '").append(kpiName).append(", aggregation: ").append(aggregation).append("' do |t|\n");
        sb.append("    t.kpi=").append(script).append("\n");
        sb.append("  end\n");
        sb.append("end");
        return sb.toString();
    }
    public static String generateNetViewScript(String kpiName,String dataset,String datasetScript,String kpiScript,String aggregation) {
        Formatter f = new Formatter();
        String script = f.format(kpiScript , datasetScript,aggregation).toString();
        StringBuffer sb = new StringBuffer("report '").append(kpiName).append("' do\n  author '").append(
                System.getProperty("user.name")).append("'\n  date '")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("'\n");
        sb.append("  text \"Dataset: ").append(dataset).append("\"\n");
        sb.append("  chart '").append(kpiName).append(", aggregation: ").append(aggregation).append("' do |chart|\n");
        sb.append("    chart.kpi=").append(script).append("\n");
        sb.append("  end\n");
        sb.append("end");
        return sb.toString();
    }
    public static String generateNetViewScriptForCounters(String[] counters,String dataset,String datasetScript,String aggregation) {
        StringBuffer countersScript=new StringBuffer("[");
        for (String counter:counters){
            countersScript.append("'").append(counter).append("',");
        }
        countersScript.deleteCharAt(countersScript.length()-1).append("]");
        StringBuffer sb = new StringBuffer("report 'Counters report' do\n  author '").append(
                System.getProperty("user.name")).append("'\n  date '")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("'\n");
        sb.append("  text \"Dataset: ").append(dataset).append("\"\n");
        sb.append("  chart 'Counters, aggregation: ").append(aggregation).append("' do |chart|\n");
        sb.append("    chart.dataset=").append(datasetScript).append("\n");
        sb.append("    chart.properties=").append(countersScript.toString()).append("\n");
        sb.append("    chart.aggregation=:").append(aggregation).append("\n");
        sb.append("  end\n");
        sb.append("end");
        return sb.toString();
    }
}
