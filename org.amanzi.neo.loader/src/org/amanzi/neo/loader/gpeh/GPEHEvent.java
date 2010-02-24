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

import org.amanzi.neo.core.enums.gpeh.Events;
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
     * constructor
     */
    public GPEHEvent() {
        events = new ArrayList<Event>();
    }

    /**
     * add event to event lists
     * 
     * @param event
     */
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
        protected Events type;

        protected Map<Parameters, Object> properties;

        /**
         * 
         */
        public Event() {
            properties = new HashMap<Parameters, Object>();
        }

        /**
         * @param parameter
         * @param bitSet
         */
        public void addProperty(Parameters parameter, String bitSet) {
            properties.put(parameter, parameter.pareseBits(bitSet));
        }

        /**
         * @return Returns the hour.
         */
        public int getHour() {
            return hour;
        }

        /**
         * @return Returns the minute.
         */
        public Integer getMinute() {
            return minute;
        }

        /**
         * @return Returns the second.
         */
        public Integer getSecond() {
            return second;
        }

        /**
         * @return Returns the millisecond.
         */
        public Integer getMillisecond() {
            return millisecond;
        }

        /**
         * @return Returns the id.
         */
        public Integer getId() {
            return id;
        }

        /**
         * @return Returns the notParsed.
         */
        public String getNotParsed() {
            return notParsed;
        }

        /**
         * @return Returns the properties.
         */
        public Map<Parameters, Object> getProperties() {
            return properties;
        }

        /**
         * @return Returns the type.
         */
        public Events getType() {
            return type;
        }

        /**
         * @param type The type to set.
         */
        public void setType(Events type) {
            this.type = type;
        }

        /**
         *get full time of event
         * 
         * @param timestampOfDay timestamp - begin of day
         * @return full time
         */
        public Long getFullTime(long timestampOfDay) {
            return timestampOfDay + (hour * 60 * 60 + minute * 60 + second) * 1000 + millisecond;
        }
    }

    @Override
    public void setEndRecord(GPEHEnd end) {
    }

    /**
     * @return Returns the events.
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

}
