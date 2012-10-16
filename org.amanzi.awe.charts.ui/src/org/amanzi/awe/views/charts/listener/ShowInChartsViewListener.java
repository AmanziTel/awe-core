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

package org.amanzi.awe.views.charts.listener;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.ui.filter.IStatisticsFilter;
import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.views.charts.ChartsView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowInChartsViewListener implements IAWEEventListenter {

    @Override
    public void onEvent(final IEvent event) {
        if (event.getStatus().equals(EventStatus.SHOW_IN_VIEW)) {
            ShowInViewEvent showInViewEvent = (ShowInViewEvent)event;

            if ((showInViewEvent.getParent() instanceof IStatisticsModel)
                    && (showInViewEvent.getElement() instanceof IStatisticsFilter)) {
                ChartsView view = showChartsView();

                if (view != null) {
                    showInView(view, (IStatisticsModel)showInViewEvent.getParent(),
                            (IStatisticsFilter)showInViewEvent.getElement());
                }
            }
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    private ChartsView showChartsView() {
        try {
            return (ChartsView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChartsView.VIEW_ID);
        } catch (PartInitException e) {
            return null;
        }
    }

    private void showInView(final ChartsView chartsView, final IStatisticsModel model, final IStatisticsFilter filter) {
        chartsView.fireStatisticsChanged(model, filter);
    }

}
