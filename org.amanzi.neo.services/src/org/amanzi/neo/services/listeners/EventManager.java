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

    private HashMap<AbstractUIEventType, Set<IEventListener>> listeners;

    private EventManager() {
        listeners = new HashMap<AbstractUIEventType, Set<IEventListener>>();
    }

    public void addListener(AbstractUIEventType eventType, IEventListener listener) {
        Set<IEventListener> list = listeners.get(eventType);
        if (list == null) {
            list = new HashSet<IEventListener>();
            listeners.put(eventType, list);
        }
        list.add(listener);
    }

    public void removeListener(AbstractUIEventType eventType, IEventListener listener) {
        Set<IEventListener> list = listeners.get(eventType);
        if (list == null)
            return;
        list.remove(listener);
        if (list.size() == 0) {
            listeners.remove(eventType);
        }
    }
    
    public void notify(AbstractUIEvent event) {
        Set<IEventListener> list = listeners.get(event.getType());
        if (list == null)
            return;
        for (IEventListener listener : list) {
            listener.handleEvent(event);
        }
    }
}
