package org.amanzi.splash.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTable;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.ui.wizards.ExportScriptWizard;
import org.amanzi.splash.utilities.ActionUtil;
import org.amanzi.splash.utilities.Util;
import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import org.eclipse.ui.part.EditorPart;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;

import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatPanel;
import com.eteks.openjeks.format.CellFormatRenderer;
import com.eteks.openjeks.format.TableFormat;

/**
 * Defines a sample "mini-spreadsheet" editor that demonstrates how to create an
 * editor whose input is based on either resources from the workspace (IDE) or
 * directly from the file system (RCP).
 */
public abstract class AbstractSplashEditor extends EditorPart implements TableModelListener {
	static final String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int defaultAlignment;
	private boolean isDirty = false;
	private boolean enabled;
	private TableViewer tableViewer;
	CellFormatPanel cellFormatPanel = null;
	CellFormat cellFormat = null;
	CellFormatRenderer renderer = null;
	TableFormat tableFormat = null;
	private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.resources.openjeks");
	private String selectCellValue = "";
	SplashTable table;
	JTable rowHeader;
	private int ROWS_EDGE_MARGIN = 5;
	private int COLUMNS_EDGE_MARGIN = 5;
	
	/**
	 * Class constructor
	 */
	public AbstractSplashEditor() {
		table = new SplashTable();		
		table.getModel().addTableModelListener(this);
	}

	/**
	 * Create a new valid <code>IEditorInput</code> for this concrete
	 * implementation (e.g., by opening a "new dialog" or launching a 
	 * creation wizard).
	 */
	public abstract IEditorInput createNewInput(String message)
	throws CoreException;

	/**
	 * 
	 * @param table
	 */
	private void launchCellFormatPanel(SplashTable table)
	{
		int firstRow, firstColumn, lastRow, lastColumn;
		firstRow = table.getSelectedRow();
		firstColumn = table.getSelectedColumn();
		lastRow = firstRow + table.getSelectedRowCount() - 1;
		lastColumn = firstColumn + table.getSelectedColumnCount() - 1;
		cellFormat = tableFormat.getFormatAt(firstRow, firstColumn , lastRow, lastColumn);

		cellFormatPanel = new CellFormatPanel(cellFormat);
		if (JOptionPane.showConfirmDialog(null,
				cellFormatPanel,
				resourceBundle.getString ("FORMAT_PANEL_TITLE"),
				JOptionPane.OK_CANCEL_OPTION ,
				JOptionPane.PLAIN_MESSAGE) == 0)
		{
			cellFormat = cellFormatPanel.getCellFormat();
			tableFormat.setFormatAt(cellFormat, firstRow, firstColumn , lastRow, lastColumn);
			setIsDirty(true);
		}
		cellFormatPanel = null;
		table.repaint();
	}

