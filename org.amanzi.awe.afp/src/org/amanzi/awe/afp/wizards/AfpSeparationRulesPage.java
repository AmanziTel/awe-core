package org.amanzi.awe.afp.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;

public class AfpSeparationRulesPage extends AfpWizardPage {
	
	private Group sectorDomainsGroup;
	private Group siteDomainsGroup;
	protected static HashMap<String, Label[]> sectorDomainLabels;
	protected static HashMap<String, Label[]> siteDomainLabels;
	private AfpModel model;
	private final String[] headers = { "BSC", "Site", "Sector", "Layer"};
	private final String[] prop_name = { "BSC", "Site", "name", "Layer"};
	private Table filterTableSector;
	private Table filterTableSite;

	public AfpSeparationRulesPage(String pageName, AfpModel model, String desc) {
		super(pageName);
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(desc);
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
    	
    	AfpWizardUtils.createButtonsGroup(this, sectorDomainsGroup, "Sector SeparationRules", model);
    	siteDomainLabels = new HashMap<String, Label[]>();
		
		
    	filterTableSector = addTRXFilterGroup(model, sectorMain, headers,10);

		
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
    	
    	AfpWizardUtils.createButtonsGroup(this, siteDomainsGroup, "Site SeparationRules", model);
    	sectorDomainLabels = new HashMap<String, Label[]>();
    	
    	filterTableSite = addTRXFilterGroup(model,siteMain, headers,10);
		
    	item2.setControl(siteMain);
		
    	setPageComplete (true);
		setControl(thisParent);

	}
	
	
	public void deleteSectorDomainLabels(String domainName){
		if (sectorDomainLabels.containsKey(domainName)){
			Label[] labels = sectorDomainLabels.get(domainName);
			sectorDomainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		sectorDomainsGroup.layout();
	}
	
	public void deleteSiteDomainLabels(String domainName){
		if (siteDomainLabels.containsKey(domainName)){
			Label[] labels = siteDomainLabels.get(domainName);
			siteDomainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		siteDomainsGroup.layout();
	}
	
	@Override
	public void refreshPage(){
		Set<String> keys = sectorDomainLabels.keySet();
		if(keys != null) {
			Iterator<String> ki = keys.iterator();
			
			while(ki.hasNext()) {
				String key = (String)ki.next();
				this.deleteSectorDomainLabels(key);
				sectorDomainLabels.remove(key);
			}
			//domainLabels.clear();
		}
		keys = siteDomainLabels.keySet();
		if(keys != null) {
			Iterator<String> ki = keys.iterator();
			
			while(ki.hasNext()) {
				String key = (String)ki.next();
				this.deleteSiteDomainLabels(key);
				siteDomainLabels.remove(key);
			}
			//domainLabels.clear();
		}
		
		Vector<AfpSeparationDomainModel> domains = model.getSectorSeparationDomains();
		for(int i=0;i< domains.size();i++) {
			AfpSeparationDomainModel sectorDomainModel = domains.elementAt(i);
			Label defaultSectorDomainLabel = new Label(sectorDomainsGroup, SWT.LEFT);
			defaultSectorDomainLabel.setText(sectorDomainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultSectorsLabel = new Label(sectorDomainsGroup, SWT.RIGHT);
			defaultSectorsLabel.setText("0");
			sectorDomainLabels.put(sectorDomainModel.getName(), new Label[]{defaultSectorDomainLabel, defaultSectorsLabel});
		}
	
		sectorDomainsGroup.layout();

		Vector<AfpSeparationDomainModel> domains2 = model.getSectorSeparationDomains();
		for(int i=0;i< domains2.size();i++) {
			AfpSeparationDomainModel sectorDomainModel = domains2.elementAt(i);
			Label defaultSiteDomainLabel = new Label(siteDomainsGroup, SWT.LEFT);
			defaultSiteDomainLabel.setText(sectorDomainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultSitesLabel = new Label(siteDomainsGroup, SWT.RIGHT);
			defaultSitesLabel.setText("0");
			siteDomainLabels.put(sectorDomainModel.getName(), new Label[]{defaultSiteDomainLabel, defaultSitesLabel});
		}
		//loadData();
		siteDomainsGroup.layout();
	}
	public void loadData(Table table) {
		if(table != null) {
			table.removeAll();
			
		    Traverser traverser = model.getTRXList(null);
		    
		    int cnt =0;
		    for (Node node : traverser) {
		    	if(cnt > 100) 
		    		break;
		    	TableItem item = new TableItem(table, SWT.NONE);
		    	for (int j = 0; j < headers.length; j++){
		    		try {
		    			String val = (String)node.getProperty(prop_name[j]);
		    			item.setText(j, val);
		    		} catch(Exception e) {
		    			item.setText(j, "NA");
		    		}
		    	}
		    	cnt++;
		    }
		    for (int i = 0; i < headers.length; i++) {
		    	table.getColumn(i).pack();
		    }
		}
	}

}
