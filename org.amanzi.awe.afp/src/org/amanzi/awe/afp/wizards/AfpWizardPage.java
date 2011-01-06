package org.amanzi.awe.afp.wizards;

import java.util.ArrayList;

import org.amanzi.awe.afp.filters.AfpColumnFilter;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.filters.AfpTRXFilter;
import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.neo4j.graphdb.Node;

public class AfpWizardPage extends WizardPage implements SelectionListener {
	
	private Label filterInfoLabel;
	private Group trxFilterGroup;
	private Label siteFilterInfoLabel;
	private Group siteTrxFilterGroup;
	protected AfpModel model;
	private TableViewer viewer;
	private AfpTRXFilter filter;
	private FilterListener listener;

	protected AfpWizardPage(String pageName) {
		super(pageName);
	}
	
	protected AfpWizardPage(String pageName, AfpModel model) {
		super(pageName);
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	public void refreshPage() {
		if (this instanceof AfpSeparationRulesPage){
			filterInfoLabel.setText(String.format("Filter Status: %d sectors selected out of %d", model.totalSectors, model.totalSectors));
			siteFilterInfoLabel.setText(String.format("Filter Status: %d sites selected out of %d", model.totalSites, model.totalSites));
			trxFilterGroup.layout();
			siteTrxFilterGroup.layout();
		}
		else if(this instanceof AfpSYHoppingMALsPage){
			filterInfoLabel.setText(String.format("Filter Status: %d Trxs selected out of %d", 0, model.totalTRX));
			trxFilterGroup.layout();
		}
		
		else{
			String info = String.format("Filter Status: %d Trxs selected out of %d", model.totalTRX, model.totalTRX);
			filterInfoLabel.setText(info);
			trxFilterGroup.layout();
		}
		
	}
	protected Table addTRXFilterGroup(Group main, String[] headers, int emptyrows, boolean isSite, FilterListener listener){
		final Shell parentShell = main.getShell();
		this.listener = listener;
		
		/** Create TRXs Filters Group */
    	Group trxFilterGroup = new Group(main, SWT.NONE);
    	trxFilterGroup.setLayout(new GridLayout(4, false));
    	trxFilterGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,1 ,2));
    	trxFilterGroup.setText("TRXs Filter");
    	
    	Label filterInfoLabel = new Label(trxFilterGroup, SWT.LEFT);
    	
    	if (isSite){
    		this.siteTrxFilterGroup = trxFilterGroup;
    		this.siteFilterInfoLabel = filterInfoLabel;
    	}
    	else {
    		this.trxFilterGroup = trxFilterGroup;
    		this.filterInfoLabel = filterInfoLabel;
    	}
    		
    	
    	Button loadButton = new Button(trxFilterGroup, SWT.RIGHT);
    	loadButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 1 , 1));
    	loadButton.setText("Load");
    	loadButton.setEnabled(false);
    	loadButton.addSelectionListener(this);
    	
    	Button clearButton = new Button(trxFilterGroup, SWT.RIGHT);
    	clearButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1 , 1));
    	clearButton.setText("Clear");
    	clearButton.setEnabled(false);
    	clearButton.addSelectionListener(this);
    	
    	Button assignButton = new Button(trxFilterGroup, SWT.RIGHT);
    	assignButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1 , 1));
    	assignButton.setText("Assign");
    	assignButton.setEnabled(false);
    	assignButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO write function for shell
    			final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
    			subShell.setText("Assign to Domain");
    			subShell.setLayout(new GridLayout(2, false));
    			subShell.setLocation(200, 200);
    			
    			Label infoLabel = new Label (subShell, SWT.LEFT);
    			//TODO update label to show correct no. of TRXs
    			infoLabel.setText("Selected x TRXs will be assigned to:");
    			infoLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
    			
    			Label domainLabel = new Label (subShell, SWT.LEFT);
    			domainLabel.setText("Select Domain");
    			domainLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
    			
    			Combo domainCombo = new Combo(subShell, SWT.DROP_DOWN);
    			//TODO populate combo values
    			domainCombo.setItems(new String[]{"Dummy"});
    			domainCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
    			domainCombo.addModifyListener(new ModifyListener(){

    				@Override
    				public void modifyText(ModifyEvent e) {
    					// TODO Auto-generated method stub
    					
    				}
    				
    			});
    			
    			domainCombo.addSelectionListener(new SelectionListener(){

    				@Override
    				public void widgetDefaultSelected(SelectionEvent e) {
    					// TODO Auto-generated method stub
    					
    				}

    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					widgetSelected(e);
    				}
    				
    			});
    			
    			Button selectButton = new Button(subShell, SWT.PUSH);
    			selectButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
    			selectButton.setText("Assign");
    			selectButton.addSelectionListener(new SelectionAdapter(){
    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					//TODO do something here
    					subShell.dispose();
    				}
    			});
    			
    			Button cancelButton = new Button(subShell, SWT.PUSH);
    			cancelButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
    			cancelButton.setText("Cancel");
    			cancelButton.addSelectionListener(new SelectionAdapter(){
    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					subShell.dispose();
    				}
    			});
    			
    			subShell.pack();
    			subShell.open();
			}
    		
    	});
    	
    	viewer = new TableViewer(trxFilterGroup, SWT.H_SCROLL | SWT.V_SCROLL);
    	Table filterTable = viewer.getTable();
    	filterTable.setHeaderVisible(true);
    	filterTable.setLinesVisible(true);
