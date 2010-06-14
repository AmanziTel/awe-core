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
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.enums.AggregationCallTypes;
import org.amanzi.awe.views.calls.enums.AggregationStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.statistics.constants.ItsiAttachConstants;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Tests for ITSI attach statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ItsiAttachStatTest extends AmsStatisticsTest{
    
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
    
    private static final int PERIODS_COUNT = 7;
    private static final Float[] PERIODS_BORDERS = new Float[]{ItsiAttachConstants.DELAY_P1_LOW,
                                                                ItsiAttachConstants.DELAY_P2_LOW,
                                                                ItsiAttachConstants.DELAY_P3_LOW,
                                                                ItsiAttachConstants.DELAY_P4_LOW,
                                                                ItsiAttachConstants.DELAY_L1_LOW,
                                                                ItsiAttachConstants.DELAY_L2_LOW,
                                                                ItsiAttachConstants.DELAY_L3_LOW ,
                                                                ItsiAttachConstants.DELAY_L4_LOW};
    
    @Override
    protected StatisticsCallType getCallType() {
        return StatisticsCallType.ITSI_ATTACH;
    }

    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        return DataGenerateManager.getItsiAttachGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        result.put(StatisticsHeaders.ATT_ATTEMPTS, 1);
        Float duration = ((Long)call.getParameter(CallParameterNames.DURATION_TIME)).floatValue()/MILLISECONDS;
        if(duration<=ItsiAttachConstants.TIME_LIMIT){
            result.put(StatisticsHeaders.ATT_SUCCESS, 1);
            Integer setupPeriod = getDurationPeriod(duration);
            switch (setupPeriod) {
            case 0:
                result.put(StatisticsHeaders.ATT_DELAY_P1,1L);
                break;
            case 1:
                result.put(StatisticsHeaders.ATT_DELAY_P2,1L);
                break;
            case 2:
                result.put(StatisticsHeaders.ATT_DELAY_P3,1L);
                break;
            case 3:
                result.put(StatisticsHeaders.ATT_DELAY_P4,1L);
                break;
            case 4:
                result.put(StatisticsHeaders.ATT_DELAY_L1,1L);
                break;
            case 5:
                result.put(StatisticsHeaders.ATT_DELAY_L2,1L);
                break;
            case 6:
                result.put(StatisticsHeaders.ATT_DELAY_L3,1L);
                break;
            case 7:
                result.put(StatisticsHeaders.ATT_DELAY_L4,1L);
                break;
            default:
                break;
            }
        }
        return result;
    }

    /**
     * Gets number of call duration period.
     *
     * @param duration Long
     * @return Integer
     */
    protected Integer getDurationPeriod(Float duration){
        for(int i=0; i<PERIODS_COUNT; i++){
            float start = PERIODS_BORDERS[i];
            float end = PERIODS_BORDERS[i+1];
            if(start<=duration && duration<end){
                return i;
            }
        }
        return PERIODS_COUNT;
    }
    
    @Override
    protected boolean hasSecondLevelStatistics() {
        return true;
    }

    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        List<IAggrStatisticsHeaders> result = new ArrayList<IAggrStatisticsHeaders>();
        result.add(AggregationStatisticsHeaders.INH_AT);
        return result;
    }
    
    @Override
    protected List<AggregationCallTypes> getAggregationTypes() {
        List<AggregationCallTypes> result = new ArrayList<AggregationCallTypes>();
        result.add(AggregationCallTypes.ITSI_ATT);
        return result;
    }
    
}
