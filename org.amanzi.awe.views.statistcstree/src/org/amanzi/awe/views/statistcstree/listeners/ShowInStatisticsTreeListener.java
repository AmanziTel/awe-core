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

package org.amanzi.awe.views.statistcstree.listeners;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.views.statistcstree.view.StatisticsTreeView;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ShowInStatisticsTreeListener implements IAWEEventListenter {

    @Override
    public void onEvent(final IEvent event) {
        if (event.getStatus().equals(EventStatus.SHOW_IN_VIEW)) {
            ShowInViewEvent showInViewEvent = (ShowInViewEvent)event;

            if (showInViewEvent.getParent() instanceof IStatisticsModel) {
                StatisticsTreeView view = showStatisticsView();

                if (view != null) {
                    showInView(view, showInViewEvent.getParent(), showInViewEvent.getElement());
                }
            }
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    private StatisticsTreeView showStatisticsView() {
        try {
            return (StatisticsTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(StatisticsTreeView.VIEW_ID);
        } catch (PartInitException e) {
            return null;
        }
    }

    private void showInView(final StatisticsTreeView statisticsTreeView, final IModel model, final IDataElement element) {
        statisticsTreeView.showElement(model, element);
    }

}
