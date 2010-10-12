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
 *Drive Loader loading preference page
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DriveLoaderPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public DriveLoaderPreferencePage() {
        super();
        setPreferenceStore(NeoLoaderPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Label label = new Label(attributePanel, SWT.FLAT);
        label.setText("When loading drive data find special types that match:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText("Latitude and Longitude");
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        int width = 49;
        // addField(new StringFieldEditor(DataLoadPreferences.NH_SITE,
        // NeoLoaderPluginMessages.PrefNetwork_field_site,width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_LATITUDE, NeoLoaderPluginMessages.PrefNetwork_field_latitude, width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_LONGITUDE, NeoLoaderPluginMessages.PrefNetwork_field_longitude, width, marginPanel));

        attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText("Channel identification");
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 52;

        addField(new StringFieldEditor(DataLoadPreferences.DR_BCCH, "BCCH", width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_TCH, "TCH", width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_TCH, "SC", width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_PN, "PN", width, marginPanel));

        attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText("Signal measurements");
        attributeGroup.setLayout(new GridLayout());
        marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        width = 53;
        addField(new StringFieldEditor(DataLoadPreferences.DR_EcIo, "Ec/Io", width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_RSSI, "RSSI", width, marginPanel));
        addField(new StringFieldEditor(DataLoadPreferences.DR_CI, "C/I", width, marginPanel));
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

}
