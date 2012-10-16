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

package org.amanzi.awe.statistics.ui.filter.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.statistics.filter.IStatisticsFilter;
import org.amanzi.awe.statistics.model.IStatisticsModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class StatisticsFilterHandler {

    private static final class StatisticsFilterInstanceHandler {
        private static volatile StatisticsFilterHandler instance = new StatisticsFilterHandler();
    }

    private final Map<IStatisticsModel, IStatisticsFilter> filterMap = new HashMap<IStatisticsModel, IStatisticsFilter>();

    /**
     * 
     */
    private StatisticsFilterHandler() {
    }

    public static synchronized StatisticsFilterHandler getInstance() {
        return StatisticsFilterInstanceHandler.instance;
    }

    public synchronized void setFilter(IStatisticsModel model, IStatisticsFilter filter) {
        filterMap.put(model, filter);
    }

    public synchronized IStatisticsFilter getFilter(IStatisticsModel model) {
        return filterMap.get(model);
    }

}
