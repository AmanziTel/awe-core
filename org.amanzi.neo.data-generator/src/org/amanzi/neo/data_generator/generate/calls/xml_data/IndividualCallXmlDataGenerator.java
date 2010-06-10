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
 * Generate xml file for Individual (single) calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class IndividualCallXmlDataGenerator extends CallXmlDataGenerator{

    /**
     * Constructor.
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public IndividualCallXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        Call call = calls[0];
        List<Probe> probes = getProbes();
        Probe sourceProbe = probes.get(group.getSourceProbe()-1);
        Probe receiverProbe = probes.get(group.getReceiverProbes().get(0)-1);
        Long endHour = getStartOfHour(hour+1);
        Long start = call.getStartTime();
        Long setupDuration = (Long)call.getParameter(CallParameterNames.SETUP_TIME);
        Long callDuration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
        
        
        Long rConTime = start+setupDuration;
        Long end = start+callDuration;
        
        Long setupTime = getRamdomTime(start, rConTime);
        Long indicTime = getRamdomTime(setupTime, rConTime);
        Long ansTime = getRamdomTime(indicTime, rConTime);
        Long sConTime = getRamdomTime(ansTime, rConTime);
        
        Long discTime = getRamdomTime(start, end);
        Long relTime = getRamdomTime(discTime, end);
        
        CallXmlData result = new CallXmlData(getKey(), sourceProbe, receiverProbe);
        SavedTag rootTag = getRootTag();
        SavedTag commonTag = generateCommonTestDataTags(sourceProbe,receiverProbe);
        rootTag.addInnerTag(commonTag);
        SavedTag eventsTag = getEventsTag();
        eventsTag.addInnerTag(getTocTag(sourceProbe,receiverProbe.getPhoneNumber(),call,0,0,1,start,setupTime,sConTime,discTime,relTime,end, endHour));
        rootTag.addInnerTag(eventsTag);
        eventsTag = getEventsTag();
        eventsTag.addInnerTag(getTtcTag(sourceProbe,receiverProbe,false,call,0,0,1,indicTime,ansTime,rConTime,end, endHour));
        rootTag.addInnerTag(eventsTag);
        SavedTag gpsDataTag = getGpsDataTag();
        //gps
        rootTag.addInnerTag(gpsDataTag);
        result.setRoot(rootTag);
        result.addCall(call);
        return result;
    }

    @Override
    protected int[] getAudioDelayBorders() {
        return CallConstants.IND_AUDIO_DELAY_BORDERS;
    }

    @Override
    protected float[] getAudioQualityBorders() {
        return CallConstants.IND_AUDIO_QUAL_BORDERS;
    }

    @Override
    protected float[] getCallDurationBorders() {
        return CallConstants.IND_CALL_DURATION_BORDERS;
    }

    @Override
    protected Long getMinCallDuration() {
        return (long)(CallConstants.IND_CALL_DURATION_TIME*CallGeneratorUtils.MILLISECONDS);
    }

    @Override
    protected Integer getCallPriority() {
        return 0;
    }

    @Override
    protected String getTypeKey() {
        return "SC";
    }

    @Override
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        List<List<Integer>> pairs = CallGeneratorUtils.initCallPairs(getProbesCount());
        for(List<Integer> pair : pairs){
            result.add(getCallGroup(pair.get(0), pair.get(1)));
        }
        return result;
    }

}
