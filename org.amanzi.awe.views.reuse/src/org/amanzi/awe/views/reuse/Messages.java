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

package org.amanzi.awe.views.reuse;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for views.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String MessageAndEventTable_label_DATA_TYPE;
    public static String MessageAndEventTable_label_DATASET;
    public static String MessageAndEventTable_label_PROPERTY;
    public static String MessageAndEventTable_label_EXPRESSION;
    
    public static String MessageAndEventTable_menu_COMMIT;
    public static String MessageAndEventTable_menu_ROLLBACK;
    public static String MessageAndEventTable_menu_CONFIGURE;
    public static String MessageAndEventTable_menu_CLEAR;
    
    public static String TableConfigWizard_title;
    public static String TableConfigWizard_description;
    public static String TableConfigWizard_label_visible;
    public static String TableConfigWizard_label_invisible;
    
    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
