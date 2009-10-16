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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.amanzi.splash.utilities.NeoSplashUtil;

import com.eteks.openjeks.format.CellFormat;

public class SplashCellRenderer extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4271039280304551527L;
	/**
	 * Constructor 
	 */
	public SplashCellRenderer ()
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
	    Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		Cell c = (Cell)value;
		Object cellValue = getExpressionValue(c);
		setValue(cellValue);
		setBackground(c.getCellFormat().getBackgroundColor());
		
		setForeground(c.getCellFormat().getFontColor());

		setFont(new Font(c.getCellFormat().getFontName(), c.getCellFormat().getFontStyle(), c.getCellFormat().getFontSize()));
		String cell_value = cellValue.toString();
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cell_value);
        Integer horizontalAlignment = c.getCellFormat().getHorizontalAlignment();
        if ((int)horizontalAlignment != CellFormat.STANDARD_ALIGNMENT && (int)horizontalAlignment != CellFormat.UNKNOWN_ALIGNMENT) {
            setHorizontalAlignment(horizontalAlignment);
        } else {
            boolean isNumerical = false;
            while (matcher.find()) {
                isNumerical = true;
            }
            if (isNumerical == true) {
                // Util.logn("Numerical cell found...");
                setHorizontalAlignment(new Integer(JLabel.RIGHT));

            } else {
                // Util.logn("Text cell found...");
                setHorizontalAlignment(new Integer(JLabel.LEFT));
            }
        }

        Integer verticalAlignment = c.getCellFormat().getVerticalAlignment();
        if ((int)verticalAlignment != CellFormat.STANDARD_ALIGNMENT && (int)verticalAlignment != CellFormat.UNKNOWN_ALIGNMENT) {
            setVerticalAlignment(verticalAlignment);
        } else {
            setVerticalAlignment(JLabel.CENTER);
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
	    Cell cell = (Cell)expression;
	    
	    //Lagutko, 5.10.2009, format value of Cell to String
	    Format format = cell.getCellFormat().getFormat();
	    Object value = cell.getValue();
	    //start formatting only if Format is set and Value not empty
	    if ((format != null) && 
	        (value != null) && 
	        (value.toString().length() > 0) &&
	        !(format instanceof MessageFormat)) {
	        return format.format(value);	        
	    }
	    
		return value;
	}
}



	

	

