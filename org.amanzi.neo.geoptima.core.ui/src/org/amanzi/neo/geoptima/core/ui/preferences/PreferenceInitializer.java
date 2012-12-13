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

package org.amanzi.neo.geoptima.core.ui.preferences;

import org.amanzi.neo.geoptima.core.ui.internal.GeoptimaCoreUIPlugin;
import org.amanzi.neo.geoptima.core.ui.manager.CredentialsManager;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    private static final IPreferenceStore PREFERENCE_STORE = GeoptimaCoreUIPlugin.getDefault().getPreferenceStore();

    @Override
    public void initializeDefaultPreferences() {
        PREFERENCE_STORE.setDefault(CredentialsManager.FTP_HOST_KEY, "ftp.amanzitel.com");
        PREFERENCE_STORE.setDefault(CredentialsManager.FTP_USERNAME_KEY, "amanzitel");
        PREFERENCE_STORE.setDefault(CredentialsManager.FTP_PASSWORD_KEY, "J3sT?dr4");
    }
}
