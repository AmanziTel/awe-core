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

package org.amanzi.splash.editors;

import org.amanzi.splash.report.model.ReportModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

/**
 * Report editor class
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportEditor extends MultiPageEditorPart {

    private RubyEditor textEditor;
    private ReportGUIEditor guiEditor;
    private ReportModel reportModel;
    private static final int GUI_PAGE_INDEX = 0;

    @Override
    protected void createPages() {
        IEditorInput fei = getEditorInput();
        setPartName(fei.getName());
        String rubyProjectName = ((FileEditorInput)fei).getFile().getParent().getProject().getName();
        reportModel = new ReportModel(rubyProjectName);

        createGUIPage();
        createSourcePage();
    }

    /**
     * Creates GUI page
     */
    private void createGUIPage() {
        guiEditor = new ReportGUIEditor();
        guiEditor.setReportModel(reportModel);

        int index;
        try {
            index = addPage(guiEditor, getEditorInput());
            setPageText(index, "GUI");
        } catch (PartInitException e) {
            // TODO Handle PartInitException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Creates Source page
     */
    private void createSourcePage() {
        try {
            textEditor = new RubyEditor();
            int index = addPage(textEditor, getEditorInput());
//            textEditor.getAction("Format").run();//TODO
            setPageText(index, "Source");
        } catch (PartInitException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        textEditor.doSave(monitor);
    }

    @Override
    public void doSaveAs() {
        textEditor.doSaveAs();
        setInput(textEditor.getEditorInput());
        updateTitle();

    }

    private void updateTitle() {
        IEditorInput input = getEditorInput();
        setPartName(input.getName());
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (newPageIndex == GUI_PAGE_INDEX) {
            String scriptText = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
            reportModel.updateModel(scriptText);
            guiEditor.repaint();
        } else {
            if (guiEditor.isReportDataModified()) {
                textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).set(reportModel.getReport().getScript());
                textEditor.getAction("Format").run();
                guiEditor.setReportDataModified(false);
            }
        }
    }

}
