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

package org.amanzi.neo.data_generator.generate.calls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;

/**
 * <p>
 * Generator for individual calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class IndividualCallsGenerator extends CallDataGenerator {
    
    private static final float[] CALL_DURATION_BORDERS = new float[]{0.01f,1.25f,2.5f,3.75f,5,7.5f,10,12.5f,45,1000};
    
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
    
    @Override
    protected CallData buildCall(CallGroup group, Integer hour, Long duration){
        Long startTime = getStartTime();
        Long networkIdentity = getNetworkIdentity();
        List<ProbeInfo> probes = getProbes();
        Long startHour = startTime+HOUR*hour;
        Long endHour = startTime+HOUR*(hour+1);
        Long start = getRamdomTime(startHour, endHour);
        
        Integer sourceNum = group.getSourceProbe();
        List<Integer> receiverNums = group.getReceiverProbes();
        
        Long time = getRamdomTime(startHour, start);
        ProbeData source = getNewProbeData(time, sourceNum);
        ProbeInfo sourceInfo = probes.get(sourceNum-1);
        time = getRamdomTime(time, start);
        Integer receiverNum = receiverNums.get(0);
        ProbeData receiver = getNewProbeData(start, receiverNum);
        ProbeInfo receiverInfo = probes.get(receiverNum-1);
        List<CommandRow> sourceCommands = source.getCommands();
        List<CommandRow> receiverCommands = receiver.getCommands();
        
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getAtCciRow(time));
        CommandRow sourceCci = CommandCreator.getCciRow(networkIdentity,sourceInfo.getLocalAria(),sourceInfo.getFrequency());
        sourceCommands.add(CommandCreator.getAtCciRow(time,sourceCci));
        
        time = getRamdomTime(time, start);
        receiverCommands.add(CommandCreator.getAtCciRow(time));
        CommandRow receiverCci = CommandCreator.getCciRow(networkIdentity,receiverInfo.getLocalAria(),receiverInfo.getFrequency());
        receiverCommands.add(CommandCreator.getAtCciRow(time,receiverCci));
        
        time = getRamdomTime(time, start);
        Long startAll = time;
        CommandRow ctsdcRow = CommandCreator.getCtsdcRow(time,0,0,0,0,0,0,0,1,0,0);
        sourceCommands.add(ctsdcRow);
        sourceCommands.add(CommandCreator.getCtsdcRow(start,ctsdcRow));
        time = getRamdomTime(0L, duration);
        CommandRow atdRow = CommandCreator.getAtdRow(start+time, receiver.getNumber());
        sourceCommands.add(atdRow);
        
        
        time = getRamdomTime(time, duration);
        CommandRow ctocp1 = CommandCreator.getCtocpRow(start+time);
        
        time = getRamdomTime(time, duration);
        receiverCommands.add(CommandCreator.getCticnRow(start+time,"0"+source.getNumber()));
        time = getRamdomTime(time, duration);
        CommandRow ataRow = CommandCreator.getAtaRow(start+time);
        receiverCommands.add(ataRow);
        
        
        time = getRamdomTime(time, duration);
        CommandRow ctocp2 = CommandCreator.getCtocpRow(start+time);
        CommandRow ctcc = CommandCreator.getCtccRow(null,1,0,0,0,0,0,1);
        sourceCommands.add(CommandCreator.getAtdRow(atdRow,ctocp1,ctocp2,ctcc));
        
        time = getRamdomTime(time, duration);
        Long end = start+duration;
        ctcc = CommandCreator.getCtccRow(end,1,0,0,0,0,0,1);
        receiverCommands.add(CommandCreator.getAtaRow(ataRow, ctcc));
        
        Long rest = startTime+HOUR*(hour+1)-end;
        if(rest<0){
            rest = HOUR;
        }
        time = getRamdomTime(0L, rest);
        long andAll = end+time;
        sourceCommands.add(CommandCreator.getAthRow(andAll));
        time = getRamdomTime(time, rest);
        CommandRow ctcrRow = CommandCreator.getCtcrRow(andAll,1,1);
        time = getRamdomTime(time, rest);
        sourceCommands.add(CommandCreator.getAthRow(andAll,ctcrRow));            
        
        time = getRamdomTime(time, rest);
        ctcrRow = CommandCreator.getCtcrRow(null,1,1);
        receiverCommands.add(CommandCreator.getUnsoCtcrRow(andAll,ctcrRow));
        Long time1 = time;
        Long time2 = time;
        for(int i=0;i<6;i++){            
            time1 = getRamdomTime(time1, rest);
            sourceCommands.add(CommandCreator.getPESQRow(time1));
            time2 = getRamdomTime(time2, rest);
            receiverCommands.add(CommandCreator.getPESQRow(time2));
        }
        
        CallData callData = new CallData(getKey(),source, receiver);
        callData.setStartTime(new Date(startTime));
        callData.addTime(duration);
        callData.addTime(andAll-startAll);
        //TODO Priority
        return callData;
    }

    @Override
    protected String getDirectoryPostfix() {
        return PAIR_DIRECTORY_POSTFIX;
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

    @Override
    protected float[] getCallDurationBorders() {
        return CALL_DURATION_BORDERS;
    }

}
