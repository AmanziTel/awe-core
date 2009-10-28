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
package org.amanzi.splash.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Transaction;

import com.eteks.openjeks.format.CellFormat;

public class SplashTable extends JTable {
	/*
	 * Row Header
	 */
	public JTable rowHeader;

	/*
	 * Default Width of Column
	 */
	private int defaultColumnWidth = 150;

	/*
	 * Default Height of Row
	 */
	private int defaultRowHeight = 20;

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1232338822L;

	protected static final String ERROR_TITLE = "ERROR_TITLE";

	protected static final String ERROR_MSG = "ERROR_MSG";

	/*
	 * Editor of Cell
	 */
	private TableCellEditor editor;

	/*
	 * Id of Spreadsheet
	 */
	private String splashName = "";

	/*
	 * Root Node for current Spreadsheet
	 */
	private RubyProjectNode root;

	/**
	 * Initialize table with 500x200
	 * 
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param root
	 *            Root node of Spreadsheet
	 */
	public SplashTable(String splash_name, RubyProjectNode root) throws IOException {
		this(Short.MAX_VALUE, Short.MAX_VALUE, splash_name, root);

	}

	/**
	 * called by previous function to set model
	 * 
	 * @param rowCount
	 *            number of Rows
	 * @param columnCount
	 *            number of Columns
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param root
	 *            root node of Spreadsheet
	 */
	public SplashTable(int rowCount, int columnCount, String splash_name, RubyProjectNode root) throws IOException {
		super();

		splashName = splash_name;
		this.root = root;

		setModel(new SplashTableModel(rowCount, columnCount, splashName, root));

		editor = new SplashCellEditor();

		setDefaultRenderer(Cell.class, new SplashCellRenderer());		

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setRowSelectionAllowed(false);
		setCellSelectionEnabled(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// --------------------------------------------------------------------
		// Row Header Renderer
		// --------------------------------------------------------------------
		RowHeaderRenderer _RowHeaderRenderer = new RowHeaderRenderer();
		rowHeader = new JTable(getModel().getRowCount(), 1);
		rowHeader.setIntercellSpacing(new Dimension(0, 0));
		Dimension d = rowHeader.getPreferredScrollableViewportSize();
		d.width = rowHeader.getPreferredSize().width / 2;
		rowHeader.setPreferredScrollableViewportSize(d);
		rowHeader.setDefaultRenderer(Object.class, _RowHeaderRenderer);
		rowHeader.setRowHeight(getDefaultRowHeight());
        rowHeader.setModel(new RowModel(getModel()));

	}

	/**
	 * Called by previous function to define the parser
	 * 
	 * @param model
	 *            TalbeModel
	 * @param editable
	 *            is table editable
	 */
	public SplashTable(TableModel model, boolean editable) {
		super();

		// Set model afterwards because expressionParser needs to be set,
		// thus addColumn () and getColumnName () can work and columns can be
		// created
		setModel(model);

		if (editable) {
			editor = new SplashCellEditor();
		}

		setDefaultRenderer(Cell.class, new SplashCellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setRowSelectionAllowed(false);
		setCellSelectionEnabled(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// --------------------------------------------------------------------
		// Row Header Renderer
		// --------------------------------------------------------------------
		RowHeaderRenderer _RowHeaderRenderer = new RowHeaderRenderer();
		rowHeader = new JTable(new RowModel(getModel()));
		rowHeader.setIntercellSpacing(new Dimension(0, 0));
		Dimension d = rowHeader.getPreferredScrollableViewportSize();
		d.width = rowHeader.getPreferredSize().width / 2;
		rowHeader.setPreferredScrollableViewportSize(d);
		// rowHeader.setRowHeight(getRowHeight());
		rowHeader.setDefaultRenderer(Object.class, _RowHeaderRenderer);
		rowHeader.setRowHeight(getDefaultRowHeight());
        rowHeader.setModel(new RowModel(getModel()));
	}

	/**
	 * Get cell renderer object
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return CellRenderer by given row and column
	 */
	public TableCellRenderer getCellRenderer(int row, int column) {
		return getDefaultRenderer(Cell.class);		
	}

	/**
	 * Get Cell Editor object
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return cell editor by given row and column
	 */
	public TableCellEditor getCellEditor(int row, int column) {
		if (editor != null) {
			return editor;
		} else {
			return super.getCellEditor(row, column);
		}
	}

	/**
	 * Get column name
	 * 
	 * @param column
	 * @return name of column
	 */
	public String getColumnName(int column) {
		return super.getColumnName(column);
	}	

	/**
	 * move row down
	 * 
	 * @param index
	 *            index of Row to move
	 */
	public void moveRowDown(final int index) {
		int rowCount = getRowCount();
		if (index >= rowCount - 1)
			return;
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().swapRows(spreadsheet, index, index + 1);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * move row up
	 * 
	 * @param index
	 *            index of row to move
	 */
	public void moveRowUp(final int index) {
		if (index < 1)
			return;
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().swapRows(spreadsheet, index, index - 1);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * move column contents to right
	 * 
	 * @param index
	 *            index of column to move
	 */
	public void moveColumnRight(final int index) {
		int columnCount = getColumnCount();
		if (index > columnCount - 1)
			return;
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().swapColumns(spreadsheet, index, index + 1);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * move column contents to left
	 * 
	 * @param index
	 *            index of column to move
	 */
	public void moveColumnLeft(final int index) {
		if (index < 1)
			return;

		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().swapColumns(spreadsheet, index, index - 1);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * Adds a new Row
	 */
	@SuppressWarnings("unused")
	private void addRow() {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel oldModel = (SplashTableModel)getModel();
		
		SplashTableModel newModel = new SplashTableModel(rowCount + 1, columnCount, splashName, oldModel.getEngine(), root);

		for (int i = 0; i < rowCount; i++)
			for (int j = 0; j < columnCount; j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);

	}

	/**
	 * Adds a new Column
	 */
	@SuppressWarnings("unused")
	private void addColumn() {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

SplashTableModel oldModel = (SplashTableModel)getModel();
        
        SplashTableModel newModel = new SplashTableModel(rowCount + 1, columnCount, splashName, oldModel.getEngine(), root);

		for (int i = 0; i < rowCount; i++)
			for (int j = 0; j < columnCount; j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * delete a complete row
	 * 
	 * @param index
	 *            of Row
	 */
	// TODO: Lagutko: deleted only from TableModel but not from Database
	public void deleteRow(final int index) {
		// int rowCount = getRowCount();
		// int columnCount = getColumnCount();
		//
		// SplashTableModel newModel = new SplashTableModel(rowCount - 1,
		// columnCount, splashName, root);
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		final Boolean result = (Boolean) ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {

			private Boolean result;

			public Object getValue() {
				return result;
			}

			public void run() {
				result = SplashPlugin.getDefault().getSpreadsheetService().deleteRow(spreadsheet, index);
				if (!result) {
					ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ERROR_TITLE, ERROR_MSG,
							Status.CANCEL_STATUS, Status.CANCEL);

				}
			}
		});
		if (!result) {
			return;
		}
		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * delete a complete column
	 * 
	 * @param index
	 *            column index
	 */
	// TODO: Lagutko: deleted only from TableModel but not from Database
	public void deleteColumn(final int index) {
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		final Boolean result = (Boolean) ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {

			private Boolean result;

			public Object getValue() {
				return result;
			}

			public void run() {
				result = SplashPlugin.getDefault().getSpreadsheetService().deleteColumn(spreadsheet, index);
				if (!result) {
					ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ERROR_TITLE, ERROR_MSG,
							Status.CANCEL_STATUS, Status.CANCEL);

				}
			}
		});
		if (!result) {
			return;
		}
		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * insert row at the end of Table
	 * 
	 * @param index
	 *            index of Row
	 */
	public void insertRow(final int index) {
		NeoSplashUtil.logn("Inserting a new row");
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().insertRow(spreadsheet, index);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);

		rowHeader.repaint();

		repaint();
	}

	/**
	 * insert rows at index
	 * 
	 * @param index
	 *            index of Row
	 * @param count
	 *            number of Rows
	 */
	public void insertRows(int index, int count) {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

SplashTableModel oldModel = (SplashTableModel)getModel();
        
        SplashTableModel newModel = new SplashTableModel(rowCount + 1, columnCount, splashName, oldModel.getEngine(), root);

		for (int i = index; i < index + count; i++)
			for (int j = 0; j < columnCount; j++) {
				newModel.setValueAt(new Cell(i, j, "", "", new CellFormat()), i, j);
			}

		for (int i = 0; i < index; i++)
			for (int j = 0; j < columnCount; j++) {
				newModel.setValueAt(getValueAt(i, j), i, j);
			}

		for (int i = index + count; i < rowCount + count; i++)
			for (int j = 0; j < columnCount; j++) {
				Cell c = (Cell) getValueAt(i - count, j);
				String oldCellID = new CellID(i - count, j).getFullID();
				String newCellID = new CellID(i, j).getFullID();
				c.renameCell(oldCellID, newCellID);

				newModel.setValueAt(c, i, j);
			}
		setModel(newModel);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * insert a column at the end of Table
	 * 
	 * @param index
	 *            index of Column
	 */
	public void insertColumn(final int index) {
		NeoSplashUtil.logn("Inserting a new row");
		SplashTableModel model = (SplashTableModel) getModel();
		final SpreadsheetNode spreadsheet = model.getSpreadsheet();
		ActionUtil.getInstance().runTask(new Runnable() {
			@Override
			public void run() {
				SplashPlugin.getDefault().getSpreadsheetService().insertColumn(spreadsheet, index);
			}
		}, false);

		model = new SplashTableModel(spreadsheet, model.getRubyRuntime(), model.getRubyProjectNode());
		setModel(model);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);

	}

	/**
	 * insert a column at index
	 * 
	 * @param index
	 *            index of Column
	 * @param count
	 *            number of Columns
	 */
	public void insertColumns(int index, int count) {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

SplashTableModel oldModel = (SplashTableModel)getModel();
        
        SplashTableModel newModel = new SplashTableModel(rowCount + 1, columnCount, splashName, oldModel.getEngine(), root);

		for (int i = 0; i < rowCount; i++) {
			for (int j = index; j < index + count; j++)
				newModel.setValueAt(new Cell(i, j, "", "", new CellFormat()), i, j);
		}

		for (int i = 0; i < rowCount; i++)
			for (int j = 0; j < index; j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		for (int i = 0; i < rowCount; i++)
			for (int j = index + count; j < columnCount + count; j++) {
				Cell c = (Cell) getValueAt(i, j - count);
				String oldCellID = new CellID(i, j - count).getFullID();
				String newCellID = new CellID(i, j).getFullID();
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);
			}

		setModel(newModel);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/*
	 * Id of Copied Cell
	 */
	private String copiedCellID = "";

	/*
	 * Id of cut Cell
	 */
	private String cutCellID = "";

	/*
	 * Definition of cut Cell
	 */
	private String cutDefinition = "";

	/*
	 * Value of cut Cell
	 */
	private String cutValue = "";

	/*
	 * Is Copy
	 */
	private boolean isCopy;

	/**
	 * Copies cell to buffer
	 * 
	 * @param row
	 *            row index of Cell to Copy
	 * @param column
	 *            column index of Cell to Copy
	 */
	public void copyCell(int row, int column) {
		NeoSplashUtil.logn("Copy pressed !!!");
		copiedCellID = new CellID(row, column).getFullID();
		NeoSplashUtil.logn("Copied cell at: " + copiedCellID);
		isCopy = true;
	}

	/**
	 * Cuts cell to buffer
	 * 
	 * @param row
	 *            row index of Cell to Cut
	 * @param column
	 *            column index of Cell to Cut
	 */
	public void cutCell(int row, int column) {
		NeoSplashUtil.logn("Cut pressed !!!");
		cutCellID = new CellID(row, column).getFullID();
		cutDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		cutValue = (String) ((Cell) getModel().getValueAt(row, column)).getValue();
		deleteCell(row, column);
		NeoSplashUtil.logn("Cut cell at: " + copiedCellID);
		isCopy = false;
	}

	/**
	 * Paste cell from buffer
	 * 
	 * @param row
	 *            row index of Cell to Paste
	 * @param column
	 *            column index of Cell to Paste
	 */
	public void pasteCell(int row, int column) {
		NeoSplashUtil.logn("Paste pressed !!!");
		if (isCopy) {
			NeoSplashUtil.logn("Pasting cell " + copiedCellID + " at " + new CellID(row, column).getFullID());
			CellID id = new CellID(copiedCellID);
			int srcColumn = id.getColumnIndex();
			int srcRow = id.getRowIndex();
			NeoSplashUtil.logn("srcColumn: " + srcColumn);
			NeoSplashUtil.logn("srcRow: " + srcRow);
			String srcDefinition = (String) ((Cell) getModel().getValueAt(srcRow, srcColumn)).getDefinition();
			String srcValue = (String) ((Cell) getModel().getValueAt(srcRow, srcColumn)).getValue();
			Cell dstCell = new Cell(row, column, srcDefinition, srcValue, new CellFormat());
			((SplashTableModel) getModel()).interpret(srcDefinition, row, column);

			getModel().setValueAt(dstCell, row, column);

		} else {
			NeoSplashUtil.logn("Pasting cell " + cutCellID + " at " + new CellID(row, column).getFullID());
			CellID id = new CellID(cutCellID);
			int srcColumn = id.getColumnIndex();
			int srcRow = id.getRowIndex();
			NeoSplashUtil.logn("srcColumn: " + srcColumn);
			NeoSplashUtil.logn("srcRow: " + srcRow);
			Cell dstCell = new Cell(row, column, cutDefinition, cutValue, new CellFormat());
			((SplashTableModel) getModel()).interpret(cutDefinition, row, column);

			getModel().setValueAt(dstCell, row, column);
		}
	}
	
	private void updateFont(Cell cell, Integer fontStyle) {
	    CellFormat format = cell.getCellFormat();
        Integer currentFont = format.getFontStyle().intValue();
         
        if (currentFont == fontStyle) {
            currentFont = Font.PLAIN;
        }
        else {
            switch (currentFont) {
            case Font.PLAIN:
                currentFont = fontStyle;
                break;
            case Font.BOLD:
                currentFont = fontStyle + Font.BOLD;
                break;
            case Font.ITALIC:
                currentFont = fontStyle + Font.ITALIC;
                break;
            case Font.BOLD + Font.ITALIC:
                currentFont = currentFont - fontStyle;
                break;
            }
        }
         
        format.setFontStyle(currentFont);
         
        ((SplashTableModel) (getModel())).updateCellFormat(cell);
         
        repaint();
    }

	/**
	 * Handles hot keys
	 */
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
	    //Lagutko, 15.10.2009, actions with pressed ctrls
        int row = getSelectedRow();
        int column = getSelectedColumn();
        
        boolean result = false;
        
        if (pressed) {
            if (e.isControlDown()) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_C:
                    copyCell(row, column);
                    result = true;
                    break;
                case KeyEvent.VK_V:
                    pasteCell(row, column);
                    result = true;
                    break;
                case KeyEvent.VK_X:
                    cutCell(row, column);
                    result = true;
                    break;
                case KeyEvent.VK_B:
                    Cell cell = (Cell) getValueAt(row, column);

                    updateFont(cell, Font.BOLD);
                    repaint();
                    result = true;
                    break;
                case KeyEvent.VK_I:
                    cell = (Cell) getValueAt(row, column);
                
                    updateFont(cell, Font.ITALIC);
                    repaint();
                    result = true;
                    break;
                case KeyEvent.VK_U:
                    cell = (Cell) getValueAt(row, column);

                    String old_value = (String) cell.getValue();
                    String new_value = "";
                    if (old_value.contains("<HTML><U>") && old_value.contains("</U></HTML>")) {
                        new_value = old_value.replace("<HTML><U>", "");
                        new_value = new_value.replace("</U></HTML>", "");
                    } else {
                        new_value = "<HTML><U>" + old_value + "</U></HTML>";
                    }

                    cell.setValue(new_value);
                    setValueAt(cell, row, column);
                    repaint();
                    result = true;
                    break;
                default:
                    return super.processKeyBinding(ks, e, condition, pressed);
                }
            }
            else {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                    deleteCell(row, column);
                    result = true;
                    break;
                default:
                    return super.processKeyBinding(ks, e, condition, pressed);
                }
            }
        }
        
        return result;
	}

	/**
	 * Deletes the Cell
	 * 
	 * @param row
	 *            row index of Cell to Delete
	 * @param column
	 *            column index of Cell to Delete
	 */
	// TODO: Lagutko: cell will be deleted only from Model but not from Database
	public void deleteCell(int row, int column) {
		NeoSplashUtil.logn("DELETE has been pressed ");
		Cell c = new Cell(row, column, "", "", new CellFormat());
		String oldDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		((SplashTableModel) getModel()).setValueAt(c, row, column, oldDefinition);
	}

	/**
	 * Returns Default width of Column
	 * 
	 * @return default column width
	 */
	public int getDefaultColumnWidth() {
		return defaultColumnWidth;
	}

	/**
	 * Sets default width of Column
	 * 
	 * @param defaultColumnWidth
	 *            default width for Column
	 */
	public void setDefaultColumnWidth(int defaultColumnWidth) {
		this.defaultColumnWidth = defaultColumnWidth;
	}

	/**
	 * Returns Default height of Row
	 * 
	 * @return default row height
	 */
	public int getDefaultRowHeight() {
		return defaultRowHeight;
	}

	/**
	 * Sets default height of Row
	 * 
	 * @param defaultRowHeight
	 *            default height for Row
	 */
	public void setDefaultRowHeight(int defaultRowHeight) {
		this.defaultRowHeight = defaultRowHeight;
	}

	public String getSplashName() {
		return splashName;
	}

	public void setSplashName(String splashName) {
		this.splashName = splashName;
	}

	public RubyProjectNode getRoot() {
		return root;
	}

	public void setRoot(RubyProjectNode root) {
		this.root = root;
	}
	
	@Override
	public void paint(Graphics g) {
	    //Lagutko, 16.10.2009, paint should run in single real transaction
	    Transaction tx = NeoUtils.beginTransaction();
	    try {
	        super.paint(g);
	    }
	    finally {
	        tx.success();
	        tx.finish();	        
	    }
	}
	
	
}
