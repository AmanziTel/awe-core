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

package org.amanzi.awe.views.statistics.table;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsTableProvider implements IStructuredContentProvider {

    private static final Logger LOGGER = Logger.getLogger(StatisticsTableProvider.class);

    private Period period;

    /**
     * 
     */
    public StatisticsTableProvider() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if ((period != null) && (inputElement instanceof IStatisticsModel)) {
            IStatisticsModel statisticsModel = (IStatisticsModel)inputElement;
            try {
                return IteratorUtils.toArray(statisticsModel.getStatisticsRows(period.getId()).iterator());
            } catch (ModelException e) {
                LOGGER.error("Error on getting Statistics Table content", e);
            }
        }
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    public void setPeriod(final Period period) {
        this.period = period;
    }
}
