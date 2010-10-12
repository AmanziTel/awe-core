package org.amanzi.neo.loader.ui.preferences;

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
		addField(new StringFieldEditor(DataLoadPreferences.NE_BTS, NeoLoaderPluginMessages.PrefNeighbour_field_bts, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_CI, NeoLoaderPluginMessages.PrefNeighbour_field_ci,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_LAC, NeoLoaderPluginMessages.PrefNeighbour_field_lac,width, marginPanel));
        
        attributeGroup = new Group(mainAttributeGroup, mainAttributeGroup.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNeighbour_title_neighbour);
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 51;
        addField(new StringFieldEditor(DataLoadPreferences.NE_ADJ_BTS, NeoLoaderPluginMessages.PrefNeighbour_field_adj_bts, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_ADJ_CI, NeoLoaderPluginMessages.PrefNeighbour_field_adj_ci,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NE_ADJ_LAC, NeoLoaderPluginMessages.PrefNeighbour_field_adj_lac,width, marginPanel));
    
    }

	@Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

}
