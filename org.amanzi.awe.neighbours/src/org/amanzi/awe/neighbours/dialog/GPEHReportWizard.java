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

package org.amanzi.awe.neighbours.dialog;

import java.io.File;
import java.util.Set;

import org.amanzi.awe.neighbours.gpeh.GpehReportCreator;
import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Wizard for generation GPEHreports
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizard extends Wizard implements INewWizard {

    private GPEHReportWizardPage firstPage;
    private GPEHReportWizardPage2 secondPage;
    private GPEHReportWizardPage3 thirdPage;

    @Override
    public void addPages() {
        super.addPages();
        addPage(firstPage);
        addPage(secondPage);
        addPage(thirdPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        firstPage = new GPEHReportWizardPage("GPEH Report Wizard", "Select GPEH and Network");
        secondPage = new GPEHReportWizardPage2("GPEH Report Wizard", "Select report type and period if needed");
        thirdPage = new GPEHReportWizardPage3("GPEH Report Wizard", "Select target directory");

        setWindowTitle("GPEH Report");
        // setWindowTitle(NeoLoaderPluginMessages.GpehWindowTitle);
        // display = workbench.getDisplay();
    }

    @Override
    public boolean performFinish() {
        final Node gpehNode = firstPage.getGpehNode();
        final Node netNode = firstPage.getNetworkNode();
        final Set<GpehReportType> repTypes = secondPage.getReportType();
        final CallTimePeriods period = secondPage.getPeriod();
        final File targetDir = thirdPage.getTargetDir();
        Job job = new Job("generate Report") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                GpehReportCreator creator = new GpehReportCreator(netNode, gpehNode, NeoServiceProvider.getProvider().getService(), NeoServiceProvider.getProvider()
                        .getIndexService());
                creator.setMonitor(monitor);
                for (GpehReportType type : repTypes) {
                    creator.exportToCSV(targetDir, type, period, monitor);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();

        return true;
    }

    /**
     * Creates the report.
     * 
     * @param gpehNode the gpeh node
     * @param netNode the net node
     * @param repType report type
     * @param period
     * @param targetDir
     * @param monitor the monitor
     */
    protected void createReport(Node gpehNode, Node netNode, GpehReportType repType, CallTimePeriods period, File targetDir, IProgressMonitor monitor) {
        GpehReportCreator creator = new GpehReportCreator(netNode, gpehNode, NeoServiceProvider.getProvider().getService(), NeoServiceProvider.getProvider()
                .getIndexService());
        creator.setMonitor(monitor);
        // TODO implementation
    }
}
