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

package org.amanzi.awe.statistics.nodeproperties.impl;

import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsNodeProperties implements IStatisticsNodeProperties {

    private static final String AGGREGATION_PROPERTY_NAME = "aggregation";

    private static final String TEMPLATE_NAME = "template";

    private static final String VALUE_PROEPRTY = "value";

    private static final String COLUMN_NAMES_PROPERTY = "column_names";

    @Override
    public String getTemplateNameProperty() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getAggregationPropertyNameProperty() {
        return AGGREGATION_PROPERTY_NAME;
    }

    @Override
    public String getValueProperty() {
        return VALUE_PROEPRTY;
    }

    @Override
    public String getColumnNamesProperty() {
        return null;
    }

}
