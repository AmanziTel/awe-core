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

package org.amanzi.awe.correlation.nodeproperties.impl;

import org.amanzi.awe.correlation.nodeproperties.ICorrelationProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationProperties implements ICorrelationProperties {

    private static final String CORRELATION_NODE_PROPERTY = "correlation_property";

    private static final String CORRELATED_NODE_PROPERTY = "correlated_property";;

    private static final String CORRELATED_MODEL_PROPERTY = "correlated_model";

    private static final String PROXIES_COUNT_NODE_PROPERTY = "proxies_count";

    @Override
    public String getCorrelatedModelNameProperty() {
        return CORRELATED_MODEL_PROPERTY;
    }

    @Override
    public String getCorrelatedNodeProperty() {
        return CORRELATED_NODE_PROPERTY;
    }

    @Override
    public String getCorrelationNodeProperty() {
        return CORRELATION_NODE_PROPERTY;
    }

    @Override
    public String getProxiesCountNodeProperty() {
        return PROXIES_COUNT_NODE_PROPERTY;
    }

}
