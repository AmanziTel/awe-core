package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AfpMalSelector extends AfpDomainSelector {
	AfpHoppingMALDomainModel domainModel;
	private static Button actionButton;
	Text text[] = new Text[13];

	public AfpMalSelector(WizardPage page, Shell parentShell,
			String action, Group parentGroup, AfpModel model) {
		super( page, parentShell, parentGroup, model);
		
		this.createUI(action, " MAL Domain",model.getAllMALDomainNames());
		if (action.equals("Edit") || action.equals("Delete")){
			for(AfpHoppingMALDomainModel d: model.getMalDomains()) {
				this.domain2Edit = d;
				domainModel =d;
				break;
			}
		}else {
			domainModel = new AfpHoppingMALDomainModel(); 
		}
		
		freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(2, false));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,2 ,1));
		freqGroup.setText("MAL Size Rules");
		
		Label trxLabel = new Label(freqGroup, SWT.LEFT);
		trxLabel.setText("Hopping TRXs");
		AfpWizardUtils.makeFontBold(trxLabel);
		
		Label sizeLabel = new Label(freqGroup, SWT.LEFT);
		sizeLabel.setText("MAL Size");
		AfpWizardUtils.makeFontBold(sizeLabel);
		boolean delete = false;
		if (action.equals("Delete")){
			delete = true;
		}
		
		for(int i=0; i< 13;i++) {
			new Label(freqGroup, SWT.CENTER).setText(""+i);
			text[i] = new Text (freqGroup, SWT.BORDER | SWT.SINGLE);
			text[i].setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			text[i].setText("" + domainModel.getMALSize(i));
			text[i].setData(new Integer(i));
			if(delete)
				text[i].setEnabled(false);
			//TODO add some mechanism to have only one listener class for all these buttons.
			text[i].addModifyListener(new ModifyListener(){
	
				@Override
				public void modifyText(ModifyEvent e) {
					int index = ((Integer)((Text)e.getSource()).getData()).intValue();
					int size = Integer.parseInt(((Text)e.widget).getText()); 
					/*if (size < 1)
						actionButton.setEnabled(false);*/
					domainModel.setMALSize(index,size);	
				}				
			});
		}
		this.addButtons(action);
	}
	protected void handleDomainNameSection(int selection, String name) {
		
		AfpHoppingMALDomainModel d = model.findMALDomain(name);
		if(d != null) {
			domainModel = d;
			this.domain2Edit =d;
			for(int i=0; i< 13;i++) {
				text[i].setText("" + domainModel.getMALSize(i));
			}
		}
		//freqGroup.layout();
	}
	protected boolean handleAddDomain() {
		if(domainName == null) {
			return false;
		}
		if(domainName.trim().length() == 0) {
			return false;
		}
		domainModel.setName(domainName);
		model.addMALDomain(domainModel);
		return true;
	}
	protected void handleEditDomain() {
		model.editMALDomain(domainModel);
	}
	protected void handleDeleteDomain() {
		model.setTotalRemainingMalTRX(model.getTotalRemainingMalTRX() + domainModel.getNumTRX());
		model.deleteMALDomain(domainModel);
	}

}
