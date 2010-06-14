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

package org.amanzi.neo.data_generator.generate.calls.xml_data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.Probe;
import org.amanzi.neo.data_generator.generate.calls.log_data.AmsDataGenerator;
import org.amanzi.neo.data_generator.utils.call.CallGeneratorUtils;
import org.amanzi.neo.data_generator.utils.call.CallXmlFileBuilder;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Common class for all XML files generators.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class AmsXmlDataGenerator extends AmsDataGenerator{    
    
    private static final String ROOT_TAG_NAME = "tns:interfaceData";
    private static final String ROOT_PR_XSI_SCHEMA_LOCATION = "xsi:schemaLocation";
    private static final String ROOT_PR_XMLNS_XSI = "xmlns:xsi";
    private static final String ROOT_PR_XMLNS_TNS = "xmlns:tns";
    private static final String ROOT_VALUE_XSI_SCHEMA_LOCATION = "http://www.sitq.com/AMS_IF";
    private static final String ROOT_VALUE_XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String ROOT_VALUE_XMLNS_TNS = "http://www.sitq.com/AMS_IF";
    
    private static final String COMMON_DATA_TAG_NAME = "commonTestData";
    private static final String PROBE_TAG_NAME = "probeIDNumberMap";
    
    private static final String EVENTS_TAG_NAME = "events";
    private static final String GPS_DATA_TAG_NAME = "gpsData";
    private static final String GPS_DATA_LIST_TAG_NAME = "completeGpsDataList";
    private static final String GPS_DATA_ONE_TAG_NAME = "completeGpsData";
    
    private static final String TAG_PR_DELIVERY_TIME = "deliveryTime";
    private static final String TAG_PR_GPS_SENTENCE = "gpsSentence";
    
    protected static final String TAG_PR_PROBE_ID = "probeID";
    protected static final String TAG_PR_CALLED_NUMBER = "calledNumber";
    protected static final String TAG_PR_CALLING_NUMBER = "callingNumber";
    protected static final String TAG_PR_LA_BEFORE = "locationAreaBefore";
    protected static final String TAG_PR_LA_AFTER = "locationAreaAfter";
    
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS");

    /**
     * Constructor.
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public AmsXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }
    
    @Override
    protected void saveData(List<CallGroup> data) {
        try {
            CallXmlFileBuilder fileBuilder = new CallXmlFileBuilder(getDirectory());
            fileBuilder.saveData(getTypeKey(),data);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }
    
    /**
     * @return SavedTag (common root tag for all data)
     */
    protected SavedTag getRootTag(){
        SavedTag result = new SavedTag(ROOT_TAG_NAME, false);
        result.addAttribute(ROOT_PR_XMLNS_TNS, ROOT_VALUE_XMLNS_TNS);
        result.addAttribute(ROOT_PR_XMLNS_XSI, ROOT_VALUE_XMLNS_XSI);
        result.addAttribute(ROOT_PR_XSI_SCHEMA_LOCATION, ROOT_VALUE_XSI_SCHEMA_LOCATION);
        return result;
    }
    
    protected SavedTag generateCommonTestDataTags(Probe... probes){
        SavedTag result = new SavedTag(COMMON_DATA_TAG_NAME, false);
        for(Probe probe : probes){
            result.addInnerTag(getProbeTag(probe));
        }
        return result;
    }
   
    protected SavedTag getProbeTag(Probe probe){
        SavedTag result = new SavedTag(PROBE_TAG_NAME, false);
        result.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+probe.getName()));
        result.addInnerTag(getPropertyTag("phoneNumber", probe.getPhoneNumber()));
        result.addInnerTag(getPropertyTag("locationArea", probe.getLocalAria())); 
        result.addInnerTag(getPropertyTag("frequency", probe.getFrequency()));
        return result;
    }
    
    protected SavedTag getPropertyTag(String tagName, Object data){
        SavedTag result = new SavedTag(tagName, false);
        result.setData(data.toString());
        return result;
    }
    
    protected SavedTag getEventsTag(){
        SavedTag result = new SavedTag(EVENTS_TAG_NAME, false);
        return result;
    }
    
    protected SavedTag getEmptyTag(String tagName){
        return new SavedTag(tagName, true);
    }
    
    protected SavedTag getGpsDataTag(Long start, Long end, Probe... probes){
        SavedTag result = new SavedTag(GPS_DATA_TAG_NAME, false);
        SavedTag listTag = new SavedTag(GPS_DATA_LIST_TAG_NAME, false);
        for(Probe probe : probes){
            Long time = CallGeneratorUtils.getRamdomTime(start, end);            
            listTag.addInnerTag(getOneGpsDataTag(probe, time));
            time = CallGeneratorUtils.getRamdomTime(time, end);            
            listTag.addInnerTag(getOneGpsDataTag(probe, time));
        }
        result.addInnerTag(listTag);
        return result;
    }
    
    private SavedTag getOneGpsDataTag(Probe probe,Long time){
        SavedTag data = new SavedTag(GPS_DATA_ONE_TAG_NAME, false);
        data.addInnerTag(getPropertyTag(TAG_PR_PROBE_ID, "PROBE0"+probe.getName()));
        data.addInnerTag(getPropertyTag(TAG_PR_DELIVERY_TIME, getTimeString(time)));
        data.addInnerTag(getPropertyTag(TAG_PR_GPS_SENTENCE, getGpsSentense()));
        return data;
    }
    
    private String getGpsSentense(){
        StringBuilder result = new StringBuilder("$GPGLL,")
                                    .append(getCoord(1000.0,9999.0)).append(",N,")
                                    .append(getCoord(10000.0,99999.0)).append(",E,")
                                    .append(getRandomGenerator().getLongValue(1000L, 999999L)).append(",A");
        return result.toString();
    }
    
    private String getCoord(double min, double max){
        BigDecimal coord = new BigDecimal(getRandomGenerator().getDoubleValue(min, max));
        coord = coord.setScale(4,RoundingMode.HALF_DOWN);
        return coord.toString();
    }
    
    protected String getTimeString(Long time){
        return formatter.format(time)+"+00:00";
    }
}
