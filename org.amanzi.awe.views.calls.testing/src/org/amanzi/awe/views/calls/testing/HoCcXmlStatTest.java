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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.AggregationCallTypes;
import org.amanzi.awe.views.calls.enums.AggregationStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.statistics.constants.CcHoConstants;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Tests for Handover/Cell change statistics (xml).
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class HoCcXmlStatTest extends IndividualCallXmlStatTest{
    
    private static final int PERIODS_COUNT = 8;
    
    private StatisticsCallType currentCallType;
    private float[] durationBorders;
    
    /**
     * Prepare operations before execute test.
     */
    @Before
    public void prepareTests(){
        prepareMainDirectory();
        initProjectService();
        initCallBorders();
        durationBorders = new float[PERIODS_COUNT+1];
        durationBorders[0] = CcHoConstants.HANDOVER_DELAY_P1_LOW;
        durationBorders[1] = CcHoConstants.HANDOVER_DELAY_P2_LOW;
        durationBorders[2] = CcHoConstants.HANDOVER_DELAY_P3_LOW;
        durationBorders[3] = CcHoConstants.HANDOVER_DELAY_P4_LOW;
        durationBorders[4] = CcHoConstants.HANDOVER_DELAY_L1_LOW;
        durationBorders[5] = CcHoConstants.HANDOVER_DELAY_L2_LOW;
        durationBorders[6] = CcHoConstants.HANDOVER_DELAY_L3_LOW;
        durationBorders[7] = CcHoConstants.HANDOVER_DELAY_L4_LOW;
        durationBorders[8] = 100;
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
        IDataGenerator generator = DataGenerateManager.getXmlHoCcAmsGenerator(dataDir, aHours,aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        return generator;
    }
    
    @Override
    protected HashMap<Integer, ProbeStat> buildStatisticsByGenerated(List<CallGroup> generated, int hours, int drift, boolean needAggregation)
            throws ParseException {
        currentCallType = StatisticsCallType.INDIVIDUAL;
        HashMap<Integer, ProbeStat> indResult = super.buildStatisticsByGenerated(generated, hours, drift, false);
        currentCallType = StatisticsCallType.ITSI_HO;
        HashMap<Integer, ProbeStat> hoResult = super.buildStatisticsByGenerated(generated, hours, drift, false);
        currentCallType = StatisticsCallType.ITSI_CC;
        HashMap<Integer, ProbeStat> ccResult = super.buildStatisticsByGenerated(generated, hours, drift, false);
        currentCallType = null;
        return unionStats(indResult,hoResult, ccResult, getHighPeriod());
    }
    
    private HashMap<Integer, ProbeStat> unionStats(HashMap<Integer, ProbeStat> indResult, HashMap<Integer, ProbeStat> hoResult,HashMap<Integer, ProbeStat> ccResult, CallTimePeriods period){
        HashMap<Integer, ProbeStat> result;
        Set<Integer> probes;
        if (period.equals(CallTimePeriods.HOURLY)) {
            result = new HashMap<Integer, ProbeStat>();
            probes = new HashSet<Integer>(hoResult.keySet());
            probes.addAll(ccResult.keySet());
        }else{
            result = unionStats(indResult, hoResult, ccResult, period.getUnderlyingPeriod());
            probes = result.keySet();
        }
        for(Integer probe : probes){
            if(probe.equals(SECOND_LEVEL_STAT_ID)){
                continue;
            }
            ProbeStat indStat = indResult.get(probe);
            ProbeStat hoStat = hoResult.get(probe);
            ProbeStat ccStat = ccResult.get(probe); 
            ProbeStat currStat= result.get(probe);
            if (currStat==null) {
                currStat = new ProbeStat(probe);
                result.put(probe, currStat);
            }
            currStat.addStatistcs(StatisticsCallType.INDIVIDUAL, indStat.getStatisticsByPeriod(StatisticsCallType.INDIVIDUAL, period));            
            if(hoStat!=null){
                currStat.addStatistcs(StatisticsCallType.ITSI_HO, hoStat.getStatisticsByPeriod(StatisticsCallType.ITSI_HO, period));
            }
            if(ccStat!=null){
                currStat.addStatistcs(StatisticsCallType.ITSI_CC, ccStat.getStatisticsByPeriod(StatisticsCallType.ITSI_CC, period));
            }                        
        }
        return buildAggregationStatistics(result, period);
    }
    
    @Override
    protected void assertResult(HashMap<Integer, ProbeStat> generated, CallStatistics statistics, Integer hours, Integer drift) {
        currentCallType = StatisticsCallType.ITSI_HO;
        super.assertResult(generated, statistics, hours, drift);
        currentCallType = StatisticsCallType.ITSI_CC;
        super.assertResult(generated, statistics, hours, drift);
    }
    
    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        List<IAggrStatisticsHeaders> result = super.getAggregationHeaders();
        result.add(AggregationStatisticsHeaders.INH_HO_CC);
        result.add(AggregationStatisticsHeaders.INH_HO);
        result.add(AggregationStatisticsHeaders.INH_CC);
        return result;
    }

    @Override
    protected StatisticsCallType getCallType() {
        return currentCallType;
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        switch (currentCallType) {
        case INDIVIDUAL:
            return super.getStatValuesFromCall(call);
        case ITSI_HO:
            return getHoStatValuesFromCall(call);
        case ITSI_CC:   
            return getCcStatValuesFromCall(call);
        default:
            return new HashMap<IStatisticsHeader, Number>();
        }
    }
    
    private HashMap<IStatisticsHeader, Number> getHoStatValuesFromCall(Call call){
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        Long parameter = (Long)call.getParameter(CallParameterNames.HO_TIME);
        if(parameter==null){
            return result;
        }
        result.put(StatisticsHeaders.CC_HO_ATTEMPTS, 1L);        
        Float handoverTime = parameter.floatValue()/MILLISECONDS;
        if (handoverTime<=CcHoConstants.HANDOVER_TIME_LIMIT) {
            result.put(StatisticsHeaders.CC_HO_SUCCESS,1L);            
        }
        Integer setupPeriod = getTimePeriod(handoverTime);
        switch (setupPeriod) {
        case 0:
            result.put(StatisticsHeaders.CC_HO_TIME_P1,1L);
            break;
        case 1:
            result.put(StatisticsHeaders.CC_HO_TIME_P2,1L);
            break;
        case 2:
            result.put(StatisticsHeaders.CC_HO_TIME_P3,1L);
            break;
        case 3:
            result.put(StatisticsHeaders.CC_HO_TIME_P4,1L);
            break;
        case 4:
            result.put(StatisticsHeaders.CC_HO_TIME_L1,1L);
            break;
        case 5:
            result.put(StatisticsHeaders.CC_HO_TIME_L2,1L);
            break;
        case 6:
            result.put(StatisticsHeaders.CC_HO_TIME_L3,1L);
            break;
        case 7:
            result.put(StatisticsHeaders.CC_HO_TIME_L4,1L);
        default:
            break;
        }
        return result;
    }

    private HashMap<IStatisticsHeader, Number> getCcStatValuesFromCall(Call call){
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        Long parameter = (Long)call.getParameter(CallParameterNames.CC_TIME);
        if(parameter==null){
            return result;
        }
        result.put(StatisticsHeaders.CC_RES_ATTEMPTS, 1L);        
        Float handoverTime = parameter.floatValue()/MILLISECONDS;
        if (handoverTime<=CcHoConstants.HANDOVER_TIME_LIMIT) {
            result.put(StatisticsHeaders.CC_RES_SUCCESS,1L);            
        }
        Integer setupPeriod = getTimePeriod(handoverTime);
        switch (setupPeriod) {
        case 0:
            result.put(StatisticsHeaders.CC_RES_TIME_P1,1L);
            break;
        case 1:
            result.put(StatisticsHeaders.CC_RES_TIME_P2,1L);
            break;
        case 2:
            result.put(StatisticsHeaders.CC_RES_TIME_P3,1L);
            break;
        case 3:
            result.put(StatisticsHeaders.CC_RES_TIME_P4,1L);
            break;
        case 4:
            result.put(StatisticsHeaders.CC_RES_TIME_L1,1L);
            break;
        case 5:
            result.put(StatisticsHeaders.CC_RES_TIME_L2,1L);
            break;
        case 6:
            result.put(StatisticsHeaders.CC_RES_TIME_L3,1L);
            break;
        case 7:
            result.put(StatisticsHeaders.CC_RES_TIME_L4,1L);
        default:
            break;
        }
        return result;
    }
    
    /**
     * Gets number of call duration period.
     *
     * @param duration Long
     * @return Integer
     */
    protected Integer getTimePeriod(Float time){
        for(int i=0; i<PERIODS_COUNT-1; i++){
            float start = durationBorders[i];
            float end = durationBorders[i+1];
            if(start<time && time<=end){
                return i;
            }
        }
        return PERIODS_COUNT-1;
    }

    @Override
    protected List<AggregationCallTypes> getAggregationTypes() {
        List<AggregationCallTypes> result = new ArrayList<AggregationCallTypes>();
        result.add(AggregationCallTypes.INDIVIDUAL);
        result.add(AggregationCallTypes.ITSI_CC);
        return result;
    }

}
