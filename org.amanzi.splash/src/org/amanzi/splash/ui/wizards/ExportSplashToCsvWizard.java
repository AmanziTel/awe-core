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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import org.amanzi.neo.services.nodes.CellNode;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Wizard for exporting Splash to CSV file
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class ExportSplashToCsvWizard extends Wizard implements IExportWizard {
    ExportSplashToCsvWizardPage mainPage = null;
    CSVPropertyWizardPage page2 = null;
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
            buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),page2.getCharsetValue()));
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
                String vStr=String.valueOf(v);
                if (needQuote(vStr)){
                    sb.append(getQuoteChar()).append(vStr).append(getQuoteChar());
                }else{
                    sb.append(vStr);
                    
                }
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

    /**
     *
     * @param v
     * @return
     */
    private boolean needQuote(String v) {
      return !Pattern.matches("\\d*\\.{0,1}\\d*",v);
    }

    public String getQuoteChar() {
        return page2.getTextDelValue();
    }

    private int addColData(CellNode cell, StringBuilder sb, int xLastPosition) {
        int xCell = cell.getCellColumn();
        String value = String.valueOf(cell.getValue());
        if(cell.getValue() == null || StringUtils.isEmpty(value)){
            return xLastPosition;
        }
        while(xLastPosition < xCell){
            sb.append(getSeparator());
            xLastPosition++;
        }
        if (needQuote(value)){
            sb.append(getQuoteChar()).append(value.toString()).append(getQuoteChar());
        }else{
            sb.append(value.toString());
            
        }
//        sb.append(getQuoteChar()).append(cell.getValue().toString()).append(getQuoteChar());
        return xCell;
    }

    private String getSeparator() {
        return page2.getFieldDelValue();
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (mainPage == null) {
            mainPage = new ExportSplashToCsvWizardPage("mainPage");
        }
        if (page2 == null) {
            //TODO add load/store common values in memento/preference
             page2 = new CSVPropertyWizardPage("propertyCSV","UTF-8","\t","\"");
        }
        addPage(mainPage);
        addPage(page2);
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
            }

        });
    }

}