	/**
	 * 
	 */
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel)e.getSource();
		if (!selectCellValue.equals(model.getValueAt(row, column))) setIsDirty(true);
	}

	/**
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

//		JMenuItem copyMenu = new JMenuItem();
//		copyMenu.setText("Copy");
//		copyMenu.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//		Object value = table.getModel().getValueAt(rowIndex,
//		columnIndex);
//		setClipboardContents(value == null ? "" : value
//		.toString());
//		}
//		});
//		contextMenu.add(copyMenu);

//		JMenuItem pasteMenu = new JMenuItem();
//		pasteMenu.setText("Paste");
//		if (isClipboardContainingText(this)
//		&& table.getModel().isCellEditable(rowIndex, columnIndex)) {
//		pasteMenu.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//		String value = getClipboardContents(this);
//		table.getModel().setValueAt(value, rowIndex,
//		columnIndex);
//		}
//		});
//		} else {
//		pasteMenu.setEnabled(false);
//		}
//		contextMenu.add(pasteMenu);

		JMenuItem cellFormattingMenu = new JMenuItem();
		cellFormattingMenu.setText("Cell Formatting");
		cellFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(cellFormattingMenu);
		
		//Lagutko, 8.05.2009: add new actions for context menu
		contextMenu.addSeparator();
		
		JMenuItem openScriptMenu = new JMenuItem();
		openScriptMenu.setText("Open script");
		openScriptMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openCell(rowIndex, columnIndex);
				
			}
		});
		contextMenu.add(openScriptMenu);
		
		
		JMenuItem exportScriptMenu = new JMenuItem();
		exportScriptMenu.setText("Export script");
		exportScriptMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportCell(rowIndex, columnIndex);
				
			}
		});
		contextMenu.add(exportScriptMenu);
		
		JMenuItem updateScriptMenu = new JMenuItem();
		updateScriptMenu.setText("Update script");
		updateScriptMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCell(rowIndex, columnIndex);
			}
		});
		contextMenu.add(updateScriptMenu);

		return contextMenu;
	}
	
	/**
	 * Updates value of Cell from referenced script
	 * 
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @author Lagutko_N
	 */
	
	private void updateCell(int rowIndex, int columnIndex) {
		//get selected cell and update value of cell
		Cell cell = (Cell)table.getValueAt(rowIndex, columnIndex);
		
		//if Cell has no reference script than it cannot be update
		if (!cell.hasReference()) {
			//TODO: add error message
			return;
		}
		
		tableModel.updateCellFromScript(cell);
	}
	
	/**
	 * Opens referenced script in editor
	 * 
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @author Lagutko_N
	 */
	
	private void openCell(int rowIndex, int columnIndex) {
		final Cell cell = (Cell)table.getValueAt(rowIndex, columnIndex);
		final Display display = swingControl.getDisplay();
		
		//if Cell has no references to script than it cannot be open
		if (!cell.hasReference()) {
			//TODO: add error message
			return;
		}
		
		//run open action
		ActionUtil.getInstance(display).runTask(new Runnable() {
			public void run() {
				//find file by URI
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(cell.getScriptURI());
				if (files.length == 1) {
					try {
						//open file in editor
						EditorUtility.openInEditor(files[0]);
					}
					catch (RubyModelException e) {
						//TODO: handle excpetion
					}
					catch (PartInitException e) {
						//TODO: handle excpetion
					}
				}
				else {
					//TODO: handle this situation
				}
			}
		});
	}
	
	/**
	 * Method that exports cell to script 
	 * 
	 * @param rowIndex row index of cell
	 * @param columnIndex column index of cell
	 * @author Lagutko_N
	 */
	
	private void exportCell(int rowIndex, int columnIndex) {		
		//get Cell and Display
		final Cell cell = (Cell)table.getValueAt(rowIndex, columnIndex);		
		final Display display = swingControl.getDisplay();
		
		//run ExportScriptWizard
		ActionUtil.getInstance(display).runTask( new Runnable() {
			public void run() {
				WizardDialog dialog = new WizardDialog(display.getActiveShell(), new ExportScriptWizard(cell));
				dialog.open();
			}
		});
	}

	/**
	 * handle event for cancelling cell editing
	 */
	private void cancelCellEditing() {
		CellEditor ce = table.getCellEditor();
		if (ce != null) {
			ce.cancelCellEditing();
		}
	}

	/**
	 * UNUSED NOW: get contents from clipboard
	 * @param requestor
	 * @return
	 */
	private static String getClipboardContents(Object requestor) {
		Transferable t = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(requestor);
		if (t != null) {
			DataFlavor df = DataFlavor.stringFlavor;
			if (df != null) {
				try {
					Reader r = df.getReaderForText(t);
					char[] charBuf = new char[512];
					StringBuffer buf = new StringBuffer();
					int n;
					while ((n = r.read(charBuf, 0, charBuf.length)) > 0) {
						buf.append(charBuf, 0, n);
					}
					r.close();
					return (buf.toString());
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (UnsupportedFlavorException ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * UNUSED NOW: check if clipboard contains text or not
	 * @param requestor
	 * @return
	 */
	private static boolean isClipboardContainingText(Object requestor) {
		Transferable t = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(requestor);
		return t != null
		&& (t.isDataFlavorSupported(DataFlavor.stringFlavor) || t
				.isDataFlavorSupported(DataFlavor.plainTextFlavor) );
	}

	/**
	 * UNUSED NOW: set clipboard contents
	 * @param s
	 */
	private static void setClipboardContents(String s) {
		StringSelection selection = new StringSelection(s);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, selection);
	}

	/**
	 * create context menu for columns
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createColumnContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem columnInsertMenu = new JMenuItem();
		columnInsertMenu.setText("Insert Column");
		columnInsertMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertColumn(columnIndex);
			}
		});
		contextMenu.add(columnInsertMenu);

		JMenuItem deleteColumnMenu = new JMenuItem();
		deleteColumnMenu.setText("Delete Column");
		deleteColumnMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteColumn(columnIndex);
			}
		});
		contextMenu.add(deleteColumnMenu);

		JMenuItem moveColumnLeftMenu = new JMenuItem();
		moveColumnLeftMenu.setText("Move Column Left");
		moveColumnLeftMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveColumnLeft(columnIndex);
				table.setColumnSelectionInterval(columnIndex-1, columnIndex-1);
			}


		});
		contextMenu.add(moveColumnLeftMenu);

		JMenuItem moveColumnRightMenu = new JMenuItem();
		moveColumnRightMenu.setText("Move Column Right");
		moveColumnRightMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveColumnRight(columnIndex);
				//Util.printTableModelStatus((SplashTableModel) table.getModel());
				table.setColumnSelectionInterval(columnIndex+1, columnIndex+1);
			}
		});
		contextMenu.add(moveColumnRightMenu);


		JMenuItem columnFormattingMenu = new JMenuItem();
		columnFormattingMenu.setText("Column Formatting");
		columnFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(columnFormattingMenu);

		return contextMenu;
	}
	/**
	 * create context menu for rows
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private JPopupMenu createRowContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem rowInsertMenu = new JMenuItem();
		rowInsertMenu.setText("Insert Row");
		rowInsertMenu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				insertRow(rowIndex);
			}
		});
		contextMenu.add(rowInsertMenu);

		JMenuItem deleteRowMenu = new JMenuItem();
		deleteRowMenu.setText("Delete Row");
		deleteRowMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(rowIndex);
			}
		});
		contextMenu.add(deleteRowMenu);

		JMenuItem moveRowUpMenu = new JMenuItem();
		moveRowUpMenu.setText("Move Row Up");
		moveRowUpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowUp(rowIndex);
				table.setRowSelectionInterval(rowIndex-1, rowIndex-1);
			}
		});
		contextMenu.add(moveRowUpMenu);

		JMenuItem moveRowDownMenu = new JMenuItem();
		moveRowDownMenu.setText("Move Row Down");
		moveRowDownMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowDown(rowIndex);
				table.setRowSelectionInterval(rowIndex+1, rowIndex+1);
			}
		});
		contextMenu.add(moveRowDownMenu);

		JMenuItem rowFormattingMenu = new JMenuItem();
		rowFormattingMenu.setText("Row Formatting");
		rowFormattingMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCellFormatPanel(table);
			}
		});
		contextMenu.add(rowFormattingMenu);

		return contextMenu;
	}


	/**
	 * utility function for swapping cell contents
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private void swapCells(int x1,int y1, int x2, int y2)
	{
		Object o1 = table.getModel().getValueAt(x1,y1);
		Object o2 = table.getModel().getValueAt(x2,y2);

		String o1CellID = Util.getCellIDfromRowColumn(x1, y1);
		String o2CellID = Util.getCellIDfromRowColumn(x2, y2);

		((Cell) o1).renameCell(o1CellID, o2CellID);
		((Cell) o2).renameCell(o2CellID, o1CellID);

		table.getModel().setValueAt(o1, x2, y2);
		table.getModel().setValueAt(o2, x1, y1);
	}

	/**
	 * move column contents to right
	 * @param index
	 */
	private void moveColumnRight(int index) {
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		if (index > columnCount-1) return;

		for (int i=0;i<rowCount;i++)
		{
			swapCells(i,index,i,index+1);
		}
		updateTableFormatting(((SplashTableModel)table.getModel()).tableFormat);
		setIsDirty(true);
	}

	/**
	 * move volumn contents to left
	 * @param index
	 */
	private void moveColumnLeft(int index) {
		int rowCount = table.getModel().getRowCount();


		if (index < 1) return;

		for (int i=0;i<rowCount;i++)
		{
			swapCells(i,index,i,index-1);
		}
		updateTableFormatting(((SplashTableModel)table.getModel()).tableFormat);
		setIsDirty(true);
	}

	private void updateTableFormatting(TableFormat tf)
	{
		tableFormat = tf;

		renderer = new CellFormatRenderer (tableFormat);

		table.setDefaultRenderer(Object.class, renderer);
		table.setDefaultRenderer(Long.class, renderer);
		table.setDefaultRenderer(Double.class, renderer);
		table.setDefaultRenderer(String.class, renderer);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void addRow()
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(table.getModel().getValueAt(i, j), i, j);

		table.setModel(newModel);
		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void addColumn()
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(table.getModel().getValueAt(i, j), i, j);

		table.setModel(newModel);
		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}

	/**
	 * delete a complete row
	 */
	private void deleteRow(int index)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount-1, columnCount);

		for (int i=0;i<index;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);

		for (int i=index+1;i<rowCount-1;i++)
			for (int j=0;j<columnCount;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i,j);
				String oldCellID = Util.getCellIDfromRowColumn(i, j);
				String newCellID = Util.getCellIDfromRowColumn(i-1, j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(table.getModel().getValueAt(i,j), i-1, j);
			}

		table.setModel(newModel);
		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);
		updateTableFormatting(newModel.tableFormat);
		setIsDirty(true);
	}
	/**
	 * delete a complete column
	 * @param index
	 */
	private void deleteColumn(int index)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount-1);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+1;j<columnCount;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i,j);
				String oldCellID = Util.getCellIDfromRowColumn(i, j);
				String newCellID = Util.getCellIDfromRowColumn(i, j-1);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j-1);
			}

		table.setModel(newModel);
		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);
		updateTableFormatting(newModel.tableFormat);
		setIsDirty(true);
	}

	/**
	 * insert row at index
	 * @param index
	 */
	private void insertRow(int index)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount);

		for (int j=0;j<columnCount;j++)
		{
			newModel.setValueAt(new Cell(index, j, "", "", new CellFormat()), index, j);
		}

		for (int i=0;i<index;i++)
			for (int j=0;j<columnCount;j++)
			{
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);
			}

		for (int i=index+1;i<rowCount+1;i++)
			for (int j=0;j<columnCount;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i-1,j);
				String oldCellID = Util.getCellIDfromRowColumn(i-1, j);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				c.renameCell(oldCellID, newCellID);
				//c.setRow(i);

				newModel.setValueAt(c, i, j);
			}
		table.setModel(newModel);

		updateTableFormatting(newModel.tableFormat);

		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}
	/**
	 * insert row at index
	 * @param index
	 */
	private void insertRows(int index, int count)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+count, columnCount);

		for (int i=index;i<index+count;i++)
			for (int j=0;j<columnCount;j++)
			{
				newModel.setValueAt(new Cell(i, j, "", "", new CellFormat()), i, j);
			}

		for (int i=0;i<index;i++)
			for (int j=0;j<columnCount;j++)
			{
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);
			}

		for (int i=index+count;i<rowCount+count;i++)
			for (int j=0;j<columnCount;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i-count,j);
				String oldCellID = Util.getCellIDfromRowColumn(i-count, j);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				c.renameCell(oldCellID, newCellID);
				//c.setRow(i);

				newModel.setValueAt(c, i, j);
			}
		table.setModel(newModel);

		updateTableFormatting(newModel.tableFormat);

		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}
	/**
	 * insert a column at index
	 * @param index
	 */
	private void insertColumn(int index)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1);

		for (int i=0;i<rowCount;i++)
		{
			newModel.setValueAt(new Cell(i,index,"","",new CellFormat()), i, index);
		}

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+1;j<columnCount+1;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i,j-1);
				String oldCellID = Util.getCellIDfromRowColumn(i, j-1);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				//c.setCellID(Util.getCellIDfromRowColumn(i, j));
				//c.setColumn(j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j-1),"");
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j), (String) c.getDefinition());
			}




		//Util.printTableModelStatus(newModel);

		table.setModel(newModel);

		updateTableFormatting(newModel.tableFormat);

		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}
	
	/**
	 * insert a column at index
	 * @param index
	 */
	private void insertColumns(int index, int count)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+count);

		for (int i=0;i<rowCount;i++)
		{
			for (int j=index;j<index+count;j++)
				newModel.setValueAt(new Cell(i,j,"","",new CellFormat()), i, j);
		}

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(table.getModel().getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+count;j<columnCount+count;j++)
			{
				Cell c = (Cell) table.getModel().getValueAt(i,j-count);
				String oldCellID = Util.getCellIDfromRowColumn(i, j-count);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				//c.setCellID(Util.getCellIDfromRowColumn(i, j));
				//c.setColumn(j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j-1),"");
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j), (String) c.getDefinition());
			}

		//Util.printTableModelStatus(newModel);

		table.setModel(newModel);

		updateTableFormatting(newModel.tableFormat);

		RowModel r = new RowModel(table.getModel());
		rowHeader.setModel(r);

		setIsDirty(true);
	}
	/**
	 * move row up
	 * @param index
	 */
	private void moveRowUp(int index)
	{

		int columnCount = table.getModel().getColumnCount();

		if (index < 1) return;

		for (int j=0;j<columnCount;j++)
		{
			swapCells(index, j, index-1, j);
		}

		updateTableFormatting(((SplashTableModel)table.getModel()).tableFormat);

		setIsDirty(true);

	}
	/**
	 * move row down
	 * @param index
	 */
	private void moveRowDown(int index)
	{
		int rowCount = table.getModel().getRowCount();
		int columnCount = table.getModel().getColumnCount();

		if (index >= rowCount-1) return;


		for (int j=0;j<columnCount;j++)
		{
			swapCells(index, j, index+1, j);
		}
		updateTableFormatting(((SplashTableModel)table.getModel()).tableFormat);
		setIsDirty(true);
	}





	/**
	 * show column menu pop up 
	 * @param e
	 */
	private void maybeShowColumnPopup(MouseEvent e){
		if (/* e.isPopupTrigger() && */ table.isEnabled()) {
			Point p = new Point(e.getX(), e.getY());
			int col = table.columnAtPoint(p);
			int row = table.rowAtPoint(p);

//			translate table index to model index
			int mcol = table.getColumn(
					table.getColumnName(col)).getModelIndex();

			if (row >= 0 && row < table.getRowCount()) {
				cancelCellEditing();

//				create popup menu...
				JPopupMenu contextMenu = createColumnContextMenu(row,
						mcol);

//				... and show it
				if (contextMenu != null
						&& contextMenu.getComponentCount() > 0) {
					contextMenu.show(table, p.x, p.y);
				}
			}
		}
	}

	/**
	 * show row menu pop up
	 * @param e
	 */
	private void maybeShowRowPopup(MouseEvent e){
		if (/* e.isPopupTrigger() && */ table.isEnabled()) {
			Point p = new Point(e.getX(), e.getY());
			int col = table.columnAtPoint(p);
			int row = table.rowAtPoint(p);

//			translate table index to model index
			int mcol = table.getColumn(
					table.getColumnName(col)).getModelIndex();

			if (row >= 0 && row < table.getRowCount()) {
				cancelCellEditing();

//				create popup menu...
				JPopupMenu contextMenu = createRowContextMenu(row,
						mcol);

//				... and show it
				if (contextMenu != null
						&& contextMenu.getComponentCount() > 0) {
					contextMenu.show(table, p.x, p.y);
				}
			}
		}
	}

	/**
	 * show cell menu pop up
	 * @param e
	 */
	private void maybeShowPopup(MouseEvent e) {
		if (/* e.isPopupTrigger() && */ table.isEnabled()) {
			Point p = new Point(e.getX(), e.getY());
			int col = table.columnAtPoint(p);
			int row = table.rowAtPoint(p);

//			translate table index to model index
			int mcol = table.getColumn(
					table.getColumnName(col)).getModelIndex();

			if (row >= 0 && row < table.getRowCount()) {
				cancelCellEditing();

//				create popup menu...
				JPopupMenu contextMenu = createContextMenu(row,
						mcol);

//				... and show it
				if (contextMenu != null
						&& contextMenu.getComponentCount() > 0) {
					contextMenu.show(table, p.x, p.y);
				}
			}
		}
	}

	/**
	 * create Swing table
	 * @param parent
	 */
	private void createTable(final Composite parent) {
		swingControl = new SwingControl(parent, SWT.NONE) {
			{
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
				setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			}
			protected JComponent createSwingComponent() {				
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.createDefaultColumnsFromModel();
				table.setCellSelectionEnabled(true);
				
				table.setOpaque(true);
				table.setBackground(getAWTHierarchyRoot().getBackground());

				JScrollPane scrollPane = new JScrollPane(table);
				scrollPane.setBorder(new EmptyBorder(0,0,0,0));

				tableFormat = tableModel.tableFormat;
				
				renderer = new CellFormatRenderer (tableFormat);

				table.setDefaultRenderer(Object.class, renderer);
				table.setDefaultRenderer(Long.class, renderer);
				table.setDefaultRenderer(Double.class, renderer);
				table.setDefaultRenderer(String.class, renderer);

				final String headers[] = new String[table.getModel().getRowCount()];
				for (int i=0;i<table.getModel().getRowCount();i++)
				{
					headers[i] = Integer.toString(i+1);
				}

				table.addKeyListener(new KeyListener(){

					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub

					}

					
					public void keyReleased(KeyEvent e) {
						// TODO Auto-generated method stub
						//Util.log("e.getKeyCode() = " + e.getKeyCode());
						//Util.log("e.VK_ENTER = " + e.VK_ENTER);
						if (e.getKeyCode() == e.VK_ENTER) /*ENTER*/
						{
							int row = table.getSelectedRow();
							int column = table.getSelectedColumn();
							
							int rdiff = table.getRowCount() - row;
							//Util.log("rdiff = " + rdiff);
							
							int cdiff = table.getColumnCount() - column;
							//Util.log("cdiff = " + cdiff);
							if (rdiff < COLUMNS_EDGE_MARGIN)
							{
								insertRows(table.getRowCount()-1, COLUMNS_EDGE_MARGIN-rdiff);
							}

							if (cdiff < ROWS_EDGE_MARGIN)
							{
								insertColumns(table.getColumnCount()-1,ROWS_EDGE_MARGIN-cdiff );
							}						
						}
					}

					
					public void keyTyped(KeyEvent e) {
						// TODO Auto-generated method stub

					}

				});
				
				table.addMouseListener(new MouseListener(){

					
					public void mouseClicked(MouseEvent e) {


					}

					
					public void mouseEntered(MouseEvent e) {


					}

					
					public void mouseExited(MouseEvent e) {


					}

					
					public void mousePressed(MouseEvent e) {

						if(e.getButton()==3)
						{

							int col = table.columnAtPoint(e.getPoint());
							int row = table.rowAtPoint(e.getPoint());

							table.setColumnSelectionInterval(col, col);
							table.setRowSelectionInterval(row, row);

							maybeShowPopup(e);
							//launchCellFormatPanel(table);
						}
					}

					
					public void mouseReleased(MouseEvent e) {


					}

				});


				// set selection mode for contiguous  intervals
				table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				table.setCellSelectionEnabled(true);

				MouseListener ml = new HeaderMouseAdapter();

				// we don't allow reordering
				table.getTableHeader().setReorderingAllowed(false);
				table.getTableHeader().addMouseListener(ml);

				rowHeader = new JTable(new RowModel(table.getModel()));
				TableCellRenderer renderer = new RowHeaderRenderer();

				rowHeader.setIntercellSpacing(new Dimension(0, 0));

				Dimension d = rowHeader.getPreferredScrollableViewportSize();
				d.width = 30;//comp.getPreferredSize().width;
				rowHeader.setPreferredScrollableViewportSize(d);
				rowHeader.setRowHeight(table.getRowHeight());
				rowHeader.setDefaultRenderer(Object.class, renderer);
				rowHeader.addMouseListener(ml);

				scrollPane.setRowHeaderView(rowHeader);
				// initial selection
				//resetSelection();
				table.setRequestFocusEnabled(true);

				//menuBar.setRequestFocusEnabled(false);
				//toolBar.setRequestFocusEnabled(false);
				table.requestFocus();

				ListSelectionListener lsl = new SelectionAdapter();
				table.getSelectionModel().addListSelectionListener(lsl);
				table.getColumnModel().getSelectionModel().addListSelectionListener(lsl);


				return scrollPane;
			}

			public Composite getLayoutAncestor() {
				return parent;
			}

		};
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(final Composite parent) {
		createTable(parent);
		table.setModel(tableModel);
		
		
		thread = Thread.currentThread();
	}



	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			if (validateEditorInput(getEditorInput()) != null) {
				if (getEditorInput().exists())
					saveContents();
				else
					doSaveAs(MessageFormat.format(
							"The original input ''{0}'' has been deleted.",
							new Object[] { getEditorInput().getName() }));
			} else {
				doSaveAs();
			}
		} catch (CoreException e) {
			monitor.setCanceled(true);
			MessageDialog.openError(null, "Unable to Save Changes", e
					.getLocalizedMessage());
			return;
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}



	/**
	 * Subclasses should persist the mini-spreadsheet in their
	 * implementation-specific manner (in our case, directly in
	 * file system or the workspace).
	 */
	public abstract boolean saveContents() throws CoreException;



	/**
	 * Respond to the Outline view's request to update the selection.
	 * 
	 * @see org.amanzi.splash.ui.outline.JRSSContentOutlinePage 
	 */
	public void selectionChanged(ISelection selection) {
		tableViewer.setSelection(selection);
	}



	/**
	 * Set the mini-spreadsheet's contents using the given <code>IEditorInput</code>
	 * (subclasses can assume it has previously been validated
	 * by <code>validateEditorInput</code>.
	 */
	public abstract void setContents(IEditorInput editorInput)
	throws CoreException;

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//table.setFocus();
	}

	/**
	 * Persist the mini-spreadsheet as a workspace resource, allowing 
	 * for the <b>Restore from Local History</b> options.
	 */

	/**
	 * Flag the mini-spreadsheet as dirty,
	 * enable the <b>Save</b> options, an update the editor's modification
	 * indicator (*).
	 */
	protected void setIsDirty(boolean isDirty) {
		this.isDirty = isDirty;

		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Verify the editor input is valid (subclasses may transform it to another
	 * implementator of <code>IEditorInput</code>), or null if the
	 * input is unacceptable.
	 */
	public abstract IEditorInput validateEditorInput(IEditorInput editorInput);



	/**
	 * @see org.eclipse.ui.IEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		doSaveAs("Save As");
	}

	private void doSaveAs(String message) {
		try {
			IEditorInput input = createNewInput(message);
			if (input != null) {
				setInput(input);
				saveContents();
				setPartName(input.getName());

				firePropertyChange(PROP_INPUT);
			}
		} catch (CoreException e) {
			MessageDialog.openError(null, "Unable to Save Changes", e
					.getLocalizedMessage());
			return;
		}
	}

	/**
	 * Return the column alignment.
	 */
	public int getDefaultAlignment() {
		return defaultAlignment;
	}



	SplashTableModel tableModel;
	private Thread thread;
	private SwingControl swingControl; 



	/**
	 * @see org.eclipse.ui.IEditorPart#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
	throws PartInitException {

		if (!editorInput.exists())
			throw new PartInitException(editorInput.getName()
					+ "does not exist.");

		IEditorInput ei = validateEditorInput(editorInput);

		// This message includes class names to help
		// the programmer / reader; production code would instead
		// log an error and provide a helpful, friendly message.
		if (ei == null)
			throw new PartInitException(
					MessageFormat.format(
							"Invalid input.\n\n({0} is not a valid input for {1})",
							new String[] {editorInput.getClass().getName(),
									this.getClass().getName()
							}));

		try {

			Util.log("ei: " + ei.toString());
			setInput(ei);
			setContents(ei);
			setSite(site);
			setPartName(editorInput.getName());
		} catch (CoreException e) {
			throw new PartInitException(e.getMessage());
		}
	}



	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private static class RowHeaderRenderer extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8376789651877346556L;

		public RowHeaderRenderer()
		{
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(RIGHT);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column)
		{
			if (table != null)
			{
				JTableHeader header = table.getTableHeader();
				if (header != null)
				{
					setForeground(header.getForeground());
					setBackground(header.getBackground());
				}
			}
			setValue(String.valueOf(row + 1));

			return this;
		}

		public void updateUI()
		{
			super.updateUI();
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		}
	}

	private static class RowModel implements TableModel
	{
		private TableModel source;

		RowModel(TableModel source)
		{
			this.source = source;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int columnIndex)
		{
			return Object.class;
		}

		public int getColumnCount()
		{
			return 1;
		}

		public String getColumnName(int columnIndex)
		{
			return null;
		}

		public int getRowCount()
		{
			return source.getRowCount();
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return null;
		}

		public void addTableModelListener(javax.swing.event.TableModelListener l)
		{
		}

		public void removeTableModelListener(javax.swing.event.TableModelListener l)
		{
		}
	}
	private class HeaderMouseAdapter extends MouseAdapter
	{

		public void mousePressed(MouseEvent e) {

			if(e.getButton()==3)
			{
				int col = -1;
				int row = -1;
				if (e.getSource() instanceof JTableHeader)
				{
					col = table.columnAtPoint(e.getPoint());
				}
				if (e.getSource() instanceof JTable)
				{
					row = table.rowAtPoint(e.getPoint());
				}
				if (col >= 0)
				{
					maybeShowColumnPopup(e);
				}
				else if (row >= 0)
				{
					maybeShowRowPopup(e);
				}

				int rowCount = table.getRowCount();
				int colCount = table.getColumnCount();

				table.setRowSelectionInterval(0, rowCount - 1);

				if (col >= 0) // select column
				{
					table.setColumnSelectionInterval(col, col);
					table.setRowSelectionInterval(0, rowCount - 1);
				}
				else if (row >= 0) // select row
				{
					table.setColumnSelectionInterval(0, colCount - 1);
					table.setRowSelectionInterval(row, row);
				}
				else // select all
				{
					table.setColumnSelectionInterval(0, colCount - 1);
					table.setRowSelectionInterval(0, rowCount - 1);
				}
			}
		}

		public void mouseClicked(MouseEvent e)
		{

			int col = -1;
			int row = -1;
			if (e.getSource() instanceof JTableHeader)
			{
				col = table.columnAtPoint(e.getPoint());
			}
			if (e.getSource() instanceof JTable)
			{
				row = table.rowAtPoint(e.getPoint());
			}

			int rowCount = table.getRowCount();
			int colCount = table.getColumnCount();

			table.setRowSelectionInterval(0, rowCount - 1);

			if (col >= 0) // select column
			{
				table.setColumnSelectionInterval(col, col);
				table.setRowSelectionInterval(0, rowCount - 1);
			}
			else if (row >= 0) // select row
			{
				table.setColumnSelectionInterval(0, colCount - 1);
				table.setRowSelectionInterval(row, row);
			}
			else // select all
			{
				table.setColumnSelectionInterval(0, colCount - 1);
				table.setRowSelectionInterval(0, rowCount - 1);
			}

		}
	}



	private class SelectionAdapter implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
//			int c = listenerList.getListenerCount(SpreadsheetSelectionListener.class);
//			if (c > 0)
//			{
//			//fireSelectionChanged();
//			}
		}
	}



	public CellFormat getCellFormat() {
		return cellFormat;
	}

	public void setCellFormat(CellFormat cellFormat) {
		this.cellFormat = cellFormat;
	}
}

class RowHeaderRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	RowHeaderRenderer(JTable table) {
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setFont(header.getFont());
	}

	public Component getListCellRendererComponent( JList list, 
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText((value == null) ? "" : value.toString());
		return this;
	}
}