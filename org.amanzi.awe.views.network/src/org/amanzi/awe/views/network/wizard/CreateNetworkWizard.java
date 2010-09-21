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

package org.amanzi.awe.views.network.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CreateNetworkWizard extends Wizard implements INewWizard {

    private IWorkbench workbench;
    private IStructuredSelection selection;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
        setWindowTitle("Create network");
        setForcePreviousAndNextButtons(true);
        initPages();
    }


    private void initPages() {
        addPage(new CreateNetworkMainPage("mainPage"));
    }

    @Override
    public boolean performFinish() {
        return true;
    }

  

}
