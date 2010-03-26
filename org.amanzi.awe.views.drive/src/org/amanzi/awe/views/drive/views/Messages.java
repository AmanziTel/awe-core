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

package org.amanzi.awe.views.drive.views;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Messages extends NLS {
    
    private static final String BUNDLE_NAME = Messages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String DriveInquirerView_10;

    public static String DriveInquirerView_100;

    public static String DriveInquirerView_101;

    public static String DriveInquirerView_102;

    public static String DriveInquirerView_11;

    public static String DriveInquirerView_12;

    public static String DriveInquirerView_13;

    public static String DriveInquirerView_14;

    public static String DriveInquirerView_15;

    public static String DriveInquirerView_16;

    public static String DriveInquirerView_18;

    public static String DriveInquirerView_2;

    public static String DriveInquirerView_20;

    public static String DriveInquirerView_3;

    public static String DriveInquirerView_36;

    public static String DriveInquirerView_4;

    public static String DriveInquirerView_5;

    public static String DriveInquirerView_6;

    public static String DriveInquirerView_60;

    public static String DriveInquirerView_7;

    public static String DriveInquirerView_8;

    public static String DriveInquirerView_9;

    public static String DriveInquirerView_97;

    public static String DriveInquirerView_99;

    public static String DriveInquirerView_label_drive;
    public static String DriveInquirerView_label_event;
    public static String DriveInquirerView_label_property;
    public static String DriveInquirerView_label_start_time;
    public static String DriveInquirerView_label_length;
    public static String DriveInquirerView_menu_add_property;
    
    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
