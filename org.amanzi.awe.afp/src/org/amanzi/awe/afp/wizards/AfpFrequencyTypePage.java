package org.amanzi.awe.afp.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Traverser;

public class AfpFrequencyTypePage extends AfpWizardPage {
	
	private Group frequencyDomainsGroup;
	protected Label free900Freq;
	protected Label free900Trx;
	protected Label free1800Freq;
	protected Label free1800Trx;
	
	protected Label bcch900Freq;
	protected Label bcch900Trx;
	protected Label bcch1800Freq;
	protected Label bcch1800Trx;
	
	protected Label tch900Freq;
	protected Label tch900Trx;
	protected Label tch1800Freq;
	protected Label tch1800Trx;
	
	protected static HashMap<String, Label[]> domainLabels;
	private final String[] headers = { "BSC", "Site", "Sector", "Layer", "Subcell", "TRX_ID", "Band", "Extended", "Hopping Type", "BCCH"};
	private final String[] prop_name = { "bsc", "Site", INeoConstants.PROPERTY_NAME_NAME, "Layer", 
			"Subcell", "TRX_ID", "band", "Extended", "Hopping Type", INeoConstants.PROPERTY_BCCH_NAME};
	
	private Table filterTable;

	public AfpFrequencyTypePage(String pageName, AfpModel model, String desc) {
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
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 3);
		
		Group main = new Group(thisParent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,2));
		
		frequencyDomainsGroup = new Group(main, SWT.NONE);
		frequencyDomainsGroup.setLayout(new GridLayout(4, true));
		frequencyDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,1 ,10));
		frequencyDomainsGroup.setText("Frequency Type Domains");
		
		Label domainsLabel = new Label(frequencyDomainsGroup, SWT.LEFT);
		domainsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
		domainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(domainsLabel);
    	
    	Label frequenciesLabel = new Label(frequencyDomainsGroup, SWT.LEFT);
    	frequenciesLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	frequenciesLabel.setText("Assigned Frequencies");
    	AfpWizardUtils.makeFontBold(frequenciesLabel);
    	
    	Label trxsLabel = new Label(frequencyDomainsGroup, SWT.LEFT);
    	trxsLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	trxsLabel.setText("Assigned TRXs");
    	AfpWizardUtils.makeFontBold(trxsLabel);
    	
    	AfpWizardUtils.createButtonsGroup(this, frequencyDomainsGroup, "FrequencyType", model);
    	domainLabels = new HashMap<String, Label[]>();
    	
    	filterTable = this.addTRXFilterGroup(main, headers,10, false);
    	

		
    	setPageComplete(true);
    	setControl (thisParent);    	
	}
	
	public void loadData() {
		if(filterTable != null) {
			filterTable.removeAll();
			
			HashMap<String, String> bandFilters = new HashMap<String, String> ();
			for (int i = 0; i < model.getFrequencyBands().length; i++){
				if (model.getFrequencyBands()[i])
					bandFilters.put("band", model.BAND_NAMES[i]);
			}
			
		    Traverser traverser = model.getTRXList(bandFilters);
		    
		    int cnt =0;
		    for (Node node : traverser) {
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
		    			else if (prop_name[j].equals(INeoConstants.PROPERTY_BCCH_NAME)){
		    				val = "1";
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
	public static void deleteDomainLabels(String domainName){
		if (domainLabels.containsKey(domainName)){
			Label[] labels = domainLabels.get(domainName);
			domainLabels.remove(domainName);
			for (Label label : labels){
				label.dispose();
			}
		}
//		frequencyDomainsGroup.layout();
	}
	
	public void refreshPage(){
		super.refreshPage();
		for(Label[] labels:domainLabels.values()){
				for (Label label : labels){
					label.dispose();
				}
		}
		domainLabels.clear();
		
		for(AfpFrequencyDomainModel domainModel: model.getFreqDomains(true)) {
			if(domainModel != null) {
				Label freePlanLabel = new Label(frequencyDomainsGroup, SWT.LEFT);
				freePlanLabel.setText(domainModel.getName());
				Label frequenciesFreePlanLabel = new Label(frequencyDomainsGroup, SWT.RIGHT);
				//TODO: update the num frequencies and TRXs by default here
				frequenciesFreePlanLabel.setText("" + domainModel.getCount());
				Label TRXsFreePlanLabel = new Label(frequencyDomainsGroup, SWT.RIGHT);
				TRXsFreePlanLabel.setText("0");
				domainLabels.put(domainModel.getName(), new Label[]{freePlanLabel, frequenciesFreePlanLabel, TRXsFreePlanLabel});
			}
    	}
		
		
		loadData();
		frequencyDomainsGroup.layout();
	}

}
