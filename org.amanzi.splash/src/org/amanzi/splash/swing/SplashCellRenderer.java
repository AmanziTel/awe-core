package org.amanzi.splash.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.amanzi.splash.utilities.Util;

import com.eteks.openjeks.format.CellFormat;

public class SplashCellRenderer extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4271039280304551527L;
	/**
	 * Constructor 
	 * @param syntax
	 * @param interpreter
	 */
	public SplashCellRenderer (	Object 			syntax,
								Object          interpreter)
	{
	}

	/**
	 * getTableCellRendererComponent
	 */
	public Component getTableCellRendererComponent (JTable table,
													Object value,
													boolean isSelected,
													boolean hasFocus,
													int row,
													int column)
	{
		if (value instanceof Cell)
		{
			
			if (value == null) Util.logNullAtCell("getTableCellRendererComponent", (String) value, row, column);
			if (table == null) Util.logNullAtCell("getTableCellRendererComponent", table.toString(), row, column);
			
			
			// Get the computed value to render
			value = getExpressionValue ((Cell)value);
			
			if (value == null){ 
				Util.logNullAtCell("getTableCellRendererComponent", (String) value, row, column);
				value = new Cell(row,column,"","",new CellFormat());
			}
			// Once the expression is computed, its rendering is delegated
			// to the default renderer set on the table according to the class of the result
			return table.getDefaultRenderer (value.getClass ()).getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
		}
		else
			// This renderer is supposed to be called only for Cell values
			// but this provides a default rendering (and avoid stack overflow)
			return super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
	}

	/**
	 * Get Expression value, returns text to be displayed into cells after
	 * stopping editing the cell.
	 * @param expression
	 * @return
	 */
	public Object getExpressionValue (Object expression)
	{
		return ((Cell)expression).getValue ();
	}
}
class RowHeaderRenderer extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8376789651877346556L;
	public RowHeaderRenderer()
	{
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
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

