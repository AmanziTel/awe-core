/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.splash.views.importbuilder;


import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.ui.SplashResourceEditor;
import org.amanzi.splash.utilities.Messages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.internal.core.CreateRubyScriptOperation;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;


public class ImportBuilderTableViewer {
    
    /** String DEFAULT_SCRIPT_NAME field */
    private static final String DEFAULT_SCRIPT_NAME = "FilterX.rb";

    /**
	 * @param parent
	 */
	public ImportBuilderTableViewer(Composite parent) {
		
		this.addChildControls(parent);
	}

	//	private Shell shell;
	private Table table;
	private TableViewer tableViewer;
	private Button runButton;
	
	// Create a ImportBuilderFilterList and assign it to an instance variable
	private ImportBuilderFilterList filtersList = new ImportBuilderFilterList(); 

	// Set the table column property names
	
	private final String FILTER_HEADING_COLUMN 			= "Filter Heading";
	private final String FILTER_TEXT_COLUMN 	= "Filter Text";
	
	

	// Set column names
	private String[] columnNames = new String[] { 
			//COMPLETED_COLUMN, 
			FILTER_HEADING_COLUMN,
			FILTER_TEXT_COLUMN
			};

	/**
	 * Release resources
	 */
	public void dispose() {
		
		// Tell the label provider to release its ressources
		tableViewer.getLabelProvider().dispose();
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * @return the shell that was created	 
	 */
	private void addChildControls(Composite composite) {

		// Create a composite to hold the children
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
		composite.setLayoutData (gridData);

		// Set numColumns to 3 for the buttons 
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 4;
		composite.setLayout (layout);

		// Create the table 
		createTable(composite);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new ExampleContentProvider());
		tableViewer.setLabelProvider(new ImportBuilderLabelProvider());
		// The input for the table viewer is the instance of ImportBuilderFilterList
		filtersList = new ImportBuilderFilterList();
		tableViewer.setInput(filtersList);

		// Add the buttons
		createButtons(composite);
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 7;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column;// = new TableColumn(table, SWT.CENTER, 0);		
		//column.setText("!");
		//column.setWidth(20);
		
		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(columnNames[0]);
		column.setWidth(100);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				//tableViewer.setSorter(new ImportBuilderFilterSorter(ImportBuilderFilterSorter.OWNER));
			}
		});
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(columnNames[1]);
		column.setWidth(400);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				//tableViewer.setSorter(new ImportBuilderFilterSorter(ImportBuilderFilterSorter.DESCRIPTION));
			}
		});

	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 2 : Description (Free text)
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[1] = textEditor;

		// Column 3 : Owner (Combo Box) 
		editors[0] = new ComboBoxCellEditor(table, filtersList.getHeadingsList(), SWT.READ_ONLY);

		// Column 4 : Percent complete (Text with digits only)
		textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).addVerifyListener(
		
			new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					// Here, we could use a RegExp such as the following 
					// if using JRE1.4 such as  e.doit = e.text.matches("[\\-0-9]*");
					e.doit = "0123456789".indexOf(e.text) >= 0 ;
				}
			});

		
		// Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new ImportBuilderCellModifier(this));
		// Set the default sorter for the viewer 
		//tableViewer.setSorter(new ImportBuilderFilterSorter(ImportBuilderFilterSorter.DESCRIPTION));
	}

	/*
	 * Close the window and dispose of resources
	 */
	public void close() {
		Shell shell = table.getShell();

		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}


	/**
	 * InnerClass that acts as a proxy for the ImportBuilderFilterList 
	 * providing content for the Table. It implements the IImportBuilderFilterListViewer 
	 * interface since it must register changeListeners with the 
	 * ImportBuilderFilterList 
	 */
	class ExampleContentProvider implements IStructuredContentProvider, IImportBuilderFilterListViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((ImportBuilderFilterList) newInput).addChangeListener(this);
			if (oldInput != null)
				((ImportBuilderFilterList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			filtersList.removeChangeListener(this);
		}

		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return filtersList.getFilters().toArray();
		}

		/* (non-Javadoc)
		 * @see IImportBuilderFilterListViewer#addTask(ImportBuilderFilter)
		 */
		public void addFilter(ImportBuilderFilter task) {
			tableViewer.add(task);
		}

		/* (non-Javadoc)
		 * @see IImportBuilderFilterListViewer#removeTask(ImportBuilderFilter)
		 */
		public void removeFilter(ImportBuilderFilter task) {
			tableViewer.remove(task);			
		}

		/* (non-Javadoc)
		 * @see IImportBuilderFilterListViewer#updateTask(ImportBuilderFilter)
		 */
		public void updateFilter(ImportBuilderFilter task) {
			tableViewer.update(task, null);	
		}
	}
	
	/**
	 * Return the array of choices for a multiple choice cell
	 */
	public String[] getChoices(String property) {
		if (FILTER_HEADING_COLUMN.equals(property))
			return filtersList.getHeadingsList();  // The ImportBuilderFilterList knows about the choice of owners
		else
			return new String[]{};
	}

	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
	    
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText(Messages.Import_Builder_Add_Button_Name);
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
       	
       		// Add a task to the ImportBuilderFilterList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				filtersList.addFilter();
			}
		});

		//	Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText(Messages.Import_Builder_Delete_Button_Name);
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		delete.setLayoutData(gridData); 

		delete.addSelectionListener(new SelectionAdapter() {
       	
			//	Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				ImportBuilderFilter task = (ImportBuilderFilter) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				if (task != null) {
					filtersList.removeFilter(task);
				} 				
			}
		});
		
		
		
		Label label = new Label(parent, SWT.LEFT);
		label.setText(Messages.Import_Builder_Filter_Filename_Field);
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80; 
		label.setLayoutData(gridData);
		
		final Text filenameTextBox = new Text(parent, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		filenameTextBox.setText(DEFAULT_SCRIPT_NAME);
		filenameTextBox.setLayoutData(gridData);
		
		//	Create and configure the "Close" button
		runButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		runButton.setText(Messages.Import_Builder_Run_Button_Name);
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		
		runButton.setLayoutData(gridData); 	
		runButton.addSelectionListener(new SelectionAdapter() {
	       	
			//	Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
			    //Lagutko, 21.10.2009, creating a ImportBuilder script
			    //get a current editor
			    SplashResourceEditor editor = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<SplashResourceEditor>(){
			        
			        private SplashResourceEditor result;
                    
                    @Override
                    public void run() {
                        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                        if (part instanceof SplashResourceEditor) { 
                            result = (SplashResourceEditor)part;
                        }
                    }
                    
                    @Override
                    public SplashResourceEditor getValue() {
                        return result;
                    }
                });
			    
			    if (editor == null) {
			        return;
			    }
			    //get a name of RubyProject
			    SplashTableModel model = (SplashTableModel)editor.getTable().getModel();
			    String rubyProjectName = model.getRubyProjectNode().getName();
			    
			    //compute IRubyProject
			    IProject rubyProjectResource = ResourcesPlugin.getWorkspace().getRoot().getProject(rubyProjectName);
			    IRubyProject rubyProject = RubyModelManager.getRubyModelManager().getRubyModel().findRubyProject(rubyProjectResource);
			    
			    //get a source folder
			    ISourceFolder folder = RubyModelUtil.getSourceFolder(rubyProject);
			    
			    //get a content of script
			    String filter_code = filtersList.getFilterRubyCode();
			    
			    //create a operation
			    final CreateRubyScriptOperation operation = new CreateRubyScriptOperation(folder, filenameTextBox.getText(), filter_code, true);
			    
			    //run operation in additional Job
			    Job createScriptJob = new Job(Messages.Import_Builder_Create_Script_Job_Name){
                    
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        try {
                            operation.run(monitor);
                        } catch (CoreException e) {                            
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                        return Status.OK_STATUS;
                    }
                };
                createScriptJob.schedule();
                
			}
		});
		
	}

	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public List<String> getColumnNames() {
		return (List<String>)Arrays.asList(columnNames);
	}

	/**
	 * @return currently selected item
	 */
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	/**
	 * Return the ImportBuilderFilterList
	 */
	public ImportBuilderFilterList getFiltersList() {
		return filtersList;	
	}

	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

	/**
	 * Return the 'close' Button
	 */
	public Button getCloseButton() {
		return runButton;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
}
