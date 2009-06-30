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

package com.eteks.openjeks.format ;

/**
 * TableFormat. This class stores the CellFormat for a cell
 * It's an alternative to {@link com.eteks.openjeks.format.TableFormat}
 * FIXME : border and internal border for a selection : ajustCellBorderPanel have one or many bugs
 *
 * @author Yvonnick Esnault
 */

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Comparator;
import java.awt.Color;

public class CellTableFormat {

//  private CellFormat defaultCellFormat;
  private TreeSet listCell;
  private int priority;
  
  public CellTableFormat ()
  {
    CellComparator cellComparator = new CellComparator ();
    listCell = new TreeSet(cellComparator);
    this.priority = 0;
  }

  // Only for the renderer
  // retrieve never UNKNKOW type for one attribute
  public CellFormat getFormatAt (int rowIndex, int columnIndex)
  {
    Cell cell;
    CellFormat cellFormat = new CellFormat();;
    cellFormat.setCellBorder(new CellBorder());

     // get the cellFormat for the cell
    Iterator obj = listCell.iterator(); 
    while(obj.hasNext()){
      cell = (Cell) obj.next() ;
      if ( (cell.getRow() == rowIndex) && (cell.getColumn() == columnIndex) ) {
        cellFormat = cell.getCellFormat();
        break;
      }
    }
    return cellFormat;
  }
  
  // Not for the renderer,
  // retrieve UNKOWN type if necessary for any attribute
  public CellFormat getFormatAt (int firstRow, int firstColumn, int lastRow, int lastColumn)
  {
    Cell cell = null;
    CellFormat cellFormat = null;
    int row=-1, column=-1;
    boolean flag = false; 
    boolean top = false, bottom = false, left = false, right = false; 
    Iterator obj;
    
    // if it is one SplashCell
    if ( (firstRow == lastRow) && (firstColumn == lastColumn) )
    {
	cellFormat = getFormatAt(firstRow, firstColumn) ;
        return cellFormat;
    }
    
    // Search if any CellFormat is set for each cell in CellSet selection
    obj = listCell.iterator();
    while(obj.hasNext()){
      cell = (Cell)obj.next();
      row = cell.getRow();
      column = cell.getColumn();
      if ( (firstRow <= row) && (lastRow >= row) &&
           (firstColumn <= column) && (lastColumn >= column)
	 )
      {
        if (row == firstRow)
           top = true;
	
        if (row == lastRow)
           bottom = true;
	
        if (column == firstColumn)
           left = true;
	
        if (column == lastColumn)
           right = true;
	
	if (flag == false)
	{
          cellFormat = new CellFormat();
          cellFormat.setCellBorder(new CellSetBorder());
	  cellFormat = copyCellFormat(cell.getCellFormat());
	  flag = true;
	} else {
          cellFormat = ajustCellFormat(cellFormat, cell.getCellFormat());
	}
      }
    }

    // Ajust with defaultCellFormat
    if ( (flag == true ) && ( (top == false) || (bottom == false) || (left == false) || (right == false) ) )
    {
      CellFormat defaultCellFormat = new CellFormat();
      defaultCellFormat.setCellBorder(new CellBorder());
      cellFormat = ajustCellFormat(cellFormat, defaultCellFormat);
    }

    if (flag == false)  // If no CellFormat is found
    {
      cellFormat = new CellFormat();
      cellFormat.setCellBorder(new CellBorder());
    }

    // Ajust Border
    cellFormat.setCellBorder(ajustCellBorderPanel(firstRow, firstColumn,
                                                  lastRow, lastColumn));
    
    return cellFormat;
  }
 
