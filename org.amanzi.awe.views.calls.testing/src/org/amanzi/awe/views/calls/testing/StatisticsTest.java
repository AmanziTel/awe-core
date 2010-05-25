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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.upload.StatisticsDataLoader;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.loader.AMSXMLoader;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class StatisticsTest extends AmsStatisticsTest {
private long stat1TimeCorrelator=0;
private StatisticsCallType cellType;
    
    /**
     * Prepare operations before execute test.
     */
    @Before
    public void prepareTests(){
        prepareMainDirectory();
        initProjectService();
    }
    @Test
    public void testCompareStatistics()throws IOException, ParseException{
        stat1TimeCorrelator=0;//-3*60*60*1000;
        CallStatistics stat1 = createStatistics(loadXMLData());
        CallStatistics stat2 = createStatistics(loadCSVData());
        compareStatistics(stat1,stat2);

    }
    /**
     *
     * @param loadXMLData
     * @throws IOException 
     */
    private CallStatistics createStatistics(Node dataset) throws IOException {
       return new CallStatistics(dataset,getNeo(),true);
    }
    /**
     * @param stat2 
     * @param stat1 
     *
     */
    private void compareStatistics(CallStatistics stat1, CallStatistics stat2) {
        Transaction tx = getNeo().beginTx();
        try{
       StringBuilder errors=new StringBuilder();
       for (StatisticsCallType type:StatisticsCallType.values()){
           cellType=type;
           Node node1 = stat1.getPeriodNode(CallTimePeriods.HOURLY, type);
           Node node2 =  stat2.getPeriodNode(CallTimePeriods.HOURLY, type);
           if(node1==null&&node2==null){
               continue;
           }
           if (node1==null){
               errors.append('\n').append(String.format("AMS Statistic: not found root node for periods=%s and type=%s",CallTimePeriods.HOURLY,type));
               continue;
           }else if (node2==null){
               errors.append('\n').append(String.format("CSV Statistic: not found root node for periods=%s and type=%s",CallTimePeriods.HOURLY,type));
               continue;               
           }
           compareRootNode(node1,node2,errors);
       }
       Assert.assertTrue(errors.toString(), errors.length()==0);
        }finally{
            tx.finish();
        }
    }

    private void compareRootNode(Node node1, Node node2, StringBuilder errors) {
        Assert.assertNotNull(node1);
        Assert.assertNotNull(node2);
         Iterator<Node> stat1SrowIter = NeoUtils.getChildTraverser(node1).iterator();
         Iterator<Node> stat2SrowIter = NeoUtils.getChildTraverser(node1).iterator();
        while (stat1SrowIter.hasNext()||stat2SrowIter.hasNext()){
            Node sRow1 = stat1SrowIter.hasNext()?stat1SrowIter.next():null;
            Node sRow2 = stat2SrowIter.hasNext()?stat2SrowIter.next():null;
            Long time1;
            Long time2;
            do {
                if (sRow1==null){
                    dropAllRows("AMS Statistic",sRow2,stat2SrowIter,errors);
                    return;
                }else if (sRow2==null){
                    dropAllRows("CSV Statistic",sRow1,stat1SrowIter,errors);
                    return;         
                }
                time1 = NeoUtils.getNodeTime(sRow1) + stat1TimeCorrelator;
                time2 = NeoUtils.getNodeTime(sRow2);
                if (time1 < time2) {
                    sRow1 = dropSrowToTime("CSV Statistic", sRow1,stat1SrowIter, time2,errors);
                    time1 = sRow1==null?-1:NeoUtils.getNodeTime(sRow1) + stat1TimeCorrelator;
                } else if (time2 < time1) {
                    sRow2 = dropSrowToTime("AMS Statistic", sRow2,stat2SrowIter, time1,errors);
                    time2 = sRow2==null?-1:NeoUtils.getNodeTime(sRow2);
                }
            } while (!time1.equals(time2));
            compareSRow(sRow1,sRow2,errors);
        }
        
    }


    private void compareSRow(Node sRow1, Node sRow2, StringBuilder errors) {
        String name1 = NeoUtils.getNodeName(sRow1);
        String name2 = NeoUtils.getNodeName(sRow2);
        HashMap<IStatisticsHeader, Number> map1 = buildCellDataMap(sRow1);
        HashMap<IStatisticsHeader, Number> map2 = buildCellDataMap(sRow2);
        for(IStatisticsHeader header : getCallType().getHeaders()){
            Number value1 = map1.get(header);
            Number value2 = map2.get(header);
            boolean isEqual = value1==null?value2==null:value1.equals(value2);
            if (!isEqual){
                errors.append('\n').append(String.format("Headers %s is not equals for '%s'=%s, and '%s'=%s",header,name1,value1,name2,value2));
            }
            
        }
        
    }
    
    private Node dropSrowToTime(String prefix, Node sRow, Iterator<Node> sRowIterator, long time, StringBuilder errors) {
        errors.append('\n').append(String.format("%s do not have sRow %s",prefix, NeoUtils.getNodeName(sRow)));
        while (sRowIterator.hasNext()){
            Node result = sRowIterator.next();
            Long nodeTime = NeoUtils.getNodeTime(result);
            if (nodeTime>=time){
                return result;
            }
            errors.append('\n').append(String.format("%s do not have sRow %s",prefix, NeoUtils.getNodeName(result)));
        }
        return null;
    }
    /**
     *
     * @param string
     * @param errors 
     * @param sRow2
     * @param stat2SrowIter
     */
    private void dropAllRows(String prefix, Node sRow, Iterator<Node> sRowIterator, StringBuilder errors) {
        errors.append('\n').append(String.format("%s do not have sRow %s",prefix, NeoUtils.getNodeName(sRow)));
        while (sRowIterator.hasNext()) {
            Node node = sRowIterator.next();
            errors.append('\n').append(String.format("%s do not have sRow %s",prefix, NeoUtils.getNodeName(node)));
        }
    }
    /**
     * Finish all tests.
     */
    @AfterClass
    public static void finishAll(){
        clearMainDirectory();
    }
    /**
     * Load csv data.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Node loadCSVData() throws IOException {
        String dataDir="files/csv/";
        StatisticsDataLoader loader = new StatisticsDataLoader(dataDir, "test", "test network", getNeo(), true);
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
    }

    private Node loadXMLData() throws IOException {
        String dataDir="files/xml/";
        AMSXMLoader loader=new AMSXMLoader(dataDir,null,"testXML","testXMLNetwork",getNeo(),true);
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
        
    }
    /**
     * Finish test.
     */
    @After
    public void finish(){
        shutdownNeo();
    }
    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        return null;
    }

    @Override
    protected StatisticsCallType getCallType() {
        return cellType;
    }

    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance, Integer aProbes, String dataDir) {
        return null;
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        return null;
    }

    @Override
    protected boolean hasSecondLevelStatistics() {
        return false;
    }

}
