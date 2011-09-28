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
package org.amanzi.neo.wizards;

import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * Load Drive page wizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TemsImportWizardPage extends WizardPage {

    private DriveDialog dialog;

    /**
     * Constructor
     * 
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
        dialog = new DriveDialog(main, this);
        setControl(main);
    }

    /**
     * @return Returns the dialog.
     */
    public DriveDialog getDialog() {
        return dialog;
    }

}
