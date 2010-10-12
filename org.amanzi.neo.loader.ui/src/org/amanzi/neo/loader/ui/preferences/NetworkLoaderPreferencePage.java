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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Network Loader loading preference page
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NetworkLoaderPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    @Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Label label = new Label(attributePanel, SWT.FLAT);
        label.setText(NeoLoaderPluginMessages.PrefNetwork_title_network);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNetwork_title_site);
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);
        
        int width = 52;                
        addField(new StringFieldEditor(DataLoadPreferences.NH_SITE, NeoLoaderPluginMessages.PrefNetwork_field_site,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_LATITUDE, NeoLoaderPluginMessages.PrefNetwork_field_latitude,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_LONGITUDE, NeoLoaderPluginMessages.PrefNetwork_field_longitude,width, marginPanel));
        
        attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNetwork_title_sector);
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 51;
        addField(new StringFieldEditor(DataLoadPreferences.NH_SECTOR, NeoLoaderPluginMessages.PrefNetwork_field_sector,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_SECTOR_CI, NeoLoaderPluginMessages.PrefNetwork_field_sector_ci,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_SECTOR_LAC, NeoLoaderPluginMessages.PrefNetwork_field_sector_lac,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_BEAMWIDTH, NeoLoaderPluginMessages.PrefNetwork_field_beamwidth, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_AZIMUTH, NeoLoaderPluginMessages.PrefNetwork_field_azimuth,width, marginPanel));
        
        attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNetwork_title_oprional);
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 57;
        addField(new StringFieldEditor(DataLoadPreferences.NH_CITY, NeoLoaderPluginMessages.PrefNetwork_field_city, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_MSC, NeoLoaderPluginMessages.PrefNetwork_field_msc, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_BSC, NeoLoaderPluginMessages.PrefNetwork_field_bsc, width,marginPanel));
                
        
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
