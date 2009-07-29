package org.amanzi.splash.neo4j.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;

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
//		if (value instanceof Cell)
//		{
//			Util.logn("SplashCellRenderer is called");
//			
//			if (value == null) Util.logNullAtCell("getTableCellRendererComponent", (String) value, row, column);
//			if (table == null) Util.logNullAtCell("getTableCellRendererComponent", table.toString(), row, column);
//			
//			// Get the computed value to render
//			value = getExpressionValue ((Cell)value);
//			
//			if (value == null){ 
//				Util.logNullAtCell("getTableCellRendererComponent", (String) value, row, column);
//				//value = new Cell(row,column,"","",new CellFormat());
//			}
//			
//			//setBackground(Color.blue);
//			
//			//setFont(new Font(cell.getCellFormat().getFontName(), cell.getCellFormat().getFontStyle(), cell.getCellFormat().getFontSize()));
//			//setForeground(cell.getce)
//			// Once the expression is computed, its rendering is delegated
//			// to the default renderer set on the table according to the class of the result
//			return table.getDefaultRenderer (value.getClass ()).getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
//		}
//		else
//			// This renderer is supposed to be called only for Cell values
//			// but this provides a default rendering (and avoid stack overflow)
//			return super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
		
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		Cell c = (Cell)value;
		setValue(c.getValue());
		setBackground(c.getCellFormat().getBackgroundColor());
		
		setForeground(c.getCellFormat().getFontColor());
		//setHorizontalAlignment(c.getCellFormat().getHorizontalAlignment());
		//setVerticalAlignment(c.getCellFormat().getVerticalAlignment());
		setFont(new Font(c.getCellFormat().getFontName(), c.getCellFormat().getFontStyle(), c.getCellFormat().getFontSize()));
		String cell_value = (String)((Cell)value).getValue();
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cell_value);
		boolean isNumerical = false;
		while (matcher.find()) {
			isNumerical = true;
		}
		
		if (isNumerical == true){
			//Util.logn("Numerical cell found...");
			setHorizontalAlignment(new Integer(JLabel.RIGHT));
			
		}else{
			//Util.logn("Text cell found...");
			setHorizontalAlignment(new Integer(JLabel.LEFT));
		}
		
		if (isSelected){
			setBackground(NeoSplashUtil.selectedCellColor);
			Border b = new MatteBorder(1,1,1,1,Color.decode("#000000"));
			setBorder(b);

		}
		
		return cell;
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



	

	

