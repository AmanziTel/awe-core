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

package org.amanzi.awe.ui.listener;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.apache.log4j.Logger;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * abstract show view listener
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractShowViewListener implements IAWEEventListenter {
    private static final Logger LOGGER = Logger.getLogger(AbstractShowViewListener.class);
    private final String REQUIRED_VIEW;
    private IViewPart view;

    protected AbstractShowViewListener(String requiredView) {
        this.REQUIRED_VIEW = requiredView;
    }

    @Override
    public void onEvent(IEvent event) {
        try {
            switch (event.getStatus()) {
            case SHOW_IN_VIEW:
                ShowInViewEvent showInView = (ShowInViewEvent)event;
                if (showInView.getViewId().equals(REQUIRED_VIEW)) {
                    view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(showInView.getViewId());
                    handleEventInView(showInView);
                }
                break;

            default:
                break;
            }
        } catch (PartInitException e) {
            LOGGER.error("Error when try to oppen view ", e);
        }
    }

    /**
     * @param showInView
     */
    protected abstract void handleEventInView(ShowInViewEvent showInView);

    /**
     * @return Returns the view.
     */
    protected IViewPart getView() {
        return view;
    }
}
