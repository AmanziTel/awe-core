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

package org.amanzi.awe.ui.udig;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.internal.impl.ProjectRegistryImpl;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class UDIGProjectListener implements IAWEEventListenter, Adapter {

    private String previousProjectName = StringUtils.EMPTY;

    /**
     * 
     */
    public UDIGProjectListener() {
    }

    @Override
    public void onEvent(IEvent event) {
        switch (event.getStatus()) {
        case AWE_STARTED:
            // register UDIG listener
            ProjectRegistryImpl.getProjectRegistry().eAdapters().add(this);
            break;
        default:
            break;
        }
    }

    @Override
    public void notifyChanged(Notification notification) {
        if (notification.getEventType() == Notification.SET) {
            IProject project = (IProject)notification.getNewValue();

            String newProjectName = project.getName();
            if (!newProjectName.equals(previousProjectName)) {
                previousProjectName = newProjectName;

                AWEEventManager.getManager().fireProjectNameChangedEvent(newProjectName);
            }
        }
    }

    @Override
    public Notifier getTarget() {
        return null;
    }

    @Override
    public void setTarget(Notifier newTarget) {
    }

    @Override
    public boolean isAdapterForType(Object type) {
        return false;
    }

}
