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

package org.amanzi.neo.data_generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.CallData;
import org.amanzi.neo.data_generator.data.CallPair;
import org.amanzi.neo.data_generator.data.CommandRow;
import org.amanzi.neo.data_generator.data.ProbeData;
import org.amanzi.neo.data_generator.utils.CommandCreator;
import org.amanzi.neo.data_generator.utils.FileBuilder;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;

/**
 * Generate AMS data (individual calls)
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class AmsDataGenerator {
    
    private static final String PROBE_NUMBER_PREFIX = "110";
    private static final String ZERO = "0";
    
    private static final int MAX_LA = 10000;
    private static final double MAX_FREQUENCY = 999;
    private static final long MAX_NETWORK_ID = 125000;
    
    private static final int CALL_DURATION_PERIODS_COUNT = 8;
    private static final float[] CALL_DURATION_BORDERS = new float[]{0,1.25f,2.5f,3.75f,5,7.5f,10,12.5f,45,1000};
    
    private static final int MILLISECONDS = 1000;
    private static final long HOUR = 60*60*MILLISECONDS;
    private static final long START_TIME = new Date(1249344651495L).getTime(); 
    
    private String directory;
    private Long networkIdentity;
    
    private Integer hours;
    private Integer calls;
    private Integer callVariance;
    private Integer probesCount;
    private List<ProbeInfo> probes;
    
    /**
     * Constructor.
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     */
    public AmsDataGenerator(String aDirectory,Integer aHours, 
                        Integer aCallsPerHour, Integer aCallPerHourVariance,
                        Integer aProbes){
        directory = aDirectory;
        hours = aHours;
        calls = aCallsPerHour;
        callVariance = aCallPerHourVariance;
        probesCount = aProbes;
    }
    
    /**
     * Generate data.
     *
     * @throws IOException
     */
    public void generate()throws IOException{
        networkIdentity = getRandomGenerator().getLongValue(0L, MAX_NETWORK_ID);
       initProbes(); 
       List<CallPair> data = buildData();
       saveData(data);
    }
    
    /**
     * Save data.
     *
     * @param data
     * @throws IOException
     */
    private void saveData(List<CallPair> data) throws IOException {
        FileBuilder fileBuilder = new FileBuilder(directory);
        fileBuilder.saveData(data);
    }
    
    /**
     * Initialize probes.
     */
    private void initProbes(){
       probes = new ArrayList<ProbeInfo>(probesCount); 
       int defLength = probesCount.toString().length();
       RandomValueGenerator generator = getRandomGenerator();
       for(int i=1; i<=probesCount; i++){
           String name = getProbeName(i, defLength);
           String number = PROBE_NUMBER_PREFIX+name;           
           Integer la = generator.getIntegerValue(0, MAX_LA);
           Double fr = generator.getDoubleValue(0.0, MAX_FREQUENCY);
           ProbeInfo probe = new ProbeInfo(name, number, la, fr);
           probes.add(probe);
       }
    }

    /**
     * Build data.
     *
     * @return List of {@link CallPair}.
     */
    private List<CallPair> buildData(){
        List<CallPair> data = initPairs();
        for(CallPair pair : data){            
            List<CallData> calls = buildCalls(pair.getFirstProbe(),pair.getSecondProbe());
            pair.setData(calls);
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
    private List<CallData> buildCalls(Integer source, Integer receiver) {
        List<CallData> calls = new ArrayList<CallData>();
        HashMap<Integer, List<Long>> hourMap = buildHourMap();
        for(Integer hour : hourMap.keySet()){
            for(Long duration : hourMap.get(hour)){
                CallData call = buildCall(source, receiver, hour, duration);
                calls.add(call);
            }
        }
        return calls;
    }
    
    /**
     * Build one call.
     *
     * @param sourceNum Integer
     * @param receiverNum Integer
     * @param hour Integer
     * @param duration Long
     * @return {@link CallData}
     */
    private CallData buildCall(Integer sourceNum, Integer receiverNum, Integer hour, Long duration){
        Long startHour = START_TIME+HOUR*hour;
        Long endHour = START_TIME+HOUR*(hour+1);
        Long start = getRamdomTime(startHour, endHour);
        
        Long time = getRamdomTime(startHour, start);
        ProbeData source = getNewProbeData(time, sourceNum);
        ProbeInfo sourceInfo = probes.get(sourceNum-1);
        time = getRamdomTime(time, start);
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
        
        sourceCommands.add(CommandCreator.getCtsdcRow(start));
        time = getRamdomTime(0L, duration);
        CommandRow atdRow = CommandCreator.getAtdRow(start+time, receiver.getNumber());
        sourceCommands.add(atdRow);
        
        
        time = getRamdomTime(time, duration);
        CommandRow ctocp1 = CommandCreator.getCtocpRow(start+time);
        
        time = getRamdomTime(time, duration);
        receiverCommands.add(CommandCreator.getCticnRow(start+time,source.getNumber()));
        time = getRamdomTime(time, duration);
        CommandRow ataRow = CommandCreator.getAtaRow(start+time);
        receiverCommands.add(ataRow);
        
        
        time = getRamdomTime(time, duration);
        CommandRow ctocp2 = CommandCreator.getCtocpRow(start+time);
        CommandRow ctcc = CommandCreator.getCtccRow(null);
        sourceCommands.add(CommandCreator.getAtdRow(atdRow,ctocp1,ctocp2,ctcc));
        
        time = getRamdomTime(time, duration);
        Long end = start+duration;
        ctcc = CommandCreator.getCtccRow(end);
        receiverCommands.add(CommandCreator.getAtaRow(ataRow, ctcc));
        
        Long rest = START_TIME+HOUR*(hour+1)-end;
        if(rest<0){
            rest = HOUR;
        }
        time = getRamdomTime(0L, rest);
        sourceCommands.add(CommandCreator.getAthRow(end+time));
        time = getRamdomTime(time, rest);
        CommandRow ctcrRow = CommandCreator.getCtcrRow(end+time);
        time = getRamdomTime(time, rest);
        sourceCommands.add(CommandCreator.getAthRow(end+time,ctcrRow));            
        
        time = getRamdomTime(time, rest);
        ctcrRow = CommandCreator.getCtcrRow(null);
        receiverCommands.add(CommandCreator.getUnsoCtcrRow(end+time,ctcrRow));
        
        return new CallData(getKey(),source, receiver);
    }

    /**
     * Returns random time in interval.
     *
     * @param start Long
     * @param end Long
     * @return Long
     */
    private Long getRamdomTime(Long start, Long end) {
        return getRandomGenerator().getLongValue(start, end);
    }
    
    /**
     * Initialize new probe data.
     *
     * @param time Long (time of first command)
     * @param number Integer (probe number)
     * @return {@link ProbeData}
     */
    private ProbeData getNewProbeData(Long time,Integer number){
        ProbeInfo probeInfo = probes.get(number-1);
        String name = probeInfo.getName();
        String callNumber = probeInfo.getPhoneNumber();
        ProbeData data = new ProbeData(name,callNumber,getKey());
        data.getCommands().add(CommandCreator.getProbeNumberRow(time, name, callNumber));
        return data;
    }
    
    /**
     * Initialize different probe pairs.
     *
     * @return List of {@link CallPair}.
     */
    private List<CallPair> initPairs(){
        List<CallPair> result = new ArrayList<CallPair>();
        int sourceCount = probesCount/2+(probesCount%2==0?0:1);
        RandomValueGenerator generator = getRandomGenerator();
        while (result.isEmpty()) {
            for (int i = 1; i <= sourceCount; i++) {
                for (int j = sourceCount + 1; j <= probesCount; j++) {
                    boolean canBePair = generator.getBooleanValue();
                    if (canBePair) {
                        result.add(getCallPair(i, j));
                        result.add(getCallPair(j, i));
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Initialize new call pair.
     *
     * @param source Integer
     * @param receiver Integer
     * @return {@link CallPair}
     */
    private CallPair getCallPair(Integer source, Integer receiver){
        String sourceName = probes.get(source-1).getName();
        String receiverName = probes.get(receiver-1).getName();
        return new CallPair(source, receiver, new String[]{sourceName,receiverName});
    }
    
    /**
     * Build map of calls duration in all hours.
     *
     * @return HashMap<Integer, List<Long>> (key - hour, value - list of durations).
     */
    private HashMap<Integer, List<Long>> buildHourMap(){
        HashMap<Integer, List<Long>> result = new HashMap<Integer, List<Long>>(hours);
        for(int i = 0; i<hours; i++){
            int count = calls + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            result.put(i, buildDurationMap(count));
        }
        return result;
    }
    
    /**
     * Build map of calls duration in one hour.
     *
     * @param allCountPerHour Integer (call count)
     * @return list of durations
     */
    private List<Long> buildDurationMap(Integer allCountPerHour){
        List<Long> result = new ArrayList<Long>(allCountPerHour);
        RandomValueGenerator generator = RandomValueGenerator.getGenerator();
        for(int i=0; i<allCountPerHour; i++) {
            int period = generator.getIntegerValue(0, CALL_DURATION_PERIODS_COUNT);
            Long[] borders = getPeriodBorders(period);
            Long duration = generator.getLongValue(borders[0], borders[1]);            
            result.add(duration);
        }
        return result;
    }
    
    /**
     * Returns borders of duration period.
     *
     * @param period Integer (period number)
     * @return Long[]
     */
    private Long[] getPeriodBorders(Integer period){
        Long start = (long)(CALL_DURATION_BORDERS[period]*MILLISECONDS);
        Long end = (long)(CALL_DURATION_BORDERS[period+1]*MILLISECONDS);
        return new Long[]{start,end};
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
    private Long getKey() {
        return getRandomGenerator().getLongValue(0L, (long)1E10);
    }
    
    /**
     * Getter for random generator.
     *
     * @return {@link RandomValueGenerator}
     */
    private RandomValueGenerator getRandomGenerator() {
        return RandomValueGenerator.getGenerator();
    }
    
    /**
     * Saving common probe information.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    protected class ProbeInfo{
        private String name;
        private String phoneNumber;
        private Integer localArea;
        private Double frequency;
        
        /**
         * Constructor.
         * @param aName String probe name
         * @param aPhoneNumber String phone number
         * @param aLocalArea Integer local area
         * @param aFrequency Double frequency
         */
        public ProbeInfo(String aName, String aPhoneNumber, Integer aLocalArea, Double aFrequency){
            name = aName;
            phoneNumber = aPhoneNumber;
            localArea = aLocalArea;
            frequency = aFrequency;
        }
        
        /**
         * Probe name.
         *
         * @return String
         */
        public String getName(){
            return name;
        }
        
        /**
         * Phone number.
         *
         * @return String
         */
        public String getPhoneNumber(){
            return phoneNumber;
        }
        
        /**
         * Local area.
         *
         * @return Integer.
         */
        public Integer getLocalAria(){
            return localArea;
        }
          
        /**
         * Frequency.
         *
         * @return Double.
         */
        public Double getFrequency(){
            return frequency;
        }
        
    }

}
