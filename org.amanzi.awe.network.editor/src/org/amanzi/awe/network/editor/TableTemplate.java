package org.amanzi.awe.network.editor;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * This class represents the Network View.
 */
public class TableTemplate extends ViewPart {
    public static final String NETWORK_VIEW_ID = "org.amanzi.awe.network.editor.TableTemplate";
    /**
     * Tree viewer reference.
     */
    private TableViewer viewer;
    /**
     * Action for double click event.
     */
    private Action doubleClickAction;
 
    private Table table;

    private TableColumn column;
    
    private Object[][] Data;
    
    private String[] columnNames;
    
    private ArrayList<String> ArrayData;
    /**
     * The constructor.
     */
    public TableTemplate(Object[][] data,String[] columns) {
              Data=data;
              columnNames=columns;
    }
    
    public TableTemplate(ArrayList<String> dataArray,String[] columns) {
              ArrayData=dataArray;
              columnNames=columns;
    }
    /**
     * Disposes the title image when super is called and then hides the view.
     */
    @Override
    public void dispose() {
        super.dispose();
        getViewSite().getPage().hideView(this);
    }
    
    public void setData(Object[][] tableData){
    	this.Data=tableData;
    }   
    
    public Object[][] getData(){
    	return Data;	
    }
    
    public void populateFromArrayList(){
    	viewer.setInput(ArrayData);
    	viewer.refresh();
    	
    }
    
    
    private void populateFrom2DArray(){
    	for(int k=0; k<Data[0].length; k++) {
    	TableItem item = new TableItem (table, SWT.NONE);
    	for (int i=0; i<columnNames.length; i++) {
    		item.setText (i, (String)Data[k][i]);
    	  }
    	 }
    	for (int i=0; i<columnNames.length; i++) {
    		table.getColumn (i).pack ();
    	}	
    	}
   
    /**
     * This is a call back method that will allow us to create the view and initialize it with its
     * contents.
     */
    public void createPartControl( final Composite parent ) {
    	table=new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    	createTable();
        viewer = new TableViewer(table);
        viewer.setUseHashlookup(true);
        viewer.setColumnProperties(columnNames);
        CellEditor[] editors = new CellEditor[columnNames.length];
        for(int i=0;i<columnNames.length;i++)
        {
        	editors[0] = new TextCellEditor(table);
        }
        viewer.setCellEditors(editors);
        createMenu();
    }

    private void createTable()
    {
    	if(columnNames!=null)
    	{
    	  for(int i=0;i<columnNames.length;i++)
    	  {
    		addColumn(columnNames[1]);
    	  }
        }
    	try
    	{
    		if(Data!=null)
    		{
    			populateFrom2DArray();
    		}
    		else if(ArrayData!=null)
    		{
    			populateFromArrayList();
    		}
    		
    	}
    	catch(Exception exc)
    	{
    		
    	}
    }

    private void addColumn(String ColumnName)
    {
    	 column = new TableColumn(table, SWT.LEFT, 1);
         column.setText(ColumnName);
  
        // column.setWidth(400);
         
         column.addSelectionListener(new SelectionAdapter() {
           	
              public void widgetSelected(SelectionEvent e) {
                   
                   }
              });
    }
    
    private void createMenu() {
       
    }

   
    /**
     * Consolidated actions.
     */
    public void makeActions() {
       
    }
    /**
     * Hooks the double click action.
     */
    public void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick( DoubleClickEvent event ) {
                doubleClickAction.run();
            }
        });
    }
    /**
     * setting the focus when view is opened.
     */
    public void setFocus() {
        if (viewer != null)
            viewer.getControl().setFocus();
    }
    /**
     * getter for Viewer so that other classes can access Viewer associated with this View.
     * 
     * @return TreeViewer
     */
    public TableViewer getViewer() {
        return viewer;
    }
    /**
     * Setter for the viewer to set the viewer for this particular view.
     * 
     * @param viewer
     */
    public void setViewer( TableViewer viewer ) {
        this.viewer = viewer;
    }

    
   
    
}