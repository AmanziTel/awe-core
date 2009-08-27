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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel for borders' properties selection.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @author  Jean-Baptiste C�r�zat
 */
public class BorderChooser extends JPanel
{
  // Constants used to set active borders
  private static final int NO_BORDERS=0;
  private static final int EXTERNAL_BORDERS=1;
  private static final int INSIDE_BORDERS=2;
  private static final int TOP_BORDER=3;
  private static final int LEFT_BORDER=4;
  private static final int BOTTOM_BORDER=5;
  private static final int RIGHT_BORDER=6;
  private static final int VERTICAL_BORDER=7;
  private static final int HORIZONTAL_BORDER=8;

  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");

  private final BorderComponent borderComponent;
  private final JList styleList;
  private final JComboBox colorCombo;

  // Icons to display in style list allowing to set style borders
  private final BorderStyleIcon icon1=new BorderStyleIcon(Color.black,new BorderStyle(1.0f,BorderStyle.BASIC));
  private final BorderStyleIcon icon2=new BorderStyleIcon(Color.black,new BorderStyle(1.0f,BorderStyle.DASH));
  private final BorderStyleIcon icon3=new BorderStyleIcon(Color.black,new BorderStyle(2.0f,BorderStyle.BASIC));
  private final BorderStyleIcon icon4=new BorderStyleIcon(Color.black,new BorderStyle(2.0f,BorderStyle.DASH));
  private final BorderStyleIcon icon5=new BorderStyleIcon(Color.black,new BorderStyle(3.0f,BorderStyle.BASIC));
  private final BorderStyleIcon icon6=new BorderStyleIcon(Color.black,new BorderStyle(3.0f,BorderStyle.DASH));
  private final BorderStyleIcon icon7=new BorderStyleIcon(Color.black,new BorderStyle(4.0f,BorderStyle.BASIC));
  private final BorderStyleIcon icon8=new BorderStyleIcon(Color.black,new BorderStyle(4.0f,BorderStyle.DASH));
  private final BorderStyleIcon icon9=new BorderStyleIcon(Color.black,new BorderStyle(5.0f,BorderStyle.BASIC));
  private final BorderStyleIcon icon10=new BorderStyleIcon(Color.black,new BorderStyle(5.0f,BorderStyle.DASH));

  private final BorderStyleIcon[] styleData={icon1,icon2,icon3,icon4,icon5,icon6,icon7,icon8,icon9,icon10};

