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

package org.amanzi.neo.loader.ui.preferences.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.saver.impl.AbstractDriveSaver;
import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.loader.ui.internal.LoaderUIPlugin;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SynonymsPreferencesInitializer extends AbstractPreferenceInitializer {

    private static final String[] SYNONYMS_TYPES = {NetworkSaver.SYNONYMS_TYPE, AbstractDriveSaver.DRIVE_SYNONYMS};

    /**
     * 
     */
    public SynonymsPreferencesInitializer() {

    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = LoaderUIPlugin.getDefault().getPreferenceStore();

        for (String synonymsType : SYNONYMS_TYPES) {
            for (Entry<INodeType, List<Synonyms>> synonymsEntry : SynonymsManager.getInstance().getSynonyms(synonymsType).entrySet()) {
                for (Synonyms singleSynonym : synonymsEntry.getValue()) {
                    String preferenceKey = synonymsType + "." + synonymsEntry.getKey().getId() + "." + singleSynonym.getPropertyName();

                    String preferenceValue = StringUtils.join(Arrays.asList(singleSynonym.getPossibleHeaders()), ", ");

                    store.setDefault(preferenceKey, preferenceValue);
                }
            }
        }
    }

}
