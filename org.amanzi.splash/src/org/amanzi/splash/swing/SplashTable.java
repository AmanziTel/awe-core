package org.amanzi.splash.swing;


import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


import org.amanzi.splash.utilities.Util;

public class SplashTable extends JTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1232338822L;
	private TableCellEditor        editor;
	//private SplashExpressionParser   expressionParser;
	
	/**
	 * Initialize table with 500x200
	 */
	public SplashTable ()
	{
		this (500, 200);
	}

	/**
	 * called by the previous function to initialize table with rows and columns
	 * @param rowCount
	 * @param columnCount
	 */
	public SplashTable (int rowCount, int columnCount)
	{
		this (new SplashTableModel (rowCount, columnCount));
	}

	/**
	 * called by previous function to set model
	 * @param model
	 */
	public SplashTable (TableModel model)
	{
		this (model, true);
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
}
