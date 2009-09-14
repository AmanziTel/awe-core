/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.wizards;

import org.amanzi.neo.loader.dialogs.TEMSDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * Load Drive page wizard
 * </p>
 * 
 * @author TsAr
 * @since 1.1.0
 */
public class TemsImportWizardPage extends WizardPage {

    private TEMSDialog dialog;

    /**
     * @param pageTitle
     * @param pageDescr
     */
    public TemsImportWizardPage(String pageTitle, String pageDescr) {
        super(pageTitle);
        setTitle(pageTitle);
        setDescription(pageDescr);
    }

    @Override
    public void createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.FILL);
        dialog = new TEMSDialog(main, this);
        setControl(main);
    }

    /**
     * @return Returns the dialog.
     */
    public TEMSDialog getDialog() {
        return dialog;
    }

}
