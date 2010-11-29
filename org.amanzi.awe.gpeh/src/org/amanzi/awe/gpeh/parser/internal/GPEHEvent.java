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

package org.amanzi.awe.gpeh.parser.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.amanzi.awe.gpeh.parser.Events;
import org.amanzi.awe.gpeh.parser.Parameters;

/**
 * <p>
 * GPEH event
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHEvent implements IGPEHBlock {
    protected Event event;

    /**
     * add event to event lists
     * 
     * @param event
     */
    public void addEvent(Event event) {
        this.event = event;
    }
    /**
     * clear all events
     * 
     * @param event
     */
    public void clearEvent() {
        event = null;
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
        protected long hour;
        protected long minute;
        protected long second;
        protected long millisecond;
        protected Integer id;
        protected String notParsed;
        protected Events type;

        protected Map<Parameters, Object> properties;
        public Integer scannerId;

        /**
         * 
         */
        public Event() {
//            properties = new HashMap<Parameters, Object>();
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
        public long getHour() {
            return hour;
        }

        /**
         * @return Returns the minute.
         */
        public long getMinute() {
            return minute;
        }

        /**
         * @return Returns the second.
         */
        public long getSecond() {
            return second;
        }

        /**
         * @return Returns the millisecond.
         */
        public long getMillisecond() {
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
//            this.properties = new HashMap<Parameters, Object>(type.getAllParameters().size());
        }

        /**
         *get full time of event
         * 
         * @param timestampOfDay timestamp - begin of day
         * @return full time
         */
        public long getFullTime(long timestampOfDay) {
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
    public Event getEvent() {
        return event;
    }

    private long size;
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public long getSize() {
        return size;
    }
}
