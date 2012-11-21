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

import org.amanzi.awe.statistics.template.functions.IAggregationFunction;

/**
 * <p>
 * * Purposed for count value calculation
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Count implements IAggregationFunction {
    private Integer count;
    private final boolean acceptsNulls;

    public Count(final boolean acceptsNulls) {
        super();
        this.acceptsNulls = acceptsNulls;
        count = 0;
    }

    public Count() {
        this(false);
    }

    @Override
    public Number getResult() {
        return count;
    }

    @Override
    public IAggregationFunction update(final Number value) {
        if (value != null || value == null && acceptsNulls) {
            count += 1;
        }
        return this;
    }

    @Override
    public boolean acceptsNulls() {
        return acceptsNulls;
    }

    @Override
    public Number getTotal() {
        return getResult();
    }
}
