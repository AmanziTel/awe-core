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

package org.amanzi.neo.loader.handlers;

import org.amanzi.neo.wizards.GPEHImportWizard;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IImportWizard;

/**
 * <p>
 *Open import GEH wizarg
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class LoadGPEHHandler extends AbstractOpenWizardHandler {

    @Override
    protected IImportWizard getWizardInstance(ExecutionEvent event) {
        String addToSelect = event.getParameter(LoadNetworkSiteHandler.PARAM_ADD_TO_SELECT);
        final GPEHImportWizard gpehImportWizard = new GPEHImportWizard();
        gpehImportWizard.addToSelectParam(addToSelect);
        return gpehImportWizard;
    }

}
