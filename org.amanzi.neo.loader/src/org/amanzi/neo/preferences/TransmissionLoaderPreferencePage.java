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
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TransmissionLoaderPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    @Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Group mainAttributeGroup = new Group(attributePanel, attributePanel.getStyle());
        mainAttributeGroup.setText(NeoLoaderPluginMessages.PrefTransmission_title);
        mainAttributeGroup.setLayout(new GridLayout());
        
        Group attributeGroup = new Group(mainAttributeGroup, mainAttributeGroup.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefTransmission_title_server);
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        int width = 50;
        addField(new StringFieldEditor(DataLoadPreferences.TR_SITE_ID_SERV, NeoLoaderPluginMessages.PrefTransmission_field_Site_ID, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.TR_SITE_NO_SERV, NeoLoaderPluginMessages.PrefTransmission_field_Site_No,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.TR_ITEM_NAME_SERV, NeoLoaderPluginMessages.PrefTransmission_field_ITEM_Name,width, marginPanel));
        
        attributeGroup = new Group(mainAttributeGroup, mainAttributeGroup.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.PrefTransmission_title_neighbour);
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        addField(new StringFieldEditor(DataLoadPreferences.TR_SITE_ID_NEIB, NeoLoaderPluginMessages.PrefTransmission_field_Site_ID, width,marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.TR_SITE_NO_NEIB, NeoLoaderPluginMessages.PrefTransmission_field_Site_No,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.TR_ITEM_NAME_NEIB, NeoLoaderPluginMessages.PrefTransmission_field_ITEM_Name,width, marginPanel));
    
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

}
