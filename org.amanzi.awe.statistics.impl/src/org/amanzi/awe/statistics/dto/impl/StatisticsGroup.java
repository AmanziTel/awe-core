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

package org.amanzi.awe.statistics.dto.impl;

import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.neo.impl.dto.DataElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsGroup extends DataElement implements IStatisticsGroup {

    private String period;

    private String propertyValue;

    /**
     * @param node
     */
    public StatisticsGroup(final Node node) {
        super(node);
    }

    /**
     * @return Returns the period.
     */
    @Override
    public String getPeriod() {
        return period;
    }

    /**
     * @param period The period to set.
     */
    public void setPeriod(final String period) {
        this.period = period;
    }

    /**
     * @return Returns the propertyValue.
     */
    @Override
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(final String propertyValue) {
        this.propertyValue = propertyValue;
    }

}
