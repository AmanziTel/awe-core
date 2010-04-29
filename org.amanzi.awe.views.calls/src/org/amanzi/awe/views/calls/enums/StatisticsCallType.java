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

import org.amanzi.neo.core.enums.CallProperties.CallType;


/**
 * <p>
 * Enumeration of call types for get statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum StatisticsCallType {
    /**
     * Individual calls.
     */
    INDIVIDUAL(CallType.INDIVIDUAL, StatisticsHeaders.CALL_ATTEMPT_COUNT,
                            StatisticsHeaders.SUCC_SETUP_COUNT,
                            StatisticsHeaders.SETUP_TM_Z1_P1,
                            StatisticsHeaders.SETUP_TM_Z1_P2,
                            StatisticsHeaders.SETUP_TM_Z1_P3,
                            StatisticsHeaders.SETUP_TM_Z1_P4,
                            StatisticsHeaders.SETUP_TM_Z1_L1,
                            StatisticsHeaders.SETUP_TM_Z1_L2,
                            StatisticsHeaders.SETUP_TM_Z1_L3,
                            StatisticsHeaders.SETUP_TM_Z1_L4,
                            StatisticsHeaders.SETUP_TIME_MIN,
                            StatisticsHeaders.SETUP_TIME_MAX,
                            StatisticsHeaders.SETUP_TOTAL_DUR,
                            StatisticsHeaders.SETUP_DUR_Z1_P1,
                            StatisticsHeaders.SETUP_DUR_Z1_P2,
                            StatisticsHeaders.SETUP_DUR_Z1_P3,
                            StatisticsHeaders.SETUP_DUR_Z1_P4,
                            StatisticsHeaders.SETUP_DUR_Z1_L1,
                            StatisticsHeaders.SETUP_DUR_Z1_L2,
                            StatisticsHeaders.SETUP_DUR_Z1_L3,
                            StatisticsHeaders.SETUP_DUR_Z1_L4,
                            StatisticsHeaders.CALL_DISC_TIME,
                            StatisticsHeaders.AUDIO_QUAL_SUCC,
                            StatisticsHeaders.AUDIO_QUAL_P1,
                            StatisticsHeaders.AUDIO_QUAL_P2,
                            StatisticsHeaders.AUDIO_QUAL_P3,
                            StatisticsHeaders.AUDIO_QUAL_P4,
                            StatisticsHeaders.AUDIO_QUAL_L1,
                            StatisticsHeaders.AUDIO_QUAL_L2,
                            StatisticsHeaders.AUDIO_QUAL_L3,
                            StatisticsHeaders.AUDIO_QUAL_L4,
                            StatisticsHeaders.AUDIO_QUAL_MIN,
                            StatisticsHeaders.AUDIO_QUAL_MAX,
                            StatisticsHeaders.AUDIO_QUAL_TOTAL,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P4,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L4/*,
                            StatisticsHeaders.IND_DELAY_COUNT_P1,
                            StatisticsHeaders.IND_DELAY_COUNT_P2,
                            StatisticsHeaders.IND_DELAY_COUNT_P3,
                            StatisticsHeaders.IND_DELAY_COUNT_P4,
                            StatisticsHeaders.IND_DELAY_COUNT_L1,
                            StatisticsHeaders.IND_DELAY_COUNT_L2,
                            StatisticsHeaders.IND_DELAY_COUNT_L3,
                            StatisticsHeaders.IND_DELAY_COUNT_L4,
                            StatisticsHeaders.IND_DELAY_MIN,
                            StatisticsHeaders.IND_DELAY_MAX,
                            StatisticsHeaders.IND_DELAY_TOTAL,
                            StatisticsHeaders.IND_DELAY_Z1_P1,
                            StatisticsHeaders.IND_DELAY_Z1_P2,
                            StatisticsHeaders.IND_DELAY_Z1_P3,
                            StatisticsHeaders.IND_DELAY_Z1_P4,
                            StatisticsHeaders.IND_DELAY_Z1_L1,
                            StatisticsHeaders.IND_DELAY_Z1_L2,
                            StatisticsHeaders.IND_DELAY_Z1_L3,
                            StatisticsHeaders.IND_DELAY_Z1_L4*/),
    
    /**
     * Group calls.
     */
    GROUP(CallType.GROUP, StatisticsHeaders.CALL_ATTEMPT_COUNT,
                            StatisticsHeaders.SUCC_SETUP_COUNT,
                            StatisticsHeaders.SETUP_TM_Z1_P1,
                            StatisticsHeaders.SETUP_TM_Z1_P2,
                            StatisticsHeaders.SETUP_TM_Z1_P3,
                            StatisticsHeaders.SETUP_TM_Z1_P4,
                            StatisticsHeaders.SETUP_TM_Z1_L1,
                            StatisticsHeaders.SETUP_TM_Z1_L2,
                            StatisticsHeaders.SETUP_TM_Z1_L3,
                            StatisticsHeaders.SETUP_TM_Z1_L4,
                            StatisticsHeaders.SETUP_TIME_MIN,
                            StatisticsHeaders.SETUP_TIME_MAX,
                            StatisticsHeaders.SETUP_TOTAL_DUR,
                            StatisticsHeaders.SETUP_DUR_Z1_P1,
                            StatisticsHeaders.SETUP_DUR_Z1_P2,
                            StatisticsHeaders.SETUP_DUR_Z1_P3,
                            StatisticsHeaders.SETUP_DUR_Z1_P4,
                            StatisticsHeaders.SETUP_DUR_Z1_L1,
                            StatisticsHeaders.SETUP_DUR_Z1_L2,
                            StatisticsHeaders.SETUP_DUR_Z1_L3,
                            StatisticsHeaders.SETUP_DUR_Z1_L4,
                            StatisticsHeaders.CALL_DISC_TIME,
                            StatisticsHeaders.AUDIO_QUAL_SUCC,
                            StatisticsHeaders.AUDIO_QUAL_P1,
                            StatisticsHeaders.AUDIO_QUAL_P2,
                            StatisticsHeaders.AUDIO_QUAL_P3,
                            StatisticsHeaders.AUDIO_QUAL_P4,
                            StatisticsHeaders.AUDIO_QUAL_L1,
                            StatisticsHeaders.AUDIO_QUAL_L2,
                            StatisticsHeaders.AUDIO_QUAL_L3,
                            StatisticsHeaders.AUDIO_QUAL_L4,
                            StatisticsHeaders.AUDIO_QUAL_MIN,
                            StatisticsHeaders.AUDIO_QUAL_MAX,
                            StatisticsHeaders.AUDIO_QUAL_TOTAL,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P4,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L4/*,
                            StatisticsHeaders.GR_DELAY_COUNT_P1,
                            StatisticsHeaders.GR_DELAY_COUNT_P2,
                            StatisticsHeaders.GR_DELAY_COUNT_P3,
                            StatisticsHeaders.GR_DELAY_COUNT_P4,
                            StatisticsHeaders.GR_DELAY_COUNT_L1,
                            StatisticsHeaders.GR_DELAY_COUNT_L2,
                            StatisticsHeaders.GR_DELAY_COUNT_L3,
                            StatisticsHeaders.GR_DELAY_COUNT_L4,
                            StatisticsHeaders.GR_DELAY_MIN,
                            StatisticsHeaders.GR_DELAY_MAX,
                            StatisticsHeaders.GR_DELAY_TOTAL,
                            StatisticsHeaders.GR_DELAY_Z1_P1,
                            StatisticsHeaders.GR_DELAY_Z1_P2,
                            StatisticsHeaders.GR_DELAY_Z1_P3,
                            StatisticsHeaders.GR_DELAY_Z1_P4,
                            StatisticsHeaders.GR_DELAY_Z1_L1,
                            StatisticsHeaders.GR_DELAY_Z1_L2,
                            StatisticsHeaders.GR_DELAY_Z1_L3,
                            StatisticsHeaders.GR_DELAY_Z1_L4*/),
    /**
     * SDS messages.
     */
    SDS(CallType.SDS,StatisticsHeaders.SDS_MESSAGE_ATTEMPT,StatisticsHeaders.SDS_MESSAGE_SUCC),
    /**
     * TSM messages.
     */
    TSM(CallType.TSM,StatisticsHeaders.TSM_MESSAGE_ATTEMPT,StatisticsHeaders.TSM_MESSAGE_SUCC),
    /**
     * Alarm messages.
     */
    ALARM(CallType.ALARM,StatisticsHeaders.ALM_ATTEMPT,
                         StatisticsHeaders.ALM_SUCCESS,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM,
                         StatisticsHeaders.ALM_DELAY_TOTAL_MIN,
                         StatisticsHeaders.ALM_DELAY_TOTAL_MAX,
                         StatisticsHeaders.ALM_DELAY_FIRST_SUM,
                         StatisticsHeaders.ALM_DELAY_FIRST_MIN,
                         StatisticsHeaders.ALM_DELAY_FIRST_MAX,
                         StatisticsHeaders.ALM_DELAY_Z1_P1,
                         StatisticsHeaders.ALM_DELAY_Z1_P2,
                         StatisticsHeaders.ALM_DELAY_Z1_P3,
                         StatisticsHeaders.ALM_DELAY_Z1_P4,
                         StatisticsHeaders.ALM_DELAY_Z1_L1,
                         StatisticsHeaders.ALM_DELAY_Z1_L2,
                         StatisticsHeaders.ALM_DELAY_Z1_L3,
                         StatisticsHeaders.ALM_DELAY_Z1_L4,
                         StatisticsHeaders.ALM_DELAY_Z2_P1,
                         StatisticsHeaders.ALM_DELAY_Z2_P2,
                         StatisticsHeaders.ALM_DELAY_Z2_P3,
                         StatisticsHeaders.ALM_DELAY_Z2_P4,
                         StatisticsHeaders.ALM_DELAY_Z2_L1,
                         StatisticsHeaders.ALM_DELAY_Z2_L2,
                         StatisticsHeaders.ALM_DELAY_Z2_L3,
                         StatisticsHeaders.ALM_DELAY_Z2_L4,
                         StatisticsHeaders.ALM_DELAY_Z3_P1,
                         StatisticsHeaders.ALM_DELAY_Z3_P2,
                         StatisticsHeaders.ALM_DELAY_Z3_P3,
                         StatisticsHeaders.ALM_DELAY_Z3_P4,
                         StatisticsHeaders.ALM_DELAY_Z3_L1,
                         StatisticsHeaders.ALM_DELAY_Z3_L2,
                         StatisticsHeaders.ALM_DELAY_Z3_L3,
                         StatisticsHeaders.ALM_DELAY_Z3_L4,
                         StatisticsHeaders.ALM_DELAY_Z4_P1,
                         StatisticsHeaders.ALM_DELAY_Z4_P2,
                         StatisticsHeaders.ALM_DELAY_Z4_P3,
                         StatisticsHeaders.ALM_DELAY_Z4_P4,
                         StatisticsHeaders.ALM_DELAY_Z4_L1,
                         StatisticsHeaders.ALM_DELAY_Z4_L2,
                         StatisticsHeaders.ALM_DELAY_Z4_L3,
                         StatisticsHeaders.ALM_DELAY_Z4_L4,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P1,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P2,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P3,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P4,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L1,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L2,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L3,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L4),
    /**
     * Emergency call type 1.
     */
    EMERGENCY(CallType.EMERGENCY, StatisticsHeaders.EC1_ATTEMPT,StatisticsHeaders.EC1_SUCCESS),
    /**
     * Emergency call type 2.
     */
    HELP(CallType.HELP, StatisticsHeaders.EC2_ATTEMPT,StatisticsHeaders.EC2_SUCCESS);
    
    private CallType id;
    private List<StatisticsHeaders> headers;
    
    /**
     * Constructor.
     * @param anId CallType
     * @param statHeaders headers
     */
    private StatisticsCallType(CallType anId, StatisticsHeaders... statHeaders ) {
        id = anId;
        headers = Arrays.asList(statHeaders);
    }
    
    /**
     * @return Returns the id.
     */
    public CallType getId() {
        return id;
    }
    
    /**
     * @return Returns the headers.
     */
    public List<StatisticsHeaders> getHeaders() {
        return headers;
    }
    
    public StatisticsHeaders getHeaderByTitle(String title){
        for(StatisticsHeaders header : headers){
            if(header.getTitle().equals(title)){
                return header;
            }
        }
        return null;
    }
    
    /**
     * Returns call type.
     *
     * @param id String
     * @return StatisticsCallTypes
     */
    public static StatisticsCallType getTypeById(String id){
        for(StatisticsCallType type : values()){
            if(type.id.toString().equals(id)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown call type <"+id+">");
    }
    
    /**
     * Returns call type.
     *
     * @param id CallType
     * @return StatisticsCallTypes
     */
    public static StatisticsCallType getTypeById(CallType id){
        for(StatisticsCallType type : values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown call type <"+id+">");
    }
}
