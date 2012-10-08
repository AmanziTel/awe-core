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

import java.util.List;
import java.util.Map.Entry;

import org.amanzi.neo.loader.core.synonyms.Synonyms;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.loader.ui.internal.LoaderUIPlugin;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
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
public abstract class AbstractDatasetSynonymsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public AbstractDatasetSynonymsPreferencePage(final String description) {
        super(GRID);

        setDescription(description);
        setPreferenceStore(LoaderUIPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        for (Entry<INodeType, List<Synonyms>> entry : SynonymsManager.getInstance().getSynonyms(getDatasetType()).entrySet()) {
            INodeType nodeType = entry.getKey();

            Group group = new Group(getFieldEditorParent(), SWT.NONE);
            group.setText(nodeType.getId());

            group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            for (Synonyms synonym : entry.getValue()) {
                StringFieldEditor synonymsEditor = new StringFieldEditor(getSynonymsKey(nodeType, synonym.getPropertyName()),
                        synonym.getPropertyName(), group);
                synonymsEditor.setEmptyStringAllowed(false);

                addField(synonymsEditor);
            }
        }
    }

    protected String getSynonymsKey(final INodeType nodeType, final String property) {
        return getDatasetType() + "." + nodeType.getId() + "." + property;
    }

    protected abstract String getDatasetType();

    @Override
    public void init(final IWorkbench workbench) {

    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();

        storeValues();

        return result;
    }

    @Override
    protected void performApply() {
        super.performApply();

        storeValues();
    }

    protected void storeValues() {
        for (Entry<INodeType, List<Synonyms>> entry : SynonymsManager.getInstance().getSynonyms(getDatasetType()).entrySet()) {
            for (Synonyms synonym : entry.getValue()) {
                String key = getSynonymsKey(entry.getKey(), synonym.getPropertyName());

                String[] value = getPreferenceStore().getString(key).split(", ");

                SynonymsManager.getInstance().updateSynonyms(getDatasetType(), entry.getKey(), synonym.getPropertyName(), value);
            }
        }
    }

}
