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

package org.amanzi.neo.db.manager.events;

/**
 * Event related to any changes in Database 
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class DatabaseEvent {
    
    /**
     * Type of Database Activity
     * 
     * @author gerzog
     * @since 1.0.0
     */
    public static enum EventType {
        /*
         * DB going to start
         */
        BEFORE_STARTUP,
        
        /*
         * DB was started
         */
        AFTER_STARTUP,
        
        /*
         * DB going to stop
         */
        BEFORE_SHUTDOWN,
        
        /*
         * DB was stopped
         */
        AFTER_SHUTDOWN,
        
        /*
         * DB Manager commit was called
         */
        BEFORE_FULL_COMMIT,
        
        /*
         * DB Manager commit finished
         */
        AFTER_FULL_COMMIT,
        
        /*
         * DB Manager rollback was called
         */
        BEFORE_FULL_ROLLBACK,
        
        /*
         * DB Manager rollback finished
         */
        AFTER_FULL_ROLLBACK,
        
        /*
         * Single Transaction commit was called
         */
        BEFORE_SINGLE_COMMIT,
        
        /*
         * Single Transaction commit was finished
         */
        AFTER_SINGLE_COMMIT,
        
        /*
         * Single Transaction rollback was finished
         */
        AFTER_SINGLE_ROLLBACK;
    }
    
    /*
     * type of event
     */
    private EventType eventType;
    
    /**
     * Simple constructor
     * 
     * @param eventType type of database event
     */
    public DatabaseEvent(EventType eventType) {
        this.eventType = eventType;
    }
    
    /**
     * Returns type of this event
     *
     * @return type of event
     */
    public EventType getEventType() {
        return eventType;
    }
    
    @Override
    public String toString() {
        return getEventType().name();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DatabaseEvent) {
            DatabaseEvent anotherEvent = (DatabaseEvent)o;
            
            return getEventType().equals(anotherEvent.getEventType());
        }
        
        return false;
    }
}
