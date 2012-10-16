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
import org.amanzi.awe.ui.tree.preferences.AbstractLabelPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkLabelsInitializer extends AbstractLabelPreferenceInitializer {

    public static final String NETWORK_LABEL_TEMPLATE = "network_tree.label_template";

    @Override
    protected String getPreferenceKey() {
        return NETWORK_LABEL_TEMPLATE;
    }

    @Override
    protected String getTemplate() {
        return "#name#";
    }

    @Override
    protected IPreferenceStore getPreferenceStore() {
        return NetworkTreePlugin.getDefault().getPreferenceStore();
    }

}
