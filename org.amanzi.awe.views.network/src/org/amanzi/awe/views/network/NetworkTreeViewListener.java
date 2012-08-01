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

package org.amanzi.awe.views.network;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.views.network.view.NetworkTreeView;
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
public class NetworkTreeViewListener implements IAWEEventListenter {

    @Override
    public void onEvent(IEvent event) {
        try {
            switch (event.getStatus()) {
            case SHOW_IN_VIEW:
                ShowInViewEvent showInView = (ShowInViewEvent)event;
                // TODO: LN: 01.08.2012, create abstract listener that will handle opening view
                if (showInView.getViewId().equals(NetworkTreeView.NETWORK_TREE_VIEW_ID)) {
                    NetworkTreeView view = (NetworkTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .showView(showInView.getViewId());
                    view.selectDataElement(showInView.getElement());
                }
                break;

            default:
                break;
            }
        } catch (PartInitException e) {
            // TODO: LN: 01.08.2012, handle this exception
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
