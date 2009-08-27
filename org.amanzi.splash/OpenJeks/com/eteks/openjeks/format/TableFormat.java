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

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Comparator;

/**
 * The <code>TableFormat</code> is used to store and manipulate
 * {@link com.eteks.openjeks.format.CellFormat} for each SplashCell of a JTable.
 *
 * <pre>
 * JTable table = new JTable();
 * TableFormat tableFormat = new TableFormat();
 * CellFormatRenderer renderer = new CellFormatRenderer (tableFormat);
 * table.setDefaultRenderer(Object.class, renderer);
 * table.setDefaultRenderer(Long.class, renderer);
 * table.setDefaultRenderer(Double.class, renderer);
 * table.setDefaultRenderer(String.class, renderer);
 * </pre>
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @see com.eteks.openjeks.format.CellFormat
 * @author Yvonnick Esnault
 * 
 */
public class TableFormat {

  private TreeSet listCell, listCellSet;
  private boolean paintDefault = true;
  
  // when a new cellFormat is set or update
  // always update the priority.
  // A SplashCell have not a priority
  private int priority; 

  private int firstFormatRow = -1;
  private int firstFormatColumn = -1;
  private int lastFormatRow = -1;
  private int lastFormatColumn = -1;
  
  public TableFormat ()
  {
    CellComparator cellComparator = new CellComparator ();
    CellSetComparator cellSetComparator = new CellSetComparator ();
    listCell = new TreeSet(cellComparator);
    listCellSet = new TreeSet(cellSetComparator); 
    this.priority = 0;
  }

  /**
   * Return a {@link com.eteks.openjeks.format.CellFormat} for the selected cell.
   * <br />
   * This function should be used only by a renderer. 
   *
   * @param rowIndex Index of the row
   * @param columnIndex Index of the column
   * @return {@link com.eteks.openjeks.format.CellFormat}
   */
  public CellFormat getFormatAt (int rowIndex, int columnIndex)
  {
    Cell cell;
    CellSet cellSet;
    CellFormat cellFormat = new CellFormat(); //defaultCellFormat;
    CellFormat defaultCellFormatTmp = new CellFormat(); //defaultCellFormat;
    CellFormat cellFormatb = null;
    int basePriority = -1;
    boolean knowed = false;
    
    // get the cellFormat from one or more cellSet
    Iterator obj = listCellSet.iterator();
    while(obj.hasNext()){
      cellSet = (CellSet)obj.next() ;

      if ( (rowIndex >= cellSet.getFirstRow()) && (rowIndex <= cellSet.getLastRow()) &&
	   (columnIndex >= cellSet.getFirstColumn()) && (columnIndex <= cellSet.getLastColumn()) )

      {
        if (knowed == false)
	{
          basePriority = cellSet.getPriority();
          defaultCellFormatTmp.setCellBorder(new CellSetBorder());
	  cellFormat = copyCellFormat(cellSet.getCellFormat());
          cellFormat = ajustCellFormatRenderer(cellFormat, defaultCellFormatTmp);
	}

        if (knowed == true)
        {
	  cellFormatb = copyCellFormat(cellSet.getCellFormat());
	  if (basePriority > cellSet.getPriority())
            cellFormat = ajustCellFormatRenderer(cellFormat, cellFormatb);
		
          else
          {
            basePriority = cellSet.getPriority();
            cellFormat = ajustCellFormatRenderer(cellFormatb, cellFormat);
          }
	}
        knowed = true;
       }   
    }

     // get the cellFormat for the cell
    obj = listCell.iterator(); 
    while(obj.hasNext()){
      cell = (Cell) obj.next() ;
      if ( (cell.getRow() == rowIndex) && (cell.getColumn() == columnIndex) ) {
        cellFormatb = copyCellFormat(cell.getCellFormat());
        cellFormat = ajustCellFormatRenderer(cellFormatb, cellFormat);
        knowed = true;
        break;
      }
    }
    
    if (knowed == false)  // If no CellFormat is found
    {
      cellFormat = new CellFormat(); 
      cellFormat.setCellBorder(new CellBorder());
    }
  
    // Fix Border
    cellFormat = ajustCellBorderRenderer(cellFormat, rowIndex, columnIndex);
    ((CellBorder)cellFormat.getCellBorder()).setPaintDefault(this.paintDefault);
    return cellFormat;
  }
  
 /**
  * Set on or off the paint of the default border.
  * this can be useful for print the table
  * 
  * @see com.eteks.openjeks.format.CellBorder
  * @param defaultPaint true or false
  *
  */
  public void setPaintDefault(boolean defaultPaint)
  {
    this.paintDefault = defaultPaint;
  }
  
  /**
   * Get true or false if the default borders are painted.
   * this can be useful for print the table
   * 
   * @see com.eteks.openjeks.format.CellBorder
   * @return boolean true if default borders are painted
   *
   */
  public boolean getPaintDefault()
  {
    return this.paintDefault;
  }
  
  /**
   * Return a <code>CellFormat</code> for selected cell or cell.
   * This method should not be used by a renderer. Set attribute of CellFormat
   * to UNKNOWN (cf. {@link com.eteks.openjeks.format.CellFormat}) if necessary.
   *
   * @see com.eteks.openjeks.format.CellFormat
   * @param firstRow Index of the first row
   * @param firstColumn Index of the first column
   * @param lastRow Index of the last row
   * @param lastColumn Index of the last column
   * @return CellFormat
   *
   */
  public CellFormat getFormatAt (int firstRow, int firstColumn, int lastRow, int lastColumn)
  {
    CellFormat cellFormat = new CellFormat();
    
     // if it is one SplashCell
    if ( (firstRow == lastRow) && (firstColumn == lastColumn) )
    {
	cellFormat = getFormatAt(firstRow, firstColumn);
        cellFormat = ajustCellBorderToUnknown(cellFormat);
	
     // If it is a CellSet
    } else {
      cellFormat.setCellBorder(new CellSetBorder());
      // Ajust attributes
      cellFormat = ajustCellSetFormatPanel(cellFormat, firstRow, lastRow, firstColumn, lastColumn);
    }
    return cellFormat;
  }

