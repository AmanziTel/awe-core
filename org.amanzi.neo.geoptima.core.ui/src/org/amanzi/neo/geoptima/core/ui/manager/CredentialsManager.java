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

package org.amanzi.neo.geoptima.core.ui.manager;

import org.amanzi.neo.geoptima.core.ui.internal.GeoptimaCoreUIPlugin;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CredentialsManager {

    public static String FTP_HOST_KEY = "host";
    public static String FTP_USERNAME_KEY = "username";
    public static String FTP_PASSWORD_KEY = "password";

    public static String getFtpHost() {
        return GeoptimaCoreUIPlugin.getDefault().getPreferenceStore().getString(FTP_HOST_KEY);
    }

    public static String getFtpUserName() {
        return GeoptimaCoreUIPlugin.getDefault().getPreferenceStore().getString(FTP_USERNAME_KEY);
    }

    public static String getFtpPassword() {
        return GeoptimaCoreUIPlugin.getDefault().getPreferenceStore().getString(FTP_PASSWORD_KEY);
    }
}
