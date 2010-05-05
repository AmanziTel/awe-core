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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.views.calls.statistics.constants.AlarmConstants;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.IStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.ItsiAttachConstants;
import org.amanzi.awe.views.calls.statistics.constants.MessageConstants;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.CallProperties.CallResult;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Enumeration of all statistics headers.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum StatisticsHeaders {
    
    //Utility headers for get second level statistics (should be not in any call type)
    SC_SUCC_SETUP_COUNT("SC_SUCC_SETUP_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_ATTEMPT_COUNT("SC_ATTEMPT_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_MAX("SC_SETUP_TIME_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_MIN("SC_SETUP_TIME_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_TOTAL("SC_SETUP_TIME_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_CALL_DISC_TIME("SC_CALL_DISC_TIME",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_COUNT("SC_AUDIO_QUAL_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_SUCC("SC_AUDIO_QUAL_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_MAX("SC_AUDIO_QUAL_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_MIN("SC_AUDIO_QUAL_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_TOTAL("SC_AUDIO_QUAL_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_COUNT("SC_DELAY_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_MAX("SC_DELAY_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_MIN("SC_DELAY_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_TOTAL("SC_DELAY_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SUCC_SETUP_COUNT("GC_SUCC_SETUP_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_ATTEMPT_COUNT("GC_ATTEMPT_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_MAX("GC_SETUP_TIME_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_MIN("GC_SETUP_TIME_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_TOTAL("GC_SETUP_TIME_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_CALL_DISC_TIME("GC_CALL_DISC_TIME",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_COUNT("GC_AUDIO_QUAL_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_SUCC("GC_AUDIO_QUAL_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_MAX("GC_AUDIO_QUAL_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_MIN("GC_AUDIO_QUAL_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_TOTAL("GC_AUDIO_QUAL_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_COUNT("GC_DELAY_COUNT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_MAX("GC_DELAY_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_MIN("GC_DELAY_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_TOTAL("GC_DELAY_TOTAL",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_CC_SUCCESS("INH_CC_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_CC_ATTEMPT("INH_CC_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    TSM_SUCCESS("TSM_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    TSM_ATTEMPT("TSM_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SDS_SUCCESS("SDS_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SDS_ATTEMPT("SDS_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_ATT_SUCCESS("INH_ATT_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_ATT_ATTEMPT("INH_ATT_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    //Utility headers for get second level statistics (should not be in any call type) - end.
    
    SC1("SC1", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC2_ZW2_AVG("SC2 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC2_ZW2_MIN("SC2 ZW2(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC2_ZW2_MAX("SC2 ZW2(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC3("SC3 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC4("SC4", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC4_ZW2_AVG("SC4 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC4_ZW2_MIN("SC4 ZW2(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC4_ZW2_MAX("SC4 ZW2(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC5_ZW1_AVG("SC5 ZW1(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC5_ZW1_MIN("SC5 ZW1(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SC5_ZW1_MAX("SC5 ZW1(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC1("GC1", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC2_ZW2_AVG("GC2 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC2_ZW2_MIN("GC2 ZW2(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC2_ZW2_MAX("GC2 ZW2(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC3("GC3 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC4("GC4", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC4_ZW2_AVG("GC4 ZW2(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC4_ZW2_MIN("GC4 ZW2(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC4_ZW2_MAX("GC4 ZW2(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC5_ZW1_AVG("GC5 ZW1(AVG)", StatisticsType.AVERAGE) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC5_ZW1_MIN("GC5 ZW1(MIN)", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    GC5_ZW1_MAX("GC5 ZW1(MAX)", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    INH_CC("INH CC", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    TSM("TSM", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    SDS("SDS", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    INH_AT("INH AT", StatisticsType.PERCENT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CALL_ATTEMPT_COUNT("CALL_ATTEMPT_COUNT", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }        
    },
    SUCC_SETUP_COUNT("SUCC_SETUP_COUNT", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P1("SETUP_TM_Z1_P1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP1(), callConstants.getCallConnTimeP2(),true,false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P2("SETUP_TM_Z1_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP2(), callConstants.getCallConnTimeP3(),true,false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P3("SETUP_TM_Z1_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP3(), callConstants.getCallConnTimeP4(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P4("SETUP_TM_Z1_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP4(), callConstants.getCallConnTimeL1(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L1("SETUP_TM_Z1_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL1(), callConstants.getCallConnTimeL2(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L2("SETUP_TM_Z1_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL2(), callConstants.getCallConnTimeL3(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L3("SETUP_TM_Z1_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL3(), callConstants.getCallConnTimeL4(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L4("SETUP_TM_Z1_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL4(), callConstants.getCallConnTimeLimit(), true, true)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TIME_MIN("SETUP_TIME_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                return getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_TIME_MAX("SETUP_TIME_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                return getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_TOTAL_DUR("SETUP_TOTAL_DUR", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                return getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P1("SETUP_DUR_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP1(), callConstants.getCallConnTimeP2(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P2("SETUP_DUR_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP2(), callConstants.getCallConnTimeP3(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P3("SETUP_DUR_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP3(), callConstants.getCallConnTimeP4(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P4("SETUP_DUR_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeP4(), callConstants.getCallConnTimeL1(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L1("SETUP_DUR_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL1(), callConstants.getCallConnTimeL2(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L2("SETUP_DUR_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL2(), callConstants.getCallConnTimeL3(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L3("SETUP_DUR_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL3(), callConstants.getCallConnTimeL4(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L4("SETUP_DUR_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(isValueInBorders(connectionTime, callConstants.getCallConnTimeL4(), callConstants.getCallConnTimeLimit(),true,true)){
                return connectionTime;
            }
            return null;
        }
    },
    CALL_DISC_TIME("CALL_DISC_TIME",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                float durationTime = getCallDurationTime(callNode);
                if(durationTime>=((ICallStatisticsConstants)constants).getIndivCallDurationTime()){
                    return 1;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_SUCC("AUDIO_QUAL_SUCC", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                float[] audioQuality = getCallAudioQuality(callNode);
                if (audioQuality.length>0) {
                    float result = audioQuality[0];
                    for (float quality : audioQuality) {
                        if (result > quality) {
                            result = quality;
                        }
                    }
                    if (isValueInBorders(result, callConstants.getIndivCallQualLimit(), callConstants.getIndivCallQualMax(), true,
                            true)) {
                        return 1;
                    }
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P1("AUDIO_QUAL_P1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP1(), callConstants.getIndivCallQualMax(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P2("AUDIO_QUAL_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP2(), callConstants.getIndivCallQualP1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P3("AUDIO_QUAL_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP3(), callConstants.getIndivCallQualP2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P4("AUDIO_QUAL_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP4(), callConstants.getIndivCallQualP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L1("AUDIO_QUAL_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL1(), callConstants.getIndivCallQualP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L2("AUDIO_QUAL_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL2(), callConstants.getIndivCallQualL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L3("AUDIO_QUAL_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL3(), callConstants.getIndivCallQualL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L4("AUDIO_QUAL_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualL3(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_MIN("AUDIO_QUAL_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
                if(!good.isEmpty()){
                    Collections.sort(good);
                    return good.get(0);
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_MAX("AUDIO_QUAL_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
                if(!good.isEmpty()){
                    Collections.sort(good);
                    return good.get(good.size()-1);
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_TOTAL("AUDIO_QUAL_TOTAL", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_P1("AUDIO_QUAL_Z1_P1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP1(), callConstants.getIndivCallQualMax(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_P2("AUDIO_QUAL_Z1_P2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP2(), callConstants.getIndivCallQualP1(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_P3("AUDIO_QUAL_Z1_P3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP3(), callConstants.getIndivCallQualP2(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_P4("AUDIO_QUAL_Z1_P4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualP4(), callConstants.getIndivCallQualP3(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_L1("AUDIO_QUAL_Z1_L1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL1(), callConstants.getIndivCallQualP4(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_L2("AUDIO_QUAL_Z1_L2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL2(), callConstants.getIndivCallQualL1(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_L3("AUDIO_QUAL_Z1_L3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualL3(), callConstants.getIndivCallQualL2(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_Z1_L4("AUDIO_QUAL_Z1_L4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualL3(), true, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float quality : good){
                        result+=quality;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P1("DELAY_COUNT_P1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P2("DELAY_COUNT_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P3("DELAY_COUNT_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P4("DELAY_COUNT_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L1("DELAY_COUNT_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L2("DELAY_COUNT_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L3("DELAY_COUNT_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L4("DELAY_COUNT_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelays = getCallAudioDelay(callNode);
                int result = 0; 
                for(float callAudioDelay : callAudioDelays){
                    if(callAudioDelay>((ICallStatisticsConstants)constants).getIndivCallDelayL4()){
                        result++;
                    }
                }
                if(result>0){
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_MIN("DELAY_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    Collections.sort(good);
                    return good.get(0);
                }
            }
            return null;
        }
    },
    IND_DELAY_MAX("DELAY_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    return good.get(good.size()-1);
                }
            }
            return null;
        }
    },
    IND_DELAY_TOTAL("DELAY_TOTAL", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_P1("DELAY_Z1_P1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_P2("DELAY_Z1_P2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_P3("DELAY_Z1_P3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_P4("DELAY_Z1_P4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_L1("DELAY_Z1_L1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_L2("DELAY_Z1_L2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_L3("DELAY_Z1_L3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_Z1_L4("DELAY_Z1_L4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallDuratiomGood(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelays = getCallAudioDelay(callNode);
                float result = 0; 
                for(float callAudioDelay : callAudioDelays){
                    if(callAudioDelay>((ICallStatisticsConstants)constants).getIndivCallDelayL4()){
                        result+=callAudioDelay;
                    }
                }
                if(result>0){
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P1("DELAY_COUNT_P1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P2("DELAY_COUNT_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P3("DELAY_COUNT_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P4("DELAY_COUNT_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L1("DELAY_COUNT_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L2("DELAY_COUNT_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L3("DELAY_COUNT_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L4("DELAY_COUNT_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelays = getCallAudioDelay(callNode);
                int result = 0; 
                for(float callAudioDelay : callAudioDelays){
                    if(callAudioDelay>((ICallStatisticsConstants)constants).getIndivCallDelayL4()){
                        result++;
                    }
                }
                if(result>0){
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_MIN("DELAY_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    Collections.sort(good);
                    return good.get(0);
                }
            }
            return null;
        }
    },
    GR_DELAY_MAX("DELAY_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    Collections.sort(good);
                    return good.get(good.size()-1);
                }
            }
            return null;
        }
    },
    GR_DELAY_TOTAL("DELAY_TOTAL", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelay = getCallAudioDelay(callNode);
                List<Float> good = Arrays.asList(callAudioDelay);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_P1("DELAY_Z1_P1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_P2("DELAY_Z1_P2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_P3("DELAY_Z1_P3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_P4("DELAY_Z1_P4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_L1("DELAY_Z1_L1", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_L2("DELAY_Z1_L2", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_L3("DELAY_Z1_L3", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    float result = 0;
                    for(float delay : good){
                        result+=delay;
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_Z1_L4("DELAY_Z1_L4", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants)){
                Float[] callAudioDelays = getCallAudioDelay(callNode);
                float result = 0; 
                for(float callAudioDelay : callAudioDelays){
                    if(callAudioDelay>((ICallStatisticsConstants)constants).getIndivCallDelayL4()){
                        result+=callAudioDelay;
                    }
                }
                if(result>0){
                    return result;
                }
            }
            return null;
        }
    },
    TSM_MESSAGE_ATTEMPT("MESSAGE_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    SDS_MESSAGE_ATTEMPT("MESSAGE_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    TSM_MESSAGE_SUCC("MESSAGE_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float receiveTime = getMessageReceiveTime(callNode);
            float acknowledgeTime = getMessageAcknowledgeTime(callNode);
            if(receiveTime<=MessageConstants.TSM_SEND_TIME_LIMIT
                    && acknowledgeTime<=MessageConstants.TSM_REPLY_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    SDS_MESSAGE_SUCC("MESSAGE_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float receiveTime = getMessageReceiveTime(callNode);
            if(receiveTime<=MessageConstants.SDS_SEND_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ALM_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    ALM_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM("DELAY_TOTAL_SUM",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_MIN("DELAY_TOTAL_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_MAX("DELAY_TOTAL_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_SUM("DELAY_FIRST_SUM",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_MIN("DELAY_FIRST_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_MAX("DELAY_FIRST_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(isCallSuccess(callNode)){
                return getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P1("DELAY_Z1_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P1, AlarmConstants.ALM_DELAY_Z1_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P2("DELAY_Z1_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P2, AlarmConstants.ALM_DELAY_Z1_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P3("DELAY_Z1_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P3, AlarmConstants.ALM_DELAY_Z1_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P4("DELAY_Z1_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P4, AlarmConstants.ALM_DELAY_Z1_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L1("DELAY_Z1_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L1, AlarmConstants.ALM_DELAY_Z1_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L2("DELAY_Z1_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L2, AlarmConstants.ALM_DELAY_Z1_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L3("DELAY_Z1_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L3, AlarmConstants.ALM_DELAY_Z1_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L4("DELAY_Z1_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z1_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P1("DELAY_Z2_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P1, AlarmConstants.ALM_DELAY_Z2_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P2("DELAY_Z2_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P2, AlarmConstants.ALM_DELAY_Z2_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P3("DELAY_Z2_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P3, AlarmConstants.ALM_DELAY_Z2_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P4("DELAY_Z2_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P4, AlarmConstants.ALM_DELAY_Z2_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L1("DELAY_Z2_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L1, AlarmConstants.ALM_DELAY_Z2_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L2("DELAY_Z2_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L2, AlarmConstants.ALM_DELAY_Z2_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L3("DELAY_Z2_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L3, AlarmConstants.ALM_DELAY_Z2_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L4("DELAY_Z2_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z2_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P1("DELAY_Z3_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P1, AlarmConstants.ALM_DELAY_Z3_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P2("DELAY_Z3_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P2, AlarmConstants.ALM_DELAY_Z3_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P3("DELAY_Z3_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P3, AlarmConstants.ALM_DELAY_Z3_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P4("DELAY_Z3_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P4, AlarmConstants.ALM_DELAY_Z3_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L1("DELAY_Z3_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L1, AlarmConstants.ALM_DELAY_Z3_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L2("DELAY_Z3_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L2, AlarmConstants.ALM_DELAY_Z3_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L3("DELAY_Z3_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L3, AlarmConstants.ALM_DELAY_Z3_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L4("DELAY_Z3_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z3_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P1("DELAY_Z4_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P1, AlarmConstants.ALM_DELAY_Z4_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P2("DELAY_Z4_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P2, AlarmConstants.ALM_DELAY_Z4_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P3("DELAY_Z4_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P3, AlarmConstants.ALM_DELAY_Z4_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P4("DELAY_Z4_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P4, AlarmConstants.ALM_DELAY_Z4_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L1("DELAY_Z4_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L1, AlarmConstants.ALM_DELAY_Z4_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L2("DELAY_Z4_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L2, AlarmConstants.ALM_DELAY_Z4_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L3("DELAY_Z4_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L3, AlarmConstants.ALM_DELAY_Z4_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L4("DELAY_Z4_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getFirstMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z4_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P1("DELAY_TOTAL_SUM_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P1, AlarmConstants.ALM_DELAY_Z3_P2, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P2("DELAY_TOTAL_SUM_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P2, AlarmConstants.ALM_DELAY_Z3_P3, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P3("DELAY_TOTAL_SUM_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P3, AlarmConstants.ALM_DELAY_Z3_P4, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P4("DELAY_TOTAL_SUM_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P4, AlarmConstants.ALM_DELAY_Z3_L1, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L1("DELAY_TOTAL_SUM_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L1, AlarmConstants.ALM_DELAY_Z3_L2, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L2("DELAY_TOTAL_SUM_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L2, AlarmConstants.ALM_DELAY_Z3_L3, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L3("DELAY_TOTAL_SUM_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L3, AlarmConstants.ALM_DELAY_Z3_L4, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L4("DELAY_TOTAL_SUM_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float delayTime = getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z3_L4){
                return delayTime;
            }
            return null;
        }
    },
    EC1_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    EC2_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    EC1_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            if(connectionTime<=AlarmConstants.EMG_CALL_CONN_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    EC2_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            if(!isCallSuccess(callNode)){
                return null;
            }
            float connectionTime = getCallConnectionTime(callNode);
            if(connectionTime<=AlarmConstants.HELP_CALL_CONN_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ATT_ATTEMPTS("ATTEMPTS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return 1;
        }
    },
    ATT_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float connectionTime = getCallDurationTime(callNode);
            if(connectionTime<=ItsiAttachConstants.TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P1("DELAY_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P1_LOW, ItsiAttachConstants.DELAY_P2_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P2("DELAY_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P2_LOW, ItsiAttachConstants.DELAY_P3_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P3("DELAY_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P3_LOW, ItsiAttachConstants.DELAY_P4_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P4("DELAY_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P4_LOW, ItsiAttachConstants.DELAY_L1_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L1("DELAY_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L1_LOW, ItsiAttachConstants.DELAY_L2_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L2("DELAY_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L2_LOW, ItsiAttachConstants.DELAY_L3_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L3("DELAY_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float updateTime = getCallDurationTime(callNode);
            if(isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L3_LOW, ItsiAttachConstants.DELAY_L4_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L4("DELAY_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            float connectionTime = getCallDurationTime(callNode);
            if(connectionTime>ItsiAttachConstants.DELAY_L4_LOW){
                return 1;
            }
            return null;
        }
    },
    CC_HO_ATTEMPTS("HO_ATTEMPTS",StatisticsType.COUNT) { //TODO next headers.
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_ATTEMPTS("RES_ATTEMPTS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_SUCCESS("HO_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_SUCCESS("RES_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_P1("HO_TIME_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_P2("HO_TIME_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_P3("HO_TIME_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_P4("DELAY_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_L1("HO_TIME_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_L2("HO_TIME_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_L3("HO_TIME_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_HO_TIME_L4("HO_TIME_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_P1("RES_TIME_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_P2("RES_TIME_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_P3("RES_TIME_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_P4("RES_TIME_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_L1("RES_TIME_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_L2("RES_TIME_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_L3("RES_TIME_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    },
    CC_RES_TIME_L4("RES_TIME_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants) {
            return null;
        }
    };
    
    protected static final float MAX_DURATION_FOR_DELAY = 10.0f;
    
    private String headerTitle;
    private StatisticsType headerType;
    
    private StatisticsHeaders(String title, StatisticsType type) {
        headerTitle = title;
        headerType = type;
    }
    
    /**
     * @return Returns the headerName.
     */
    public String getTitle() {
        return headerTitle;
    }
    
    /**
     * @return Returns the headerType.
     */
    public StatisticsType getType() {
        return headerType;
    }
    
    /**
     * Get statistics data from call by header.
     * Returns null if call does not good for this header.
     *
     * @param callNode Node (call)
     * @return Number
     */
    public abstract Number getStatisticsData(Node callNode, IStatisticsConstants constants);
    
    /**
     * Check call for success.
     *
     * @param callNode Node (call)
     * @return boolean
     */
    protected boolean isCallSuccess(Node callNode){
        CallResult callResult = CallResult.valueOf((String)callNode.getProperty(CallProperties.CALL_RESULT.getId()));
        return callResult.equals(CallResult.SUCCESS);
    }
    
    /**
     * Check call for success and time limit.
     *
     * @param callNode  Node (call)
     * @param constants IStatisticsConstants
     * @return boolean
     */
    protected boolean isCallInTimeLimit(Node callNode, ICallStatisticsConstants constants){
        if(!isCallSuccess(callNode)){
            return false;
        }
        float connectionTime = getCallConnectionTime(callNode);
        return connectionTime<=constants.getCallConnTimeLimit();
    }
    
    /**
     * Check call for success, time limit and duration time for delay.
     *
     * @param callNode  Node (call)
     * @param constants IStatisticsConstants
     * @return boolean
     */
    protected boolean isCallDuratiomGood(Node callNode, ICallStatisticsConstants constants){
        if(!isCallInTimeLimit(callNode, constants)){
            return false;
        }
        float callDuration = getCallDurationTime(callNode)-getCallConnectionTime(callNode)-getCallTerminationTime(callNode);
        return callDuration < MAX_DURATION_FOR_DELAY;
    }
    
    /**
     * Returns call connection time.
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getCallConnectionTime(Node callNode){
        long connectionTime = (Long)callNode.getProperty(CallProperties.SETUP_DURATION.getId());
        return (float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call duration time.
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getCallDurationTime(Node callNode){
        long connectionTime = (Long)callNode.getProperty(CallProperties.CALL_DURATION.getId()); 
        return ((float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR);
    }
    
    /**
     * Returns call termination time.
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getCallTerminationTime(Node callNode){
        long connectionTime = (Long)callNode.getProperty(CallProperties.TERMINATION_DURATION.getId());
        return (float)connectionTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns call audio sample qualities.
     *
     * @param callNode Node (call)
     * @return Float[]
     */
    protected float[] getCallAudioQuality(Node callNode){
        return (float[])callNode.getProperty(CallProperties.LQ.getId()); 
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
    protected List<Float> getAllGoodQualities(Node callNode, Float start, Float end, boolean inclStart, boolean inclEnd){
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
     * Returns call audio sample delay.
     *
     * @param callNode Node (call)
     * @return Float[]
     */
    protected Float[] getCallAudioDelay(Node callNode){
        Float[] delays = (Float[])callNode.getProperty(CallProperties.DELAY.getId());
        //System.out.println("Call "+callNode.getProperty("name")+", delays: "+Arrays.toString(delays)+".");
        return delays;//TODO ?
    }
    
    /**
     * Returns call audio sample delay.
     *
     * @param callNode Node (call)
     * @return Float[]
     */
    protected float getCallAudioDelayOne(Node callNode){
        Float[] delays = (Float[])callNode.getProperty(CallProperties.DELAY.getId());
        float result = 0;
        for(float delay : delays){
            result+=delay;
        }
        return result;//TODO ?
    }
    
    /**
     * Returns message received time.
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getMessageReceiveTime(Node callNode){
        long receiveTime = (Long)callNode.getProperty(CallProperties.MESS_RECEIVE_TIME.getId());
        return (float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns message acknowledge time.
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getMessageAcknowledgeTime(Node callNode){
        long receiveTime = (Long)callNode.getProperty(CallProperties.MESS_ACKNOWLEDGE_TIME.getId());
        return (float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns message delay time (alarm).
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getMessageDelayTime(Node callNode){
        long receiveTime = (Long)callNode.getProperty(CallProperties.ALM_MESSAGE_DELAY.getId());
        return (float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
    }
    
    /**
     * Returns first message delay time (alarm).
     *
     * @param callNode Node (call)
     * @return float
     */
    protected float getFirstMessageDelayTime(Node callNode){
        long receiveTime = (Long)callNode.getProperty(CallProperties.ALM_FIRST_MESS_DELAY.getId());
        return (float)receiveTime / ICallStatisticsConstants.MILLISECONDS_FACTOR;
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
    protected List<Float> getAllGoodDelays(Node callNode, Float start, Float end, boolean inclStart, boolean inclEnd){
        Float[] callAudioDelays = getCallAudioDelay(callNode);
        List<Float> good = new ArrayList<Float>(callAudioDelays.length);
        for(float callAudioDelay : callAudioDelays){
            if(isValueInBorders(callAudioDelay, start, end, inclStart, inclEnd)){
                good.add(callAudioDelay);
            }
        }
        return good;
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
    protected boolean isValueInBorders(Float value, Float start, Float end, boolean inclStart, boolean inclEnd){
        return ((inclStart&&start.equals(value))||start<value) && (value<end||(inclEnd&&end.equals(value)));
    }
}
