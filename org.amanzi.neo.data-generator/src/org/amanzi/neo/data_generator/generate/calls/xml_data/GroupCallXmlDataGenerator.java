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
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.data.calls.CallXmlData;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.utils.call.CallConstants;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Generate xml file for group calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class GroupCallXmlDataGenerator extends CallXmlDataGenerator{
    
    private static final String GROUP_ATTACH_TAG_NAME = "groupAttach";
    private static final String TAG_PR_ATTACH_TIME = "groupAttachTime";
    
    private static final String ATTACHMENT_TAG_NAME = "attachment";
    private static final String TAG_PR_GROUP_TYPE = "groupType";
    private static final String TAG_PR_GSSI = "gssi";

    private int maxGroupSize;

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aMaxGroupSize
     * @param aProbes
     */
    public GroupCallXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes, Integer aMaxGroupSize) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        maxGroupSize = aMaxGroupSize;
    }
    
    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        Call call = calls[0];
        List<Probe> probes = getProbes();
        Probe sourceProbe = probes.get(group.getSourceProbe()-1);
        List<Integer> receiverProbeNums = group.getReceiverProbes();
        int recCount = receiverProbeNums.size();
        Probe[] receiverProbes = new Probe[recCount];
        Probe[] allProbes = new Probe[recCount+1];
        allProbes[0]= sourceProbe;
        for(int i=0; i<recCount; i++){
            receiverProbes[i] = probes.get(receiverProbeNums.get(i)-1);
            allProbes[i+1]= receiverProbes[i];
        }        
        Long startHour = getStartOfHour(hour);
        Long endHour = getStartOfHour(hour+1);
        Long start = call.getStartTime();
        Long setupDuration = (Long)call.getParameter(CallParameterNames.SETUP_TIME);
        Long callDuration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);        
        
        Long sConTime = start+setupDuration;
        Long end = start+callDuration;
        while(end>endHour){
            endHour+=CallGeneratorUtils.HOUR;
        }
        
        Long setupTime = CallGeneratorUtils.getRamdomTime(start, sConTime);
        Long discTime = CallGeneratorUtils.getRamdomTime(sConTime, end);
        Long sRelTime = CallGeneratorUtils.getRamdomTime(discTime, end);
        
        CallXmlData result = new CallXmlData(getKey(), sourceProbe, receiverProbes);
        SavedTag rootTag = getRootTag();
        SavedTag commonTag = generateCommonTestDataTags(allProbes);
        rootTag.addInnerTag(commonTag);
        SavedTag eventsTag;
        String groupNumber = group.getGroupNumber();
        Long time = startHour;
        for(Probe probe : allProbes){
            eventsTag = getEventsTag();
            time = CallGeneratorUtils.getRamdomTime(time, start);
            eventsTag.addInnerTag(getGroupAttachTag(probe, groupNumber, time));
            rootTag.addInnerTag(eventsTag);
        }
        eventsTag = getEventsTag();
        eventsTag.addInnerTag(getTocTag(sourceProbe,groupNumber,call,1,1,1,start,setupTime,sConTime,discTime,sRelTime,end, endHour));
        rootTag.addInnerTag(eventsTag);
        eventsTag = getEventsTag();
        boolean setEnd = false;
        for (Probe receiverProbe : receiverProbes) {
            Long indicTime = CallGeneratorUtils.getRamdomTime(sConTime, sRelTime);
            Long ansTime = CallGeneratorUtils.getRamdomTime(indicTime, sRelTime);
            Long rConTime = CallGeneratorUtils.getRamdomTime(ansTime, sRelTime);
            Long relTime = setEnd?CallGeneratorUtils.getRamdomTime(sRelTime, end):end;
            eventsTag.addInnerTag(getTtcTag(sourceProbe, receiverProbe, true, call,1,1,1, indicTime, ansTime, rConTime, relTime, endHour));
            setEnd = true;
        }
        rootTag.addInnerTag(eventsTag);
        SavedTag gpsDataTag = getGpsDataTag(start,end,allProbes);
        rootTag.addInnerTag(gpsDataTag);
        result.setRoot(rootTag);
        result.addCall(call);
        return result;
    }
    
    private SavedTag getGroupAttachTag(Probe probe, String groupId, Long time){
        SavedTag result = new SavedTag(GROUP_ATTACH_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+probe.getName())); 
        result.addInnerTag(getPropertyTag(TAG_PR_ATTACH_TIME, getTimeString(time)));
        SavedTag attach = new SavedTag(ATTACHMENT_TAG_NAME, false);
        attach.addInnerTag(getPropertyTag(TAG_PR_GROUP_TYPE, 1));
        attach.addInnerTag(getPropertyTag(TAG_PR_GSSI, groupId));
        result.addInnerTag(attach);
        return result;
    }

    @Override
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        int groupSize = 1;
        while (groupSize<maxGroupSize) {
            groupSize++;            
            List<List<Integer>> groups = CallGeneratorUtils.buildAllGroups(groupSize, getProbesCount());
            for(List<Integer> group : groups){
                boolean canBeGroup = getRandomGenerator().getBooleanValue();
                if(canBeGroup){
                    int source = group.get(0);
                    CallGroup call = getCallGroup(source, group.subList(1, groupSize).toArray(new Integer[0]));
                    result.add(call);
                }
            }
            
        }
        return result;
    }

    @Override
    protected int[] getAudioDelayBorders() {
        return CallConstants.GR_AUDIO_DELAY_BORDERS;
    }

    @Override
    protected float[] getAudioQualityBorders() {
        return CallConstants.GR_AUDIO_QUAL_BORDERS;
    }

    @Override
    protected float[] getCallDurationBorders() {
        return CallConstants.GR_CALL_DURATION_BORDERS;
    }

    @Override
    protected Long getMinCallDuration() {
        return (long)(CallConstants.GR_CALL_DURATION_TIME*CallGeneratorUtils.MILLISECONDS);
    }
    
    @Override
    protected Integer getCallPriority() {
        return 0;
    }

    @Override
    protected String getTypeKey() {
        return "GC_1";
    }

}
