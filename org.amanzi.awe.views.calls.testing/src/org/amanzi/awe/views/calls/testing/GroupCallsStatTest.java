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

import org.amanzi.awe.views.calls.enums.AggregationStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.statistics.constants.GroupCallConstants;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.Call;
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
    
    private int maxGroupSize = 3;
    
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
        executeTest(1,5,2,1,3);
    }
    
    /**
     * Check statistics by several hours.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsSeveralHours()throws IOException, ParseException{
        executeTest(5,0,10,5,3); 
    }

    /**
     * Check statistics by one day.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsOneDay()throws IOException, ParseException{
        executeTest(DAY,3,5,3,3);
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
        executeTest(DAY*2,3,3,2,3);
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
        return DataGenerateManager.getGroupAmsGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, maxGroupSize);
    }

    @Override
    protected StatisticsCallType getCallType() {
        return StatisticsCallType.GROUP;
    }

    @Override
    protected ICallStatisticsConstants getStatisticsConstants() {
        return new GroupCallConstants();
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getAudioDelayStatistics(Call call) {
        return null; //TODO get statistics
    }

    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        List<IAggrStatisticsHeaders> result = new ArrayList<IAggrStatisticsHeaders>();
        result.add(AggregationStatisticsHeaders.GC1);
        result.add(AggregationStatisticsHeaders.GC2_ZW2_AVG);
        result.add(AggregationStatisticsHeaders.GC2_ZW2_MIN);
        result.add(AggregationStatisticsHeaders.GC2_ZW2_MAX);
        result.add(AggregationStatisticsHeaders.GC3);
        result.add(AggregationStatisticsHeaders.GC4);
        result.add(AggregationStatisticsHeaders.GC4_ZW2_AVG);
        result.add(AggregationStatisticsHeaders.GC4_ZW2_MIN);
        result.add(AggregationStatisticsHeaders.GC4_ZW2_MAX);
        /*result.add(AggregationStatisticsHeaders.GC5_ZW1_AVG);
        result.add(AggregationStatisticsHeaders.GC5_ZW1_MIN);
        result.add(AggregationStatisticsHeaders.GC5_ZW1_MAX);*/
        return result;
    }

}
