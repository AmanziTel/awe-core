package org.amanzi.awe.afp.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpSYHoppingMALsPage extends WizardPage {
	
	private Group malDomainsGroup;
	private Label defaultTrx;
	public static String test = "test";
	
	protected static HashMap<String, Label[]> domainLabels;
	private AfpModel model;
	
	
	public AfpSYHoppingMALsPage(String pageName, AfpModel model) {
		super(pageName);
        this.model = model;
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
    	
    	AfpWizardUtils.createButtonsGroup(malDomainsGroup, "HoppingMAL", model);
    	domainLabels = new HashMap<String, Label[]>();
    	
    	AfpWizardUtils.getTRXFilterGroup(main);

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
		Set<String> keys = domainLabels.keySet();
		if(keys != null) {
			Iterator ki = keys.iterator();
			
			while(ki.hasNext()) {
				String key = (String)ki.next();
				deleteDomainLabels(key);
				domainLabels.remove(key);
			}
			//domainLabels.clear();
		}
		Vector<AfpHoppingMALDomainModel> domains = model.getMalDomains();
		for(int i=0;i< domains.size();i++) {
			AfpHoppingMALDomainModel domainModel = domains.elementAt(i);
			Label defaultDomainLabel = new Label(malDomainsGroup, SWT.LEFT);
			defaultDomainLabel.setText(domainModel.getName());
			//TODO: update the TRXs by default here
			Label defaultTRXsLabel = new Label(malDomainsGroup, SWT.RIGHT);
			defaultTRXsLabel.setText("0");
			domainLabels.put(domainModel.getName(), new Label[]{defaultDomainLabel, defaultTRXsLabel});
		}    	
		
		malDomainsGroup.layout();
	}


}
