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
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * Amanzi data loading preference page
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DataLoadPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String LABEL_REMOVE_SITE_NAME = "Remove site name from sector name";
    private static final String USE_COMBINED_CALCULATION = "Use combined network density calculation";
    private static final String ZOOM_TO_DATA = "Zoom to data";
    private static final String NH_CITY = "city";
    private static final String NH_MSC = "msc";
    private static final String NH_BSC = "bsc";
    private static final String NH_SITE = "site";
    private static final String NH_SECTOR = "sector";
    private static final String NH_LATITUDE = "latitude";
    private static final String NH_LONGITUDE = "longitude";

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {
        BooleanFieldEditor editor = new BooleanFieldEditor(DataLoadPreferences.REMOVE_SITE_NAME, LABEL_REMOVE_SITE_NAME,
                getFieldEditorParent());
        addField(editor);
        editor = new BooleanFieldEditor(DataLoadPreferences.NETWORK_COMBINED_CALCULATION, USE_COMBINED_CALCULATION,
                getFieldEditorParent());
        addField(editor);
        editor = new BooleanFieldEditor(DataLoadPreferences.ZOOM_TO_LAYER, ZOOM_TO_DATA, getFieldEditorParent());
        addField(editor);
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
        attributePanel.setLayout(new GridLayout());

        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText("When loading network data find special types that match the following:");
        attributeGroup.setLayout(new GridLayout());

        // group.setText("When loading network data find special types that match the following:");
        StringFieldEditor strEd = new StringFieldEditor(DataLoadPreferences.NH_CITY, NH_CITY, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_MSC, NH_MSC, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_BSC, NH_BSC, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_SITE, NH_SITE, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_SECTOR, NH_SECTOR, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_LATITUDE, NH_LATITUDE, attributeGroup);
        addField(strEd);
        strEd = new StringFieldEditor(DataLoadPreferences.NH_LONGITUDE, NH_LONGITUDE, attributeGroup);
        addField(strEd);
    }


    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