  /**
   * Constructor uses to instanciate a border settings selection panel with specific settings.
   *
   * @param cellBorder : com.eteks.openjeks.format.CellBorder
   */
  public BorderChooser(Object cellBorder)
  {
    super(new BorderLayout());

    // Need to detect cell set case
    boolean insideMode=false;
    if(cellBorder instanceof CellSetBorder)
      insideMode=true;

    JPanel northPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,50,20));
    JPanel mainComponentPanel=new JPanel(new BorderLayout());
    JPanel buttonsComponentPanel=new JPanel(new FlowLayout());
   
    JPanel stylePanel=new JPanel(new BorderLayout());
    JPanel southPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,60,40));

    // Button reseting borders
    BorderButtonIcon noBordersIcon=new BorderButtonIcon(NO_BORDERS);
    BorderButton noBordersButton=new BorderButton(noBordersIcon);
    noBordersButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        borderComponent.setActiveBorders(EXTERNAL_BORDERS);
        borderComponent.setActiveBordersColor(null);
        borderComponent.setActiveBordersStyle(null);
        borderComponent.setActiveBorders(INSIDE_BORDERS);
        borderComponent.setActiveBordersColor(null);
        borderComponent.setActiveBordersStyle(null);
        borderComponent.setActiveBorders(NO_BORDERS);
      }
    });
    buttonsComponentPanel.add(noBordersButton);

    // Button setting all borders to active with
    // current color and current style excepting inside borders
    BorderButtonIcon allBordersIcon=new BorderButtonIcon(EXTERNAL_BORDERS);
    BorderButton allBordersButton=new BorderButton(allBordersIcon);
    allBordersButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        borderComponent.setActiveBorders(EXTERNAL_BORDERS);
        borderComponent.setActiveBordersColor(borderComponent.getCurrentColor());
        borderComponent.setActiveBordersStyle(((BorderStyleIcon)styleList.getSelectedValue()).getBorderStyle());
        borderComponent.setActiveBorders(NO_BORDERS);
      }
    });
    buttonsComponentPanel.add(allBordersButton);

    // Buttons to display in cell set case
    if(insideMode)
    {
      // Button setting all borders to active with
      // current color and current style including inside borders
      BorderButtonIcon allBordersInsideIcon=new BorderButtonIcon(INSIDE_BORDERS);
      BorderButton allBordersInsideButton=new BorderButton(allBordersInsideIcon);
      allBordersInsideButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          borderComponent.setActiveBorders(INSIDE_BORDERS);
          borderComponent.setActiveBordersColor(borderComponent.getCurrentColor());
          borderComponent.setActiveBordersStyle(((BorderStyleIcon)styleList.getSelectedValue()).getBorderStyle());
          borderComponent.setActiveBorders(NO_BORDERS);
        }
      });
      buttonsComponentPanel.add(allBordersInsideButton);
    }

    mainComponentPanel.add(buttonsComponentPanel,BorderLayout.NORTH);

    // Component to display borders preview
    borderComponent=new BorderComponent(insideMode,Color.black,new BorderStyle(1,0));
    borderComponent.initComponent(cellBorder);
    mainComponentPanel.add(borderComponent,BorderLayout.CENTER);
    mainComponentPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("FORMAT_BORDER_PREVIEW")));

    northPanel.add(mainComponentPanel);

    // List which allows to set borders style
    styleList=new JList(styleData);
    styleList.setSelectedIndex(0);
    JScrollPane listScrollPane=new JScrollPane(styleList);
    listScrollPane.setPreferredSize(new Dimension(100,153));
    styleList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged (ListSelectionEvent ev)
      {
        BorderStyleIcon icon=(BorderStyleIcon)styleList.getSelectedValue();
        borderComponent.setCurrentStyle(icon.getBorderStyle());
      }
    });
    stylePanel.add(listScrollPane,BorderLayout.SOUTH);
    stylePanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("FORMAT_BORDER_STYLE")));

    northPanel.add(stylePanel);

    add(northPanel,BorderLayout.NORTH);

    Object[] colors={new BorderColorIcon(Color.black,resourceBundle.getString("FORMAT_BORDER_NOCOLOR")),
                    new BorderColorIcon(Color.black,resourceBundle.getString("FORMAT_BORDER_BLACK")),
                    new BorderColorIcon(Color.gray,resourceBundle.getString("FORMAT_BORDER_GRAY")),
                    new BorderColorIcon(Color.yellow,resourceBundle.getString("FORMAT_BORDER_YELLOW")),
                    new BorderColorIcon(Color.orange,resourceBundle.getString("FORMAT_BORDER_ORANGE")),
                    new BorderColorIcon(Color.red,resourceBundle.getString("FORMAT_BORDER_RED")),
                    new BorderColorIcon(Color.green,resourceBundle.getString("FORMAT_BORDER_GREEN")),
                    new BorderColorIcon(Color.blue,resourceBundle.getString("FORMAT_BORDER_BLUE"))};

    colorCombo=new JComboBox(colors);
    colorCombo.setPreferredSize(new Dimension(120,30));
    colorCombo.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Color color=((BorderColorIcon)((JComboBox)ae.getSource()).getSelectedItem()).getColor();

        if(color!=null)
        {
          borderComponent.setCurrentColor(color);
          for(int i=0;i<styleData.length;i++)
            styleData[i].setColor(color);
          ((BorderColorIcon)colorCombo.getItemAt(0)).setColor(color);
        }
      }
    });
    southPanel.add(colorCombo);

    JButton colorButton=new JButton(resourceBundle.getString("FORMAT_BORDER_CUSTOMIZED"));
    colorButton.setPreferredSize(new Dimension(100,30));
    colorButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Color color=JColorChooser.showDialog(new JPanel(),resourceBundle.getString("FORMAT_BORDER_COLOR_DIALOG"),((BorderColorIcon)colorCombo.getItemAt(0)).getColor());

        if(color!=null)
        {
          borderComponent.setCurrentColor(color);
          borderComponent.setActiveBordersColor(color);
          for(int i=0;i<styleData.length;i++)
            styleData[i].setColor(color);
          ((BorderColorIcon)colorCombo.getItemAt(0)).setColor(color);
          colorCombo.setSelectedIndex(0);
        }
      }
    });
    southPanel.add(colorButton);

    add(southPanel,BorderLayout.CENTER);
  }

  // Shortcut setting button
  private class BorderButton extends JButton
  {
    private BorderButton(Icon borderIcon)
    {
      super(borderIcon);

      setPreferredSize(new Dimension(23,23));
      setBackground(Color.white);
      setFocusPainted(false);
    }
  }

  // Icon to display in shortcut setting buttons
  private class BorderButtonIcon implements Icon
  {
    private int display;

    private BorderButtonIcon(int display)
    {
      this.display=display;
    }

    public int getIconWidth()
    {
      return 19;
    }

    public int getIconHeight()
    {
      return 19;
    }

    public  void paintIcon(Component c,Graphics g,int x,int y)
    {
      if(display==BorderChooser.EXTERNAL_BORDERS)
      {
        g.setColor(Color.black);
        g.drawRect(3,3,16,16);
      }
      else if(display==INSIDE_BORDERS)
      {
        g.setColor(Color.black);
        g.drawLine(11,3,11,19);
        g.drawLine(3,11,19,11);
        g.setColor(Color.lightGray);
        g.drawRect(3,3,16,16);
      }
    }
  }

  // Icon to display in style list allowing to retreive style borders
  private class BorderStyleIcon implements Icon
  {
    private Color graphicsColor;
    private BorderStyle borderStyle;

    private BorderStyleIcon(Color graphicsColor,BorderStyle borderStyle)
    {
      this.graphicsColor=graphicsColor;
      this.borderStyle=borderStyle;
    }

    public int getIconWidth()
    {
      return 50;
    }

    public int getIconHeight()
    {
      return 20;
    }

    public void paintIcon(Component c,Graphics g,int x,int y)
    {
      g.setColor(graphicsColor);
      ((Graphics2D)g).setStroke(borderStyle.getStroke());
      ((Graphics2D)g).drawLine(3,10,25,10);
    }

    private void setColor(Color color)
    {
      graphicsColor=color;
      repaint();
    }

    private BorderStyle getBorderStyle()
    {
      return borderStyle;
    }
  }

  private class BorderColorIcon implements Icon
  {
    private Color color;
    private String name;

    BorderColorIcon(Color color,String name)
    {
      this.color=color;
      this.name=name;
    }

    public int getIconWidth()
    {
      return 70;
    }

    public int getIconHeight()
    {
      return 20;
    }

    public void paintIcon(Component c,Graphics g,int x,int y)
    {
      if(color!=null)
      {
        g.setColor(color);
        g.fillRect(3,3,16,16);
        g.setColor(Color.black);
        g.drawRect(3,3,16,16);

        g.drawString(name,22,16);
      }
      else
      {
        g.setColor(Color.black);
        g.drawString(name,3,16);
      }
    }

    private void setColor(Color color)
    {
      this.color=color;
      repaint();
    }

    private Color getColor()
    {
      return color;
    }
  }

  // Preview component displaying borders settings
  private class BorderComponent extends JComponent
  {
    // Borders color
    private Color currentColor=null;
    private Color topColor=null;
    private Color leftColor=null;
    private Color bottomColor=null;
    private Color rightColor=null;
    private Color verticalColor=null;
    private Color horizontalColor=null;

    // Borders style
    private BorderStyle currentStyle=null;
    private BorderStyle topStyle=null;
    private BorderStyle leftStyle=null;
    private BorderStyle bottomStyle=null;
    private BorderStyle rightStyle=null;
    private BorderStyle verticalStyle=null;
    private BorderStyle horizontalStyle=null;
    private boolean insideMode=false;

    // Border is active?
    private boolean topActive=false;
    private boolean leftActive=false;
    private boolean bottomActive=false;
    private boolean rightActive=false;
    private boolean verticalActive=false;
    private boolean horizontalActive=false;

    private BorderComponent(boolean insideMode,Color currentColor,BorderStyle currentStyle)
    {
      // Use to distinct if it's a cell set case
      this.insideMode=insideMode;

      this.currentColor=currentColor;
      this.currentStyle=currentStyle;

      setPreferredSize(new Dimension(120,120));
      setOpaque(true);

      // Listener allowing to set borders by clicking on component
      // If the border is already active needs to deactivate and unset it
      addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(MouseEvent me)
        {
          if(me.getY()<20 && me.getX()>19 && me.getX()<101)
          {
            if(getTopActive() && getTopColor()==getCurrentColor() && getTopStyle()==getCurrentStyle())
            {
              setTopColor(null);
              setTopStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.TOP_BORDER);
              setTopColor(getCurrentColor());
              setTopStyle(getCurrentStyle());
            }
          }
          else if(me.getX()<20 && me.getY()>19 && me.getY()<101)
          {
            if(getLeftActive() && getLeftColor()==getCurrentColor() && getLeftStyle()==getCurrentStyle())
            {
              setLeftColor(null);
              setLeftStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.LEFT_BORDER);
              setLeftColor(getCurrentColor());
              setLeftStyle(getCurrentStyle());
            }
          }
          else if(me.getY()>100 && me.getX()>19 && me.getX()<101)
          {
            if(getBottomActive() && getBottomColor()==getCurrentColor() && getBottomStyle()==getCurrentStyle())
            {
              setBottomColor(null);
              setBottomStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.BOTTOM_BORDER);
              setBottomColor(getCurrentColor());
              setBottomStyle(getCurrentStyle());
            }
          }
          else if(me.getX()>100 && me.getY()>19 && me.getY()<101)
          {
            if(getRightActive() && getRightColor()==getCurrentColor() && getRightStyle()==getCurrentStyle())
            {
              setRightColor(null);
              setRightStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.RIGHT_BORDER);
              setRightColor(getCurrentColor());
              setRightStyle(getCurrentStyle());
            }
          }
          else if(me.getX()>57 && me.getX()<63 && me.getY()>19 && me.getY()<101)
          {
            if(getVerticalActive() && getVerticalColor()==getCurrentColor() && getVerticalStyle()==getCurrentStyle())
            {
              setVerticalColor(null);
              setVerticalStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.VERTICAL_BORDER);
              setVerticalColor(getCurrentColor());
              setVerticalStyle(getCurrentStyle());
            }
          }
          else if(me.getX()>19 && me.getX()<101 && me.getY()>57 && me.getY()<63)
          {
            if(getHorizontalActive() && getHorizontalColor()==getCurrentColor() && getHorizontalStyle()==getCurrentStyle())
            {
              setHorizontalColor(null);
              setHorizontalStyle(null);
              setActiveBorders(BorderChooser.NO_BORDERS);
            }
            else
            {
              setActiveBorders(BorderChooser.HORIZONTAL_BORDER);
              setHorizontalColor(getCurrentColor());
              setHorizontalStyle(getCurrentStyle());
            }
          }
          repaint();
        }
      });
    }

    private void initComponent(Object cellBorder)
    {
      topColor=((CellBorder)cellBorder).getTopColor();
      leftColor=((CellBorder)cellBorder).getLeftColor();
      bottomColor=((CellBorder)cellBorder).getBottomColor();
      rightColor=((CellBorder)cellBorder).getRightColor();
      topStyle=((CellBorder)cellBorder).getTopStyle();
      leftStyle=((CellBorder)cellBorder).getLeftStyle();
      bottomStyle=((CellBorder)cellBorder).getBottomStyle();
      rightStyle=((CellBorder)cellBorder).getRightStyle();

      if(cellBorder instanceof CellSetBorder)
      {
        horizontalColor=((CellSetBorder)cellBorder).getInternalHorizontalColor();
        verticalColor=((CellSetBorder)cellBorder).getInternalVerticalColor();
        horizontalStyle=((CellSetBorder)cellBorder).getInternalHorizontalStyle();
        verticalStyle=((CellSetBorder)cellBorder).getInternalVerticalStyle();
      }
    }

    // Paint the background component which depends on cell set case
    public void paint(Graphics g)
    {

      //backgroung
      g.setColor(Color.white);
      g.fillRect(0,0,getWidth()-1,getHeight()-1);

      // borders
      g.setColor(Color.black);
      g.drawRect(0,0,getWidth()-1,getHeight()-1);

      // up left corner
      if(topActive)
      {
        int[] x={3,3,10};
        int[] y={7,13,10};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(3,10,10,10);
      if(leftActive)
      {
        int[] x={7,13,10};
        int[] y={3,3,10};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(10,3,10,10);

      // up middle
      g.drawLine(57,10,63,10);
      if(verticalActive)
      {
        int[] x={57,63,60};
        int[] y={3,3,10};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(60,3,60,10);

      // up right corner
      if(topActive)
      {
        int[] x={getWidth()-3,getWidth()-11,getWidth()-3};
        int[] y={7,10,13};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(getWidth()-11,10,getWidth()-4,10);
      if(rightActive)
      {
        int[] x={getWidth()-14,getWidth()-8,getWidth()-11};
        int[] y={3,3,10};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(getWidth()-11,3,getWidth()-11,10);

      // right middle
      g.drawLine(getWidth()-11,57,getWidth()-11,63);
      if(horizontalActive)
      {
        int[] x={getWidth()-3,getWidth()-11,getWidth()-3};
        int[] y={57,60,63};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(getWidth()-11,60,getWidth()-4,60);

      // down left corner
      if(bottomActive)
      {
        int[] x={3,3,10};
        int[] y={getHeight()-14,getHeight()-8,getHeight()-11};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(3,getHeight()-11,10,getHeight()-11);
      if(leftActive)
      {
        int[] x={7,13,10};
        int[] y={getHeight()-3,getHeight()-3,getHeight()-11};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(10,getHeight()-11,10,getHeight()-4);

      // left middle
      g.drawLine(10,57,10,63);
      if(horizontalActive)
      {
        int[] x={3,3,10};
        int[] y={57,63,60};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(3,60,10,60);

      // down right corner
      if(rightActive)
      {
        int[] x={getWidth()-14,getWidth()-8,getWidth()-11};
        int[] y={getHeight()-3,getHeight()-3,getHeight()-11};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(getWidth()-11,getHeight()-11,getWidth()-11,getHeight()-4);
      if(bottomActive)
      {
        int[] x={getWidth()-3,getWidth()-11,getWidth()-3};
        int[] y={getHeight()-14,getHeight()-11,getHeight()-8};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(getWidth()-11,getHeight()-11,getWidth()-4,getHeight()-11);

      // bottom middle
      g.drawLine(57,getHeight()-11,63,getHeight()-11);
      if(verticalActive)
      {
        int[] x={57,63,60};
        int[] y={getHeight()-3,getHeight()-3,getHeight()-11};
        g.fillPolygon(x,y,3);
      }
      else
        g.drawLine(60,getHeight()-11,60,getHeight()-4);

      // inside square(s)
      if(insideMode)
      {
        g.setColor(Color.lightGray);
        g.fillRect(20,20,38,38);
        g.setColor(Color.lightGray);
        g.fillRect(63,20,38,38);
        g.setColor(Color.lightGray);
        g.fillRect(20,63,38,38);
        g.setColor(Color.lightGray);
        g.fillRect(63,63,38,38);
      }
      else
      {
        g.setColor(Color.lightGray);
        g.fillRect(20,20,80,80);
      }
      updateBorders(g);
    }

    // Draw the borders which specified color and style on component
    private void updateBorders(Graphics g)
    {
      Graphics2D g2D=(Graphics2D)g;

      if(verticalColor!=null && verticalStyle!=null)
      {
        g2D.setColor(verticalColor);
        ((Graphics2D)g).setStroke(verticalStyle.getStroke());
        g2D.drawLine(60,17,60,getHeight()-17);
      }

      if(horizontalColor!=null && horizontalStyle!=null)
      {
        g2D.setColor(horizontalColor);
        ((Graphics2D)g).setStroke(horizontalStyle.getStroke());
        g2D.drawLine(17,60,getWidth()-17,60);
      }

      if(leftColor!=null && leftStyle!=null)
      {
        g2D.setColor(leftColor);
        ((Graphics2D)g).setStroke(leftStyle.getStroke());
        g2D.drawLine(15,15,15,getHeight()-15);
      }

      if(rightColor!=null && rightStyle!=null)
      {
        g2D.setColor(rightColor);
        ((Graphics2D)g).setStroke(rightStyle.getStroke());
        g2D.drawLine(getWidth()-15,15,getWidth()-15,getHeight()-15);
      }

      if(bottomColor!=null && bottomStyle!=null)
      {
        g2D.setColor(bottomColor);
        ((Graphics2D)g).setStroke(bottomStyle.getStroke());
        g2D.drawLine(15,getHeight()-15,getWidth()-15,getHeight()-15);
      }

      if(topColor!=null && topStyle!=null)
      {
        g2D.setColor(topColor);
        ((Graphics2D)g).setStroke(topStyle.getStroke());
        g2D.drawLine(15,15,getWidth()-15,15);
      }
    }

    // Set active the specified border and deactive others
    private void setActiveBorders(int activeBorders)
    {
      switch(activeBorders)
      {
        case BorderChooser.NO_BORDERS :
          topActive=leftActive=bottomActive=rightActive=verticalActive=horizontalActive=false;
          break;
        case BorderChooser.EXTERNAL_BORDERS :
          topActive=leftActive=bottomActive=rightActive=true;
          verticalActive=horizontalActive=false;
          break;
        case BorderChooser.INSIDE_BORDERS :
          topActive=leftActive=bottomActive=rightActive=false;
          verticalActive=horizontalActive=true;
          break;
        case BorderChooser.TOP_BORDER :
          topActive=true;
          leftActive=bottomActive=rightActive=verticalActive=horizontalActive=false;
          break;
        case BorderChooser.LEFT_BORDER :
          leftActive=true;
          topActive=bottomActive=rightActive=verticalActive=horizontalActive=false;
          break;
        case BorderChooser.BOTTOM_BORDER :
          bottomActive=true;
          topActive=leftActive=rightActive=verticalActive=horizontalActive=false;
          break;
        case BorderChooser.RIGHT_BORDER :
          rightActive=true;
          topActive=leftActive=bottomActive=verticalActive=horizontalActive=false;
          break;
        case BorderChooser.VERTICAL_BORDER :
          verticalActive=true;
          topActive=leftActive=bottomActive=rightActive=horizontalActive=false;
          break;
        case BorderChooser.HORIZONTAL_BORDER :
          horizontalActive=true;
          topActive=leftActive=bottomActive=rightActive=verticalActive=false;
          break;
      }
    }

    // Set color for active borders only
    private void setActiveBordersColor(Color color)
    {
      if(topActive)
        topColor=color;

      if(leftActive)
        leftColor=color;

      if(bottomActive)
        bottomColor=color;

      if(rightActive)
        rightColor=color;

      if(horizontalActive)
        horizontalColor=color;

      if(verticalActive)
        verticalColor=color;

      repaint();
    }

    // Set style for active borders only
    private void setActiveBordersStyle(BorderStyle style)
    {
      if(topActive)
        topStyle=style;

      if(leftActive)
        leftStyle=style;

      if(bottomActive)
        bottomStyle=style;

      if(rightActive)
        rightStyle=style;

      if(horizontalActive)
        horizontalStyle=style;

      if(verticalActive)
        verticalStyle=style;

      repaint();
    }

    private boolean getTopActive()
    {
      return topActive;
    }

    private boolean getLeftActive()
    {
      return leftActive;
    }

    private boolean getBottomActive()
    {
      return bottomActive;
    }

    private boolean getRightActive()
    {
      return rightActive;
    }

    private boolean getVerticalActive()
    {
      return verticalActive;
    }

    private boolean getHorizontalActive()
    {
      return horizontalActive;
    }

    private void setCurrentColor(Color color)
    {
      currentColor=color;
    }

    private Color getCurrentColor()
    {
      return currentColor;
    }

    private void setTopColor(Color color)
    {
      topColor=color;
    }

    private void setLeftColor(Color color)
    {
      leftColor=color;
    }

    private void setBottomColor(Color color)
    {
      bottomColor=color;
    }

    private void setRightColor(Color color)
    {
      rightColor=color;
    }

    private void setVerticalColor(Color color)
    {
      verticalColor=color;
    }

    private void setHorizontalColor(Color color)
    {
      horizontalColor=color;
    }

    private Color getTopColor()
    {
      return topColor;
    }

    private Color getLeftColor()
    {
      return leftColor;
    }

    private Color getBottomColor()
    {
      return bottomColor;
    }

    private Color getRightColor()
    {
      return rightColor;
    }

    private Color getVerticalColor()
    {
      return verticalColor;
    }

    private Color getHorizontalColor()
    {
      return horizontalColor;
    }

    private void setCurrentStyle(BorderStyle borderStyle)
    {
      currentStyle=borderStyle;
    }

    private BorderStyle getCurrentStyle()
    {
      return currentStyle;
    }

    private void setTopStyle(BorderStyle style)
    {
      topStyle=style;
    }

    private void setLeftStyle(BorderStyle style)
    {
      leftStyle=style;
    }

    private void setBottomStyle(BorderStyle style)
    {
      bottomStyle=style;
    }

    private void setRightStyle(BorderStyle style)
    {
      rightStyle=style;
    }

    private void setVerticalStyle(BorderStyle style)
    {
      verticalStyle=style;
    }

    private void setHorizontalStyle(BorderStyle style)
    {
      horizontalStyle=style;
    }

    private BorderStyle getTopStyle()
    {
      return topStyle;
    }

    private BorderStyle getLeftStyle()
    {
      return leftStyle;
    }

    private BorderStyle getBottomStyle()
    {
      return bottomStyle;
    }

    private BorderStyle getRightStyle()
    {
      return rightStyle;
    }

    private BorderStyle getVerticalStyle()
    {
      return verticalStyle;
    }

    private BorderStyle getHorizontalStyle()
    {
      return horizontalStyle;
    }
  }

  /**
   * Method which return the top border style.
   *
   * @return topStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getTopStyle()
  {
    return borderComponent.getTopStyle();
  }

  /**
   * Method which return the top border color.
   *
   * @return topColor : java.awt.Color
   */
  public Color getTopColor()
  {
    return borderComponent.getTopColor();
  }

  /**
   * Method which return the bottom border style.
   *
   * @return bottomStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getBottomStyle()
  {
    return borderComponent.getBottomStyle();
  }

  /**
   * Method which return the bottom border color.
   *
   * @return bottomColor : java.awt.Color
   */
  public Color getBottomColor()
  {
    return borderComponent.getBottomColor();
  }

  /**
   * Method which return the left border style.
   *
   * @return leftStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getLeftStyle()
  {
    return borderComponent.getLeftStyle();
  }

  /**
   * Method which return the left border color.
   *
   * @return leftColor : java.awt.Color
   */
  public Color getLeftColor()
  {
    return borderComponent.getLeftColor();
  }

  /**
   * Method which return the right border style.
   *
   * @return rightStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getRightStyle()
  {
    return borderComponent.getRightStyle();
  }

  /**
   * Method which return the right border color.
   *
   * @return rightColor : java.awt.Color
   */
  public Color getRightColor()
  {
    return borderComponent.getRightColor();
  }

  /**
   * Method which return the horizontal border style.
   *
   * @return horizontalStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getInternalHorizontalStyle()
  {
    return borderComponent.getHorizontalStyle();
  }

  /**
   * Method which return the horizontal border color.
   *
   * @return horizontalColor : java.awt.Color
   */
  public Color getInternalHorizontalColor()
  {
    return borderComponent.getHorizontalColor();
  }

  /**
   * Method which return the vertical border style.
   *
   * @return verticalStyle : com.eteks.openjeks.format.BorderStyle
   */
  public BorderStyle getInternalVerticalStyle()
  {
    return borderComponent.getVerticalStyle();
  }

  /**
   * Method which return the vertical border color.
   *
   * @return verticalColor : java.awt.Color
   */
  public Color getInternalVerticalColor()
  {
    return borderComponent.getVerticalColor();
  }
}

