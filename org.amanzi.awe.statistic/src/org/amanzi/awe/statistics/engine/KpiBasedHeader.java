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

package org.amanzi.awe.statistics.engine;

import java.util.ArrayList;
import java.util.List;

import org.jruby.Ruby;
import org.jruby.RubyNumeric;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class KpiBasedHeader implements IStatisticsHeader {
    private static final String EVALUATE = "Neo4j::load_node(%s).instance_eval {%s(self)}";
    private Ruby ruby;
    private String kpiName;
    private String name;

    /**
     * @param ruby
     * @param kpiName
     * @param name TODO
     */
    public KpiBasedHeader(Ruby ruby, String kpiName, String name) {
        this.ruby = ruby;
        this.kpiName = kpiName;
        this.name = name;
    }

    @Override
    public Number calculate(IDatasetService service, Node node) {
        IRubyObject result = ruby.evalScriptlet(String.format(EVALUATE, node.getId(), kpiName));
        if (result instanceof RubyString) {
            return Double.NaN;
        } else if (result instanceof RubyNumeric) {
            return ((RubyNumeric)result).getDoubleValue();
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getKpiName() {
        return kpiName;
    }

}
