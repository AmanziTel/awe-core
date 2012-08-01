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

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * Щ
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class KpiBasedHeader implements IStatisticsHeader {
    // private static final String EVALUATE = "Neo4j::load_node(%s).instance_eval {%s(self)}";
    private String kpiName;
    private String name;

    public KpiBasedHeader() {

    }

    /**
     * @param kpiName
     * @param name TODO
     */
    public KpiBasedHeader(String kpiName, String name) {
        this.kpiName = kpiName;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getKpiName() {
        return kpiName;
    }

    /**
     * @param kpiName The kpiName to set.
     */
    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // TODO: LN: 01.08.2012, to constant
        return String.format("Name: %s, Formula: %s", name, kpiName);
    }

    @Override
    public Number calculate(Node node) {
        return null;
    }

}
