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

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.AWEStartedEvent;
import org.amanzi.awe.ui.events.impl.ProjectNameChangedEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class AWEEventManager {

    private static final Logger LOGGER = Logger.getLogger(AWEEventManager.class);

    private static class AWEEventManagerInstanceHandler {
        private static volatile AWEEventManager instance = new AWEEventManager();
    }

    private static final String AWE_LISTENER_EXTENSION_POINT_ID = "org.amanzi.awe.ui.listeners";

    private static final String EVENT_STATUS_CHILD = "eventStatus";

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String STATUS_ATTRIBUTE = "status";

    private static final IEvent AWE_STARTED_EVENT = new AWEStartedEvent();

    private final Map<EventStatus, List<IAWEEventListenter>> listeners = new HashMap<EventStatus, List<IAWEEventListenter>>();

    private final IExtensionRegistry registry;

    protected AWEEventManager() {
        this(Platform.getExtensionRegistry());
    }

    protected AWEEventManager(IExtensionRegistry registry) {
        this.registry = registry;
        initializeExtensionPointListeners();
    }

    public static AWEEventManager getManager() {
        return AWEEventManagerInstanceHandler.instance;
    }

    public synchronized void addListener(IAWEEventListenter listener, EventStatus... statuses) {
        assert listener != null;
        assert statuses != null;

        for (EventStatus eventStatus : statuses) {
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

    private void initializeExtensionPointListeners() {
        for (IConfigurationElement listenerElement : registry.getConfigurationElementsFor(AWE_LISTENER_EXTENSION_POINT_ID)) {
            try {
                initializeListenerFromElement(listenerElement);
            } catch (CoreException e) {
                LOGGER.error("Error on initialization Listener <" + listenerElement.getAttribute(CLASS_ATTRIBUTE) + ">", e);
            } catch (ClassCastException e) {
                LOGGER.error("Class <" + listenerElement.getAttribute(CLASS_ATTRIBUTE) + "> is not a Listener class", e);
            }
        }
    }

    private void initializeListenerFromElement(IConfigurationElement element) throws CoreException {
        IAWEEventListenter listener = (IAWEEventListenter)element.createExecutableExtension(CLASS_ATTRIBUTE);

        IConfigurationElement[] eventStatusElement = element.getChildren(EVENT_STATUS_CHILD);

        for (IConfigurationElement singleEventStatus : eventStatusElement) {
            EventStatus status = getEventStatusFromElement(singleEventStatus);

            if (status != null) {
                addListener(listener, status);
            }
        }
    }

    private EventStatus getEventStatusFromElement(IConfigurationElement element) {
        String statusName = element.getAttribute(STATUS_ATTRIBUTE);

        return EventStatus.valueOf(statusName);
    }

    protected Map<EventStatus, List<IAWEEventListenter>> getListeners() {
        return listeners;
    }
}
