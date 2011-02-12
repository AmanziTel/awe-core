package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class AfpScalingRulesPage extends AfpWizardPage implements Listener{
	
	private static final int textWidth = 25;
	private static final int sepTextWidth = 35;
	// Indentation between cells in Interference Matrix
	private static final int imIndent = 10;
	
	
	private Text[][] intTexts = new Text[9][8];
	private Text[][] sepTexts = new Text[9][2];
	
	
	public AfpScalingRulesPage(String pageName, AfpModel model, String desc) {
		super(pageName, model);
        setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete (false);
        
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 6);
		
		TabFolder tabFolder =new TabFolder(thisParent, SWT.NONE | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    	TabItem item1 =new TabItem(tabFolder,SWT.NONE);
		item1.setText("Separations");
		
		Group separationsGroup = new Group(tabFolder, SWT.NONE);
		separationsGroup.setLayout(new GridLayout(4, false));
		separationsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		Label servingLabel = new Label(separationsGroup, SWT.LEFT);
		servingLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		servingLabel.setText("Serving");
    	AfpWizardUtils.makeFontBold(servingLabel);
    	
    	Label interferingLabel = new Label(separationsGroup, SWT.LEFT);
    	interferingLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
    	interferingLabel.setText("Interfering");
    	AfpWizardUtils.makeFontBold(interferingLabel);
    	
    	Label sectorLabel = new Label(separationsGroup, SWT.LEFT);
    	sectorLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
    	sectorLabel.setText("Sector");
    	AfpWizardUtils.makeFontBold(sectorLabel);
    	
    	Label siteLabel = new Label(separationsGroup, SWT.LEFT);
    	siteLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
    	siteLabel.setText("Site");
    	AfpWizardUtils.makeFontBold(siteLabel);
    	
    	
    	
    	float[][] separationRules = new float[][]{
				model.getSectorSeparation(),
				model.getSiteSeparation()
				};
    	
    	for (int i = 0; i < sepTexts.length; i++){
			new Label(separationsGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][0]);
			new Label(separationsGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][1]);
			for (int j = 0; j < sepTexts[i].length; j++){
				GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
				gridData.widthHint = sepTextWidth;
				sepTexts[i][j] = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
				sepTexts[i][j].setLayoutData(gridData);
				sepTexts[i][j].setText(Float.toString(separationRules[j][i]));
				sepTexts[i][j].addListener(SWT.Modify, this);
			}
			
		}
    	
		item1.setControl(separationsGroup);
		
		
		
		TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Interference Matrices");
		
		Group interferenceGroup = new Group(tabFolder, SWT.NONE);
		interferenceGroup.setLayout(new GridLayout(10, false));
		interferenceGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		String headers[] = {"","Interference", "Neighbour", "Triangulation", "Shadowing"};
		for (String item: headers){
			Label headerLabel = new Label(interferenceGroup, SWT.LEFT);
			headerLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, false, false, 2, 1));
			headerLabel.setText(item);
	    	AfpWizardUtils.makeFontBold(headerLabel);
		}
		
		String headers2[] = {"Serving", "Interfering", "Co", "Adj", "Co", "Adj", "Co", "Adj", "Co", "Adj"};
		for (String item: headers2){
			GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
			Label headerLabel = new Label(interferenceGroup, SWT.CENTER);
			if (item.equals("Serving")){
				gridData.horizontalAlignment = GridData.BEGINNING | GridData.FILL;
			}
			headerLabel.setLayoutData(gridData);
			headerLabel.setText(item);
	    	AfpWizardUtils.makeFontBold(headerLabel);
		}
		
		
		float[][] scalingRules = new float[][]{
				model.getCoInterference(),
				model.getAdjInterference(),
				model.getCoNeighbor(),
				model.getAdjNeighbor(),
				model.getCoTriangulation(),
				model.getAdjTriangulation(),
				model.getCoShadowing(),
				model.getAdjShadowing()
				};
    	
    	for (int i = 0; i < intTexts.length; i++){
			new Label(interferenceGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][0]);
			new Label(interferenceGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][1]);
			for (int j = 0; j < intTexts[i].length; j++){
				GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
				gridData.widthHint = textWidth;
				if ((j != 0) && ((j % 2) == 0))
					gridData.horizontalIndent = imIndent;
				intTexts[i][j] = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
				intTexts[i][j].setLayoutData(gridData);
				intTexts[i][j].setText(Float.toString(scalingRules[j][i]));
				intTexts[i][j].addListener(SWT.Modify, this);
			}
			
		}
		
		item2.setControl(interferenceGroup);

    	
		
		setPageComplete (true);
		setControl(thisParent);
	}

	@Override
	public void handleEvent(Event event) {
		setErrorMessage(null);
		setPageComplete(true);
		float[] siteSeparation = new float[sepTexts.length];
		float[] sectorSeparation = new float[sepTexts.length];
		//Construct an array of all interference matrix arrays
		float[][] interferenceArray= new float[intTexts[0].length][intTexts.length];
		
		try{
			for (int i = 0; i < sepTexts.length; i++){
				float val = Float.parseFloat(sepTexts[i][0].getText());
				if (val < 0 || val > 100)
					showError();
				sectorSeparation[i] = val;
			}
			
			for (int i = 0; i < sepTexts.length; i++){
				float val = Float.parseFloat(sepTexts[i][1].getText());
				if (val < 0 || val > 100)
					showError();
				siteSeparation[i] = val;
			}
			
			
			for (int i = 0; i < interferenceArray.length; i++){
				for (int j = 0; j < interferenceArray[i].length; j++){
					float val = Float.parseFloat(intTexts[j][i].getText());
					if (val < 0 || val > 100)
						showError();
					interferenceArray[i][j] = val;
				}
			}
		}
		catch(NumberFormatException e){
			setErrorMessage("Only numeric values between 1 and 100 with step size of 0.1 are allowed");
		}
		
		model.setSiteSeparation(siteSeparation);
		model.setSectorSeparation(sectorSeparation);
		model.setInterferenceMatrixArrays(interferenceArray);
	}
	
	public void showError(){
		setErrorMessage("Only numeric values between 1 and 100 with step size of 0.1 are allowed");
		setPageComplete(false);
	}
	
	
	
	public void refreshPage(){
		
	}
	

}
