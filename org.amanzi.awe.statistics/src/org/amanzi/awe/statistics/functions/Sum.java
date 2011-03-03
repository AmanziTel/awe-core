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

package org.amanzi.awe.statistics.functions;

import org.amanzi.awe.statistics.engine.IAggregationFunction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Sum implements IAggregationFunction {
    private Double sum;

    public Sum() {
        sum = 0.0;
    }

    @Override
    public Number getResult() {
        return sum;
    }

    @Override
    public IAggregationFunction update(Number value) {
        sum += value.doubleValue();
        return this;
    }

    @Override
    public boolean acceptsNulls() {
        return false;
    }

}
