/*
 * @(#)BorderChooser.java   06/05/2004
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

import java.awt.BasicStroke;

/**
 * Class uses to define borders style
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellBorder
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @see com.eteks.openjeks.format.BorderChooser
 * @author  Jean-Baptiste Cérézat
 */
public class BorderStyle
{
  /**
   * Constant uses to define a simple line
   */
  public static final int BASIC=0;
  /**
   * Constant uses to define a dash line
   */
  public static final int DASH=1;

  private float size;
  private int type;
  private BasicStroke stroke;

  /**
   * Constructor uses to instanciate default border style.<br />
   * Type is BorderStyle.BASIC and size=0
   */
  public BorderStyle()
  {
    size = 0.0f;
    type=BASIC;
    stroke = new BasicStroke(size);
  }

  /**
   * Constructor uses to instanciate border style with specific size and type.<br />
   * Type can be BorderStyle.BASIC or BorderStyle.DASH
   *
   * @param size : float
   * @param type : int
   */
  public BorderStyle(float size,int type)
  {
     this.size=size;
     this.type=type;

     switch(type)
     {
      case BASIC :
        this.stroke=new BasicStroke(size);
        break;
      case DASH :
        float[] dash={5.0f,2.0f};
        this.stroke=new BasicStroke(size,0,0,1.0f,dash,0.0f);
        break;
     }
  }

  /**
   * Method which return this BorderStyle size.
   *
   * @return size : float
   */
  public float getSize()
  {
    return size;
  }

  /**
   * Method which return this BorderStyle type.
   *
   * @return type : int
   */
  public int getType()
  {
    return type;
  }

  /**
   * Method which return the BasicStroke associate to this BorderStyle
   *
   * @return {@link java.awt.BasicStroke}
   */
  public BasicStroke getStroke()
  {
    return stroke;
  }

  /**
   * Method which return true if size and type are the same
   *
   * @param borderStyle : {@link com.eteks.openjeks.format.BorderStyle}
   * @return boolean
   */
  public boolean equals(BorderStyle borderStyle)
  {
    if(borderStyle!=null)
      if(borderStyle.size==size && borderStyle.type==type)
        return true;

    return false;
  }
}
