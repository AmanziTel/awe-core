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
import org.amanzi.neo.core.database.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.loader.APDLoader;
import org.amanzi.neo.loader.GPEHLoader;
import org.amanzi.neo.loader.IdenLoader;
import org.amanzi.neo.loader.OSSCounterLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * GPEH import wizard page
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHImportWizard extends Wizard implements IImportWizard {

    private OSSImportWizardPage mainPage;
    private Display display;
    private boolean addToSelect;

    @Override
    public boolean performFinish() {
        Job job = new Job("Load OSS '" + (new File(mainPage.getDirectory())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    switch (mainPage.ossDirType.getLeft()) {
                    case GPEH:
                        GPEHLoader loader = new GPEHLoader(mainPage.getDirectory(), mainPage.getDatasetName(), display);
                        loader.run(monitor);
                        break;
                    case COUNTER:
                        OSSCounterLoader loaderOss = new OSSCounterLoader(mainPage.getDirectory(), mainPage.getDatasetName(), display);
                        loaderOss.run(monitor);
                        break;
                    case APD:
                        APDLoader apdLoader = new APDLoader(mainPage.getDirectory(), mainPage.getDatasetName(), display);
                        apdLoader.run(monitor);
                        break;
                    case iDEN:
                        IdenLoader idenLoader = new IdenLoader(mainPage.getDirectory(), mainPage.getDatasetName(), display);
                        idenLoader.run(monitor);
                        break;
                    default:
                        break;
                    }
                    NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));
                } catch (IOException e) {
                    NeoLoaderPlugin.error(e.getLocalizedMessage());
                    return new Status(Status.ERROR, "org.amanzi.neo.loader", e.getMessage());
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        return true;
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new OSSImportWizardPage("ossPage1");
        setWindowTitle(NeoLoaderPluginMessages.GpehWindowTitle);
        display = workbench.getDisplay();
    }

    public void addToSelectParam(String addToSelect) {
        this.addToSelect = addToSelect != null && "true".equalsIgnoreCase(addToSelect);
    }
}
