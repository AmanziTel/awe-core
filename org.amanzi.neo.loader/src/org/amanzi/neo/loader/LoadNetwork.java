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
package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.NetworkFileType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * This class launches the network loading, by first asking the user for a file, and then scheduling
 * a job to actually load the file. This class can also be used to support toolbar actions, both
 * standard eclipse actions using a handler like LoadNetworkhandler which specifically calls the
 * run() method to load the network, or through the fact that the run() method is actually an
 * implementation of AbstractActionTool so this can be used as a uDIG specific toolbar action on
 * open maps. To remove this secondary feature, simply remove the 'extends AbstractActionTool' part
 * of the class definition.
 * 
 * @author craig
 * @since 1.0.0
 */
public class LoadNetwork extends AbstractActionTool {
    /*
     * Names of supported files for Network
     */
    public static final String[] NETWORK_FILE_NAMES = {
        "Comma Separated Values Files (*.csv)",
        "Plain Text Files (*.txt)",
        "OpenOffice.org Spreadsheet Files (*.sxc)",
        "Microsoft Excel Spreadsheet Files (*.xls)",
        "All Files (*.*)" };
    
    /*
     * Extensions of supported files for Network
     */
    public static final String[] NETWORK_FILE_EXTENSIONS = {"*.csv", "*.txt", "*.sxc", "*.xls", "*.*"};

    // private static String directory = null;

    private final Display display;

	public LoadNetwork() {
        display = null;
	}

    public LoadNetwork(Display display) {
        this.display = display;
    }

    public static String getDirectory() {
        return NeoLoaderPlugin.getDefault().getPluginPreferences().getString(AbstractLoader.DEFAULT_DIRRECTORY_LOADER);
    }
	
	/**
	 * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @param newDirectory new default directory
	 * @author Lagutko_N
	 */
	
	public static void setDirectory(String newDirectory) {
        NeoLoaderPlugin.getDefault().getPluginPreferences().setValue(AbstractLoader.DEFAULT_DIRRECTORY_LOADER, newDirectory);
	}
	
    /**
     * Run from action handler
     */
    public void run() {
        final FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
        dlg.setText(NeoLoaderPluginMessages.NetworkDialog_DialogTitle);
        dlg.setFilterNames(NETWORK_FILE_NAMES);
        dlg.setFilterExtensions(NETWORK_FILE_EXTENSIONS);
        dlg.setFilterPath(getDirectory());
        final String filename = dlg.open();
        if (filename != null) {
            setDirectory(dlg.getFilterPath());
            Job job = new Job("Load Network '" + (new File(filename)).getName() + "'") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    NetworkLoader networkLoader;
                    try {
                        NeoUtils.checkTransactionOnThread(null, "load1");
                        // TODO refactor
                        if (NetworkFileType.RADIO_SECTOR == LoaderUtils.getFileType(filename).getLeft()) {
                            networkLoader = new NetworkLoader(new File(filename).getName(),filename, dlg.getParent().getDisplay());
                            networkLoader.setup();
                            SubMonitor monitor2 = SubMonitor.convert(monitor, 100);
                            networkLoader.run(monitor2);
                            networkLoader.printStats(false);
                            NetworkLoader.addDataToCatalog();                            
                            networkLoader.addLayersToMap();
                        } 
                        else {
                            NeoUtils.checkTransactionOnThread(null, "load2");
                            ProbeLoader loader = new ProbeLoader(new File(filename).getName(),filename, display);
                            NeoUtils.checkTransactionOnThread(null, "load3");
                            loader.run(monitor);
                            loader.addLayersToMap();
                        }
                    } catch (IOException e) {
                        NeoCorePlugin.error("Error loading Network file", e);
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule(50);
        }
    }
	public void dispose() {
	}

}
