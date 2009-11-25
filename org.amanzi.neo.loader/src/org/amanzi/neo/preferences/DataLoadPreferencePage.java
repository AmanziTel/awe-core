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
package org.amanzi.neo.preferences;

import net.refractions.udig.ui.preferences.CharSetFieldEditor;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * Amanzi data loading preference page
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DataLoadPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String LABEL_REMOVE_SITE_NAME = "Remove site name from sector name";
    private static final String USE_COMBINED_CALCULATION = "Use combined network density calculation";
    private static final String ZOOM_TO_DATA = "Zoom to data";
    private static final String PREFERENCE_CHARSET = "Default Character Set";


    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {
        BooleanFieldEditor editor = new BooleanFieldEditor(DataLoadPreferences.REMOVE_SITE_NAME, LABEL_REMOVE_SITE_NAME,
                getFieldEditorParent());
        addField(editor);
        editor = new BooleanFieldEditor(DataLoadPreferences.NETWORK_COMBINED_CALCULATION, USE_COMBINED_CALCULATION,
                getFieldEditorParent());
        addField(editor);
        editor = new BooleanFieldEditor(DataLoadPreferences.ZOOM_TO_LAYER, ZOOM_TO_DATA, getFieldEditorParent());
        addField(editor);

        addField(new CharSetFieldEditor(DataLoadPreferences.DEFAULT_CHARSET, PREFERENCE_CHARSET, getFieldEditorParent()));

    }


    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
