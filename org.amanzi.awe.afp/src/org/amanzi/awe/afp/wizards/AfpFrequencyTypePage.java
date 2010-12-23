package org.amanzi.awe.afp.wizards;

import java.util.HashMap;

import org.amanzi.awe.afp.models.AfpModel;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpFrequencyTypePage extends WizardPage {
	
	public final static String freePlan900DomainName = "Free Plan 900";
	public final static String freePlan1800DomainName = "Free Plan 1800";
	public final static String freePlan850DomainName = "Free Plan 850";
	public final static String freePlan1900DomainName = "Free Plan 1900";
	
	private final GraphDatabaseService service;
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
	
	private AfpModel model;
	protected static HashMap<String, Label[]> domainLabels;
	
	public AfpFrequencyTypePage(String pageName, GraphDatabaseService servise, AfpModel model) {
		super(pageName);
        this.service = servise;
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page3Name);
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
    	
    	AfpWizardUtils.createButtonsGroup(frequencyDomainsGroup, "FrequencyType", model);
    	domainLabels = new HashMap<String, Label[]>();
    	
    	AfpWizardUtils.getTRXFilterGroup(main);		
		
		
    	setPageComplete(true);
    	setControl (thisParent);
		
    	
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
		if (model.getFrequencyBands()[0]){
			Label freePlan900Label = new Label(frequencyDomainsGroup, SWT.LEFT);
			freePlan900Label.setText(freePlan900DomainName);
			Label frequenciesFreePlan900Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			//TODO: update the num frequencies and TRXs by default here
			frequenciesFreePlan900Label.setText("todo");
			Label TRXsFreePlan900Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			TRXsFreePlan900Label.setText("todo");
			domainLabels.put(freePlan900DomainName, new Label[]{freePlan900Label, frequenciesFreePlan900Label, TRXsFreePlan900Label});
    	}
		
		if (model.getFrequencyBands()[1]){
			Label freePlan1800Label = new Label(frequencyDomainsGroup, SWT.LEFT);
			freePlan1800Label.setText(freePlan1800DomainName);
			Label frequenciesFreePlan1800Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			//TODO: update the num frequencies and TRXs by default here
			frequenciesFreePlan1800Label.setText("todo");
			Label TRXsFreePlan1800Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			TRXsFreePlan1800Label.setText("todo");
			domainLabels.put(freePlan1800DomainName, new Label[]{freePlan1800Label,frequenciesFreePlan1800Label, TRXsFreePlan1800Label});
    	}
		
		if (model.getFrequencyBands()[2]){
			Label freePlan850Label = new Label(frequencyDomainsGroup, SWT.LEFT);
			freePlan850Label.setText(freePlan850DomainName);
			Label frequenciesFreePlan850Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			//TODO: update the num frequencies and TRXs by default here
			frequenciesFreePlan850Label.setText("todo");
			Label TRXsFreePlan850Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			TRXsFreePlan850Label.setText("todo");
			domainLabels.put(freePlan850DomainName, new Label[]{freePlan850Label, frequenciesFreePlan850Label, TRXsFreePlan850Label});
    	}
		
		if (model.getFrequencyBands()[3]){
			Label freePlan1900Label = new Label(frequencyDomainsGroup, SWT.LEFT);
			freePlan1900Label.setText(freePlan1900DomainName);
			Label frequenciesFreePlan1900Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			//TODO: update the num frequencies and TRXs by default here
			frequenciesFreePlan1900Label.setText("todo");
			Label TRXsFreePlan1900Label = new Label(frequencyDomainsGroup, SWT.RIGHT);
			TRXsFreePlan1900Label.setText("todo");
			domainLabels.put(freePlan1900DomainName, new Label[]{freePlan1900Label, frequenciesFreePlan1900Label, TRXsFreePlan1900Label});
    	}
		
		frequencyDomainsGroup.layout();
	}

}
