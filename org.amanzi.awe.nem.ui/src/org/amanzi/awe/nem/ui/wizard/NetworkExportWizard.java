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

import java.util.Map.Entry;

import org.amanzi.awe.nem.export.ExportedDataContainer;
import org.amanzi.awe.nem.export.ExportedDataItems;
import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.wizard.pages.export.EditExportSettingsPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.EditSynonymsPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.ExportedDataSetupPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.INetworkExportPage;
import org.amanzi.awe.nem.ui.wizard.pages.export.SelectDestinationFolderPage;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
    private ExportedDataSetupPage exportDataPage;
    private EditExportSettingsPage generalExportSettingsPage;

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
        this.exportDataPage = new ExportedDataSetupPage();
        addPage(exportDataPage);
        for (ExportedDataItems item : ExportedDataItems.values()) {
            EditSynonymsPage page = new EditSynonymsPage(item);
            page.setUpNetwork(networkModel);
            addPage(page);
        }
        generalExportSettingsPage = new EditExportSettingsPage();
        addPage(generalExportSettingsPage);
    }

    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        if (page.equals(mainPage)) {
            networkModel = mainPage.getNetworkModel();
        } else if (exportDataPage.equals(page)) {
            EditSynonymsPage nextPage = (EditSynonymsPage)getPage(exportDataPage.getSelectedPages().get(0).getName());
            nextPage.setUpNetwork(networkModel);
            return nextPage;
        } else if (page instanceof EditSynonymsPage) {
            EditSynonymsPage currentPage = (EditSynonymsPage)page;
            int index = currentPage.getPageType().getIndex();
            index++;
            ExportedDataItems nextPage = exportDataPage.getSelectedPages().get(index);
            if (nextPage == null) {
                return generalExportSettingsPage;
            } else {
                return getPage(nextPage.getName());
            }
        }
        INetworkExportPage networkPage = (INetworkExportPage)super.getNextPage(page);
        if (networkPage != null) {
            networkPage.setUpNetwork(networkModel);
        }
        return networkPage;
    }

    @Override
    public IWizardPage getPreviousPage(final IWizardPage page) {
        if (page.equals(mainPage)) {
            return null;
        } else if (exportDataPage.equals(page)) {
            return mainPage;
        } else if (page instanceof EditSynonymsPage) {
            EditSynonymsPage currentPage = (EditSynonymsPage)page;
            int index = currentPage.getPageType().getIndex();
            index--;
            ExportedDataItems prevPage = exportDataPage.getSelectedPages().get(index);
            if (prevPage == null) {
                return exportDataPage;
            } else {
                return getPage(prevPage.getName());
            }
        } else if (generalExportSettingsPage.equals(page)) {
            ExportedDataItems prevPage = (ExportedDataItems)exportDataPage.getSelectedPages().values().toArray()[exportDataPage
                    .getSelectedPages().size()];
            return getPage(prevPage.getName());
        }
        return null;
    }

    @Override
    public boolean performFinish() {
        final ExportedDataContainer container = prepareExportedContainer();
        Job job = new Job("Export network " + mainPage.getNetworkModel().getName()) {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    NetworkElementManager.getInstance().exportNetworkData(container, monitor);
                } catch (Exception e) {
                    return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "Can't export network");
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

    /**
     * @return
     */
    private ExportedDataContainer prepareExportedContainer() {
        ExportedDataContainer container = new ExportedDataContainer(mainPage.getNetworkModel(),
                generalExportSettingsPage.getCharset(), generalExportSettingsPage.getSeparator(),
                generalExportSettingsPage.getQuoteSeparator(), mainPage.getDestinationFolderPath());
        for (Entry<Integer, ExportedDataItems> pages : exportDataPage.getSelectedPages().entrySet()) {
            EditSynonymsPage page = (EditSynonymsPage)getPage(pages.getValue().getName());
            container.addToSynonyms(page.getPageType(), page.getProperties());
        }
        return container;
    }
}
