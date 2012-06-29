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

package org.amanzi.awe.ui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.IEventStatus;
import org.amanzi.awe.ui.events.impl.AWEStartedEvent;
import org.amanzi.awe.ui.events.impl.ProjectNameChangedEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class AWEEventManager {

    private static class AWEEventManagerInstanceHandler {
        private static volatile AWEEventManager instance = new AWEEventManager();
    }

    private static final IEvent AWE_STARTED_EVENT = new AWEStartedEvent();

    private final Map<IEventStatus, List<IAWEEventListenter>> listeners = new HashMap<IEventStatus, List<IAWEEventListenter>>();

    private AWEEventManager() {
        // hide constructor
    }

    public static AWEEventManager getManager() {
        return AWEEventManagerInstanceHandler.instance;
    }

    public synchronized void addListener(IAWEEventListenter listener, IEventStatus... statuses) {
        assert listener != null;
        assert statuses != null;

        for (IEventStatus eventStatus : statuses) {
            List<IAWEEventListenter> eventListeners = listeners.get(eventStatus);

            if (eventListeners == null) {
                eventListeners = new ArrayList<IAWEEventListenter>();
                listeners.put(eventStatus, eventListeners);
            }

            if (!eventListeners.contains(listener)) {
                eventListeners.add(listener);
            }
        }
    }

    private void fireEvent(IEvent event) {
        List<IAWEEventListenter> eventListeners = listeners.get(event.getStatus());

        if (eventListeners != null) {
            for (IAWEEventListenter singleListener : eventListeners) {
                singleListener.onEvent(event);
            }
        }
    }

    public synchronized void fireAWEStartedEvent() {
        fireEvent(AWE_STARTED_EVENT);
    }

    public synchronized void fireProjectNameChangedEvent(String newProjectName) {
        fireEvent(new ProjectNameChangedEvent(newProjectName));
    }

}
