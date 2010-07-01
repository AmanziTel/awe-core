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

import java.util.Set;

import org.amanzi.awe.neighbours.gpeh.GpehReportCreator;
import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.splash.utilities.NeoSplashUtil;
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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHReportWizard extends Wizard implements INewWizard {

    private GPEHReportWizardPage firstPage;
    private GPEHReportWizardPage2 secondPage;

    @Override
    public void addPages() {
        super.addPages();
        addPage(firstPage);
        addPage(secondPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        firstPage = new GPEHReportWizardPage("ossPage1");
        secondPage = new GPEHReportWizardPage2("ossPage2");
        // setWindowTitle(NeoLoaderPluginMessages.GpehWindowTitle);
        // display = workbench.getDisplay();
    }

    @Override
    public boolean performFinish() {
        final Node gpehNode = firstPage.getGpehNode();
        final Node netNode = firstPage.getNetworkNode();
        final Set<GpehReportType> repTypes = secondPage.getReportType();
        final CallTimePeriods period = secondPage.getPeriod();
        Job job = new Job("generate Report") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                for (GpehReportType type : repTypes) {
                    createReport(gpehNode, netNode, type, period, monitor);
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
     * @param monitor the monitor
     */
    protected void createReport(Node gpehNode, Node netNode, GpehReportType repType, CallTimePeriods period, IProgressMonitor monitor) {
        GpehReportCreator creator = new GpehReportCreator(netNode, gpehNode, NeoServiceProvider.getProvider().getService(), NeoServiceProvider.getProvider().getIndexService());
        creator.setMonitor(monitor);
        creator.createMatrix();

        final SpreadsheetNode spreadsheet;
        switch (repType) {
        case UE_TX_POWER_ANALYSIS:
            creator.createUeTxPowerCellReport(period);
            spreadsheet = creator.createUeTxPowerCellSpreadSheet("UxTxPower", period);
            return;
        case IDCM_INTRA:
            spreadsheet = creator.createIntraIDCMSpreadSheet("IntraMatrix");
            break;
        case IDCM_INTER:
            spreadsheet = creator.createInterIDCMSpreadSheet("InterMatrix");
            break;
        case CELL_RSCP_ANALYSIS:
            // TODO remove after implementing and testing
            // if (true)return;
            creator.createRSCPCellReport(period);
            spreadsheet = creator.createRSCPCellSpreadSheet("RSCPCell", period);
            return;
        case CELL_ECNO_ANALYSIS:
            // TODO remove after implementing and testing
            // if (true)return;
            creator.createEcNoCellReport(period);
            spreadsheet = creator.createEcNoCellSpreadSheet("RSCPCell", period);
            return;
        default:
            return;
            // break;
        }
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                NeoSplashUtil.openSpreadsheet(spreadsheet);
            }
        }, true);
    }
}
