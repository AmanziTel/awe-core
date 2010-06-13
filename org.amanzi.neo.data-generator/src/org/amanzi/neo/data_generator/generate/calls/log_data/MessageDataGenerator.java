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
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;

/**
 * <p>
 * Common class for all messages data.  
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class MessageDataGenerator extends AmsDataGenerator{

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public MessageDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected CallData buildCallCommands(CallGroup group,Integer hour, Call... calls) {
        Long networkIdentity = getNetworkIdentity();
        List<Probe> probes = getProbes();   
        Integer sourceNum = group.getSourceProbe();
        Integer recNum = group.getReceiverProbes().get(0);
        Probe sourceInfo = probes.get(sourceNum-1);        
        Probe recInfo = probes.get(recNum-1);
        ProbeData source = null;
        ProbeData receiver = null;
        List<CommandRow> sourceCommands = null;
        List<CommandRow> receiverCommands = null;
        CallData callData = null;
        
        Long startHour = getStartOfHour(hour);
        Long time = startHour;
        Integer aiService = getAiService();
        String sourceKey = networkIdentity+sourceInfo.getPhoneNumber();
        String recKey = networkIdentity+recInfo.getPhoneNumber();
        for(Call call : calls){
            Long start = call.getStartTime();
            if(source==null||receiver==null){
                time = CallGeneratorUtils.getRamdomTime(time, start);
                source = getNewProbeData(time, sourceNum);
                sourceCommands = source.getCommands();
                time = CallGeneratorUtils.getRamdomTime(time, start);
                receiver = getNewProbeData(time, recNum);
                receiverCommands = receiver.getCommands();
                callData = new CallData(getKey(),source, receiver);
            }
            Long duration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
            Long acknowledge = (Long)call.getParameter(CallParameterNames.ACKNOWLEDGE_TIME);
            time = CallGeneratorUtils.getRamdomTime(time, start);
            sourceCommands.add(CommandCreator.getAtCciRow(time));
            CommandRow sourceCci = CommandCreator.getCciRow(networkIdentity,sourceInfo.getLocalAria(),sourceInfo.getFrequency());
            sourceCommands.add(CommandCreator.getAtCciRow(time,sourceCci));
            
            time = CallGeneratorUtils.getRamdomTime(time, start);
            receiverCommands.add(CommandCreator.getAtCciRow(time));
            CommandRow receiverCci = CommandCreator.getCciRow(networkIdentity,recInfo.getLocalAria(),recInfo.getFrequency());
            receiverCommands.add(CommandCreator.getAtCciRow(time,receiverCci));
            
            time = CallGeneratorUtils.getRamdomTime(time, start);
            
            CommandRow ctsdsRow = CommandCreator.getCtsdsRow(time,aiService,0,0,0,0);
            sourceCommands.add(ctsdsRow);
            time = CallGeneratorUtils.getRamdomTime(time, start);
            sourceCommands.add(CommandCreator.getCtsdsRow(time,ctsdsRow));
            
            time = CallGeneratorUtils.getRamdomTime(time, start);
            String message = call.getParameter(CallParameterNames.MESSAGE).toString();
            CommandRow atCmgs = CommandCreator.getCmgsRow(time, recInfo.getPhoneNumber(), message);
            sourceCommands.add(atCmgs);
            
            
            CommandRow cmgs = CommandCreator.getCmgsRow(233,2,30);
            sourceCommands.add(CommandCreator.getCmgsRow(start, atCmgs, cmgs));
            
            time = start+acknowledge;
            sourceCommands.add(CommandCreator.getUnsoCmgsRow(time,cmgs));            
            
            time = start+duration;
            receiverCommands.add(CommandCreator.getCtsdsrRow(time, aiService, sourceKey, recKey, message));
            callData.addCall(call);
        }
        return callData;
    }
    
    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        int hours = getHours();
        int callsCount = getCalls();
        int callVariance = getCallVariance();
        int messagesCount = getMessagesCount();
        Integer callPriority = getCallPriority();
        Long[] durationBorders = getDurationBorders();
        Long[] acknowledgeBorders = getAcknowledgeBorders();
        String[] messages = getAllMessages();
        for(int i = 0; i<hours; i++){
            int currCallCount = callsCount + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            Long startOfHour = getStartOfHour(i);
            for(int j = 0; j<currCallCount; j++){
                CallData call = buildCallCommands(group, i, CallGeneratorUtils.createMessages(startOfHour,messagesCount, callPriority, durationBorders,acknowledgeBorders,messages));
                calls.add(call);
            }
        }
        return calls;
    }

    @Override
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        Integer probesCount = getProbesCount();
        int sourceCount = probesCount/2+(probesCount%2==0?0:1);
        RandomValueGenerator generator = getRandomGenerator();
        while (result.isEmpty()) {
            for (int i = 1; i <= sourceCount; i++) {
                for (int j = sourceCount + 1; j <= probesCount; j++) {
                    boolean canBePair = generator.getBooleanValue();
                    if (canBePair) {
                        result.add(getCallGroup(i, j));
                        result.add(getCallGroup(j, i));
                    }
                }
            }
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
    
    /**
     * Get count of messages in one call.
     *
     * @return int
     */
    protected abstract int getMessagesCount();
    
    /**
     * Get messages for one call.
     *
     * @return String[]
     */
    protected abstract String[] getAllMessages();
    
    /**
     * Get ai service.
     *
     * @return Integer
     */
    protected abstract Integer getAiService();

    @Override
    protected Integer getCallPriority() {
        return 1; //TODO correct
    }

}
