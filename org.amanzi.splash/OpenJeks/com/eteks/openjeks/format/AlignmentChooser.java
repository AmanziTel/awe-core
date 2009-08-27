/*
 * @(#)AlignmentChooser.java   06/05/2004
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

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.util.ResourceBundle;

/**
 * Panel for alignment choice.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @author  Emmanuel Puybaret, Jean-Baptiste C�r�zat
 */
public class AlignmentChooser extends JPanel
{
  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");
  private JComboBox horizontalAlignmentComboBox;
  private JComboBox verticalAlignmentComboBox;

  /**
   * Constructor uses to instanciate an alignment selection panel with specific alignement.<br />
   * If alignment is standard, the format alignment will be applicate.
   *
   * @param horizontalAlignment : java.lang.Integer
   * @param verticalAlignment : java.lang.Integer
   */
  public AlignmentChooser (Integer   horizontalAlignment,
                           Integer   verticalAlignment)
  {
    Object [] horizontalAlignments = {new TextObjectEntry(" ",new Integer(-1)),
                                      new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_STANDARD"),
                                                            CellFormat.STANDARD_ALIGNMENT),
                                      new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_HORIZONTAL_LEFT"),
                                                            new Integer (JLabel.LEFT)),
                                      new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_HORIZONTAL_CENTER"),
                                                            new Integer (JLabel.CENTER)),
                                      new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_HORIZONTAL_RIGHT"),
                                                            new Integer (JLabel.RIGHT))};
    horizontalAlignmentComboBox = new JComboBox (horizontalAlignments);

    selectComboBoxValue (horizontalAlignmentComboBox, horizontalAlignment);

    Object [] verticalAlignments = {new TextObjectEntry(" ",new Integer(-1)),

                                    new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_VERTICAL_TOP"),
                                                          new Integer (JLabel.TOP)),
                                    new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_VERTICAL_CENTER"),
                                                          new Integer (JLabel.CENTER)),
                                    new TextObjectEntry (resourceBundle.getString ("FORMAT_ALIGNMENT_VERTICAL_BOTTOM"),
                                                          new Integer (JLabel.BOTTOM))};
    verticalAlignmentComboBox = new JComboBox (verticalAlignments);
    selectComboBoxValue (verticalAlignmentComboBox, verticalAlignment);

    JPanel textAlignmentPanel = new JPanel (new GridLayout (5, 1, 0, 3));
    textAlignmentPanel.setPreferredSize(new Dimension(200,200));
    textAlignmentPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString ("FORMAT_TEXT_ALIGNMENT_TITLE")));
    textAlignmentPanel.add (new JLabel (resourceBundle.getString ("FORMAT_ALIGNMENT_HORIZONTAL_LABEL")));
    textAlignmentPanel.add (horizontalAlignmentComboBox);
    textAlignmentPanel.add (new JLabel (resourceBundle.getString ("FORMAT_ALIGNMENT_VERTICAL_LABEL")));
    textAlignmentPanel.add (verticalAlignmentComboBox);

    setLayout(new FlowLayout (FlowLayout.LEFT, 3, 3));
    add (textAlignmentPanel);
  }

  private void selectComboBoxValue (JComboBox comboBox, Integer value)
  {
    for (int i = 0; i < comboBox.getItemCount(); i++)
      if (((Integer)((TextObjectEntry)comboBox.getItemAt(i)).getValue()).intValue() == value.intValue())
      {
        comboBox.setSelectedIndex(i);
        break;
      }
  }

  /**
   * Method which return the horizontal alignment selected.
   *
   * @return horizontalAlignment : java.lang.Integer
   */
  public Integer getHorizontalAlignment ()
  {
    return ((Integer)((TextObjectEntry)horizontalAlignmentComboBox.getSelectedItem()).getValue());
  }

  /**
   * Method which return the vertical alignment selected.
   *
   * @return verticalAlignment : java.lang.Integer
   */
  public Integer getVerticalAlignment()
  {
    return ((Integer)((TextObjectEntry)verticalAlignmentComboBox.getSelectedItem()).getValue());
  }
}
