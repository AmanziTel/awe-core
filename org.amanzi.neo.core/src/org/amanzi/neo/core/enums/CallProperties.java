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

package org.amanzi.neo.core.enums;

/**
 * <p>
 * Enum of all Calls properties
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum CallProperties {

    SETUP_DURATION("setup_duration"), 
    CALL_RESULT("call_result") {
        @Override
        public boolean needMappedCount() {
            return true;
        }
    },
    CALL_TYPE("call_type"),
    TERMINATION_DURATION("termination_duration"),
    CALL_DURATION("call_duration"),
    LQ("Listening quality"),
    DELAY("Audio delay"),
    MESS_RECEIVE_TIME("message received time"),
    MESS_ACKNOWLEDGE_TIME("message acknowledge time"),
    ALM_MESSAGE_DELAY("alarm delay"),
    ALM_FIRST_MESS_DELAY("first alarm delay"),
    CC_HANDOVER_TIME("handover time"),
    CC_RESELECTION_TIME("reselection time");

    private final String id;

    /**
     * constructor
     * 
     * @param id - property name
     */
    private CallProperties(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Need mapped count
     * 
     * @return
     */
    public boolean needMappedCount() {
        return false;
    }

    /**
     * is properties needs to analyse
     * 
     * @return coolean
     */
    public boolean isAnalysed() {
        return true;
    }

    /**
     * Gets enum by id
     * 
     * @param enumId - enum id
     * @return CallProperties or null
     */
    public static CallProperties getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (CallProperties call : CallProperties.values()) {
            if (call.getId().equals(enumId)) {
                return call;
            }
        }
        return null;
    }

    public enum CallResult {
        SUCCESS, FAILURE;
    }

    public enum CallType {
        INDIVIDUAL("has_individual_calls"), 
        GROUP("has_group_calls"), 
        SDS("has_sds"), 
        TSM("has_tsm"), 
        ALARM("has_alarm"), 
        EMERGENCY("has_emergency"), 
        HELP("has_help"),
        ITSI_ATTACH("has_attach"),
        ITSI_CC("has_itsi_cc"),
        ITSI_HO("has_itsi_ho"),
        CS_DATA("has_csd"),
        PS_DATA("has_psd");
        
        private String hasCallsProperty;
        
        private CallType(String hasCallsProperty) {
        	this.hasCallsProperty = hasCallsProperty;
        }
        
        public String getProperty() {
        	return hasCallsProperty;
        }
    }
    
    
}
