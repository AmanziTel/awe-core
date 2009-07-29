package org.amanzi.splash.neo4j.swing;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.amanzi.splash.neo4j.database.nodes.RootNode;
import org.amanzi.splash.neo4j.utilities.Util;

import com.eteks.openjeks.format.CellFormat;

public class SplashTable extends JTable
{
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
	
	/*
	 * Editor of Cell
	 */
	private TableCellEditor        editor;
	
	/*
	 * Id of Spreadsheet
	 */
	private String splashName = "";
	
	/*
	 * Root Node for current Spreadsheet
	 */
	private RootNode root;
	
	/**
	 * Initialize table with 500x200
	 * 
	 * @param splash_name name of Spreadsheet
	 * @param root Root node of Spreadsheet
	 */
	public SplashTable (String splash_name, RootNode root)
	{
		this (10, 10, splash_name, root);

	}

	/**
	 * called by previous function to set model
	 * 
	 * @param rowCount number of Rows
	 * @param columnCount number of Columns
	 * @param splash_name name of Spreadsheet
	 * @param root root node of Spreadsheet
	 */
	public SplashTable (int rowCount, int columnCount, String splash_name, RootNode root)
	{
		super();
		
		splashName = splash_name;
		this.root = root;
		
		setModel(new SplashTableModel (rowCount, columnCount, splashName, root));
	
		editor = new SplashCellEditor ();

		setDefaultRenderer(Cell.class, new SplashCellRenderer (null, null));

		setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setRowSelectionAllowed (false);
		setCellSelectionEnabled (true);
		setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

		//--------------------------------------------------------------------
		// Row Header Renderer
		//--------------------------------------------------------------------
		RowHeaderRenderer _RowHeaderRenderer = new RowHeaderRenderer();
		rowHeader = new JTable(getModel().getRowCount(), 1);
		rowHeader.setIntercellSpacing(new Dimension(0, 0));
		Dimension d = rowHeader.getPreferredScrollableViewportSize();
		d.width = rowHeader.getPreferredSize().width/2;
		rowHeader.setPreferredScrollableViewportSize(d);
		rowHeader.setDefaultRenderer(Object.class, _RowHeaderRenderer);
		rowHeader.setRowHeight(getDefaultRowHeight());

	}



	/**
	 * Called by previous function to define the parser
	 * 
	 * @param model TalbeModel
	 * @param editable is table editable
	 */
	public SplashTable (TableModel model, boolean editable)
	{
		super ();

		// Set model afterwards because expressionParser needs to be set,
		// thus addColumn () and getColumnName () can work and columns can be created
		setModel (model);

		if (editable) {
			editor = new SplashCellEditor ();
		}

		setDefaultRenderer (Cell.class, new SplashCellRenderer (null, null));

		setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setRowSelectionAllowed (false);
		setCellSelectionEnabled (true);
		setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

		//--------------------------------------------------------------------
		// Row Header Renderer
		//--------------------------------------------------------------------
		RowHeaderRenderer _RowHeaderRenderer = new RowHeaderRenderer();
		rowHeader = new JTable(new RowModel(getModel()));
		rowHeader.setIntercellSpacing(new Dimension(0, 0));
		Dimension d = rowHeader.getPreferredScrollableViewportSize();
		d.width = rowHeader.getPreferredSize().width/2;
		rowHeader.setPreferredScrollableViewportSize(d);
		//rowHeader.setRowHeight(getRowHeight());
		rowHeader.setDefaultRenderer(Object.class, _RowHeaderRenderer);
		rowHeader.setRowHeight(getDefaultRowHeight());
	}

	/**
	 * Get cell renderer object
	 * 
	 * @param row row index
	 * @param column column index
	 * @return CellRenderer by given row and column
	 */
	public TableCellRenderer getCellRenderer (int row, int column)
	{
		Object value = getValueAt (row, column);
		if (value != null) {
			return getDefaultRenderer (getValueAt (row, column).getClass ());
		}
		else {
			return super.getCellRenderer (row, column);
		}
	}

	/**
	 * Get Cell Editor object
	 * 
	 * @param row row index
	 * @param column column index
	 * @return cell editor by given row and column
	 */
	public TableCellEditor getCellEditor (int row, int column)
	{
		if (editor != null) {
			return editor;
		}
		else {
			return super.getCellEditor (row, column);
		}
	}

	/**
	 * Get column name
	 * 
	 * @param column
	 * @return name of column
	 */
	public String getColumnName (int column)
	{
		return super.getColumnName (column);
	}
	
	/**
	 * utility function for swapping cell contents
	 * @param firstCellRow row index of first cell
	 * @param firstCellColumn column index of first cell
	 * @param secondCellRow row index of second cell
	 * @param secondCellColumn column index of second cell
	 */
	private void swapCells(int firstCellRow,int firstCellColumn, int secondCellRow, int secondCellColumn)
	{
		String firstCellId = Util.getCellIDfromRowColumn(firstCellRow, firstCellColumn);
		String secondCellId = Util.getCellIDfromRowColumn(secondCellRow, secondCellColumn);
			
		SplashTableModel model = (SplashTableModel)getModel();		
		
		Cell firstCell = model.getCellByID(firstCellId);
		Cell secondCell = model.getCellByID(secondCellId);
		model.setValueAt(firstCell, secondCellRow, secondCellColumn);
		model.setValueAt(secondCell, firstCellRow, firstCellColumn);
	}
	
