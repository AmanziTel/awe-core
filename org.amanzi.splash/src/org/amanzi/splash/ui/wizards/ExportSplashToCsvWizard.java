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

package org.amanzi.splash.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class ExportSplashToCsvWizard extends Wizard implements IExportWizard {
    ExportSplashToCsvWizardPage mainPage = null;
    @Override
    public boolean performFinish() {
        return false;
    }

    @Override
    public void addPages() {
        super.addPages();
        if (mainPage == null) {
            mainPage = new ExportSplashToCsvWizardPage("mainPage");
        }
        addPage(mainPage);
    }
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Export Splash to CSV file");
    }

}
