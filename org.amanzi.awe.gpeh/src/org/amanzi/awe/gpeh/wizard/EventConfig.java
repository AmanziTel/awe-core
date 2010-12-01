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

package org.amanzi.awe.gpeh.wizard;

import java.util.ArrayList;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class EventConfig {
    private ArrayList<EventGroup> eventGroups = new ArrayList<EventGroup>();
    
    public EventConfig() {
    }
    
    public void setEventGroups(ArrayList<EventGroup> eventGroups) {
        this.eventGroups = eventGroups;
    }
    
    public ArrayList<EventGroup> getEventGroups() {
        return eventGroups;
    }
    
    public void addEventGroup(EventGroup eventGroup) {
        eventGroups.add(eventGroup);
    }
}
