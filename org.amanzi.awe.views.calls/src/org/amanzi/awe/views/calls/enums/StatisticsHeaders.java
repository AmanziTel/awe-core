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

import java.util.Collections;
import java.util.List;

import org.amanzi.awe.views.calls.statistics.CallStatisticsUtills;
import org.amanzi.awe.views.calls.statistics.constants.AlarmConstants;
import org.amanzi.awe.views.calls.statistics.constants.CcHoConstants;
import org.amanzi.awe.views.calls.statistics.constants.ICallStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.IStatisticsConstants;
import org.amanzi.awe.views.calls.statistics.constants.ItsiAttachConstants;
import org.amanzi.awe.views.calls.statistics.constants.MessageConstants;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Enumeration of all statistics headers.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum StatisticsHeaders implements IStatisticsHeader{
    
    CALL_ATTEMPT_COUNT("CALL_ATTEMPT_COUNT", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }        
    },
    SUCC_SETUP_COUNT("SUCC_SETUP_COUNT", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P1("SETUP_TM_Z1_P1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP1(), callConstants.getCallConnTimeP2(),true,false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P2("SETUP_TM_Z1_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP2(), callConstants.getCallConnTimeP3(),true,false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P3("SETUP_TM_Z1_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP3(), callConstants.getCallConnTimeP4(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_P4("SETUP_TM_Z1_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP4(), callConstants.getCallConnTimeL1(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L1("SETUP_TM_Z1_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL1(), callConstants.getCallConnTimeL2(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L2("SETUP_TM_Z1_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL2(), callConstants.getCallConnTimeL3(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L3("SETUP_TM_Z1_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL3(), callConstants.getCallConnTimeL4(), true, false)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TM_Z1_L4("SETUP_TM_Z1_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL4(), callConstants.getCallConnTimeLimit(), true, true)){
                return 1;
            }
            return null;
        }
    },
    SETUP_TIME_MIN("SETUP_TIME_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                return CallStatisticsUtills.getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_TIME_MAX("SETUP_TIME_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                return CallStatisticsUtills.getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_TOTAL_DUR("SETUP_TOTAL_DUR", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                return CallStatisticsUtills.getCallConnectionTime(callNode);
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P1("SETUP_DUR_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP1(), callConstants.getCallConnTimeP2(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P2("SETUP_DUR_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP2(), callConstants.getCallConnTimeP3(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P3("SETUP_DUR_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP3(), callConstants.getCallConnTimeP4(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_P4("SETUP_DUR_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeP4(), callConstants.getCallConnTimeL1(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L1("SETUP_DUR_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL1(), callConstants.getCallConnTimeL2(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L2("SETUP_DUR_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL2(), callConstants.getCallConnTimeL3(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L3("SETUP_DUR_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL3(), callConstants.getCallConnTimeL4(),true,false)){
                return connectionTime;
            }
            return null;
        }
    },
    SETUP_DUR_Z1_L4("SETUP_DUR_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
            if(CallStatisticsUtills.isValueInBorders(connectionTime, callConstants.getCallConnTimeL4(), callConstants.getCallConnTimeLimit(),true,true)){
                return connectionTime;
            }
            return null;
        }
    },
    CALL_DISC_TIME("CALL_DISC_TIME",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                Float durationTime = CallStatisticsUtills.getCallDurationTime(callNode);
                if(durationTime!=null&&durationTime>=((ICallStatisticsConstants)constants).getIndivCallDurationTime()){
                    return 1;
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_SUCC("AUDIO_QUAL_SUCC", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                float[] audioQuality = CallStatisticsUtills.getCallAudioQuality(callNode);
                if (audioQuality.length>0) {
                    float result = audioQuality[0];
                    for (float quality : audioQuality) {
                        if (result > quality) {
                            result = quality;
                        }
                    }
                    if (CallStatisticsUtills.isValueInBorders(result, callConstants.getIndivCallQualLimit(), callConstants.getIndivCallQualMax(), true,
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP1(), callConstants.getIndivCallQualMax(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P2("AUDIO_QUAL_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP2(), callConstants.getIndivCallQualP1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P3("AUDIO_QUAL_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP3(), callConstants.getIndivCallQualP2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_P4("AUDIO_QUAL_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP4(), callConstants.getIndivCallQualP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L1("AUDIO_QUAL_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL1(), callConstants.getIndivCallQualP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L2("AUDIO_QUAL_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL2(), callConstants.getIndivCallQualL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L3("AUDIO_QUAL_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL3(), callConstants.getIndivCallQualL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_L4("AUDIO_QUAL_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualL3(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    AUDIO_QUAL_MIN("AUDIO_QUAL_MIN", StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualMax(), true, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP1(), callConstants.getIndivCallQualMax(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP2(), callConstants.getIndivCallQualP1(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP3(), callConstants.getIndivCallQualP2(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualP4(), callConstants.getIndivCallQualP3(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL1(), callConstants.getIndivCallQualP4(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL2(), callConstants.getIndivCallQualL1(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualL3(), callConstants.getIndivCallQualL2(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodQualities(callNode, callConstants.getIndivCallQualMin(), callConstants.getIndivCallQualL3(), true, true);
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
    IND_DELAY_COUNT_P1("DELAY_COUNT_P1", StatisticsType.COUNT) {//TODO may be union delays for Group and Individual
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P2("DELAY_COUNT_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P3("DELAY_COUNT_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_P4("DELAY_COUNT_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L1("DELAY_COUNT_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L2("DELAY_COUNT_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L3("DELAY_COUNT_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    IND_DELAY_COUNT_L4("DELAY_COUNT_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelays = CallStatisticsUtills.getCallAudioDelay(callNode);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = callAudioDelay[0];
                    for(float delay : callAudioDelay){
                        if(result>delay){
                            result=delay;
                        }
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_MAX("DELAY_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = callAudioDelay[0];
                    for(float delay : callAudioDelay){
                        if(result<delay){
                            result=delay;
                        }
                    }
                    return result;
                }
            }
            return null;
        }
    },
    IND_DELAY_TOTAL("DELAY_TOTAL", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = 0;
                    for(float delay : callAudioDelay){
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelays = CallStatisticsUtills.getCallAudioDelay(callNode);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P2("DELAY_COUNT_P2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P3("DELAY_COUNT_P3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_P4("DELAY_COUNT_P4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L1("DELAY_COUNT_L1", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L2("DELAY_COUNT_L2", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L3("DELAY_COUNT_L3", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
                if(!good.isEmpty()){
                    return good.size();
                }
            }
            return null;
        }
    },
    GR_DELAY_COUNT_L4("DELAY_COUNT_L4", StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelays = CallStatisticsUtills.getCallAudioDelay(callNode);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = callAudioDelay[0];
                    for(float delay : callAudioDelay){
                        if(result>delay){
                            result=delay;
                        }
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_MAX("DELAY_MAX", StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = callAudioDelay[0];
                    for(float delay : callAudioDelay){
                        if(result<delay){
                            result=delay;
                        }
                    }
                    return result;
                }
            }
            return null;
        }
    },
    GR_DELAY_TOTAL("DELAY_TOTAL", StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelay = CallStatisticsUtills.getCallAudioDelay(callNode);
                if(callAudioDelay!=null&&callAudioDelay.length>0){
                    float result = 0;
                    for(float delay : callAudioDelay){
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP1(), callConstants.getIndivCallDelayP2(), true, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP2(), callConstants.getIndivCallDelayP3(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP3(), callConstants.getIndivCallDelayP4(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayP4(), callConstants.getIndivCallDelayL1(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL1(), callConstants.getIndivCallDelayL2(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL2(), callConstants.getIndivCallDelayL3(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                ICallStatisticsConstants callConstants = (ICallStatisticsConstants)constants;
                List<Float> good = CallStatisticsUtills.getAllGoodDelays(callNode, callConstants.getIndivCallDelayL3(), callConstants.getIndivCallDelayL4(), false, true);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallInTimeLimit(callNode, (ICallStatisticsConstants)constants,inclInconclusive)){
                float[] callAudioDelays = CallStatisticsUtills.getCallAudioDelay(callNode);
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
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    SDS_MESSAGE_ATTEMPT("MESSAGE_ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    TSM_MESSAGE_SUCC("MESSAGE_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float receiveTime = CallStatisticsUtills.getMessageReceiveTime(callNode);
            Float acknowledgeTime = CallStatisticsUtills.getMessageAcknowledgeTime(callNode);
            if(receiveTime!=null&&acknowledgeTime!=null
                    && receiveTime<=MessageConstants.TSM_SEND_TIME_LIMIT
                    && acknowledgeTime<=MessageConstants.TSM_REPLY_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    SDS_MESSAGE_SUCC("MESSAGE_SUCC",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float receiveTime = CallStatisticsUtills.getMessageReceiveTime(callNode);
            if(receiveTime!=null&&receiveTime<=MessageConstants.SDS_SEND_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ALM_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    ALM_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM("DELAY_TOTAL_SUM",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_MIN("DELAY_TOTAL_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_MAX("DELAY_TOTAL_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_SUM("DELAY_FIRST_SUM",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_MIN("DELAY_FIRST_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_FIRST_MAX("DELAY_FIRST_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P1("DELAY_Z1_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P1, AlarmConstants.ALM_DELAY_Z1_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P2("DELAY_Z1_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P2, AlarmConstants.ALM_DELAY_Z1_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P3("DELAY_Z1_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P3, AlarmConstants.ALM_DELAY_Z1_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_P4("DELAY_Z1_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_P4, AlarmConstants.ALM_DELAY_Z1_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L1("DELAY_Z1_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L1, AlarmConstants.ALM_DELAY_Z1_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L2("DELAY_Z1_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L2, AlarmConstants.ALM_DELAY_Z1_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L3("DELAY_Z1_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z1_L3, AlarmConstants.ALM_DELAY_Z1_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z1_L4("DELAY_Z1_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z1_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P1("DELAY_Z2_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P1, AlarmConstants.ALM_DELAY_Z2_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P2("DELAY_Z2_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P2, AlarmConstants.ALM_DELAY_Z2_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P3("DELAY_Z2_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P3, AlarmConstants.ALM_DELAY_Z2_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_P4("DELAY_Z2_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_P4, AlarmConstants.ALM_DELAY_Z2_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L1("DELAY_Z2_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L1, AlarmConstants.ALM_DELAY_Z2_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L2("DELAY_Z2_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L2, AlarmConstants.ALM_DELAY_Z2_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L3("DELAY_Z2_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z2_L3, AlarmConstants.ALM_DELAY_Z2_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z2_L4("DELAY_Z2_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(delayTime>=AlarmConstants.ALM_DELAY_Z2_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P1("DELAY_Z3_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P1, AlarmConstants.ALM_DELAY_Z3_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P2("DELAY_Z3_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P2, AlarmConstants.ALM_DELAY_Z3_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P3("DELAY_Z3_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P3, AlarmConstants.ALM_DELAY_Z3_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_P4("DELAY_Z3_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P4, AlarmConstants.ALM_DELAY_Z3_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L1("DELAY_Z3_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L1, AlarmConstants.ALM_DELAY_Z3_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L2("DELAY_Z3_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L2, AlarmConstants.ALM_DELAY_Z3_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L3("DELAY_Z3_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L3, AlarmConstants.ALM_DELAY_Z3_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z3_L4("DELAY_Z3_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(delayTime!=null&&delayTime>=AlarmConstants.ALM_DELAY_Z3_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P1("DELAY_Z4_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P1, AlarmConstants.ALM_DELAY_Z4_P2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P2("DELAY_Z4_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P2, AlarmConstants.ALM_DELAY_Z4_P3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P3("DELAY_Z4_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P3, AlarmConstants.ALM_DELAY_Z4_P4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_P4("DELAY_Z4_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_P4, AlarmConstants.ALM_DELAY_Z4_L1, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L1("DELAY_Z4_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L1, AlarmConstants.ALM_DELAY_Z4_L2, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L2("DELAY_Z4_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L2, AlarmConstants.ALM_DELAY_Z4_L3, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L3("DELAY_Z4_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z4_L3, AlarmConstants.ALM_DELAY_Z4_L4, true, false)){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_Z4_L4("DELAY_Z4_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getFirstMessageDelayTime(callNode);
            if(delayTime!=null&&delayTime>=AlarmConstants.ALM_DELAY_Z4_L4){
                return 1;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P1("DELAY_TOTAL_SUM_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P1, AlarmConstants.ALM_DELAY_Z3_P2, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P2("DELAY_TOTAL_SUM_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P2, AlarmConstants.ALM_DELAY_Z3_P3, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P3("DELAY_TOTAL_SUM_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P3, AlarmConstants.ALM_DELAY_Z3_P4, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_P4("DELAY_TOTAL_SUM_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_P4, AlarmConstants.ALM_DELAY_Z3_L1, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L1("DELAY_TOTAL_SUM_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L1, AlarmConstants.ALM_DELAY_Z3_L2, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L2("DELAY_TOTAL_SUM_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L2, AlarmConstants.ALM_DELAY_Z3_L3, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L3("DELAY_TOTAL_SUM_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(delayTime, AlarmConstants.ALM_DELAY_Z3_L3, AlarmConstants.ALM_DELAY_Z3_L4, true, false)){
                return delayTime;
            }
            return null;
        }
    },
    ALM_DELAY_TOTAL_SUM_L4("DELAY_TOTAL_SUM_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float delayTime = CallStatisticsUtills.getMessageDelayTime(callNode);
            if(delayTime!=null&&delayTime>=AlarmConstants.ALM_DELAY_Z3_L4){
                return delayTime;
            }
            return null;
        }
    },
    EC1_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    EC2_ATTEMPT("ATTEMPT",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    EC1_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            if(connectionTime!=null&&connectionTime<=AlarmConstants.EMG_CALL_CONN_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    EC2_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            if(!CallStatisticsUtills.isCallSuccess(callNode,inclInconclusive)){
                return null;
            }
            Float connectionTime = CallStatisticsUtills.getCallConnectionTime(callNode);
            if(connectionTime!=null&&connectionTime<=AlarmConstants.HELP_CALL_CONN_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ATT_ATTEMPTS("ATTEMPTS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    ATT_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(updateTime!=null&&updateTime<=ItsiAttachConstants.TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P1("DELAY_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P1_LOW, ItsiAttachConstants.DELAY_P2_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P2("DELAY_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P2_LOW, ItsiAttachConstants.DELAY_P3_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P3("DELAY_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P3_LOW, ItsiAttachConstants.DELAY_P4_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_P4("DELAY_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_P4_LOW, ItsiAttachConstants.DELAY_L1_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L1("DELAY_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L1_LOW, ItsiAttachConstants.DELAY_L2_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L2("DELAY_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L2_LOW, ItsiAttachConstants.DELAY_L3_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L3("DELAY_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L3_LOW, ItsiAttachConstants.DELAY_L4_LOW, true, false)){
                return 1;
            }
            return null;
        }
    },
    ATT_DELAY_L4("DELAY_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float updateTime = CallStatisticsUtills.getCallDurationTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(updateTime, ItsiAttachConstants.DELAY_L4_LOW, ItsiAttachConstants.TIME_LIMIT, true, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_ATTEMPTS("HO_ATTEMPTS",StatisticsType.COUNT) { 
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    CC_RES_ATTEMPTS("RES_ATTEMPTS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return 1;
        }
    },
    CC_HO_SUCCESS("HO_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(handoverTime!=null&&handoverTime<=CcHoConstants.HANDOVER_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    CC_RES_SUCCESS("RES_SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(reselectionTime!=null&&reselectionTime<=CcHoConstants.HANDOVER_TIME_LIMIT){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_P1("HO_TIME_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_P1_LOW, CcHoConstants.HANDOVER_DELAY_P2_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_P2("HO_TIME_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_P2_LOW, CcHoConstants.HANDOVER_DELAY_P3_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_P3("HO_TIME_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_P3_LOW, CcHoConstants.HANDOVER_DELAY_P4_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_P4("HO_TIME_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_P4_LOW, CcHoConstants.HANDOVER_DELAY_L1_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_L1("HO_TIME_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_L1_LOW, CcHoConstants.HANDOVER_DELAY_L2_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_L2("HO_TIME_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_L2_LOW, CcHoConstants.HANDOVER_DELAY_L3_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_L3("HO_TIME_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(handoverTime, CcHoConstants.HANDOVER_DELAY_L3_LOW, CcHoConstants.HANDOVER_DELAY_L4_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_HO_TIME_L4("HO_TIME_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float handoverTime = CallStatisticsUtills.getCallHandoverTime(callNode);
            if(handoverTime!=null&&handoverTime>CcHoConstants.HANDOVER_DELAY_L4_LOW){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_P1("RES_TIME_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_P1_LOW, CcHoConstants.HANDOVER_DELAY_P2_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_P2("RES_TIME_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_P2_LOW, CcHoConstants.HANDOVER_DELAY_P3_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_P3("RES_TIME_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_P3_LOW, CcHoConstants.HANDOVER_DELAY_P4_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_P4("RES_TIME_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_P4_LOW, CcHoConstants.HANDOVER_DELAY_L1_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_L1("RES_TIME_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_L1_LOW, CcHoConstants.HANDOVER_DELAY_L2_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_L2("RES_TIME_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_L2_LOW, CcHoConstants.HANDOVER_DELAY_L3_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_L3("RES_TIME_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(CallStatisticsUtills.isValueInBorders(reselectionTime, CcHoConstants.HANDOVER_DELAY_L3_LOW, CcHoConstants.HANDOVER_DELAY_L4_LOW, false, true)){
                return 1;
            }
            return null;
        }
    },
    CC_RES_TIME_L4("RES_TIME_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            Float reselectionTime = CallStatisticsUtills.getCallReselectionTime(callNode);
            if(reselectionTime!=null&&reselectionTime>CcHoConstants.HANDOVER_DELAY_L4_LOW){
                return 1;
            }
            return null;
        }
    },
    CSD_ATTEMPTS("ATTEMPTS",StatisticsType.COUNT) {//TODO next headers.
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_STABILITY("STABILITY",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_MIN("THROUGHPUT_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_MAX("THROUGHPUT_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_CONNECT_TOTAL_DUR("CONNECT_TOTAL_DUR",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_EXCH_SUCC("DATA_EXCH_SUCC",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_P1("THROUGHPUT_Z1_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_P2("THROUGHPUT_Z1_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_P3("THROUGHPUT_Z1_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_P4("THROUGHPUT_Z1_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_L1("THROUGHPUT_Z1_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_L2("THROUGHPUT_Z1_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_L3("THROUGHPUT_Z1_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z1_L4("THROUGHPUT_Z1_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_P1("THROUGHPUT_Z2_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_P2("THROUGHPUT_Z2_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_P3("THROUGHPUT_Z2_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_P4("THROUGHPUT_Z2_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_L1("THROUGHPUT_Z2_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_L2("THROUGHPUT_Z2_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_L3("THROUGHPUT_Z2_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_THROUGHPUT_Z2_L4("THROUGHPUT_Z2_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_P1("DATA_SUM_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_P2("DATA_SUM_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_P3("DATA_SUM_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_P4("DATA_SUM_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_L1("DATA_SUM_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_L2("DATA_SUM_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_L3("DATA_SUM_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_DATA_SUM_Z1_L4("DATA_SUM_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_P1("TIME_SUM_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_P2("TIME_SUM_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_P3("TIME_SUM_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_P4("TIME_SUM_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_L1("TIME_SUM_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_L2("TIME_SUM_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_L3("TIME_SUM_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    CSD_TIME_SUM_Z1_L4("TIME_SUM_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_ATTEMPTS("ATTEMPTS",StatisticsType.COUNT) { //TODO may be union with CSD 
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_SUCCESS("SUCCESS",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_STABILITY("STABILITY",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_MIN("THROUGHPUT_MIN",StatisticsType.MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_MAX("THROUGHPUT_MAX",StatisticsType.MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_CONNECT_TOTAL_DUR("CONNECT_TOTAL_DUR",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_EXCH_SUCC("DATA_EXCH_SUCC",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_P1("THROUGHPUT_Z1_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_P2("THROUGHPUT_Z1_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_P3("THROUGHPUT_Z1_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_P4("THROUGHPUT_Z1_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_L1("THROUGHPUT_Z1_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_L2("THROUGHPUT_Z1_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_L3("THROUGHPUT_Z1_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z1_L4("THROUGHPUT_Z1_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_P1("THROUGHPUT_Z2_P1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_P2("THROUGHPUT_Z2_P2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_P3("THROUGHPUT_Z2_P3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_P4("THROUGHPUT_Z2_P4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_L1("THROUGHPUT_Z2_L1",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_L2("THROUGHPUT_Z2_L2",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_L3("THROUGHPUT_Z2_L3",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_THROUGHPUT_Z2_L4("THROUGHPUT_Z2_L4",StatisticsType.COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_P1("DATA_SUM_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_P2("DATA_SUM_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_P3("DATA_SUM_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_P4("DATA_SUM_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_L1("DATA_SUM_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_L2("DATA_SUM_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_L3("DATA_SUM_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_DATA_SUM_Z1_L4("DATA_SUM_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_P1("TIME_SUM_Z1_P1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_P2("TIME_SUM_Z1_P2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_P3("TIME_SUM_Z1_P3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_P4("TIME_SUM_Z1_P4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_L1("TIME_SUM_Z1_L1",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_L2("TIME_SUM_Z1_L2",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_L3("TIME_SUM_Z1_L3",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    },
    IP_TIME_SUM_Z1_L4("TIME_SUM_Z1_L4",StatisticsType.SUM) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return null;
        }
    };
    
    
    
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
    
    
}
