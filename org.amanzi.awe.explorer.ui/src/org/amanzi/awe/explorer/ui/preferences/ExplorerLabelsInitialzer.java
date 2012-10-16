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

package org.amanzi.awe.explorer.ui.preferences;

import org.amanzi.awe.explorer.ui.ProjectExplorerPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ExplorerLabelsInitialzer extends AbstractPreferenceInitializer {

    public static final String EXPLORER_LABEL_TEMPLATE = "project_explorer.label_template";

    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = ProjectExplorerPlugin.getDefault().getPreferenceStore();

        store.setDefault(EXPLORER_LABEL_TEMPLATE, "#name#");
    }

}
