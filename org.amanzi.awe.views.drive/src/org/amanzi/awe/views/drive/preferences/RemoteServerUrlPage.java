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

package org.amanzi.awe.views.drive.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * Page that keeps remote server url
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class RemoteServerUrlPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    @Override
    protected void createFieldEditors() {
        Composite attributePanel = getFieldEditorParent();
        attributePanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        attributePanel.setLayout(new GridLayout());

        Group attributeGroup = new Group(attributePanel, attributePanel.getStyle());
        attributeGroup.setText(NeoLoaderPluginMessages.RemoteServerUrlPage_1);
        attributeGroup.setLayout(new GridLayout());
        Composite marginPanel = new Composite(attributeGroup, attributeGroup.getStyle());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 15;
        layout.marginWidth = 15;
        marginPanel.setLayout(layout);

        int width = 52;
        // addField(new StringFieldEditor(DataLoadPreferences.REMOTE_SERVER_URL,
        // NeoLoaderPluginMessages.RemoteServerUrlPage_0, width, marginPanel));
        StringFieldEditor field = new UrlFieldEditor(DataLoadPreferences.REMOTE_SERVER_URL, NeoLoaderPluginMessages.RemoteServerUrlPage_0, width, 0, marginPanel);
        StringFieldEditor imeiField = new StringFieldEditor(DataLoadPreferences.USER_IMEI,NeoLoaderPluginMessages.PrefUrl_imei, 20, 0, marginPanel);
        imeiField.setEmptyStringAllowed(false);
        StringFieldEditor imsiField = new StringFieldEditor(DataLoadPreferences.USER_IMSI,NeoLoaderPluginMessages.PrefUrl_imsi, 20, 0, marginPanel);
        imsiField.setEmptyStringAllowed(false);
        // field.setEmptyStringAllowed(false);
        // field
        // field.
        addField(field);
        addField(imeiField);
        addField(imsiField);
        // setMessage("BUGOGA", IStatus.WARNING);
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

    private class UrlFieldEditor extends StringFieldEditor {

        public UrlFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
            super(name, labelText, width, strategy, parent);
        }

        @Override
        protected boolean checkState() {
            Text textField = getTextControl();

            String txt = textField.getText();

            if (txt.trim().isEmpty()) {
                acceptance("URL is empty!", textField);
                return true;
            }
            try {
                new URL(txt);
            } catch (MalformedURLException e) {
                // e.printStackTrace();
                error("URL is not valid!", textField);
                return false;
            }
            clearErrorMessage();
            valid(textField);

            return true;
        }

        private void error(String message, Text field) {
            setErrorMessage(message);
            showErrorMessage();
            field.setForeground(new Color(null, 255, 0, 0));
            field.setToolTipText(message);
        }

        private void acceptance(String message, Text field) {
            clearErrorMessage();
            setMessage(message, IStatus.WARNING);
            field.setForeground(new Color(null, 0, 0, 0));
            field.setToolTipText(message);
        }

        private void valid(Text field) {
            clearErrorMessage();
            setMessage(getTitle(), IStatus.OK);
            field.setForeground(new Color(null, 0, 0, 0));
            field.setToolTipText(null);
        }

    }

}
