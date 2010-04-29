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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.IndividualCallConstants;
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
 * Tests for individual calls statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class IndividualCallStatTest extends CallStatisticsTest{
    
    /**
     * Prepare operations before execute test.
     */
    @Before
    public void prepareTests(){
        prepareMainDirectory();
        initProjectService();
        initCallBorders();
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
        executeTest(DAY,3,5,3,6);
    }
    
    /**
     * Check statistics by several days.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsSeveralDays()throws IOException, ParseException{
        executeTest(DAY*2,3,3,2,6);
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
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes, String dataDir) {
        IDataGenerator generator = DataGenerateManager.getIndividualAmsGenerator(dataDir, aHours,aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        return generator;
    }

    @Override
    protected Float getCallSetupDuration(CallData call, Date start) throws ParseException {
        ProbeData data = call.getReceiverProbes().get(0);
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equalsIgnoreCase(CTCC_COMMAND)){
                return (float)(row.getTime().getTime()-start.getTime())/MILLISECONDS;
            }
            if(row.getCommand().equalsIgnoreCase(ATA_COMMAND)){
                for(Object add : row.getAdditional()){
                    if(add instanceof String){
                        String str = (String)add;
                        if(str.contains(CTCC_COMMAND)){
                           String timeStr = str.substring(1, str.indexOf(CTCC_COMMAND)-1); 
                           Date end = TIME_FORMATTER.parse(timeStr);
                           return (float)(end.getTime()-start.getTime())/MILLISECONDS;
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
    protected StatisticsCallType getCallType() {
        return StatisticsCallType.INDIVIDUAL;
    }

    @Override
    protected ICallStatisticsConstants getStatisticsConstants() {
        return new IndividualCallConstants();
    }

    @Override
    protected HashMap<StatisticsHeaders, Number> getStatValuesFromCall(CallData call)  throws ParseException {
        HashMap<StatisticsHeaders, Number> result = new HashMap<StatisticsHeaders, Number>();
        ICallStatisticsConstants constants = getStatisticsConstants();
        Date start = getCallStartTime(call);
        Float setupDuration = getCallSetupDuration(call, start);
        result.put(StatisticsHeaders.CALL_ATTEMPT_COUNT,1L);
        if (setupDuration<constants.getCallConnTimeLimit()) {
            result.put(StatisticsHeaders.SUCC_SETUP_COUNT,1L);
            Integer setupPeriod = getSetupDurationPeriod(setupDuration);
            switch (setupPeriod) {
            case 0:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P1,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P1,setupDuration);
                break;
            case 1:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P2,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P2,setupDuration);
                break;
            case 2:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P3,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P3,setupDuration);
                break;
            case 3:
                result.put(StatisticsHeaders.SETUP_TM_Z1_P4,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_P4,setupDuration);
                break;
            case 4:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L1,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L1,setupDuration);
                break;
            case 5:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L2,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L2,setupDuration);
                break;
            case 6:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L3,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L3,setupDuration);
                break;
            case 7:
                result.put(StatisticsHeaders.SETUP_TM_Z1_L4,1L);
                result.put(StatisticsHeaders.SETUP_DUR_Z1_L4,setupDuration);
            default:
                break;
            }
            result.put(StatisticsHeaders.SETUP_TIME_MIN,setupDuration);
            result.put(StatisticsHeaders.SETUP_TIME_MAX,setupDuration);
            result.put(StatisticsHeaders.SETUP_TOTAL_DUR,setupDuration);
            Float callDuration = getCallDuration(call, start);
            if(callDuration>=constants.getIndivCallDurationTime()){
                result.put(StatisticsHeaders.CALL_DISC_TIME,1L);
            }
            List<Float> audioQuality = getCallAudioQualitySorted(call);
            if (!audioQuality.isEmpty()) {
                for(Float curr : audioQuality){
                    if(curr>=constants.getIndivCallQualLimit()){
                        result.put(StatisticsHeaders.AUDIO_QUAL_SUCC,1L);
                        break;
                    }
                }
                HashMap<Integer, List<Float>> audioMap = getAudioMap(audioQuality);
                List<Float> list = audioMap.get(0);
                result.put(StatisticsHeaders.AUDIO_QUAL_P1,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P1,getSumFromList(list));
                list = audioMap.get(1);
                result.put(StatisticsHeaders.AUDIO_QUAL_P2,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P2,getSumFromList(list));
                list = audioMap.get(2);
                result.put(StatisticsHeaders.AUDIO_QUAL_P3,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P3,getSumFromList(list));
                list = audioMap.get(3);
                result.put(StatisticsHeaders.AUDIO_QUAL_P4,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_P4,getSumFromList(list));
                list = audioMap.get(4);
                result.put(StatisticsHeaders.AUDIO_QUAL_L1,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L1,getSumFromList(list));
                list = audioMap.get(5);
                result.put(StatisticsHeaders.AUDIO_QUAL_L2,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L2,getSumFromList(list));
                list = audioMap.get(6);
                result.put(StatisticsHeaders.AUDIO_QUAL_L3,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L3,getSumFromList(list));
                list = audioMap.get(7);
                result.put(StatisticsHeaders.AUDIO_QUAL_L4,list==null||list.isEmpty()?null:list.size());
                result.put(StatisticsHeaders.AUDIO_QUAL_Z1_L4,getSumFromList(list));

                result.put(StatisticsHeaders.AUDIO_QUAL_MIN,audioQuality.get(0));
                result.put(StatisticsHeaders.AUDIO_QUAL_MAX,audioQuality.get(audioQuality.size()-1));
                result.put(StatisticsHeaders.AUDIO_QUAL_TOTAL,getSumFromList(audioQuality));
            }
        }
        return result;
    }

    @Override
    protected List<Float> getCallAudioQualitySorted(CallData call) {
        List<Float> result = new ArrayList<Float>();
        ICallStatisticsConstants constants = getStatisticsConstants();
        float min = constants.getIndivCallQualMin();
        float max = constants.getIndivCallQualMax();
        ProbeData data = call.getSourceProbe();
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equals(PESQ_COMMAND)){
                Float value = Float.valueOf(row.getAdditional().get(0).toString());
                if(min<=value&&value<=max){
                    result.add(value);
                }
            }
        }
        data = call.getReceiverProbes().get(0);
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equals(PESQ_COMMAND)){
                Float value = Float.valueOf(row.getAdditional().get(0).toString());
                if(min<=value&&value<=max){
                    result.add(value);
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    protected Float getCallDuration(CallData call, Date start) throws ParseException{
        ProbeData data = call.getSourceProbe();
        for(CommandRow row : data.getCommands()){
            if(row.getCommand().equalsIgnoreCase(CTCR_COMMAND)){
                return (float)(row.getTime().getTime()-start.getTime())/MILLISECONDS;
            }
            if(row.getCommand().equalsIgnoreCase(ATH_COMMAND)){
                for(Object add : row.getAdditional()){
                    if(add instanceof String){
                        String str = (String)add;
                        if(str.contains(CTCR_COMMAND)){
                           String timeStr = str.substring(1, str.indexOf(CTCR_COMMAND)-1); 
                           Date end = TIME_FORMATTER.parse(timeStr);
                           return (float)(end.getTime()-start.getTime())/MILLISECONDS;
                        }
                    }
                }
            }
        }
        return null;
    }

}
