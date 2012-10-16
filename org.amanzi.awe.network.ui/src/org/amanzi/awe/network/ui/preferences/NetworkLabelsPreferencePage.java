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

package org.amanzi.awe.network.ui.preferences;

import org.amanzi.awe.network.ui.NetworkTreePlugin;
import org.amanzi.awe.ui.tree.preferences.AbstractLabelPreferencePage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkLabelsPreferencePage extends AbstractLabelPreferencePage {

    /**
     * @param description
     * @param label
     * @param preferenceStore
     */
    public NetworkLabelsPreferencePage() {
        super("Select template of label for Network Tree Item", "Network Tree item label:", NetworkTreePlugin.getDefault()
                .getPreferenceStore());
    }

    @Override
    protected String getPreferenceKey() {
        return NetworkLabelsInitializer.NETWORK_LABEL_TEMPLATE;
    }

}
