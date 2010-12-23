package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpSummaryPage extends WizardPage {
	
	private final GraphDatabaseService service;
	private Text summaryText;
	private AfpModel model;
	
	public AfpSummaryPage(String pageName, GraphDatabaseService servise, AfpModel model) {
		super(pageName);
        this.service = servise;
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page7Name);
        setPageComplete (false);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite parentLocal = new Composite(parent, SWT.NONE);
   	 	parentLocal.setLayout(new GridLayout(2, false));
//		parent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(parentLocal, 7);
		
		Group main = new Group(parentLocal, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1 ,1));
		
		summaryText = new Text (main, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 1 ,1);
		gridData.heightHint = 300;
		gridData.minimumWidth = 100;
//		gridData.widthHint = 300;
		summaryText.setLayoutData(gridData);
		summaryText.setText("Summary Report Content");
		
		Button saveButton = new Button(main, SWT.PUSH);
		saveButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		
		saveButton.setText("Save As");
		saveButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO do something here
			}
		});
		
		setPageComplete(true);
    	setControl (parentLocal);
		

	}
	
	protected boolean isValidPage() {
	      //TODO set this flag to true here only for testing purpose. Should be only done in summary page otherwise
	      AfpImportWizard.isDone = true;
	      return true;
	  }
	public void refreshPage(){
		summaryText.setText(model.toString());
		isValidPage();
	}

}
