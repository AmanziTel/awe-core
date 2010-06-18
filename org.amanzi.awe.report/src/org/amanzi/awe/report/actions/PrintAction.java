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

package org.amanzi.awe.report.actions;

import java.io.File;

import org.amanzi.awe.report.ReportPlugin;
import org.amanzi.awe.report.editor.ReportEditor;
import org.amanzi.awe.report.editor.ReportGUIEditor;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Class for an action that prints report to pdf file
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class PrintAction extends Action implements IEditorActionDelegate {
    private IEditorPart editor;

    /**
     * Constructor
     */
    public PrintAction() {
        super("Print report", AbstractUIPlugin.imageDescriptorFromPlugin(ReportPlugin.PLUGIN_ID, "icons/pdf.png"));
    }

    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor instanceof ReportGUIEditor || targetEditor instanceof ReportEditor) {
            editor = targetEditor;
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void run(IAction action) {
        printReport();
    }

    @Override
    public void run() {
        printReport();
    }

    /**
     * Prints report to pdf file
     */
    private void printReport() {
        Report report = getReport();
        if (report != null) {
            PDFPrintingEngine engine = new PDFPrintingEngine();
            Shell shell = editor.getSite().getShell();
            if (report.getFile() == null) {
                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setText("Specify PDF file");
                dialog.setFilterExtensions(new String[] {"*.pdf"});
                dialog.setFilterNames(new String[] {"PDF file (*.pdf)"});
                if (dialog.open() != null) {
                    String fileName = dialog.getFilterPath() + File.separator + dialog.getFileName();
                    if (new File(fileName).exists()) {
                        MessageBox box = new MessageBox(shell, SWT.OK | SWT.CANCEL);
                        box.setText("Warning: File exists!");
                        box.setMessage("Do you want to overwrite the file '" + fileName + "'?");
                        if (box.open() == SWT.OK) {
                            report.setFile(fileName);
                        }
                    } else {
                        report.setFile(fileName);
                    }
                }
            }
            engine.printReport(report);
            MessageBox box = new MessageBox(shell, SWT.OK);
            box.setText("Information");
            box.setMessage("Report was successfully printed to pdf file\n'" + report.getFile() + "'!");
            box.open();
        }
    }

    /**
     * Obtains report from the editor
     * 
     * @return report
     */
    private Report getReport() {
        if (editor instanceof ReportGUIEditor) {
            return ((ReportGUIEditor)editor).getReportModel().getReport();
        } else if (editor instanceof ReportEditor) {
            return ((ReportEditor)editor).getReport();
        }
        return null;
    }
}