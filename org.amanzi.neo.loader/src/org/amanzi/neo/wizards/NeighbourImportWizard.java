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

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
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
 * NeighbourImportWizard - wizard for import Neighbour data
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
@Deprecated
//TODO remove candidate
public class NeighbourImportWizard extends Wizard implements IImportWizard {

    private static final String PAGE_TITLE = "Import Neighbours";
    private static final String PAGE_DESCR = "Import a neighbour list file into a previously loaded network";
    private NeighbourImportWizardPage mainPage;

    @Override
    public boolean performFinish() {
        Job job = new Job("Load Neighbour '" + (new File(mainPage.getFileName())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                NeighbourLoader neighbourLoader;
                neighbourLoader = new NeighbourLoader(mainPage.getNetworkNode(), mainPage.getFileName(), NeoServiceProvider
                        .getProvider().getService());
                try {
                    neighbourLoader.run(monitor);
                    NeoServiceProvider.getProvider().commit();
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
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new NeighbourImportWizardPage(PAGE_TITLE, PAGE_DESCR);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }
}
