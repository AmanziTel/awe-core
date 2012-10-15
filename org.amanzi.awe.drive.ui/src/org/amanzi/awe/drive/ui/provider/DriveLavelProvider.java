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

package org.amanzi.awe.drive.ui.provider;

import org.amanzi.awe.drive.ui.item.PeriodItem;
import org.amanzi.awe.ui.icons.IconManager;
import org.amanzi.awe.ui.label.AWELabelProvider;
import org.amanzi.neo.core.period.PeriodManager;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveLavelProvider extends AWELabelProvider {

    @Override
    public String getText(final Object element) {
        if (element instanceof PeriodItem) {
            final PeriodItem period = (PeriodItem)element;
            return PeriodManager.getPeriodName(period.getPeriod(), period.getStartTime(), period.getEndTime());
        } else {
            return super.getText(element);
        }
    }

    @Override
    public Image getImage(final Object element) {
        if (element instanceof PeriodItem) {
            return IconManager.getInstance().getImage("aggregation");
        } else {
            return super.getImage(element);
        }
    }

}
