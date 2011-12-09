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

package org.amanzi.neo.loader.ui.wizards;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Network import wizard page
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkImportWizard extends AbstractLoaderWizard<ConfigurationDataImpl> {

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        super.init(workbench, selection);
        setWindowTitle(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_TITLE);
    }

    @Override
    public boolean performFinish() {
        if (super.performFinish()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected ConfigurationDataImpl getConfigInstance() {
        return new ConfigurationDataImpl();
    }

}
