package org.amanzi.awe.afp.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.filters.AfpColumnFilter;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpModelUtils;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class AfpSeparationRulesPage extends AfpWizardPage  implements FilterListener{
	
	private Group sectorDomainsGroup;
	private Group siteDomainsGroup;
	protected HashMap<String, Label[]> sectorDomainLabels;
	protected HashMap<String, Label[]> siteDomainLabels;
	private final String[] headers = { "BSC", "Site", "Sector", "Layer"};
	private final HashMap<String,String> headersNodeType = new HashMap<String,String>(); 
	private final HashMap<String,String> headers_prop = new HashMap<String,String>();

	private Table filterTableSector;
	private Table filterTableSite;
	protected AfpRowFilter siteRowFilter;
	protected AfpRowFilter sectorRowFilter;
	
	TabFolder tabFolder;
	
    HashMap<String,Set<Object>> uniqueSitePropertyValuesSite = new HashMap<String,Set<Object>>();
    HashMap<String,Set<Object>> uniqueSectorPropertyValuesSite = new HashMap<String,Set<Object>>();
    HashMap<String,Set<Object>> uniqueTrxPropertyValuesSite = new HashMap<String,Set<Object>>();
    HashMap<String,Set<Object>> uniqueSitePropertyValuesSector = new HashMap<String,Set<Object>>();
    HashMap<String,Set<Object>> uniqueSectorPropertyValuesSector = new HashMap<String,Set<Object>>();
    HashMap<String,Set<Object>> uniqueTrxPropertyValuesSector = new HashMap<String,Set<Object>>();


	public AfpSeparationRulesPage(String pageName, AfpModel model, String desc) {
		super(pageName, model);

		headers_prop.put("BSC", "bsc");
		headers_prop.put("Site", INeoConstants.PROPERTY_NAME_NAME);
		headers_prop.put("Sector", INeoConstants.PROPERTY_NAME_NAME);
		headers_prop.put("Layer", "Layer");

		headersNodeType.put("BSC", NodeTypes.SITE.getId());
		headersNodeType.put("Site", NodeTypes.SITE.getId());
		headersNodeType.put("Sector", NodeTypes.SECTOR.getId());
		headersNodeType.put("Layer", NodeTypes.TRX.getId());
		
	    uniqueSitePropertyValuesSector = this.uniqueSitePropertyValues;
	    uniqueSectorPropertyValuesSector = this.uniqueSectorPropertyValues;
	    uniqueTrxPropertyValuesSector = this.uniqueTrxPropertyValues;

	    for(String p: AfpModel.sitePropertiesName) {
	    	uniqueSitePropertyValuesSite.put(p, new HashSet<Object>());
		}
		for(String p: AfpModel.sectorPropertiesName) {
			uniqueSectorPropertyValuesSector.put(p, new HashSet<Object>());
		}
		
		for(String p: AfpModel.trxPropertiesName) {
			uniqueTrxPropertyValuesSector.put(p, new HashSet<Object>());
		}

		setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete (false);
        siteRowFilter = new AfpRowFilter();
        sectorRowFilter = new AfpRowFilter();
        
        
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 5);

		Group main = new Group(thisParent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,1));

		tabFolder =new TabFolder(main, SWT.NONE);// | SWT.BORDER);
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
		
		
    	filterTableSector = addTRXFilterGroup(sectorMain, headers,10, false, this);
		
		item1.setControl(sectorMain);
		
		TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Site");
		
		Group siteMain = new Group(tabFolder, SWT.NONE);
		siteMain.setLayout(new GridLayout(1, false));
		siteMain.setLayoutData(new GridData(GridData.FILL, SWT.FILL, true, true, 1 ,2));
		
		siteDomainsGroup = new Group(siteMain, SWT.NONE);
		siteDomainsGroup.setLayout(new GridLayout(3, true));
		siteDomainsGroup.setLayoutData(new GridData(GridData.FILL, SWT.FILL, true, true,1 ,10));
		
		Label siteDomainsLabel = new Label(siteDomainsGroup, SWT.LEFT);
		siteDomainsLabel.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false, 1 , 1));
		siteDomainsLabel.setText("Domains");
    	AfpWizardUtils.makeFontBold(siteDomainsLabel);
    	
    	Label sitesLabel = new Label(siteDomainsGroup, SWT.LEFT);
    	sitesLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 , 1));
    	sitesLabel.setText("Assigned Sites");
    	AfpWizardUtils.makeFontBold(sitesLabel);
    	
    	AfpWizardUtils.createButtonsGroup(this, siteDomainsGroup, "Site SeparationRules", model);
    	sectorDomainLabels = new HashMap<String, Label[]>();
    	
    	filterTableSite = addTRXFilterGroup(siteMain, headers,10, true, this);
		
    	item2.setControl(siteMain);
		
    	
    	this.tabFolder.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
    			// interchange the unique value cache
    			if(tabFolder.getSelectionIndex() == 0) {
    				interChangeUniquePropertySet(true);
    			} else {
    				interChangeUniquePropertySet(false);
    			}
    			
    		}
    	});
    	
    	
    	setPageComplete (true);
		setControl(thisParent);

	}
	
	private void interChangeUniquePropertySet(boolean sector) {
		if(sector) {
		    uniqueSitePropertyValues = uniqueSitePropertyValuesSector;
		    uniqueSectorPropertyValues = uniqueSectorPropertyValuesSector;
		    uniqueTrxPropertyValues = uniqueTrxPropertyValuesSector;
		} else {
		    uniqueSitePropertyValues = uniqueSitePropertyValuesSite;
		    uniqueSectorPropertyValues = uniqueSectorPropertyValuesSite;
		    uniqueTrxPropertyValues = uniqueTrxPropertyValuesSite;
		}
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
		super.refreshPage();
		for(Label[] labels : sectorDomainLabels.values()) {
			for (Label label : labels){
				label.dispose();
			}
		}
		sectorDomainLabels.clear();

		for(Label[] labels: siteDomainLabels.values()){
			for (Label label : labels){
				label.dispose();
			}
		}
		siteDomainLabels.clear();
		
		for(AfpSeparationDomainModel sectorDomainModel :model.getSectorSeparationDomains() ){
			Label defaultSectorDomainLabel = new Label(sectorDomainsGroup, SWT.LEFT);
			defaultSectorDomainLabel.setText(sectorDomainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultSectorsLabel = new Label(sectorDomainsGroup, SWT.RIGHT);
			defaultSectorsLabel.setText("0");
			sectorDomainLabels.put(sectorDomainModel.getName(), new Label[]{defaultSectorDomainLabel, defaultSectorsLabel});
		}
	
		sectorDomainsGroup.layout();

		for(AfpSeparationDomainModel sectorDomainModel : model.getSectorSeparationDomains()) {
			Label defaultSiteDomainLabel = new Label(siteDomainsGroup, SWT.LEFT);
			defaultSiteDomainLabel.setText(sectorDomainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultSitesLabel = new Label(siteDomainsGroup, SWT.RIGHT);
			defaultSitesLabel.setText("0");
			siteDomainLabels.put(sectorDomainModel.getName(), new Label[]{defaultSiteDomainLabel, defaultSitesLabel});
		}
		this.interChangeUniquePropertySet(false);
		loadData(filterTableSite, siteRowFilter);
		this.interChangeUniquePropertySet(true);
		loadData(filterTableSector, sectorRowFilter);
		if(tabFolder.getSelectionIndex() ==0) {
			this.interChangeUniquePropertySet(true);
		} else {
			this.interChangeUniquePropertySet(false);
		}
		siteDomainsGroup.layout();
	}
	public void loadData(Table table, AfpRowFilter rowFilter) {
		if(table != null) {
			table.removeAll();
			
		    Traverser sectorTraverser = model.getTRXList(null);
		    
		    this.clearAllUniqueValuesForProperty();
		    
		    int cnt =0;
		    for (Node sectorNode : sectorTraverser) {
		    	// temp fix.
		    	// find the one TRX node under
		    	Node siteNode = sectorNode.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
		    	Traverser trxTraverser = AfpModelUtils.getTrxTraverser(sectorNode);
		    	
		    	Node trxNode = null;
		    	for(Node n  :trxTraverser) {
		    		trxNode = n;
		    		break;
		    	}
		    	
		    	if(trxNode == null)
		    		continue;
		    	
		    	boolean includeFlag = true;
		    	for(AfpFrequencyDomainModel mod: model.getFreqDomains(false)){
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
		    		
	    		if (rowFilter != null){
		    		if (!rowFilter.equal(trxNode)) 
		    			continue;
		    	}
		    	this.addSectorUniqueProperties(sectorNode);
				this.addSiteUniqueProperties(siteNode);
		    	this.addTrxUniqueProperties(trxNode);
    		    	
		    	
		    	if(cnt <= 10) {  
			    	TableItem item = new TableItem(table, SWT.NONE);
			    	int j=0;
			    	for (String prop_name : headers){
			    		Object val = null;
			    		try {
			    			String type = this.headersNodeType.get(prop_name);
			    			if(NodeTypes.SITE.getId().equals(type)) {
			    				if (siteNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals("site"))
			    					val = (String)siteNode.getProperty(headers_prop.get(prop_name), "");

			    			} else if( NodeTypes.SECTOR.getId().equals(type)) {
			    				val = sectorNode.getProperty(headers_prop.get(prop_name), "");
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
			    cnt++;
		    }
		    for (int i = 0; i < headers.length; i++) {
		    	table.getColumn(i).pack();
		    }
		}
	}

	@Override
	public void onFilterSelected(String columnName,	ArrayList<String> selectedValues) {
		
	String val = headers_prop.get(columnName);
		
		if(val != null ) {
			AfpColumnFilter colFilter = new AfpColumnFilter(val, this.headersNodeType.get(columnName));
			for (String value: selectedValues){
				colFilter.addValue(value);
			}
			if(tabFolder.getSelectionIndex() ==1) {
				siteRowFilter.addColumn(colFilter);
				loadData(filterTableSite, siteRowFilter);
			} else {
				sectorRowFilter.addColumn(colFilter);
				loadData(filterTableSector, sectorRowFilter);
			}
		}
	}
	

	@Override
	public Object[] getColumnUniqueValues(String colName){
		
		String type  = headersNodeType.get(colName);
		if(NodeTypes.SITE.getId().equals(type)) {
			return this.getSiteUniqueValuesForProperty(headers_prop.get(colName));
		} else if(NodeTypes.SECTOR.getId().equals(type)) {
			return this.getSectorUniqueValuesForProperty(headers_prop.get(colName));
		} else {
			return this.getTrxUniqueValuesForProperty(headers_prop.get(colName));
		}
	}
}
