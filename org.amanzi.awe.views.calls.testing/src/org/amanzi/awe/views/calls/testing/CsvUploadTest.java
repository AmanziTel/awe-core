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

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.enums.AggregationCallTypes;
import org.amanzi.awe.views.calls.enums.IAggrStatisticsHeaders;
import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.upload.StatisticsDataLoader;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.Call;
import org.amanzi.neo.data_generator.data.calls.csv.CsvData;
import org.amanzi.neo.data_generator.data.calls.csv.CsvHeaders;
import org.amanzi.neo.data_generator.data.calls.csv.FileData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Tests for {@link StatisticsDataLoader}
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CsvUploadTest extends AmsStatisticsTest{
    
    private StatisticsCallType currentCallType;
    
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
        executeTest(DAY,3,3,1,2);
    }
    
    /**
     * Check statistics by several days.
     *
     * @throws IOException (problem in data generation)
     * @throws ParseException (problem in gets etalon parameters)
     */
    @Test
    public void testCallStatisicsSeveralDays()throws IOException, ParseException{
        executeTest(DAY*2,3,2,1,2);
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
    protected Node loadData(String dataDir) throws IOException {
        StatisticsDataLoader loader = new StatisticsDataLoader(dataDir, "test", "test network", getNeo(), true);
        loader.run(new NullProgressMonitor());
        return loader.getVirtualDataset();
    }
    
    @Override
    protected HashMap<Integer, ProbeStat> generateDataFiles(Integer aHours, Integer aDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes, String dataDir) throws IOException, ParseException {
        IDataGenerator generator = getDataGenerator(aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, dataDir);
        CsvData generated = (CsvData)generator.generate();
        return buildStatisticsByGenerated(generated, aHours, aDrift);
    }

    /**
     * Build statistics by generated data.
     * @param generated
     * @param hours
     * @return HashMap<Integer, ProbeStat>
     */
    private HashMap<Integer, ProbeStat> buildStatisticsByGenerated(CsvData generated, Integer hours, Integer drift) {
        if(hours+drift> DAY){
            return buildStatistcsByPeriod(generated, CallTimePeriods.MONTHLY);
        }
        if(hours>1){
            return buildStatistcsByPeriod(generated, CallTimePeriods.DAILY);
        }
        return buildStatistcsByPeriod(generated, CallTimePeriods.HOURLY);
    }

    /**
     * Build statistics by period.
     * @param generated
     * @param period
     * @return HashMap<Integer, ProbeStat>
     */
    private HashMap<Integer, ProbeStat> buildStatistcsByPeriod(CsvData generated, CallTimePeriods period) {
        if(period.equals(CallTimePeriods.HOURLY)){
            return buildHourlyStatistics(generated);
        }
        CallTimePeriods undPeriod = period.getUnderlyingPeriod();
        HashMap<Integer, ProbeStat> statistics = buildStatistcsByPeriod(generated, undPeriod);        
        for(Integer probe : statistics.keySet()){
            if(probe.equals(SECOND_LEVEL_STAT_ID)){
                continue;
            }
            ProbeStat curr = statistics.get(probe);
            for (StatisticsCallType callType : getFirstLevelStatTypes()) {
                currentCallType = callType;
                curr.addStatistcs(callType,collectStatisticsByUnderling(curr.getStatisticsByPeriod(callType,undPeriod), period));
            }
        }
        return buildAggregationStatistics(statistics, period);
    }
    
    @Override
    protected HashMap<Integer, ProbeStat> buildAggregationStatistics(HashMap<Integer, ProbeStat> statistics, CallTimePeriods period) {
        ProbeStat aggrStat = statistics.get(SECOND_LEVEL_STAT_ID);
        if(aggrStat==null){
            aggrStat = new ProbeStat(SECOND_LEVEL_STAT_ID);
            statistics.put(SECOND_LEVEL_STAT_ID, aggrStat);
        }
        PeriodStat periodStat = new PeriodStat(period);
        for(AggregationCallTypes stat : AggregationCallTypes.values()){
            StatisticsCallType callType = stat.getRealType();
            HashMap<Long,HashMap<IStatisticsHeader, Number>> allUtilValues = new HashMap<Long,HashMap<IStatisticsHeader, Number>>();
            for(Integer probe : statistics.keySet()){
                if(probe.equals(SECOND_LEVEL_STAT_ID)){
                    continue;
                }
                PeriodStat currStat = statistics.get(probe).getStatisticsByPeriod(callType, period);
                for (Long time : currStat.getAllTimesSorted()) {
                    HashMap<IStatisticsHeader, Number> row = currStat.getRowValues(time);
                    HashMap<IStatisticsHeader, Number> utilValues = allUtilValues.get(time);
                    if(utilValues==null){
                        utilValues = new HashMap<IStatisticsHeader, Number>();
                        allUtilValues.put(time, utilValues);
                    }
                    for (IStatisticsHeader util : stat.getUtilHeaders()) {
                        for (IStatisticsHeader real : ((IAggrStatisticsHeaders)util).getDependendHeaders()) {
                            Number value = row.get(real);
                            Number curr = utilValues.get(util);
                            utilValues.put(util, updateValueByHeader(curr, value, util));
                        }
                    }
                    periodStat.incSourceCount(time);
                }
            }
            for (Long time : allUtilValues.keySet()) {
                HashMap<IStatisticsHeader, Number> utilValues = allUtilValues.get(time);
                if(utilValues==null){
                    continue;
                }
                HashMap<IStatisticsHeader, Number> resultRow = periodStat.getRowValues(time);                
                if(resultRow==null){
                    resultRow = new HashMap<IStatisticsHeader, Number>();
                    periodStat.addRow(time, resultRow);
                }
                for (IStatisticsHeader aggr : stat.getAggrHeaders()) {
                    resultRow.put(aggr, getAggrStatValue(utilValues, (IAggrStatisticsHeaders)aggr));
                }
            }            
        }
        aggrStat.addStatistcs(StatisticsCallType.AGGREGATION_STATISTICS,periodStat);
        return statistics;
    }

    private List<StatisticsCallType> getFirstLevelStatTypes() {
        return StatisticsCallType.getTypesByLevel(StatisticsCallType.FIRST_LEVEL);
    }

    /**
     *
     * @param generated
     * @return
     */
    private HashMap<Integer, ProbeStat> buildHourlyStatistics(CsvData generated) {
        HashMap<Integer, ProbeStat> result = new HashMap<Integer, ProbeStat>();
        HashMap<String, FileData> data = generated.getPart1();
        data.putAll(generated.getPart2());
        Long startTime = getStartTime();
        for(String key : data.keySet()){
            FileData fileData = data.get(key);
            for(int i=0; i<fileData.getLineCount(); i++){
                Integer probe = getProbeNumber((String)fileData.getCellValue(i, CsvHeaders.HOST));
                ProbeStat probeStat = result.get(probe);
                if(probeStat==null){
                    probeStat = new ProbeStat(probe);
                    result.put(probe, probeStat);
                }
                Long start = (Long)fileData.getCellValue(i, CsvHeaders.STARTTIME);
                if(startTime==null||start<startTime){
                    startTime = start;
                }
                for(CsvHeaders header : CsvHeaders.values()){
                    if(header.getPart()==CsvHeaders.COMMON_PART){ 
                        continue;
                    }
                    currentCallType = HeaderTypes.getTypeByHeader(header.getFullName()).getRealType();
                    PeriodStat periodStat = probeStat.getStatisticsByPeriod(currentCallType,CallTimePeriods.HOURLY);
                    if(periodStat==null){
                        periodStat = new PeriodStat(CallTimePeriods.HOURLY);
                        probeStat.addStatistcs(currentCallType,periodStat);
                    }
                    HashMap<IStatisticsHeader, Number> row = periodStat.getRowValues(start);
                    if(row == null){
                        row = new HashMap<IStatisticsHeader, Number>();
                        periodStat.addRow(start, row);
                    }
                    IStatisticsHeader statHeader = getStatHeaderByCsv(header);
                    Number cellValue = (Number)fileData.getCellValue(i, header);
                    if (cellValue!=null) {
                        row.put(statHeader, cellValue);
                    }
                }
            }
        }
        setStartTime(startTime);
        return buildAggregationStatistics(result, CallTimePeriods.HOURLY);
    }
    
    private IStatisticsHeader getStatHeaderByCsv(CsvHeaders csvHeader){
        String name = csvHeader.getFullName();
        StatisticsCallType callType = HeaderTypes.getTypeByHeader(name).getRealType();
        for(IStatisticsHeader header : callType.getHeaders()){
            if(name.endsWith(header.getTitle())){
                return header;
            }
        }
        if(callType.equals(StatisticsCallType.GROUP)&&name.equals("SL-SRV-GC-1_ATTEMPT")){
            return StatisticsHeaders.CALL_ATTEMPT_COUNT;
        }
        return null;
    }
    
    @Override
    protected Number getAggrValueByCells(Node parentCell, IStatisticsHeader header) {
        IAggrStatisticsHeaders realHeader = (IAggrStatisticsHeaders)header;        
        HashMap<IStatisticsHeader, Number> sourceValues = new HashMap<IStatisticsHeader, Number>();
        for(Node cell : getAllSourceCells(parentCell)){
            IStatisticsHeader real = getRealHeaderByAggr(cell, realHeader);
            Number value = getCellValue(cell, real);
            Number curr = sourceValues.get(real);
            sourceValues.put(real, updateValueByHeader(curr, value, real));
        }
        List<IStatisticsHeader> utilHeaders = realHeader.getDependendHeaders();
        HashMap<IStatisticsHeader, Number> utilValues = new HashMap<IStatisticsHeader, Number>();
        for(IStatisticsHeader util : utilHeaders){
            for(IStatisticsHeader real : ((IAggrStatisticsHeaders)util).getDependendHeaders()){
                Number value = sourceValues.get(real);
                Number curr = utilValues.get(util);
                utilValues.put(util, updateValueByHeader(curr, value, util));
            }
        }
        return getAggrStatValue(utilValues, realHeader);
    }
    
    private IStatisticsHeader getRealHeaderByAggr(Node cell,IAggrStatisticsHeaders aggrHeader){
        String headerName = (String)cell.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        for(IStatisticsHeader real : getAllRealForAggregation(aggrHeader)){
            if(headerName.equals(real.getTitle())){
                return real;
            }
        }
        throw new IllegalArgumentException("Unknown header ic cell "+headerName);
    }
    
    private Integer getProbeNumber(String name){
        return Integer.parseInt(name.split(" ")[0].substring(PROBE_NAME_PREFIX.length()));
    }

    @Override
    protected List<IAggrStatisticsHeaders> getAggregationHeaders() {
        List<IStatisticsHeader> headers = StatisticsCallType.AGGREGATION_STATISTICS.getHeaders();
        List<IAggrStatisticsHeaders> result = new ArrayList<IAggrStatisticsHeaders>(headers.size());
        for(IStatisticsHeader curr : headers){            
            IAggrStatisticsHeaders aggrHeader = (IAggrStatisticsHeaders)curr;
            List<IStatisticsHeader> allReal = getAllRealForAggregation(aggrHeader);
            if(currentCallType.getHeaders().containsAll(allReal)){
                result.add(aggrHeader);
            }
        }
        return result;
    }
    
    private List<IStatisticsHeader> getAllRealForAggregation(IAggrStatisticsHeaders aggrHeader){
        List<IStatisticsHeader> result = new ArrayList<IStatisticsHeader>();
        for(IStatisticsHeader util : aggrHeader.getDependendHeaders()){
            result.addAll(((IAggrStatisticsHeaders)util).getDependendHeaders());
        }
        return result;
    }
    
    @Override
    protected void assertResult(HashMap<Integer, ProbeStat> generated, CallStatistics statistics, Integer hours, Integer drift) {
        for (StatisticsCallType callType : getFirstLevelStatTypes()) {
            currentCallType = callType;
            super.assertResult(generated, statistics, hours, drift);
        }
    }

    
    
    @Override
    protected StatisticsCallType getCallType() {
        return currentCallType;
    }

    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        return DataGenerateManager.getCsvStatisticsGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes, false);
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(Call call) throws ParseException {
        return null;
    }

    @Override
    protected boolean hasSecondLevelStatistics() {
        return true;
    }
    
