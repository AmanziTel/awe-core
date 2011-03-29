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
package org.amanzi.awe.afp;

import org.amanzi.neo.services.ui.utils.DoubleFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * Afp preference page
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AfpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        DoubleFieldEditor editor = new DoubleFieldEditor(PreferenceInitializer.AFP_MIN_CO,
                "Do not create IM relation if Co below:", parent);
        editor.setValidRange(0, Double.MAX_VALUE);
        addField(editor);
        editor = new DoubleFieldEditor(PreferenceInitializer.AFP_MIN_PROP_VALUE, "Do not store in relation properties bellow:",
                parent);
        editor.setValidRange(0, Double.MAX_VALUE);
        addField(editor);
    }

}