//    	filter = new AfpTRXFilter();
//    	viewer.addFilter(filter);
    	for (String item : headers) {
    		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
  	      	TableColumn column = viewerColumn.getColumn();
  	      	column.setText(item);
  	      	column.setData(item);
  	      	column.setResizable(true);
  	      	column.addListener(SWT.Selection, new ColumnFilterListener());
  	    }
    	
    	
    	

//		Table filterTable = new Table(trxFilterGroup, SWT.VIRTUAL | SWT.MULTI);
//		filterTable.setHeaderVisible(true);
		GridData tableGrid = new GridData(GridData.FILL, GridData.CENTER, false, false, 4 ,1);
		filterTable.setLayoutData(tableGrid);
		
//	    for (String item : headers) {
//	      TableColumn column = new TableColumn(filterTable, SWT.NONE);
//	      column.setText(item);
//	    }
	    for (int i=0;i<emptyrows;i++) {
	    	TableItem item = new TableItem(filterTable, SWT.NONE);
	    	for (int j = 0; j < headers.length; j++){
    			item.setText(j, "");
	    	}
	    }
	    for (int i = 0; i < headers.length; i++) {
	    	filterTable.getColumn(i).pack();
	    }
	    return filterTable;

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param colName
	 * @return unique values for the column
	 */
	public String[] getUniqueValues(String colName){
		if (colName.equals("Band"))
			return new String[]{"900", "1800", "850", "1900"};
		if (colName.equals("TRX_ID"))
			return new String[]{"0", "1", "2", "3"};
		if (colName.equals("Site"))
			return new String[]{"AMZ04345", "AMZ04343", "AMZ02652", "AMZ02653", "AMZ02570"};
		if (colName.equals("Sector"))
			return new String[]{"4345A", "4345B", "4345C", "4345D", "4343A", "4343B", "4343C", "4343D"};
		
		return new String[]{"900", "1800", "850", "1900"};
	}
	
	class ColumnFilterListener implements Listener{
		
		
		
		@Override
		public void handleEvent(Event event) {
			final ArrayList<String> selectedValues = new ArrayList<String>();
			final Shell subShell = new Shell(event.widget.getDisplay(), SWT.PRIMARY_MODAL);
			subShell.setLayout(new GridLayout(2, false));
			subShell.setLocation(300, 200);
			
			final String col = (String)event.widget.getData();
			
			Group filterGroup = new Group(subShell, SWT.NONE);
			filterGroup.setLayout(new GridLayout(2, false));
			filterGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,2 ,1));
			filterGroup.setText(col);
			
			String[] values = getUniqueValues(col);
		    final Tree tree = new Tree(filterGroup, SWT.CHECK | SWT.BORDER);
		    tree.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,2 ,1));
		    
	    	for (String value : values){
	    		TreeItem item = new TreeItem(tree, 0);
		        item.setText(value);
	    	}

			Button applyButton = new Button(filterGroup, SWT.PUSH);
			applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 2, 1));
			applyButton.setText("Apply");
			applyButton.addSelectionListener(new SelectionAdapter(){
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (TreeItem item : tree.getItems()){
						if (item.getChecked()){
							selectedValues.add(item.getText());
						}
					}
					listener.onFilterSelected(col, selectedValues);
//					filter.setEqualityText("900");
//					viewer.refresh(true);
					subShell.dispose();
				}
				
			});

			
			subShell.pack();
			subShell.open();
			
		}//end handle event
		
	}
	

}
