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


import java.text.Format;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
/**
 * The <code>CellFormat</code> is used to store
 * attributes for SplashCell or CellSet of a JTable.
 * <br /><br />
 * CellFormat is manipulated by {@link com.eteks.openjeks.format.TableFormat}
 * and {@link com.eteks.openjeks.format.CellFormatPanel} to set attribute.
 * <br /><br />
 * CellFormat is also used by {@link com.eteks.openjeks.format.CellFormatRenderer}
 * to display format on a JTable
 *<br />
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @see com.eteks.openjeks.format.CellFormatRenderer
 * @see com.eteks.openjeks.format.TableFormat
 * @author Yvonnick Esnault
 * 
 */

public class CellFormat implements Cloneable
{

  public final static Format UNKNOWN_FORMAT = null;
  public final static String UNKNOWN_FONTNAME = null;
  public final static Integer UNKNOWN_FONTSTYLE = new Integer(-1);
  public final static Integer UNKNOWN_FONTSIZE = new Integer(-1);
  public final static Integer UNKNOWN_ALIGNMENT = new Integer(-1);
  public final static Integer STANDARD_ALIGNMENT = new Integer(-2);
  public final static Color UNKNOWN_FONTCOLOR = null;
  public final static Color UNKNOWN_BACKGROUNDCOLOR = null;
  public final static CellBorder UNKNOWN_CELLBORDER = null;
  
  private Format format ;
  private String fontName ;
  private Integer fontStyle ;
  private Integer fontSize ;
  private Integer verticalAlignment ;
  private Integer horizontalAlignment ;
  private Color fontColor ;
  private Color backgroundColor ;
  private Object cellBorder ;

  /**
   * Constructor with default Value for a CellSet. <br />
   * Format : none <br />
   * Font Name : Arial <br />
   * Font Size : 10 <br />
   * Font Color : black <br />
   * Vertical Alignment :  bottom <br />
   * Horizontal Alignemnt : left <br />
   * Background Color : white <br />
   * CellBorder : default CellBorder <br />
   *
   * @see com.eteks.openjeks.format.CellBorder
   * @see com.eteks.openjeks.format.CellSetBorder
   *
   */
  public CellFormat ()
  {
    this.format = null;
    this.fontName = "Arial";
    this.fontStyle = new Integer(Font.PLAIN);
    this.fontSize = new Integer(14);
    this.fontColor = Color.BLACK ;
    this.verticalAlignment = new Integer(JLabel.CENTER);
    this.horizontalAlignment = new Integer(-2);
    this.backgroundColor = Color.WHITE;
    this.cellBorder = new CellBorder();
  }
  
  /* Format */
  /**
   * @param format :  {@link java.text.Format}
   */
  public void setFormat (Format format) { this.format = format; }
  /**
   * @return {@link java.text.Format}
   */
  public Format getFormat () { return this.format; }

  /* Font Name */
  /**
   * @param fontName : {@link java.lang.String}
   */
  public void setFontName (String fontName) { this.fontName = fontName; }
  /**
   * @return {@link java.lang.String}
   */
  public String getFontName () { return this.fontName; }

  /* Font Style */
  /**
   * @param fontStyle : {@link java.lang.Integer}
   */
  public void setFontStyle (Integer fontStyle) { this.fontStyle = fontStyle; }
  /**
   * @return {@link java.lang.Integer}
   */
  public Integer getFontStyle () { return this.fontStyle; }

  /* Font Size */
  /**
   * @param fontSize : {@link java.lang.Integer}
   */
  public void setFontSize (Integer fontSize) { this.fontSize = fontSize; }
  /**
   * @return {@link java.lang.Integer}
   */
  public Integer getFontSize () { return this.fontSize; }

  /* Font Color */
  /**
   * @param fontColor : {@link java.awt.Color}
   */
  public void setFontColor (Color fontColor) { this.fontColor = fontColor; }
  /**
   * @return {@link java.awt.Color}
   */
  public Color getFontColor () { return this.fontColor; }

  /* Vertical Alignment */
  /**
   * @param verticalAlignment : {@link java.lang.Integer}
   */
  public void setVerticalAlignment (Integer verticalAlignment) { this.verticalAlignment = verticalAlignment; }
  /**
   * @return {@link java.lang.Integer}
   */
  public Integer getVerticalAlignment () { return this.verticalAlignment; }

  /* Horizontal Alignment */
  /**
   * @param horizontalAlignment : {@link java.lang.Integer}
   */
  public void setHorizontalAlignment (Integer horizontalAlignment) { this.horizontalAlignment = horizontalAlignment; }
  /**
   * @return {@link java.lang.Integer}
   */
  public Integer getHorizontalAlignment () { return this.horizontalAlignment; }

  /* Background Color */
  /**
   * @param backgroundColor : {@link java.awt.Color}
   */
  public void setBackgroundColor (Color backgroundColor) { this.backgroundColor = backgroundColor; }
  /**
   * @return {@link java.awt.Color}
   */
  public Color getBackgroundColor () { return this.backgroundColor; }

  /* CellBorder */
  /**
   * @param cellBorder : {@link java.lang.Object} an instance of {@link com.eteks.openjeks.format.CellBorder} or 
   * of {@link com.eteks.openjeks.format.CellSetBorder}
   */
  public void setCellBorder (Object cellBorder) { this.cellBorder = cellBorder; }
  /**
   * @return {@link java.lang.Object}  an instance of {@link com.eteks.openjeks.format.CellBorder} or 
   * of {@link com.eteks.openjeks.format.CellSetBorder}
   */
  public Object getCellBorder () { return this.cellBorder; }

/*  public Object clone()
  {
    Object obj = null;
    try{
      obj = super.clone();
    } catch (CloneNotSupportedException e) {
      System.out.println("Error = " + e);
    }
    ((CellFormat)obj).setCellBorder( (Object) ((CellBorder)this.cellBorder).clone()  );
    return obj;
  }
*/
}

