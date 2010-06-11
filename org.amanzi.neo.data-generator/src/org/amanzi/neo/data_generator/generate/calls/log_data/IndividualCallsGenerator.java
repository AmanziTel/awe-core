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
import org.amanzi.neo.data_generator.utils.call.CallConstants;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;

/**
 * <p>
 * Generator for individual calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class IndividualCallsGenerator extends CallDataGenerator {
    
    private static final String PAIR_DIRECTORY_POSTFIX = "IndividualCall";

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public IndividualCallsGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected CallData buildCallCommands(CallGroup group,Integer hour, Call... calls){
        Call call = calls[0];
        Long networkIdentity = getNetworkIdentity();
        List<Probe> probes = getProbes();
        Long startHour = getStartOfHour(hour);
        Long endHour = getStartOfHour(hour+1);
        Long start = call.getStartTime();
        Long setupDuration = (Long)call.getParameter(CallParameterNames.SETUP_TIME);
        Long callDuration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
        
        Integer sourceNum = group.getSourceProbe();
        List<Integer> receiverNums = group.getReceiverProbes();
        
        Long time = CallGeneratorUtils.getRamdomTime(startHour, start);
        ProbeData source = getNewProbeData(time, sourceNum);
        Probe sourceInfo = probes.get(sourceNum-1);
        time = CallGeneratorUtils.getRamdomTime(time, start);
        Integer receiverNum = receiverNums.get(0);
        ProbeData receiver = getNewProbeData(start, receiverNum);
        Probe receiverInfo = probes.get(receiverNum-1);
        List<CommandRow> sourceCommands = source.getCommands();
        List<CommandRow> receiverCommands = receiver.getCommands();
        
        time = CallGeneratorUtils.getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getAtCciRow(time));
        CommandRow sourceCci = CommandCreator.getCciRow(networkIdentity,sourceInfo.getLocalAria(),sourceInfo.getFrequency());
        sourceCommands.add(CommandCreator.getAtCciRow(time,sourceCci));
        
        time = CallGeneratorUtils.getRamdomTime(time, start);
        receiverCommands.add(CommandCreator.getAtCciRow(time));
        CommandRow receiverCci = CommandCreator.getCciRow(networkIdentity,receiverInfo.getLocalAria(),receiverInfo.getFrequency());
        receiverCommands.add(CommandCreator.getAtCciRow(time,receiverCci));
        
        
        CommandRow ctsdcRow = CommandCreator.getCtsdcRow(start,0,0,0,0,0,0,0,1,0,0);
        sourceCommands.add(ctsdcRow);
        time = CallGeneratorUtils.getRamdomTime(0L, setupDuration);
        sourceCommands.add(CommandCreator.getCtsdcRow(start+time,ctsdcRow));
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        CommandRow atdRow = CommandCreator.getAtdRow(start+time, receiver.getNumber());
        sourceCommands.add(atdRow);
        
        
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        CommandRow ctocp1 = CommandCreator.getCtocpRow(start+time);
        
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        receiverCommands.add(CommandCreator.getCticnRow(start+time,"0"+source.getNumber()));
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        CommandRow ataRow = CommandCreator.getAtaRow(start+time);
        receiverCommands.add(ataRow);
        
        
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        CommandRow ctocp2 = CommandCreator.getCtocpRow(start+time);
        CommandRow ctcc = CommandCreator.getCtccRow(null,1,0,0,0,0,0,1);
        sourceCommands.add(CommandCreator.getAtdRow(atdRow,ctocp1,ctocp2,ctcc));
        
        time = CallGeneratorUtils.getRamdomTime(time, setupDuration);
        Long end = start+setupDuration;
        ctcc = CommandCreator.getCtccRow(end,1,0,0,0,0,0,1);
        receiverCommands.add(CommandCreator.getAtaRow(ataRow, ctcc));
        
        
        long endAll = start+callDuration;
        sourceCommands.add(CommandCreator.getAthRow(endAll));
        CommandRow ctcrRow = CommandCreator.getCtcrRow(endAll,1,1);
        sourceCommands.add(CommandCreator.getAthRow(endAll,ctcrRow));            
        
        ctcrRow = CommandCreator.getCtcrRow(null,1,1);
        receiverCommands.add(CommandCreator.getUnsoCtcrRow(endAll,ctcrRow));
        Long rest = endHour-endAll;
        if(rest<0){
            rest = CallGeneratorUtils.HOUR;
        }
        time = CallGeneratorUtils.getRamdomTime(0L, rest);
        Long time1 = time;
        List<Float> audioQuals = (List<Float>)call.getParameter(CallParameterNames.AUDIO_QUALITY+group.getSourceName());
        for(Float quality : audioQuals){
            sourceCommands.add(CommandCreator.getPESQRow(end+time1,quality));
            time1 = CallGeneratorUtils.getRamdomTime(time1, rest);
        }
        time1 = time;
        audioQuals = (List<Float>)call.getParameter(CallParameterNames.AUDIO_QUALITY+group.getReceiverNames().get(0));
        for(Float quality : audioQuals){   
            receiverCommands.add(CommandCreator.getPESQRow(end+time1,quality));
            time1 = CallGeneratorUtils.getRamdomTime(time1, rest);
        }
        
        CallData callData = new CallData(getKey(),source, receiver);
        callData.addCall(call);
        //TODO Priority
        return callData;
    }

    @Override
    protected String getTypeKey() {
        return PAIR_DIRECTORY_POSTFIX;
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

    @Override
    protected float[] getCallDurationBorders() {
        return CallConstants.IND_CALL_DURATION_BORDERS;
    }

    @Override
    protected float[] getAudioQualityBorders() {
        return CallConstants.IND_AUDIO_QUAL_BORDERS;
    }

    @Override
    protected Long getMinCallDuration() {
        return (long)(CallConstants.IND_CALL_DURATION_TIME*CallGeneratorUtils.MILLISECONDS);
    }

    @Override
    protected Integer getCallPriority() {
        return 1;//TODO
    }

}
