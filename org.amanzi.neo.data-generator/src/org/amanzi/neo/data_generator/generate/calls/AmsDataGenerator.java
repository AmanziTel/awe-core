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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.GeneratedCallsData;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;
import org.amanzi.neo.data_generator.utils.call.CallFileBuilder;

/**
 * Generate AMS data (individual calls)
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class AmsDataGenerator implements IDataGenerator{
    
    private static final String PROBE_NUMBER_PREFIX = "110";
    private static final String ZERO = "0";
    
    private static final int MAX_LA = 10000;
    private static final double MAX_FREQUENCY = 999;
    private static final long MAX_NETWORK_ID = 125000;
    
    private static final int CALL_DURATION_PERIODS_COUNT = 8;
    
    private static final int MILLISECONDS = 1000;
    protected static final long HOUR = 60*60*MILLISECONDS;
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private static long START_TIME; 
    
    static{
        try {
            START_TIME = TIME_FORMATTER.parse("2008-01-01 00:00:00,000").getTime();
        } catch (ParseException e) {
            START_TIME = new Date(1249344651495L).getTime();
        }
    }
    
    private String directory;
    private Long networkIdentity;
    private Long startTime;
    
    private Integer hours;
    private Integer calls;
    private Integer callVariance;
    private Integer probesCount;
    private List<ProbeInfo> probes;
    
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
        directory = aDirectory;
        hours = aHours;
        calls = aCallsPerHour;
        callVariance = aCallPerHourVariance;
        probesCount = aProbes;
        startTime = START_TIME+aHourDrift*HOUR;
    }
    
    /**
     * @return Returns the probes.
     */
    public List<ProbeInfo> getProbes() {
        return probes;
    }
    
    /**
     * @return Returns the networkIdentity.
     */
    public Long getNetworkIdentity() {
        return networkIdentity;
    }
    
    /**
     * @return Returns the startTime.
     */
    public Long getStartTime() {
        return startTime;
    }
    
    /**
     * @return Returns the probesCount.
     */
    public Integer getProbesCount() {
        return probesCount;
    }
    
    @Override
    public GeneratedCallsData generate(){
        List<CallGroup> data;
        if (hours>0&&probesCount>0&&calls>0) {
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
            CallFileBuilder fileBuilder = new CallFileBuilder(directory,getDirectoryPostfix());
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
     * @return List of {@link CallGroup}.
     */
    private List<CallGroup> buildData(){
        List<CallGroup> data = initCallGroups();
        for(CallGroup pair : data){            
            List<CallData> calls = buildCalls(pair);
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
    private List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        HashMap<Integer, List<Long>> hourMap = buildHourMap();
        for(Integer hour : hourMap.keySet()){
            for(Long duration : hourMap.get(hour)){
                CallData call = buildCall(group, hour, duration);
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
    protected abstract CallData buildCall(CallGroup group, Integer hour, Long duration);

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
        ProbeInfo sourceProbeInfo = probes.get(source-1);
        String sourceName = sourceProbeInfo.getName();
        sourceProbeInfo.addSourceGroup(groupName);
        int resCount = receivers.length;
        List<Integer> receiverList = Arrays.asList(receivers);
        List<String> receiverNames = new ArrayList<String>(resCount);
        for(Integer receiver : receiverList){
            ProbeInfo resProbeInfo = probes.get(receiver-1);
            resProbeInfo.addResGroup(groupName);
            String receiverName = resProbeInfo.getName();
            receiverNames.add(receiverName);
        }             
        return new CallGroup(groupName, source, sourceName, receiverList,receiverNames);
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
        float[] durationBorders = getCallDurationBorders();
        Long start = (long)(durationBorders[period]*MILLISECONDS);
        Long end = (long)(durationBorders[period+1]*MILLISECONDS);
        return new Long[]{start,end};
    }
    
    /**
     * @return Returns Call duration borders
     */
    protected abstract float[] getCallDurationBorders();
    
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
        return getRandomGenerator().getLongValue(0L, (long)1E10);
    }
    
    /**
     * Getter for random generator.
     *
     * @return {@link RandomValueGenerator}
     */
    protected RandomValueGenerator getRandomGenerator() {
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
        private List<String> sourceGroups = new ArrayList<String>();
        private List<String> resGroups = new ArrayList<String>();
        
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
        
        /**
         * Set group to source groups
         *
         * @param number
         */
        public void addSourceGroup(String number){
            sourceGroups.add(number);
        }
        
        /**
         * @return Returns the source Gropes.
         */
        public List<String> getSourceGroups() {
            return sourceGroups;
        }
        
        /**
         * Set group to receiver groups
         *
         * @param number
         */
        public void addResGroup(String number){
            resGroups.add(number);
        }
        
        /**
         * @return Returns the receiver Gropes.
         */
        public List<String> getResGroups() {
            return resGroups;
        }
    }

}
