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
package org.amanzi.neo.wizards;

import java.io.File;
import java.io.IOException;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.events.ImportCsvStatisticsEvent;
import org.amanzi.neo.loader.AMSLoader;
import org.amanzi.neo.loader.AMSXMLoader;
import org.amanzi.neo.loader.LoaderUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * AMSImportWizard - wizard for import AMS data
 * </p>
 * 
 * @author Lagutko_n
 * @since 1.0.0
 */
public class AMSImportWizard extends Wizard implements IImportWizard {

    private AMSImportWizardPage mainPage;

    @Override
    public boolean performFinish() {
        Job job = new Job("Load AMS '" + (new File(mainPage.getFileName())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                File firstFile = LoaderUtils.getFirstFile(mainPage.getFileName());
                if (firstFile != null) {
                    String fileExtension = LoaderUtils.getFileExtension(firstFile.getName());
                    if (fileExtension.equalsIgnoreCase(".xml")) {
                        AMSXMLoader loader = new AMSXMLoader(mainPage.getFileName(), null, mainPage.getDatasetName(), mainPage.getNetworkName());
                        try {
                            loader.run(monitor);
                            AMSXMLoader.finishUpGis();
                        } catch (IOException e) {
                            NeoLoaderPlugin.error(e.getLocalizedMessage());
                            return new Status(Status.ERROR, "org.amanzi.neo.loader", e.getMessage());
                        }
                    } else if (fileExtension.equalsIgnoreCase(".csv")) {
                        NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new ImportCsvStatisticsEvent(mainPage.getFileName(), mainPage.getDatasetName(), mainPage.getNetworkName()));
                    } else {
                        AMSLoader loader = new AMSLoader(mainPage.getFileName(), null, mainPage.getDatasetName(), mainPage.getNetworkName());
                        try {
                            loader.run(monitor);
                            AMSLoader.finishUpGis();
                        } catch (IOException e) {
                            NeoLoaderPlugin.error(e.getLocalizedMessage());
                            return new Status(Status.ERROR, "org.amanzi.neo.loader", e.getMessage());
                        }
                    }
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new AMSImportWizardPage(NeoLoaderPluginMessages.AMSImport_page_title, NeoLoaderPluginMessages.AMSImport_page_descr);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }
}
