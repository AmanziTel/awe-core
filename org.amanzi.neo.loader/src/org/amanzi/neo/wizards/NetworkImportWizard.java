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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Import Network data wizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NetworkImportWizard extends Wizard implements IImportWizard {

    private IWizardPage mainPage;
    @Override
    public boolean performFinish() {
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = null;
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
