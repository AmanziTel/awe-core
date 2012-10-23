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

package org.amanzi.awe.drive.ui.item;

import org.amanzi.awe.ui.dto.IPeriodItem;
import org.amanzi.awe.ui.tree.item.impl.TreeItem;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.measurement.IMeasurementModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PeriodItem extends TreeItem implements IPeriodItem {

    private final long startTime;

    private final Period period;

    /**
     * @param parent
     * @param child
     * @param wrapper
     */
    public PeriodItem(final IMeasurementModel parent, final ITreeWrapper wrapper, final Period period, final long startTime) {
        super(parent, null, wrapper);

        this.period = period;
        this.startTime = startTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return period.getEndTime(startTime);
    }

    @Override
    public Period getPeriod() {
        return period;
    }

}
