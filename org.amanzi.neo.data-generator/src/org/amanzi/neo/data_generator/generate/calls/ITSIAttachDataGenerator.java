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
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;

/**
 * <p>
 * Generate data for ITSI attach calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ITSIAttachDataGenerator extends AmsDataGenerator {
    
    private static final String PAIR_DIRECTORY_POSTFIX = "ITSI-Attach";
    private static final Long[] DURATION_BORDERS = new Long[]{10L,MILLISECONDS*50L};

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public ITSIAttachDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected CallData buildCallCommands(CallGroup group, Integer hour, Call... calls) {
        Long networkIdentity = getNetworkIdentity();
        List<Probe> probes = getProbes();   
        Integer sourceNum = group.getSourceProbe();
        Probe sourceInfo = probes.get(sourceNum-1); 
        
        Call call = calls[0];
        Long startHour = getStartOfHour(hour);
        Long start = call.getStartTime();
        Long duration = (Long)call.getParameter(CallParameterNames.DURATION_TIME);
        Long time = getRamdomTime(startHour, start);
        ProbeData source = getNewProbeData(time, sourceNum);
        List<CommandRow> sourceCommands = source.getCommands();
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getAtCciRow(time));
        Integer la = sourceInfo.getLocalAria();
        CommandRow cci = CommandCreator.getCciRow(networkIdentity, la, sourceInfo.getFrequency());
        String mni = cci.getParams().get(0).toString();
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getAtCciRow(time, cci));
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getCsprtRow(time, true));
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getCsprtRow(time, false));
        time = getRamdomTime(time, start);
        sourceCommands.add(CommandCreator.getCregRow(time));        
        sourceCommands.add(CommandCreator.getCregRow(start, "+CME ERROR: 3"));
      
        time = getRamdomTime(0L, duration);
        sourceCommands.add(CommandCreator.getCregRow(start+time));
        Long end = start+duration;
        sourceCommands.add(CommandCreator.getCregRow(end, CommandCreator.getCregRow(mni,la)));
        
        CallData callData = new CallData(getKey(),source);
        callData.addCall(call);
        return callData;
    }

    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        int hours = getHours();
        int callsCount = getCalls();
        int callVariance = getCallVariance();
        for(int i = 0; i<hours; i++){
            int currCallCount = callsCount + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            for(int j = 0; j<currCallCount; j++){                
                CallData call = buildCallCommands(group, i, createCall(i));
                calls.add(call);
            }
        }
        return calls;
    }
    
    private Call createCall(Integer hour){
        Long duration = getRamdomTime(DURATION_BORDERS[0], DURATION_BORDERS[1]);
        Long startOfHour = getStartOfHour(hour);
        Long endOfHour = getStartOfHour(hour+1);
        Long start = getRamdomTime(startOfHour, endOfHour-duration/2);
        Call call = getEmptyCall(start);
        call.addParameter(CallParameterNames.DURATION_TIME, duration);
        return call;
    }

    @Override
    protected Integer getCallPriority() {
        return 0;
    }

    @Override
    protected String getDirectoryPostfix() {
        return PAIR_DIRECTORY_POSTFIX;
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
