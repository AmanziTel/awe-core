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

package org.amanzi.awe.nem.ui.wizard;

import org.amanzi.awe.nem.ui.wizard.pages.export.ExportedDataSetupPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.INetworkExportPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.SelectDestinationFolderPage;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkExportWizard extends Wizard {

    private INetworkModel networkModel;
    private SelectDestinationFolderPage mainPage;

    public NetworkExportWizard(final INetworkModel model) {
        this.networkModel = model;
    }

    @Override
    public void addPage(final IWizardPage page) {
        super.addPage(page);
    }

    @Override
    public void addPages() {
        this.mainPage = new SelectDestinationFolderPage();
        mainPage.setUpNetwork(networkModel);
        addPage(mainPage);
        addPage(new ExportedDataSetupPage());
    }

    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        if (page.equals(mainPage)) {
            networkModel = mainPage.getNetworkModel();
        }
        INetworkExportPage networkPage = (INetworkExportPage)super.getNextPage(page);
        if (networkPage != null) {
            networkPage.setUpNetwork(networkModel);
        }
        return networkPage;
    }

    @Override
    public boolean performFinish() {
        return false;
    }

}
