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
import org.amanzi.neo.core.enums.NetworkFileType;
import org.amanzi.neo.loader.NetworkLoader;
import org.amanzi.neo.loader.ProbeLoader;
import org.amanzi.neo.loader.ui.utils.LoaderUtils;
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
 * Import Network data wizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
@Deprecated
//TODO remove candidate
public class NetworkImportWizard extends Wizard implements IImportWizard {

    /** String PAGE_TITLE field */
    private static final String PAGE_TITLE = "Import Network File";
    /** String PAGE_DESCR field */
    private static final String PAGE_DESCR = "Import a file from the local file system into the workspace";
    private NetworkImportWizardPage mainPage;
    private Display display;
    @Override
    public boolean performFinish() {

        Job job = new Job("Load Network '" + (new File(mainPage.getFileName())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                NetworkLoader networkLoader;
                try {
                    // TODO refactor
                    if (NetworkFileType.RADIO_SECTOR == LoaderUtils.getFileType(mainPage.getFileName()).getLeft()) {
                        networkLoader = new NetworkLoader(new File(mainPage.getFileName()).getName(),mainPage.getFileName(), display);
                        networkLoader.setup();
                        networkLoader.run(monitor);
                        networkLoader.printStats(false);
                        NetworkLoader.finishUpGis();
                        networkLoader.addLayersToMap();
                    } else {
                        ProbeLoader loader = new ProbeLoader(new File(mainPage.getFileName()).getName(),mainPage.getFileName(), display);
                        loader.run(monitor);
                        // TODO add to layer after changing NetworkRenderer
                    }

                } catch (IOException e) {
                    NeoCorePlugin.error("Error loading Network file", e);
                    return new Status(Status.ERROR, "org.amanzi.neo.loader", e.getMessage());
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule(50);
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new NetworkImportWizardPage(PAGE_TITLE, PAGE_DESCR);
        display = workbench.getDisplay();
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
