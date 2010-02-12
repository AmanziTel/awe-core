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

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
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

        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefNetwork_title);
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        int width = 54;
		addField(new StringFieldEditor(DataLoadPreferences.NH_CITY, NeoLoaderPluginMessages.PrefNetwork_field_city, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_MSC, NeoLoaderPluginMessages.PrefNetwork_field_msc, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_BSC, NeoLoaderPluginMessages.PrefNetwork_field_bsc, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_SITE, NeoLoaderPluginMessages.PrefNetwork_field_site,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_SECTOR, NeoLoaderPluginMessages.PrefNetwork_field_sector,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_LATITUDE, NeoLoaderPluginMessages.PrefNetwork_field_latitude,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.NH_LONGITUDE, NeoLoaderPluginMessages.PrefNetwork_field_longitude,width, marginPanel));
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
