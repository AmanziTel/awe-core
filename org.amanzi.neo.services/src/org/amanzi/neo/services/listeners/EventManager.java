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

package org.amanzi.neo.services.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class EventManager {

    private static EventManager instance = null;

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    private HashMap<EventUIType, Set<IEventListener>> listeners;

    private EventManager() {
        listeners = new HashMap<EventUIType, Set<IEventListener>>();
    }

    public void addListener(IEventListener listener, EventUIType... eventTypes) {
        for (EventUIType type : eventTypes) {
            Set<IEventListener> list = listeners.get(type);
            if (list == null) {
                list = new HashSet<IEventListener>();
                listeners.put(type, list);
            }
            list.add(listener);
        }
    }

    public void removeListener(EventUIType eventType, IEventListener listener) {
        Set<IEventListener> list = listeners.get(eventType);
        if (list == null)
            return;
        list.remove(listener);
        if (list.size() == 0) {
            listeners.remove(eventType);
        }
    }
    
    public void notify(EventUIType eventType) {
        notify(eventType, null);
    }

    public void notify(EventUIType eventType, Object data) {
        Set<IEventListener> list = listeners.get(eventType);
        if (list == null)
            return;
        for (IEventListener listener : list) {
            listener.handleEvent(eventType, data);
        }
    }
}