	/**
	 * move row down
	 * @param index index of Row to move
	 */
	public void moveRowDown(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		if (index >= rowCount-1) return;

		Util.logn("I'm before loop");

		for (int j=0;j<columnCount;j++)
		{
			Util.logn("loop index = " + j);
			swapCells(index, j, index+1, j);
		}

		Util.logn("I'm after loop");


	}

	/**
	 * move row up
	 * @param index index of row to move
	 */
	public void moveRowUp(int index)
	{
		int columnCount = getColumnCount();

		if (index < 1) return;

		for (int j=0;j<columnCount;j++)
		{
			swapCells(index, j, index-1, j);
		}
	}

	/**
	 * move column contents to right
	 * @param index index of column to move
	 */
	public void moveColumnRight(int index) {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		if (index > columnCount-1) return;

		for (int i=0;i<rowCount;i++)
		{
			swapCells(i,index,i,index+1);
		}
	}

	/**
	 * move volumn contents to left
	 * @param index index of column to move
	 */
	public void moveColumnLeft(int index) {
		int rowCount = getRowCount();

		if (index < 1) return;

		for (int i=0;i<rowCount;i++)
		{
			swapCells(i,index,i,index-1);
		}


		//setTableFormat(tableFormat);
	}

	/**
	 * Adds a new Row
	 */
	@SuppressWarnings("unused")
	private void addRow()
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount, splashName, root);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);


	}

	/**
	 * Adds a new Column
	 */
	@SuppressWarnings("unused")
	private void addColumn()
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1, splashName, root);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * delete a complete row
	 * 
	 * @param index of Row
	 */
	//TODO: Lagutko: deleted only from TableModel but not from Database
	public void deleteRow(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount-1, columnCount, splashName, root);

		for (int i=0;i<index;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(getValueAt(i,j), i, j);

		for (int i=index+1;i<rowCount-1;i++)
			for (int j=0;j<columnCount;j++)
			{
				Cell c = (Cell) getValueAt(i,j);
				String oldCellID = Util.getCellIDfromRowColumn(i, j);
				String newCellID = Util.getCellIDfromRowColumn(i-1, j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(getValueAt(i,j), i-1, j);
			}

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}
	
	/**
	 * delete a complete column
	 * 
	 * @param index column index
	 */
	//TODO: Lagutko: deleted only from TableModel but not from Database
	public void deleteColumn(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		//TODO: Lagutko: why here Constructor without RubyEngine and in insertRow RubyEngine exists
		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount-1, splashName, root);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+1;j<columnCount;j++)
			{
				Cell c = (Cell) getValueAt(i,j);
				String oldCellID = Util.getCellIDfromRowColumn(i, j);
				String newCellID = Util.getCellIDfromRowColumn(i, j-1);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(getValueAt(i,j), i, j-1);
			}

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}
	
		

	/**
	 * insert row at the end of Table
	 * @param index index of Row
	 */
	public void insertRow(int index)
	{
		Util.logn("Inserting a new row");
		SplashTableModel model = (SplashTableModel)getModel();		
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		
		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount, splashName, model.getEngine(), root);
	
		for (int i=0;i<newModel.getColumnCount();i++)
			newModel.setValueAt(new Cell(newModel.getRowCount()-1, i, "","", new CellFormat()), newModel.getRowCount()-1, i);
		
		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
		
		rowHeader.repaint();
		
		repaint();
	}
	/**
	 * insert rows at index
	 * @param index index of Row
	 * @param count number of Rows
	 */
	public void insertRows(int index, int count)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+count, columnCount, splashName, root);

		for (int i=index;i<index+count;i++)
			for (int j=0;j<columnCount;j++)
			{
				newModel.setValueAt(new Cell(i, j, "", "", new CellFormat()), i, j);
			}

		for (int i=0;i<index;i++)
			for (int j=0;j<columnCount;j++)
			{
				newModel.setValueAt(getValueAt(i,j), i, j);
			}

		for (int i=index+count;i<rowCount+count;i++)
			for (int j=0;j<columnCount;j++)
			{
				Cell c = (Cell) getValueAt(i-count,j);
				String oldCellID = Util.getCellIDfromRowColumn(i-count, j);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				c.renameCell(oldCellID, newCellID);

				newModel.setValueAt(c, i, j);
			}
		setModel(newModel);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}
	/**
	 * insert a column at the end of Table
	 * @param index index of Column
	 */
	public void insertColumn(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1, splashName, root);

		for (int i=0;i<rowCount;i++)
		{
			newModel.setValueAt(new Cell(i,index,"","",new CellFormat()), i, index);
		}

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+1;j<columnCount+1;j++)
			{
				Cell c = (Cell) getValueAt(i,j-1);
				String oldCellID = Util.getCellIDfromRowColumn(i, j-1);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);				
			}
		
		setModel(newModel);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	/**
	 * insert a column at index
	 * 
	 * @param index index of Column
	 * @param count number of Columns
	 */
	public void insertColumns(int index, int count)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+count, splashName, root);

		for (int i=0;i<rowCount;i++)
		{
			for (int j=index;j<index+count;j++)
				newModel.setValueAt(new Cell(i,j,"","",new CellFormat()), i, j);
		}

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<index;j++)
				newModel.setValueAt(getValueAt(i,j), i, j);

		for (int i=0;i<rowCount;i++)
			for (int j=index+count;j<columnCount+count;j++)
			{
				Cell c = (Cell) getValueAt(i,j-count);
				String oldCellID = Util.getCellIDfromRowColumn(i, j-count);
				String newCellID = Util.getCellIDfromRowColumn(i, j);
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
	private String cutValue="";

	/*
	 * Is Copy
	 */
	private boolean isCopy;

	/**
	 * Copies cell to buffer
	 * 
	 * @param row row index of Cell to Copy
	 * @param column column index of Cell to Copy
	 */
	public void copyCell(int row, int column){
		Util.logn("Copy pressed !!!");
		copiedCellID = Util.getCellIDfromRowColumn(row, column);
		Util.logn("Copied cell at: " + copiedCellID);
		isCopy = true;
	}

	/**
     * Cuts cell to buffer
     * 
     * @param row row index of Cell to Cut
     * @param column column index of Cell to Cut
     */
	public void cutCell(int row, int column){
		Util.logn("Cut pressed !!!");
		cutCellID = Util.getCellIDfromRowColumn(row, column);
		cutDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		cutValue = (String) ((Cell) getModel().getValueAt(row, column)).getValue();
		deleteCell(row, column);
		Util.logn("Cut cell at: " + copiedCellID);
		isCopy = false;
	}

	/**
     * Paste cell from buffer
     * 
     * @param row row index of Cell to Paste
     * @param column column index of Cell to Paste
     */
	public void pasteCell(int row, int column){
		Util.logn("Paste pressed !!!");
		if (isCopy){
			Util.logn("Pasting cell " + copiedCellID + " at " + Util.getCellIDfromRowColumn(row, column));
			int srcColumn = Util.getColumnIndexFromCellID(copiedCellID);
			int srcRow = Util.getRowIndexFromCellID(copiedCellID);
			Util.logn("srcColumn: " + srcColumn);
			Util.logn("srcRow: " + srcRow);
			String srcDefinition = (String) ((Cell) getModel().getValueAt(srcRow, srcColumn)).getDefinition();
			String srcValue = (String) ((Cell) getModel().getValueAt(srcRow, srcColumn)).getValue();
			Cell dstCell = new Cell(row, column, srcDefinition, srcValue, new CellFormat());
			String oldDstDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
			((SplashTableModel)getModel()).interpret(srcDefinition, oldDstDefinition, row, column);

			getModel().setValueAt(dstCell, row, column);

		}else{		
			Util.logn("Pasting cell " + cutCellID + " at " + Util.getCellIDfromRowColumn(row, column));
			int srcColumn = Util.getColumnIndexFromCellID(cutCellID);
			int srcRow = Util.getRowIndexFromCellID(cutCellID);
			Util.logn("srcColumn: " + srcColumn);
			Util.logn("srcRow: " + srcRow);
			Cell dstCell = new Cell(row, column, cutDefinition, cutValue, new CellFormat());
			String oldDstDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
			((SplashTableModel)getModel()).interpret(cutDefinition, oldDstDefinition, row, column);

			getModel().setValueAt(dstCell, row, column);
		}
	}

	/**
	 * Handles hot keys
	 */
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed){
		int row = getSelectedRow();
		int column = getSelectedColumn();

		switch (ks.getKeyCode()){
		case 66: // Ctrl+B


			return false;
		case 73:
			Util.logn("CTRL+i has been pressed ");

			return false;

		case 85:
			Util.logn("CTRL+U has been pressed ");
			return false;

		case 127:
			deleteCell(row, column);
			return false;

		default:
			return super.processKeyBinding (ks, e, condition, pressed);
		}


		//return false;
	}

	/**
	 * Deletes the Cell
	 *
	 * @param row row index of Cell to Delete
	 * @param column column index of Cell to Delete
	 */
	//TODO: Lagutko: cell will be deleted only from Model but not from Database
	public void deleteCell(int row, int column){
		Util.logn("DELETE has been pressed ");
		Cell c = new Cell(row, column, "","",new CellFormat());
		String oldDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		((SplashTableModel)getModel()).setValueAt(c, row, column, oldDefinition);
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
	 * @param defaultColumnWidth default width for Column
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
     * @param defaultRowHeight default height for Row
     */
	public void setDefaultRowHeight(int defaultRowHeight) {
		this.defaultRowHeight = defaultRowHeight;
	}
}
