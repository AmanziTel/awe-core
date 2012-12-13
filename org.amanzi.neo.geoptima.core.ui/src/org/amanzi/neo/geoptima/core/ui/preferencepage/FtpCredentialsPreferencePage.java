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

package org.amanzi.neo.geoptima.core.ui.preferencepage;

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.TextWidget;
import org.amanzi.awe.ui.view.widgets.TextWidget.ITextChandedListener;
import org.amanzi.neo.geoptima.core.ui.internal.GeoptimaCoreUIPlugin;
import org.amanzi.neo.geoptima.core.ui.manager.CredentialsManager;
import org.amanzi.neo.geoptima.core.ui.messages.CoreMessages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FtpCredentialsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, ITextChandedListener {

    private static final int LABEL_WIDTH = 70;
    private TextWidget hostWidget;
    private TextWidget userNameWidget;
    private TextWidget passwordWidget;

    @Override
    public void init(final IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Control createContents(final Composite parent) {
        hostWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, CoreMessages.host, parent, LABEL_WIDTH);
        userNameWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, CoreMessages.userName, parent, LABEL_WIDTH);
        passwordWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, CoreMessages.password, parent, LABEL_WIDTH);

        hostWidget.setDefault(CredentialsManager.getFtpHost());
        userNameWidget.setDefault(CredentialsManager.getFtpUserName());
        passwordWidget.setDefault(CredentialsManager.getFtpPassword());
        return parent;
    }

    @Override
    public void onTextChanged(final String text) {
    }

    @Override
    public boolean performOk() {
        GeoptimaCoreUIPlugin.getDefault().getPreferenceStore().setValue(CredentialsManager.FTP_HOST_KEY, hostWidget.getText());
        GeoptimaCoreUIPlugin.getDefault().getPreferenceStore()
                .setValue(CredentialsManager.FTP_USERNAME_KEY, userNameWidget.getText());
        GeoptimaCoreUIPlugin.getDefault().getPreferenceStore()
                .setValue(CredentialsManager.FTP_PASSWORD_KEY, passwordWidget.getText());
        return super.performOk();
    }
}
