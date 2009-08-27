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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * Borders Informations for a JRubyJavaInterface or a SplashCell Set.
 * the attributes for a cell set are the internal borders.
 * Method paintBorder paint border for a cell only.
 *
 * @author Yvonnick Esnault
 */
public class CellBorder implements Border, Cloneable
{
  private Color topColor = Color.lightGray;
  private Color bottomColor = Color.lightGray;
  private Color leftColor = Color.lightGray;
  private Color rightColor = Color.lightGray;

  private BorderStyle topStyle = null;
  private BorderStyle bottomStyle = null;
  private BorderStyle leftStyle = null;
  private BorderStyle rightStyle = null;
  private BorderStyle bottomAndRightDefault = new BorderStyle(1,BorderStyle.BASIC);

  public static final BorderStyle UNKNOWN_BORDERSTYLE = null;
  public static final Color UNKNOWN_BORDERCOLOR = null;

  private boolean paintDefault = true;
  private boolean bottomDefault = true;
  private boolean rightDefault = true;

  private Insets insets = new Insets(1,1,1,1);

  public CellBorder ()
  {
    this.topStyle = new BorderStyle(1,BorderStyle.BASIC);
    this.bottomStyle = new BorderStyle(1,BorderStyle.BASIC);
    this.leftStyle = new BorderStyle(1,BorderStyle.BASIC);
    this.rightStyle = new BorderStyle(1,BorderStyle.BASIC);
  }

  public CellBorder (BorderStyle topStyle, BorderStyle bottomStyle, BorderStyle leftStyle, BorderStyle rightStyle,
         Color topColor, Color bottomColor, Color leftColor, Color rightColor)
  {

    this.topStyle = topStyle;
    this.bottomStyle = bottomStyle;
    this.leftStyle = leftStyle;
    this.rightStyle = rightStyle;

    this.topColor = topColor;
    this.bottomColor = bottomColor;
    this.leftColor = leftColor;
    this.rightColor = rightColor;

    this.bottomDefault = false;
    this.rightDefault = false;
  }

  public BorderStyle getTopStyle ()
  {
    return this.topStyle;
  }

  public BorderStyle getBottomStyle ()
  {
    return this.bottomStyle;
  }

  public BorderStyle getLeftStyle ()
  {
    return this.leftStyle;
  }

  public BorderStyle getRightStyle ()
  {
    return this.rightStyle;
  }


  public Color getTopColor ()
  {
    return this.topColor;
  }

  public Color getBottomColor ()
  {
    return this.bottomColor;
  }

  public Color getLeftColor ()
  {
    return this.leftColor;
  }

  public Color getRightColor ()
  {
    return this.rightColor;
  }

  public void setTopStyle (BorderStyle style)
  {
    this.topStyle = style;
    if (style != null)
      this.insets.top = (int)style.getSize();
    else
      this.insets.top = 0;
  }

  public void setBottomStyle (BorderStyle style)
  {
    this.bottomStyle = style;
    if (style != null)
      this.insets.bottom = (int)style.getSize();
    else
      this.insets.bottom = 0;
    this.bottomDefault = false;
  }

  public void setLeftStyle (BorderStyle style)
  {
    this.leftStyle = style;
    if (style != null)
      this.insets.left = (int)style.getSize();
    else
      this.insets.left = 0;
  }

  public void setRightStyle (BorderStyle style)
  {
    this.rightStyle = style;
    if (style != null)
      this.insets.right = (int)style.getSize();
    else
      this.insets.right = 0;
    this.rightDefault = false;
  }

  public void setTopColor (Color color)
  {
    this.topColor = color;
  }

  public void setBottomColor (Color color)
  {
    this.bottomColor = color;
  }

  public void setLeftColor (Color color)
  {
    this.leftColor = color;
  }

  public void setRightColor (Color color)
  {
    this.rightColor = color;
  }

