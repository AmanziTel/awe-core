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
import java.util.List;

import org.amanzi.awe.ui.events.IEvent;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class EventChain {

    private final boolean isAsync;

    private final List<IEvent> events = new ArrayList<IEvent>();

    /**
     * @param status
     * @param isAsync
     */
    public EventChain(final boolean isAsync) {
        this.isAsync = isAsync;
    }

    public void addEvent(final IEvent event) {
        events.add(event);
    }

    public List<IEvent> getEvents() {
        return events;
    }

    public boolean isAsync() {
        return isAsync;
    }

}
