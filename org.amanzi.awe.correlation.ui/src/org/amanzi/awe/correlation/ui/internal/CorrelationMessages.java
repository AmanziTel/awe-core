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
package org.amanzi.awe.correlation.ui.internal;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for Network Tree
 */

public class CorrelationMessages extends NLS {

    private static final String BUNDLE_NAME = CorrelationMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String NETWORK_NAME_LABEL;

    public static String DRIVE_NAME_LABEL;

    public static String CORRELATION_BUTTON;

    public static String NETWORK_COLUMN_LABEL;

    public static String MEASUREMENT_COLUMN_LABEL;

    public static String PROXIES_COLUMN_LABEL;

    public static String SECTOR_COUNT_COLUMN_LABEL;

    public static String CORRELATED_M_COUNT_COLUMN_LABEL;

    public static String TOTAL_M_COUNT_COLUMN_LABEL;

    public static String START_TIME_COLUMN_LABEL;

    public static String END_TIME_COLUMN_LABEL;

    public static String DELETE_COLUMN_LABEL;

    public static String REMOVE_CORRELATION_DIALOG_TITLE;

    public static String REMOVE_CORRELATION_DIALOG_MESSAGE;

    static {
        NLS.initializeMessages(BUNDLE_NAME, CorrelationMessages.class);
    }

    public static String getFormattedString(final String key, final String... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private CorrelationMessages() {
    }

}
