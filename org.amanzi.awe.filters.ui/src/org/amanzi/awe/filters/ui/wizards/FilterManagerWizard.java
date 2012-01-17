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

package org.amanzi.awe.filters.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * wizard for manage filters
 * 
 * @author Vladislav_Kondratneko
 */
public class FilterManagerWizard extends Wizard implements IImportWizard {
    private final static String FILTER_MANAGER_TITLE = "Filter Manager ";

    @Override
    public boolean performFinish() {
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        super.addPage(new FilterManagerPage(FILTER_MANAGER_TITLE));
        setWindowTitle(FILTER_MANAGER_TITLE);
    }

}
