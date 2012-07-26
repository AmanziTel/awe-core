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

package org.amanzi.neo.loader.ui.page.widgets.internal;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget.IPageEventListener;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSelectDatasetNameWidget<E extends IPageEventListener> extends AbstractComboWidget<E>
        implements
            IAWEEventListenter {

    /**
     * @param isEditable
     * @param isEnabled
     * @param loaderPage
     * @param projectModelProvider
     */
    protected AbstractSelectDatasetNameWidget(final String labelText, final Composite parent, final E listener,
            final boolean isEditable, final boolean isEnabled, final IProjectModelProvider projectModelProvider) {
        super(labelText, isEditable, isEnabled, parent, listener, projectModelProvider);
        AWEEventManager.getManager().addListener(this, EventStatus.DATA_UPDATED);
    }

    @Override
    public void onEvent(final IEvent event) {
        switch (event.getStatus()) {
        case DATA_UPDATED:
            fillData();
            break;
        default:
            break;
        }
    }

    @Override
    public void finishUp() {
        AWEEventManager.getManager().removeListener(this);
        super.finishUp();
    }

}
