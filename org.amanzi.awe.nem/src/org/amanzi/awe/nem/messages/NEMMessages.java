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

package org.amanzi.awe.nem.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NEMMessages extends NLS {

    private static final String BUNDLE_NAME = NEMMessages.class.getName();

    public static String EXPORT_GENERAL_SETTINGS_PAGE;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String EXPORT_NETWORK_DATA_ITEM;

    public static String EXPORT_SELECTION_DATA_ITEM;

    public static String EXPORT_NETWORK_DATA_ITEM_FILE_NAME_FORMAT;

    public static String EXPORT_SELECTION_DATA_ITEM_FILE_NAME_FORMAT;

    static {
        NLS.initializeMessages(BUNDLE_NAME, NEMMessages.class);
    }

    public static String getFormattedString(final String key, final String... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private NEMMessages() {
    }

}
