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

package org.amanzi.awe.ui.view;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractAWEView extends ViewPart implements IAWEEventListenter {

    private static final EventStatus[] SUPPORTED_EVENTS = {EventStatus.DATA_UPDATED, EventStatus.PROJECT_CHANGED};

    private final IProjectModelProvider projectModelProvider;

    protected AbstractAWEView(final IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void onEvent(final IEvent event) {
        if (ArrayUtils.contains(SUPPORTED_EVENTS, event.getStatus())) {
            update();
        }
    }

    @Override
    public void createPartControl(final Composite parent) {


        addEvents();
    }

    protected void addEvents() {
        AWEEventManager.getManager().addListener(this, SUPPORTED_EVENTS);
    }

    @Override
    public void dispose() {
        AWEEventManager.getManager().removeListener(this);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

    protected abstract void update();

    protected IProjectModel getActiveProject() {
        return projectModelProvider.getActiveProjectModel();
    }

}
