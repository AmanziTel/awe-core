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

package org.amanzi.splash.ui.importWizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Page for wizard that exports script from ImportBuilder
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ExportBuilderScriptWizardPage extends WizardNewFileCreationPage {
    
    

    /**
     * @param pageName
     * @param selection
     */
    public ExportBuilderScriptWizardPage(String pageName, IStructuredSelection selection) {
        super(pageName, selection);        
    }
    
    @Override
    protected void createAdvancedControls(Composite parent) {       
        //no advanced controls
    }
    
    @Override
    protected IStatus validateLinkedResource() {
        return Status.OK_STATUS;
    }

}
