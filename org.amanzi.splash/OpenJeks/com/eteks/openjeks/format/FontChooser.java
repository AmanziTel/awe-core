/*
 * @(#)FontChooser.java   09/03/2003
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel uses for font choice.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @author  Emmanuel Puybaret, Jean-Baptiste C�r�zat
 */
public class FontChooser extends JPanel
{
  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");

  private JList      fontList;
  private JList      styleList;
  private JTextField sizeField = new JTextField (4);
  private JList      sizeList;
  private JButton    colorButton;
  private JLabel     fontPreviewLabel;
  private String     fontName;
  private Integer    fontStyle;
  private Integer    fontSize;
  private Color      fontColor;
  private Font       font;

  /**
   * Constructor uses to instanciate a font settings selection panel with specific settings.<br />
   * If the paramaters are null, the lists are not selected
   *
   * @param fontName : java.lang.String
   * @param fontStyle : java.lang.Integer
   * @param fontSize : java.lang.Integer
   * @param defaultColor : java.awt.Color
   * @param backgroundColor : java.awt.Color
   */
  public FontChooser (String fontName,Integer fontStyle,Integer fontSize,
                    Color defaultColor,
                    Color backgroundColor)
  {
    if(fontName!=CellFormat.UNKNOWN_FONTNAME && fontStyle!=CellFormat.UNKNOWN_FONTSTYLE && fontSize!=CellFormat.UNKNOWN_FONTSIZE)
      this.font = new Font(fontName,fontStyle.intValue(),fontSize.intValue());
    else
      font=null;

    this.fontName = fontName;
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    this.fontColor = defaultColor;

    String [] fonts;
    try
    {
      fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }
    catch (NoClassDefFoundError ex)
    {
      // GraphicsEnvironment is available only with JDK >= 1.2
      fonts = Toolkit.getDefaultToolkit ().getFontList ();
    }
    fontList = new JList (fonts);
    fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    if(fontName!=CellFormat.UNKNOWN_FONTNAME)
      fontList.setSelectedValue(fontName, true);

    Object [] styles = {new TextObjectEntry (resourceBundle.getString ("FORMAT_FONT_STYLE_PLAIN"), new Integer (Font.PLAIN)),
                        new TextObjectEntry (resourceBundle.getString ("FORMAT_FONT_STYLE_ITALIC"), new Integer (Font.ITALIC)),
                        new TextObjectEntry (resourceBundle.getString ("FORMAT_FONT_STYLE_BOLD"), new Integer (Font.BOLD)),
                        new TextObjectEntry (resourceBundle.getString ("FORMAT_FONT_STYLE_BOLD_ITALIC"), new Integer (Font.BOLD + Font.ITALIC))};
    styleList = new JList (styles);
    styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    if(fontStyle!=CellFormat.UNKNOWN_FONTSTYLE)
      styleList.setSelectedValue(new TextObjectEntry (null, fontStyle), true);

    sizeList = new JList (new Integer [] {new Integer (9),
                                          new Integer (10),
                                          new Integer (12),
                                          new Integer (14),
                                          new Integer (18),
                                          new Integer (24),
                                          new Integer (36),
                                          new Integer (48),
                                          new Integer (72)});
    sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    if(fontSize!=CellFormat.UNKNOWN_FONTSTYLE)
      sizeList.setSelectedValue(fontSize, true);

    sizeField.setText(String.valueOf((fontSize.intValue()==-1)?0:fontSize.intValue()));

    fontPreviewLabel = new JLabel (resourceBundle.getString ("FORMAT_FONT_PREVIEW_TEXT"));
    fontPreviewLabel.setPreferredSize(new Dimension (150, 75));
    fontPreviewLabel.setHorizontalAlignment(JLabel.CENTER);
    fontPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    fontPreviewLabel.setOpaque(true);
    fontPreviewLabel.setBackground (backgroundColor);
    fontPreviewLabel.setForeground(fontColor);

    ListSelectionListener fontListener = new ListSelectionListener ()
      {
        public void valueChanged (ListSelectionEvent ev)
        {
          updateFont ();
        }
      };
    fontList.addListSelectionListener(fontListener);
    styleList.addListSelectionListener(fontListener);
    sizeField.getDocument().addDocumentListener(new DocumentListener ()
      {
        public void insertUpdate (DocumentEvent ev)
        {
          updateFont ();
        }

        public void removeUpdate (DocumentEvent ev)
        {
          updateFont ();
        }

        public void changedUpdate (DocumentEvent ev)
        { }
      });
    sizeList.addListSelectionListener (new ListSelectionListener ()
      {
        public void valueChanged (ListSelectionEvent ev)
        {
          sizeField.setText(sizeList.getSelectedValue().toString());
          sizeField.selectAll();
        }
      });

    colorButton=new JButton(new ColorButtonIcon(fontColor));
    colorButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          Color color=JColorChooser.showDialog(new JPanel(),resourceBundle.getString("FORMAT_FONT_COLOR_DIALOG"),fontColor);

          if(color!=null)
          {
            fontColor=color;
            fontPreviewLabel.setForeground(fontColor);
            ((ColorButtonIcon)colorButton.getIcon()).setColor(fontColor);
          }
        }
      });

    JPanel fontNamePanel  = new JPanel (new BorderLayout (3, 3));
    fontNamePanel.add(new JLabel (resourceBundle.getString ("FORMAT_FONT_NAME_LABEL")), BorderLayout.NORTH);
    fontNamePanel.add(new JScrollPane (fontList), BorderLayout.CENTER);

    JPanel fontStylePanel = new JPanel (new BorderLayout (3, 3));
    fontStylePanel.add(new JLabel (resourceBundle.getString ("FORMAT_FONT_STYLE_LABEL")), BorderLayout.NORTH);
    fontStylePanel.add(new JScrollPane (styleList), BorderLayout.CENTER);

    JPanel fontSizeFieldPanel  = new JPanel (new BorderLayout (3, 3));
    fontSizeFieldPanel.add(sizeField, BorderLayout.NORTH);
    fontSizeFieldPanel.add(new JScrollPane (sizeList), BorderLayout.CENTER);
    JPanel fontSizePanel  = new JPanel (new BorderLayout (3, 3));
    fontSizePanel.add(new JLabel (resourceBundle.getString ("FORMAT_FONT_SIZE_LABEL")), BorderLayout.NORTH);
    fontSizePanel.add(fontSizeFieldPanel, BorderLayout.CENTER);

    JPanel fontStyleAndSizePanel  = new JPanel (new GridLayout (1, 2, 3, 3));
    fontStyleAndSizePanel.add (fontStylePanel);
    fontStyleAndSizePanel.add (fontSizePanel);

    JPanel fontPanel = new JPanel (new BorderLayout (3, 3));
    fontPanel.add (fontNamePanel, BorderLayout.CENTER);
    fontPanel.add (fontStyleAndSizePanel, BorderLayout.EAST);

    JPanel fontColorPanel  = new JPanel (new BorderLayout (3, 3));
    fontColorPanel.add(new JLabel (resourceBundle.getString ("FORMAT_FONT_COLOR_LABEL")), BorderLayout.WEST);
    colorButton.setPreferredSize(new Dimension(50,30));
    fontColorPanel.add(colorButton, BorderLayout.CENTER);

    JPanel fontPreviewPanel  = new JPanel (new BorderLayout ());
    fontPreviewPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString ("FORMAT_FONT_PREVIEW_TITLE")));
    fontPreviewPanel.add (fontPreviewLabel);

    if(font!=null)
      updateFont();

    JPanel fontColorAndPreviewPanel = new JPanel (new BorderLayout (3, 3));
    fontColorAndPreviewPanel.add (fontColorPanel, BorderLayout.WEST);
    fontColorAndPreviewPanel.add (fontPreviewPanel, BorderLayout.SOUTH);

    setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    setLayout(new BorderLayout (3, 3));
    add (fontPanel, BorderLayout.CENTER);
    add (fontColorAndPreviewPanel, BorderLayout.SOUTH);
  }

  private void updateFont ()
  {
    String defaultName=null;
    Integer defaultStyle,defaultSize;
    defaultStyle=defaultSize=null;

    try
    {
      if(fontList.getSelectedValue()!=null)
      {
        this.fontName=(String)fontList.getSelectedValue();
        defaultName=this.fontName;
      }
      else
      {
        defaultName="Arial";
      }

      if(styleList.getSelectedValue()!=null)
      {
        this.fontStyle=(Integer)((TextObjectEntry)styleList.getSelectedValue()).getValue();
        defaultStyle=this.fontStyle;
      }
      else
      {
        defaultStyle=new Integer(Font.PLAIN);
      }

      Integer size=new Integer(sizeField.getText());
      if(size.intValue()!=0)
      {
        this.fontSize=new Integer(sizeField.getText());
        defaultSize=this.fontSize;
      }
      else
      {
        this.fontSize=new Integer(-1);
        defaultSize=new Integer(10);
      }

      this.font = new Font (defaultName,defaultStyle.intValue(),defaultSize.intValue());
      fontPreviewLabel.setFont(font);
      fontPreviewLabel.setForeground((fontColor==null)?Color.black:fontColor);
    }
    catch (NumberFormatException ex)
    {  }
  }

  /**
   * Method which return the name of the font.
   *
   * @return fontName : java.lang.String
   */
  public String getFontName()
  {
    return fontName;
  }

  /**
   * Method which return the style of the font.
   *
   * @return fontStyle : java.lang.Integer
   */
  public Integer getFontStyle()
  {
    return fontStyle;
  }

  /**
   * Method which return the size of the font.
   *
   * @return fontSize : java.lang.Integer
   */
  public Integer getFontSize()
  {
    return fontSize;
  }

  /**
   * Method which return the background color of the cell.
   *
   * @return backgroundColor : java.awt.Color
   */
  public Color getFontColor()
  {
    return this.fontColor;
  }

  private class ColorButtonIcon implements Icon
  {
    private Color color;

    ColorButtonIcon(Color color)
    {
      this.color=color;
    }

    public int getIconWidth()
    {
      return 50;
    }

    public int getIconHeight()
    {
      return 24;
    }

    public void paintIcon(Component c,Graphics g,int x,int y)
    {
      if(color!=null)
      {
        g.setColor(color);
        g.fillRect(15,6,18,17);
        g.setColor(Color.black);
        g.drawRect(15,6,18,17);
      }
    }

    void setColor(Color color)
    {
      this.color=color;
      repaint();
    }
  }
}
