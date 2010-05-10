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
    
    public static String AIW_PAGE_TITLE;
    public static String AIW_PAGE_DESCR;
    public static String AIW_DATASET;
    public static String AIW_NETWORK;
    public static String AIW_DIRECTORY;
    public static String AIW_DIR_EDITOR_TITLE;
    
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
