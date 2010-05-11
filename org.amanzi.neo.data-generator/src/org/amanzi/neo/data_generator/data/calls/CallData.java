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

package org.amanzi.neo.data_generator.data.calls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



/**
 * <p>
 * Data saver for one call.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallData {
    
    private Long key;
    private ProbeData sourceProbe;
    private List<ProbeData> receiverProbes;
    
    private Date startTime;
    private List<Long> callTimes;
    private Integer priority;
    
    /**
     * Constructor.
     * @param aKey Long
     * @param first ProbeData source data
     * @param second ProbeData receiver data
     */
    public CallData(Long aKey,ProbeData source,ProbeData... receivers) {
        key = aKey;
        sourceProbe = source;
        receiverProbes = Arrays.asList(receivers);
        callTimes = new ArrayList<Long>();
    }
    
    /**
     * Getter for call key.
     *
     * @return Long
     */
    public Long getKey(){
        return key;
    }
    
    /**
     * Getter for source data.
     *
     * @return ProbeData
     */
    public ProbeData getSourceProbe(){
        return sourceProbe;
    }
    
    /**
     * Getter for receiver data.
     *
     * @return ProbeData
     */
    public List<ProbeData> getReceiverProbes(){
        return receiverProbes;
    }
    
    /**
     * @return Returns the startTime.
     */
    public Date getStartTime() {
        return startTime;
    }
    
    /**
     * @param startTime The startTime to set.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    /**
     * @return Returns the callTimes.
     */
    public List<Long> getCallTimes() {
        return callTimes;
    }
    
    public void addTime(Long time){
        callTimes.add(time);
    }
    
    /**
     * @return Returns the priority.
     */
    public Integer getPriority() {
        return priority;
    }
    
    /**
     * @param priority The priority to set.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
}
