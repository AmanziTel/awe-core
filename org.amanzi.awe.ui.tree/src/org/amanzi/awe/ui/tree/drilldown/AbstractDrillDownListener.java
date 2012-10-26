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

package org.amanzi.awe.ui.tree.drilldown;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.tree.view.IAWETreeView;
import org.amanzi.neo.dto.IDataElement;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractDrillDownListener implements IAWEEventListenter {

    private final String viewId;

    protected AbstractDrillDownListener(final String viewId) {
        this.viewId = viewId;
    }

    @Override
    public void onEvent(final IEvent event) {
        if (event.getStatus().equals(EventStatus.SHOW_IN_VIEW)) {
            final ShowInViewEvent eventImpl = (ShowInViewEvent)event;

            if (eventImpl.getElement() instanceof IDataElement) {
                final IAWETreeView view = showView();

                if (view != null) {
                    if (view.show(eventImpl.getParent(), (IDataElement)eventImpl.getElement())) {
                        event.stop();
                    }
                }
            }
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    private IAWETreeView showView() {
        IViewPart view;
        try {
            view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);

            if (view instanceof IAWETreeView) {
                return (IAWETreeView)view;
            }
        } catch (final PartInitException e) {
            // do nothing
        }

        return null;
    }

}
