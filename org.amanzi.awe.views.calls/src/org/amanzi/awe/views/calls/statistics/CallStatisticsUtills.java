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

package org.amanzi.awe.views.calls.statistics;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.CallProperties.CallResult;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Common methods for all call statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallStatisticsUtills {
    
    /*
     * a Hour period
     */
    public static final long HOUR = 1000 * 60 * 60;
    
    /*
     * a Day period
     */
    public static final long DAY = 24 * HOUR; 
    
    protected static final float MAX_DURATION_FOR_DELAY = 10.0f;
    
    /**
     * Returns call connection time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getCallConnectionTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.SETUP_DURATION.getId(),null);
        Long connectionTime = (property==null||property.equals(Double.NaN))?null:(Long)property;        
        return connectionTime==null||connectionTime<0?null:(float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call duration time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getCallDurationTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.CALL_DURATION.getId(),null);
        Long connectionTime = (property==null||property.equals(Double.NaN))?null:(Long)property; 
        return connectionTime==null||connectionTime<0?null:((float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR);
    }
    
    /**
     * Returns call termination time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getCallTerminationTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.TERMINATION_DURATION.getId(),null);
        Long connectionTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return connectionTime==null||connectionTime<0?null:(float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call handover time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getCallHandoverTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.CC_HANDOVER_TIME.getId(),null);
        Long connectionTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return connectionTime==null||connectionTime<0?null:(float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call reselection time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getCallReselectionTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.CC_RESELECTION_TIME.getId(),null);
        Long connectionTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return connectionTime==null||connectionTime<0?null:(float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call audio sample qualities.
     *
     * @param callNode Node (call)
     * @return Float[]
     */
    public static float[] getCallAudioQuality(Node callNode){
        return (float[])callNode.getProperty(CallProperties.LQ.getId(),new float[]{}); 
    }
    
    /**
     * Returns call audio sample delay.
     *
     * @param callNode Node (call)
     * @return Float[]
     */
    public static float[] getCallAudioDelay(Node callNode){
        float[] delays = (float[])callNode.getProperty(CallProperties.DELAY.getId(),new float[]{});
        return delays;
    }
    
    /**
     * Returns message received time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getMessageReceiveTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.MESS_RECEIVE_TIME.getId(),null);
        Long receiveTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return receiveTime==null||receiveTime<0?null:(float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns message acknowledge time.
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getMessageAcknowledgeTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.MESS_ACKNOWLEDGE_TIME.getId(),null);
        Long receiveTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return receiveTime==null||receiveTime<0?null:(float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns message delay time (alarm).
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getMessageDelayTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.ALM_MESSAGE_DELAY.getId(),null);
        Long receiveTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return receiveTime==null||receiveTime<0?null:(float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns first message delay time (alarm).
     *
     * @param callNode Node (call)
     * @return float
     */
    public static Float getFirstMessageDelayTime(Node callNode){
        Object property = callNode.getProperty(CallProperties.ALM_FIRST_MESS_DELAY.getId(),null);
        Long receiveTime = (property==null||property.equals(Double.NaN))?null:(Long)property;
        return receiveTime==null||receiveTime<0?null:(float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call audio sample qualities in borders.
     *
     * @param callNode  Node (call)
     * @param start Float
     * @param end Float
     * @param inclStart boolean (is include start)
     * @param inclEnd boolean (is include end)
     * @return list of Float
     */
    public static List<Float> getAllGoodQualities(Node callNode, Float start, Float end, boolean inclStart, boolean inclEnd){
        float[] callAudioQualities = getCallAudioQuality(callNode);
        List<Float> good = new ArrayList<Float>(callAudioQualities.length);
        for(float callAudioQuality : callAudioQualities){
            if(isValueInBorders(callAudioQuality, start, end, inclStart, inclEnd)){
                good.add(callAudioQuality);
            }
        }
        return good;
    }
    
    /**
     * Returns call audio sample delays in borders.
     *
     * @param callNode  Node (call)
     * @param start Float
     * @param end Float
     * @param inclStart boolean (is include start)
     * @param inclEnd boolean (is include end)
     * @return list of Float
     */
    public static List<Float> getAllGoodDelays(Node callNode, Float start, Float end, boolean inclStart, boolean inclEnd){
        float[] callAudioDelays = getCallAudioDelay(callNode);
        List<Float> good = new ArrayList<Float>(callAudioDelays.length);
        for(float callAudioDelay : callAudioDelays){
            if(isValueInBorders(callAudioDelay, start, end, inclStart, inclEnd)){
                good.add(callAudioDelay);
            }
        }
        return good;
    }
    
    /**
     * Check call for success.
     *
     * @param callNode Node (call)
     * @return boolean
     */
    public static boolean isCallSuccess(Node callNode, boolean inclInconclusive){
        CallResult callResult = CallResult.valueOf((String)callNode.getProperty(CallProperties.CALL_RESULT.getId(),null));
        if(callResult==null){
            return false;
        }
        if(callResult.equals(CallResult.SUCCESS)){
            return true;
        }
        if(inclInconclusive){
            boolean inconclusive = (Boolean)callNode.getProperty(INeoConstants.PROPERTY_IS_INCONCLUSIVE,false);
            return callResult.equals(CallResult.FAILURE)&&inconclusive;
        }
        return false;
    }
    
    /**
     * Check call for attempt.
     *
     * @param callNode Node (call)
     * @return boolean
     */
    public static boolean isCallAttempt(Node callNode, boolean inclInconclusive){
        if(inclInconclusive){
            return true;
        }
        boolean inconclusive = (Boolean)callNode.getProperty(INeoConstants.PROPERTY_IS_INCONCLUSIVE,false);        
        return !inconclusive;
    }
    
    /**
     * Check call for success and time limit.
     *
     * @param callNode  Node (call)
     * @param constants IStatisticsConstants
     * @return boolean
     */
    public static boolean isCallInTimeLimit(Node callNode, ICallStatisticsConstants constants, boolean inclInconclusive){
        if(!isCallSuccess(callNode,inclInconclusive)){
            return false;
        }
        Float connectionTime = getCallConnectionTime(callNode);
        return connectionTime!=null&&connectionTime<=constants.getCallConnTimeLimit();
    }
    
    /**
     * Check call for success, time limit and duration time for delay.
     *
     * @param callNode  Node (call)
     * @param constants IStatisticsConstants
     * @return boolean
     */
    public static boolean isCallDuratiomGood(Node callNode, ICallStatisticsConstants constants, boolean inclInconclusive){
        if(!isCallInTimeLimit(callNode, constants,inclInconclusive)){
            return false;
        }
        Float duration = getCallDurationTime(callNode);
        Float connection = getCallConnectionTime(callNode);
        Float termination = getCallTerminationTime(callNode);
        float callDuration = duration-connection-termination;
        return (duration!=null)&&(connection!=null)&&(termination!=null)&&(callDuration < MAX_DURATION_FOR_DELAY);
    }
    
    /**
     * Is value between start and end.
     *
     * @param value Float
     * @param start Float
     * @param end Float
     * @param inclStart boolean (is include start)
     * @param inclEnd boolean (is include end)
     * @return boolean
     */
    public static boolean isValueInBorders(Float value, Float start, Float end, boolean inclStart, boolean inclEnd){
        return value!=null&&((inclStart&&start.equals(value))||start<value) && (value<end||(inclEnd&&end.equals(value)));
    }
    
    public static CallTimePeriods getHighestPeriod(long minTime, long maxTime) {
        long delta = CallTimePeriods.DAILY.getFirstTime(maxTime) - CallTimePeriods.DAILY.getFirstTime(minTime);
        if (delta >= DAY) {
            return CallTimePeriods.MONTHLY;
        }
        delta = CallTimePeriods.HOURLY.getFirstTime(maxTime) - CallTimePeriods.HOURLY.getFirstTime(minTime);
        if (delta >= HOUR) {
            return CallTimePeriods.DAILY;
        }
        
        return CallTimePeriods.HOURLY;
    }
    
    public static long getNextStartDate(CallTimePeriods period, long endDate, long currentStartDate) {
        long nextStartDate = period.addPeriod(currentStartDate);
        if(!period.equals(CallTimePeriods.HOURLY)&&(nextStartDate > endDate)){
            nextStartDate = endDate;
        }
        return nextStartDate;
    }

}
