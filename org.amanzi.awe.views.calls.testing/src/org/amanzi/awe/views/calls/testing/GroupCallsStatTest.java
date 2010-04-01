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

package org.amanzi.awe.views.calls.testing;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.data.calls.ProbeData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Tests call statistics for group calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class GroupCallsStatTest extends CallStatisticsTest{
    
    private static final float[] CALL_DURATION_BORDERS = new float[]{0,0.125f,0.25f,0.375f,0.5f,0.75f,1,2,5,1000};

    private int maxGroupSize = 3;
    
    /**
     * Prepare operations before execute test.
     */
    @Before
    public void prepareTests(){
        prepareMainDirectory();
        initProjectService();
    }
    
    /**
     * Check statistics by one hour.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsOneHour()throws IOException, ParseException{
        executeTest(1,5,10,5,6);
    }
    
    /**
     * Check statistics by several hours.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsSeveralHours()throws IOException, ParseException{
        executeTest(5,0,10,5,6);
    }

    /**
     * Check statistics by one day.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsOneDay()throws IOException, ParseException{
        executeTest(24,3,5,3,6);
    }
    
    /**
     * Check statistics by several days.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsSeveralDays()throws IOException, ParseException{
        maxGroupSize = 2;
        executeTest(48,3,3,2,6);
    }
    
    /**
     * Finish test.
     */
    @After
    public void finish(){
        shutdownNeo();
    }
    
    /**
     * Finish all tests.
     */
    @AfterClass
    public static void finishAll(){
        clearMainDirectory();
    }
    
    @Override
    protected Long getCallDuration(CallData call, Date start) throws ParseException {
        ProbeData data = call.getSourceProbe();
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equalsIgnoreCase(CTCR_COMMAND)){
                return row.getTime().getTime()-start.getTime();
            }
            if(row.getCommand().equalsIgnoreCase(ATH_COMMAND)){
                for(Object add : row.getAdditional()){
                    if(add instanceof String){
                        String str = (String)add;
                        if(str.contains(CTCR_COMMAND)){
                           String timeStr = str.substring(1, str.indexOf(CTCR_COMMAND)-1); 
                           Date end = TIME_FORMATTER.parse(timeStr);
                           return end.getTime()-start.getTime();
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Date getCallStartTime(CallData call) {
        ProbeData data = call.getSourceProbe();
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equalsIgnoreCase(CTSDC_COMMAND)){
                return row.getTime();
            }
        }
        return null;
    }

    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        return DataGenerateManager.getGroupAmsGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, maxGroupSize);
    }

    @Override
    protected float[] getCallDurationBorders() {
        return CALL_DURATION_BORDERS;
    }

}
