package org.amanzi.awe.wizards.geoptima;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.amanzi.awe.gps.GPSCorrelator;
import org.amanzi.awe.wizards.pages.SelectCorrelationDataPage;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.events.NewCorrelationEvent;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Node;

public class GeoptimaWizard extends Wizard implements INewWizard, IWizard {

    private SelectCorrelationDataPage page;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPages() {
        page = new SelectCorrelationDataPage("Select");
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        final Node network = page.getNetworkGISNode();
        final Node gps = page.getGPSGisNode();
        final Node oss = page.getOSSNode();
        final Node gpeh = page.getGPEHGisNode();

        try {
            getContainer().run(true, false, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    GPSCorrelator correlator = new GPSCorrelator(network, monitor);

                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            IViewPart reuseView;
                            try {
                                reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                                        "org.amanzi.awe.views.drive.views.CorrelationList");
                                NewCorrelationEvent ncEvent = new NewCorrelationEvent(network, gps != null ? gps : (oss != null ? oss : gpeh));
                                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(ncEvent);
                            } catch (PartInitException e) {
                                e.printStackTrace();
                            }
                        }
                    }, true);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
