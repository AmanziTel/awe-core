package org.amanzi.splash.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.amanzi.splash.utilities.NeoSplashUtil;

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
			setFont(NeoSplashUtil.selectedHeaderFont);
			setBackground(NeoSplashUtil.selectedHeaderColor);
		}
		else{
			setFont(NeoSplashUtil.unselectedHeaderFont);
			setBackground(NeoSplashUtil.unselectedHeaderColor);
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
