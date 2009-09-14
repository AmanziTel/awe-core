/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.wizards;

import java.io.File;
import java.io.IOException;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.NetworkLoader;
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
 * Import Network data wizard
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class NetworkImportWizard extends Wizard implements IImportWizard {

    /** String PAGE_TITLE field */
    private static final String PAGE_TITLE = "Import Network File";
    /** String PAGE_DESCR field */
    private static final String PAGE_DESCR = "Import a file from the local file system into the workspace";
    private NetworkImportWizardPage mainPage;
    @Override
    public boolean performFinish() {

        Job job = new Job("Load Network '" + (new File(mainPage.getFileName())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                NetworkLoader networkLoader;
                try {
                    networkLoader = new NetworkLoader(mainPage.getFileName());
                    networkLoader.run();
                    networkLoader.printStats(false);
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
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
