package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpProgressPage extends WizardPage {
	
	private AfpModel model;
	
	private Button[] colorButtons = new Button[8];
	private String[] graphParams = new String[]{
		"Total",
		"Sector Separations",
		"Interference",
		"Site Separations",
		"Neighbour",
		"Frequency Constraints",
		"Triangulation",
		"Shadowing"
	};
	private Button[] paramButtons = new Button[8];
	
	public AfpProgressPage(String pageName, AfpModel model) {
		super(pageName);
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page8Name);
        setPageComplete (false);
	}

	
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
   	 	main.setLayout(new GridLayout(2, false));
   	 	main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
   	 	
   	 	Group summaryGroup = new Group(main, SWT.NONE);
   	 	summaryGroup.setLayout(new GridLayout(2, false));
   	 	summaryGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
   	 	summaryGroup.setText("Summary");
   	 	
   	 	new Label(summaryGroup, SWT.LEFT).setText("Selected Sectors:");
   	 	new Label(summaryGroup, SWT.LEFT).setText("todo");
	   	new Label(summaryGroup, SWT.LEFT).setText("Selected TRXs:");
	   	new Label(summaryGroup, SWT.LEFT).setText("todo");
	   	new Label(summaryGroup, SWT.LEFT).setText("BCCH TRXs:");
	   	new Label(summaryGroup, SWT.LEFT).setText("todo");
	   	new Label(summaryGroup, SWT.LEFT).setText("TCH Non/BB Hopping TRXs");
	   	new Label(summaryGroup, SWT.LEFT).setText("todo");
	   	new Label(summaryGroup, SWT.LEFT).setText("TCH SY Hopping TRXs");
	   	new Label(summaryGroup, SWT.LEFT).setText("todo");
   	 	
   	 	
   	 	
	   	TabFolder tabFolder =new TabFolder(main, SWT.NONE | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		
		TabItem item1 =new TabItem(tabFolder,SWT.NONE);
		item1.setText("Graph");
		
		Group graphGroup = new Group(tabFolder, SWT.NONE);
		graphGroup.setLayout(new GridLayout(6, false));
		graphGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		
		Text progressText = new Text (graphGroup, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 6 ,1);
		gridData.heightHint = 200;
		gridData.minimumWidth = 100;
		progressText.setLayoutData(gridData);
		progressText.setText("Graph to be displayed");
		
		
		
		for (int i = 0; i < graphParams.length; i++){
			colorButtons[i] = new Button(graphGroup, SWT.PUSH);
			paramButtons[i] = new Button(graphGroup, SWT.CHECK);
			paramButtons[i].setSelection(true);
			paramButtons[i].setText(graphParams[i]);
		}
		colorButtons[0].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
		paramButtons[0].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
		colorButtons[5].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true, 1, 2));
		paramButtons[5].setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true, 1, 2));
		
		item1.setControl(graphGroup);
		
		TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Table");
		
		Group tableGroup = new Group(tabFolder, SWT.NONE);
		tableGroup.setLayout(new GridLayout(6, false));
		tableGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		
//		Text progressText = new Text (graphGroup, SWT.BORDER | SWT.MULTI);
//		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true, 6 ,1);
//		gridData.heightHint = 200;
//		gridData.minimumWidth = 100;
//		progressText.setLayoutData(gridData);
//		progressText.setText("Summary Report Content");
		
		item2.setControl(tableGroup);
		
		
		Group controlGroup = new Group(main, SWT.NONE);
		controlGroup.setLayout(new GridLayout(1, false));
		controlGroup.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 1, 1));
		controlGroup.setText("Control");
		
		Button pauseButton = new Button(controlGroup, SWT.PUSH);
		pauseButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button resumeButton = new Button(controlGroup, SWT.PUSH);
		resumeButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		resumeButton.setText("Resume");
		resumeButton.setEnabled(false);
		resumeButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button stopButton = new Button(controlGroup, SWT.PUSH);
		stopButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, true));
		stopButton.setText("Stop");
		stopButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		setPageComplete (true);
		setControl(main);
	}

}
