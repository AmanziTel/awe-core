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

package org.amanzi.neo.data_generator.generate.calls.xml_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Common class for all calls data generator for xml files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallXmlDataGenerator extends AmsXmlDataGenerator{
    
    private static final String TOC_TAG_NAME = "toc";
    private static final String TTC_TAG_NAME = "ttc";
    
    private static final String TAG_PR_HOOK = "hook";
    private static final String TAG_PR_SIMPLEX = "simplex"; 
    private static final String TAG_PR_PRIORITY = "priority"; 
    private static final String TAG_PR_CONFIG_TIME = "configTime"; 
    private static final String TAG_PR_SETUP_TIME = "setupTime"; 
    private static final String TAG_PR_CONNECT_TIME = "connectTime"; 
    private static final String TAG_PR_DISCONNECT_TIME = "disconnectTime"; 
    private static final String TAG_PR_RELEASE_TIME = "releaseTime"; 
    private static final String TAG_PR_TERMINATION_CAUSE = "causeForTermination";    
    
    private static final String TAG_PR_INDICATION_TIME = "indicationTime"; 
    private static final String TAG_PR_ANSWER_TIME = "answerTime"; 
    
    private static final String PESQ_TAG_NAME = "pesqResult";
    private static final String TAG_PR_PESQ_TIME = "sendSampleStart";
    private static final String TAG_PR_PESQ = "pesq"; 
    private static final String TAG_PR_DELAY = "delay";

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public CallXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        HashMap<Integer, List<Long>> hourMap = CallGeneratorUtils.buildHourMap(getHours(), getCalls(), getCallVariance(), getCallDurationBorders());
        for(Integer hour : hourMap.keySet()){
            for(Long setupDuration : hourMap.get(hour)){
                Call call = CallGeneratorUtils.createCall(getStartOfHour(hour), setupDuration,getCallPriority(),group,getAudioQualityBorders(),getAudioDelayBorders(),getMinCallDuration());
                CallData callData = buildCallCommands(group, hour, call);
                calls.add(callData);
            }
        }
        return calls;
    }
    
    protected SavedTag getTocTag(Probe source, String number, Call call, int hook, int simplex, int termCause, Long... times){
        SavedTag result = new SavedTag(TOC_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+source.getName())); 
        result.addInnerTag(getPropertyTag(TAG_PR_CALLED_NUMBER, number));  
        result.addInnerTag(getPropertyTag(TAG_PR_HOOK, hook));  
        result.addInnerTag(getPropertyTag(TAG_PR_SIMPLEX, simplex)); 
        result.addInnerTag(getPropertyTag(TAG_PR_PRIORITY, call.getPriority())); 
        result.addInnerTag(getPropertyTag(TAG_PR_CONFIG_TIME, getTimeString(times[0])));  
        result.addInnerTag(getPropertyTag(TAG_PR_SETUP_TIME, getTimeString(times[1])));  
        result.addInnerTag(getPropertyTag(TAG_PR_CONNECT_TIME, getTimeString(times[2])));  
        result.addInnerTag(getPropertyTag(TAG_PR_DISCONNECT_TIME, getTimeString(times[3])));  
        result.addInnerTag(getPropertyTag(TAG_PR_RELEASE_TIME, getTimeString(times[4])));  
        result.addInnerTag(getPropertyTag(TAG_PR_TERMINATION_CAUSE, termCause));  
        addPesqTags(result, call, source, times[5], times[6]);
        return result;
    }
    
    protected SavedTag getTtcTag(Probe source, Probe received, boolean putRecName, Call call, int hook, int simplex, int termCause, Long... times){
        SavedTag result = new SavedTag(TTC_TAG_NAME, false);
        if (putRecName) {
            result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+received.getName()));
        }else{
            result.addInnerTag(getEmptyTag(TAG_PR_PROBE_ID));
        }
        result.addInnerTag(getPropertyTag(TAG_PR_CALLING_NUMBER, source.getPhoneNumber()));  
        result.addInnerTag(getPropertyTag(TAG_PR_HOOK, hook));  
        result.addInnerTag(getPropertyTag(TAG_PR_SIMPLEX, simplex)); 
        result.addInnerTag(getPropertyTag(TAG_PR_INDICATION_TIME, getTimeString(times[0]))); 
        result.addInnerTag(getPropertyTag(TAG_PR_ANSWER_TIME, getTimeString(times[1])));  
        result.addInnerTag(getPropertyTag(TAG_PR_CONNECT_TIME, getTimeString(times[2])));  
        result.addInnerTag(getPropertyTag(TAG_PR_RELEASE_TIME, getTimeString(times[3])));  
        result.addInnerTag(getPropertyTag(TAG_PR_TERMINATION_CAUSE, termCause));
        addPesqTags(result, call, received, times[3], times[4]);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private void addPesqTags(SavedTag tag, Call call, Probe probe, Long start, Long end){
        List<Float> audioQuals = (List<Float>)call.getParameter(CallParameterNames.AUDIO_QUALITY+probe.getName());
        List<Integer> audioDelays = (List<Integer>)call.getParameter(CallParameterNames.AUDIO_DELAY+probe.getName());
        Long time = start;
        for(int i=0; i<audioQuals.size(); i++){
            time = CallGeneratorUtils.getRamdomTime(time, end);
            float audioQual = audioQuals.get(i);
            int audioDelay = audioDelays.get(i);
            SavedTag pesq = new SavedTag(PESQ_TAG_NAME, false);
            pesq.addInnerTag(getPropertyTag(TAG_PR_PESQ_TIME, getTimeString(time))); 
            pesq.addInnerTag(getPropertyTag(TAG_PR_PESQ, audioQual));
            pesq.addInnerTag(getPropertyTag(TAG_PR_DELAY, audioDelay));
            tag.addInnerTag(pesq);
        }
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
     * @return audio delay borders {start,end}.
     */
    protected abstract int[] getAudioDelayBorders(); 

    /**
     * @return Returns Call duration borders
     */
    protected abstract float[] getCallDurationBorders();

}
