/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.preferences;

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
 * @since 1.1.0
 */
public class DataLoadPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String LABEL_REMOVE_SITE_NAME = "Remove site name from sector name";

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {
        BooleanFieldEditor helpOnStart = new BooleanFieldEditor(DataLoadPreferences.REMOVE_SITE_NAME, LABEL_REMOVE_SITE_NAME,
                getFieldEditorParent());
        addField(helpOnStart);
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
