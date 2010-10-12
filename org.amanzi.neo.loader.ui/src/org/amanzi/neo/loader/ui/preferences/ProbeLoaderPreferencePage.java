package org.amanzi.neo.loader.ui.preferences;

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ProbeLoaderPreferencePage  extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefProbe_title);        
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);
        
        int width = 52;
		addField(new StringFieldEditor(DataLoadPreferences.PR_NAME,NeoLoaderPluginMessages.PrefProbe_field_name, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.PR_TYPE, NeoLoaderPluginMessages.PrefProbe_field_probe_type,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.PR_LATITUDE, NeoLoaderPluginMessages.PrefProbe_field_latitude,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.PR_LONGITUDE, NeoLoaderPluginMessages.PrefProbe_field_longitude,width, marginPanel));
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

}
