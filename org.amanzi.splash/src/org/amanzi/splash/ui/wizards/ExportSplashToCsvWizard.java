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

package org.amanzi.splash.ui.wizards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.splash.ui.SplashPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Node;

/**
 * <p>
 * Wizard for exporting Splash to CSV file
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class ExportSplashToCsvWizard extends Wizard implements IExportWizard {
    ExportSplashToCsvWizardPage mainPage = null;
    @Override
    public boolean performFinish() {
        String fileName = mainPage.getFileName();
        File f = new File(fileName);
        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(f));
            Node node = mainPage.getSelectedNode();
//            SplashPlugin.getDefault().getSpreadsheetService().getCell(new SpreadsheetNode(node, name), 1, 1).getValue().toString();
            buf.write("testText");
            buf.close();
        } catch (IOException e) {
//            e.printStackTrace();
            displayErrorMessage(e);
            return false;
        }finally{
            if(buf != null){
                    try {
                        buf.close();
                    } catch (IOException e) {
                        return false;
                    }
            }
        }
        return true;
    }

    @Override
    public void addPages() {
        super.addPages();
        if (mainPage == null) {
            mainPage = new ExportSplashToCsvWizardPage("mainPage");
        }
        addPage(mainPage);
    }
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Export Splash to CSV file");
    }
    
    /**
     * Displays error message instead of throwing an exception
     * 
     * @param e exception thrown
     */
    private void displayErrorMessage(final Exception e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openError(display.getActiveShell(), "Export problem", e.getMessage());
//                ErrorDialog.openError(display.getActiveShell(), "Error", "An exception occured:\n" + e.getMessage(),
//                        new Status(Status.ERROR, SplashPlugin.getId(), e.getClass().getName(), e));
            }

        });
    }

}
