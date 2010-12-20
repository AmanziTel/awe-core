package org.amanzi.awe.afp.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpSYHoppingMALsPage extends WizardPage {
	
	private final GraphDatabaseService service;
	private Label defaultTrx;
	public static String test = "test";
	
	public AfpSYHoppingMALsPage(String pageName, GraphDatabaseService servise) {
		super(pageName);
        this.service = servise;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page4Name);
        setPageComplete (false);
	}

	@Override
	public void createControl(Composite parent) {
		final  Shell parentShell = parent.getShell();
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 4);
		
		Group main = new Group(thisParent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,2));
		
		Group malDomainsGroup = new Group(main, SWT.NONE);
		malDomainsGroup.setLayout(new GridLayout(3, true));
		malDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,1 ,3));
		malDomainsGroup.setText("MAL Domains");
		
		Label domainsLabel = new Label(malDomainsGroup, SWT.LEFT);
		domainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		domainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(domainsLabel);
    	
    	Label trxsLabel = new Label(malDomainsGroup, SWT.LEFT);
    	trxsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	trxsLabel.setText("Assigned TRXs");
    	AfpWizardUtils.makeFontBold(trxsLabel);
    	
    	AfpWizardUtils.createButtonsGroup(malDomainsGroup, "HoppingMAL");
		
    	
    	new Label(malDomainsGroup, SWT.NONE).setText("Default MAL");
    	defaultTrx = new Label(malDomainsGroup, SWT.RIGHT);
    	defaultTrx.setText("Edit it");
		
    	AfpWizardUtils.getTRXFilterGroup(main);

    	setPageComplete(true);
    	setControl (thisParent);
		

	}

}
