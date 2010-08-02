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
import java.util.LinkedHashSet;
import java.util.Map;

import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;

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
    private boolean valid = true;

    /**
     * constructor
     */
    public GPEHEvent() {
        valid = true;
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
     * clear all events
     * 
     * @param event
     */
    public void clearEvent() {
        valid = true;
        events.clear();
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
        public Integer scannerId;

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
        public void addProperty(Parameters parameter, Object value) {
            if (value==null){
                return;
            }
            properties.put(parameter, value);
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

        /**
         *gets set of cells id for event
         * @return
         */
        public LinkedHashSet<Integer> getCellId() {
            LinkedHashSet<Integer> result=new LinkedHashSet<Integer>();
            for (Parameters parametr: new Parameters[]{Parameters.EVENT_PARAM_C_ID_1,  Parameters.EVENT_PARAM_C_ID_2,  Parameters.EVENT_PARAM_C_ID_3, Parameters.EVENT_PARAM_C_ID_4}){
                final Object value = properties.get(parametr);
                if (value!=null){
                    result.add((Integer)value);
                }
            }
            return result;
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

    /**
     * Sets the valid.
     * 
     * @param b the new valid
     */
    public void setValid(boolean isValid) {
        this.valid = isValid;
    }

    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public boolean isValid() {
        return valid;
    }

}
