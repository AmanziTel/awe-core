package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AfpFrequencySelector {
	final Shell subShell;
	final AfpFrequencyDomainModel domainModel;
	private String domainName;
	Combo bandCombo;
	List selectedList;
	List freqList; 
	Group freqGroup;
	AfpModel model;
	Label selectionLabel;
	private static String[] selectedArray;
	
	boolean newDomain = true;
	AfpFrequencyDomainModel domain2Edit = null;
	int bandIndexes[];
	Button leftArrowButton;
	Button rightArrowButton;
	
	public AfpFrequencySelector(final WizardPage page, Shell parentShell, final String action, final Group parentGroup, final AfpModel model){

		int selectedBand =0;
		bandIndexes = model.getAvailableFrequencyBandsIndexs();
		subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
		domainModel = new AfpFrequencyDomainModel();
		this.model = model;
		
		subShell.setText(action +  " Frequency Domain");
		subShell.setLayout(new GridLayout(3, false));
		subShell.setLocation(200, 200);
		
		Label nameLabel = new Label(subShell, SWT.NONE);
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		nameLabel.setText("Domain Name");

		if (action.equals("Add")){
			Text nameText = new Text (subShell, SWT.BORDER | SWT.SINGLE);
			nameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
			nameText.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Text)e.widget).getText();
				}
			});
		}
		
		if (action.equals("Edit") || action.equals("Delete")){
			newDomain = false;
			Combo nameCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			String names[] = model.getAllFrequencyDomainNames();
			if(names == null) {
				return;
			}
			if(names.length ==0) {
				return;
			}
			nameCombo.setItems(names);
			nameCombo.select(0);
			nameCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
			nameCombo.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					//domainName = ((Combo)e.widget).getText();
					int i= ((Combo)e.widget).getSelectionIndex();
					int j=0;
					for(AfpFrequencyDomainModel d: model.getFreqDomains(false)) {
						if(j ==i) {
							domain2Edit = d;
							break;
						}
						j++;
					}
					updateFreqList();
				}
				
			});
			
			nameCombo.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);					
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					domainName = ((Combo)e.widget).getText();
				}
				
			});
			
			
			for(AfpFrequencyDomainModel d: model.getFreqDomains(false)) {
				this.domain2Edit = d;
				break;
			}
		}
		
		freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(3, false));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,3 ,1));
		freqGroup.setText("Frequency Selector");
		
		Label bandLabel = new Label(freqGroup, SWT.LEFT);
		bandLabel.setText("Band");
		bandLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,3 ,1));
		
		bandCombo = new Combo(freqGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		bandCombo.setItems(model.getAvailableBands());
		bandCombo.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,3 ,1));
		bandCombo.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);					
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					if(domain2Edit != null) {
						domain2Edit.setBand(AfpModel.BAND_NAMES[bandIndexes[bandCombo.getSelectionIndex()]]);
						domain2Edit.setFrequencies(null);
					}
					updateFreqList();
				}
				
		});
		bandCombo.select(selectedBand);
		
		Label freqLabel = new Label (freqGroup, SWT.LEFT);
		freqLabel.setText("Frequencies");
		freqLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
		
		selectionLabel = new Label (freqGroup, SWT.LEFT);
		selectionLabel.setText("0 Frequencies Selected");
		selectionLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));

		//updateFreqList();
		createListSelector(new String[0],new String[0]);
		updateFreqList();
		if (action.equals("Delete")){
			bandCombo.setEnabled(false);
			selectedList.setEnabled(false);
			freqList.setEnabled(false);
			this.leftArrowButton.setEnabled(false);
			this.rightArrowButton.setEnabled(false);
		}

		Button actionButton = new Button(subShell, SWT.PUSH);
		actionButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
		
		actionButton.setText(action);
		actionButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (action.equals("Add")){
					domainModel.setName(domainName);
					domainModel.setBand(bandCombo.getText());
					domainModel.setFrequencies(selectedArray);
					model.addFreqDomain(domainModel);
					((AfpFrequencyTypePage)page).refreshPage();
					//parentGroup.layout();
				}
				if (action.equals("Edit")){
					selectedArray = selectedList.getItems();
					domain2Edit.setFrequencies(selectedArray);
					model.editFreqDomain(domain2Edit);
					((AfpFrequencyTypePage)page).refreshPage();
					//parentGroup.layout();
				}
				
				if (action.equals("Delete")){
					if (domain2Edit == null){
						//TODO Do some error handling here;
						return;
					}
					model.deleteFreqDomain(domain2Edit.getName());
					((AfpFrequencyTypePage)page).refreshPage();
				}
				
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

	void updateFreqList() {
		//TODO update the frequencies on basis of selection
		//String leftFreq[] = model.getFrequencyArray(bandIndexes[0]);
		String frequencies[];
		if(this.domain2Edit == null) {
			frequencies = AfpModel.rangeArraytoArray(model.getAvailableFreq(bandCombo.getItem(bandCombo.getSelectionIndex())).split(","));
			selectedList.removeAll();
		} else {
			//select the band
			for(int i=0;i< bandIndexes.length;i++) {
				if(AfpModel.BAND_NAMES[bandIndexes[i]].compareTo(this.domain2Edit.getBand())==0) {
					bandCombo.select(i);
					break;
				}
			}
			frequencies = AfpModel.rangeArraytoArray(model.getAvailableFreq(bandCombo.getItem(bandCombo.getSelectionIndex())).split(","));

			selectedList.removeAll();
			String[] selected = AfpModel.convertFreqString2Array(domain2Edit.getFrequenciesAsString(),frequencies);
			if(selected.length >0) {
				selectedList.setItems(selected);
			}
		}
		
		freqList.setItems(frequencies);
	}
	public void createListSelector(String[] leftList, String[] rightList){
		
		int numSelected = 0;
		final Label thisSelectionLabel = selectionLabel;
		freqList = new List(freqGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2);
		int listHeight = freqList.getItemHeight() * 12;
		int listWidth = selectionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		Rectangle trim = freqList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		gridData.widthHint = listWidth;
		freqList.setLayoutData(gridData);
		//freqList.setItems(leftList);
		
		rightArrowButton = new Button (freqGroup, SWT.ARROW | SWT.RIGHT | SWT.BORDER);
		GridData arrowGridData = new GridData(GridData.FILL, GridData.END, true, false,1 ,1);
		arrowGridData.verticalIndent = trim.height/2;
		rightArrowButton.setLayoutData(arrowGridData);
		
		
		selectedList = new List(freqGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2);
		gridData.heightHint = trim.height;
		gridData.widthHint = listWidth;
		selectedList.setLayoutData(gridData);
		selectedList.setItems(rightList);
		
		rightArrowButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (freqList.getSelectionCount() > 0){
					String selectedNew[] = freqList.getSelection();
					for (String item: selectedNew){//int i = 0; i < selectedNew.length; i++){
						selectedList.add(item);
						freqList.remove(item);
					}
					String array[] = AfpModel.rangeArraytoArray(selectedList.getItems());
					String selected[] = AfpModel.arrayToRangeArray(array);
					selectedArray = selected;
					selectedList.setItems(selected);
					thisSelectionLabel.setText("" + array.length + " Frequencies selected");
				}
				
			}
		});
		
		
		leftArrowButton = new Button (freqGroup, SWT.ARROW | SWT.LEFT | SWT.BORDER);
		leftArrowButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));
		leftArrowButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedList.getSelectionCount() > 0){
					String deSelected[] = selectedList.getSelection();
					for (String item: deSelected){//int i = 0; i < deSelected.length; i++){
						freqList.add(item);
						selectedList.remove(item);
					}
					selectedArray = selectedList.getItems();
					String array[] = AfpModel.rangeArraytoArray(selectedArray);
					thisSelectionLabel.setText("" + array.length + " Frequencies selected");
					String notSelected[] = AfpModel.rangeArraytoArray(freqList.getItems());
					freqList.setItems(notSelected);
				}
				
			}
		});
	}

}
