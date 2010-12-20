package org.amanzi.awe.afp.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.GraphDatabaseService;

public class AfpScalingRulesPage extends WizardPage {
	
	private final GraphDatabaseService service;
	private static final int textWidth = 25;
	// Indentation between cells in Interference Matrix
	private static final int imIndent = 10;
	
	public AfpScalingRulesPage(String pageName, GraphDatabaseService servise) {
		super(pageName);
        this.service = servise;
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page6Name);
        setPageComplete (false);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite thisParent = new Composite(parent, SWT.NONE);
   	 	thisParent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 6);
		
		TabFolder tabFolder =new TabFolder(thisParent, SWT.NONE | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
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
    	
    	
    	/** Create GridData objects for all texts **/
		//Row 0
		GridData sepGridData00 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData00.widthHint = textWidth;
		GridData sepGridData01 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData01.widthHint = textWidth;
		
		//Row 1
		GridData sepGridData10 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData10.widthHint = textWidth;
		GridData sepGridData11 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData11.widthHint = textWidth;
		
		//Row 2
		GridData sepGridData20 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData20.widthHint = textWidth;
		GridData sepGridData21 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData21.widthHint = textWidth;
		
		//Row 3
		GridData sepGridData30 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData30.widthHint = textWidth;
		GridData sepGridData31 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData31.widthHint = textWidth;
		
		//Row 4
		GridData sepGridData40 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData40.widthHint = textWidth;
		GridData sepGridData41 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData41.widthHint = textWidth;
	
		//Row 5
		GridData sepGridData50 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData50.widthHint = textWidth;
		GridData sepGridData51 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData51.widthHint = textWidth;
		
		//Row 6
		GridData sepGridData60 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData60.widthHint = textWidth;
		GridData sepGridData61 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData61.widthHint = textWidth;
		
		//Row 7
		GridData sepGridData70 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData70.widthHint = textWidth;
		GridData sepGridData71 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData71.widthHint = textWidth;
		
		//Row 8
		GridData sepGridData80 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData80.widthHint = textWidth;
		GridData sepGridData81 = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		sepGridData81.widthHint = textWidth;
    	
    	
    	new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		Text sectorText1 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText1.setLayoutData(sepGridData00);
		sectorText1.setText("100");
		Text siteText1 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText1.setLayoutData(sepGridData01);
		siteText1.setText("100");
		
		new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		Text sectorText2 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText2.setLayoutData(sepGridData10);
		sectorText2.setText("100");
		Text siteText2 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText2.setLayoutData(sepGridData11);
		siteText2.setText("70");
		
		new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		new Label(separationsGroup, SWT.LEFT).setText("SY TCH");
		Text sectorText3 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText3.setLayoutData(sepGridData20);
		sectorText3.setText("100");
		Text siteText3 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText3.setLayoutData(sepGridData21);
		siteText3.setText("50");
		
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		Text sectorText4 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText4.setLayoutData(sepGridData30);
		sectorText4.setText("100");
		Text siteText4 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText4.setLayoutData(sepGridData31);
		siteText4.setText("70");
		
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		Text sectorText5 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText5.setLayoutData(sepGridData40);
		sectorText5.setText("100");
		Text siteText5 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText5.setLayoutData(sepGridData41);
		siteText5.setText("50");
		
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(separationsGroup, SWT.LEFT).setText("SY TCH");
		Text sectorText6 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText6.setLayoutData(sepGridData50);
		sectorText6.setText("100");
		Text siteText6 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText6.setLayoutData(sepGridData51);
		siteText6.setText("30");
		
		new Label(separationsGroup, SWT.LEFT).setText("SY TCH");
		new Label(separationsGroup, SWT.LEFT).setText("BCCH");
		Text sectorText7 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText7.setLayoutData(sepGridData60);
		sectorText7.setText("100");
		Text siteText7 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText7.setLayoutData(sepGridData61);
		siteText7.setText("70");
		
		new Label(separationsGroup, SWT.LEFT).setText("SY TCH");
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		Text sectorText8 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText8.setLayoutData(sepGridData70);
		sectorText8.setText("100");
		Text siteText8 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText8.setLayoutData(sepGridData71);
		siteText8.setText("50");
		
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(separationsGroup, SWT.LEFT).setText("Non/BB TCH");
		Text sectorText9 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		sectorText9.setLayoutData(sepGridData80);
		sectorText9.setText("100");
		Text siteText9 = new Text (separationsGroup, SWT.BORDER | SWT.SINGLE);
		siteText9.setLayoutData(sepGridData81);
		siteText9.setText("30");
    	
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
		
		/** Create GridData objects for all texts **/
		//Row 0
		GridData gridData00 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData00.widthHint = textWidth;
		GridData gridData01 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData01.widthHint = textWidth;
		GridData gridData02 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData02.widthHint = textWidth;
		gridData02.horizontalIndent = imIndent;
		GridData gridData03 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData03.widthHint = textWidth;
		GridData gridData04 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData04.widthHint = textWidth;
		gridData04.horizontalIndent = imIndent;
		GridData gridData05 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData05.widthHint = textWidth;
		GridData gridData06 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData06.widthHint = textWidth;
		gridData06.horizontalIndent = imIndent;
		GridData gridData07 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData07.widthHint = textWidth;
		
		
		
		//Row 1
		GridData gridData10 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData10.widthHint = textWidth;
		GridData gridData11 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData11.widthHint = textWidth;
		GridData gridData12 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData12.widthHint = textWidth;
		gridData12.horizontalIndent = imIndent;
		GridData gridData13 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData13.widthHint = textWidth;
		GridData gridData14 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData14.widthHint = textWidth;
		gridData14.horizontalIndent = imIndent;
		GridData gridData15 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData15.widthHint = textWidth;
		GridData gridData16 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData16.widthHint = textWidth;
		gridData16.horizontalIndent = imIndent;
		GridData gridData17 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData17.widthHint = textWidth;
		
		//Row 2
		GridData gridData20 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData20.widthHint = textWidth;
		GridData gridData21 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData21.widthHint = textWidth;
		GridData gridData22 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData22.widthHint = textWidth;
		gridData22.horizontalIndent = imIndent;
		GridData gridData23 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData23.widthHint = textWidth;
		GridData gridData24 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData24.widthHint = textWidth;
		gridData24.horizontalIndent = imIndent;
		GridData gridData25= new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData25.widthHint = textWidth;
		GridData gridData26 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData26.widthHint = textWidth;
		gridData26.horizontalIndent = imIndent;
		GridData gridData27 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData27.widthHint = textWidth;
		
		//Row 3
		GridData gridData30 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData30.widthHint = textWidth;
		GridData gridData31 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData31.widthHint = textWidth;
		GridData gridData32 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData32.widthHint = textWidth;
		gridData32.horizontalIndent = imIndent;
		GridData gridData33 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData33.widthHint = textWidth;
		GridData gridData34 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData34.widthHint = textWidth;
		gridData34.horizontalIndent = imIndent;
		GridData gridData35 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData35.widthHint = textWidth;
		GridData gridData36 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData36.widthHint = textWidth;
		gridData36.horizontalIndent = imIndent;
		GridData gridData37 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData37.widthHint = textWidth;
		
		//Row 4
		GridData gridData40 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData40.widthHint = textWidth;
		GridData gridData41 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData41.widthHint = textWidth;
		GridData gridData42 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData42.widthHint = textWidth;
		gridData42.horizontalIndent = imIndent;
		GridData gridData43 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData43.widthHint = textWidth;
		GridData gridData44 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData44.widthHint = textWidth;
		gridData44.horizontalIndent = imIndent;
		GridData gridData45 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData45.widthHint = textWidth;
		GridData gridData46 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData46.widthHint = textWidth;
		gridData46.horizontalIndent = imIndent;
		GridData gridData47 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData47.widthHint = textWidth;
		
		//Row 5
		GridData gridData50 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData50.widthHint = textWidth;
		GridData gridData51 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData51.widthHint = textWidth;
		GridData gridData52 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData52.widthHint = textWidth;
		gridData52.horizontalIndent = imIndent;
		GridData gridData53 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData53.widthHint = textWidth;
		GridData gridData54 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData54.widthHint = textWidth;
		gridData54.horizontalIndent = imIndent;
		GridData gridData55 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData55.widthHint = textWidth;
		GridData gridData56 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData56.widthHint = textWidth;
		gridData56.horizontalIndent = imIndent;
		GridData gridData57 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData57.widthHint = textWidth;
		
		//Row 6
		GridData gridData60 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData60.widthHint = textWidth;
		GridData gridData61 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData61.widthHint = textWidth;
		GridData gridData62 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData62.widthHint = textWidth;
		gridData62.horizontalIndent = imIndent;
		GridData gridData63 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData63.widthHint = textWidth;
		GridData gridData64 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData64.widthHint = textWidth;
		gridData64.horizontalIndent = imIndent;
		GridData gridData65 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData65.widthHint = textWidth;
		GridData gridData66 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData66.widthHint = textWidth;
		gridData66.horizontalIndent = imIndent;
		GridData gridData67 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData67.widthHint = textWidth;
		
		//Row 7
		GridData gridData70 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData70.widthHint = textWidth;
		GridData gridData71 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData71.widthHint = textWidth;
		GridData gridData72 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData72.widthHint = textWidth;
		gridData72.horizontalIndent = imIndent;
		GridData gridData73 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData73.widthHint = textWidth;
		GridData gridData74 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData74.widthHint = textWidth;
		gridData74.horizontalIndent = imIndent;
		GridData gridData75 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData75.widthHint = textWidth;
		GridData gridData76 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData76.widthHint = textWidth;
		gridData76.horizontalIndent = imIndent;
		GridData gridData77 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData77.widthHint = textWidth;
		
		//Row 8
		GridData gridData80 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData80.widthHint = textWidth;
		GridData gridData81 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData81.widthHint = textWidth;
		GridData gridData82 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData82.widthHint = textWidth;
		gridData82.horizontalIndent = imIndent;
		GridData gridData83 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData83.widthHint = textWidth;
		GridData gridData84 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData84.widthHint = textWidth;
		gridData84.horizontalIndent = imIndent;
		GridData gridData85 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData85.widthHint = textWidth;
		GridData gridData86 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData86.widthHint = textWidth;
		gridData86.horizontalIndent = imIndent;
		GridData gridData87 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gridData87.widthHint = textWidth;
		
		
		
		
		//Row 0
    	new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		Text intCoText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText0.setLayoutData(gridData00);
		intCoText0.setText("1");
		Text intAdjText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText0.setLayoutData(gridData01);
		intAdjText0.setText("1");
		
		Text neiCoText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText0.setLayoutData(gridData02);
		neiCoText0.setText("1");
		Text neiAdjText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText0.setLayoutData(gridData03);
		neiAdjText0.setText("1");
		
		Text triCoText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText0.setLayoutData(gridData04);
		triCoText0.setText("1");
		Text triAdjText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText0.setLayoutData(gridData05);
		triAdjText0.setText("1");
		
		Text shaCoText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText0.setLayoutData(gridData06);
		shaCoText0.setText("1");
		Text shaAdjText0 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText0.setLayoutData(gridData07);
		shaAdjText0.setText("1");
		
		//Row 1
		new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		
		Text intCoText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText1.setLayoutData(gridData10);
		intCoText1.setText("0.7");
		Text intAdjText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText1.setLayoutData(gridData11);
		intAdjText1.setText("0.7");
		
		Text neiCoText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText1.setLayoutData(gridData12);
		neiCoText1.setText("0.3");
		Text neiAdjText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText1.setLayoutData(gridData13);
		neiAdjText1.setText("0.1");
		
		Text triCoText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText1.setLayoutData(gridData14);
		triCoText1.setText("0");
		Text triAdjText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText1.setLayoutData(gridData15);
		triAdjText1.setText("0");
		
		Text shaCoText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText1.setLayoutData(gridData16);
		shaCoText1.setText("0");
		Text shaAdjText1 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText1.setLayoutData(gridData17);
		shaAdjText1.setText("0");
		
		
		//Row 2
		new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		new Label(interferenceGroup, SWT.LEFT).setText("SY TCH");
		
		Text intCoText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText2.setLayoutData(gridData20);
		intCoText2.setText("0.7");
		Text intAdjText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText2.setLayoutData(gridData21);
		intAdjText2.setText("0.7");
		
		Text neiCoText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText2.setLayoutData(gridData22);
		neiCoText2.setText("0.3");
		Text neiAdjText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText2.setLayoutData(gridData23);
		neiAdjText2.setText("0.1");
		
		Text triCoText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText2.setLayoutData(gridData24);
		triCoText2.setText("0");
		Text triAdjText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText2.setLayoutData(gridData25);
		triAdjText2.setText("0");
		
		Text shaCoText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText2.setLayoutData(gridData26);
		shaCoText2.setText("0");
		Text shaAdjText2 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText2.setLayoutData(gridData27);
		shaAdjText2.setText("0");
		
		
		//Row 3
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		
		Text intCoText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText3.setLayoutData(gridData30);
		intCoText3.setText("0.7");
		Text intAdjText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText3.setLayoutData(gridData31);
		intAdjText3.setText("0.7");
		
		Text neiCoText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText3.setLayoutData(gridData32);
		neiCoText3.setText("0.3");
		Text neiAdjText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText3.setLayoutData(gridData33);
		neiAdjText3.setText("0.1");
		
		Text triCoText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText3.setLayoutData(gridData34);
		triCoText3.setText("0");
		Text triAdjText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText3.setLayoutData(gridData35);
		triAdjText3.setText("0");
		
		Text shaCoText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText3.setLayoutData(gridData36);
		shaCoText3.setText("0");
		Text shaAdjText3 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText3.setLayoutData(gridData37);
		shaAdjText3.setText("0");
		
		
		//Row 4
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		Text intCoText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText4.setLayoutData(gridData40);
		intCoText4.setText("0.7");
		Text intAdjText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText4.setLayoutData(gridData41);
		intAdjText4.setText("0.7");
		
		Text neiCoText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText4.setLayoutData(gridData42);
		neiCoText4.setText("0.3");
		Text neiAdjText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText4.setLayoutData(gridData43);
		neiAdjText4.setText("0.1");
		
		Text triCoText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText4.setLayoutData(gridData44);
		triCoText4.setText("0");
		Text triAdjText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText4.setLayoutData(gridData45);
		triAdjText4.setText("0");
		
		Text shaCoText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText4.setLayoutData(gridData46);
		shaCoText4.setText("0");
		Text shaAdjText4 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText4.setLayoutData(gridData47);
		shaAdjText4.setText("0");
		
		
		//Row 5
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("SY TCH");
		Text intCoText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText5.setLayoutData(gridData50);
		intCoText5.setText("0.7");
		Text intAdjText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText5.setLayoutData(gridData51);
		intAdjText5.setText("0.7");
		
		Text neiCoText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText5.setLayoutData(gridData52);
		neiCoText5.setText("0.3");
		Text neiAdjText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText5.setLayoutData(gridData53);
		neiAdjText5.setText("0.1");
		
		Text triCoText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText5.setLayoutData(gridData54);
		triCoText5.setText("0");
		Text triAdjText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText5.setLayoutData(gridData55);
		triAdjText5.setText("0");
		
		Text shaCoText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText5.setLayoutData(gridData56);
		shaCoText5.setText("0");
		Text shaAdjText5 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText5.setLayoutData(gridData57);
		shaAdjText5.setText("0");
		
		
		//Row 6
		new Label(interferenceGroup, SWT.LEFT).setText("SY TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("BCCH");
		
		Text intCoText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText6.setLayoutData(gridData60);
		intCoText6.setText("0.7");
		Text intAdjText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText6.setLayoutData(gridData61);
		intAdjText6.setText("0.7");
		
		Text neiCoText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText6.setLayoutData(gridData62);
		neiCoText6.setText("0.3");
		Text neiAdjText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText6.setLayoutData(gridData63);
		neiAdjText6.setText("0.1");
		
		Text triCoText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText6.setLayoutData(gridData64);
		triCoText6.setText("0");
		Text triAdjText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText6.setLayoutData(gridData65);
		triAdjText6.setText("0");
		
		Text shaCoText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText6.setLayoutData(gridData66);
		shaCoText6.setText("0");
		Text shaAdjText6 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText6.setLayoutData(gridData67);
		shaAdjText6.setText("0");
		
		//Row 7
		new Label(interferenceGroup, SWT.LEFT).setText("SY TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		
		Text intCoText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText7.setLayoutData(gridData70);
		intCoText7.setText("0.7");
		Text intAdjText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText7.setLayoutData(gridData71);
		intAdjText7.setText("0.7");
		
		Text neiCoText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText7.setLayoutData(gridData72);
		neiCoText7.setText("0.3");
		Text neiAdjText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText7.setLayoutData(gridData73);
		neiAdjText7.setText("0.1");
		
		Text triCoText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText7.setLayoutData(gridData74);
		triCoText7.setText("0");
		Text triAdjText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText7.setLayoutData(gridData75);
		triAdjText7.setText("0");
		
		Text shaCoText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText7.setLayoutData(gridData76);
		shaCoText7.setText("0");
		Text shaAdjText7 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText7.setLayoutData(gridData77);
		shaAdjText7.setText("0");
		
		//Row 8
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(interferenceGroup, SWT.LEFT).setText("Non/BB TCH");
		
		Text intCoText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intCoText8.setLayoutData(gridData80);
		intCoText8.setText("0.7");
		Text intAdjText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		intAdjText8.setLayoutData(gridData81);
		intAdjText8.setText("0.7");
		
		Text neiCoText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiCoText8.setLayoutData(gridData82);
		neiCoText8.setText("0.3");
		Text neiAdjText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		neiAdjText8.setLayoutData(gridData83);
		neiAdjText8.setText("0.1");
		
		Text triCoText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triCoText8.setLayoutData(gridData84);
		triCoText8.setText("0");
		Text triAdjText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		triAdjText8.setLayoutData(gridData85);
		triAdjText8.setText("0");
		
		Text shaCoText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaCoText8.setLayoutData(gridData86);
		shaCoText8.setText("0");
		Text shaAdjText8 = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		shaAdjText8.setLayoutData(gridData87);
		shaAdjText8.setText("0");
   	
		item2.setControl(interferenceGroup);

    	
		
		setPageComplete (true);
		setControl(thisParent);
	}

}
