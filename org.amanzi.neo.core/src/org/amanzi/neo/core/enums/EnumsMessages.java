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

package org.amanzi.neo.core.enums;

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
public class EnumsMessages extends NLS {

    private static final String BUNDLE_NAME = EnumsMessages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    
    public static String NetworkFileType_RADIO_SITE;
    public static String NetworkFileType_RADIO_SECTOR;
    public static String NetworkFileType_TRANSMISSION;
    public static String NetworkFileType_NEIGHBOUR;

    public static String NetworkFileType_NOKIA;
    public static String NetworkFileType_PROBE;

    public static String NetworkFileType_UTRAN;
    
    private EnumsMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, EnumsMessages.class);
    }
    
}
