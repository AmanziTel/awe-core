package org.amanzi.splash.neo4j.swing;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.amanzi.splash.neo4j.SplashNeoManager;
import org.amanzi.splash.neo4j.utilities.Util;

import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatRenderer;
import com.eteks.openjeks.format.TableFormat;

public class SplashTable extends JTable
{
	public JTable rowHeader;
	private int defaultColumnWidth = 150;
	private int defaultRowHeight = 20; 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1232338822L;
	private TableCellEditor        editor;
	private String splashID = "";	

	/**
	 * Initialize table with 500x200
	 */
	public SplashTable (String splash_id)
	{
		this (10, 10, splash_id);

	}


	/**
	 * called by previous function to set model
	 * @param model
	 */
	public SplashTable (int rowCount, int columnCount, String splash_id)
	{
		super();
		
		splashID = splash_id;
		
			setModel(new SplashTableModel (rowCount, columnCount, splashID));
	
		editor = new SplashCellEditor ();

		setDefaultRenderer (		Cell.class,
				new SplashCellRenderer (null,
						null));

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
		//rowHeader.setRowHeight(getRowHeight());
		rowHeader.setDefaultRenderer(Object.class, _RowHeaderRenderer);
		rowHeader.setRowHeight(getDefaultRowHeight());

	}



