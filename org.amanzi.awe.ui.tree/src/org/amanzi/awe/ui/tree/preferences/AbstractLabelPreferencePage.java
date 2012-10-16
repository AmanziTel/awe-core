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

package org.amanzi.awe.ui.tree.preferences;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.tree.label.LabelTemplateUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLabelPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private final String label;

    private StringFieldEditor editor;

    protected AbstractLabelPreferencePage(final String description, final String label, final IPreferenceStore preferenceStore) {
        super();

        this.label = label;

        setDescription(description);
        setPreferenceStore(preferenceStore);
    }

    @Override
    public boolean performOk() {
        final boolean valid = validateInput() && super.performOk();

        if (valid) {
            AWEEventManager.getManager().fireDataUpdatedEvent(this);
        }

        return valid;
    }

    private boolean validateInput() {
        final String value = editor.getStringValue();

        if (!StringUtils.isEmpty(value)) {
            return LabelTemplateUtils.getTemplate(value) != null;
        }

        return false;
    }

    @Override
    protected void createFieldEditors() {
        editor = new StringFieldEditor(getPreferenceKey(), label, getFieldEditorParent());

        addField(editor);
    }

    protected abstract String getPreferenceKey();

    @Override
    public void init(final IWorkbench workbench) {
        // do nothing
    }
}
