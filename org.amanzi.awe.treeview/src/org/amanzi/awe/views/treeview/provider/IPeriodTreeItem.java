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

package org.amanzi.awe.views.treeview.provider;

import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 * @param <T>
 * @param <E>
 */
public interface IPeriodTreeItem<T extends IModel, E extends Object> extends ITreeItem<T, E> {

    /**
     * @return Returns the endDate.
     */
    Long getEndDate();

    /**
     * @return Returns the startDate.
     */
    Long getStartDate();

    /**
     * @return Returns the period.
     */
    Period getPeriod();

}