	/**
	 * Called by previous function to define the parser
	 * @param model
	 * @param expressionParser
	 * @param editable
	 */
	public SplashTable (TableModel           model,
			boolean              editable)
	{
		super ();


		// Set model afterwards because expressionParser needs to be set,
		// thus addColumn () and getColumnName () can work and columns can be created
		setModel (model);

		if (editable)
			editor = new SplashCellEditor ();

		setDefaultRenderer (Cell.class,
				new SplashCellRenderer (null,
						null));

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
	 */
	public TableCellRenderer getCellRenderer (int row,
			int column)
	{
		Object value = getValueAt (row, column);
		if (value != null)
			return getDefaultRenderer (getValueAt (row, column).getClass ());
		else
			return super.getCellRenderer (row, column);
	}

	/**
	 * Get Cell Editor object
	 */
	public TableCellEditor getCellEditor (int row,
			int column)
	{
		if (editor != null)
			return editor;
		else
			return super.getCellEditor (row, column);
	}

	/**
	 * Get column name
	 */
	public String getColumnName (int column)
	{
		return super.getColumnName (column);
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
		String id1 = Util.getCellIDfromRowColumn(x1, y1);
		String id2 = Util.getCellIDfromRowColumn(x2, y2);
			
		SplashTableModel model = (SplashTableModel)getModel();
		SplashNeoManager manager = model.getSplashNeoManager();
		
		Cell c1 = manager.getCell(id1);
		Cell c2 = manager.getCell(id2);
		model.setValueAt(c1, x2, y2);
		model.setValueAt(c2, x1, y1);
	}
	
	private void moveCell(int x1,int y1, int x2, int y2)
	{
		String id1 = Util.getCellIDfromRowColumn(x1, y1);
		String id2 = Util.getCellIDfromRowColumn(x2, y2);
			
		SplashTableModel model = (SplashTableModel)getModel();
		SplashNeoManager manager = model.getSplashNeoManager();
		
		Cell c1 = manager.getCell(id1);
		//Cell c2 = manager.getCell(id2);
		model.setValueAt(c1, x2, y2);
		//model.setValueAt(c2, x1, y2);
	}

	/**
	 * move row down
	 * @param index
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
	 * @param index
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
	 * @param index
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
	 * @param index
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
	 * 
	 */
	@SuppressWarnings("unused")
	private void addRow()
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount, splashID);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);


	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void addColumn()
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1, splashID);

		for (int i=0;i<rowCount;i++)
			for (int j=0;j<columnCount;j++)
				newModel.setValueAt(getValueAt(i, j), i, j);

		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);


	}

	/**
	 * delete a complete row
	 */
	public void deleteRow(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount-1, columnCount, splashID);

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
		//setTableFormat(newModel.tableFormat);

	}
	/**
	 * delete a complete column
	 * @param index
	 */
	public void deleteColumn(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount-1, splashID);

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
		//setTableFormat(newModel.tableFormat);

	}
	
		

	/**
	 * insert row at index
	 * @param index
	 */
	public void insertRow(int index)
	{
		Util.logn("Inserting a new row");
		SplashTableModel model = (SplashTableModel)getModel();
		SplashNeoManager manager = model.getSplashNeoManager();
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		
		SplashTableModel newModel = new SplashTableModel(rowCount+1, columnCount, splashID, manager, model.getEngine());
			//model;
		
		
//		Cell[] clist = new Cell[10];
//		for (int i=0;i<10;i++)
//			clist[i] = new Cell(10, i, "","", new CellFormat());
		
		//newModel.addRow(new Vector<Cell>());
		
	
		for (int i=0;i<newModel.getColumnCount();i++)
			newModel.setValueAt(new Cell(newModel.getRowCount()-1, i, "","", new CellFormat()), newModel.getRowCount()-1, i);
		
//		for (int i=newModel.getRowCount()-1;i>=index;i--){
//			for (int j=0;j<newModel.getColumnCount();j++){
//				moveCell(i,j, i+1,j);
//			}
//		}
	    
		
		setModel(newModel);
		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
		
		rowHeader.repaint();
		
		repaint();
		

	}
	/**
	 * insert row at index
	 * @param index
	 */
	public void insertRows(int index, int count)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount+count, columnCount, splashID);

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
				//c.setRow(i);

				newModel.setValueAt(c, i, j);
			}
		setModel(newModel);

		//setTableFormat(newModel.tableFormat);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);


	}
	/**
	 * insert a column at index
	 * @param index
	 */
	public void insertColumn(int index)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+1, splashID);

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
				//c.setCellID(Util.getCellIDfromRowColumn(i, j));
				//c.setColumn(j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j-1),"");
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j), (String) c.getDefinition());
			}




		//Util.printTableModelStatus(newModel);

		setModel(newModel);

		//setTableFormat(newModel.tableFormat);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);


	}

	/**
	 * insert a column at index
	 * @param index
	 */
	public void insertColumns(int index, int count)
	{
		int rowCount = getRowCount();
		int columnCount = getColumnCount();

		SplashTableModel newModel = new SplashTableModel(rowCount, columnCount+count, splashID);

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
				//c.setCellID(Util.getCellIDfromRowColumn(i, j));
				//c.setColumn(j);
				c.renameCell(oldCellID, newCellID);
				newModel.setValueAt(c, i, j);
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j-1),"");
				//newModel.updateCellReferences(Util.getCellIDfromRowColumn(i, j), (String) c.getDefinition());
			}

		//Util.printTableModelStatus(newModel);

		setModel(newModel);

		//setTableFormat(newModel.tableFormat);

		RowModel r = new RowModel(getModel());
		rowHeader.setModel(r);
	}

	private String copiedCellID = ""; 
	private String cutCellID = ""; 

	private String cutDefinition = "";
	private String cutValue="";

	private boolean isCopy;

	public void copyCell(int row, int column){
		Util.logn("Copy pressed !!!");
		copiedCellID = Util.getCellIDfromRowColumn(row, column);
		Util.logn("Copied cell at: " + copiedCellID);
		isCopy = true;
	}

	public void cutCell(int row, int column){
		Util.logn("Cut pressed !!!");
		cutCellID = Util.getCellIDfromRowColumn(row, column);
		cutDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		cutValue = (String) ((Cell) getModel().getValueAt(row, column)).getValue();
		deleteCell(row, column);
		Util.logn("Cut cell at: " + copiedCellID);
		isCopy = false;
	}

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

	public void deleteCell(int row, int column){
		Util.logn("DELETE has been pressed ");
		Cell c = new Cell(row, column, "","",new CellFormat());
		String oldDefinition = (String) ((Cell) getModel().getValueAt(row, column)).getDefinition();
		((SplashTableModel)getModel()).setValueAt(c, row, column, oldDefinition);

	}




	public int getDefaultColumnWidth() {
		return defaultColumnWidth;
	}




	public void setDefaultColumnWidth(int defaultColumnWidth) {
		this.defaultColumnWidth = defaultColumnWidth;
	}




	public int getDefaultRowHeight() {
		return defaultRowHeight;
	}




	public void setDefaultRowHeight(int defaultRowHeight) {
		this.defaultRowHeight = defaultRowHeight;
	}
}
