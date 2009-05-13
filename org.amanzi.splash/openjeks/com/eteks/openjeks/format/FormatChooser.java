/*
 * @(#)FormatChooser.java   09/03/2003
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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel for number format choice.
 *
 * @see com.eteks.openjeks.format.CellFormat
 * @see com.eteks.openjeks.format.CellFormatPanel
 * @author  Emmanuel Puybaret, Jean-Baptiste C�r�zat
 */
public class FormatChooser extends JPanel
{
  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.format.resources.format");

  private Object preview = new Integer (123);
  private JLabel previewLabel = new JLabel ();
  private Format format;
  private FormatPanel standardPanel=null;
  private FormatPanel datePanel=null;
  private FormatPanel timePanel=null;
  private FormatPanel textPanel=null;
  private DecimalCountFormatPanel numberPanel=null;
  private DecimalCountFormatPanel currencyPanel=null;
  private DecimalCountFormatPanel percentagePanel=null;
  private DecimalCountFormatPanel scientificPanel=null;
  private JList categoryList=null;
  private JList dateFormatList=null;
  private JList timeFormatList=null;
  private JList currencyList=null;
  private JCheckBox groupingCheckBox=null;

  private class FormatPanel extends JPanel
  {
    private Format format;

    private FormatPanel (LayoutManager layout, Format format)
    {
      super (layout);
      this.format = format;
    }

    private Format getFormat ()
    {
      return format;
    }

    public void setFormat(Format format)
    {
      this.format = format;
      previewLabel.setText (format==null || format instanceof MessageFormat? preview.toString() : format.format(preview));
    }
  }

  private class DecimalCountFormatPanel extends FormatPanel
  {
    private JComponent decimalCountComponent = null;
    private boolean separator=false;

    private DecimalCountFormatPanel (Format format)
    {
      super (new BorderLayout (3, 3), format);
      JPanel decimalCountPanel = new JPanel (new FlowLayout (FlowLayout.LEFT, 3, 3));
      decimalCountPanel.add(new JLabel (resourceBundle.getString ("FORMAT_NUMBER_DECIMAL_COUNT")));

      try
      {
        decimalCountComponent = (JComponent)Class.forName("javax.swing.JSpinner").newInstance();
        SpinnerNumberModel spinnerModel=new SpinnerNumberModel(0,0,10,1);
        ((JSpinner)decimalCountComponent).setModel(spinnerModel);
        JFormattedTextField textField=(JFormattedTextField)((JSpinner.DefaultEditor)((JSpinner)decimalCountComponent).getEditor()).getTextField();
        textField.setEditable(false);
        textField.setBackground(Color.white);
        ((JSpinner)decimalCountComponent).addChangeListener(new ChangeListener()
        {
          public void stateChanged(ChangeEvent ce)
          {
            Format format=getFormat();
            setFormat(format);
            previewLabel.setText(format.format(preview));
          }
        });
      }
      catch (ClassNotFoundException ex)
      {
        // JSpinner is available only with JDK >= 1.4
        decimalCountComponent = new JTextField ();
      }
      catch (IllegalAccessException ex)
      {  }
      catch (InstantiationException ex)
      {  }
      decimalCountPanel.add (decimalCountComponent);
      add (decimalCountPanel, BorderLayout.NORTH);
    }

   

    private void setDecimalCountComponent(int decimals)
    {
      if(decimalCountComponent instanceof JTextField)
        ((JTextField)decimalCountComponent).setText(String.valueOf(decimals));
      else
        ((JSpinner)decimalCountComponent).setValue(new Integer(decimals));
    }

    private Format getFormat()
    {
      format=super.getFormat();

      Object decimals;
      if(decimalCountComponent instanceof JTextField)
        decimals=new Integer(((JTextField)decimalCountComponent).getText());
      else
        decimals=((JSpinner)decimalCountComponent).getValue();

      ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(separator);
      ((DecimalFormat)format).setMaximumFractionDigits(((Integer)decimals).intValue());
      ((DecimalFormat)format).setMinimumFractionDigits(((Integer)decimals).intValue());

      return format;
    }

