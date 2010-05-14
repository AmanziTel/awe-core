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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;

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
     * Gets call audio quality.
     *
     * @param call CallData
     * @param start Date (call start)
     * @return Long
     * @throws ParseException (problem in gets parameters)
     */
    @SuppressWarnings("unchecked")
    protected List<Float> getCallAudioQualitySorted(Call call){
        List<Float> result = new ArrayList<Float>();
        HashMap<String, Object> parameters = call.getParameters();
        for(String key : parameters.keySet()){
            if(key.startsWith(CallParameterNames.AUDIO_QUALITY)){
                result.addAll((List<Float>)parameters.get(key));
            }
        }
        Collections.sort(result);
        return result;
    }
    
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
    
    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        ICallStatisticsConstants constants = getStatisticsConstants();
        Float setupDuration = ((Long)call.getParameter(CallParameterNames.SETUP_TIME)).floatValue()/MILLISECONDS;
        result.put(StatisticsHeaders.CALL_ATTEMPT_COUNT,1L);
        if (setupDuration<constants.getCallConnTimeLimit()) {
            result.put(StatisticsHeaders.SUCC_SETUP_COUNT,1L);
            Integer setupPeriod = getSetupDurationPeriod(setupDuration);
            switch (setupPeriod) {
            case 0:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P1,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P1,setupDuration);
                break;
            case 1:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P2,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P2,setupDuration);
                break;
            case 2:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P3,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P3,setupDuration);
                break;
            case 3:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P4,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P4,setupDuration);
                break;
            case 4:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L1,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L1,setupDuration);
                break;
            case 5:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L2,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L2,setupDuration);
                break;
            case 6:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L3,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L3,setupDuration);
                break;
            case 7:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L4,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L4,setupDuration);
            default:
                break;
            }
            result.put(StatisticsHeaders.SETUP_TIME_MIN,setupDuration);
            result.put(StatisticsHeaders.SETUP_TIME_MAX,setupDuration);
            result.put(StatisticsHeaders.SETUP_TOTAL_DUR,setupDuration);
            Float callDuration = ((Long)call.getParameter(CallParameterNames.DURATION_TIME)).floatValue()/MILLISECONDS;;
            if(callDuration>=constants.getIndivCallDurationTime()){
                result.put(StatisticsHeaders.CALL_DISC_TIME,1L);
            }
            List<Float> audioQuality = getCallAudioQualitySorted(call);
            if (!audioQuality.isEmpty()) {
                if(audioQuality.get(0)>=constants.getIndivCallQualLimit()){
                    result.put(StatisticsHeaders.AUDIO_QUAL_SUCC,1L);
                }                
                HashMap<Integer, List<Float>> audioMap = getAudioMap(audioQuality);
                List<Float> list = audioMap.get(0);
                result.put(StatisticsHeaders.AUDIO_QUAL_P1,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P1,getSumFromList(list));
                list = audioMap.get(1);
                result.put(StatisticsHeaders.AUDIO_QUAL_P2,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P2,getSumFromList(list));
                list = audioMap.get(2);
                result.put(StatisticsHeaders.AUDIO_QUAL_P3,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P3,getSumFromList(list));
                list = audioMap.get(3);
                result.put(StatisticsHeaders.AUDIO_QUAL_P4,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P4,getSumFromList(list));
                list = audioMap.get(4);
                result.put(StatisticsHeaders.AUDIO_QUAL_L1,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L1,getSumFromList(list));
                list = audioMap.get(5);
                result.put(StatisticsHeaders.AUDIO_QUAL_L2,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L2,getSumFromList(list));
                list = audioMap.get(6);
                result.put(StatisticsHeaders.AUDIO_QUAL_L3,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L3,getSumFromList(list));
                list = audioMap.get(7);
                result.put(StatisticsHeaders.AUDIO_QUAL_L4,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L4,getSumFromList(list));

                result.put(StatisticsHeaders.AUDIO_QUAL_MIN,audioQuality.get(0));
                result.put(StatisticsHeaders.AUDIO_QUAL_MAX,audioQuality.get(audioQuality.size()-1));
                result.put(StatisticsHeaders.AUDIO_QUAL_TOTAL,getSumFromList(audioQuality));
            }
            HashMap<IStatisticsHeader, Number> audioDelayStatistics = getAudioDelayStatistics(call);
            if(audioDelayStatistics!=null){
                result.putAll(audioDelayStatistics);
            }
        }
        return result;
    }
    
    @Override
    protected boolean hasSecondLevelStatistics() {
        return true;
    }
    
    /**
     * @return Statistics constants for concrete type.
     */
    protected abstract ICallStatisticsConstants getStatisticsConstants();
    
    /**
     * Get statistics for audio sample delay.
     *
     * @param call Call
     * @return HashMap<IStatisticsHeader, Number>
     */
    protected abstract HashMap<IStatisticsHeader, Number> getAudioDelayStatistics(Call call);

}
