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

package org.amanzi.splash.ui;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.splash.swing.Cell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Component for editing Cell definition that is synchronized with CellEditor
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class FormulaEditor {
    
    /**
     * Listener for Input events of this Editor
     *  
     * @author Lagutko_N
     * @since 1.0.0
     */
    private class InputFinishListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.keyCode) {
            case SWT.CR:
                if ((e.stateMask & SWT.CTRL) != 0) {
                    //if pressed key is Ctrl+Enter than stop editing
                    stopCellEditing(true);
                    e.doit = false;
                }
                break;
            case SWT.ESC: 
                cancelCellEditing();
                break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.keyCode != SWT.CR) {
                printToCell();
                e.doit = false;
            }
        }
        
    }
    
    /**
     * Listener that handle events that occur resizing of Editor
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
    private class TextResizer implements ModifyListener, FocusListener {
        
        private int lineNumber = 1;
        
        @Override
        public void modifyText(ModifyEvent e) {
            if (e.getSource().equals(formulaTextEditor)) {
                if (lineNumber != formulaTextEditor.getLineCount()) {
                    lineNumber = formulaTextEditor.getLineCount();
                    showFormulaEditor(lineNumber);
                }                
            }   
        }

        @Override
        public void focusGained(FocusEvent e) {
            System.out.println("focusGained " + lineNumber);
            showFormulaEditor(lineNumber);
        }

        @Override
        public void focusLost(FocusEvent e) {
            lineNumber = 1;
            collapseFormulaEditor();
        }
    }
    
    /*
     * Row of the currently edited Cell
     */
    private int currentRow;
    /*
     * Column of the currently edited Cell
     */
    private int currentColumn;
    /*
     * Editor Component of currently edited Cell
     */
    private JTextField currentCellEditorComponent = null;
    /*
     * Table for which FormulaEditor was created
     */
    private JTable table;
    /*
     * Cell Editor of currently edited Cell
     */
    private CellEditor currentCellEditor = null;
    /*
     * An SWT-based Editor for editing of Cell
     */
    private Text formulaTextEditor;
    /*
     * An instance of Resizer 
     */
    private TextResizer resizer;
    
    /**
     * Creates a FormulaEditor on the parent with given Layout Data and registers listeners
     * 
     * @param parent parent component
     * @param layoutData layout data
     */
    public FormulaEditor(Composite parent, GridData layoutData) {
        formulaTextEditor = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        resizer = new TextResizer();        
        
        formulaTextEditor.setLayoutData(layoutData);
        formulaTextEditor.addKeyListener(new InputFinishListener());
        formulaTextEditor.addModifyListener(resizer);
        formulaTextEditor.addFocusListener(resizer);
    }
    
    /**
     * Sets a Table for this Formula Editor
     *
     * @param table
     */
    public void setTable(JTable table) {
        this.table = table;
    }
    
    /**
     * Sets a CellEditor Component
     *
     * @param editor a Component of CellEditor
     */
    public void setCellEditorComponent(JTextField editor) {
        currentCellEditorComponent = editor;
    }
    
    /**
     * Sets a CellEditor
     *
     * @param editor CellEditor
     */
    public void setCellEditor(CellEditor editor) {
        currentCellEditor = editor;
    }
    
    /**
     * Stops CellEditing within FormulaEditor and provides this to Editor of Cell 
     */
    public void stopCellEditing(boolean fromEditor) {
        if ((currentCellEditor != null) && fromEditor) {
            currentCellEditor.stopCellEditing();
        }
        
        ActionUtil.getInstance().runTask(new Runnable(){
            
            @Override
            public void run() {
                collapseFormulaEditor();
            }
        }, false);
        
        currentCellEditorComponent = null;
    }
    
    /**
     * Cancels CellEditing within FormulaEditor and provides this to Editor of Cell
     */
    public void cancelCellEditing() {
        ActionUtil.getInstance().runTask(new Runnable(){
            
            @Override
            public void run() {
                collapseFormulaEditor();
            }
        }, false);
        
        if (currentCellEditor != null) {
            currentCellEditor.cancelCellEditing();
        }
        
        setText(currentRow, currentColumn);
    }
    
    /**
     * Prints a new content to Cell's Editor
     */
    private void printToCell() {
        if (currentCellEditor == null) {
            //if there are no CellEditor than we should start Editing of Cell
            table.editCellAt(currentRow, currentColumn);
        }
        if (currentCellEditorComponent != null) {
            currentCellEditorComponent.setText(formulaTextEditor.getText());
        }
    }

    /**
     * Collapses FormulaEditor 
     */
    private void collapseFormulaEditor() {
        int width = formulaTextEditor.getSize().x;
        GC gc = new GC(formulaTextEditor);
        FontMetrics fm = gc.getFontMetrics();
        int height = fm.getHeight();
        gc.dispose();
        formulaTextEditor.setSize(formulaTextEditor.computeSize(width, height));
        formulaTextEditor.redraw();        
    }
    
    /**
     * Shows FormulaEditor
     *
     * @param lineNumber number of line in Formula Editor's text
     */
    private void showFormulaEditor(int lineNumber) {
        //TODO: Lagutko, method computeSize() didn't computes width correctly, so compute it manually
        System.out.println("showFormula " + lineNumber);
        int width = formulaTextEditor.getClientArea().width + formulaTextEditor.getBorderWidth() * 2;
        GC gc = new GC(formulaTextEditor);
        FontMetrics fm = gc.getFontMetrics();
        int height = lineNumber * fm.getHeight();
        gc.dispose();
        formulaTextEditor.setSize(width, formulaTextEditor.computeSize(width, height).y);        
        formulaTextEditor.redraw();
    }
    
    /**
     * Sets a new text for FormulaEditor from Cell by Row and Column.
     * This method should be used for initial settings of text (for example when a Cell was selected).
     *
     * @param newText text of cell
     * @param row row of cell
     * @param column column of cell
     */
    public void setText(int row, int column) {
        currentRow = row;
        currentColumn = column;
        currentCellEditorComponent = null;
        currentCellEditor = null;
        
        Cell cell = (Cell)table.getValueAt(row, column);
        final String newText = cell.getDefinition().replaceAll("\r", "\n");
        
        ActionUtil.getInstance().runTask(new Runnable(){
            
            @Override
            public void run() {
                formulaTextEditor.setText(newText);
                formulaTextEditor.setSelection(3, 3);
                
            }
        }, false);
    }
    
    /**
     * Sets the text of Formula Editor
     *
     * @param newText new text for editor
     */
    
    public void setText(final String newText) {
        ActionUtil.getInstance().runTask(new Runnable(){
            
            @Override
            public void run() {
                formulaTextEditor.setText(newText);
            }
        }, false);
    }
    
    
}
