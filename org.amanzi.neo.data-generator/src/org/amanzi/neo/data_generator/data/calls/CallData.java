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
    
    private Long startTime;
    private List<Call> calls;
    
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
        calls = new ArrayList<Call>();
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
    public Long getStartTime() {
        return startTime;
    }
    
    /**
     * @return Returns the calls.
     */
    public List<Call> getCalls() {
        return calls;
    }
    
    /**
     * Add call to data.
     *
     * @param call
     */
    public void addCall(Call call){
        calls.add(call);
        if(startTime==null||call.getStartTime()<startTime){
            startTime = call.getStartTime();
        }
    }
    
    
    
}
