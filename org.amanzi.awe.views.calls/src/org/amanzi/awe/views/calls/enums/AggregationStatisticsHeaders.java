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

package org.amanzi.awe.views.calls.enums;

import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.views.calls.Messages;
import org.amanzi.awe.views.calls.statistics.CallStatisticsUtills;
import org.amanzi.awe.views.calls.statistics.constants.IStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.SecondLevelConstants;
import org.amanzi.neo.core.enums.ColoredFlags;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Headers for second level statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum AggregationStatisticsHeaders implements IAggrStatisticsHeaders {

    SC1("SC1", Messages.R_SC1_TITLE,Messages.R_SC1_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_SC1, Condition.LT, UtilAggregationHeaders.SC_SUCC_SETUP_COUNT, UtilAggregationHeaders.SC_ATTEMPT_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_SC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_SC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    SC2_ZW2_AVG("SC2 ZW2(AVG)", Messages.R_SC2_ZW2_AVG_TITLE,Messages.R_SC2_ZW2_AVG_THRESHOLD_TITLE,Units.SECOND, StatisticsType.AVERAGE, SecondLevelConstants.MAX_SC2_AVERAGE, Condition.GT, UtilAggregationHeaders.SC_SETUP_TIME_TOTAL, UtilAggregationHeaders.SC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_SC2_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_SC2_AVG){
                return ColoredFlags.YELLOW;
            }
            return ColoredFlags.NONE;
        }
    },
    SC2_ZW2_MIN("SC2 ZW2(MIN)", Messages.R_SC2_ZW2_MIN_TITLE, null,Units.SECOND, StatisticsType.MIN, null, null, UtilAggregationHeaders.SC_SETUP_TIME_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    SC2_ZW2_MAX("SC2 ZW2(MAX)", Messages.R_SC2_ZW2_MAX_TITLE,Messages.R_SC2_ZW2_MAX_THRESHOLD_TITLE, Units.SECOND, StatisticsType.MAX, SecondLevelConstants.MAX_SC2_MAX, Condition.GT, UtilAggregationHeaders.SC_SETUP_TIME_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_SC2_MAX){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_SC2_MAX){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    SC3("SC3", Messages.R_SC3_TITLE,Messages.R_SC3_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_SC3, Condition.LT, UtilAggregationHeaders.SC_CALL_DISC_TIME, UtilAggregationHeaders.SC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_SC3){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallDurationTime(call);
            if(time!=null&&time<SecondLevelConstants.MIN_DURATION_SC3){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    SC4("SC4", Messages.R_SC4_TITLE,Messages.R_SC4_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_SC4, Condition.LT, UtilAggregationHeaders.SC_AUDIO_QUAL_SUCC, UtilAggregationHeaders.SC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_SC4){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioQuality(call);
            for(float value : values){
                if(value<SecondLevelConstants.MIN_QUALITY_SC4){
                    return ColoredFlags.RED;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    SC4_ZW2_AVG("SC4 ZW2(AVG)", Messages.R_SC4_ZW2_AVG_TITLE,Messages.R_SC4_ZW2_AVG_THRESHOLD_TITLE,Units.NONE, StatisticsType.AVERAGE, SecondLevelConstants.MIN_SC4_AVERAGE, Condition.LT, UtilAggregationHeaders.SC_AUDIO_QUAL_TOTAL, UtilAggregationHeaders.SC_AUDIO_QUAL_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_SC4_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioQuality(call);
            for(float value : values){
                if(value<SecondLevelConstants.MIN_QUALITY_SC4_AVG){
                    return ColoredFlags.YELLOW;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    SC4_ZW2_MIN("SC4 ZW2(MIN)", Messages.R_SC4_ZW2_MIN_TITLE,null,Units.NONE,  StatisticsType.MIN, null, null, UtilAggregationHeaders.SC_AUDIO_QUAL_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    SC4_ZW2_MAX("SC4 ZW2(MAX)", Messages.R_SC4_ZW2_MAX_TITLE,null,Units.NONE,  StatisticsType.MAX, null, null, UtilAggregationHeaders.SC_AUDIO_QUAL_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    SC5_ZW1_AVG("SC5 ZW1(AVG)", Messages.R_SC5_ZW1_AVG_TITLE,Messages.R_SC5_ZW1_AVG_THRESHOLD_TITLE,Units.SECOND, StatisticsType.AVERAGE, SecondLevelConstants.MAX_SC5_AVERAGE, Condition.GT, UtilAggregationHeaders.SC_DELAY_TOTAL, UtilAggregationHeaders.SC_DELAY_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_SC5_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioDelay(call);
            for(float value : values){
                if(value>=SecondLevelConstants.MAX_DELAY_SC5){
                    return ColoredFlags.RED;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    SC5_ZW1_MIN("SC5 ZW1(MIN)", Messages.R_SC5_ZW1_MIN_TITLE,null,Units.NONE,  StatisticsType.MIN, null, null, UtilAggregationHeaders.SC_DELAY_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    SC5_ZW1_MAX("SC5 ZW1(MAX)", Messages.R_SC5_ZW1_MAX_TITLE,null, Units.NONE, StatisticsType.MAX, null, null, UtilAggregationHeaders.SC_DELAY_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    GC1("GC1", Messages.R_GC1_TITLE,Messages.R_GC1_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_GC1, Condition.LT, UtilAggregationHeaders.GC_SUCC_SETUP_COUNT, UtilAggregationHeaders.GC_ATTEMPT_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_GC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_GC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    GC2_ZW2_AVG("GC2 ZW2(AVG)", Messages.R_GC2_ZW2_AVG_TITLE,Messages.R_GC2_ZW2_AVG_THRESHOLD_TITLE,Units.SECOND, StatisticsType.AVERAGE, SecondLevelConstants.MAX_GC2_AVERAGE, Condition.GT, UtilAggregationHeaders.GC_SETUP_TIME_TOTAL, UtilAggregationHeaders.GC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_GC2_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_GC2_AVG){
                return ColoredFlags.YELLOW;
            }
            return ColoredFlags.NONE;
        }
    },
    GC2_ZW2_MIN("GC2 ZW2(MIN)", Messages.R_GC2_ZW2_MIN_TITLE,null, Units.NONE, StatisticsType.MIN, null, null, UtilAggregationHeaders.GC_SETUP_TIME_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    GC2_ZW2_MAX("GC2 ZW2(MAX)", Messages.R_GC2_ZW2_MAX_TITLE,Messages.R_GC2_ZW2_MAX_THRESHOLD_TITLE, Units.SECOND, StatisticsType.MAX, SecondLevelConstants.MAX_GC2_MAX, Condition.GT, UtilAggregationHeaders.GC_SETUP_TIME_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_GC2_MAX){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_GC2_MAX){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    GC3("GC3", Messages.R_GC3_TITLE,Messages.R_GC3_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_GC3, Condition.LT, UtilAggregationHeaders.GC_CALL_DISC_TIME, UtilAggregationHeaders.GC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_GC3){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallDurationTime(call);
            if(time!=null&&time<SecondLevelConstants.MIN_DURATION_GC3){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    GC4("GC4", Messages.R_GC4_TITLE,Messages.R_GC4_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_GC4, Condition.LT, UtilAggregationHeaders.GC_AUDIO_QUAL_SUCC, UtilAggregationHeaders.GC_SUCC_SETUP_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_GC4){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioQuality(call);
            for(float value : values){
                if(value<SecondLevelConstants.MIN_QUALITY_GC4){
                    return ColoredFlags.RED;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    GC4_ZW2_AVG("GC4 ZW2(AVG)", Messages.R_GC4_ZW2_AVG_TITLE,Messages.R_GC4_ZW2_AVG_THRESHOLD_TITLE,Units.NONE, StatisticsType.AVERAGE, SecondLevelConstants.MIN_GC4_AVERAGE, Condition.LT, UtilAggregationHeaders.GC_AUDIO_QUAL_TOTAL, UtilAggregationHeaders.GC_AUDIO_QUAL_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_GC4_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioQuality(call);
            for(float value : values){
                if(value<SecondLevelConstants.MIN_QUALITY_GC4_AVG){
                    return ColoredFlags.YELLOW;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    GC4_ZW2_MIN("GC4 ZW2(MIN)", Messages.R_GC4_ZW2_MIN_TITLE,null, null, StatisticsType.MIN, null, null, UtilAggregationHeaders.GC_AUDIO_QUAL_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    GC4_ZW2_MAX("GC4 ZW2(MAX)", Messages.R_GC4_ZW2_MIN_TITLE,null, null, StatisticsType.MAX, null, null, UtilAggregationHeaders.GC_AUDIO_QUAL_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    GC5_ZW1_AVG("GC5 ZW1(AVG)", Messages.R_GC5_ZW1_AVG_TITLE,Messages.R_GC5_ZW1_AVG_THRESHOLD_TITLE,Units.SECOND, StatisticsType.AVERAGE, SecondLevelConstants.MAX_GC5_AVERAGE, Condition.GT, UtilAggregationHeaders.GC_DELAY_TOTAL, UtilAggregationHeaders.GC_DELAY_COUNT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()>SecondLevelConstants.MAX_GC5_AVERAGE){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            float[] values = CallStatisticsUtills.getCallAudioDelay(call);
            for(float value : values){
                if(value>=SecondLevelConstants.MAX_DELAY_GC5){
                    return ColoredFlags.RED;
                }
            }
            return ColoredFlags.NONE;
        }
    },
    GC5_ZW1_MIN("GC5 ZW1(MIN)", Messages.R_GC5_ZW1_MIN_TITLE,null, Units.NONE, StatisticsType.MIN, null, null, UtilAggregationHeaders.GC_DELAY_MIN) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    GC5_ZW1_MAX("GC5 ZW1(MAX)", Messages.R_GC5_ZW1_MAX_TITLE,null, Units.NONE, StatisticsType.MAX, null, null, UtilAggregationHeaders.GC_DELAY_MAX) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    INH_HO_CC("INH HO/CC", Messages.R_INH_HO_CC_TITLE,Messages.R_INH_HO_CC_THRESHOLD_TITLE,Units.PERCENT,StatisticsType.PERCENT,SecondLevelConstants.MIN_INH_HO_CC, Condition.LT, UtilAggregationHeaders.INH_HO_CC_SUCCESS, UtilAggregationHeaders.INH_HO_CC_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float hTime = CallStatisticsUtills.getCallHandoverTime(call);
            Float rTime = CallStatisticsUtills.getCallReselectionTime(call);
            Float time = hTime==null?rTime:hTime;
            if(time!=null&&time>=SecondLevelConstants.MAX_DURATION_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    INH_HO("INH HO", Messages.R_INH_HO_TITLE,Messages.R_INH_HO_THRESHOLD_TITLE,Units.PERCENT,StatisticsType.PERCENT,SecondLevelConstants.MIN_INH_HO_CC, Condition.LT, UtilAggregationHeaders.INH_HO_SUCCESS, UtilAggregationHeaders.INH_HO_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallHandoverTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_DURATION_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    INH_CC("INH CC", Messages.R_INH_CC_TITLE,Messages.R_INH_CC_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_INH_HO_CC, Condition.LT, UtilAggregationHeaders.INH_CC_SUCCESS, UtilAggregationHeaders.INH_CC_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallReselectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_DURATION_INH_HO_CC){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    TSM("TSM", Messages.R_TSM_TITLE,Messages.R_TSM_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_TSM, Condition.LT, UtilAggregationHeaders.TSM_SUCCESS, UtilAggregationHeaders.TSM_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_TSM){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getMessageReceiveTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_RECEIVE_TSM){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    SDS("SDS", Messages.R_SDS_TITLE,Messages.R_SDS_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_SDS, Condition.LT, UtilAggregationHeaders.SDS_SUCCESS, UtilAggregationHeaders.SDS_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_SDS){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getMessageReceiveTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_RECEIVE_SDS){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    INH_AT("INH AT", Messages.R_INH_AT_TITLE,Messages.R_INH_AT_THRESHOLD_TITLE,Units.PERCENT, StatisticsType.PERCENT, SecondLevelConstants.MIN_INH_ATT, Condition.LT, UtilAggregationHeaders.INH_ATT_SUCCESS, UtilAggregationHeaders.INH_ATT_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_INH_ATT){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallDurationTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_DURATION_INH_ATT){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    },
    EC1("EC1",Messages.R_EC1_TITLE, Messages.R_EC1_THRESHOLD_TITLE,Units.PERCENT,StatisticsType.PERCENT,SecondLevelConstants.MIN_EC1, Condition.LT, UtilAggregationHeaders.EC1_SUCCESS, UtilAggregationHeaders.EC1_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            if(value!=null&&value.floatValue()<SecondLevelConstants.MIN_EC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
        @Override
        public ColoredFlags getFlagByCall(Node call) {
            Float time = CallStatisticsUtills.getCallConnectionTime(call);
            if(time!=null&&time>=SecondLevelConstants.MAX_SETUP_EC1){
                return ColoredFlags.RED;
            }
            return ColoredFlags.NONE;
        }
    }/*, TODO uncomment after support statistics
    EC2("EC2", Messages.R_EC2_TITLE,StatisticsType.PERCENT,null,UtilAggregationHeaders.EC2_SUCCESS,UtilAggregationHeaders.EC2_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    IP("IP", Messages.R_IP_TITLE,StatisticsType.PERCENT,null,UtilAggregationHeaders.IP_SUCCESS,UtilAggregationHeaders.IP_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    },
    CSD("CSD", Messages.R_CSD_TITLE,StatisticsType.PERCENT,null,UtilAggregationHeaders.CSD_SUCCESS,UtilAggregationHeaders.CSD_ATTEMPT) {
        @Override
        public ColoredFlags getFlagByStatValue(Number value) {
            return ColoredFlags.NONE;
        }

        @Override
        public ColoredFlags getFlagByCall(Node call) {
            return ColoredFlags.NONE;
        }
    }*/;
    
    private String title;
    private String thresholdTitle;
    private String chartTitle;//it's also the report chart title
    private Float threshold;
    private Units unit;
    private Condition condition;
    private StatisticsType type;
    private List<IStatisticsHeader> dependent;
    
    private AggregationStatisticsHeaders(String title, String chartTitle, String thresholdTitle, Units unit, StatisticsType type, Float threshold, Condition condition, IStatisticsHeader... dependendHeaders) {
        this.title = title;
        this.chartTitle = chartTitle;
        this.thresholdTitle = thresholdTitle;
        this.threshold = threshold;
        this.unit = unit;
        this.type = type;
        this.condition = condition;
        dependent = Arrays.asList(dependendHeaders);
    }

    @Override
    public List<IStatisticsHeader> getDependendHeaders() {
        return dependent;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    public String getChartTitle() {
        return chartTitle;
    }

    /**
     * @return Returns the thresholdTitle.
     */
    public String getThresholdTitle() {
        return thresholdTitle;
    }

    /**
     * @return Returns the threshold.
     */
    public Float getThreshold() {
        return threshold;
    }

    /**
     * @return Returns the condition.
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @return Returns the unit.
     */
    public Units getUnit() {
        return unit;
    }

    @Override
    public StatisticsType getType() {
        return type;
    }

    @Override
    public Number getStatisticsData(Node dataNode, IStatisticsConstants constants, boolean inclInconclusive) {
        return null;
    }
    
    /**
     * Check value for flag.
     *
     * @param value Number.
     * @return StatisticsFlags
     */
    public abstract ColoredFlags getFlagByStatValue(Number value);
    
    /**
     * Check value for flag.
     *
     * @param call Node.
     * @return StatisticsFlags
     */
    public abstract ColoredFlags getFlagByCall(Node call);
}