  public void setFormatAt (CellFormat cellFormat, int firstRow, int firstColumn, int lastRow, int lastColumn)
  {
    Cell cell = null;
    boolean flag = false;
    CellFormat cellFormatb = null;
    int row=-1, column=-1;
    
    // If it is only one cell
    if ( (firstRow == lastRow) && (firstColumn == lastColumn) )
    {
      setFormatAt(cellFormat, firstRow, firstColumn);
      return;
    }
    
    Iterator obj = listCell.iterator();
    for (int i = firstRow; i <= lastRow; i++)
      for (int j = firstColumn; j <= lastColumn; j++)
      {
	flag = false;
        cellFormatb = copyCellFormat(cellFormat);
        while(obj.hasNext()){
          cell = (Cell)obj.next() ;
          row = cell.getRow();
          column = cell.getColumn();
          cellFormatb.setCellBorder(ajustCellBorder(cellFormatb, row, column, firstRow,
                                                    firstColumn, lastRow, lastColumn));
          if ( (row == i) && (column == j)  )
          {
            cell.setCellFormat(cellFormatb, this.priority++);
            flag = true;
	    break;
         }
        }
	if (flag == false)
	{
	  cell = new Cell(cellFormatb, this.priority++,  i, j);
          listCell.add (cell);  
	}
      }
  }
 
  private void setFormatAt (CellFormat cellFormat, int rowIndex, int columnIndex)
  {
     Cell cell;
     boolean flag=false;

     // TODO
     // check if one or many attributes is different with
     // the defaultCellFormat 

    Iterator obj = listCell.iterator();
     
    while(obj.hasNext()){
      cell = (Cell)obj.next() ;
      if ( (cell.getRow() == rowIndex) && (cell.getColumn() == columnIndex) )
      {
	cell.setCellFormat(cellFormat, this.priority++);
	flag = true;
      }
    }
    if (flag == false)
    {
      cell = new Cell(cellFormat, this.priority++,  rowIndex, columnIndex);
      listCell.add (cell);
    }
  }

