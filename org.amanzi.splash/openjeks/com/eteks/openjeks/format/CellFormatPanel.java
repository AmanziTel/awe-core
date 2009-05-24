/*
 * @(#)CellFormatPanel.java   09/03/2003
 *
 * Copyright (c) 1998-2003 Emmanuel PUYBARET / eTeks <info@eteks.com>. All Rights Reserved.
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
 * Visit eTeks web site for up-to-date versions of this file and other
 * Java tools and tutorials : http://www.eteks.com/
 */
package com.eteks.openjeks.format;

import java.awt.Color;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * Panel used to specify cell format.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @author  Emmanuel Puybaret, Jean-Baptiste C�r�zat, Yvonnick Esnault
 */
public class CellFormatPanel extends JPanel
{
  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");
  private JTabbedPane tabPane = null ;
  private CellFormat cellFormat ;
  private boolean isCellSet = false;

  public CellFormatPanel (CellFormat cellFormat)
  {
    /*
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
   	
    */
	  
    this.cellFormat = cellFormat ;
    if (this.cellFormat.getCellBorder() instanceof CellSetBorder )
      this.isCellSet = true;

    tabPane = new JTabbedPane ();

    tabPane.addTab(resourceBundle.getString ("FORMAT_NUMBER_TITLE"),
                   new FormatChooser (new Integer (10),cellFormat.getFormat()));

    tabPane.addTab(resourceBundle.getString ("FORMAT_ALIGNMENT_TITLE"),
                   new AlignmentChooser (cellFormat.getHorizontalAlignment(),cellFormat.getVerticalAlignment()));

    Font font;
    if (cellFormat.getFontName()!=null && cellFormat.getFontStyle()!=null && cellFormat.getFontSize()!=null)
      font=new Font(cellFormat.getFontName(),cellFormat.getFontStyle().intValue(),cellFormat.getFontSize().intValue());
    else
      font=null;

    tabPane.addTab(resourceBundle.getString ("FORMAT_FONT_TITLE"),
                   new FontChooser (cellFormat.getFontName(),cellFormat.getFontStyle(),cellFormat.getFontSize(), cellFormat.getFontColor(),cellFormat.getBackgroundColor()));

    tabPane.addTab(resourceBundle.getString ("FORMAT_BACKGROUND_TITLE"),
                   new BackgroundChooser(cellFormat.getBackgroundColor(),cellFormat.getFontColor(),font));

    tabPane.addTab(resourceBundle.getString ("FORMAT_BORDER_TITLE"),
                   new BorderChooser(cellFormat.getCellBorder()));
    add(tabPane);

  }

  private FormatChooser getFormatChooser()
  {
    return (FormatChooser)tabPane.getComponentAt(0);
  }

  private AlignmentChooser getAlignmentChooser()
  {
    return (AlignmentChooser)tabPane.getComponentAt(1);
  }

  private FontChooser getFontChooser()
  {
    return (FontChooser)tabPane.getComponentAt(2);
  }

  private BackgroundChooser getBackgroundChooser()
  {
    return (BackgroundChooser)tabPane.getComponentAt(3);
  }

  private BorderChooser getBorderChooser()
  {
    return (BorderChooser)tabPane.getComponentAt(4);
  }

