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
import java.util.LinkedHashSet;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.NetworkLoader;
import org.amanzi.neo.loader.NetworkSiteLoader;
import org.amanzi.neo.loader.NokiaTopologyLoader;
import org.amanzi.neo.loader.ProbeLoader;
import org.amanzi.neo.loader.TransmissionLoader;
import org.amanzi.neo.loader.UTRANLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.enums.NetworkFileType;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Network import wizard
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkSiteImportWizard extends Wizard implements IImportWizard {

    /** The Constant PAGE_TITLE. */
    private static final String PAGE_TITLE = NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_TITLE;
    
    /** The Constant PAGE_DESCR. */
    private static final String PAGE_DESCR = NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR;
    
    /** The main page. */
    private NetworkSiteImportWizardPage mainPage;
    
    /** The display. */
    private Display display;
    
    /** The add to select. */
    private boolean addToSelect;

    /**
     * Perform finish.
     *
     * @return true, if successful
     */
    @Override
    public boolean performFinish() {
        Job job = new Job("Load Network Site'" + (new File(mainPage.getFileName())).getName() + "'") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                NetworkFileType fileType = mainPage.getFileType();
                try {
                    switch (fileType) {
                    case RADIO_SITE:
                        NetworkSiteLoader networkSiteLoader = new NetworkSiteLoader(mainPage.getNetworkName(), mainPage.getFileName(), display);
                        networkSiteLoader.run(monitor);
                        networkSiteLoader.printStats(false);
                        handleSelect(monitor,networkSiteLoader.getRootNodes());
                        NetworkSiteLoader.sendUpdateEvent(UpdateViewEventType.GIS);
                        break;
                    case RADIO_SECTOR:
                        NetworkLoader networkLoader = new NetworkLoader(mainPage.getNetworkName(), mainPage.getFileName(), display);
                        networkLoader.setup();
                        SubMonitor monitor2 = SubMonitor.convert(monitor, 100);
                        networkLoader.run(monitor2);
                        networkLoader.printStats(false);
                        NetworkLoader.addDataToCatalog();
                        networkLoader.addLayersToMap();
                        handleSelect(monitor,networkLoader.getRootNodes());
                        break;
                    case PROBE:
                        ProbeLoader loader = new ProbeLoader(mainPage.getNetworkName(), mainPage.getFileName(), display);
                        loader.run(monitor);
                        NetworkLoader.addDataToCatalog();
                        loader.addLayersToMap();      
                        handleSelect(monitor,loader.getRootNodes());
                        break;
                    case INTERFERENCE:
                    case NEIGHBOUR:
                        NeighbourLoader neighbourLoader;
                        neighbourLoader = new NeighbourLoader(mainPage.getNetworkNode(), mainPage.getFileName(), fileType == NetworkFileType.INTERFERENCE);
                        neighbourLoader.run(monitor);
                        NeoServiceProviderUi.getProvider().commit();
                        break;
                    case TRANSMISSION:
                        TransmissionLoader transmissionLoader;
                        transmissionLoader = new TransmissionLoader(mainPage.getNetworkName(), mainPage.getFileName(), NeoServiceProviderUi.getProvider().getService());
                        transmissionLoader.run(monitor);
                        NeoServiceProviderUi.getProvider().commit();
                        break;
                    case UTRAN:
                        UTRANLoader utranLoader;
                        utranLoader = new UTRANLoader( mainPage.getFileName(),mainPage.getNetworkName(),display);
                        utranLoader.run(monitor);
                        NeoServiceProviderUi.getProvider().commit(); 
                        utranLoader.addLayersToMap();
                        handleSelect(monitor,utranLoader.getRootNodes());
                        break;
                    case NOKIA_TOPOLOGY:
                        NokiaTopologyLoader nokiaTopologyLoader;
                        nokiaTopologyLoader = new NokiaTopologyLoader( mainPage.getFileName(),mainPage.getNetworkName(),display);
                        nokiaTopologyLoader.run(monitor);
                        NeoServiceProviderUi.getProvider().commit(); 
                        nokiaTopologyLoader.addLayersToMap();
                        handleSelect(monitor,nokiaTopologyLoader.getRootNodes());
                        break;
                    default:
                        break;

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


    /**
     * Handle select.
     *
     * @param monitor the monitor
     * @param rootNodes the root nodes
     */
    protected void handleSelect(IProgressMonitor monitor, Node[] rootNodes) {
        if (!addToSelect||monitor.isCanceled()){
            return;
        }
        LinkedHashSet<Node> sets = LoaderUiUtils.getSelectedNodes(NeoServiceProviderUi.getProvider().getService());
        for (Node node : rootNodes) {
            sets.add(node);
        }
        LoaderUiUtils.storeSelectedNodes(sets);
    }

    /**
     * Inits.
     *
     * @param workbench the workbench
     * @param selection the selection
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new NetworkSiteImportWizardPage(PAGE_TITLE, PAGE_DESCR);
        setWindowTitle(PAGE_TITLE);
        display = workbench.getDisplay();
    }

    /**
     * Adds the pages.
     */
    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    /**
     * Adds the to select param.
     *
     * @param addToSelect the add to select
     */
    public void addToSelectParam(String addToSelect) {
        this.addToSelect = addToSelect != null && "true".equalsIgnoreCase(addToSelect);
    }
}
