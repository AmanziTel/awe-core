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
package org.amanzi.awe.afp.ericsson.parser;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.Parameters;

/**
 * @author Kasnitskij_V
 *
 */
public class BARRecord {
	
    protected Record record;
    
    public BARRecord() {
    	record = new Record();
    }

    /**
     * add record to record lists
     * 
     * @param event
     */
    public void setRecord(Record record) {
        this.record = record;
    }
    
    /**
     * clear all events
     * 
     * @param event
     */
    public void clearEvent() {
        record = null;
    }

    /**
     * <p>
     * Record
     * </p>
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    public static class Record {
    	private BARRecords type;
        protected Map<Parameters, Object> properties;

        /**
         * 
         */
        public Record() {
        	properties = new HashMap<Parameters, Object>();
        }

        /**
         * @param parameter
         */
        public void addProperty(Parameters parameter, Object value) {
            if (value==null){
                return;
            }
            properties.put(parameter, value);
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
        public BARRecords getType() {
            return type;
        }

        /**
         * @param type The type to set.
         */
        public void setType(BARRecords type) {
            this.type = type;
            this.properties = new HashMap<Parameters, Object>(type.getAllParameters().length);
        }
    }

    /**
     * @return Returns the events.
     */
    public Record getEvent() {
        return record;
    }

    private long size;
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public long getSize() {
        return size;
    }
}
