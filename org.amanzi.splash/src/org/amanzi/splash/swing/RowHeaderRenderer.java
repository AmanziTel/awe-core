package org.amanzi.splash.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.amanzi.splash.utilities.Util;

public class RowHeaderRenderer extends DefaultTableCellRenderer
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
	public boolean selected = false;
	public int row = -1;
	public int column = -1;
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column)
	{
		//Util.logn("getTableCellRendererComponent is called ");
		
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		
		if (this.row == row){
			setFont(Util.selectedHeaderFont);
			setBackground(Util.selectedHeaderColor);
		}
		else{
			setFont(Util.unselectedHeaderFont);
			setBackground(Util.unselectedHeaderColor);
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
