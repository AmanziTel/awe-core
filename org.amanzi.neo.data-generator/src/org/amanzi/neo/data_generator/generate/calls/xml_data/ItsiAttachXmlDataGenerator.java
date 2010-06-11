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
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Generate xml files for ITSI Attach data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ItsiAttachXmlDataGenerator extends AmsXmlDataGenerator{
    
    private static final String ATTACH_TAG_NAME = "itsiAttach";
    private static final String TAG_PR_REQ_TIME = "itsiAtt_Req";
    private static final String TAG_PR_ACC_TIME = "itsiAtt_Accept";
    private static final String TAG_PR_LA_BEFORE = "locationAreaBefore";
    private static final String TAG_PR_LA_AFTER = "locationAreaAfter";

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public ItsiAttachXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        Call call = calls[0];
        List<Probe> probes = getProbes();
        Probe sourceProbe = probes.get(group.getSourceProbe()-1);
        Long start = call.getStartTime();
        Long callDuration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
        
        Long end = start+callDuration;
        
        CallXmlData result = new CallXmlData(getKey(), sourceProbe);
        SavedTag rootTag = getRootTag();
        SavedTag commonTag = generateCommonTestDataTags(sourceProbe);
        rootTag.addInnerTag(commonTag);
        SavedTag eventsTag = getEventsTag();
        eventsTag.addInnerTag(getAttachTag(sourceProbe,start,end));        
        rootTag.addInnerTag(eventsTag);
        SavedTag gpsDataTag = getGpsDataTag();
        //gps
        rootTag.addInnerTag(gpsDataTag);
        result.setRoot(rootTag);
        result.addCall(call);
        return result;
    }
    
    private SavedTag getAttachTag(Probe probe, Long reqTime, Long accTime){
        SavedTag result = new SavedTag(ATTACH_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+probe.getName()));
        result.addInnerTag(getPropertyTag(TAG_PR_REQ_TIME, getTimeString(reqTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_ACC_TIME, getTimeString(accTime)));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_BEFORE, probe.getLocalAria()));
        result.addInnerTag(getPropertyTag(TAG_PR_LA_AFTER, probe.getLocalAria()));
        return result;
    }

    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        int hours = getHours();
        int priority = getCallPriority();
        int callsCount = getCalls();
        int callVariance = getCallVariance();
        for(int i = 0; i<hours; i++){
            Long startBorder = getStartOfHour(i);
            Long endBorder = getStartOfHour(i+1);
            int currCallCount = callsCount + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            for(int j = 0; j<currCallCount; j++){                 
                CallData call = buildCallCommands(group, i, CallGeneratorUtils.createITSICall(startBorder, endBorder, priority));
                calls.add(call);
            }
        }
        return calls;
    }

    @Override
    protected Integer getCallPriority() {
        return 0;
    }

    @Override
    protected String getTypeKey() {
        return "ITS";
    }

    @Override
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        Integer probesCount = getProbesCount();
        for(int i = 1; i<=probesCount; i++){
            result.add(getCallGroup(i));
        }
        return result;
    }

}
