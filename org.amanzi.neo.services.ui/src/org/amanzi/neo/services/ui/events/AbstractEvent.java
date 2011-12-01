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

package org.amanzi.neo.services.ui.events;

import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * describe common functional for all events
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AbstractEvent {

    /**
     * type of event
     */
    protected EventsType type;

    /**
     * get event type
     * 
     * @return event type
     */
    protected EventsType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEvent other = (AbstractEvent)obj;
        if (type != other.type)
            return false;
        return true;
    }
}
