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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.amanzi.neo.data_generator.data.IGeneratedData;
import org.amanzi.neo.data_generator.data.calls.csv.CsvData;
import org.amanzi.neo.data_generator.data.calls.csv.CsvHeaders;
import org.amanzi.neo.data_generator.data.calls.csv.FileData;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.call.CallFileBuilder;
import org.apache.log4j.Logger;

/**
 * <p>
 * Generate data for cvs statistics files.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CsvDataGenerator extends CallStatisticsDataGenerator{
    
    private static final int COMMON_PERIODS_COUNT = 8;
    
    private static final float[] IND_CALL_SETUP_BORDERS = new float[]{0.01f,1.25f,2.5f,3.75f,5,7.5f,10,12.5f,45};
    private static final float[] GR_CALL_SETUP_BORDERS = new float[]{0.01f,0.125f,0.25f,0.375f,0.5f,0.75f,1,2,5};
    private static final float[] AUDIO_QUAL_BORDERS = new float[]{-0.5f,2.2f,2.3f,2.4f,2.5f,3,3.5f,4,4.5f};
    private static final float[] AUDIO_DELAY_BORDERS = new float[]{0.1f,150,250,300,350,400,450,550,700};
    
    private static final float[] ATTACH_BORDERS = new float[]{0.1f,5,10,15,20,25,30,35,40};
    private static final float[] CC_BORDERS = new float[]{0.1f,0.25f,0.5f,0.75f,1,1.25f,1.5f,1.75f,5};
    
    private static final float ALARM_MIN = 0.1f;
    private static final float ALARM_MAX = 1000f;
    private static final float[] ALARM_BORDERS1 = new float[]{0.1f,5,15,25,35,45,55,60,ALARM_MAX};
    private static final float[] ALARM_BORDERS2 = new float[]{0.1f,90,180,270,360,450,460,470,ALARM_MAX};
    private static final float[] ALARM_BORDERS3 = new float[]{0.1f,170,340,510,680,850,875,900,ALARM_MAX};
    private static final float[] ALARM_FIRST_BORDERS = new float[]{0.1f,2,4,6,8,10,15,20,ALARM_MAX};
    
    private static final float[] CS_PS_RATE1_BORDERS = new float[]{1.3f,1.7f,1.8f,1.9f,2,2.1f,2.2f,2.3f,5};
    private static final float[] CS_PS_RATE2_BORDERS = new float[]{0,0.6f,0.7f,0.9f,1,1.1f,1.2f,1.3f,1.7f};
    
    private static final Logger LOGGER = Logger.getLogger(CsvDataGenerator.class);
    
    private static final String PROBE_PREFIX = "PROBE";
    private static final String FILE_NAME_PREFIX = "PM.AMS.part";
    private static final String FILE_NAME_SEPARATOR = ".";
    private static final String FILE_NAME_POSTFIX = "csv";
    
    private static final String VERSION_NUMBER = "2.5.0 22.07.2009";
    private static final int CONFIG_VERSION = 2;
    
    private boolean hasDuplicate;
    private HashMap<String, List<ProbeInfo>> probes;
    
    /**
     * Constructor.
     * @param aDirectory String (path to save data)
     * @param aHours Integer (count of hours)
     * @param aHourDrift Integer (drift of start time)
     * @param aCallsPerHour Integer (call count in hour)
     * @param aCallPerHourVariance Integer (call variance in hour)
     * @param aProbes Integer (probes count)
     * @param needDuplicates boolean (has probes with same name, but different LA or F)
     */
    public CsvDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes, boolean needDuplicates) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
        hasDuplicate = needDuplicates;
    }    
   
    @Override
    public IGeneratedData generate() {
        initializeProbes();
        CsvData result = new CsvData();
        result.setPart1(generatePart(CsvHeaders.FIRST_PART));
        result.setPart2(generatePart(CsvHeaders.SECOND_PART));
        CallFileBuilder fileBuilder = new CallFileBuilder(getDirectory(), null);
        try {
            fileBuilder.saveCsvData(result);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        return result;
    }
    
    private void initializeProbes() {
        int count = getProbesCount();
        int defNameLength = getDefNameLength(count);
        probes = new HashMap<String, List<ProbeInfo>>();
        RandomValueGenerator generator = getRandomGenerator();
        for(int i=1;i<=count; i++){
            String name = getProbeName(i, defNameLength);
            Integer la = getLa();
            String freq = getFreqUsString();
            List<ProbeInfo> info = new ArrayList<ProbeInfo>();
            info.add(new ProbeInfo(name, la, freq));            
            boolean needDubl = hasDuplicate&&generator.getBooleanValue();
            if(needDubl){
                la = getLa();
                freq = getFreqUsString();
                info.add(new ProbeInfo(name, la, freq));     
            }
            probes.put(name, info);
        }
    }
    
    private int getDefNameLength(int count){
        int length = 1;
        while(count>Math.pow(10, length)){
            length++;
        }
        return length;
    }
    
    private String getProbeName(int number, int defLength){
         String result = ""+number;
         while(result.length()<defLength){
             result="0"+result;
         }
         return PROBE_PREFIX+result;
    }
    
    private String getFreqUsString(){
        BigDecimal freq = new BigDecimal(getFrequency());
        freq = freq.setScale(3, RoundingMode.HALF_EVEN);
        return freq.toString();
    }

    private HashMap<String, FileData> generatePart(int number){
        Integer hours = getHours();
        HashMap<String, FileData> result = new HashMap<String, FileData>(hours);
        for(int i=0;i<hours;i++){
            Long start = getStartOfHour(i);
            Long end = getStartOfHour(i+1);
            String fileName = buildFileName(CsvHeaders.getTimeString(start), CsvHeaders.getTimeString(end), number);
            FileData file = new FileData(fileName, start);
            int line = 0;
            for(String probeName : probes.keySet()){
                List<ProbeInfo> info = probes.get(probeName);
                addLine(info.get(0), start, end, number, line++, file);
                boolean doDupl = getRandomGenerator().getBooleanValue();
                if(hasDuplicate&&info.size()>1&&doDupl){
                    addLine(info.get(1), start, end, number, line++, file);
                }
            }
            result.put(fileName, file);
        }
        return result;
    }
    
    private String buildFileName(String start, String end, int part){
        StringBuilder name = new StringBuilder(FILE_NAME_PREFIX).append(part)
                                        .append(FILE_NAME_SEPARATOR).append(start)
                                        .append(FILE_NAME_SEPARATOR).append(end)
                                        .append(FILE_NAME_SEPARATOR)
                                        .append(FILE_NAME_POSTFIX);
        return name.toString();
    }
    
    private void addLine(ProbeInfo probe,Long start,Long end, int part,int lineNumber, FileData file){
        file.addCellValue(lineNumber, CsvHeaders.STARTTIME, start);
        file.addCellValue(lineNumber, CsvHeaders.ENDTIME, end);
        file.addCellValue(lineNumber, CsvHeaders.HOST, probe.getName());
        file.addCellValue(lineNumber, CsvHeaders.LA, probe.getLa());
        file.addCellValue(lineNumber, CsvHeaders.FREQUENCY, probe.getFrequency());
        switch (part) {
        case CsvHeaders.FIRST_PART:
            addFirstPartHeaders(lineNumber, file);
            break;
        case CsvHeaders.SECOND_PART:
            addSecondPartHeaders(lineNumber, file);
            break;
        default:
            LOGGER.warn("Unknown part <"+part+">.");
        }
        file.addCellValue(lineNumber, CsvHeaders.VERSION_NUMBER, VERSION_NUMBER);
        file.addCellValue(lineNumber, CsvHeaders.CONFIG_VERSION, CONFIG_VERSION);
    }
    
    private void addFirstPartHeaders(int lineNumber, FileData file){
        RandomValueGenerator generator = getRandomGenerator();
        Integer allCallsCount = getCalls();
        Integer callVariance = getCallVariance();
        
        Integer callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.SC1_CALL_ATTEMPT_COUNT, callAttemptCount);
        Integer callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.SC1_SUCC_SETUP_COUNT, callSuccessCount);
        List<Float> allValues;
        HashMap<Integer, List<Float>> values;
        List<Float> timesInPeriod;
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, IND_CALL_SETUP_BORDERS[0], IND_CALL_SETUP_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TIME_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TIME_MAX, allValues.get(callSuccessCount-1));
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TOTAL_DUR, getSummFromList(allValues));
            values = getValues(allValues, IND_CALL_SETUP_BORDERS);
            timesInPeriod = values.get(0);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_P1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_P1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(1);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_P2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_P2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(2);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_P3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_P3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(3);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_P4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_P4, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(4);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_L1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_L1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(5);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_L2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_L2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(6);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_L3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_L3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(7);
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_TM_Z1_L4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.SC2_SETUP_DUR_Z1_L4, getSummFromList(timesInPeriod));
            file.addCellValue(lineNumber, CsvHeaders.SC3_CALL_DISC_TIME, generator.getIntegerValue(0, callSuccessCount));
            file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_SUCC, generator.getIntegerValue(0, callSuccessCount));
            Integer audioCount = generator.getIntegerValue(callSuccessCount * 4, callSuccessCount * 6);
            if (audioCount>0) {
                allValues = getValuesSorted(audioCount, AUDIO_QUAL_BORDERS[0], AUDIO_QUAL_BORDERS[COMMON_PERIODS_COUNT]);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_MIN, allValues.get(0));
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_MAX, allValues.get(audioCount - 1));
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_TOTAL, getSummFromList(allValues));
                values = getValues(allValues, AUDIO_QUAL_BORDERS);
                timesInPeriod = values.get(7);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_P1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_P1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(6);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_P2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_P2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(5);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_P3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_P3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(4);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_P4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_P4, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(3);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_L1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_L1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(2);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_L2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_L2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(1);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_L3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_L3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(0);
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_L4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC4_AUDIO_QUAL_Z1_L4, getSummFromList(timesInPeriod));
            }
            Integer delayCount = generator.getIntegerValue(0, callSuccessCount);
            if (delayCount>0) {
                allValues = getValuesSorted(delayCount, AUDIO_DELAY_BORDERS[0], AUDIO_DELAY_BORDERS[COMMON_PERIODS_COUNT]);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_MIN, allValues.get(0));
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_MAX, allValues.get(delayCount - 1));
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_TOTAL, getSummFromList(allValues));
                values = getValues(allValues, AUDIO_DELAY_BORDERS);
                timesInPeriod = values.get(0);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_P1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_P1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(1);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_P2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_P2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(2);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_P3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_P3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(3);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_P4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_P4, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(4);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_L1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_L1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(5);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_L2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_L2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(6);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_L3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_L3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(7);
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_COUNT_L4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.SC5_DELAY_Z1_L4, getSummFromList(timesInPeriod));
            }            
        }
        
        callAttemptCount = allCallsCount + generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.ATT_ATTEMPTS, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.ATT_SUCCESS, callSuccessCount);
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, ATTACH_BORDERS[0], ATTACH_BORDERS[COMMON_PERIODS_COUNT]);
            values = getValues(allValues, ATTACH_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.ATT_DELAY_L4, values.get(7).size());
        }
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.CC_HO_ATTEMPTS, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.CC_HO_SUCCESS, callSuccessCount);
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, CC_BORDERS[0], CC_BORDERS[COMMON_PERIODS_COUNT]);
            values = getValues(allValues, CC_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_HO_TIME_L4, values.get(7).size());
        }
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.CC_RES_ATTEMPTS, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.CC_RES_SUCCESS, callSuccessCount);
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, CC_BORDERS[0], CC_BORDERS[COMMON_PERIODS_COUNT]);
            values = getValues(allValues, CC_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CC_RES_TIME_L4, values.get(7).size());
        }
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.TSM_MESSAGE_ATTEMPT, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.TSM_MESSAGE_SUCC, generator.getIntegerValue(0, callAttemptCount));
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.SDS_MESSAGE_ATTEMPT, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.SDS_MESSAGE_SUCC, generator.getIntegerValue(0, callAttemptCount));
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.EC1_ATTEMPT, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.EC1_SUCCESS, generator.getIntegerValue(0, callAttemptCount));
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.EC2_ATTEMPT, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.EC2_SUCCESS, generator.getIntegerValue(0, callAttemptCount));
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.ALM_ATTEMPT, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.ALM_SUCCESS, callSuccessCount);
        
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, ALARM_MIN, ALARM_MAX);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_MAX, allValues.get(callSuccessCount - 1));
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM, getSummFromList(allValues));
            values = getValues(allValues, ALARM_BORDERS1);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z1_L4, values.get(7).size());
            values = getValues(allValues, ALARM_BORDERS2);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z2_L4, values.get(7).size());
            values = getValues(allValues, ALARM_BORDERS3);
            timesInPeriod = values.get(0);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_P1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_P1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(1);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_P2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_P2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(2);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_P3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_P3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(3);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_P4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_P4, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(4);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_L1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_L1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(5);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_L2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_L2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(6);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_L3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_L3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(7);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z3_L4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_TOTAL_SUM_L4, getSummFromList(timesInPeriod));
            allValues = getValuesSorted(callSuccessCount, ALARM_FIRST_BORDERS[0], ALARM_FIRST_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_FIRST_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_FIRST_MAX, allValues.get(callSuccessCount - 1));
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_FIRST_SUM, getSummFromList(allValues));
            values = getValues(allValues, ALARM_FIRST_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_P1, values.get(0).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_P2, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_P3, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_P4, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_L1, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_L2, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_L3, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.ALM_DELAY_Z4_L4, values.get(7).size());
        }
     }
    
    private void addSecondPartHeaders(int lineNumber, FileData file){
        RandomValueGenerator generator = getRandomGenerator();
        Integer allCallsCount = getCalls();
        Integer callVariance = getCallVariance();
        
        Integer callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.GC1_ATTEMPT, callAttemptCount);
        Integer callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.GC1_SUCC_SETUP_COUNT, callSuccessCount);
        List<Float> allValues;
        HashMap<Integer, List<Float>> values;
        Integer audioCount;
        if (callSuccessCount>0) {
            allValues = getValuesSorted(callSuccessCount, GR_CALL_SETUP_BORDERS[0], GR_CALL_SETUP_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TIME_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TIME_MAX, allValues.get(callSuccessCount-1));
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TOTAL_DUR, getSummFromList(allValues));
            values = getValues(allValues, GR_CALL_SETUP_BORDERS);
            List<Float> timesInPeriod = values.get(0);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_P1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_P1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(1);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_P2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_P2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(2);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_P3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_P3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(3);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_P4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_P4, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(4);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_L1, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_L1, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(5);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_L2, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_L2, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(6);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_L3, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_L3, getSummFromList(timesInPeriod));
            timesInPeriod = values.get(7);
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_TM_Z1_L4, timesInPeriod.size());
            file.addCellValue(lineNumber, CsvHeaders.GC2_SETUP_DUR_Z1_L4, getSummFromList(timesInPeriod));
            file.addCellValue(lineNumber, CsvHeaders.GC3_CALL_DISC_TIME, generator.getIntegerValue(0, callSuccessCount));
            file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_SUCC, generator.getIntegerValue(0, callSuccessCount));
            audioCount = generator.getIntegerValue(callSuccessCount * 4, callSuccessCount * 6);
            if (audioCount>0) {
                allValues = getValuesSorted(audioCount, AUDIO_QUAL_BORDERS[0], AUDIO_QUAL_BORDERS[COMMON_PERIODS_COUNT]);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_MIN, allValues.get(0));
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_MAX, allValues.get(audioCount - 1));
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_TOTAL, getSummFromList(allValues));
                values = getValues(allValues, AUDIO_QUAL_BORDERS);
                timesInPeriod = values.get(7);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_P1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_P1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(6);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_P2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_P2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(5);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_P3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_P3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(4);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_P4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_P4, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(3);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_L1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_L1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(2);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_L2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_L2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(1);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_L3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_L3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(0);
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_L4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC4_AUDIO_QUAL_Z1_L4, getSummFromList(timesInPeriod));
            }
            Integer delayCount = generator.getIntegerValue(0, callSuccessCount);
            if (delayCount>0) {
                allValues = getValuesSorted(delayCount, AUDIO_DELAY_BORDERS[0], AUDIO_DELAY_BORDERS[COMMON_PERIODS_COUNT]);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_MIN, allValues.get(0));
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_MAX, allValues.get(delayCount - 1));
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_TOTAL, getSummFromList(allValues));
                values = getValues(allValues, AUDIO_DELAY_BORDERS);
                timesInPeriod = values.get(0);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_P1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_P1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(1);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_P2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_P2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(2);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_P3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_P3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(3);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_P4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_P4, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(4);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_L1, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_L1, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(5);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_L2, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_L2, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(6);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_L3, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_L3, getSummFromList(timesInPeriod));
                timesInPeriod = values.get(7);
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_COUNT_L4, timesInPeriod.size());
                file.addCellValue(lineNumber, CsvHeaders.GC5_DELAY_Z1_L4, getSummFromList(timesInPeriod));
            }
        }
        //TODO check next
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.CSD1_ATTEMPTS, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.CSD1_SUCCESS, callSuccessCount);
        if (callSuccessCount>0) {
            file.addCellValue(lineNumber, CsvHeaders.CSD2_STABILITY, generator.getIntegerValue(0, callSuccessCount));
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE2_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_MAX, allValues.get(callSuccessCount-1));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z1_L4, values.get(0).size());
            values = getValues(allValues, CS_PS_RATE2_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_THROUGHPUT_Z2_L4, values.get(0).size());
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE1_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_CONNECT_TOTAL_DUR, getSummFromList(allValues));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_SUM_Z1_L4, values.get(0).size());
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE1_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_DATA_EXCH_SUCC, getSummFromList(allValues));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.CSD3_TIME_SUM_Z1_L4, values.get(0).size());
        }
        
        callAttemptCount = allCallsCount+generator.getIntegerValue(-callVariance, callVariance);
        file.addCellValue(lineNumber, CsvHeaders.IP1_ATTEMPTS, callAttemptCount);
        callSuccessCount = generator.getIntegerValue(0, callAttemptCount);
        file.addCellValue(lineNumber, CsvHeaders.IP1_SUCCESS, callSuccessCount);
        if (callSuccessCount>0) {
            file.addCellValue(lineNumber, CsvHeaders.IP2_STABILITY, generator.getIntegerValue(0, callSuccessCount));
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE2_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_MIN, allValues.get(0));
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_MAX, allValues.get(callSuccessCount-1));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z1_L4, values.get(0).size());
            values = getValues(allValues, CS_PS_RATE2_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_THROUGHPUT_Z2_L4, values.get(0).size());
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE1_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.CSD3_CONNECT_TOTAL_DUR, getSummFromList(allValues));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_SUM_Z1_L4, values.get(0).size());
            allValues = getValuesSorted(callSuccessCount, CS_PS_RATE1_BORDERS[0], CS_PS_RATE1_BORDERS[COMMON_PERIODS_COUNT]);
            file.addCellValue(lineNumber, CsvHeaders.IP3_DATA_EXCH_SUCC, getSummFromList(allValues));
            values = getValues(allValues, CS_PS_RATE1_BORDERS);
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_P1, values.get(7).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_P2, values.get(6).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_P3, values.get(5).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_P4, values.get(4).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_L1, values.get(3).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_L2, values.get(2).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_L3, values.get(1).size());
            file.addCellValue(lineNumber, CsvHeaders.IP3_TIME_SUM_Z1_L4, values.get(0).size());
        }
    }
    
    private List<Float> getValuesSorted(int count, Float min, Float max){
        List<Float> result = new ArrayList<Float>(count);
        RandomValueGenerator generator = getRandomGenerator();
        for(int i=0; i<count; i++){
            result.add(generator.getFloatValue(min, max));
        }
        Collections.sort(result);
        return result;
    }
    
    private HashMap<Integer, List<Float>> getValues(List<Float> valuesSorted, float[] borders){
        HashMap<Integer, List<Float>> result = new HashMap<Integer, List<Float>>(COMMON_PERIODS_COUNT);
        int last = 0;
        for(int i=0; i<COMMON_PERIODS_COUNT; i++){
            List<Float> currValues = new ArrayList<Float>();
            Float max = borders[i+1];
            while(last<valuesSorted.size()&&valuesSorted.get(last)<=max){
                currValues.add(valuesSorted.get(last++));
            }
            result.put(i, currValues);
        }
        return result;
    }
    
    private Float getSummFromList(List<Float> list){
        Float summ = 0f;
        for(Float value : list){
            summ+=value;
        }
        return summ;
    }
    
    private class ProbeInfo{
        
        private String name;
        private Integer la;
        private String freq;
        
        /**
         * Constructor.
         * @param probeName
         * @param locArea
         * @param frequency
         */
        public ProbeInfo(String probeName, Integer locArea, String frequency) {
            name = probeName;
            la = locArea;
            freq = frequency;
        }
        
        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return Returns the local area.
         */
        public Integer getLa() {
            return la;
        }
        
        /**
         * @return Returns the frequency.
         */
        public String getFrequency() {
            return freq;
        }
    }

}