  // Ajust Border for CellFormatPanel
  //
  private CellBorder ajustCellBorderPanel(int firstRow, int firstColumn,
                                          int lastRow, int lastColumn)
  {	  
    CellSetBorder cb = new CellSetBorder();
    Cell cell = null;
    int row = -1, column = -1;
    int top = -1, bottom = -1, left = -1, right =-1, horizontal = -1, vertical = -1;
    boolean ftop = false, fbottom = false, fleft = false, fright = false, fhorizontal = false, fvertical = false;
    
    Color topColor = CellBorder.UNKNOWN_BORDERCOLOR;
    Color bottomColor = CellBorder.UNKNOWN_BORDERCOLOR;
    Color leftColor = CellBorder.UNKNOWN_BORDERCOLOR;
    Color rightColor = CellBorder.UNKNOWN_BORDERCOLOR;
    Color horizontalColor = CellBorder.UNKNOWN_BORDERCOLOR;
    Color verticalColor = CellBorder.UNKNOWN_BORDERCOLOR;

    BorderStyle topStyle = CellBorder.UNKNOWN_BORDERSTYLE;
    BorderStyle bottomStyle = CellBorder.UNKNOWN_BORDERSTYLE;
    BorderStyle leftStyle = CellBorder.UNKNOWN_BORDERSTYLE;
    BorderStyle rightStyle = CellBorder.UNKNOWN_BORDERSTYLE;
    BorderStyle horizontalStyle = CellBorder.UNKNOWN_BORDERSTYLE;
    BorderStyle verticalStyle = CellBorder.UNKNOWN_BORDERSTYLE;

    Iterator obj = listCell.iterator();

    for (int i = firstRow; i <= lastRow; i++)
      for (int j = firstColumn; j <= lastColumn; j++)
      {

         while(obj.hasNext()){
           cell = (Cell)obj.next() ;
           row = cell.getRow();
           column = cell.getColumn();
           if ( (firstRow <= row) && (lastRow >= row) &&
                (firstColumn <= column) && (lastColumn >= column)
              )
           {
             if ( (row == firstRow)  && (ftop == false))
	     {
               top++;
               if ( (top == 0) && (ftop == false) )
	       {
		 topColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getTopColor();
		 topStyle =  ((CellBorder)cell.getCellFormat().getCellBorder()).getTopStyle();
	       }
	       else if (topColor != ((CellBorder)cell.getCellFormat().getCellBorder()).getTopColor() )
	       {
		 ftop = true; top =-1;
	       }
	     }
             
	     if ( (row == lastRow) && (fbottom == false))
	     {
               bottom++;
               if ( (bottom == 0) && (fbottom == false) )
	       {
		 bottomColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getBottomColor();
		 bottomStyle =  ((CellBorder)cell.getCellFormat().getCellBorder()).getBottomStyle();
	       }
	       else if (bottomColor != ((CellBorder)cell.getCellFormat().getCellBorder()).getBottomColor() )
	       {
		 fbottom = true; bottom = -1;
	       }
	     }
             if ( (column == firstColumn) && (fleft == false))
	     {
               left++;         
               if ( (left == 0) && (fleft == false) )
	       {
		 leftColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getLeftColor();
		 leftStyle =  ((CellBorder)cell.getCellFormat().getCellBorder()).getLeftStyle();
	       }
	       else if (leftColor != ((CellBorder)cell.getCellFormat().getCellBorder()).getLeftColor() )
	       {
		 fleft = true; left = -1;
	       }
	     }

             if ( (column == lastColumn) && (fright == false))
	     {
               right++;
               if ( (right == 0) && (fright == false) )
	       {
		 rightColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getRightColor();
		 rightStyle =  ((CellBorder)cell.getCellFormat().getCellBorder()).getRightStyle();
	       }
	       else if (rightColor != ((CellBorder)cell.getCellFormat().getCellBorder()).getRightColor() )
	       {
		 fright = true; right = -1;
	       }
	     }

             if ( ( (row != firstRow) || (row != lastRow) )  && (fhorizontal == false))
	     {
               horizontal++;
	       if ( (fhorizontal == false) && (horizontal >0) )
	       {
	         if ( ( (row != firstRow) && (horizontalColor !=  ((CellBorder)cell.getCellFormat().getCellBorder()).getTopColor()) ) ||
		      ( (row != lastRow) && (horizontalColor !=  ((CellBorder)cell.getCellFormat().getCellBorder()).getBottomColor()) )
		    )
		 {
		   fhorizontal = true; horizontal = -1;
		 }
	       }
	       else if ( (horizontal == 0) && (fhorizontal == false) )
	       {
		 if (row != firstRow) horizontalColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getTopColor();
		 if (row != lastRow)  horizontalColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getBottomColor();
	       }
	     }

             if ( ( (column != firstColumn) || (column != lastColumn) )  && (fvertical == false))
	     {
               vertical++;
	       if ( (fvertical == false) && (vertical >0) )
	       {
	         if ( ( (column != firstColumn) && (verticalColor !=  ((CellBorder)cell.getCellFormat().getCellBorder()).getLeftColor()) ) ||
		      ( (column != lastColumn) && (verticalColor !=  ((CellBorder)cell.getCellFormat().getCellBorder()).getRightColor()) )
		    )
		 {
		   fvertical = true; vertical = -1;
		 }
	       }
	       else if ( (vertical == 0) && (fvertical == false) )
	       {
		 if (column != firstColumn) verticalColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getLeftColor();
		 if (column != lastColumn)  verticalColor =  ((CellBorder)cell.getCellFormat().getCellBorder()).getRightColor();
	       }
	     }

             if (top != (lastColumn - firstColumn))
	     {
               topColor = CellBorder.UNKNOWN_BORDERCOLOR;
	       topStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	     if (bottom != (lastColumn - firstColumn))
	     {
	       bottomColor = CellBorder.UNKNOWN_BORDERCOLOR;
               bottomStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	      if (left != (lastRow - firstRow))
	     {
	       leftColor =  CellBorder.UNKNOWN_BORDERCOLOR;
	       leftStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	     if (right != (lastRow - firstRow))
	     {
	       rightColor = CellBorder.UNKNOWN_BORDERCOLOR;
	       rightStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	     if (horizontal != (lastRow - firstRow) * (lastColumn - firstColumn) - (lastRow - firstRow))
	     {
	       horizontalColor = CellBorder.UNKNOWN_BORDERCOLOR;
	       horizontalStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	     if (vertical != (lastRow - firstRow) * (lastColumn - firstColumn) - (lastRow - firstRow))
	     {
	       verticalColor = CellBorder.UNKNOWN_BORDERCOLOR;
	       verticalStyle = CellBorder.UNKNOWN_BORDERSTYLE;
	     }
	   }
	 }
      }

    cb.setTopColor(topColor);
    cb.setTopStyle(topStyle);
    cb.setBottomColor(bottomColor);
    cb.setBottomStyle(bottomStyle);
    cb.setLeftColor(leftColor);
    cb.setLeftStyle(leftStyle);
    cb.setRightColor(rightColor);
    cb.setRightStyle(rightStyle);
    cb.setInternalHorizontalColor(horizontalColor);
    cb.setInternalHorizontalStyle(horizontalStyle);
    cb.setInternalVerticalColor(verticalColor);
    cb.setInternalVerticalStyle(verticalStyle);

    return cb;
  }
  
  private CellBorder ajustCellBorder(CellFormat c2,
			             int row, int column, int firstRow, int firstColumn,
				      int lastRow, int lastColumn)
  {
    CellBorder cb1 = new CellBorder();
    CellBorder cb2 = (CellBorder)c2.getCellBorder();

    if (row == firstRow)
    {
      cb1.setTopColor(cb2.getTopColor());
      cb1.setTopStyle(cb2.getTopStyle());
    }
      
    if (row == lastRow)
    {
     cb1.setBottomColor(cb2.getBottomColor());
     cb1.setBottomStyle(cb2.getBottomStyle());
    }
      
    if (column == firstColumn)
    {
      cb1.setLeftColor(cb2.getLeftColor());
      cb1.setLeftStyle(cb2.getLeftStyle());
    }
      
    if (column == lastColumn)
    {
      cb1.setRightColor(cb2.getRightColor());
      cb1.setRightStyle(cb2.getRightStyle());
    }
   
    if (cb2 instanceof CellSetBorder)
    {
      CellSetBorder csb2 = new CellSetBorder();
	      
      if (row != firstRow)
      {
         cb1.setTopColor(csb2.getInternalHorizontalColor());
         cb1.setTopStyle(csb2.getInternalHorizontalStyle());
      }
      if (row != lastRow)
      {
         cb1.setBottomColor(csb2.getInternalHorizontalColor());
         cb1.setBottomStyle(csb2.getInternalHorizontalStyle());       
      }
      if (column != firstColumn)
      {
         cb1.setLeftColor(csb2.getInternalVerticalColor());
         cb1.setLeftStyle(csb2.getInternalVerticalStyle());       
      }
      if (column != lastColumn)
      {
         cb1.setRightColor(csb2.getInternalVerticalColor());
         cb1.setRightStyle(csb2.getInternalVerticalStyle());       
      }
    }
    return cb1; 
  }
  
  // Not for the renderer !!
  private CellFormat ajustCellFormat(CellFormat c1, CellFormat c2) 
  {
    if (c1.getHorizontalAlignment().intValue() != c2.getHorizontalAlignment().intValue())
      c1.setHorizontalAlignment(c1.UNKNOWN_ALIGNMENT);
    if (c1.getVerticalAlignment().intValue() != c2.getVerticalAlignment().intValue())
      c1.setVerticalAlignment(c2.UNKNOWN_ALIGNMENT);
    if (c1.getBackgroundColor() != c2.getBackgroundColor())
      c1.setBackgroundColor(c2.UNKNOWN_BACKGROUNDCOLOR);
    if (c1.getFormat() != c2.getFormat())
      c1.setFormat(c2.UNKNOWN_FORMAT);
    if (c1.getFontName() != c2.getFontName())
      c1.setFontName(c2.UNKNOWN_FONTNAME);
    if (c1.getFontStyle().equals(c2.getFontStyle()) == false)
      c1.setFontStyle(c2.UNKNOWN_FONTSTYLE);
    if (c1.getFontSize().equals(c2.getFontSize()) == false)
      c1.setFontSize(c2.UNKNOWN_FONTSIZE);
    if (c1.getFontColor() != c2.getFontColor())
      c1.setFontColor(c2.UNKNOWN_FONTCOLOR);

    return c1 ;
  }

  // Perhaps a best method exist to make a CellFormat
  // whith attributes fixed...
  private CellFormat copyCellFormat(CellFormat c1)
  {
    CellFormat c = new CellFormat();

    c.setHorizontalAlignment(c1.getHorizontalAlignment());
    c.setVerticalAlignment(c1.getVerticalAlignment());
    c.setBackgroundColor(c1.getBackgroundColor());
    c.setFormat(c1.getFormat());
    c.setFontName(c1.getFontName());
    c.setFontStyle(c1.getFontStyle());
    c.setFontSize(c1.getFontSize());
    c.setFontColor(c1.getFontColor());
    
    if (c1.getCellBorder() instanceof CellSetBorder) {
      CellSetBorder cellSetBorder = new CellSetBorder();
      cellSetBorder.setTopColor(((CellSetBorder)c1.getCellBorder()).getTopColor());
      cellSetBorder.setBottomColor(((CellSetBorder)c1.getCellBorder()).getBottomColor());
      cellSetBorder.setLeftColor(((CellSetBorder)c1.getCellBorder()).getLeftColor());
      cellSetBorder.setRightColor(((CellSetBorder)c1.getCellBorder()).getRightColor());
      cellSetBorder.setInternalHorizontalColor(((CellSetBorder)c1.getCellBorder()).getInternalHorizontalColor());
      cellSetBorder.setInternalVerticalColor(((CellSetBorder)c1.getCellBorder()).getInternalVerticalColor());
      
      cellSetBorder.setTopStyle(((CellSetBorder)c1.getCellBorder()).getTopStyle());
      cellSetBorder.setBottomStyle(((CellSetBorder)c1.getCellBorder()).getBottomStyle());
      cellSetBorder.setLeftStyle(((CellSetBorder)c1.getCellBorder()).getLeftStyle());
      cellSetBorder.setRightStyle(((CellSetBorder)c1.getCellBorder()).getRightStyle());
      cellSetBorder.setInternalHorizontalStyle(((CellSetBorder)c1.getCellBorder()).getInternalHorizontalStyle());
      cellSetBorder.setInternalVerticalStyle(((CellSetBorder)c1.getCellBorder()).getInternalVerticalStyle());
      c.setCellBorder(cellSetBorder);
      
    } else if (c1.getCellBorder() instanceof CellBorder) {
      CellBorder cellBorder = new CellBorder();
      cellBorder.setTopColor(((CellBorder)c1.getCellBorder()).getTopColor());
      cellBorder.setBottomColor(((CellBorder)c1.getCellBorder()).getBottomColor());
      cellBorder.setLeftColor(((CellBorder)c1.getCellBorder()).getLeftColor());
      cellBorder.setRightColor(((CellBorder)c1.getCellBorder()).getRightColor());
      
      cellBorder.setTopStyle(((CellBorder)c1.getCellBorder()).getTopStyle());
      cellBorder.setBottomStyle(((CellBorder)c1.getCellBorder()).getBottomStyle());
      cellBorder.setLeftStyle(((CellBorder)c1.getCellBorder()).getLeftStyle());
      cellBorder.setRightStyle(((CellBorder)c1.getCellBorder()).getRightStyle());
      c.setCellBorder(cellBorder);
    }

   // CellFormat c = (CellFormat)c1.clone();
    return c;
  }

  private class Cell
  {
    private int priority, row, column;
    private CellFormat cellCellFormat;

    public Cell (CellFormat cellCellFormat, int priority, int row, int column)
    {
      this.row = row;
      this.column = column;
      this.priority = priority;
      this.cellCellFormat = new CellFormat();
      
      setCellFormat(cellCellFormat);
    }
    
    public CellFormat getCellFormat ()
    {
      return this.cellCellFormat;
    }
    
    public void setCellFormat(CellFormat cellCellFormat, int priority)
    {
      this.priority = priority;
      setCellFormat(cellCellFormat);
    }
    
    public void setCellFormat(CellFormat c1)
    {

      if (c1.getHorizontalAlignment().intValue() != CellFormat.UNKNOWN_ALIGNMENT.intValue())
        this.cellCellFormat.setHorizontalAlignment(c1.getHorizontalAlignment());
      if (c1.getVerticalAlignment().intValue() != CellFormat.UNKNOWN_ALIGNMENT.intValue())
        this.cellCellFormat.setVerticalAlignment(c1.getVerticalAlignment());
      if (c1.getBackgroundColor() != CellFormat.UNKNOWN_BACKGROUNDCOLOR)
        this.cellCellFormat.setBackgroundColor(c1.getBackgroundColor());
      if (c1.getFormat() != CellFormat.UNKNOWN_FORMAT)
        this.cellCellFormat.setFormat(c1.getFormat());
      if (c1.getFontName() != CellFormat.UNKNOWN_FONTNAME)
        this.cellCellFormat.setFontName(c1.getFontName());
      if (c1.getFontStyle().equals(CellFormat.UNKNOWN_FONTSTYLE) == false)
        this.cellCellFormat.setFontStyle(c1.getFontStyle());
      if (c1.getFontSize().equals(CellFormat.UNKNOWN_FONTSIZE) == false)
        this.cellCellFormat.setFontSize(c1.getFontSize());
      if (c1.getFontColor() != CellFormat.UNKNOWN_FONTCOLOR)
        this.cellCellFormat.setFontColor(c1.getFontColor());
      
      CellBorder cb = (CellBorder)c1.getCellBorder();

      if (cb.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        ((CellBorder)this.cellCellFormat.getCellBorder()).setTopColor(cb.getTopColor());
        ((CellBorder)this.cellCellFormat.getCellBorder()).setTopStyle(cb.getTopStyle());
      }
      if (cb.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        ((CellBorder)this.cellCellFormat.getCellBorder()).setBottomColor(cb.getBottomColor());
        ((CellBorder)this.cellCellFormat.getCellBorder()).setBottomStyle(cb.getBottomStyle());
      }
      if (cb.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        ((CellBorder)this.cellCellFormat.getCellBorder()).setLeftColor(cb.getLeftColor());
        ((CellBorder)this.cellCellFormat.getCellBorder()).setLeftStyle(cb.getLeftStyle());
      }
      if (cb.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        ((CellBorder)this.cellCellFormat.getCellBorder()).setRightColor(cb.getRightColor());
        ((CellBorder)this.cellCellFormat.getCellBorder()).setRightStyle(cb.getRightStyle());
      }
    }
    
    public int getRow ()
    {
      return this.row;
    }
    
    public int getColumn ()
    {
      return this.column;
    }
    
    public int getPriority()
    {
      return this.priority;
    }
    
    public void setPriority(int priority)
    {
      this.priority = priority;
    }
    
  }

  private class CellComparator implements Comparator
  {
    public int compare(Object obj1, Object obj2)
    {
      int row1 = ((Cell)obj1).getRow();
      int row2 = ((Cell)obj2).getRow();
      int column1 = ((Cell)obj1).getColumn();
      int column2 = ((Cell)obj2).getColumn();
      int val = 0 ;
      
      if (row1 < row2)
        val = -1 ;
       else if (row1 > row2)
        val = 1;
       else if (row1 == row2)
        if (column1 < column2)
          val = -1;
	else if (column1 > column2)
	  val = 1;
	else if (column1 == column2)
	  val = 0;

       return val ;
    } 
  }
}

