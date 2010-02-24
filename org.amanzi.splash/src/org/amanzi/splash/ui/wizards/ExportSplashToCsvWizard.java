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

import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;

import org.apache.commons.lang.StringUtils;
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
        
        Node node = mainPage.getSelectedNode();
        SpreadsheetNode sn = SpreadsheetNode.fromNode(node);
        
        String toFile;
        try {
            toFile = getCsvString(sn);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }
        
        String fileName = mainPage.getFileName();
        File f = new File(fileName);
        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(f));
//            buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"UTF-8"));
            buf.write(toFile);
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

    /**
     *
     * @param sn
     */
    private String getCsvString(SpreadsheetNode node) {
        StringBuilder sb = new StringBuilder("");
//        SpreadsheetService serv = SplashPlugin.getDefault().getSpreadsheetService();
        CellNode startCell = node.getRowHeader(0);
        CellNode rowHeader = startCell;
        int yPosition = 1;
        while(rowHeader != null){
            int yCell = rowHeader.getCellRow();
            while(yPosition < yCell){
                sb.append("\n");
                yPosition++;
            }
            Object v = rowHeader.getValue();
            if(v != null){
                sb.append("\"").append(rowHeader.getValue().toString()).append("\"");
            }
            int xLastPosition = 1;
            CellNode colCell = rowHeader.getNextCellInRow();
            while(colCell != null){
                xLastPosition = addColData(colCell, sb, xLastPosition);
                colCell = colCell.getNextCellInRow(); 
            }
            rowHeader = rowHeader.getNextCellInColumn();
        }
        return sb.toString();
    }

    private int addColData(CellNode cell, StringBuilder sb, int xLastPosition) {
        int xCell = cell.getCellColumn();
        if(cell.getValue() == null || StringUtils.isEmpty(cell.getValue().toString())){
            return xLastPosition;
        }
        while(xLastPosition < xCell){
            sb.append(";");
            xLastPosition++;
        }
        sb.append("\"").append(cell.getValue().toString()).append("\"");
        return xCell;
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
