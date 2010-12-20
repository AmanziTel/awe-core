package org.amanzi.awe.afp.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpSeparationRulesPage extends WizardPage {
	
	private final GraphDatabaseService service;

	public AfpSeparationRulesPage(String pageName, GraphDatabaseService servise) {
		super(pageName);
        this.service = servise;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page5Name);
        setPageComplete (false);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 5);
		
		TabFolder tabFolder =new TabFolder(thisParent, SWT.NONE | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    	TabItem item1 =new TabItem(tabFolder,SWT.NONE);
		item1.setText("Sector");
		
		Group sectorMain = new Group(tabFolder, SWT.NONE);
		sectorMain.setLayout(new GridLayout(1, false));
		sectorMain.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,2));
		
		Group sectorDomainsGroup = new Group(sectorMain, SWT.NONE);
		sectorDomainsGroup.setLayout(new GridLayout(3, true));
		sectorDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1 ,3));
		
		Label sectorDomainsLabel = new Label(sectorDomainsGroup, SWT.LEFT);
		sectorDomainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		sectorDomainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(sectorDomainsLabel);
    	
    	Label sectorsLabel = new Label(sectorDomainsGroup, SWT.LEFT);
    	sectorsLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 , 1));
    	sectorsLabel.setText("Assigned Sectors");
    	AfpWizardUtils.makeFontBold(sectorsLabel);
    	
    	AfpWizardUtils.createButtonsGroup(sectorDomainsGroup, "Sector SeparationRules");
		
    	
    	new Label(sectorDomainsGroup, SWT.NONE).setText("Default Separations");
    	Label defaultSeparation = new Label(sectorDomainsGroup, SWT.RIGHT);
    	defaultSeparation.setText("Edit it");
		
    	AfpWizardUtils.getTRXFilterGroup(sectorMain);

		
		
		
		
		item1.setControl(sectorMain);
		
		TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Site");
		
		Group siteMain = new Group(tabFolder, SWT.NONE);
		siteMain.setLayout(new GridLayout(1, false));
		siteMain.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,2));
		
		Group siteDomainsGroup = new Group(siteMain, SWT.NONE);
		siteDomainsGroup.setLayout(new GridLayout(3, true));
		siteDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1 ,3));
		
		Label siteDomainsLabel = new Label(siteDomainsGroup, SWT.LEFT);
		siteDomainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		siteDomainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(siteDomainsLabel);
    	
    	Label sitesLabel = new Label(siteDomainsGroup, SWT.LEFT);
    	sitesLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	sitesLabel.setText("Assigned Sites");
    	AfpWizardUtils.makeFontBold(sitesLabel);
    	
    	AfpWizardUtils.createButtonsGroup(siteDomainsGroup, "Site SeparationRules");
		
    	
    	new Label(siteDomainsGroup, SWT.NONE).setText("Default Separations");
    	Label siteDefaultSeparation = new Label(siteDomainsGroup, SWT.RIGHT);
    	siteDefaultSeparation.setText("Edit it");
		
    	AfpWizardUtils.getTRXFilterGroup(siteMain);
		
    	item2.setControl(siteMain);
		
    	setPageComplete (true);
		setControl(thisParent);

	}

}
