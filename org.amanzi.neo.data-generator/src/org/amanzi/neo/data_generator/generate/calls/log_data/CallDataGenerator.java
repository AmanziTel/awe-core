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

package org.amanzi.neo.data_generator.generate.calls.log_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;

/**
 * <p>
 * Common class for generate call data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallDataGenerator extends AmsDataGenerator {

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public CallDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        HashMap<Integer, List<Long>> hourMap = CallGeneratorUtils.buildHourMap(getHours(), getCalls(), getCallVariance(), getCallDurationBorders());
        for(Integer hour : hourMap.keySet()){
            for(Long setupDuration : hourMap.get(hour)){
                CallData call = buildCallCommands(group, hour, CallGeneratorUtils.createCall(getStartOfHour(hour), setupDuration,getCallPriority(),group,getAudioQualityBorders(),null, getMinCallDuration()));
                calls.add(call);
            }
        }
        return calls;
    }
    
    /**
     * @return minimum of call duration.
     */
    protected abstract Long getMinCallDuration();
    
    /**
     * @return audio quality borders {start,end}.
     */
    protected abstract float[] getAudioQualityBorders();  

    /**
     * @return Returns Call duration borders
     */
    protected abstract float[] getCallDurationBorders();


}