  public CellFormat getCellFormat()
  {
     // We must create a new CellFormat and not use this.cellFormat ;
     CellFormat retCellFormat = new CellFormat();
     retCellFormat.setHorizontalAlignment(getAlignmentChooser().getHorizontalAlignment());
     retCellFormat.setVerticalAlignment(getAlignmentChooser().getVerticalAlignment());
     retCellFormat.setBackgroundColor(getBackgroundChooser().getBackgroundColor());

     try
     {
       retCellFormat.setFormat(getFormatChooser().getFormat());
     }
     catch(NullPointerException e)
     {
       retCellFormat.setFormat(CellFormat.UNKNOWN_FORMAT);
     }

     try
     {
       retCellFormat.setFontName(getFontChooser().getFontName());
     }
     catch (NullPointerException e)
     {
       retCellFormat.setFontName(CellFormat.UNKNOWN_FONTNAME);
     }

     try
     {
       retCellFormat.setFontStyle(getFontChooser().getFontStyle());
     }
     catch (NullPointerException e)
     {
       retCellFormat.setFontStyle(CellFormat.UNKNOWN_FONTSTYLE);
     }

     try
     {
       retCellFormat.setFontSize(getFontChooser().getFontSize());
     }
     catch (NullPointerException e)
     {
       retCellFormat.setFontSize(CellFormat.UNKNOWN_FONTSIZE);
     }

     try
     {
       retCellFormat.setFontColor(getFontChooser().getFontColor());
     }
     catch (NullPointerException e)
     {
       retCellFormat.setFontColor(CellFormat.UNKNOWN_FONTCOLOR);
     }

     BorderStyle top,bottom,left,right,internalHorizontal,internalVertical;
     top = bottom = left = right = internalHorizontal = internalVertical = null;

     Color topColor, bottomColor, leftColor, rightColor, internalHorizontalColor, internalVerticalColor;

     topColor = null;
     bottomColor = null;
     leftColor = null;
     rightColor = null;
     internalHorizontalColor = null;
     internalVerticalColor = null;

     //if (this.cellFormat.getCellBorder() instanceof CellSetBorder)
     //=> doesn't work, the getCellBorder give a CellBorder even if it is a cellSet.

     if (getBorderChooser().getTopColor() != null)
     {
       topColor = getBorderChooser().getTopColor();
       top = getBorderChooser().getTopStyle();
     }

     if (getBorderChooser().getBottomColor() != null)
     {
       bottomColor = getBorderChooser().getBottomColor();
       bottom = getBorderChooser().getBottomStyle();
     }

     if (getBorderChooser().getLeftColor() != null)
     {
       leftColor = getBorderChooser().getLeftColor();
       left = getBorderChooser().getLeftStyle();
     }

     if (getBorderChooser().getRightColor() != null)
     {
       rightColor = getBorderChooser().getRightColor();
       right = getBorderChooser().getRightStyle();
     }


     if (isCellSet == false)
     {
      CellBorder cellBorder = new CellBorder(top,bottom,left,right,
                            topColor, bottomColor, leftColor, rightColor);
      retCellFormat.setCellBorder((CellBorder)cellBorder);
     }
     else
     {

       if (getBorderChooser().getInternalHorizontalColor() != null)
       {
         internalHorizontalColor = getBorderChooser().getInternalHorizontalColor();
         internalHorizontal = getBorderChooser().getInternalHorizontalStyle();
       }

       if (getBorderChooser().getInternalVerticalColor() != null)
       {
         internalVerticalColor = getBorderChooser().getInternalVerticalColor();
          internalVertical = getBorderChooser().getInternalVerticalStyle();
       }

        //CellSetBorder cellSetBorder = (CellSetBorder)cellBorder; // CastException
        CellSetBorder cellSetBorder = new CellSetBorder();
        cellSetBorder.setTopColor(topColor);
        cellSetBorder.setBottomColor(bottomColor);
        cellSetBorder.setLeftColor(leftColor);
        cellSetBorder.setRightColor(rightColor);
        cellSetBorder.setInternalHorizontalColor(internalHorizontalColor);
        cellSetBorder.setInternalVerticalColor(internalVerticalColor);

        cellSetBorder.setTopStyle(top);
        cellSetBorder.setBottomStyle(bottom);
        cellSetBorder.setLeftStyle(left);
        cellSetBorder.setRightStyle(right);
        cellSetBorder.setInternalHorizontalStyle(internalHorizontal);
        cellSetBorder.setInternalVerticalStyle(internalVertical);

        retCellFormat.setCellBorder(cellSetBorder);
      }

      return retCellFormat;
  }
}

