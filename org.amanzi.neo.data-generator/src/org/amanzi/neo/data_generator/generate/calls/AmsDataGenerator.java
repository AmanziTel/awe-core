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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;
import org.amanzi.neo.data_generator.utils.call.CallFileBuilder;

/**
 * <p>
 * Generate AMS data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class AmsDataGenerator extends CallStatisticsDataGenerator{
    
    private static final long MAX_KEY_VALUE = (long)1E10;
    private static final long MAX_NETWORK_ID = 125000;
    
    private Long networkIdentity;
    private List<Probe> probes;
    
    /**
     * Constructor.
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aHourDrift Integer (drift of start time)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     */
    public AmsDataGenerator(String aDirectory,Integer aHours, Integer aHourDrift,
                        Integer aCallsPerHour, Integer aCallPerHourVariance,
                        Integer aProbes){
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    /**
     * @return Returns the probes.
     */
    public List<Probe> getProbes() {
        return probes;
    }
    
    /**
     * @return Returns the networkIdentity.
     */
    public Long getNetworkIdentity() {
        return networkIdentity;
    }
    
    @Override
    public GeneratedCallsData generate(){
        List<CallGroup> data;
        if (getHours()>0&&getProbesCount()>0&&getCalls()>0) {
            networkIdentity = getRandomGenerator().getLongValue(0L, MAX_NETWORK_ID);
            initProbes();
            data = buildData();
        }
        else{
            data = new ArrayList<CallGroup>(0);
        }
        saveData(data);
        return new GeneratedCallsData(data);
    }
    
    /**
     * Save data.
     *
     * @param data
     * @throws IOException
     */
    private void saveData(List<CallGroup> data) {
        try {
            CallFileBuilder fileBuilder = new CallFileBuilder(getDirectory(),getDirectoryPostfix());
            fileBuilder.saveData(data);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
    
    protected abstract String getDirectoryPostfix();
    
    /**
     * Initialize probes.
     */
    private void initProbes(){
       Integer probesCount = getProbesCount();
       probes = new ArrayList<Probe>(probesCount); 
       int defLength = probesCount.toString().length();
       for(int i=1; i<=probesCount; i++){
           String name = getProbeName(i, defLength);
           String number = PROBE_NUMBER_PREFIX+name;           
           Integer la = getLa();
           Double fr = getFrequency();
           Probe probe = new Probe(name, number, la, fr);
           probes.add(probe);
       }
    }

    /**
     * Build data.
     *
     * @return List of {@link CallGroup}.
     */
    private List<CallGroup> buildData(){
        List<CallGroup> data = initCallGroups();
        for(CallGroup group : data){            
            List<CallData> calls = buildCalls(group);
            group.setData(calls);
        }
        return data;
    }

    /**
     * Generate call for one pair.
     *
     * @param source Integer
     * @param receiver Integer
     * @return List of {@link CallData}
     */
    protected abstract List<CallData> buildCalls(CallGroup group);
    
    /**
     * Build call data.
     *
     * @param group CallGroup
     * @param calls Call... (calls in file)
     * @return {@link CallData}
     */
    protected abstract CallData buildCallCommands(CallGroup group,Integer hour, Call... calls);
    
    /**
     * Create new empty call
     *
     * @param start
     * @return Call.
     */
    protected Call getEmptyCall(Long start){
        return new Call(start, getCallPriority());
    }
    
    /**
     * Get call priority value.
     *
     * @return Integer
     */
    protected abstract Integer getCallPriority();

    /**
     * Returns random time in interval.
     *
     * @param start Long
     * @param end Long
     * @return Long
     */
    protected Long getRamdomTime(Long start, Long end) {
        if(start.equals(end)){
            return start;
        }
        Long time = getRandomGenerator().getLongValue(start, end);
        while((start-end>1)&&(time.equals(start)||time.equals(end))){
            time = getRandomGenerator().getLongValue(start, end);
        }
        return time;
    }
    
    /**
     * Initialize new probe data.
     *
     * @param time Long (time of first command)
     * @param number Integer (probe number)
     * @return {@link ProbeData}
     */
    protected ProbeData getNewProbeData(Long time,Integer number){
        Probe probeInfo = probes.get(number-1);
        String name = probeInfo.getName();
        String callNumber = probeInfo.getPhoneNumber();
        ProbeData data = new ProbeData(name,callNumber,getKey());
        data.getCommands().add(CommandCreator.getProbeNumberRow(time, name, callNumber));
        return data;
    }
    
    /**
     * Initialize different probe pairs.
     *
     * @return List of {@link CallGroup}.
     */
    protected abstract List<CallGroup> initCallGroups();
    
    /**
     * Initialize new call pair.
     *
     * @param source Integer
     * @param receiver Integer
     * @return {@link CallGroup}
     */
    protected CallGroup getCallGroup(Integer source, Integer... receivers){
        String groupName = getRandomGenerator().getLongValue(0L, 100000L).toString();
        Probe sourceProbeInfo = probes.get(source-1);
        String sourceName = sourceProbeInfo.getName();
        sourceProbeInfo.addSourceGroup(groupName);
        int resCount = receivers.length;
        List<Integer> receiverList = Arrays.asList(receivers);
        List<String> receiverNames = new ArrayList<String>(resCount);
        for(Integer receiver : receiverList){
            Probe resProbeInfo = probes.get(receiver-1);
            resProbeInfo.addResGroup(groupName);
            String receiverName = resProbeInfo.getName();
            receiverNames.add(receiverName);
        }             
        return new CallGroup(groupName, source, sourceName, receiverList,receiverNames);
    }
    
    /**
     * Build name for probe.
     *
     * @param number Integer (probe number)
     * @param defLength Integer (default name length)
     * @return String.
     */
    private String getProbeName(Integer number, Integer defLength){
        String postfix = number.toString();
        int zeroCount = defLength-postfix.length();
        StringBuilder name = new StringBuilder();
        for(int j=0;j<zeroCount;j++){
            name.append(ZERO);
        }
        name.append(postfix);
        return postfix.toString();
    }
    
    /**
     * Returns random key for.
     *
     * @return Long.
     */
    protected Long getKey() {
        return getRandomGenerator().getLongValue(0L, MAX_KEY_VALUE);
    }
    
    

}
