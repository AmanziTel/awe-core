package org.amanzi.splash.swing;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

class RowModel implements TableModel
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



	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}
}