  /**
   * Set a {@link com.eteks.openjeks.format.CellFormat} to the SplashCell or the CellSet.
   * Should be used after used {@link com.eteks.openjeks.format.CellFormatPanel}
   * 
   * @param cellFormat CellFormat to set to the selected SplashCell or CellSet
   * @param firstRow Index of the first row
   * @param firstColumn Index of the first column
   * @param lastRow Index of the last row
   * @param lastColumn Index of the last column
   * @return void
   * @see com.eteks.openjeks.format.CellFormat
   * @see com.eteks.openjeks.format.CellFormatPanel
   *
   */
  public void setFormatAt (CellFormat cellFormat, int firstRow, int firstColumn, int lastRow, int lastColumn)
  {
    CellSet cellSet = null;
    Cell cell = null;
    CellFormat cellFormatb = null;
    
    // Ajust Border
    cellFormat  = ajustCellBorderToUnknown(cellFormat);

    // If it is only one cell
    if ( (firstRow == lastRow) && (firstColumn == lastColumn) )
    {
      setFormatAt(cellFormat, firstRow, firstColumn);
      return;
    }
    
    // if the cellSet have allready a CellFormat, remove it
    // this is for keep a good order for the treeset
    Iterator obj = listCellSet.iterator();
    while(obj.hasNext()){
      cellSet = (CellSet)obj.next() ;
      if ( (cellSet.getFirstRow() == firstRow) && (cellSet.getFirstColumn() == firstColumn) &&
           (cellSet.getLastRow() == lastRow) && (cellSet.getLastColumn() == lastColumn) )
      {
        listCellSet.remove(cellSet);
        break;
       }
    }

    // add the CellSet in listCellSet
    cellSet = new CellSet(cellFormat, priority++, firstRow, firstColumn, lastRow, lastColumn);
    // add in listCellSet
    listCellSet.add (cellSet);

    // update CellFormat for all SplashCell in the CellSet where attributes are knowed for the cellset
    obj = listCell.iterator();
    
    // update CellFormat for the SplashCell in the CellSet with new Attribute.
    while(obj.hasNext()){
      cell = (Cell)obj.next() ;
      if ( (cell.getRow() >= firstRow -1) && (cell.getColumn() >= firstColumn -1) &&
           (cell.getRow() <= lastRow +1) && (cell.getColumn() <= lastColumn +1) )
      {
	cellFormatb = updateCellFormatToNull(cell.getCellFormat(), cellFormat,
			                     cell.getRow(), cell.getColumn(),
					     firstRow, lastRow,
					     firstColumn, lastColumn);
	cell.setCellFormat(cellFormatb);
       }
     }
  }
  
