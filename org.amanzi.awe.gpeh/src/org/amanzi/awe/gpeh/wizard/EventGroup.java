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

import org.amanzi.awe.gpeh.parser.Events;

//TODO: LN: comments!!!!!

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class EventGroup {
    private String eventGroupName;
    private ArrayList<Events> supportedEvents;

    public EventGroup() {
        supportedEvents = new ArrayList<Events>();
    }
    
    public void addSupportedEvent(Events event) {
        supportedEvents.add(event);
    }
    
    public ArrayList<Events> getSupportedEvents() {
        return supportedEvents;
    }


    /**
     * @param eventGroupName The eventGroupName to set.
     */
    public void setEventGroupName(String eventGroupName) {
        this.eventGroupName = eventGroupName;
    }


    /**
     * @return Returns the eventGroupName.
     */
    public String getEventGroupName() {
        return eventGroupName;
    }
}
