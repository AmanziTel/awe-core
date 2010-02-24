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

package org.amanzi.neo.loader.gpeh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.loader.IGPEHBlock;

/**
 * <p>
 * GPEH event
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHEvent implements IGPEHBlock {
    protected ArrayList<Event> events;

    /**
     * 
     */
    public GPEHEvent() {
        events = new ArrayList<Event>();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * <p>
     * Event
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class Event {
        protected int hour;
        protected Integer minute;
        protected Integer second;
        protected Integer millisecond;
        protected Integer id;
        protected String notParsed;
        protected Map<Parameters, Object> properties;

        /**
         * 
         */
        public Event() {
            properties=new HashMap<Parameters, Object>();
        }

        /**
         * @param parameter
         * @param bitSet
         */
        public void addProperty(Parameters parameter, String bitSet) {
            properties.put(parameter, parameter.pareseBits(bitSet));
        }
    }

    @Override
    public void setEndRecord(GPEHEnd end) {
    }

}
