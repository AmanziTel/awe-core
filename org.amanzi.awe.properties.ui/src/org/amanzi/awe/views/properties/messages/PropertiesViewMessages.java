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
package org.amanzi.awe.views.properties.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for Network Tree
 */

public class PropertiesViewMessages extends NLS {

    private static final String BUNDLE_NAME = PropertiesViewMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String INCORRECT_PROPERTY_TYPE;

    public static String INCORRECT_PROPERTY_TYPE_TEXT;

    public static String CANT_EDIT_PROPERTY_MESSAGE;

    public static String CANT_EDIT_PROPERTY_TITLE;

    private PropertiesViewMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getFormattedString(String key, String... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, PropertiesViewMessages.class);
    }

}
