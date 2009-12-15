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

import java.util.ArrayList;

import org.amanzi.neo.core.utils.Pair;
import org.amanzi.splash.report.IReportModelListener;
import org.amanzi.splash.report.IReportPart;
import org.amanzi.splash.report.ReportModelEvent;
import org.amanzi.splash.report.ReportPartType;
import org.amanzi.splash.report.model.Chart;
import org.amanzi.splash.report.model.Report;
import org.amanzi.splash.report.model.ReportModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.NewVariableEntryDialog;

/**
 * Report editor class
 * 
 * @author Pechko E.
 * @since 1.0.0
 */
public class ReportEditor extends MultiPageEditorPart implements IReportModelListener {

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
        reportModel.addReportListener(this);
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
            // textEditor.getAction("Format").run();//TODO
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
            String scriptText = getText();
            reportModel.updateModel(scriptText);
            guiEditor.repaint();
        } else {
            if (guiEditor.isReportDataModified()) {
                // setText(reportModel.getReport().getScript());
                textEditor.getAction("Format").run();
                guiEditor.setReportDataModified(false);
            }
        }
    }

    public String getText() {
        return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
    }

    public void setText(String text) {
        textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).set(text);
    }

    @Override
    public void reportChanged(ReportModelEvent event) {
        System.out.println("reportChanged: " + event);
        ArrayList<String> parts = extractPartsFromScript(getText());
        StringBuffer sb = new StringBuffer();
        IReportPart source = (IReportPart)event.getSource();
        final int index = source.getIndex() + 1;
        switch (event.getType()) {
        case PART_ADDED:
            guiEditor.createCompositeForPart(source);
            guiEditor.forceRepaint();
            parts.add(source.getIndex(), (String)event.getData());
            generateScriptFromParts(parts, sb);
            break;
        case PART_MOVED_DOWN:
            swapParts(parts, sb, source.getIndex() - 1, source.getIndex());
            break;
        case PART_MOVED_UP:
            swapParts(parts, sb, source.getIndex(), index);
            break;
        case PART_REMOVED:
            parts.remove(index);
            generateScriptFromParts(parts, sb);
            break;
        case PROPERTY_CHANGED:
            Pair<String, String> pair = (Pair<String, String>)event.getData();
            String part = parts.get(index);
            String closure = "\\s*(" + ReportPartType.getTypesAsRegex() + ")(.|\\s)*do(.|\\s)*\\s*end\\s*";
            String hash = "\\s*(" + ReportPartType.getTypesAsRegex() + ")\\s*'\\w*',(\\s*\\w*\\=\\>['|:]?\\w*'?,?)*";
            if (pair.l().equals(Report.FIRST_ARGUMENT)) {
                ReportPartType type = source.getType();
                System.out.println("Part: " + part+"\n New value: "+pair.r());
                switch (type){
                case TEXT:
                case IMAGE:
                    parts.set(index, type.getText() + " '" + pair.r() + "'\n");
                    break;
                    default:
                        parts.set(index, part.replaceAll(type.getText() + "\\s*'[\\w|\\s]*'", type.getText() + " '" + pair.r() + "'"));
                }
            } else if (part.matches(closure)) {
                System.out.println("Part matches closure syntax: " + part);
                String oldString = new StringBuffer("self\\.").append(pair.l()).append("=[':]*\\w*[']*").toString();
                String newString = new StringBuffer("self\\.").append(pair.l()).append("=").append(pair.r()).toString();
                parts.set(index, part.replaceAll(oldString, newString));
            } else if (part.matches(hash)) {
                System.out.println("Part matches hash syntax: " + part);
                String oldString = new StringBuffer(pair.l()).append("\\=\\>['|:]?\\w*'?").toString();
                String newString = new StringBuffer(pair.l()).append("=>").append(pair.r()).toString();
                parts.set(index, part.replaceAll(oldString, newString));
            }

            generateScriptFromParts(parts, sb);
            break;
        }
        System.out.println("===> New script: " + sb.toString());
        setText(sb.toString());
    }

    /**
     * Generates the new report script text from the script parts
     * 
     * @param parts - script parts
     * @param sb - string buffer that will contain new script text
     */
    private void generateScriptFromParts(ArrayList<String> parts, StringBuffer sb) {
        for (int i = 0; i < parts.size(); i++) {
            sb.append(parts.get(i));
        }
    }

    /**
     * Swaps given script parts and write new script to the string buffer given
     * 
     * @param parts - the script parts
     * @param sb - string buffer to be filled
     * @param ind1 - index of the first part to be swapped
     * @param ind2 - index of the second part to be swapped
     */
    private void swapParts(ArrayList<String> parts, StringBuffer sb, int ind1, int ind2) {
        // convert the report part indices to script part indices
        // script part #0 - contains report title and settings
        // script parts #1..parts.size-2 - contain script text for every report part
        // script part #parts.size-1 - contains the keyword 'end'
        int index1 = ind1 + 1;
        int index2 = ind2 + 1;
        System.out.println("parts[" + index1 + "]: " + parts.get(index1));
        System.out.println("parts[" + index2 + "]: " + parts.get(index2));
        for (int i = 0; i < index1; i++) {
            sb.append(parts.get(i));
        }
        sb.append(parts.get(index2));
        for (int i = index1 + 1; i < index2; i++) {
            sb.append(parts.get(i));
        }
        sb.append(parts.get(index1));
        for (int i = index2 + 1; i < parts.size(); i++) {
            sb.append(parts.get(i));
        }
    }

    /**
     * Extracts parts from the script given
     * 
     * @param script - the script text to be processed
     * @return list of extracted parts
     */
    private ArrayList<String> extractPartsFromScript(String script) {
        int last_end_index = script.lastIndexOf("end");
        String before_parts = script.substring(0, last_end_index);
        String after_parts = script.substring(last_end_index);
        ArrayList<Integer> positions = new ArrayList<Integer>();
        String[] lines = before_parts.split("\n");
        int ind = 0;
        String PATTERN = new StringBuffer("\\s*(").append(ReportPartType.getTypesAsRegex()).append(")(.*)").toString();
        // System.out.println(" PATTERN: "+PATTERN);
        while (ind < lines.length) {
            if (lines[ind].matches(PATTERN)) {
                // System.out.println(" +++> "+lines[ind]);
                positions.add(ind);
                ind++;
            }
            while (ind < lines.length && !lines[ind].matches(PATTERN)) {
                // System.out.println(" ---> "+lines[ind]);
                ind++;
            }
        }
        ArrayList<String> parts = new ArrayList<String>(positions.size() + 2);
        addLines(positions, lines, parts, 0, positions.get(0));
        for (int i = 0; i < positions.size() - 1; i++) {
            addLines(positions, lines, parts, positions.get(i), positions.get(i + 1));
        }
        addLines(positions, lines, parts, positions.get(positions.size() - 1), lines.length);
        parts.add(after_parts);
        return parts;
    }

    /**
     * Adds lines from start index to end index to parts
     * 
     * @param positions - list of the line numbers where report part were found
     * @param lines - list of all report lines
     * @param parts - script parts
     * @param start - start index
     * @param end - end index
     */
    private void addLines(ArrayList<Integer> positions, String[] lines, ArrayList<String> parts, int start, int end) {
        StringBuffer buffer = new StringBuffer();
        for (int j = start; j < end; j++) {
            buffer.append(lines[j]).append("\n");
        }
        parts.add(buffer.toString());
    }

}
