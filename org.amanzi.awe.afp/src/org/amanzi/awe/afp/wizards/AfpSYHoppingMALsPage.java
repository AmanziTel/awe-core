package org.amanzi.awe.afp.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.filters.AfpColumnFilter;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.models.AfpDomainModel;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class AfpSYHoppingMALsPage extends AfpWizardPage  implements FilterListener{
	
	private Group malDomainsGroup;
	private Label defaultTrx;
	public String test = "test";
	
	protected HashMap<String, Label[]> domainLabels;
	private final String[] headers = { "BSC", "Site", "Sector", "Layer", "Subcell", "TRX_ID", "Band", "Extended", "Hopping Type"};
	private final HashMap<String,Integer> headersNodeType = new HashMap<String,Integer>(); 
	private final HashMap<String,String> headers_prop = new HashMap<String,String>();
	private Table filterTable;
	private int trxCount;
	protected AfpRowFilter rowFilter;
	protected  Shell parentShell;

	
	public AfpSYHoppingMALsPage(String pageName, AfpModel model, String desc) {
		super(pageName, model);
		
		headers_prop.put("BSC", "bsc");
		headers_prop.put("Site", INeoConstants.PROPERTY_NAME_NAME);
		headers_prop.put("Sector", INeoConstants.PROPERTY_NAME_NAME);
		headers_prop.put("Layer", "Layer");
		headers_prop.put("Subcell", "Subcell");
		headers_prop.put("TRX_ID", "trx_id");
		headers_prop.put("Band", "band");
		headers_prop.put("Extended", "Extended");
		headers_prop.put("Hopping Type", "hopping_type");
//		headers_prop.put("BCCH", INeoConstants.PROPERTY_BCCH_NAME);

		headersNodeType.put("BSC", 0);
		headersNodeType.put("Site", 0);
		headersNodeType.put("Sector", 1);
		headersNodeType.put("Layer", 2);
		headersNodeType.put("Subcell", 1);
		headersNodeType.put("TRX_ID", 2);
		headersNodeType.put("Band", 2);
		headersNodeType.put("Extended", 2);
		headersNodeType.put("Hopping Type", 2);
//		headersNodeType.put("BCCH", 2);
		
		
        setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete (false);
        rowFilter = new AfpRowFilter();	
	}

	@Override
	public void createControl(Composite parent) {
		parentShell = parent.getShell();
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
    	
    	filterTable = addTRXFilterGroup(main,headers,10, false, this);

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
//		super.refreshPage();
		updateLabels();
		loadData();
		
		malDomainsGroup.layout();
	}
	
	public void updateLabels(){
		for(Label[] labels: domainLabels.values() ){
			for (Label label : labels){
				label.dispose();
			}
		}
		domainLabels.clear();
		
		for(AfpHoppingMALDomainModel domainModel :model.getMalDomains()) {
			Label defaultDomainLabel = new Label(malDomainsGroup, SWT.LEFT);
			defaultDomainLabel.setText(domainModel.getName());
			Label defaultTRXsLabel = new Label(malDomainsGroup, SWT.RIGHT);
			defaultTRXsLabel.setText("" + domainModel.getNumTRX());
			domainLabels.put(domainModel.getName(), new Label[]{defaultDomainLabel, defaultTRXsLabel});
		}
		
		malDomainsGroup.layout();
	}
	
	public void loadData() {
		if(filterTable != null) {
			filterTable.removeAll();
			
			this.clearAllUniqueValuesForProperty();
			
			HashMap<String, String> bandFilters = new HashMap<String, String> ();
			for (int i = 0; i < model.getFrequencyBands().length; i++){
				if (model.getFrequencyBands()[i])
					if (bandFilters.get("band") == null)
						bandFilters.put("band", model.BAND_NAMES[i]);
					else
						bandFilters.put("band", bandFilters.get("band") + "," + model.BAND_NAMES[i]);
			}
			
		    Traverser sectorTraverser = model.getTRXList(bandFilters);
		    
		    trxCount =0;
		    for (Node node : sectorTraverser) {
		    	Traverser trxTraverser = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

					@Override
					public boolean isReturnableNode(TraversalPosition currentPos) {
						if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.TRX.getId())){
							return true;
						}
							
						return false;
					}
		    		
		    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		    	
		    	Node siteNode = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
		    	this.addSectorUniqueProperties(node);
				this.addSiteUniqueProperties(siteNode);
				boolean includeFlag = true;
		    	
		    	for (Node trxNode: trxTraverser){
		    		
		    		for(AfpHoppingMALDomainModel mod: model.getMalDomains()){
			    		String filterString = mod.getFilters();
			    		if (filterString != null && !filterString.trim().isEmpty()){
				    		AfpRowFilter rf = AfpRowFilter.getFilter(mod.getFilters());
				    		if (rf != null){
					    		if (rf.equal(trxNode)){
					    			includeFlag = false;
					    			break;
					    		}
				    		}
			    		}
		    		}
		    		
		    		if (!includeFlag)
			    		continue;
		    		
//			    	if ((Integer)trxNode.getProperty(INeoConstants.PROPERTY_HOPPING_TYPE_NAME, 0) < 1)
//			    		continue;
			    	
			    	if (rowFilter != null){
			    		if (!rowFilter.equal(trxNode)) 
			    			continue;
			    	}
			    	this.addTrxUniqueProperties(trxNode);
			    	
		    	
			    	TableItem item = new TableItem(filterTable, SWT.NONE);
			    	int j=0;
			    	if(trxCount <= 100){ 
				    	for (String prop_name : headers){
				    		Object val = null;
				    		try {
				    			Integer type = this.headersNodeType.get(prop_name);
				    			if(type ==0) {
				    				if (siteNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals("site"))
				    					val = (String)siteNode.getProperty(headers_prop.get(prop_name), "");
	
				    			} else if( type == 1) {
				    				val = node.getProperty(headers_prop.get(prop_name), "");
				    			} else {
				    				val = trxNode.getProperty(headers_prop.get(prop_name), "");
				    			}
	
				    			if(val == null) val ="";
				    			
				    			item.setText(j, val.toString());
				    		} catch(Exception e) {
				    			item.setText(j, "");
				    		}
				    		j++;
				    	}
			    	}
			    	trxCount++;
		    	}
		    }
//		    for(;trxCount <10;trxCount++) {
//		    	TableItem item = new TableItem(filterTable, SWT.NONE);
//		    	for (int j = 0; j < headers.length; j++){
//		    		String val = "";
//	    			item.setText(j, val);
//		    	}		    	
//		    }
		    for (int i = 0; i < headers.length; i++) {
		    	filterTable.getColumn(i).pack();
		    }
		    
		    this.updateTRXFilterLabel(trxCount, model.getTotalRemainingMalTRX());
		}
	}

	@Override
	public void onFilterSelected(String columnName, ArrayList<String> selectedValues) {
		String val = headers_prop.get(columnName);
		
		if(val != null ) {
			AfpColumnFilter colFilter = new AfpColumnFilter(val);
			for (String value: selectedValues){
				colFilter.addValue(value);
			}
			rowFilter.addColumn(colFilter);
			loadData();
		}	
	}
	
	@Override
	public Object[] getColumnUniqueValues(String colName){
		
		Integer type  = headersNodeType.get(colName);
		if(type.intValue() ==0) {
			return this.getSiteUniqueValuesForProperty(headers_prop.get(colName));
		} else if(type.intValue() ==1) {
			return this.getSectorUniqueValuesForProperty(headers_prop.get(colName));
		} else {
			return this.getTrxUniqueValuesForProperty(headers_prop.get(colName));
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget.getData().equals(AfpWizardPage.ASSIGN)){
			final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL|SWT.TITLE);
			subShell.setText("Assign to Domain");
			subShell.setLayout(new GridLayout(2, false));
			subShell.setLocation(200, 200);
			
			Label infoLabel = new Label (subShell, SWT.LEFT);
			//TODO update label to show correct no. of TRXs
			infoLabel.setText("Selected " + trxCount + " TRXs will be assigned to:");
			infoLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
			
			Label domainLabel = new Label (subShell, SWT.LEFT);
			domainLabel.setText("Select Domain");
			domainLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
			
			final Combo domainCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			ArrayList<String> modelNames = new ArrayList<String>();
			for (AfpHoppingMALDomainModel dm : model.getMalDomains()){
				modelNames.add(dm.getName());
			}
			domainCombo.setItems(modelNames.toArray(new String[0]));
			domainCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			
			Button selectButton = new Button(subShell, SWT.PUSH);
			selectButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
			selectButton.setText("Assign");
			selectButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					String domainName = domainCombo.getText();
					AfpDomainModel malModel = model.findDomainByName(model.DOMAIN_TYPES[1], domainName);
					malModel.setFilters(rowFilter.toString());
					malModel.setNumTRX(trxCount);
					model.setTotalRemainingMalTRX(model.getTotalRemainingMalTRX() - trxCount);
					rowFilter.clear();
					loadData();
					updateLabels();
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
		
		else if (e.widget.getData().equals(AfpWizardPage.CLEAR)){
			rowFilter.clear();
			loadData();
		}
			
	}

}
