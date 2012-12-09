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

package org.amanzi.awe.distribution.model.type.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IntegerDistributionType extends NumberDistributionType {

    private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#");

    /**
     * @param model
     * @param nodeType
     * @param propertyName
     * @param numberDistributionRange
     * @param select
     */
    public IntegerDistributionType(IPropertyStatisticalModel model, INodeType nodeType, String propertyName,
            NumberDistributionRange numberDistributionRange, org.amanzi.awe.distribution.model.type.IDistributionType.Select select) {
        super(model, nodeType, propertyName, numberDistributionRange, select);
    }

    @Override
    protected double increaseMinimum(final double previousMinimum, final double step) {
        return Math.round(previousMinimum + step);
    }

    @Override
    protected String getNumberDistributionRangeName(final double min, final double max) {
        final StringBuilder sb = new StringBuilder();

        sb.append(DECIMAL_FORMAT.format(min)).append(" - ").append(DECIMAL_FORMAT.format(max));
        return sb.toString();
    }

}
