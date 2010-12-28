package org.amanzi.awe.afp.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;

public class AfpSYHoppingMALsPage extends AfpWizardPage {
	
	private Group malDomainsGroup;
	private Label defaultTrx;
	public String test = "test";
	
	protected HashMap<String, Label[]> domainLabels;
	private final String[] headers = { "BSC", "Site", "Sector", "Layer", "Subcell", "TRX_ID", "Band", "Extended", "Hopping Type"};
	private final String[] prop_name = { "bsc", "Site", INeoConstants.PROPERTY_NAME_NAME, "Layer", 
			"Subcell", "TRX_ID", "band", "Extended", "Hopping Type"};
	private Table filterTable;

	
	public AfpSYHoppingMALsPage(String pageName, AfpModel model, String desc) {
		super(pageName, model);
        setTitle(AfpImportWizard.title);
        setDescription(desc);
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
		
		malDomainsGroup = new Group(main, SWT.NONE);
		malDomainsGroup.setLayout(new GridLayout(3, true));
		malDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,1 ,10));
		malDomainsGroup.setText("MAL Domains");
		
		Label domainsLabel = new Label(malDomainsGroup, SWT.LEFT);
		domainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		domainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(domainsLabel);
    	
    	Label trxsLabel = new Label(malDomainsGroup, SWT.LEFT);
    	trxsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	trxsLabel.setText("Assigned TRXs");
    	AfpWizardUtils.makeFontBold(trxsLabel);
    	
    	AfpWizardUtils.createButtonsGroup(this,malDomainsGroup, "HoppingMAL", model);
    	domainLabels = new HashMap<String, Label[]>();
    	
    	filterTable = addTRXFilterGroup(main,headers,10, false);

    	setPageComplete(true);
    	setControl (thisParent);
		

	}
	
	public void deleteDomainLabels(String domainName){
		if (domainLabels.containsKey(domainName)){
			Label[] labels = domainLabels.get(domainName);
			domainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		malDomainsGroup.layout();
	}
	
	public void refreshPage(){
		super.refreshPage();
		for(Label[] labels: domainLabels.values() ){
			for (Label label : labels){
				label.dispose();
			}
		}
		domainLabels.clear();
		
		for(AfpHoppingMALDomainModel domainModel :model.getMalDomains()) {
			Label defaultDomainLabel = new Label(malDomainsGroup, SWT.LEFT);
			defaultDomainLabel.setText(domainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultTRXsLabel = new Label(malDomainsGroup, SWT.RIGHT);
			defaultTRXsLabel.setText("0");
			domainLabels.put(domainModel.getName(), new Label[]{defaultDomainLabel, defaultTRXsLabel});
		}    	
		loadData();
		
		malDomainsGroup.layout();
	}
	
	public void loadData() {
		if(filterTable != null) {
			filterTable.removeAll();
			
		    Traverser traverser = model.getTRXList(null);
		    
		    int cnt =0;
		    for (Node node : traverser) {
		    	if (!((String)node.getProperty("hopping type", "")).equals("SY"))
		    		continue;
		    	if(cnt > 100) 
		    		break;
		    	TableItem item = new TableItem(filterTable, SWT.NONE);
		    	for (int j = 0; j < headers.length; j++){
		    		String val = "";
		    		try {
		    			if (prop_name[j].equals("bsc")){
		    				val = (String)node.getProperty(prop_name[j], "bsc");
		    			}
		    			else if (prop_name[j].equals("Site")){
		    				Node siteNode = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
		    				if (siteNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals("site"))
		    					val = (String)siteNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "");
		    			}
		    			else if (prop_name[j].equals("TRX_ID")){
		    				val = (String)node.getProperty(prop_name[j], "0");
		    			}
		    			else if (prop_name[j].equals("band")){
		    				val = (String)node.getProperty(prop_name[j], "");
		    				if (val.equals("")) 
		    					val = (String)node.getProperty("ant_freq_band", "");
		    				val = val.split(" ")[val.split(" ").length - 1].trim();
		    			}
		    			else if (prop_name[j].equals("Extended")){
		    				val = (String)node.getProperty(prop_name[j], "NA");
		    			}
		    			else if (prop_name[j].equals("Hopping Type")){
		    				val = (String)node.getProperty(prop_name[j], "Non");
		    			}
		    			else 
		    				val = (String)node.getProperty(prop_name[j], "");
		    			item.setText(j, val);
		    		} catch(Exception e) {
		    			item.setText(j, "");
		    		}
		    	}
		    	cnt++;
		    }
		    for (int i = 0; i < headers.length; i++) {
		    	filterTable.getColumn(i).pack();
		    }
		}
	}

}
