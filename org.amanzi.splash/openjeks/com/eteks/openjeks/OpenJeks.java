
package com.eteks.openjeks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatPanel;
import com.eteks.openjeks.format.CellFormatRenderer;
import com.eteks.openjeks.format.TableFormat;

public class OpenJeks
{
	public ProtoFrame frame;
	public OpenJeks()
	{
		frame=new ProtoFrame();
	}
	
//  public static void main(String[] argv)
//  {
//    ProtoFrame frame=new ProtoFrame();
//  }
}

class ProtoFrame extends JFrame implements ActionListener
{
  JTable table = null;
  CellFormatPanel cellFormatPanel = null;
  CellFormat cellFormat = null;
  CellFormatRenderer renderer = null;
  TableFormat tableFormat = null;

  private ResourceBundle resourceBundle = ResourceBundle.getBundle ("com.eteks.openjeks.resources.openjeks");

  ProtoFrame()
  {
    getContentPane().setLayout(new BorderLayout());

    JPanel menupanel=new JPanel(new BorderLayout());
    JButton button=new JButton(resourceBundle.getString ("PRINT"));
    JLabel label=new JLabel(resourceBundle.getString ("TIPS"));

    button.addActionListener(this);

    menupanel.add(button,BorderLayout.NORTH);
    menupanel.add(label,BorderLayout.SOUTH);

    getContentPane().add(menupanel,BorderLayout.NORTH);

    table=new JTable(500,200);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setCellSelectionEnabled(true);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0,0));

    tableFormat = new TableFormat();
    renderer = new CellFormatRenderer (tableFormat);

    table.setDefaultRenderer(Object.class, renderer);
    table.setDefaultRenderer(Long.class, renderer);
    table.setDefaultRenderer(Double.class, renderer);
    table.setDefaultRenderer(String.class, renderer);

    table.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent me)
      {
        if(me.getButton()==3)
          launchCellFormatPanel();
      }
    });

    JScrollPane scrollpane = new JScrollPane(table);
    getContentPane().add(scrollpane,BorderLayout.CENTER);

    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    show();
  }

  void launchCellFormatPanel()
  {
    int firstRow, firstColumn, lastRow, lastColumn;
    firstRow = table.getSelectedRow();
    firstColumn = table.getSelectedColumn();
    lastRow = firstRow + table.getSelectedRowCount() - 1;
    lastColumn = firstColumn + table.getSelectedColumnCount() - 1;

    cellFormat = tableFormat.getFormatAt(firstRow, firstColumn , lastRow, lastColumn);

    cellFormatPanel = new CellFormatPanel(cellFormat);
    if (JOptionPane.showConfirmDialog(null,
                                      cellFormatPanel,
                                      resourceBundle.getString ("FORMAT_PANEL_TITLE"),
                                      JOptionPane.OK_CANCEL_OPTION ,
                                      JOptionPane.PLAIN_MESSAGE) == 0)
    {
      cellFormat = cellFormatPanel.getCellFormat();
      tableFormat.setFormatAt(cellFormat, firstRow, firstColumn , lastRow, lastColumn);
    }
    cellFormatPanel = null;
    table.repaint();
  }

  public void actionPerformed(ActionEvent ae)
  {
    printData();
  }

  public void printData()
  {
/*	  
    int numRow=tableFormat.getLastFormatRow();
    int numCol=tableFormat.getLastFormatColumn();

    // Needs to detect if there are cells with content and no format after last format
    for(int i=0;i<table.getModel().getColumnCount();i++)
      for(int j=0;j<table.getModel().getRowCount();j++)
      {
        if(table.getModel().getValueAt(j,i)!=null)
        {
          if(numRow<j)
            numRow=j;
          if(numCol<i)
            numCol=i;
        }
      }

    // Initialisation of the JTable to be printed
    JTable printTable=new JTable(numRow+1,numCol+1);

    for(int i=0;i<=numCol;i++)
      for(int j=0;j<=numRow;j++)
        printTable.getColumnModel().addColumn(table.getColumnModel().getColumn(i));

    for(int i=0;i<=numCol;i++)
      for(int j=0;j<=numRow;j++)
        printTable.getColumnModel().removeColumn(printTable.getColumnModel().getColumn(i));

    tableFormat.setPaintDefault(false); // set the grid to white to not print it
    printTable.setDefaultRenderer(Object.class, table.getDefaultRenderer(Object.class));
    printTable.setDefaultRenderer(Long.class, table.getDefaultRenderer(Long.class));
    printTable.setDefaultRenderer(Double.class, table.getDefaultRenderer(Double.class));
    printTable.setDefaultRenderer(String.class, table.getDefaultRenderer(String.class));
    printTable.setShowGrid(false);
    printTable.setIntercellSpacing(new Dimension(0,0));
    printTable.setTableHeader(null);
    printTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // set the values to be printed
    for(int i=0;i<=numCol;i++)
      for(int j=0;j<=numRow;j++)
      {
        printTable.getModel().setValueAt(table.getModel().getValueAt(j,i),j,i);
      }

    JScrollPane scrollPreview=new JScrollPane(printTable);
    // If we don't do that we print an empty table
    if(JOptionPane.showConfirmDialog(this,scrollPreview)==0)
    {
      try
      {
        printTable.print(JTable.PrintMode.NORMAL);
      }
      catch(PrinterException pe)
      {
        JOptionPane.showMessageDialog(null,"Pb d'impression!");
      }
    }

    tableFormat.setPaintDefault(true);
    table.repaint();
 */
  }
}
