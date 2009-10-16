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
