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

import org.amanzi.awe.views.calls.statistics.constants.IStatisticsConstants;
import org.amanzi.neo.core.INeoConstants;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Utility headers for get second level statistics (should be not in any call type).
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum UtilAggregationHeaders implements IAggrStatisticsHeaders {
    
    SC_SUCC_SETUP_COUNT("SC_SUCC_SETUP_COUNT",StatisticsType.COUNT,StatisticsHeaders.SUCC_SETUP_COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_ATTEMPT_COUNT("SC_ATTEMPT_COUNT",StatisticsType.COUNT,StatisticsHeaders.CALL_ATTEMPT_COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_MAX("SC_SETUP_TIME_MAX",StatisticsType.MAX,StatisticsHeaders.SETUP_TIME_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_MIN("SC_SETUP_TIME_MIN",StatisticsType.MIN,StatisticsHeaders.SETUP_TIME_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_SETUP_TIME_TOTAL("SC_SETUP_TIME_TOTAL",StatisticsType.SUM,StatisticsHeaders.SETUP_TOTAL_DUR) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_CALL_DISC_TIME("SC_CALL_DISC_TIME",StatisticsType.COUNT,StatisticsHeaders.CALL_DISC_TIME) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_COUNT("SC_AUDIO_QUAL_COUNT",StatisticsType.COUNT, StatisticsHeaders.AUDIO_QUAL_P1,
                                                                    StatisticsHeaders.AUDIO_QUAL_P2,
                                                                    StatisticsHeaders.AUDIO_QUAL_P3,
                                                                    StatisticsHeaders.AUDIO_QUAL_P4,
                                                                    StatisticsHeaders.AUDIO_QUAL_L1,
                                                                    StatisticsHeaders.AUDIO_QUAL_L2,
                                                                    StatisticsHeaders.AUDIO_QUAL_L3,
                                                                    StatisticsHeaders.AUDIO_QUAL_L4) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_SUCC("SC_AUDIO_QUAL_SUCC",StatisticsType.COUNT,StatisticsHeaders.AUDIO_QUAL_SUCC) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_MAX("SC_AUDIO_QUAL_MAX",StatisticsType.MAX,StatisticsHeaders.AUDIO_QUAL_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_MIN("SC_AUDIO_QUAL_MIN",StatisticsType.MIN,StatisticsHeaders.AUDIO_QUAL_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_AUDIO_QUAL_TOTAL("SC_AUDIO_QUAL_TOTAL",StatisticsType.SUM,StatisticsHeaders.AUDIO_QUAL_TOTAL) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_COUNT("SC_DELAY_COUNT",StatisticsType.COUNT,StatisticsHeaders.IND_DELAY_COUNT_P1,
                                                        StatisticsHeaders.IND_DELAY_COUNT_P2,
                                                        StatisticsHeaders.IND_DELAY_COUNT_P3,
                                                        StatisticsHeaders.IND_DELAY_COUNT_P4,
                                                        StatisticsHeaders.IND_DELAY_COUNT_L1,
                                                        StatisticsHeaders.IND_DELAY_COUNT_L2,
                                                        StatisticsHeaders.IND_DELAY_COUNT_L3,
                                                        StatisticsHeaders.IND_DELAY_COUNT_L4) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_MAX("SC_DELAY_MAX",StatisticsType.MAX,StatisticsHeaders.IND_DELAY_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_MIN("SC_DELAY_MIN",StatisticsType.MIN,StatisticsHeaders.IND_DELAY_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SC_DELAY_TOTAL("SC_DELAY_TOTAL",StatisticsType.SUM,StatisticsHeaders.IND_DELAY_TOTAL) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SUCC_SETUP_COUNT("GC_SUCC_SETUP_COUNT",StatisticsType.COUNT,StatisticsHeaders.SUCC_SETUP_COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_ATTEMPT_COUNT("GC_ATTEMPT_COUNT",StatisticsType.COUNT,StatisticsHeaders.CALL_ATTEMPT_COUNT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_MAX("GC_SETUP_TIME_MAX",StatisticsType.MAX,StatisticsHeaders.SETUP_TIME_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_MIN("GC_SETUP_TIME_MIN",StatisticsType.MIN,StatisticsHeaders.SETUP_TIME_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_SETUP_TIME_TOTAL("GC_SETUP_TIME_TOTAL",StatisticsType.SUM,StatisticsHeaders.SETUP_TOTAL_DUR) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_CALL_DISC_TIME("GC_CALL_DISC_TIME",StatisticsType.COUNT,StatisticsHeaders.CALL_DISC_TIME) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_COUNT("GC_AUDIO_QUAL_COUNT",StatisticsType.COUNT, StatisticsHeaders.AUDIO_QUAL_P1,
                                                                    StatisticsHeaders.AUDIO_QUAL_P2,
                                                                    StatisticsHeaders.AUDIO_QUAL_P3,
                                                                    StatisticsHeaders.AUDIO_QUAL_P4,
                                                                    StatisticsHeaders.AUDIO_QUAL_L1,
                                                                    StatisticsHeaders.AUDIO_QUAL_L2,
                                                                    StatisticsHeaders.AUDIO_QUAL_L3,
                                                                    StatisticsHeaders.AUDIO_QUAL_L4) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_SUCC("GC_AUDIO_QUAL_SUCC",StatisticsType.COUNT,StatisticsHeaders.AUDIO_QUAL_SUCC) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_MAX("GC_AUDIO_QUAL_MAX",StatisticsType.MAX,StatisticsHeaders.AUDIO_QUAL_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_MIN("GC_AUDIO_QUAL_MIN",StatisticsType.MIN,StatisticsHeaders.AUDIO_QUAL_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_AUDIO_QUAL_TOTAL("GC_AUDIO_QUAL_TOTAL",StatisticsType.SUM,StatisticsHeaders.AUDIO_QUAL_TOTAL) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_COUNT("GC_DELAY_COUNT",StatisticsType.COUNT,StatisticsHeaders.GR_DELAY_COUNT_P1,
                                                        StatisticsHeaders.GR_DELAY_COUNT_P2,
                                                        StatisticsHeaders.GR_DELAY_COUNT_P3,
                                                        StatisticsHeaders.GR_DELAY_COUNT_P4,
                                                        StatisticsHeaders.GR_DELAY_COUNT_L1,
                                                        StatisticsHeaders.GR_DELAY_COUNT_L2,
                                                        StatisticsHeaders.GR_DELAY_COUNT_L3,
                                                        StatisticsHeaders.GR_DELAY_COUNT_L4) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_MAX("GC_DELAY_MAX",StatisticsType.MAX,StatisticsHeaders.GR_DELAY_MAX) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_MIN("GC_DELAY_MIN",StatisticsType.MIN,StatisticsHeaders.GR_DELAY_MIN) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    GC_DELAY_TOTAL("GC_DELAY_TOTAL",StatisticsType.SUM,StatisticsHeaders.GR_DELAY_TOTAL) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Float)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_CC_SUCCESS("INH_CC_SUCCESS",StatisticsType.COUNT,StatisticsHeaders.CC_HO_SUCCESS,
            StatisticsHeaders.CC_RES_SUCCESS) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_CC_ATTEMPT("INH_CC_ATTEMPT",StatisticsType.COUNT,StatisticsHeaders.CC_HO_ATTEMPTS,
            StatisticsHeaders.CC_RES_ATTEMPTS) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    TSM_SUCCESS("TSM_SUCCESS",StatisticsType.COUNT,StatisticsHeaders.TSM_MESSAGE_SUCC) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    TSM_ATTEMPT("TSM_ATTEMPT",StatisticsType.COUNT,StatisticsHeaders.TSM_MESSAGE_ATTEMPT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SDS_SUCCESS("SDS_SUCCESS",StatisticsType.COUNT,StatisticsHeaders.SDS_MESSAGE_SUCC) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    SDS_ATTEMPT("SDS_ATTEMPT",StatisticsType.COUNT,StatisticsHeaders.SDS_MESSAGE_ATTEMPT) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_ATT_SUCCESS("INH_ATT_SUCCESS",StatisticsType.COUNT,StatisticsHeaders.ATT_SUCCESS) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    },
    INH_ATT_ATTEMPT("INH_ATT_ATTEMPT",StatisticsType.COUNT,StatisticsHeaders.ATT_ATTEMPTS) {
        @Override
        public Number getStatisticsData(Node callNode, IStatisticsConstants constants, boolean inclInconclusive) {
            return (Integer)callNode.getProperty(INeoConstants.PROPERTY_VALUE_NAME,null);
        }
    };
    
    private String headerTitle;
    private StatisticsType headerType;
    private List<IStatisticsHeader> dependent;
    
    private UtilAggregationHeaders(String title, StatisticsType type, IStatisticsHeader... dependendHeaders) {
        headerTitle = title;
        headerType = type;
        dependent = Arrays.asList(dependendHeaders);
    }

    @Override
    public List<IStatisticsHeader> getDependendHeaders() {
        return dependent;
    }

    @Override
    public String getTitle() {
        return headerTitle;
    }

    @Override
    public StatisticsType getType() {
        return headerType;
    }

}
