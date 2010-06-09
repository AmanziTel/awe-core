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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;

/**
 * <p>
 * Common class for all call statistics data generators.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class CallStatisticsDataGenerator implements IDataGenerator {
    
    protected static final String PROBE_NUMBER_PREFIX = "110";
    protected static final String ZERO = "0";
    
    private static final int MAX_LA = 10000;
    private static final double MAX_FREQUENCY = 999;
        
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
    private Long startTime;
    
    private Integer hours;
    private Integer calls;
    private Integer callVariance;
    private Integer probesCount;
    
    /**
     * Constructor.
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aHourDrift Integer (drift of start time)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     */
    public CallStatisticsDataGenerator(String aDirectory,Integer aHours, Integer aHourDrift,
            Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes) {
        directory = aDirectory;
        hours = aHours;
        calls = aCallsPerHour;
        callVariance = aCallPerHourVariance;
        probesCount = aProbes;
        startTime = START_TIME+aHourDrift*CallGeneratorUtils.HOUR;
    }
    
    /**
     * @return Returns the calls.
     */
    public Integer getCalls() {
        return calls;
    }
    
    /**
     * @return Returns the callVariance.
     */
    public Integer getCallVariance() {
        return callVariance;
    }
    
    /**
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * @return Returns the hours.
     */
    public Integer getHours() {
        return hours;
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
    
    /**
     * Getter for random generator.
     *
     * @return {@link RandomValueGenerator}
     */
    protected RandomValueGenerator getRandomGenerator() {
        return RandomValueGenerator.getGenerator();
    }
    
    /**
     * @return Returns the local area.
     */
    public Integer getLa() {
        return getRandomGenerator().getIntegerValue(0, MAX_LA);
    }
    
    /**
     * @return Returns the local area.
     */
    public Double getFrequency() {
        return getRandomGenerator().getDoubleValue(0.0, MAX_FREQUENCY);
    }
    
    protected Long getStartOfHour(Integer hour){
        return getStartTime()+CallGeneratorUtils.HOUR*hour;
    }
}
