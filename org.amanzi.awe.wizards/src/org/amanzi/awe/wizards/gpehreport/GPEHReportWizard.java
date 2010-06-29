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

package org.amanzi.awe.wizards.gpehreport;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizard extends Wizard implements INewWizard {

    private GPEHReportWizardPage firstPage;

    @Override
    public void addPages() {
        super.addPages();
        addPage(firstPage);
        // addPage(gpehOptionalLoadPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        firstPage = new GPEHReportWizardPage("ossPage1");
        // setWindowTitle(NeoLoaderPluginMessages.GpehWindowTitle);
        // display = workbench.getDisplay();
    }

    @Override
    public boolean performFinish() {
        return false;
    }
}
