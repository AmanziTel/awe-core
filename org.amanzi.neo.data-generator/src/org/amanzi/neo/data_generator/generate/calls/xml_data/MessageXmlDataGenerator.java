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
 * Common class for generate messages data (xml)
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class MessageXmlDataGenerator extends AmsXmlDataGenerator{
    
    private static final String SEND_TAG_NAME = "sendMsg";
    private static final String TAG_PR_MSG_TYPE = "msgType"; 
    private static final String TAG_PR_DATA_LENGTH = "dataLength"; 
    private static final String TAG_PR_DATA_TXT = "dataTxt"; 
    private static final String TAG_PR_SEND_TIME = "sendTime"; 
    
    private static final String RECEIVE_TAG_NAME = "receiveMsg";
    private static final String TAG_PR_REC_TIME = "receiveTime";
    
    private static final String REPORT_TAG_NAME = "sendReport";
    private static final String TAG_PR_REPORT_TIME = "reportTime";
    private static final String TAG_PR_REPORT_STATUS = "status";

    /**
     * Constructor.
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public MessageXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        List<Probe> probes = getProbes();
        Probe sourceProbe = probes.get(group.getSourceProbe()-1);
        Probe receiverProbe = probes.get(group.getReceiverProbes().get(0)-1);
        Integer msgType = getAiService();
        
        CallXmlData result = new CallXmlData(getKey(), sourceProbe, receiverProbe);
        SavedTag rootTag = getRootTag();
        SavedTag commonTag = generateCommonTestDataTags(sourceProbe,receiverProbe);
        rootTag.addInnerTag(commonTag);
        
        for(Call call : calls){
            Long start = call.getStartTime();
            Long duration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
            Long acknowledge = (Long)call.getParameter(CallParameterNames.ACKNOWLEDGE_TIME);
            
            Long report2 = start+acknowledge;
            Long send = start+duration;
            Long report1 = CallGeneratorUtils.getRamdomTime(start, report2);            
            
            SavedTag eventsTag = getEventsTag();
            eventsTag.addInnerTag(getSendTag(sourceProbe,receiverProbe.getPhoneNumber(),msgType,call,start,report1,report2));
            rootTag.addInnerTag(eventsTag);
            eventsTag = getEventsTag();
            eventsTag.addInnerTag(getReceiveTag(receiverProbe,sourceProbe.getPhoneNumber(),msgType,call,send));
            rootTag.addInnerTag(eventsTag);
            result.addCall(call);
        }
        
        SavedTag gpsDataTag = getGpsDataTag(getStartOfHour(hour),getStartOfHour(hour+1),sourceProbe,receiverProbe);
        rootTag.addInnerTag(gpsDataTag);
        result.setRoot(rootTag);        
        return result;
    }
    
    private SavedTag getSendTag(Probe source, String number, int msgType, Call call, Long sendTime, Long report1, Long report2){
        SavedTag result = new SavedTag(SEND_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+source.getName())); 
        result.addInnerTag(getPropertyTag(TAG_PR_CALLED_NUMBER, number));  
        result.addInnerTag(getPropertyTag(TAG_PR_MSG_TYPE, msgType));  
        String message = getCallMessage(call);
        result.addInnerTag(getPropertyTag(TAG_PR_DATA_LENGTH, message.length()*4)); 
        result.addInnerTag(getPropertyTag(TAG_PR_DATA_TXT, message)); 
        result.addInnerTag(getPropertyTag(TAG_PR_SEND_TIME, getTimeString(sendTime)));  
        result.addInnerTag(getSendReportTag(report1, 0));
        result.addInnerTag(getSendReportTag(report2, 9));
        return result;
    }

    private String getCallMessage(Call call) {
        String message = CallGeneratorUtils.convertAsciiToHex(call.getParameter(CallParameterNames.MESSAGE).toString());
        return message;
    }
    
    private SavedTag getReceiveTag(Probe receiver, String number,int msgType, Call call, Long time){
        SavedTag result = new SavedTag(RECEIVE_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+receiver.getName())); 
        result.addInnerTag(getPropertyTag(TAG_PR_CALLING_NUMBER, number));  
        result.addInnerTag(getPropertyTag(TAG_PR_MSG_TYPE, msgType));  
        String message = getCallMessage(call);
        result.addInnerTag(getPropertyTag(TAG_PR_DATA_LENGTH, message.length()*4)); 
        result.addInnerTag(getPropertyTag(TAG_PR_DATA_TXT, message)); 
        result.addInnerTag(getPropertyTag(TAG_PR_REC_TIME, getTimeString(time)));  
        return result;
    }
    
    private SavedTag getSendReportTag(Long time, int status){
        SavedTag result = new SavedTag(REPORT_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_REPORT_TIME, getTimeString(time))); 
        result.addInnerTag(getPropertyTag(TAG_PR_REPORT_STATUS, status));  
        return result;
    }

    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        int hours = getHours();
        int callsCount = getCalls();
        int callVariance = getCallVariance();
        Integer callPriority = getCallPriority();
        Long[] durationBorders = getDurationBorders();
        Long[] acknowledgeBorders = getAcknowledgeBorders();
        String message = getMessage();
        for(int i = 0; i<hours; i++){
            int currCallCount = callsCount + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            Long start = getStartOfHour(i);
            for(int j = 0; j<currCallCount; j++){
                CallData call = buildCallCommands(group, i, CallGeneratorUtils.createMessages(start,1, callPriority, durationBorders,acknowledgeBorders,message));
                start = call.getStartTime()+1;
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
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        List<List<Integer>> pairs = CallGeneratorUtils.initCallPairs(getProbesCount());
        for(List<Integer> pair : pairs){
            result.add(getCallGroup(pair.get(0), pair.get(1)));
        }
        return result;
    }
    
    /**
     * Get borders for call duration.
     *
     * @return Long[] (start, end)
     */
    protected abstract Long[] getDurationBorders();
    
    /**
     * Get borders for call duration.
     *
     * @return Long[] (start, end)
     */
    protected abstract Long[] getAcknowledgeBorders();
    
    protected abstract String getMessage();
    
    protected abstract Integer getAiService();

}
