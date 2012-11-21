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

package org.amanzi.awe.statistics.template.functions.impl;

import org.amanzi.awe.statistics.template.functions.AbstractFunction;
import org.amanzi.awe.statistics.template.functions.IAggregationFunction;

/**
 * <p>
 * Purposed for average value calculation
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Average extends AbstractFunction {
    private Integer count;

    public Average() {
        super();
        count = 0;
    }

    @Override
    public Number getResult() {
        if (count != 0) {
            return getTotal().doubleValue() / count;
        }
        return Double.NaN;
    }

    @Override
    public IAggregationFunction update(final Number value) {
        super.update(value);
        count += 1;
        return this;
    }

    public IAggregationFunction update(final Number value, final int count) {
        super.update(value);
        this.count += count;
        return this;
    }

    @Override
    public boolean acceptsNulls() {
        return false;
    }

}
