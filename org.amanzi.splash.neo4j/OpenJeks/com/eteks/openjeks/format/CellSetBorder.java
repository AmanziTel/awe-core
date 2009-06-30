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
 * Borders Informations for a SplashCell Set.
 *
 * @author Yvonnick Esnault
 */

import java.awt.Color;

public class CellSetBorder extends CellBorder
{
  private BorderStyle internalHorizontalStyle = null;
  private BorderStyle internalVerticalStyle = null;
  
  private Color internalHorizontalColor = Color.lightGray;
  private Color internalVerticalColor = Color.lightGray;

  public CellSetBorder()
  {
    this.internalHorizontalStyle = new BorderStyle(1,BorderStyle.BASIC);
    this.internalVerticalStyle = new BorderStyle(1,BorderStyle.BASIC);

    this.internalHorizontalColor = Color.lightGray;
    this.internalVerticalColor = Color.lightGray; 
  }

  public void setInternalHorizontalStyle(BorderStyle style)
  {
    this.internalHorizontalStyle = style; 
  }
  
  public void setInternalVerticalStyle(BorderStyle style)
  {
    this.internalVerticalStyle = style;
  }
  
  public BorderStyle getInternalHorizontalStyle()
  {
    return this.internalHorizontalStyle;
  }
  
  public BorderStyle getInternalVerticalStyle()
  {
    return this.internalVerticalStyle;
  }
  
  public Color getInternalHorizontalColor ()
  {
    return internalHorizontalColor;
  }

  public Color getInternalVerticalColor ()
  {
    return internalVerticalColor;
  }
  
  public void setInternalHorizontalColor (Color color)
  {
    this.internalHorizontalColor = color;
  }

  public void setInternalVerticalColor (Color color)
  {
    this.internalVerticalColor = color;
  }

  public boolean internalHorizontalEquals(CellSetBorder cellSetBorder)
  {
    boolean flag = false;
    if  (this.internalHorizontalColor ==  cellSetBorder.getInternalHorizontalColor())
      if (this.internalHorizontalStyle != CellBorder.UNKNOWN_BORDERSTYLE)
	if (this.internalHorizontalStyle.equals(cellSetBorder.getInternalHorizontalStyle()) )
           flag = true;

    return flag;
  }
  public boolean internalVerticalEquals(CellSetBorder cellSetBorder)
  {
    boolean flag = false;
    if (this.internalVerticalColor ==  cellSetBorder.getInternalVerticalColor())
      if (this.internalVerticalStyle != CellBorder.UNKNOWN_BORDERSTYLE)
        if (this.internalVerticalStyle.equals(cellSetBorder.getInternalVerticalStyle()))
          flag = true;
    return flag;
  }
}

