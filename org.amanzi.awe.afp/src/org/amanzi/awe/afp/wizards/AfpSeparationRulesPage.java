package org.amanzi.awe.afp.wizards;

import java.util.HashMap;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
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
	
	public final static String defaultDomainName = "Default Separations";
	
	private final GraphDatabaseService service;
	
	private Group sectorDomainsGroup;
	private Group siteDomainsGroup;
	protected static HashMap<String, Label[]> sectorDomainLabels;
	protected static HashMap<String, Label[]> siteDomainLabels;
	private AfpModel model;

	public AfpSeparationRulesPage(String pageName, GraphDatabaseService servise, AfpModel model) {
		super(pageName);
        this.service = servise;
        this.model = model;
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
		
		sectorDomainsGroup = new Group(sectorMain, SWT.NONE);
		sectorDomainsGroup.setLayout(new GridLayout(3, true));
		sectorDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,1 ,10));
		
		Label sectorDomainsLabel = new Label(sectorDomainsGroup, SWT.LEFT);
		sectorDomainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		sectorDomainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(sectorDomainsLabel);
    	
    	Label sectorsLabel = new Label(sectorDomainsGroup, SWT.LEFT);
    	sectorsLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 , 1));
    	sectorsLabel.setText("Assigned Sectors");
    	AfpWizardUtils.makeFontBold(sectorsLabel);
    	
    	AfpWizardUtils.createButtonsGroup(sectorDomainsGroup, "Sector SeparationRules", model);
    	siteDomainLabels = new HashMap<String, Label[]>();
		
    	
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
		
		siteDomainsGroup = new Group(siteMain, SWT.NONE);
		siteDomainsGroup.setLayout(new GridLayout(3, true));
		siteDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,1 ,10));
		
		Label siteDomainsLabel = new Label(siteDomainsGroup, SWT.LEFT);
		siteDomainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		siteDomainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(siteDomainsLabel);
    	
    	Label sitesLabel = new Label(siteDomainsGroup, SWT.LEFT);
    	sitesLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	sitesLabel.setText("Assigned Sites");
    	AfpWizardUtils.makeFontBold(sitesLabel);
    	
    	AfpWizardUtils.createButtonsGroup(siteDomainsGroup, "Site SeparationRules", model);
    	sectorDomainLabels = new HashMap<String, Label[]>();
    	
    	new Label(siteDomainsGroup, SWT.NONE).setText("Default Separations");
    	Label siteDefaultSeparation = new Label(siteDomainsGroup, SWT.RIGHT);
    	siteDefaultSeparation.setText("Edit it");
		
    	AfpWizardUtils.getTRXFilterGroup(siteMain);
		
    	item2.setControl(siteMain);
		
    	setPageComplete (true);
		setControl(thisParent);

	}
	
	
	public static void deleteSectorDomainLabels(String domainName){
		if (sectorDomainLabels.containsKey(domainName)){
			Label[] labels = sectorDomainLabels.get(domainName);
			sectorDomainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		sectorDomainsGroup.layout();
	}
	
	public static void deleteSiteDomainLabels(String domainName){
		if (siteDomainLabels.containsKey(domainName)){
			Label[] labels = siteDomainLabels.get(domainName);
			siteDomainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		siteDomainsGroup.layout();
	}
	
	public void refreshPage(){
		
		AfpSeparationDomainModel sectorDomainModel = new AfpSeparationDomainModel();
		sectorDomainModel.setName(defaultDomainName);
		sectorDomainModel.setSeparations(new String[]{"todo"});
		model.addSectorSeparationDomain(sectorDomainModel);
		Label defaultSectorDomainLabel = new Label(sectorDomainsGroup, SWT.LEFT);
		defaultSectorDomainLabel.setText(defaultDomainName);
		//TODO: update the TRXs by default here
		Label defaultSectorsLabel = new Label(sectorDomainsGroup, SWT.RIGHT);
		defaultSectorsLabel.setText("todo");
		sectorDomainLabels.put(defaultDomainName, new Label[]{defaultSectorDomainLabel, defaultSectorsLabel});
	
		sectorDomainsGroup.layout();
		
		AfpSeparationDomainModel siteDomainModel = new AfpSeparationDomainModel();
		siteDomainModel.setName(defaultDomainName);
		siteDomainModel.setSeparations(new String[]{"todo"});
		model.addSiteSeparationDomain(siteDomainModel);
		Label defaultSiteDomainLabel = new Label(siteDomainsGroup, SWT.LEFT);
		defaultSiteDomainLabel.setText(defaultDomainName);
		//TODO: update the TRXs by default here
		Label defaultSitesLabel = new Label(siteDomainsGroup, SWT.RIGHT);
		defaultSectorsLabel.setText("todo");
		sectorDomainLabels.put(defaultDomainName, new Label[]{defaultSiteDomainLabel, defaultSitesLabel});
	
		siteDomainsGroup.layout();
	}

}