  public boolean topEquals(CellBorder cellBorder)
  {
    boolean flag = false;
    if (this.topColor ==  cellBorder.getTopColor())
      if (this.topStyle != CellBorder.UNKNOWN_BORDERSTYLE)
   if (this.topStyle.equals(cellBorder.getTopStyle()))
           flag = true;
    return flag;
  }

  public boolean bottomEquals(CellBorder cellBorder)
  {
    boolean flag = false;
    if (this.bottomColor ==  cellBorder.getBottomColor())
      if (this.bottomStyle != CellBorder.UNKNOWN_BORDERSTYLE)
        if (this.bottomStyle.equals(cellBorder.getBottomStyle()))
           flag = true;
    return flag;
  }

  public boolean leftEquals(CellBorder cellBorder)
  {
    boolean flag = false;
    if (this.leftColor ==  cellBorder.getLeftColor())
      if (this.leftStyle != CellBorder.UNKNOWN_BORDERSTYLE)
        if (this.leftStyle.equals(cellBorder.getLeftStyle()))
           flag = true;
    return flag;
  }

  public boolean rightEquals(CellBorder cellBorder)
  {
    boolean flag = false;
    if (this.rightColor ==  cellBorder.getRightColor())
      if (this.rightStyle != CellBorder.UNKNOWN_BORDERSTYLE)
   if (this.rightStyle.equals(cellBorder.getRightStyle()))
           flag = true;
    return flag;
  }

  public void setPaintDefault(boolean f)
  {
    this.paintDefault = f;

  }

  public void paintBorder(Component c,Graphics g,int x,int y,int width,int height)
  {
    Graphics2D g2D=(Graphics2D)g;
/*    if(topColor!=null && topStyle!=null && topColor!=Color.white)
    {
      g2D.setColor(topColor);
      ((Graphics2D)g).setStroke(topStyle.getStroke());
      g2D.drawLine(x, y, x + width - 1, y);
    }

    if(leftColor!=null && leftStyle!=null && leftColor!=Color.white)
    {
      g2D.setColor(leftColor);
      ((Graphics2D)g).setStroke(leftStyle.getStroke());
      g2D.drawLine(x, y, x,height + y);
    }
*/
//    if(rightColor!=null && rightStyle!=null)
//    {
//      g2D.setColor(rightColor);
//      ((Graphics2D)g).setStroke(rightStyle.getStroke());
//    } else {
//       g2D.setColor(Color.lightGray);
//      ((Graphics2D)g).setStroke(bottomAndRightDefault.getStroke());
//    }
////    if ((this.rightDefault && this.paintDefault) ||
////        (this.rightDefault!=true)
////       )
//
//     if ((this.paintDefault) ||
//         ( (this.paintDefault == false) &&
//          ((rightColor != Color.lightGray) && (rightColor != null))
//         )
//        )
//        {
//          g2D.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
//        }
//
//    if(bottomColor!=null && bottomStyle!=null)
//    {
//      g2D.setColor(bottomColor);
//      ((Graphics2D)g).setStroke(bottomStyle.getStroke());
//    } else {
//      g2D.setColor(Color.lightGray);
//      ((Graphics2D)g).setStroke(bottomAndRightDefault.getStroke());
//    }
//
////    if ((this.bottomDefault && this.paintDefault) ||
////        (this.bottomDefault!=true)
////       )
//     if ((this.paintDefault) ||
//         ( (this.paintDefault == false) &&
//           ((bottomColor != Color.lightGray) && (bottomColor != null))
//         )
//        )
//        {
//          g2D.drawLine(x, y + height - 1, width + x - 1, y + height - 1);
//        }
//

  }

  public boolean isBorderOpaque()
  {
    return true;
  }

  public Insets getBorderInsets(Component c)
  {
    return this.insets;
  }

/*   public Object clone()
  {
    Object obj = null;
    try{
      obj = super.clone();
    } catch (CloneNotSupportedException e) {
      System.out.println("Error = " + e);
    }
    return obj;
  }
*/
}

