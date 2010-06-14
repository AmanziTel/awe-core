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
import org.amanzi.awe.views.calls.statistics.constants.MessageConstants;
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
 * Tests for TSM messages statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TSMMessagesStatTest extends MessagesStatisticsTest{
    
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
    
    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        return DataGenerateManager.getTSMMessagesGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected StatisticsCallType getCallType() {
        return StatisticsCallType.TSM;
    }
    
    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>(getCallType().getHeaders().size());
        result.put(StatisticsHeaders.TSM_MESSAGE_ATTEMPT, 1);
        Float callDuration = getTimeByKey(call, CallParameterNames.DURATION_TIME);
        Float callAcknowledge = getTimeByKey(call, CallParameterNames.ACKNOWLEDGE_TIME);
        if(callDuration<=MessageConstants.TSM_SEND_TIME_LIMIT
                && callAcknowledge<=MessageConstants.TSM_REPLY_TIME_LIMIT){
            result.put(StatisticsHeaders.TSM_MESSAGE_SUCC, 1);
        }
        return result;
    }

    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        List<IAggrStatisticsHeaders> result = new ArrayList<IAggrStatisticsHeaders>();
        result.add(AggregationStatisticsHeaders.TSM);
        return result;
    }
    
    @Override
    protected List<AggregationCallTypes> getAggregationTypes() {
        List<AggregationCallTypes> result = new ArrayList<AggregationCallTypes>();
        result.add(AggregationCallTypes.TSM);
        return result;
    }
}
