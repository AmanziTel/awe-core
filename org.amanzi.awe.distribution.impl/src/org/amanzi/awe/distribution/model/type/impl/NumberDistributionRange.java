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

/**
 * <p>
 * Enum for NumberDistributionType
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public enum NumberDistributionRange {

    I10(10), I50(50);

    /** in NumberDistribution, step of ranges = (max - min) / delta */
    private int delta;

    private NumberDistributionRange(final int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}
