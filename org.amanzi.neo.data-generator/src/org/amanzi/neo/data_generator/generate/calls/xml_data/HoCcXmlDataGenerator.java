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

import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.data.calls.CallXmlData;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Generate data for Handover/Cell change calls (xml).
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class HoCcXmlDataGenerator extends IndividualCallXmlDataGenerator{
    
    private static final String HO_TAG_NAME = "handover";
    private static final String CC_TAG_NAME = "cellResel";
    
    private static final String TAG_PR_HO_REC_TIME = "ho_Req";
    private static final String TAG_PR_HO_ACC_TIME = "ho_Accept";
    
    private static final String TAG_PR_CC_REC_TIME = "cellReselReq";
    private static final String TAG_PR_CC_ACC_TIME = "cellReselAccept";

    /**
     * Constructor.
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public HoCcXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    @Override
    protected Call createCall(CallGroup group,Long start,Long end, Long setupDuration) {
        Call call = CallGeneratorUtils.createCallWithHoCc(start,end, setupDuration,getCallPriority(),group,getAudioQualityBorders(),getAudioDelayBorders(),getMinCallDuration());
        return call;
    }
    
    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        CallXmlData result =  (CallXmlData)super.buildCallCommands(group, hour, calls);
        List<Probe> probes = getProbes();
        Probe source = probes.get(group.getSourceProbe()-1);
        SavedTag root = result.getRoot();
        Long endOfHour = getStartOfHour(hour+1);
        Call call = calls[0];
        Long start = call.getStartTime();
        Long setupDuration = (Long)call.getParameter(CallParameterNames.SETUP_TIME);
        Long callDuration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
        Long hoDuration = (Long)call.getParameter(CallParameterNames.HO_TIME);
        Long ccDuration = (Long)call.getParameter(CallParameterNames.CC_TIME);
        
        if(hoDuration!=null){
            Long endCall = start+callDuration;
            Long endSetup = start+setupDuration;
            Long recTime = CallGeneratorUtils.getRamdomTime(endSetup, endCall);
            Long end = recTime+hoDuration;
            SavedTag eventsTag = getEventsTag();
            eventsTag.addInnerTag(getHoTag(source,false,recTime,end));
            root.addInnerTag(eventsTag);
        }
        if(ccDuration!=null){
            Long endCall = start+callDuration;
            Long recTime = CallGeneratorUtils.getRamdomTime(endCall, endOfHour);
            Long end = recTime+ccDuration;
            SavedTag eventsTag = getEventsTag();
            eventsTag.addInnerTag(getCcTag(source,false,recTime,end));
            root.addInnerTag(eventsTag);
        }
        
        return result;
    }

    private SavedTag getHoTag(Probe source, boolean needChangeLa, Long recTime, Long accTime){
        SavedTag result = new SavedTag(HO_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+source.getName()));
        result.addInnerTag(getPropertyTag(TAG_PR_HO_REC_TIME, getTimeString(recTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_HO_ACC_TIME, getTimeString(accTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_BEFORE, source.getLocalAria()));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_AFTER, source.getLocalAria()+(needChangeLa?1:0)));
        return result;
    }
    
    private SavedTag getCcTag(Probe source, boolean needChangeLa, Long recTime, Long accTime){
        SavedTag result = new SavedTag(CC_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+source.getName()));
        result.addInnerTag(getPropertyTag(TAG_PR_CC_REC_TIME, getTimeString(recTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_CC_ACC_TIME, getTimeString(accTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_BEFORE, source.getLocalAria()));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_AFTER, source.getLocalAria()+(needChangeLa?1:0)));
        return result;
    }
}
