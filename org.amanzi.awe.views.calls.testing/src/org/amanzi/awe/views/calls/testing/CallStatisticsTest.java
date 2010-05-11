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

package org.amanzi.awe.views.calls.testing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.neo.data_generator.data.calls.CallData;

/**
 * Abstract class for testing CallStatistics.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallStatisticsTest extends AmsStatisticsTest{
    
    private static final int PERIODS_COUNT = 8;
    private static final float MAX_CALL_DURATION = 1000;
    
    private float[] callDurationBorders;
    private float[] audioQualityBorders;
    
    
    
    
    /**
     * Gets call setup duration.
     *
     * @param call CallData
     * @param start Date (call start)
     * @return Long
     * @throws ParseException (problem in gets parameters)
     */
    protected abstract Float getCallSetupDuration(CallData call, Date start)throws ParseException;
    
    /**
     * Gets call duration.
     *
     * @param call CallData
     * @param start Date (call start)
     * @return Long
     * @throws ParseException (problem in gets parameters)
     */
    protected abstract Float getCallDuration(CallData call, Date start)throws ParseException;    
    
    /**
     * Gets call audio quality.
     *
     * @param call CallData
     * @param start Date (call start)
     * @return Long
     * @throws ParseException (problem in gets parameters)
     */
    protected abstract List<Float> getCallAudioQualitySorted(CallData call);
    
    /**
     * Gets number of call duration period.
     *
     * @param duration Long
     * @return Integer
     */
    protected Integer getSetupDurationPeriod(Float duration){
        float[] durationBorders = callDurationBorders;
        for(int i=0; i<PERIODS_COUNT; i++){
            float start = durationBorders[i];
            float end = durationBorders[i+1];
            if(start<=duration && duration<end){
                return i;
            }
        }
        return PERIODS_COUNT;
    }
    
    /**
     * Gets number of call duration period.
     *
     * @param duration Long
     * @return Integer
     */
    protected Integer getAudioQualityPeriod(Float audioQuality){
        for(int i=0; i<PERIODS_COUNT; i++){
            float start = audioQualityBorders[i+1];
            float end = audioQualityBorders[i];
            if(start<=audioQuality && audioQuality<end){
                return i;
            }
        }
        return PERIODS_COUNT-1;
    }
    
    protected HashMap<Integer, List<Float>> getAudioMap(List<Float> audioQuality){
        HashMap<Integer, List<Float>> result = new HashMap<Integer, List<Float>>(PERIODS_COUNT);
        for(Float curr : audioQuality){
            Integer period = getAudioQualityPeriod(curr);
            List<Float> list = result.get(period);
            if(list==null){
                list = new ArrayList<Float>();
                result.put(period, list);
            }
            list.add(curr);
        }
        return result;
    }
    
    /**
     * Initialize Call duration borders.
     */
    protected void initCallBorders(){
        callDurationBorders = new float[PERIODS_COUNT+2];
        ICallStatisticsConstants constants = getStatisticsConstants();
        callDurationBorders[0] = constants.getCallConnTimeP1();        
        callDurationBorders[1] = constants.getCallConnTimeP2();
        callDurationBorders[2] = constants.getCallConnTimeP3();
        callDurationBorders[3] = constants.getCallConnTimeP4();
        callDurationBorders[4] = constants.getCallConnTimeL1();
        callDurationBorders[5] = constants.getCallConnTimeL2();
        callDurationBorders[6] = constants.getCallConnTimeL3();
        callDurationBorders[7] = constants.getCallConnTimeL4();
        callDurationBorders[8] = constants.getCallConnTimeLimit();
        callDurationBorders[9] = MAX_CALL_DURATION;
        audioQualityBorders = new float[PERIODS_COUNT+1];
        audioQualityBorders[0] = constants.getIndivCallQualMax();        
        audioQualityBorders[1] = constants.getIndivCallQualP1();
        audioQualityBorders[2] = constants.getIndivCallQualP2();
        audioQualityBorders[3] = constants.getIndivCallQualP3();
        audioQualityBorders[4] = constants.getIndivCallQualP4();
        audioQualityBorders[5] = constants.getIndivCallQualL1();
        audioQualityBorders[6] = constants.getIndivCallQualL2();
        audioQualityBorders[7] = constants.getIndivCallQualL3();
        audioQualityBorders[8] = constants.getIndivCallQualMin();
    }
    
    /**
     * @return Statistics constants for concrete type.
     */
    protected abstract ICallStatisticsConstants getStatisticsConstants();

}
