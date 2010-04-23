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
    public static class MatrixProperties{
        public static final String DISTANCE = "distance";
        public static final String DEFINED_NBR = "defined_nbr";
        public static final String NUM_REPORTS_FOR_BEST_CELL = "# of MR for best cell";
        public static final String EC_NO_DELTA_PREFIX = "EcNo Delta";
        public static final String RSCP_DELTA_PREFIX = "RSCP Delta";
        public static final String POSITION_PREFIX = "Position";

        /**
         * hide constructor
         */
        private MatrixProperties() {
        }
    }
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
    public static final String REPORTS_ID = "report_id";
    public static final String RUBY_PROJECT_NAME = "gpeh_report";

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
        REPORTS,ICDM_INTRA_FR,ICDM_INTER_FR,ICDM_IRAT,BEST_CELL,SECOND_SELL,SOURCE_MATRIX_EVENT;
    }

    /**
     * Gets the table id.
     *
     * @param pscMain the psc main
     * @param psc2 the psc2
     * @return the table id
     */
    public static String getTableId(String pscMain, String psc2) {
        return pscMain+"@"+psc2;
    }

    /**
     * Gets the matrix lucene index name.
     *
     * @param networkName the network name
     * @param gpehEventsName the gpeh events name
     * @param type the type
     * @return the matrix lucene index name
     */
    public static String getMatrixLuceneIndexName(String networkName, String gpehEventsName, String type) {
        return getReportId(networkName, gpehEventsName).append("@").append(type).toString();
    }
    
    /**
     * Gets the report id.
     *
     * @param networkName the network name
     * @param gpehEventsName the gpeh events name
     * @return the report id
     */
    public static StringBuilder getReportId(String networkName, String gpehEventsName) {
        return new StringBuilder(networkName).append("@").append(gpehEventsName);
    }

}