  /* Set or Update a CellFormat for a cell
   *
   */
  public void setFormatAt (CellFormat cellFormat, int rowIndex, int columnIndex)
  {
    Cell cell;
    boolean knowed = false;

    // update CellFormat for all SplashCell in the CellSet where attributes are knowed for the cellset
    Iterator obj = listCell.iterator();
    
    // if the cellSet have allready a CellFormat, update it
    while(obj.hasNext()){
      cell = (Cell)obj.next() ;
      if ( (cell.getRow() == rowIndex) && (cell.getColumn() == columnIndex) )
      {
	cell.setCellFormat(cellFormat);
	knowed = true;
	break;
      }
    }
    if (knowed == false)
    {
      cell = new Cell(cellFormat,  rowIndex, columnIndex);
      listCell.add (cell);
    }

    obj = listCell.iterator();

    // update CellFormat for the SplashCell in the CellSet with new Attribute.
    CellFormat cellFormatb = copyCellFormat(cellFormat);
    while(obj.hasNext()){
      cell = (Cell)obj.next() ;
      if ( (cell.getRow() >= rowIndex -1) && (cell.getColumn() >= columnIndex -1) &&
           (cell.getRow() <= rowIndex +1) && (cell.getColumn() <= columnIndex +1) )
      {
	cellFormatb = updateCellFormatToNull(cell.getCellFormat(), cellFormat,
			                     cell.getRow(), cell.getColumn(),
					     rowIndex, rowIndex,
					     columnIndex, columnIndex);
	cell.setCellFormat(cellFormatb);
       }
     }
    

  }


 
  /* Update Attribute to Null => here, update to the new CellFormat 
   * two possibles methods, update to null, or update to the new attribute (of c2)
   */
  private CellFormat updateCellFormatToNull(CellFormat c1, CellFormat c2,
                                            int rowIndex, int columnIndex, 
	                                    int firstRow, int lastRow,
		                            int firstColumn, int lastColumn)
  {
   
    // Fix attribute if cell is in cellSet
    if ( (rowIndex >= firstRow) && (rowIndex <= lastRow) &&
         (columnIndex >= firstColumn) && (columnIndex <= lastColumn)
       )
    {
      if (c2.getHorizontalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT) == false)
        c1.setHorizontalAlignment(c2.getHorizontalAlignment());
      if (c2.getVerticalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT) == false)
        c1.setVerticalAlignment(c2.getVerticalAlignment());
      if (c2.getBackgroundColor() != CellFormat.UNKNOWN_BACKGROUNDCOLOR)
        c1.setBackgroundColor(c2.getBackgroundColor());
      if (c2.getFormat() != CellFormat.UNKNOWN_FORMAT)
        c1.setFormat(c2.getFormat());
      if (c2.getFontName() != CellFormat.UNKNOWN_FONTNAME)
        c1.setFontName(c2.getFontName());
      if (c2.getFontStyle().equals(CellFormat.UNKNOWN_FONTSTYLE) == false)
        c1.setFontStyle(c2.getFontStyle());
      if (c2.getFontSize().equals(CellFormat.UNKNOWN_FONTSIZE) == false)
        c1.setFontSize(c2.getFontSize());
      if (c2.getFontColor() != CellFormat.UNKNOWN_FONTCOLOR)
        c1.setFontColor(c2.getFontColor());
    }
    
    CellBorder cellBorder1  = (CellBorder)c1.getCellBorder();
    CellBorder cellBorder2 = (CellBorder)c2.getCellBorder();
    
    // Fix attribute border around the cellset. exemple, the bottom border
    // is the same than the top border
    
    if ( (cellBorder2.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (rowIndex == firstRow -1) && (columnIndex >= firstColumn) && (columnIndex <= lastColumn) )
    {
      cellBorder1.setBottomColor(cellBorder2.getTopColor());
      cellBorder1.setBottomStyle(cellBorder2.getTopStyle());
    }
    if ( (cellBorder2.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (rowIndex == lastRow +1) && (columnIndex >= firstColumn) && (columnIndex <= lastColumn) )
    {
      cellBorder1.setTopColor(cellBorder2.getBottomColor());
      cellBorder1.setTopStyle(cellBorder2.getBottomStyle());
    }
    
    if ( (cellBorder2.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (columnIndex == firstColumn -1)  && (rowIndex >= firstRow) && (rowIndex <= lastRow) )
    {
      cellBorder1.setRightColor(cellBorder2.getLeftColor());
      cellBorder1.setRightStyle(cellBorder2.getLeftStyle());  
    }
    if ( (cellBorder2.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (columnIndex == lastColumn +1)  && (rowIndex >= firstRow) && (rowIndex <= lastRow) )
    {
      cellBorder1.setLeftColor(cellBorder2.getRightColor());
      cellBorder1.setLeftStyle(cellBorder2.getRightStyle());
    }
	      
    // Fix the border if the cell is in selected cellSet 
    if ( (cellBorder2.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR) && 
         (rowIndex == firstRow) && (columnIndex >= firstColumn) && (columnIndex <= lastColumn) )
    {
      cellBorder1.setTopColor(cellBorder2.getTopColor());
      cellBorder1.setTopStyle(cellBorder2.getTopStyle());
    }
    if ( (cellBorder2.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
	 (rowIndex == lastRow) && (columnIndex >= firstColumn) && (columnIndex <= lastColumn) )
    {
      cellBorder1.setBottomColor(cellBorder2.getBottomColor());
      cellBorder1.setBottomStyle(cellBorder2.getBottomStyle());
    }
    if ( (cellBorder2.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (columnIndex == firstColumn) && (rowIndex >= firstRow) && (rowIndex <= lastRow) )
    {
      cellBorder1.setLeftColor(cellBorder2.getLeftColor());
      cellBorder1.setLeftStyle(cellBorder2.getLeftStyle());
    }   
    if ( (cellBorder2.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR) &&
         (columnIndex == lastColumn) && (rowIndex >= firstRow) && (rowIndex <= lastRow) )
    {
      cellBorder1.setRightColor(cellBorder2.getRightColor());
      cellBorder1.setRightStyle(cellBorder2.getRightStyle());
    }
    if (cellBorder2 instanceof CellSetBorder) 
    {
      if (((CellSetBorder)cellBorder2).getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        if ( (rowIndex > firstRow) && (rowIndex <= lastRow) &&
             (columnIndex >= firstColumn) && (columnIndex <= lastColumn)
           )
	{
	  cellBorder1.setTopColor(((CellSetBorder)cellBorder2).getInternalHorizontalColor());
	  cellBorder1.setTopStyle(((CellSetBorder)cellBorder2).getInternalHorizontalStyle());
	}
	if ( (rowIndex < lastRow) && (rowIndex >= firstRow) &&
             (columnIndex >= firstColumn) && (columnIndex <= lastColumn)
           )
	{
	  cellBorder1.setBottomColor(((CellSetBorder)cellBorder2).getInternalHorizontalColor());
	  cellBorder1.setBottomStyle(((CellSetBorder)cellBorder2).getInternalHorizontalStyle());  
	}
      }

      if (((CellSetBorder)cellBorder2).getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        if ( (columnIndex > firstColumn) && (columnIndex <= lastColumn) &&
             (rowIndex >= firstRow) && (rowIndex <= lastRow)
           )
	{
	  cellBorder1.setLeftColor(((CellSetBorder)cellBorder2).getInternalVerticalColor());
	  cellBorder1.setLeftStyle(((CellSetBorder)cellBorder2).getInternalVerticalStyle());
	}
	if ( (columnIndex < lastColumn) && (columnIndex >= firstColumn) &&
             (rowIndex >= firstRow) && (rowIndex <= lastRow)
           )
	{
	  cellBorder1.setRightColor(((CellSetBorder)cellBorder2).getInternalVerticalColor());
	  cellBorder1.setRightStyle(((CellSetBorder)cellBorder2).getInternalVerticalStyle());
	}
      }
    }

    c1.setCellBorder(cellBorder1);

    return c1;
  }
  
  /* ajust UNKNOWN attributes for the renderer
   * c2 is a default CellFormat at one time
   */
  private CellFormat ajustCellFormatRenderer(CellFormat c1,
                                             CellFormat c2)
  {
    if (c1.getHorizontalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT))
      c1.setHorizontalAlignment(c2.getHorizontalAlignment());
    if (c1.getVerticalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT))
      c1.setVerticalAlignment(c2.getVerticalAlignment()); 
    if (c1.getBackgroundColor() == CellFormat.UNKNOWN_BACKGROUNDCOLOR)
      c1.setBackgroundColor(c2.getBackgroundColor()); 
    if (c1.getFormat() == CellFormat.UNKNOWN_FORMAT)
      c1.setFormat(c2.getFormat()); 
    if (c1.getFontName() == CellFormat.UNKNOWN_FONTNAME)
      c1.setFontName(c2.getFontName()); 
    if (c1.getFontStyle().equals(CellFormat.UNKNOWN_FONTSTYLE))
      c1.setFontStyle(c2.getFontStyle()); 
    if (c1.getFontSize().equals(CellFormat.UNKNOWN_FONTSIZE))
      c1.setFontSize(c2.getFontSize()); 
    if (c1.getFontColor() == CellFormat.UNKNOWN_FONTCOLOR)
      c1.setFontColor(c2.getFontColor()); 

    return c1 ;
  }
  
  /*
   * Ajust Border for the renderer. 
   */
  private CellFormat ajustCellBorderRenderer(CellFormat c1,
		                             int rowIndex, int columnIndex)
  {
    CellBorder cb1 = null, cb = new CellBorder();
    CellSetBorder csb = null;
    CellSet cellSet = null;
    Cell cell = null;
    Iterator obj = null;
    int frow = -1, lrow = -1, fcolumn = -1, lcolumn = -1, row = -1, column = -1;
    boolean debug = false;
    
    obj = listCellSet.iterator();
    while(obj.hasNext())
    {
      cellSet = (CellSet)obj.next() ;
      frow = cellSet.getFirstRow();
      lrow = cellSet.getLastRow();
      fcolumn = cellSet.getFirstColumn();
      lcolumn = cellSet.getLastColumn();
      
      // If the selected cell is on the top, on the bottom,
      // on the left, on the right with the same border (top, bottom, 
      // left, right) than the current CellSet.
      // For example, if the cell is on the top of the CellSet, border
      // at the bottom is the same than the top border of the group.
      //
      // We don't have to look the priority, listCellSell is ordered by priority.
      // 
      if ( (rowIndex >= frow-1 ) && (rowIndex <= lrow+1) &&
	   (columnIndex >= fcolumn-1) && (columnIndex <= lcolumn+1) )
      {

        if (debug) System.out.println("AjustCellBorderRenderer RowIndex="+rowIndex+" columnIndex="+columnIndex);
        if (debug) System.out.println("frow ="+frow+", lrow ="+lrow+", fcolumn ="+fcolumn+", lcolumn ="+lcolumn+" priority="+cellSet.getPriority());
        csb = (CellSetBorder)cellSet.getCellFormat().getCellBorder();
        
        // If the cell is on at the top of the current CellSet
        if ( (rowIndex == frow-1) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ1");
          if (csb.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setBottomColor(csb.getTopColor());
            cb.setBottomStyle(csb.getTopStyle());
	  }
	}
	
	// if the cell is at the bottom of the current CellSet
        if ( (rowIndex == lrow+1) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ2");
          if (csb.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setTopColor(csb.getBottomColor());
            cb.setTopStyle(csb.getBottomStyle());
	  }
	}

	// if the cell is at the left of the current CellSet
        if ( (columnIndex == fcolumn-1) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
          if (debug) System.out.print(" AJ3");
          if (csb.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setRightColor(csb.getLeftColor());
	    if (debug) System.out.println( "AJ3 COLOR="+csb.getLeftColor()); 
            cb.setRightStyle(csb.getLeftStyle());
	  }
	}
        
	// if the cell is at the right of the current CellSet
	if ( (columnIndex == lcolumn+1) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
         if (debug)  System.out.print(" AJ4");
          if (csb.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setLeftColor(csb.getRightColor());
            cb.setLeftStyle(csb.getRightStyle());
	  }
	}
        
	// if the cell is at the same top of the current CellSet
        if ( (rowIndex == frow) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ5");
          if (csb.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setTopColor(csb.getTopColor());
            cb.setTopStyle(csb.getTopStyle());
	  }
        }
	
	// if the cell is at the same bottom of the current CellSet
	if ( (rowIndex == lrow) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ6");
          if (csb.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setBottomColor(csb.getBottomColor());
            cb.setBottomStyle(csb.getBottomStyle());
	  }
	}
	
	// if the cell is at the same left of the current CellSet
        if ( (columnIndex == fcolumn) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
          if (debug) System.out.print(" AJ7");
          if (csb.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setLeftColor(csb.getLeftColor());
            cb.setLeftStyle(csb.getLeftStyle());
	  }
	}
	
	// if the cell is at the same right of the current CellSet
	if ( (columnIndex == lcolumn) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
          if (debug) System.out.print(" AJ8");
          if (csb.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setRightColor(csb.getRightColor());
            cb.setRightStyle(csb.getRightStyle());
	  }
	}
	
	// if the cell is in the current CellSet with or not the same right border
	if ( (rowIndex > frow) && (rowIndex <= lrow) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ9");
          if (csb.getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setTopColor(csb.getInternalHorizontalColor());
            cb.setTopStyle(csb.getInternalHorizontalStyle());
	  }
        }
	
	// if the cell is in the current CellSet with or not the same left border
	if ( (rowIndex < lrow) && (rowIndex >= frow) && (columnIndex >= fcolumn) && (columnIndex <= lcolumn) )
        {
          if (debug) System.out.print(" AJ10");
          if (csb.getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setBottomColor(csb.getInternalHorizontalColor());
            cb.setBottomStyle(csb.getInternalHorizontalStyle());
	  }
        }

	// if the cell is in the current CellSet with or not the same bottom border
	if ( (columnIndex > fcolumn) && (columnIndex <= lcolumn) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
          if (debug) System.out.print(" AJ11");
          if (csb.getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setLeftColor(csb.getInternalVerticalColor());
            cb.setLeftStyle(csb.getInternalVerticalStyle());
	  }
        }

	// if the cell is in the current CellSet with or not the same top border
	if ( (columnIndex < lcolumn) && (columnIndex >= fcolumn) && (rowIndex >= frow) && (rowIndex <= lrow) )
        {
          if (debug) System.out.print(" AJ12");
          if (csb.getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
          {
            cb.setRightColor(csb.getInternalVerticalColor());
	    if (debug) System.out.println("AJ12 = Color = "+ csb.getInternalVerticalColor());
            cb.setRightStyle(csb.getInternalVerticalStyle());
	  }
        }
      }
    }

    // Look if the cell have a knowed CellFormat
    obj = listCell.iterator();
    while(obj.hasNext())
    {
      cell = (Cell)obj.next() ;
      row = cell.getRow();
      column = cell.getColumn();
      cb1 = (CellBorder)cell.getCellFormat().getCellBorder(); 
      
      // if the CellBorder are knowed
      if ( (rowIndex == row) && (columnIndex == column) )
      {
	cb.setTopColor(cb1.getTopColor());
        cb.setTopStyle(cb1.getTopStyle());
        cb.setBottomColor(cb1.getBottomColor());
        cb.setBottomStyle(cb1.getBottomStyle());
        cb.setLeftColor(cb1.getLeftColor());
        cb.setLeftStyle(cb1.getLeftStyle());
        cb.setRightColor(cb1.getRightColor());
        cb.setRightStyle(cb1.getRightStyle());
      }

      // if the cell at the top is knowed
      if ( (rowIndex == row+1) && (columnIndex == column) )
      {
 	cb.setTopColor(cb1.getBottomColor());
        cb.setTopStyle(cb1.getBottomStyle());     
      }
      
      // if the cell at the bottom is knowed
      if ( (rowIndex == row-1) && (columnIndex == column) )
      {
 	cb.setBottomColor(cb1.getTopColor());
        cb.setBottomStyle(cb1.getTopStyle());     
      }
      
      // if the cell at the left is knowed
      if ( (columnIndex == column+1) && (rowIndex == row) )
      {
 	cb.setLeftColor(cb1.getRightColor());
        cb.setLeftStyle(cb1.getRightStyle());     
      }
      
      // if the cell at the right is knowed
      if ( (columnIndex == column-1) && (rowIndex == row) )
      {
 	cb.setRightColor(cb1.getLeftColor());
        cb.setRightStyle(cb1.getLeftStyle());     
      }     
    }
    
    // update c1 with cb CellBorder
    c1.setCellBorder(cb);
    
    // return c1 cellFormat with borders updated
    return c1;
  }
	  
  /* Copy - Clone a CellFormat
   * 
   * Perhaps a best method exist to make a clone CellFormat
   * see clone() method of CellFormat
   * whith attributes fixed...
   */
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
 
  /*
   * Ajust Attibute for give the CellFormat to the Panel
   * Set UNKNOWN if attribute if there is one conflict.
   */
  private CellFormat ajustCellSetFormatPanel(CellFormat cf1,int firstRow,int lastRow,int firstColumn,int lastColumn)
  {
    CellFormat cf = null;
    CellBorder cb = null;
    CellSetBorder cb1 = null;
    CellSetBorder cstmp = new CellSetBorder();
    boolean topFirst = true, bottomFirst = true, leftFirst = true, rightFirst = true;
    boolean horizontalFirst = true, verticalFirst = true, first = true;
    boolean debug = false;
    if (cf1.getCellBorder() instanceof CellSetBorder)
    {
      if (debug) System.out.println("Instance CellSetBorder");
    } else {
      cf1.setCellBorder(new CellSetBorder());
    }
    
    cf1.setCellBorder(new CellSetBorder());
    cb1 = (CellSetBorder)cf1.getCellBorder();
    
    for (int i = firstRow; i <= lastRow; i++)
      for (int j = firstColumn; j <= lastColumn; j++)
      {
        cf = getFormatAt(i,j);
	cb = (CellBorder)cf.getCellBorder();
        
	if (debug) System.out.println("\ni="+i+" j="+j);
        // Ajust attributes
	if (first)
        {
          cf1.setHorizontalAlignment(cf.getHorizontalAlignment());
          cf1.setVerticalAlignment(cf.getVerticalAlignment());
          cf1.setBackgroundColor(cf.getBackgroundColor());
          cf1.setFormat(cf.getFormat());
          cf1.setFontName(cf.getFontName());
          cf1.setFontStyle(cf.getFontStyle());
          cf1.setFontSize(cf.getFontSize());
          cf1.setFontColor(cf.getFontColor());
	  first = false;
	} else {
          if (cf1.getHorizontalAlignment().intValue() != cf.getHorizontalAlignment().intValue())
          { 
             cf1.setHorizontalAlignment(CellFormat.UNKNOWN_ALIGNMENT);
             if (debug) System.out.println("Halignment= UNKNOWN_ALIGNMENT"); 
          }
          if (cf1.getVerticalAlignment().intValue() != cf.getVerticalAlignment().intValue())
          { 
            cf1.setVerticalAlignment(CellFormat.UNKNOWN_ALIGNMENT); 
            System.out.println("Valignment= UNKNOWN_ALIGNMENT"); 
          }
	  if (cf1.getBackgroundColor() != CellFormat.UNKNOWN_BACKGROUNDCOLOR)
            if (cf1.getBackgroundColor().equals(cf.getBackgroundColor()) == false)
            { 
              cf1.setBackgroundColor(CellFormat.UNKNOWN_BACKGROUNDCOLOR); 
              if (debug) System.out.println("BackgroundColor= UNKNOWN_BACKGROUNDCOLOR");
            }
	  if (cf1.getFormat() != cf.getFormat())
          { 
            cf1.setFormat(CellFormat.UNKNOWN_FORMAT); 
            if (debug) System.out.println("Format= UNKNOWN_FORMAT"); 
          }
          if (cf1.getFontName() != cf.getFontName())
          { 
            cf1.setFontName(CellFormat.UNKNOWN_FONTNAME); 
            if (debug) System.out.println("FontName= UNKNOWN_FONTNAME"); 
          }
          if (cf1.getFontStyle().equals(cf.getFontStyle()) == false)
          { 
            cf1.setFontStyle(CellFormat.UNKNOWN_FONTSTYLE); 
            if (debug) System.out.println("FontStyle= UNKNOWN_FONTSTYLE"); 
          }
          if (cf1.getFontSize().equals(cf.getFontSize()) == false)
          { 
            cf1.setFontSize(CellFormat.UNKNOWN_FONTSIZE); 
            if (debug) System.out.println("FontSize= UNKNOWN_FONTSIZE"); 
          }
          if (cf1.getFontColor() != CellFormat.UNKNOWN_FONTCOLOR)
            if (cf1.getFontColor().equals(cf.getFontColor()) == false)
            { 
              cf1.setFontColor(CellFormat.UNKNOWN_FONTCOLOR);
              if (debug) System.out.println("FontColor= UNKNOWN_FONCOLOR"); 
            }
	}
	
        // ajust Borders
	if (firstRow == i)
        {		
          if (debug) System.out.print("ASB 1");
          if (topFirst == false)
	  {
            if (cb.topEquals(cb1) == false)
            {
              if (debug) System.out.print(" ASB 2");
              cb1.setTopColor(CellBorder.UNKNOWN_BORDERCOLOR); 
              cb1.setTopStyle(CellBorder.UNKNOWN_BORDERSTYLE); 
	    }
          } else {
            if (debug) System.out.print(" ASB 3");
            cb1.setTopColor(cb.getTopColor());
            cb1.setTopStyle(cb.getTopStyle());
	    topFirst = false; 
	  }
		  
	} else {
          if (debug) System.out.print(" ASB 4 HF="+horizontalFirst);
	  if (horizontalFirst == false)
	  {
	    cstmp.setTopColor(((CellSetBorder)cb1).getInternalHorizontalColor());
            cstmp.setTopStyle(((CellSetBorder)cb1).getInternalHorizontalStyle());    
            if (cb.topEquals(cstmp) == false)
            {
              if (debug) System.out.print(" ASB 5");
              cb1.setInternalHorizontalColor(CellBorder.UNKNOWN_BORDERCOLOR); 
              cb1.setInternalHorizontalStyle(CellBorder.UNKNOWN_BORDERSTYLE); 
	    }
	  } else {
            if (debug) System.out.print(" ASB 5 BIS");
	    if (cb1.getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
	    { 
              if (debug) System.out.print(" ASB 5 BIS2");
              cb1.setInternalHorizontalColor(cb.getTopColor());
              cb1.setInternalHorizontalStyle(cb.getTopStyle());
	      horizontalFirst = false;
	    }
	  }
	}
	    
	if (lastRow == i)
	{ 
          if (debug) System.out.print(" ASB 6");
          if (bottomFirst == false)
	  {
            if (debug) System.out.print(" ASB 7");
            if (cb.bottomEquals(cb1) == false)
            {
              if (debug) System.out.print(" ASB 8");
              cb1.setBottomColor(CellBorder.UNKNOWN_BORDERCOLOR); 
              cb1.setBottomStyle(CellBorder.UNKNOWN_BORDERSTYLE); 
            }
	  } else {
            if (debug) System.out.print(" ASB 9");
            cb1.setBottomColor(cb.getBottomColor());
            cb1.setBottomStyle(cb.getBottomStyle());
	    bottomFirst = false;
	  }
        } else {
          if (debug) System.out.print(" ASB 10");
	  if (horizontalFirst == false)
	  {
	    cstmp.setBottomColor(((CellSetBorder)cb1).getInternalHorizontalColor());
            cstmp.setBottomStyle(((CellSetBorder)cb1).getInternalHorizontalStyle());
	    if (cb.bottomEquals(cstmp) == false)
            {
              if (debug) System.out.print(" ASB 11");
              cb1.setInternalHorizontalColor(CellBorder.UNKNOWN_BORDERCOLOR); 
              cb1.setInternalHorizontalStyle(CellBorder.UNKNOWN_BORDERSTYLE); 
	    }
          } else {
            if (debug) System.out.print(" ASB 11 BIS");
            if (cb1.getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
	    {
              if (debug) System.out.print(" ASB 11 BIS2");
	      cb1.setInternalHorizontalColor(cb.getBottomColor());
              cb1.setInternalHorizontalStyle(cb.getBottomStyle());           
	      horizontalFirst = false;
	    }
	  }
	}
		  
        if (firstColumn == j)
        {
          if (debug) System.out.print(" ASB 12");
          if (leftFirst == false)
	  {
           if (debug) System.out.print(" ASB 13");
           if (cb.leftEquals(cb1) == false)
           {
             if (debug) System.out.print(" ASB 14");
	     cb1.setLeftColor(CellBorder.UNKNOWN_BORDERCOLOR);
	     cb1.setLeftStyle(CellBorder.UNKNOWN_BORDERSTYLE);
            }
	  } else {
            if (debug) System.out.print(" ASB 15");
	    cb1.setLeftColor(cb.getLeftColor());
	    cb1.setLeftStyle(cb.getLeftStyle());
	    leftFirst = false;
	  }
        } else {
          if (debug) System.out.print(" ASB 16");
	  if (verticalFirst == false)
	  {
            cstmp.setLeftColor(((CellSetBorder)cb1).getInternalVerticalColor());
            cstmp.setLeftStyle(((CellSetBorder)cb1).getInternalVerticalStyle()); 
            if (cb.leftEquals(cstmp) == false)
            {
              if (debug) System.out.print(" ASB 17");
              cb1.setInternalVerticalColor(CellBorder.UNKNOWN_BORDERCOLOR);
              cb1.setInternalVerticalStyle(CellBorder.UNKNOWN_BORDERSTYLE);
	    }
	  } else {
            if (debug) System.out.print(" ASB 17 BIS");
            if (cb1.getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
	    {
              if (debug) System.out.print(" ASB 17 BIS2");
              cb1.setInternalVerticalColor(cb.getLeftColor());
              cb1.setInternalVerticalStyle(cb.getLeftStyle());           
	      verticalFirst = false;
	    }
	  }
        }
		  
	if (lastColumn == j)
	{
          if (debug) System.out.print(" ASB 18");
	  if (rightFirst == false)
	  {
            if (debug) System.out.print(" ASB 19");
            if (cb.rightEquals(cb1) == false) 
            {
              if (debug) System.out.print(" ASB 20");
	      cb1.setRightColor(CellBorder.UNKNOWN_BORDERCOLOR);
              cb1.setRightStyle(CellBorder.UNKNOWN_BORDERSTYLE);
            }
	  } else {
            if (debug) System.out.print(" ASB 21");
	    cb1.setRightColor(cb.getRightColor());
	    cb1.setRightStyle(cb.getRightStyle());
	    rightFirst = false; 
	  }
        } else {
          if (debug) System.out.print(" ASB 22");
	  if (verticalFirst == false)
	  {
            cstmp.setRightColor(((CellSetBorder)cb1).getInternalVerticalColor());
            cstmp.setRightStyle(((CellSetBorder)cb1).getInternalVerticalStyle());           	
	    if (cb.rightEquals(cstmp) == false)
            {
              if (debug) System.out.print(" ASB 23");
              cb1.setInternalVerticalColor(CellBorder.UNKNOWN_BORDERCOLOR);
              cb1.setInternalVerticalStyle(CellBorder.UNKNOWN_BORDERSTYLE);
	    }
	  } else {
            if (debug) System.out.print(" ASB 23 BIS");
            if (cb1.getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
	    {
              if (debug) System.out.print(" ASB 23 BIS2");
              cb1.setInternalVerticalColor(cb.getRightColor());
              cb1.setInternalVerticalStyle(cb.getRightStyle());    
	      verticalFirst = false;
	    }
          }
	}
      }
    cf1.setCellBorder(cb1); 
    cf1 = ajustCellBorderToUnknown(cf1);

    return cf1;
  }

  /*
   * Ajust cellFormat to Unknown value if the border have a default value
   */
  private CellFormat ajustCellBorderToUnknown (CellFormat cf1)
  {
    boolean debug = false;
    if (debug) System.out.println("AJUST BORDER  ##########################################");
    
    CellBorder cb = new CellBorder(); // default CellBorder
    CellBorder cb1 = (CellBorder)cf1.getCellBorder();
    
    if (cb1.topEquals(cb))
    {
      if (debug) System.out.println("BORDER TOP UNKNOWN !##########################################");
      cb1.setTopColor(CellBorder.UNKNOWN_BORDERCOLOR);
      cb1.setTopStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }
     if (cb1.bottomEquals(cb))
    {
      if (debug) System.out.println("BORDER BOTTOM UNKNOWN !##########################################");
      cb1.setBottomColor(CellBorder.UNKNOWN_BORDERCOLOR);
      cb1.setBottomStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }   
    if (cb1.leftEquals(cb))
    {
      if (debug) System.out.println("BORDER LEFT UNKNOWN !##########################################");
      cb1.setLeftColor(CellBorder.UNKNOWN_BORDERCOLOR);
      cb1.setLeftStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }  
    if (cb1.rightEquals(cb))
    {
      if (debug) System.out.println("BORDER RIGHT UNKNOWN !##########################################");
      cb1.setRightColor(CellBorder.UNKNOWN_BORDERCOLOR);
      cb1.setRightStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }
    
  if (cf1.getCellBorder() instanceof CellSetBorder)
  {
    CellSetBorder csbDefault = new CellSetBorder();
    if (csbDefault.internalHorizontalEquals((CellSetBorder)cb1))
    {
      if (debug) System.out.println("INTERNAL HORIZONTAL UNKNOWN !##########################################");
      ((CellSetBorder)cb1).setInternalHorizontalColor(CellBorder.UNKNOWN_BORDERCOLOR);
      ((CellSetBorder)cb1).setInternalHorizontalStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }
    if (csbDefault.internalVerticalEquals((CellSetBorder)cb1))
    {
      if (debug) System.out.println("INTERNAL VERTICAL UNKNOWN !##########################################");
      ((CellSetBorder)cb1).setInternalVerticalColor(CellBorder.UNKNOWN_BORDERCOLOR);
      ((CellSetBorder)cb1).setInternalVerticalStyle(CellBorder.UNKNOWN_BORDERSTYLE);
    }
  }

    cf1.setCellBorder(cb1);
    return cf1; 
  }
  /**
   * Return a <code>int</code> indicating the first row wich have a CellFormat.
   *
   * @return int position of the first row which have a CellFormat. -1 if no CellFormat is set
   *
   */
  public int getFirstFormatRow()
  {
    return this.firstFormatRow;
  }
  
  /**
  * Return a <code>int</code> indicating the first column wich have a CellFormat.
  *
  * @return int position of the first column which have a CellFormat. -1 if no CellFormat is set
  *
  */ 
  public int getFirstFormatColumn()
  {
    return this.firstFormatColumn;
  }
  
  /**
  * Return a <code>int</code> indicating the last row wich have a CellFormat.
  *
  * @return int position of the last row which have a CellFormat. -1 if no CellFormat is set
  *
  */ 
  public int getLastFormatRow()
  {
    return this.lastFormatRow;
  }
  
  /**
  * Return a <code>int</code> indicating the last column wich have a CellFormat.
  *
  * @return int position of the last column which have a CellFormat. -1 if no CellFormat is set
  *
  */ 
  public int getLastFormatColumn()
  {
    return this.lastFormatColumn;
  }

  /*
   * Store a CellFormat for a SplashCell (row, column)
   */
  private class Cell
  {
    private int row, column;
    private CellFormat cellCellFormat;

    public Cell (CellFormat cellCellFormat, int row, int column)
    {
      this.row = row;
      this.column = column;
      this.cellCellFormat = cellCellFormat;

      if ((row < firstFormatRow) || (firstFormatRow == -1))
        firstFormatRow = row;
      if ((column < firstFormatColumn) || (firstFormatColumn == -1))
        firstFormatColumn = column;
      if ((row > lastFormatRow) || (lastFormatRow == -1))
        lastFormatRow = row;
      if ((column > lastFormatColumn) || (lastFormatColumn == -1))
        lastFormatColumn = column;
    }
    
    public CellFormat getCellFormat ()
    {
      return this.cellCellFormat;
    }
    
    public void setCellFormat(CellFormat cellCellFormat)
    {
      this.cellCellFormat = cellCellFormat;
    }
    
    public int getRow ()
    {
      return this.row;
    }
    
    public int getColumn ()
    {
      return this.column;
    }
    
  }

  /*
   * Store a CellFormat for a CellSet (firstRow, firstColumn, lastRow, lastColumn) 
   * with priority
   */
  private class CellSet
  {
    private int priority, firstRow, firstColumn, lastRow, lastColumn;
    private CellFormat cellSetCellFormat ;

    public CellSet (CellFormat cellSetCellFormat, int priority, 
		    int firstRow, int firstColumn, 
		    int lastRow, int lastColumn)
    {
      this.cellSetCellFormat = cellSetCellFormat;
      this.priority = priority;
      this.firstRow = firstRow;
      this.firstColumn = firstColumn;
      this.lastRow = lastRow;
      this.lastColumn = lastColumn;

      if ((firstRow < firstFormatRow) || (firstFormatRow == -1))
        firstFormatRow = firstRow;
      if ((firstColumn < firstFormatColumn) || (firstFormatColumn == -1))
        firstFormatColumn = firstColumn;
      if ((lastRow > lastFormatRow) || (lastFormatRow == -1))
        lastFormatRow = lastRow;
      if ((lastColumn > lastFormatColumn) || (lastFormatColumn == -1))
        lastFormatColumn = lastColumn;
    }
    
    public CellFormat getCellFormat()
    {
      return this.cellSetCellFormat;
    }
    
    public void setCellFormat(CellFormat pcellSetCellFormat)
    {
      setCellFormat(pcellSetCellFormat, this.priority);
    }

    public void setCellFormat(CellFormat pcellSetCellFormat, int priority)
    {
      this.priority = priority;
      
      // Check if the new attribute are UNKNOWNED
      // if yes, keep the old value
      if (pcellSetCellFormat.getHorizontalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT) == false)
        this.cellSetCellFormat.setHorizontalAlignment(pcellSetCellFormat.getHorizontalAlignment());
      
      if (pcellSetCellFormat.getVerticalAlignment().equals(CellFormat.UNKNOWN_ALIGNMENT) == false)
        this.cellSetCellFormat.setVerticalAlignment(pcellSetCellFormat.getVerticalAlignment());
      
      if (pcellSetCellFormat.getBackgroundColor() != CellFormat.UNKNOWN_BACKGROUNDCOLOR)
        this.cellSetCellFormat.setBackgroundColor(pcellSetCellFormat.getBackgroundColor());
      
      if (pcellSetCellFormat.getFontName() != CellFormat.UNKNOWN_FONTNAME)
        this.cellSetCellFormat.setFontName(pcellSetCellFormat.getFontName());
      
      if (pcellSetCellFormat.getFontStyle().equals(CellFormat.UNKNOWN_FONTSTYLE) == false)
        this.cellSetCellFormat.setFontStyle(pcellSetCellFormat.getFontStyle());
      
      if (pcellSetCellFormat.getFontSize().equals(CellFormat.UNKNOWN_FONTSIZE) == false)
        this.cellSetCellFormat.setFontSize(pcellSetCellFormat.getFontSize());
      
      if (pcellSetCellFormat.getFontColor() != CellFormat.UNKNOWN_FONTCOLOR)
        this.cellSetCellFormat.setFontColor(pcellSetCellFormat.getFontColor());

      // Check BORDER 
      CellSetBorder pcellSetBorder = (CellSetBorder)pcellSetCellFormat.getCellBorder();
      CellSetBorder cellSetBorder = (CellSetBorder)this.cellSetCellFormat.getCellBorder();
      if (pcellSetBorder.getTopColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        cellSetBorder.setTopColor(pcellSetBorder.getTopColor());
        cellSetBorder.setTopStyle(pcellSetBorder.getTopStyle());
      }
      if (pcellSetBorder.getBottomColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        cellSetBorder.setBottomColor(pcellSetBorder.getBottomColor());
        cellSetBorder.setBottomStyle(pcellSetBorder.getBottomStyle());     
      }
      if (pcellSetBorder.getLeftColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        cellSetBorder.setLeftColor(pcellSetBorder.getLeftColor());
        cellSetBorder.setLeftStyle(pcellSetBorder.getLeftStyle());    
      }
      if (pcellSetBorder.getRightColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        cellSetBorder.setRightColor(pcellSetBorder.getRightColor());
        cellSetBorder.setRightStyle(pcellSetBorder.getRightStyle());     
      }
      if (pcellSetBorder.getInternalHorizontalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
        cellSetBorder.setInternalHorizontalColor(pcellSetBorder.getInternalHorizontalColor());
        cellSetBorder.setInternalHorizontalStyle(pcellSetBorder.getInternalHorizontalStyle());     
      }
      if (pcellSetBorder.getInternalVerticalColor() != CellBorder.UNKNOWN_BORDERCOLOR)
      {
	cellSetBorder.setInternalVerticalColor(pcellSetBorder.getInternalVerticalColor());
        cellSetBorder.setInternalVerticalStyle(pcellSetBorder.getInternalVerticalStyle());     
      }      
    }
    
    public int getPriority()
    {
      return this.priority;
    }
    
    public void setPriority(int priority)
    {
      this.priority = priority;
    }
    public int getFirstRow()
    {
      return this.firstRow;
    }
    public int getFirstColumn()
    {
      return this.firstColumn;
    }
    public int getLastRow()
    {
      return this.lastRow;
    }
    public int getLastColumn()
    {
      return this.lastColumn;
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

  private class CellSetComparator implements Comparator
  {
    public int compare(Object obj1, Object obj2)
    {
      int p1 = ((CellSet)obj1).getPriority();
      int p2 = ((CellSet)obj2).getPriority();
      
      return p1 - p2; 
    } 
  }
}

