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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpAvailableResourcesPage extends WizardPage implements Listener {
	
	private final GraphDatabaseService service;
	protected Text frequencies900;
	protected Text frequencies1800;
	protected Text frequencies850;
	protected Text frequencies1900;
	protected Button ncc[] = new Button[8];
	protected Button bcc[] = new Button[8];
	
	private Label freq900Label;
	private Label freq1800Label;
	private Label freq850Label;
	private Label freq1900Label;
	private Button button900;
	private Button button1800;
	private Button button850;
	private Button button1900;
	
	
	private AfpModel model;
	

	public AfpAvailableResourcesPage(String pageName, GraphDatabaseService servise, AfpModel model) {
		super(pageName);
        this.service = servise;
        this.model = model;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page2Name);
        setPageComplete (false);
	}

	@Override
	public void createControl(Composite parent) {
		final  Shell parentShell = parent.getShell();
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 2);
		
		Group main = new Group(thisParent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1 ,2));
		
		Group frequenciesGroup = new Group(main, SWT.NONE);
		frequenciesGroup.setLayout(new GridLayout(3, false));
		frequenciesGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1 ,5));
		frequenciesGroup.setText("Frequencies");
		
		Label bandLabel = new Label(frequenciesGroup, SWT.LEFT);
		bandLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 3 , 1));
		bandLabel.setText("Band");
    	AfpWizardUtils.makeFontBold(bandLabel);
		
    	freq900Label = new Label (frequenciesGroup, SWT.LEFT);
    	freq900Label.setText("900: ");
    	frequencies900 = new Text (frequenciesGroup, SWT.BORDER | SWT.SINGLE);
    	frequencies900.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
    	frequencies900.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (frequencies900.getText().trim().equals("") && model.getFrequencyBands()[0])
					setErrorMessage("Select frequencies for 900 band");
				else {
					setErrorMessage(null);
					model.setAvailableFreq900(frequencies900.getText());
				}
				button900.setSelection(false);
			
				setPageComplete(canFlipToNextPage());
					
			}
    		
    	});
		
    	button900 = new Button(frequenciesGroup, GridData.END);
    	button900.setText("...");
    	button900.addSelectionListener(new SelectionAdapter(){
    		
    		public String[] getFrequencyArray(){
    			String frequencies[] = new String[(124-0+1) + (1023-955+1)];
    			for (int i = 0; i < frequencies.length; i++){
    				if (i <= 124)
    					frequencies[i] = Integer.toString(i);
    				else
    					frequencies[i] = Integer.toString(i + 955 - 124 + 1);
    			}
    			
    			return frequencies;
    		}
    		
			@Override
			public void widgetSelected(SelectionEvent e) {
				AfpWizardUtils.createFrequencySelector(parentShell, frequencies900, getFrequencyArray());
				
			}
    		
    	});
    	
    	freq1800Label = new Label (frequenciesGroup, SWT.LEFT);
    	freq1800Label.setText("1800: ");
    	frequencies1800 = new Text (frequenciesGroup, SWT.BORDER | SWT.SINGLE);
    	frequencies1800.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
    	frequencies1800.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (frequencies1800.getText().trim().equals("") && model.getFrequencyBands()[1])
					setErrorMessage("Select frequencies for 1800 band");
				else {
					setErrorMessage(null);
					model.setAvailableFreq1800(frequencies1800.getText());
				}
				button1800.setSelection(false);
				setPageComplete(canFlipToNextPage());	
			}
    		
    	});
		
    	button1800 = new Button(frequenciesGroup, GridData.END);
    	button1800.setText("...");
    	button1800.addSelectionListener(new SelectionAdapter(){
    		
    		public String[] getFrequencyArray(){
    			String frequencies[] = new String[885-512+1];
    			for (int i = 0; i < frequencies.length; i++){
    				frequencies[i] = Integer.toString(512 + i); 
    			}
    			
    			return frequencies;
    		}
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				AfpWizardUtils.createFrequencySelector(parentShell, frequencies1800, getFrequencyArray());				
			}
    		
    	});
    	
    	freq850Label = new Label (frequenciesGroup, SWT.LEFT);
    	freq850Label.setText("850: ");
    	frequencies850 = new Text (frequenciesGroup, SWT.BORDER | SWT.SINGLE);
    	frequencies850.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
    	frequencies850.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (frequencies850.getText().trim().equals("") && model.getFrequencyBands()[2])
					setErrorMessage("Select frequencies for 850 band");
				else {
					setErrorMessage(null);
					model.setAvailableFreq850(frequencies850.getText());
				}
				button850.setSelection(false);
		
				setPageComplete(canFlipToNextPage());
					
			}
    		
    	});
		
    	button850 = new Button(frequenciesGroup, GridData.END);
    	button850.setText("...");
    	button850.addSelectionListener(new SelectionAdapter(){
			
    		public String[] getFrequencyArray(){
    			String frequencies[] = new String[251-128+1];
    			for (int i = 0; i < frequencies.length; i++){
    				frequencies[i] = Integer.toString(251 + i); 
    			}
    			
    			return frequencies;
    		}
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				AfpWizardUtils.createFrequencySelector(parentShell, frequencies850, getFrequencyArray());
				
			}
    		
    	});
    	
    	freq1900Label = new Label (frequenciesGroup, SWT.LEFT);
    	freq1900Label.setText("1900: ");
    	frequencies1900 = new Text (frequenciesGroup, SWT.BORDER | SWT.SINGLE);
    	frequencies1900.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
    	frequencies1900.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (frequencies1900.getText().trim().equals("") && model.getFrequencyBands()[0])
					setErrorMessage("Select frequencies for 1900 band");
				else {
					setErrorMessage(null);
					model.setAvailableFreq1900(frequencies1900.getText());
				}
				
				button1900.setSelection(false);
				setPageComplete(canFlipToNextPage());
					
			}
    		
    	});
		
    	button1900 = new Button(frequenciesGroup, GridData.END);
    	button1900.setText("...");
    	button1900.addSelectionListener(new SelectionAdapter(){
    		
    		public String[] getFrequencyArray(){
    			String frequencies[] = new String[512-810+1];
    			for (int i = 0; i < frequencies.length; i++){
    				frequencies[i] = Integer.toString(512 + i); 
    			}
    			
    			return frequencies;
    		}
    		
			@Override
			public void widgetSelected(SelectionEvent e) {
				AfpWizardUtils.createFrequencySelector(parentShell, frequencies1900, getFrequencyArray());
				button1900.setSelection(false);
			}
    		
    	});
    	
    	
    	/** Create BSIC Group*/
    	Group bsicGroup = new Group(main, SWT.NONE);
    	bsicGroup.setLayout(new GridLayout(9, false));
    	bsicGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false,1 ,5));
    	bsicGroup.setText("BSIC");
    	
    	String[] headers = { " ", "0", "1", "2", "3", "4", "5", "6", "7"};
	    for (int i = 0; i < headers.length; i++) {
	    	new Label(bsicGroup, GridData.BEGINNING).setText(headers[i]);
	    }
		
	    new Label(bsicGroup, GridData.BEGINNING).setText("Available NCCs: ");
	    for(int i=0; i< ncc.length;i++) {
		    ncc[i] = new Button (bsicGroup, SWT.CHECK);
		    ncc[i].addListener(SWT.Selection, this);
		    ncc[i].setSelection(true);
	    }
	    
	    new Label(bsicGroup, GridData.BEGINNING).setText("Available BCCs: ");
	    for(int i=0; i< ncc.length;i++) {
		    bcc[i] = new Button (bsicGroup, SWT.CHECK);
		    bcc[i].addListener(SWT.Selection, this);
		    bcc[i].setSelection(true);
	    }
	    
    	
	    setPageComplete(true);
		setControl (thisParent);
		
	}
	
	 public boolean canFlipToNextPage(){
	  	   if (isValidPage())
	  	        return true;
	  	    return false;
	  	}


	  /**
	   * Checks if is valid page.
	   * 
	   * @return true, if is valid page
	   */
	  protected boolean isValidPage() {
	      if ((frequencies900.isEnabled() && frequencies900.getText().trim().equals("")) 
	    		  || (frequencies1800.isEnabled() && frequencies1800.getText().trim().equals("")) 
	    		  || (frequencies850.isEnabled() && frequencies850.getText().trim().equals(""))
	    		  || (frequencies1900.isEnabled() && frequencies1900.getText().trim().equals("")))
	    	  return false;
	      
	      //TODO set this flag to true here only for testing purpose. Should be only done in summary page otherwise
	      //AfpImportWizard.isDone = true;
	      return true;
	  }
	  
	 /* public HashMap<String, String> getFrequencies(){
		  HashMap<String, String>  bandFrequencies = null;
		  bandFrequencies.put("900", frequencies900.getText());
		  bandFrequencies.put("1800", frequencies1800.getText());
		  bandFrequencies.put("850", frequencies850.getText());
		  bandFrequencies.put("1900", frequencies1900.getText());
		  
		  return bandFrequencies;
	  }
	  
	  public void setFrequencies(HashMap<String, String>  bandFrequencies){
		  frequencies900.setText(bandFrequencies.get("900"));
		  frequencies1800.setText(bandFrequencies.get("1800"));
		  frequencies850.setText(bandFrequencies.get("850"));
		  frequencies1900.setText(bandFrequencies.get("1900"));
		  
	  }
	  
	  public HashMap<String, boolean[]> getBSIC(){
		  HashMap<String, boolean[]> bsics = null;
		  boolean[] availableNCCs = {ncc0.getSelection(),
				  					 ncc1.getSelection(),
				  					 ncc2.getSelection(),
				  					 ncc3.getSelection(),
				  					 ncc4.getSelection(),
				  					 ncc5.getSelection(),
				  					 ncc6.getSelection(),
				  					 ncc7.getSelection(),
				  					};
		  
		  boolean[] availableBCCs = {bcc0.getSelection(),
					 				 bcc1.getSelection(),
					 				 bcc2.getSelection(),
					 				 bcc3.getSelection(),
					 				 bcc4.getSelection(),
					 				 bcc5.getSelection(),
					 				 bcc6.getSelection(),
					 				 bcc7.getSelection(),
									};
		  
		  bsics.put("NCCs", availableNCCs);
		  bsics.put("BCCs", availableBCCs);
		  
		  return bsics;
	  }*/

	@Override
	public void handleEvent(Event event) {
		boolean[] availableNCCs = new boolean[ncc.length];
		boolean[] availableBCCs = new boolean[bcc.length];

		for(int i=0;i< ncc.length;i++) {
			availableNCCs[i] = ncc[i].getSelection();
		}
		for(int i=0;i< bcc.length;i++) {
			availableBCCs[i] = bcc[i].getSelection();
		}
		model.setAvailableNCCs(availableNCCs);
		model.setAvailableBCCs(availableBCCs);
	}
	
	public void refreshPage(){
		if (!model.getFrequencyBands()[0]){
    		frequencies900.setEnabled(false);
    		freq900Label.setEnabled(false);
    		button900.setEnabled(false);
    	}
		if (!model.getFrequencyBands()[1]){
    		frequencies1800.setEnabled(false);
    		freq1800Label.setEnabled(false);
    		button1800.setEnabled(false);
    	}
		if (!model.getFrequencyBands()[2]){
    		frequencies850.setEnabled(false);
    		freq850Label.setEnabled(false);
    		button850.setEnabled(false);
    	}
		if (!model.getFrequencyBands()[3]){
    		frequencies1900.setEnabled(false);
    		freq1900Label.setEnabled(false);
    		button1900.setEnabled(false);
    	}
		boolean[] availableNCCs = model.getAvailableNCCs();
		boolean[] availableBCCs = model.getAvailableBCCs();

		if(availableNCCs != null) {
			for(int i=0;i< ncc.length;i++) {
				ncc[i].setSelection(availableNCCs[i]);
			}
		}
		if(availableBCCs != null) {
			for(int i=0;i< ncc.length;i++) {
				ncc[i].setSelection(availableBCCs[i]);
			}
		}
	}

}
