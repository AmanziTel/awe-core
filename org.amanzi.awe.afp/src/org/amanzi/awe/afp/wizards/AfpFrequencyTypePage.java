package org.amanzi.awe.afp.wizards;

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
	
	private final GraphDatabaseService service;
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
	
	public AfpFrequencyTypePage(String pageName, GraphDatabaseService servise) {
		super(pageName);
        this.service = servise;
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
		
		Group frequencyDomainsGroup = new Group(main, SWT.NONE);
		frequencyDomainsGroup.setLayout(new GridLayout(4, true));
		frequencyDomainsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,1 ,7));
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
    	
    	AfpWizardUtils.createButtonsGroup(frequencyDomainsGroup, "FrequencyType");
    	
    	
    	AfpWizardUtils.getTRXFilterGroup(main);		
		
		
    	setPageComplete(true);
    	setControl (thisParent);
		
    	
	}

}
