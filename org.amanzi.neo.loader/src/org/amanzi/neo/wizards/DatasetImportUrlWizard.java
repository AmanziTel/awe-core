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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.loader.DriveLoader;
import org.amanzi.neo.loader.GPSRemoteUrlLoader;
import org.amanzi.neo.loader.LoaderUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Wizard for import dataset from url
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DatasetImportUrlWizard extends Wizard implements IImportWizard {

    /** The Constant PAGE_TITLE. */
    private static final String PAGE_TITLE = NeoLoaderPluginMessages.TemsImportWizard_PAGE_TITLE;

    /** The Constant PAGE_DESCR. */
    private static final String PAGE_DESCR = NeoLoaderPluginMessages.TemsImportWizard_PAGE_DESCR;

    /** The main page. */
    private DatasetImportUrlWizardPage mainPage;

    /** The dataset name. */
    private String datasetName;

    /** The url. */
    private URL url;

    /**
     * Perform finish.
     * 
     * @return true, if successful
     */
    @Override
    public boolean performFinish() {
        try {
            url = new URL(mainPage.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        runLoadingJob();
        return true;
    }

    /**
     * Run loading job.
     */
    private void runLoadingJob() {
        datasetName = mainPage.getDataset();
        LoadDriveJob job = new LoadDriveJob(mainPage.getControl().getDisplay());
        job.schedule(50);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new DatasetImportUrlWizardPage(PAGE_TITLE, PAGE_DESCR);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    /**
     * The Class LoadDriveJob.
     */
    private class LoadDriveJob extends Job {

        /** The job display. */
        private final Display jobDisplay;

        /**
         * Instantiates a new load drive job.
         * 
         * @param jobDisplay the job display
         */
        public LoadDriveJob(Display jobDisplay) {
            super(NeoLoaderPluginMessages.DriveDialog_MonitorName);
            this.jobDisplay = jobDisplay;
        }

        /**
         * Run.
         * 
         * @param monitor the monitor
         * @return the status
         */
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                loadDriveData(jobDisplay, monitor);
                return Status.OK_STATUS;
            } catch (Exception e) {
                e.printStackTrace();
                return Status.CANCEL_STATUS;
            }
        }

    }

    /**
     * Load drive data.
     * 
     * @param display the display
     * @param monitor the monitor
     */
    private void loadDriveData(Display display, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        DriveLoader driveLoader = null;
        try {

            driveLoader = new GPSRemoteUrlLoader(url, datasetName, display);

            driveLoader.run(monitor);
            driveLoader.printStats(false);
            driveLoader.clearCaches();
        } catch (IOException e) {
            NeoLoaderPlugin.exception(e);
        }
        handleSelect(monitor, driveLoader.getRootNodes());
        if (driveLoader != null) {
            try {
                DriveLoader.finishUpGis();
            } catch (MalformedURLException e) {
                NeoLoaderPlugin.error(e.getMessage());
            }
        }

        if (driveLoader != null) {
            driveLoader.addLayersToMap();
        }

        monitor.done();
    }

    /**
     * Handle select.
     * 
     * @param monitor the monitor
     * @param rootNodes the root nodes
     */
    protected void handleSelect(IProgressMonitor monitor, Node[] rootNodes) {
        if (monitor.isCanceled()) {
            return;
        }
        LinkedHashSet<Node> sets = LoaderUtils.getSelectedNodes(NeoServiceProvider.getProvider().getService());
        for (Node node : rootNodes) {
            sets.add(node);
        }
        LoaderUtils.storeSelectedNodes(sets);
    }
}
