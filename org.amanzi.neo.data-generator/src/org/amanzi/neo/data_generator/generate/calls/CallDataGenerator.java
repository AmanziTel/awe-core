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

package org.amanzi.neo.data_generator.generate.calls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;

/**
 * <p>
 * Common class for generate call data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallDataGenerator extends AmsDataGenerator {
    
    protected static final int CALL_DURATION_PERIODS_COUNT = 8;

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
        HashMap<Integer, List<Long>> hourMap = buildHourMap();
        for(Integer hour : hourMap.keySet()){
            for(Long duration : hourMap.get(hour)){
                CallData call = buildCall(group, hour, duration);
                calls.add(call);
            }
        }
        return calls;
    }
    
    /**
     * Build map of calls duration in all hours.
     *
     * @return HashMap<Integer, List<Long>> (key - hour, value - list of durations).
     */
    private HashMap<Integer, List<Long>> buildHourMap(){
        int hours = getHours();
        int calls = getCalls();
        int callVariance = getCallVariance();
        HashMap<Integer, List<Long>> result = new HashMap<Integer, List<Long>>(hours);
        for(int i = 0; i<hours; i++){
            int count = calls + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            result.put(i, buildDurationMap(count));
        }
        return result;
    }
    
    /**
     * Build map of calls duration in one hour.
     *
     * @param allCountPerHour Integer (call count)
     * @return list of durations
     */
    protected List<Long> buildDurationMap(Integer allCountPerHour){
        List<Long> result = new ArrayList<Long>(allCountPerHour);
        RandomValueGenerator generator = RandomValueGenerator.getGenerator();
        for(int i=0; i<allCountPerHour; i++) {
            int period = generator.getIntegerValue(0, CALL_DURATION_PERIODS_COUNT);
            Long[] borders = getPeriodBorders(period);
            Long duration = generator.getLongValue(borders[0], borders[1]);            
            result.add(duration);
        }
        return result;
    }
    
    /**
     * Returns borders of duration period.
     *
     * @param period Integer (period number)
     * @return Long[]
     */
    private Long[] getPeriodBorders(Integer period){
        float[] durationBorders = getCallDurationBorders();
        Long start = (long)(durationBorders[period]*MILLISECONDS);
        Long end = (long)(durationBorders[period+1]*MILLISECONDS);
        return new Long[]{start,end};
    }
    

    /**
     * @return Returns Call duration borders
     */
    protected abstract float[] getCallDurationBorders();


}
