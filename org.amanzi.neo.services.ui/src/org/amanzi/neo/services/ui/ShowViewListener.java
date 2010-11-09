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

package org.amanzi.neo.services.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.events.ShowViewEvent;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Listener for ShowViewEvent and its subclasses.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ShowViewListener implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.SHOW_VIEW);
        spr.add(UpdateViewEventType.SHOW_PREPARED_VIEW);
        handedTypes = Collections.unmodifiableCollection(spr);
    }

    @Override
    public void updateView(UpdateViewEvent event) {
        switch (event.getType()) {
        case SHOW_VIEW:
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .showView(((ShowViewEvent)event).getUpdatedView());
            } catch (PartInitException e) {
                Logger.getLogger(this.getClass()).error(e.getLocalizedMessage(), e);
            }
            break;
        case SHOW_PREPARED_VIEW:
            ShowPreparedViewEvent spvEvent = (ShowPreparedViewEvent)event;
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(spvEvent.getUpdatedView());
            NeoServicesUiPlugin.getDefault().getUpdateViewManager().fireShowPreparedView(spvEvent);
            break;
        case IMPORT_STATISTICS:
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView("org.amanzi.awe.views.calls.views.CallAnalyserView");
            NeoServicesUiPlugin.getDefault().getUpdateViewManager().fireShowPreparedView((ShowViewEvent)event);
        default:
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }

}
