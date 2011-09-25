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

public class NeighbourLoaderPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Group mainAttributeGroup = new Group(attributePanel, attributePanel.getStyle());
        mainAttributeGroup.setText(NeoLoaderPluginMessages.PrefNeighbour_title);
        mainAttributeGroup.setLayout(new GridLayout());
        
        Group attributeGroup = new Group(mainAttributeGroup, mainAttributeGroup.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNeighbour_title_server);
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        int width = 55;
		addField(new StringFieldEditor(DataLoadPreferences.NE_SRV_NAME, NeoLoaderPluginMessages.PrefNeighbour_field_srv_name, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_SRV_CI, NeoLoaderPluginMessages.PrefNeighbour_field_srv_ci,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_SRV_LAC, NeoLoaderPluginMessages.PrefNeighbour_field_srv_lac,width, marginPanel));
        
        attributeGroup = new Group(mainAttributeGroup, mainAttributeGroup.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNeighbour_title_neighbour);
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 51;
        addField(new StringFieldEditor(DataLoadPreferences.NE_NBR_NAME, NeoLoaderPluginMessages.PrefNeighbour_field_nbr_name, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_NBR_CI, NeoLoaderPluginMessages.PrefNeighbour_field_nbr_ci,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_NBR_LAC, NeoLoaderPluginMessages.PrefNeighbour_field_nbr_lac,width, marginPanel));
    
    }

	@Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

}
