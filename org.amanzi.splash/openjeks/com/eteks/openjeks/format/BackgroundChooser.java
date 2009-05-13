/*
 * @(#)BackgroundChooser.java   04/05/2004
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel used to specify cell background color.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @author  Jean-Baptiste C�r�zat
 */

public class BackgroundChooser extends JPanel
{
  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");

  private boolean formatIsKnown=false;
  private Color backgroundColor=null;
  private Color fontColor=null;
  private Font font=null;
  private JColorChooser colorChooser=null;
  private JLabel backgroundPreviewLabel=null;

  /**
   * Constructor uses to instanciate a background selection panel with specific font, background and font color.<br />
   * This parameters are uses to set the preview label
   *
   * @param backgroundColor : java.awt.Color
   * @param fontColor : java.awt.Color
   * @param font : java.awt.Font
   */
  public BackgroundChooser(Color backgroundColor,Color fontColor,Font font)
  {
    super(new BorderLayout());

    this.backgroundColor=backgroundColor;
    this.fontColor=fontColor;
    this.font=font;

    if(backgroundColor==CellFormat.UNKNOWN_BACKGROUNDCOLOR)
      colorChooser=new JColorChooser();
    else
    {
      colorChooser=new JColorChooser(backgroundColor);
      formatIsKnown=true;
    }
    colorChooser.setPreviewPanel(new JPanel());
    ColorSelectionModel csm=colorChooser.getSelectionModel();
    csm.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent ce)
      {
        backgroundPreviewLabel.setBackground(((ColorSelectionModel)ce.getSource()).getSelectedColor());
        setFormatIsKnown(true);
      }
    });
    add(colorChooser,BorderLayout.NORTH);

    backgroundPreviewLabel = new JLabel ("AaBbCcYyZz");
    backgroundPreviewLabel.setPreferredSize(new Dimension (150, 75));
    backgroundPreviewLabel.setHorizontalAlignment(JLabel.CENTER);
    backgroundPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    backgroundPreviewLabel.setOpaque(true);
    backgroundPreviewLabel.setFont(font);
    backgroundPreviewLabel.setBackground (colorChooser.getColor());
    backgroundPreviewLabel.setForeground(fontColor);

    JPanel backgroundPreviewPanel  = new JPanel (new BorderLayout ());
    backgroundPreviewPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("FORMAT_BACKGROUND_PREVIEW")));
    backgroundPreviewPanel.add (backgroundPreviewLabel);
    add(backgroundPreviewPanel,BorderLayout.CENTER);
  }

  private void setFormatIsKnown(boolean isKnown)
  {
    formatIsKnown=isKnown;
  }

  /**
   * Method which return the background color selected.<br />
   * If no color has been selected it returns the color send in parameters.
   *
   * @return backgroundColor : java.awt.Color
   */
  public Color getBackgroundColor()
  {
    if(formatIsKnown)
      return colorChooser.getColor();

    return CellFormat.UNKNOWN_BACKGROUNDCOLOR;
  }
}
