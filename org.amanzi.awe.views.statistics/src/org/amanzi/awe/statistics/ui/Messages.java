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

package org.amanzi.awe.statistics.ui;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for views.
 * <p>
 * </p>
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    /*
     * labels
     */
    public static String statisticsViewLabel_DATASET;
    public static String statisticsViewLabel_BUILD;
    public static String statisticsViewLabel_AGGREGATION;
    public static String statisticsViewLabel_TEMPLATE;
    public static String statisticsViewLabel_START_TIME;
    public static String statisticsViewLabel_PERIOD;
    public static String statisticsViewLabel_RESET_BUTTON;
    public static String statisticsViewLabel_END_TIME;
    public static String statisticsViewLabel_REPORT;
    public static String statisticsViewLabel_EXPORT;
    public static String statisticsViewLabel_CHART_VIEW;
    public static String PATH_TO_REFRESH_BUTTON_IMG;

    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
