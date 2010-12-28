package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AfpSeperationSelector extends AfpDomainSelector {
	AfpSeparationDomainModel domainModel;
	boolean isSector = true;
	Text text[] = new Text[9];
	final static String[]  serving = new String[] { "BCCH","BCCH","BCCH","Non/BB TCH","Non/BB TCH","Non/BB TCH" ,"SY TCH","SY TCH","SY TCH"};
	final static String[]  interfering = new String[] { "BCCH","Non/BB TCH","SY TCH","BCCH","Non/BB TCH","SY TCH","BCCH","Non/BB TCH","SY TCH"};

	public AfpSeperationSelector(WizardPage page,
			Shell parentShell, String action, Group parentGroup,
			AfpModel model, boolean isSector) {
		
		super(page, parentShell, parentGroup, model);
		this.isSector =isSector; 
		String entity = isSector? "Sector" : "Site";
		String title = action + " " + entity + " Separation Domain";	

		if(isSector) {
			this.createUI(action, title, model.getAllSectorSeparationDomainNames());
			if (action.equals("Edit") || action.equals("Delete")){
				for(AfpSeparationDomainModel d: model.getSectorSeparationDomains()) {
					this.domain2Edit = d;
					domainModel =d;
					break;
				}
			}else {
				domainModel = new AfpSeparationDomainModel(); 
			}
		}
		else {
			this.createUI(action, title, model.getAllSiteSeparationDomainNames());
			if (action.equals("Edit") || action.equals("Delete")){
				for(AfpSeparationDomainModel d: model.getSectorSeparationDomains()) {
					this.domain2Edit = d;
					domainModel =d;
					break;
				}
			}else {
				domainModel = new AfpSeparationDomainModel(); 
			}
		}

		freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(3, true));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 10));
		
		freqGroup.setText(entity + " Separation Rules");
		
		Label servingLabel = new Label(freqGroup, SWT.LEFT);
		servingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		servingLabel.setText("Serving");
		AfpWizardUtils.makeFontBold(servingLabel);
		
		Label interferingLabel = new Label(freqGroup, SWT.LEFT);
		interferingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		interferingLabel.setText("Interfering");
		AfpWizardUtils.makeFontBold(interferingLabel);
		
		Label separationLabel = new Label(freqGroup, SWT.LEFT);
		separationLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		separationLabel.setText("Separation");
		AfpWizardUtils.makeFontBold(separationLabel);
		
		for(int i=0; i<serving.length;i++) {
			new Label(freqGroup, SWT.LEFT).setText(serving[i]);
			new Label(freqGroup, SWT.LEFT).setText(interfering[i]);
			text[i] = new Text (freqGroup, SWT.BORDER | SWT.SINGLE);
			text[i].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			text[i].setText(domainModel.getSeparation(i));
			text[i].setData(new Integer(i));
			text[i].addModifyListener(new ModifyListener(){
	
				@Override
				public void modifyText(ModifyEvent e) {
					int index = ((Integer)((Text)e.getSource()).getData()).intValue();
					domainModel.setSeparation( index, ((Text)e.widget).getText());	
				}
				
			});
		}

		this.addButtons(action);
	}
	
	protected void handleDomainNameSection(int selection, String name) {
		AfpSeparationDomainModel d;
		if(this.isSector)
			d = model.findSectorSeparationDomain(name);
		else 
			d = model.findSiteSeparationDomain(name);
		
		if(d != null) {
			domainModel = d;
			this.domain2Edit =d;
			for(int i=0; i< 13;i++) {
				text[i].setText("" + domainModel.getSeparation(i));
			}
		}
	}
	protected void handleAddDomain() {
		if (isSector){
			domainModel.setName(domainName);
			model.addSectorSeparationDomain(domainModel);
		}
		else{
			domainModel.setName(domainName);
			model.addSiteSeparationDomain(domainModel);
		}
	}
	protected void handleEditDomain() {
		if(isSector)
			model.editSectorSeparationDomain(domainModel);
		else 
			model.editSiteSeparationDomain(domainModel);
	}
	
	protected void handleDeleteDomain() {
		if (isSector){
			model.deleteSectorSeparationDomain(domainModel);
			((AfpSeparationRulesPage)page).refreshPage();
		}
		else{
			model.deleteSiteSeparationDomain(domainModel);
			((AfpSeparationRulesPage)page).refreshPage();
		}
}

}
