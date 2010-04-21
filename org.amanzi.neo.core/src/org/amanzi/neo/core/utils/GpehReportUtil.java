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

package org.amanzi.neo.core.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.math.DoubleRange;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * Utility methods for working with Gpeh Events
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportUtil {
    /**
     * Prefixes for Property Names of Measurement Report values
     */
    public static final String GPEH_RRC_MR_BSIC_PREFIX = "MR_BSIC";
    public static final String GPEH_RRC_MR_UE_TX_POWER_PREFIX = "MR_UE-TX-POWER";
    public static final String GPEH_RRC_MR_RSCP_PREFIX = "MR_RSCP";
    public static final String GPEH_RRC_MR_ECNO_PREFIX = "MR_ECNO";
    public static final String GPEH_RRC_SCRAMBLING_PREFIX = "MR_SCRAMBLING";
    private static final String ERR_VALUE = "Wrong value: %s";
    public static final String MR_TYPE = "mr_type";
    public static final String MR_TYPE_INTERF = "InterFreq";
    public static final String MR_TYPE_IRAT = "InterRAT";
    public static final String MR_TYPE_INTRAF = "IntraF";
    public static final String MR_TYPE_UE_INTERNAL = "Ue_Internal";
    public static final String RNC_ID = "rncId";
    public static final String PRIMARY_SCR_CODE = "primaryScramblingCode";

    /**
     * hide constructor
     */
    private GpehReportUtil() {
    }
    public static String getPropertyRangeName(String key, Object value) {
        if (true)
            return value == null ? "" : value.toString();
        if (isRSCPProperty(key)) {
            return getRSCPRangeName(value);
        } else if (isECNOProperty(key)) {
            return getECNORangeName(value);
        } else if (isTxPowerProperty(key)) {
            return getTxPowerRangeName(value);
        }
        return value == null ? null : value.toString();
    }
    
    public static boolean isReportProperties(String key) {
        return isTxPowerProperty(key) || isECNOProperty(key) || isRSCPProperty(key);
    }

    public static boolean isTxPowerProperty(String key) {
        return Pattern.matches(GPEH_RRC_MR_UE_TX_POWER_PREFIX + "\\d+", key);
    }

    public static boolean isECNOProperty(String key) {
        return Pattern.matches(GPEH_RRC_MR_ECNO_PREFIX + "\\d+", key);
    }

    public static boolean isRSCPProperty(String key) {
        return Pattern.matches(GPEH_RRC_MR_RSCP_PREFIX + "\\d+", key);
    }

    // TODO implement
    /**
     * @param value
     * @return
     */
    private static String getTxPowerRangeName(Object value) {
        if (value == null) {
            return "";
        } else if (!(value instanceof Integer)) {
            return String.format(ERR_VALUE, value);
        }
        DoubleRange range = getRangeOfTxPower((Integer)value);
        return null;
    }

    private static DoubleRange getRangeOfTxPower(Integer value) {
        return null;
    }

    /**
     * @param value
     * @return
     */
    private static String getECNORangeName(Object value) {
        return null;
    }

    /**
     * @param value
     * @return
     */
    private static String getRSCPRangeName(Object value) {
        return null;
    }
    public static enum ReportsRelations implements RelationshipType {
        REPORTS,ICDM_INTRA_FR,ICDM_INTER_FR,ICDM_IRAT;
    }
}
