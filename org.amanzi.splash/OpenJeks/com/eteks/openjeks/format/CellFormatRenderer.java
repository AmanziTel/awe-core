/*
 * Copyright (c) 2004 Emmanuel PUYBARET / eTeks <info@eteks.com>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Visit sourceforge web site for up-to-date of this file
 * http://sourceforge.net/project/openjeks
 *
 * Visit eTeks web site for up-to-date versions of Jeks Spreadsheet and other
 * Java tools and tutorials : http://www.eteks.com/
 *
 */

package com.eteks.openjeks.format;

/**
 * SplashCell renderer for formated cells.
 *
 * @author Yvonnick Esnault
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;


public class CellFormatRenderer extends DefaultTableCellRenderer {

  private TableFormat tableFormat;
  private JLabel render;
  private Font font;
  private Color color;
  private CellFormat cellFormat;
  private MatteBorder borderSelected;
  private EmptyBorder noFocusBorder;

  public CellFormatRenderer (TableFormat tableFormat)
  {

    super();
    this.tableFormat = tableFormat;
    // Color of border for the cell on the focus
    this.borderSelected = new MatteBorder(1, 1, 1, 1,Color.lightGray);

    noFocusBorder = new EmptyBorder(1, 2, 1, 2);
    setOpaque(true);
    setBorder(noFocusBorder);
  }

  public Component getTableCellRendererComponent (JTable table, Object value,
                                                  boolean isSelected, boolean hasFocus,
                                int row, int column)
  {
    String d = "";
    Date dt = null;
    boolean isDecimal = false;
    cellFormat = tableFormat.getFormatAt(row,column) ;

    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    render = (JLabel)comp;

    // background color if the SplashCell is not selected
    if (! isSelected)
      render.setBackground(cellFormat.getBackgroundColor()) ;

    // font
    font = new Font(cellFormat.getFontName(), cellFormat.getFontStyle().intValue(), cellFormat.getFontSize().intValue());
    render.setFont(font);

    // text color
    Color color = cellFormat.getFontColor() ;
    if (color != null)
       render.setForeground(color);
    
    // format
    if (value != null)
    {

       if (cellFormat.getFormat() instanceof SimpleDateFormat)
       {
         SimpleDateFormat simpleDateFormat = (SimpleDateFormat)cellFormat.getFormat();
	   	    
         // dateFormat.setLenient(true);

	 // FIXME this code is deprecated, but the parse methode of class DateFormat
	 // don't work as well as the parsing of the constructor Date(String)
	 // DateFormat.parse don't recognize date with a string like : 04/19/1983
	 try {  
           dt = new Date((String)value); // this work
	 } catch (IllegalArgumentException e) {
	 }
	   
	 if (dt != null)
	   render.setText(simpleDateFormat.format(dt));
       }

       
       try{
    	   //Util.log("(String)value:" + value);
         //d = Long.toString(value);
         	isDecimal = true;
       }catch (NumberFormatException e) {
       }
       
       //Alignment and Format
       if (isDecimal)
       {
         if (cellFormat.getFormat() instanceof DecimalFormat)
           render.setText(((DecimalFormat)cellFormat.getFormat()).format(d));
         else if ( ((cellFormat.getFormat() instanceof MessageFormat) == false) &&
                   (cellFormat.getHorizontalAlignment().equals(CellFormat.STANDARD_ALIGNMENT))
                 )
           render.setHorizontalAlignment(JLabel.RIGHT);
	 else
           render.setHorizontalAlignment(cellFormat.getHorizontalAlignment().intValue());
       } else {
         if (cellFormat.getHorizontalAlignment().equals(CellFormat.STANDARD_ALIGNMENT))
            render.setHorizontalAlignment(JLabel.LEFT);
	 else
           render.setHorizontalAlignment(cellFormat.getHorizontalAlignment().intValue());
       }
 
       render.setVerticalAlignment(cellFormat.getVerticalAlignment().intValue());
       
    }

    //render.setBorder(cellFormat.getCellBorder());
    //JLabel test = new JLabel();
    //test = (JLabel)comp.getParent();
    super.setBorder((CellBorder)cellFormat.getCellBorder());
      
    // borders
    if (hasFocus)
      render.setBorder(borderSelected);
    else
      render.setBorder((CellBorder)cellFormat.getCellBorder()) ;

//    render.setText(value.toString()) ;

    return render ;
  }
}

