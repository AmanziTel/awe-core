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

package org.amanzi.awe.report.wizards;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Page for GeOptima Report Wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SelectDataPage extends WizardPage {

    private static final String SELECT_DIRECTORY = "Select directory";
    private DirectoryFieldEditor directoryFieldEditor;

    public SelectDataPage(String pageName) {
        super(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        directoryFieldEditor = new DirectoryFieldEditor("geoptimaDataDir", SELECT_DIRECTORY, parent);
        directoryFieldEditor.getTextControl(parent).addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(directoryFieldEditor.getStringValue() != null);
            }
        });
        setPageComplete(false);
        setControl(parent);
    }

    /**
     * Gets directory
     */
    public String getDirectory() {
        return directoryFieldEditor.getStringValue();
    }
}
