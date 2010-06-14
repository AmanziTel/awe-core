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

package org.amanzi.awe.views.calls;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * <p>
 * Messages for call plug-in.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {
    
    private static final String BUNDLE_NAME = Messages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String CAV_ERROR_VALUE;
    public static String CAV_ALL_VALUE;
    public static String CAV_LBL_DRIVE;
    public static String CAV_LBL_PROBE;
    public static String CAV_LBL_START_TIME;
    public static String CAV_LBL_END_TIME;
    public static String CAV_LBL_PERIOD;
    public static String CAV_LBL_CALL_TYPE;
    public static String CAV_COL_PERIOD;
    public static String CAV_COL_HOST;
    public static String CAV_LB_EXPORT;
    public static String CAV_LB_INCONCLUSIVE;
    public static String CAV_LB_REPORT;
    
    public static String AIW_PAGE_TITLE;
    public static String AIW_PAGE_DESCR;
    public static String AIW_DATASET;
    public static String AIW_NETWORK;
    public static String AIW_DIRECTORY;
    public static String AIW_DIR_EDITOR_TITLE;
    /*Report keys*/
    public static String R_SC1_TITLE;
    public static String R_SC2_ZW2_AVG_TITLE;
    public static String R_SC2_ZW2_MIN_TITLE;
    public static String R_SC2_ZW2_MAX_TITLE;
    public static String R_SC3_TITLE;
    public static String R_SC4_TITLE;
    public static String R_SC4_ZW2_MIN_TITLE;
    public static String R_SC4_ZW2_AVG_TITLE;
    public static String R_SC4_ZW2_MAX_TITLE;
    public static String R_SC5_ZW1_AVG_TITLE;
    public static String R_SC5_ZW1_MIN_TITLE;
    public static String R_SC5_ZW1_MAX_TITLE;
    public static String R_GC2_ZW2_AVG_TITLE;
    public static String R_GC1_TITLE;
    public static String R_GC2_ZW2_MIN_TITLE;
    public static String R_GC2_ZW2_MAX_TITLE;
    public static String R_GC3_TITLE;
    public static String R_GC4_TITLE;
    public static String R_GC4_ZW2_AVG_TITLE;
    public static String R_GC4_ZW2_MIN_TITLE;
    public static String R_GC5_ZW1_AVG_TITLE;
    public static String R_GC5_ZW1_MIN_TITLE;
    public static String R_GC5_ZW1_MAX_TITLE;
    public static String R_INH_CC_TITLE;
    public static String R_TSM_TITLE;
    public static String R_SDS_TITLE;
    public static String R_INH_AT_TITLE;
    public static String R_EC1_TITLE;
    public static String R_EC2_TITLE;
    public static String R_IP_TITLE;
    public static String R_CSD_TITLE;
    public static String R_INH_HO_CC_TITLE;
    public static String R_INH_HO_TITLE;
    public static String R_THRESHOLD;
    public static String R_SC1_THRESHOLD_TITLE;

    public static String R_SC2_ZW2_AVG_THRESHOLD_TITLE;

    public static String R_SC2_ZW2_MAX_THRESHOLD_TITLE;

    public static String R_SC3_THRESHOLD_TITLE;

    public static String R_SC4_THRESHOLD_TITLE;

    public static String R_SC4_ZW2_AVG_THRESHOLD_TITLE;

    public static String R_SC5_ZW1_AVG_THRESHOLD_TITLE;

    public static String R_GC1_THRESHOLD_TITLE;

    public static String R_GC2_ZW2_AVG_THRESHOLD_TITLE;

    public static String R_GC2_ZW2_MAX_THRESHOLD_TITLE;

    public static String R_GC3_THRESHOLD_TITLE;

    public static String R_GC4_THRESHOLD_TITLE;

    public static String R_GC4_ZW2_AVG_THRESHOLD_TITLE;

    public static String R_GC5_ZW1_AVG_THRESHOLD_TITLE;

    public static String R_INH_HO_CC_THRESHOLD_TITLE;

    public static String R_INH_HO_THRESHOLD_TITLE;

    public static String R_INH_CC_THRESHOLD_TITLE;

    public static String R_TSM_THRESHOLD_TITLE;

    public static String R_SDS_THRESHOLD_TITLE;

    public static String R_INH_AT_THRESHOLD_TITLE;

    public static String R_EC1_THRESHOLD_TITLE;

    public static String PERCENT;

    public static String SECOND;

    
    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    public static String getFormattedString(String key, Object ... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