private enum HeaderTypes{
        
        INDIVIDUAL(StatisticsCallType.INDIVIDUAL, "SL-SRV-SC"),
        GROUP(StatisticsCallType.GROUP, "SL-SRV-GC"),
        ITSI_ATTACH(StatisticsCallType.ITSI_ATTACH,"SL-INH-ATT"),
        ITSI_CC(StatisticsCallType.ITSI_CC,"SL-INH-CC_"),
        TSM(StatisticsCallType.TSM,"SL-SRV-TSM"),
        SDS(StatisticsCallType.SDS,"SL-SRV-SDS"),
        EMERGENCY(StatisticsCallType.EMERGENCY,"SL-SRV-EC-1"),
        HELP(StatisticsCallType.HELP,"SL-SRV-EC-2"),
        ALARM(StatisticsCallType.ALARM,"SL-SRV-ALM"),
        CS_DATA(StatisticsCallType.CS_DATA,"SL-SRV-CSD"),
        PS_DATA(StatisticsCallType.PS_DATA,"SL-SRV-IP");        
        
        private StatisticsCallType realType;
        private String prefix;
        
        private HeaderTypes(StatisticsCallType type, String headerPrefix) {
            realType = type;
            prefix = headerPrefix;
        }
        
        /**
         * @return Returns the realType.
         */
        public StatisticsCallType getRealType() {
            return realType;
        }
        
        public static HeaderTypes getTypeByHeader(String header){
            for(HeaderTypes type : values()){
                if(header.startsWith(type.prefix)){
                    return type;
                }
            }
            return null;
        }
    }

}
