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

package org.amanzi.awe.distribution.properties.impl;

import org.amanzi.awe.distribution.properties.IDistributionNodeProperties;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionNodeProperties implements IDistributionNodeProperties {

    private static final String CURRENT_DISTRIBUTION_ID = "current_distribution_id";

    private static final String DISTRIBUTION_NODE_TYPE = "distribution_node_type";

    private static final String DISTRIBUTION_PROOPERTY_NAME = "distribution_parameter_name";

    @Override
    public String getCurrentDistributionProperty() {
        return CURRENT_DISTRIBUTION_ID;
    }

    @Override
    public String getDistributionPropertyName() {
        return DISTRIBUTION_PROOPERTY_NAME;
    }

    @Override
    public String getDistributionNodeType() {
        return DISTRIBUTION_NODE_TYPE;
    }

}
