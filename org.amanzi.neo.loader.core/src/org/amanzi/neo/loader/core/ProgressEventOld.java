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

package org.amanzi.neo.loader.core;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ProgressEventOld {
    
    public enum ProgressEventType {
        FILE, RECORD;
    }
    
    private ProgressEventType eventType;
    
    private long fullCount;
    
    private long handledCount;
    
    private String name;
    

    /**
     * @param eventType
     * @param fullCount
     * @param handledCount
     */
    public ProgressEventOld(ProgressEventType eventType, long fullCount, long handledCount) {
        super();
        this.eventType = eventType;
        this.fullCount = fullCount;
        this.handledCount = handledCount;
    }
    
    /**
     * @param eventType
     * @param fullCount
     * @param handledCount
     */
    public ProgressEventOld(ProgressEventType eventType, long fullCount, long handledCount, String name) {
        super();
        this.eventType = eventType;
        this.fullCount = fullCount;
        this.handledCount = handledCount;
        this.name = name;
    }


    /**
     * @return Returns the eventType.
     */
    public ProgressEventType getEventType() {
        return eventType;
    }


    /**
     * @return Returns the fullCount.
     */
    public long getFullCount() {
        return fullCount;
    }


    /**
     * @return Returns the handledCount.
     */
    public long getHandledCount() {
        return handledCount;
    }
    
    public String getProgressName() {
        return name;
    }
    

}
