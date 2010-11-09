package org.amanzi.awe.wizards.geoptima;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.amanzi.awe.wizards.geoptima.ExportDialog.NetworkExport;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.ui.wizards.CreateSpreadsheetOperation;
import org.amanzi.splash.ui.wizards.SplashNewSpreadsheetWizard;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Transaction;
import org.rubypeople.rdt.core.RubyModelException;

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

/**
 * <p>
 *Export to spreadsheet
 * </p>
 * @author Tsinkel_a
 * @since 1.0.0
 */
public class ExportToSpreadsheetWizard extends SplashNewSpreadsheetWizard{
    private final List<IExportProvider> exports;

    /**
     * @param exports
     */
    public ExportToSpreadsheetWizard(List<IExportProvider> exports) {
        this.exports = exports;
    }

    @Override
    protected void doFinish(String containerName, final String fileName, final IProgressMonitor monitor) throws CoreException {
        // create a sample file
        Transaction tx = NeoServiceProviderUi.getProvider().getService().beginTx();
        final IResource resource;
        try {
            if (monitor.isCanceled()){
                return;
            }
            monitor.beginTask("Creating " + fileName, 2);
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            resource = root.findMember(new Path(containerName));
            if (!resource.exists() || !(resource instanceof IContainer)) {
                throwCoreException("Container \"" + containerName + "\" does not exist.");
            }
            monitor.worked(1);

            monitor.setTaskName("Create and import data...");
            SpreadsheetCreator spreadsheetCreator = new SpreadsheetCreator(new Path(containerName), fileName);
            int row=0;
            for (IExportProvider exporter : exports) {
                if (monitor.isCanceled()) {
                    break;
                }
                String dataName = exporter.getDataName();
                String[] line = new String[] {dataName};
                saveLine(spreadsheetCreator,row++,line);
                line = formatList(exporter.getHeaders());
                saveLine(spreadsheetCreator,row++,line);
                while (exporter.hasNextLine() && !monitor.isCanceled()) {
                    if (exporter instanceof NetworkExport){
                        if (!dataName.equals(exporter.getDataName())){
                            dataName = exporter.getDataName();
                            line = new String[] {dataName};
                            saveLine(spreadsheetCreator,row++,line);
                            line = formatList(exporter.getHeaders());
                            saveLine(spreadsheetCreator,row++,line);                                        
                        }
                    }
                    line = formatList(exporter.getNextLine());
                    saveLine(spreadsheetCreator,row++,line);
                }
            }

             tx.success();
        } finally {
            tx.finish();
        }
            // Lagutko 20.07.2009, create Spreadsheet also for EMF structure
            try {
                final URL spreadsheetURL = NeoSplashUtil.getSpeadsheetURL(fileName);
                getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        NeoSplashUtil.openSpreadsheet(PlatformUI.getWorkbench(), spreadsheetURL, resource.getProject().getName());

                        // Lagutko, 11.08.2009, put newly created Spreadsheet to Delta
                        CreateSpreadsheetOperation op = new CreateSpreadsheetOperation((IProject)resource, fileName);
                        try {
                            op.runOperation(monitor);
                        } catch (RubyModelException e) {
                            SplashPlugin.error(null, e);
                        }
                    }
                });
            } catch (MalformedURLException e) {
                throw new CoreException(new Status(Status.ERROR, SplashPlugin.getId(), null, e));
            }
        monitor.worked(1);
    }

    private void saveLine(SpreadsheetCreator spreadsheetCreator, int row, String[] line) {
        for (int c=0;c<line.length;c++){
            Cell cellToadd = new Cell(row, c, "", line[c], null);
            spreadsheetCreator.saveCell(cellToadd);
            
        }
    }
    /**
     * Format string array depend on list.
     * 
     * @param list the list
     * @return the string[]
     */
    private String[] formatList(List list) {
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            Object obj = list.get(i);
            result[i] = null == obj ? "" : String.valueOf(obj);
        }
        return result;
    }
}
