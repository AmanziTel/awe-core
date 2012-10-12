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

package org.amanzi.neo.services.impl.statistics;

import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class PropertyStatisticsNodeProperties implements IPropertyStatisticsNodeProperties {

    private static final String COUNT_PROPERTY = "count";

    private static final String CLASS_PROPERTY = "class";

    private static final String VALUE_PREFIX = "v";

    private static final String COUNT_PREFIX = "c";

    private static final String DEFAULT_VALUE_PROPERTY = "default_value";

    @Override
    public String getCountProperty() {
        return COUNT_PROPERTY;
    }

    @Override
    public String getClassProperty() {
        return CLASS_PROPERTY;
    }

    @Override
    public String getValuePrefix() {
        return VALUE_PREFIX;
    }

    @Override
    public String getCountPrefix() {
        return COUNT_PREFIX;
    }

    @Override
    public String getDefaultValueProperty() {
        return DEFAULT_VALUE_PROPERTY;
    }

}
