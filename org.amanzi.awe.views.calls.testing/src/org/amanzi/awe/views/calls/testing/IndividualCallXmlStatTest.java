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
import java.util.HashMap;
import java.util.List;

import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.CallParameterNames;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSXMLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Test for call statistics from XML files (Single calls)
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class IndividualCallXmlStatTest extends IndividualCallStatTest{
    
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
        IDataGenerator generator = DataGenerateManager.getXmlIndividualAmsGenerator(dataDir, aHours,aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        return generator;
    }
    
    @Override
    protected Node loadData(String dataDir) throws IOException {
        AMSXMLoader loader = new AMSXMLoader(dataDir,null, "test", "test network", getNeo(), true);
        loader.setLimit(5000);
        loader.run(new NullProgressMonitor());
        Node datasetNode = loader.getDatasetNode();
        return NeoUtils.findOrCreateVirtualDatasetNode(datasetNode, DriveTypes.AMS_CALLS, getNeo());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected HashMap<IStatisticsHeader, Number> getAudioDelayStatistics(Call call) {
        HashMap<IStatisticsHeader, Number> result = new HashMap<IStatisticsHeader, Number>();
        HashMap<String, Object> parameters = call.getParameters();
        List<Float> delays = new ArrayList<Float>();
        for(String key : parameters.keySet()){
            if(key.startsWith(CallParameterNames.AUDIO_DELAY)){
                List<Integer> audioDelays = (List<Integer>)parameters.get(key);
                int summ = 0;
                for(int delay : audioDelays){
                    summ+=delay;
                }
                delays.add(((float)(summ/audioDelays.size()))/MILLISECONDS);
            }
        }
        Collections.sort(delays);
        
        HashMap<Integer, List<Float>> delayMap = getDelayMap(delays);
        List<Float> list = delayMap.get(0);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_P1,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_P1,getSumFromList(list));
        list = delayMap.get(1);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_P2,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_P2,getSumFromList(list));
        list = delayMap.get(2);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_P3,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_P3,getSumFromList(list));
        list = delayMap.get(3);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_P4,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_P4,getSumFromList(list));
        list = delayMap.get(4);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_L1,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_L1,getSumFromList(list));
        list = delayMap.get(5);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_L2,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_L2,getSumFromList(list));
        list = delayMap.get(6);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_L3,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_L3,getSumFromList(list));
        list = delayMap.get(7);
        result.put(StatisticsHeaders.IND_DELAY_COUNT_L4,list==null||list.isEmpty()?null:list.size());
        result.put(StatisticsHeaders.IND_DELAY_Z1_L4,getSumFromList(list));
        
        result.put(StatisticsHeaders.IND_DELAY_MIN,delays.get(0));
        result.put(StatisticsHeaders.IND_DELAY_MAX,delays.get(delays.size()-1));
        result.put(StatisticsHeaders.IND_DELAY_TOTAL,getSumFromList(delays));
        return result; 
    }

}
