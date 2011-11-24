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

package org.amanzi.awe.ui;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.internal.impl.ProjectRegistryImpl;

import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.EventUIType;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IStartup;

/**
 * <p>
 * Manager for handling uDig events
 * </p>
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class UdigEventManager implements IStartup {
    
    public void registerProjectChangedEvent() {
        ProjectRegistryImpl.getProjectRegistry().eAdapters().add(new Adapter() {

            @Override
            public Notifier getTarget() {
                return null;
            }

            @Override
            public boolean isAdapterForType(Object arg0) {
                return false;
            }

            @Override
            public void notifyChanged(Notification arg0) {
                if (arg0.getEventType() == Notification.SET) {
                    IProject project = (IProject)arg0.getNewValue();
                    EventManager.getInstance().notify(EventUIType.PROJECT_CHANGED, project.getName());
                }
            }

            @Override
            public void setTarget(Notifier arg0) {
            }
        });

    }

    @Override
    public void earlyStartup() {
        registerProjectChangedEvent();
    }
    
}
