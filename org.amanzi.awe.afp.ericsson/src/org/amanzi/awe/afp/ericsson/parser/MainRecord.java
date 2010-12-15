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

import org.amanzi.awe.afp.ericsson.IParameters;
import org.amanzi.awe.afp.ericsson.IRecords;

/**
 * @author Kasnitskij_V
 *
 */
public class MainRecord {
	
    protected Record record;
    
    public MainRecord() {
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
    	private IRecords type;
        protected Map<IParameters, Object> properties;

        /**
         * 
         */
        public Record() {
        	properties = new HashMap<IParameters, Object>();
        }

        /**
         * @param parameter
         */
        public void addProperty(IParameters parameter, Object value) {
            if (value==null){
                return;
            }
            properties.put(parameter, value);
        }

        /**
         * @return Returns the properties.
         */
        public Map<IParameters, Object> getProperties() {
            return properties;
        }

        /**
         * @return Returns the type.
         */
        public IRecords getType() {
            return type;
        }

        /**
         * @param type The type to set.
         */
        public void setType(IRecords type) {
            this.type = type;
            this.properties = new HashMap<IParameters, Object>(type.getAllParameters().length);
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