    private void setSeparator(boolean isChecked)
    {
      separator=isChecked;
    }
  }

  /**
   * Constructor uses to instanciate a font settings selection panel with specific settings.<br />
   * If the paramaters are null, the lists are not selected
   *
   * @param preview : java.lang.Object
   * @param defaultFormat : java.text.Format
   */
  public FormatChooser (Object preview, Format defaultFormat)
  {
    if (preview != null)
      this.preview = preview;
    this.format = defaultFormat;
    previewLabel.setText(defaultFormat==null || defaultFormat instanceof MessageFormat?preview.toString():format.format(preview));

    // Standard category subpanel
    standardPanel = new FormatPanel (new FlowLayout (FlowLayout.LEFT, 3, 3), null);
    standardPanel.add(new JLabel (resourceBundle.getString ("FORMAT_NUMBER_STANDARD_LABEL")));
    standardPanel.setFormat(null);

    // Number category subpanel
    numberPanel = new DecimalCountFormatPanel ((Format)NumberFormat.getNumberInstance());
    JPanel groupingPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
    groupingCheckBox = new JCheckBox (resourceBundle.getString ("FORMAT_NUMBER_USE_GROUPING"));
    groupingCheckBox.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
          numberPanel.setSeparator(true);
        else
          numberPanel.setSeparator(false);
      }
    });
    groupingPanel.add (groupingCheckBox);
    numberPanel.add (groupingPanel, BorderLayout.CENTER);

    // Currency category subpanel
    currencyPanel = new DecimalCountFormatPanel (NumberFormat.getCurrencyInstance());
    Locale[] locales=Locale.getAvailableLocales();
    Comparator comparator=new Comparator()
    {
      public int compare(Object o1,Object o2)
      {
        String text1=((TextObjectEntry)o1).getText();
        String text2=((TextObjectEntry)o2).getText();

        return text1.compareTo(text2);
      }

      public boolean equals(Object obj)
      {
        return equals(obj);
      }
    };
    TreeSet currenciesTree=new TreeSet(comparator);
    int count=0;
    for(int i=0;i<locales.length;i++)
    {
      NumberFormat currency=NumberFormat.getCurrencyInstance(locales[i]);
      String symbol=currency.getCurrency().getSymbol(locales[i]);
      if(symbol!="XXX")
      {
        currenciesTree.add(new TextObjectEntry(locales[i].getDisplayCountry() + " " + symbol,currency));
        count++;
      }
    }
    Object[] currencies=new Object[count];
    currenciesTree.toArray(currencies);
    currencyList=new JList(currencies);
    currencyList.addListSelectionListener(new ListSelectionListener ()
    {
      public void valueChanged (ListSelectionEvent ev)
      {
        int decimals=((DecimalFormat)((TextObjectEntry)currencyList.getSelectedValue()).getValue()).getMaximumFractionDigits();
        currencyPanel.setFormat((DecimalFormat)((TextObjectEntry)currencyList.getSelectedValue()).getValue());
        currencyPanel.setDecimalCountComponent(decimals);
      }
    });
    JScrollPane currencyScroll=new JScrollPane(currencyList);
    currencyPanel.add(currencyScroll);

    // Date category subpanel
    datePanel = new FormatPanel (new BorderLayout (3, 3), DateFormat.getDateInstance());
    datePanel.add(new JLabel (resourceBundle.getString ("FORMAT_NUMBER_TYPE_LABEL")), BorderLayout.NORTH);
    DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    DateFormat longDateFormat = DateFormat.getDateInstance(DateFormat.LONG);
    DateFormat fullDateFormat = DateFormat.getDateInstance(DateFormat.FULL);
    DateFormat shortDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    DateFormat mediumDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    Date now = new Date ();
    Object [] dateFormats = {new TextObjectEntry (shortDateFormat.format (now), shortDateFormat),
                             new TextObjectEntry (mediumDateFormat.format (now), mediumDateFormat),
                             new TextObjectEntry (longDateFormat.format (now), longDateFormat),
                             new TextObjectEntry (fullDateFormat.format (now), fullDateFormat),
                             new TextObjectEntry (shortDateTimeFormat.format (now), shortDateTimeFormat),
                             new TextObjectEntry (mediumDateTimeFormat.format (now), mediumDateTimeFormat)};
    dateFormatList = new JList (dateFormats);
    dateFormatList.addListSelectionListener(new ListSelectionListener ()
    {
      public void valueChanged (ListSelectionEvent ev)
      {
        datePanel.setFormat((SimpleDateFormat)((TextObjectEntry)dateFormatList.getSelectedValue()).getValue());
      }
    });
    datePanel.add (new JScrollPane (dateFormatList), BorderLayout.CENTER);

    // Time category subpanel
    timePanel=new FormatPanel(new BorderLayout (3, 3),DateFormat.getTimeInstance());
    timePanel.add(new JLabel(resourceBundle.getString("FORMAT_NUMBER_TYPE_LABEL")),BorderLayout.NORTH);
    DateFormat shortTimeFormat=DateFormat.getTimeInstance(DateFormat.SHORT);
    DateFormat mediumTimeFormat=DateFormat.getTimeInstance(DateFormat.MEDIUM);
    DateFormat longTimeFormat=DateFormat.getTimeInstance(DateFormat.LONG);
    DateFormat fullTimeFormat=DateFormat.getTimeInstance(DateFormat.FULL);
    Object[] timeFormats={new TextObjectEntry(shortTimeFormat.format (now), shortTimeFormat),
                             new TextObjectEntry(mediumTimeFormat.format (now), mediumTimeFormat),
                             new TextObjectEntry(longTimeFormat.format (now), longTimeFormat),
                             new TextObjectEntry(fullTimeFormat.format (now), fullTimeFormat)};
    timeFormatList=new JList(timeFormats);
    timeFormatList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent ev)
      {
        timePanel.setFormat((SimpleDateFormat)((TextObjectEntry)timeFormatList.getSelectedValue()).getValue());
      }
    });
    timePanel.add (new JScrollPane (timeFormatList), BorderLayout.CENTER);

    // Percentage category subpanel
    percentagePanel=new DecimalCountFormatPanel (defaultFormat);
    percentagePanel.setFormat(NumberFormat.getPercentInstance());

    // Scientific category subpanel
    scientificPanel=new DecimalCountFormatPanel (defaultFormat);
    scientificPanel.setFormat(new DecimalFormat("##0.##E0"));

    // Text category panel
    textPanel=new FormatPanel (new FlowLayout (FlowLayout.LEFT, 3, 3), defaultFormat);
    textPanel.add(new JLabel (resourceBundle.getString ("FORMAT_NUMBER_TEXT_LABEL")));
    textPanel.setFormat(new MessageFormat("null"));

    TextObjectEntry[] categories={
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_STANDARD"),
                                                 standardPanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_NUMBER"),
                                                 numberPanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_CURRENCY"),
                                                 currencyPanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_DATE"),
                                                  datePanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_HOUR"),
                                                  timePanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_PERCENTAGE"),
                                                  percentagePanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_SCIENTIFIC"),
                                                  scientificPanel),
                            new TextObjectEntry (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_TEXT"),
                                                  textPanel)};

    final CardLayout categoriesLayout = new CardLayout ();
    final JPanel categoriesPanel = new JPanel (categoriesLayout);
    for (int i = 0; i < categories.length; i++)
      categoriesPanel.add ((JPanel)categories [i].getValue (), categories [i].getText ());

    JPanel categoryListPanel = new JPanel (new BorderLayout (3, 3));
    categoryListPanel.add (new JLabel (resourceBundle.getString ("FORMAT_NUMBER_CATEGORY_LABEL")), BorderLayout.NORTH);
    categoryList = new JList (categories);
    categoryList.addListSelectionListener(new ListSelectionListener ()
    {
      public void valueChanged (ListSelectionEvent ev)
      {
        categoriesLayout.show(categoriesPanel,((TextObjectEntry)categoryList.getSelectedValue()).getText());
        Format tempFormat=(Format)((FormatPanel)((TextObjectEntry)categoryList.getSelectedValue()).getValue()).getFormat();
        previewLabel.setText (tempFormat==null || tempFormat instanceof MessageFormat? getPreview().toString() : tempFormat.format(getPreview()));
      }
    });
    categoryListPanel.add (new JScrollPane (categoryList), BorderLayout.CENTER);

    JPanel formatPreviewPanel  = new JPanel (new BorderLayout ());
    previewLabel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString ("FORMAT_FONT_PREVIEW_TITLE")));
    formatPreviewPanel.add (previewLabel, BorderLayout.NORTH);
    formatPreviewPanel.add (categoriesPanel, BorderLayout.CENTER);

    setLayout (new BorderLayout (3, 3));
    add (categoryListPanel, BorderLayout.WEST);
    add (formatPreviewPanel, BorderLayout.CENTER);

    setSelectedFormat(this.format);
  }

  void setSelectedFormat(Format format)
  {
    if(format==null)
    {
      categoryList.setSelectedValue(new TextObjectEntry(null,standardPanel),true);
    }
    else if(format instanceof NumberFormat)
    {
      int decimals=((DecimalFormat)format).getMaximumFractionDigits();
      String test=((DecimalFormat)format).format(10);
      try
      {
        if(test.endsWith("E0"))
        {
          categoryList.setSelectedValue(new TextObjectEntry(null,scientificPanel),true);
          scientificPanel.setDecimalCountComponent(decimals);
        }
        else
        {
          new Double(test.replace(',','.'));
          categoryList.setSelectedValue(new TextObjectEntry(null,numberPanel),true);
          numberPanel.setDecimalCountComponent(decimals);
          groupingCheckBox.setSelected(((DecimalFormat)format).isDecimalSeparatorAlwaysShown());
        }
      }
      catch(NumberFormatException e)
      {
        if(test.endsWith("%"))
        {
          categoryList.setSelectedValue(new TextObjectEntry(null,percentagePanel),true);
          percentagePanel.setDecimalCountComponent(decimals);
        }
        else
        {
          categoryList.setSelectedValue(new TextObjectEntry(null,currencyPanel),true);
          String inputCurrency=((Currency)((DecimalFormat)format).getCurrency()).getCurrencyCode();
          for(int i=0;i<currencyList.getModel().getSize();i++)
          {
            String currencyToCompare=((Currency)((DecimalFormat)((TextObjectEntry)currencyList.getModel().getElementAt(i)).getValue()).getCurrency()).getCurrencyCode();
            if(inputCurrency==currencyToCompare)
            {
              currencyList.setSelectedIndex(i);
              break;
            }
          }
          currencyPanel.setDecimalCountComponent(decimals);
        }
      }
    }
    else if(format instanceof SimpleDateFormat)
    {
      categoryList.setSelectedValue(new TextObjectEntry(null,datePanel),true);
      dateFormatList.setSelectedValue(new TextObjectEntry(null,format),true);
      if(dateFormatList.getSelectedIndex()==-1)
      {
        categoryList.setSelectedValue(new TextObjectEntry(null,timePanel),true);
        timeFormatList.setSelectedValue(new TextObjectEntry(null,format),true);
      }
    }
    else if(format instanceof MessageFormat)
    {
      categoryList.setSelectedValue(new TextObjectEntry(null,textPanel),true);
    }
  }

  private Object getPreview()
  {
    return preview;
  }

  /**
   * Method which return the format selected.
   *
   * @return format : java.text.Format
   */
  public Format getFormat()
  {
    return (Format)((FormatPanel)((TextObjectEntry)categoryList.getSelectedValue()).getValue()).getFormat();
  }
}

