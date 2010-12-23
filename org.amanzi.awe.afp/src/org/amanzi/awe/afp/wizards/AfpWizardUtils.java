package org.amanzi.awe.afp.wizards;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import org.amanzi.awe.afp.models.AfpDomainModel;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;


public class AfpWizardUtils {
	
	private static String domainName;
	private static String[] selectedArray;
	private static Button actionButton;
	
	protected static Group getStepsGroup(Composite parent, int stepNumber){
		Group stepsGroup = new Group(parent, SWT.NONE);
		stepsGroup.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, true, 1 ,2);
		gridData.widthHint = 220;
		stepsGroup.setLayoutData(gridData);
		
		String steps[] = {"Step 1 - Optimization Goals  ",
						  "Step 2 - Available Resources  ",
						  "Step 3 - Frequency Type  ",
						  "Step 4 - SY Hopping MALs  ",
						  "Step 5 - Separation Rules  ",
						  "Step 6 - Scaling Rules  ",
						  "Step 7 - Summary  "};
		
		for (int i = 0; i < steps.length; i++){
			Label label = new Label(stepsGroup, SWT.LEFT_TO_RIGHT);
			label.setText(steps[i]);
			if (i == stepNumber - 1)
				makeFontBold(label);
		}
		
		
		return stepsGroup;
	}
	
	/*protected static String[][] getSummaryTableItems(){
		
		String rowHeaders[] = {"Selected Sectors: ",
							   "Selected TRXs: ",
							   "BCCH TRXs: ",
							   "TCH Non/BB Hopping TRXs: ",
							   "TCH SY HoppingTRXs: "};
		
		String[][] items = new String[rowHeaders.length][6];
		
		for (int i=0; i < items.length; i++){
			items[i][0] = rowHeaders[i];
		}
		
		int total = 0;

		//populate 1st row
		int[] sectorStats = getStatistics("Sector");
		
		for (int i = 0; i < sectorStats.length; i++){
			items[0][i+2] = Integer.toString(sectorStats[i]);
			total += sectorStats[i];
		}
		items[0][1] = Integer.toString(total);
		
		//populate 2nd row
		total = 0;
		int[] selectedTRXStats = getStatistics("SelectedTRX");
		
		for (int i = 0; i < selectedTRXStats.length; i++){
			items[1][i+2] = Integer.toString(selectedTRXStats[i]);
			total += selectedTRXStats[i];
		}
		items[1][1] = Integer.toString(total);
		
		//populate 3rd row
		total = 0;
		int[] bcchTRXStats = getStatistics("BCCHTRX");
		
		for (int i = 0; i < bcchTRXStats.length; i++){
			items[2][i+2] = Integer.toString(bcchTRXStats[i]);
			total += bcchTRXStats[i];
		}
		items[2][1] = Integer.toString(total);
		
		//populate 4th row
		total = 0;
		int[] nonBBTRXStats = getStatistics("NonBBTRX");
		
		for (int i = 0; i < nonBBTRXStats.length; i++){
			items[3][i+2] = Integer.toString(nonBBTRXStats[i]);
			total += nonBBTRXStats[i];
		}
		items[3][1] = Integer.toString(total);
		
		//populate 4th row
		total = 0;
		int[] sYTRXStats = getStatistics("SYTRX");
		
		for (int i = 0; i < sYTRXStats.length; i++){
			items[4][i+2] = Integer.toString(sYTRXStats[i]);
			total += sYTRXStats[i];
		}
		items[4][1] = Integer.toString(total);
		
		return items;
	}
	
	public static int[] getStatistics(String element){
		return new int[] {150, 150, 0, 0};
	}*/
	
	protected static void makeFontBold(Control label){
		FontData[] fD = label.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		Font font = new	Font(label.getDisplay(),fD[0]);
		label.setFont(font);
		font.dispose();
	}
	
	public static void createFrequencyDomainNode(Node afpNode, AfpFrequencyDomainModel domainModel, GraphDatabaseService service){
		Node frequencyNode = findOrCreateDomainNode(afpNode, INeoConstants.FREQUENCY_DOMAIN_NAME, domainModel.getName(), service);
        
        frequencyNode.setProperty(INeoConstants.PROPERTY_FREQUENCY_BAND_NAME, domainModel.getBand());
        frequencyNode.setProperty(INeoConstants.PROPERTY_FREQUENCIES_NAME, domainModel.getFrequencies());
	}
	
	public static void createHoppingMALDomainNode(Node afpNode, AfpHoppingMALDomainModel domainModel, GraphDatabaseService service){
		Node malNode = findOrCreateDomainNode(afpNode, INeoConstants.MAL_DOMAIN_NAME, domainModel.getName(), service);
		malNode.setProperty(INeoConstants.PROPERTY_MAL_SIZE_NAME, domainModel.getMALSize());
	}
	
	public static void createSectorSeparationDomainNode(Node afpNode, AfpSeparationDomainModel domainModel, GraphDatabaseService service){
		Node separationNode = findOrCreateDomainNode(afpNode, INeoConstants.SECTOR_SEPARATION_DOMAIN_NAME, domainModel.getName(), service);
		separationNode.setProperty(INeoConstants.PROPERTY_SEPARATIONS_NAME, domainModel.getSeparations());
	}
	
	public static void createSiteSeparationDomainNode(Node afpNode, AfpSeparationDomainModel domainModel, GraphDatabaseService service){
		Node separationNode = findOrCreateDomainNode(afpNode, INeoConstants.SITE_SEPARATION_DOMAIN_NAME, domainModel.getName(), service);
		separationNode.setProperty(INeoConstants.PROPERTY_SEPARATIONS_NAME, domainModel.getSeparations());
	}
	
	public static void deleteDomainNode(Node afpNode, String domain, String name, GraphDatabaseService service){
		//TODO implement this method
	}
	
	public static Node findOrCreateDomainNode(Node afpNode, String domain, String name, GraphDatabaseService service){
		Node domainNode = null;
		
		Traverser traverser = afpNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

			@Override
			public boolean isReturnableNode(TraversalPosition currentPos) {
				if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.AFP_DOMAIN))
					return true;
				return false;
			}
    		
    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		
		for (Node node : traverser) {
        	if (node.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(name) &&
        			node.getProperty(INeoConstants.PROPERTY_DOMAIN_NAME).equals(domain))
        		domainNode = node;
        }
		
		if (domainNode == null){
			domainNode = service.createNode();
    		NodeTypes.AFP_DOMAIN.setNodeType(domainNode, service);
            NeoUtils.setNodeName(domainNode, name, service);
            domainNode.setProperty(INeoConstants.PROPERTY_DOMAIN_NAME, domain);
            afpNode.createRelationshipTo(domainNode, NetworkRelationshipTypes.CHILD);
		}
		
		return domainNode;

	}
	
	protected static void createFrequencySelector(Shell parentShell, Text frequenciesText, String frequencies[]){
		final Text frequenciesTextLocal = frequenciesText;
		int numSelected = 0;
		String[] frequenciesLeft = null;
		String[] selectedRanges = new String[]{};
		if (!frequenciesText.getText().trim().equals(""))
			selectedRanges = frequenciesText.getText().split(",");
		
		if (selectedRanges.length > 0 && selectedRanges[0] != null && !selectedRanges[0].trim().equals("")){
			String[] selected = rangeArraytoArray(selectedRanges);
			numSelected = selected.length;
			frequenciesLeft = new String[frequencies.length - selected.length];
			
			Arrays.sort(selected);
			int i = 0;
			for (String item: frequencies){
				if (Arrays.binarySearch(selected, item) < 0){
					frequenciesLeft[i] = item;
					i++;
				}		
			}
		}
		else {
			frequenciesLeft = frequencies;
		}
		
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
		subShell.setText("Frequency Selector");
		subShell.setLayout(new GridLayout(3, false));
		subShell.setLocation(200, 200);
		
		Group freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(3, false));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,3 ,1));
		freqGroup.setText("Frequency Selector");
		Label freqLabel = new Label (freqGroup, SWT.LEFT);
		freqLabel.setText("Frequencies");
		freqLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
		
		Label selectionLabel = new Label (freqGroup, SWT.LEFT);
		//TODO update this label on selection and removal of frequencies
		selectionLabel.setText(numSelected + " Frequencies Selected");
		selectionLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));
		
		final List selectedList = createListSelector(freqGroup, frequenciesLeft, selectedRanges, selectionLabel);
		
		/*	selectedList.add(new SelectionAdapter() {
			
		});*/
		Button selectButton = new Button(subShell, SWT.PUSH);
		selectButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 2, 1));
		selectButton.setText("Select");
		selectButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selected[] = selectedList.getItems();
				String selectedString = "";
				for (int i = 0; i< selected.length; i++){
					if (i == selected.length - 1)
						selectedString += selected[i];
					else 
						selectedString += selected[i] + ",";
				}
				frequenciesTextLocal.setText(selectedString);
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
	
	protected static void createFrequencyDomainShell(Shell parentShell, final String action, final Group parentGroup, final AfpModel model){
		
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
		final AfpFrequencyDomainModel domainModel = new AfpFrequencyDomainModel();
		
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
			Combo nameCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			//TODO populate combo values
			nameCombo.setItems(model.getAllFrequencyDomainNames());
			nameCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
			nameCombo.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Combo)e.widget).getText();
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
		}
		
		
		
		Group freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(3, false));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,3 ,1));
		freqGroup.setText("Frequency Selector");
		
		Label bandLabel = new Label(freqGroup, SWT.LEFT);
		bandLabel.setText("Band");
		bandLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,3 ,1));
		
		final Combo bandCombo = new Combo(freqGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		//TODO populate combo values
		bandCombo.setItems(model.getAvailableBands());
		bandCombo.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,3 ,1));
		
		Label freqLabel = new Label (freqGroup, SWT.LEFT);
		freqLabel.setText("Frequencies");
		freqLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
		
		Label selectionLabel = new Label (freqGroup, SWT.LEFT);
		//TODO update this label on selection and removal of frequencies
		selectionLabel.setText("0 Frequencies Selected");
		selectionLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));
		
		//TODO update the frequencies on basis of selection
		String frequencies[] = new String[885-512+1];
		for (int i = 0; i < frequencies.length; i++){
			frequencies[i] = Integer.toString(512 + i); 
		}
		List selectedList = createListSelector(freqGroup, frequencies, new String[0], selectionLabel);
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
					Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
					newDomainLabel.setText(domainName);
					Label domainFreqLabel = new Label(parentGroup, SWT.LEFT);
					domainFreqLabel.setText(Integer.toString(rangeArraytoArray(selectedArray).length));
					Label domainTRXLabel = new Label(parentGroup, SWT.LEFT);
					//TODO Do something for TRX
					domainTRXLabel.setText("todo");
					AfpFrequencyTypePage.domainLabels.put(domainName, new Label[]{newDomainLabel, domainFreqLabel, domainTRXLabel});
					parentGroup.layout();
				}
				//TODO add for edit and delete buttons
				
				if (action.equals("Delete")){
					AfpFrequencyDomainModel domainModel = model.findFreqDomain(domainName);
					
					if (domainModel == null){
						//TODO Do some error handling here;
					}
					model.deleteFreqDomain(domainModel);
					AfpFrequencyTypePage.deleteDomainLabels(domainName);
					parentGroup.layout();
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
	
	
	/**
	 * Creates a list GUI to select from the given list on the parent group
	 * @param parentGroup
	 * @param leftList
	 * @return
	 */
	private static List createListSelector(Group parentGroup, String[] leftList, String[] rightList, Label selectionLabel){
		
		int numSelected = 0;
		final Label thisSelectionLabel = selectionLabel;
		final List freqList = new List(parentGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2);
		int listHeight = freqList.getItemHeight() * 12;
		int listWidth = selectionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		Rectangle trim = freqList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		gridData.widthHint = listWidth;
		freqList.setLayoutData(gridData);
		freqList.setItems(leftList);
		
		Button rightArrowButton = new Button (parentGroup, SWT.ARROW | SWT.RIGHT | SWT.BORDER);
		GridData arrowGridData = new GridData(GridData.FILL, GridData.END, true, false,1 ,1);
		arrowGridData.verticalIndent = trim.height/2;
		rightArrowButton.setLayoutData(arrowGridData);
		
		
		final List selectedList = new List(parentGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
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
					String array[] = rangeArraytoArray(selectedList.getItems());
					String selected[] = arrayToRangeArray(array);
					selectedArray = selected;
					selectedList.setItems(selected);
					thisSelectionLabel.setText("" + array.length + " Frequencies selected");
				}
				
			}
		});
		
		
		Button leftArrowButton = new Button (parentGroup, SWT.ARROW | SWT.LEFT | SWT.BORDER);
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
					String array[] = rangeArraytoArray(selectedArray);
					thisSelectionLabel.setText("" + array.length + " Frequencies selected");
					String notSelected[] = rangeArraytoArray(freqList.getItems());
					freqList.setItems(notSelected);
				}
				
			}
		});
		
		return selectedList;
	}
	
	/**
	 * Converts string array containing integer values to string array containing int values and/or ranges (wherever applicable)
	 * For example {"0","2","4","8","9","10","12","13","15","16","17","18","19","20", "22"} is converted to {"0","2","4","8-10","12","13","15-20", "22"}
	 * @param array An string array containing string representations of int values (no ranges)
	 * @return
	 */
	public static String[] arrayToRangeArray(String array[]){

		int lastItem = -1;
		int rangeFirstItem = -1;
		String range = null;
		boolean isRange = false;
		int[] rangeArray = new int[array.length];
		
		for (int i = 0; i < array.length; i++){
			rangeArray[i] = Integer.parseInt(array[i].trim());
		}
		
		Arrays.sort(rangeArray);
		
		ArrayList<String> list = new ArrayList<String>();
		for (int currItem : rangeArray){
			if (lastItem >= 0 && currItem == lastItem + 1){
				range = "" + rangeFirstItem + "-" +  currItem;
				isRange = true;
				lastItem = currItem;
			}
			else {
				rangeFirstItem = currItem;
				if (isRange){
					list.add(range);
					isRange = false;
				}
				
				else if (lastItem >= 0)
					list.add(Integer.toString(lastItem));
				lastItem = currItem;
			}
		}
		if (isRange)
			list.add(range);
		else list.add(Integer.toString(lastItem));
		
	
		return list.toArray(new String[0]);
	}
	
	/**
	 * Converts string array containing integer values and ranges to string array containing int values only
	 * For example {"0","2","4","8-10","12","13","15-20", "22"} is converted to {"0","2","4","8","9","10","12","13","15","16","17","18","19","20", "22"} 
	 * @param rangeArray string array containing string representations of int and/or ranges (eg. 9-12 implies 9,10,11,12) 
	 * @return sorted string array containing only string representations of int and no ranges.
	 */
	public static String[] rangeArraytoArray(String[] rangeArray){
		ArrayList<String> list = new ArrayList<String>();
		for (String item : rangeArray){
			int index = item.indexOf("-");
			if (index == -1){
				list.add(item);
			}
			else{
				int start = Integer.parseInt(item.substring(0,index).trim());
				int end = Integer.parseInt(item.substring(index + 1).trim());
				for (int i = start; i<= end; i++){
					list.add(Integer.toString(i));
				}
			}
		}
		
		String[] stringArray = new String[list.size()];
		int[] intArray = new int[list.size()];
		list.toArray(stringArray);
		for (int i = 0; i < stringArray.length; i++){
			intArray[i] = Integer.parseInt(stringArray[i].trim());
		}
		
		Arrays.sort(intArray);
		for (int i = 0; i < intArray.length; i++){
			stringArray[i] = Integer.toString(intArray[i]);
		}
		
		return stringArray;
	}

	public static void main(String args[]){
//		String[] array = {"0","2","4","8","9","10","12","13","15","16","17","18","19","20", "22"};
//		String[] rangeArray = arrayToRangeArray(array);
//		String[] rangeArray = {"0","2","4","8-10","12","13","15-20", "22"};
//		String[] array = rangeArraytoArray(rangeArray);
//		for (String item : array)
//			System.out.println(item);
		
	}
	
	protected static void getTRXFilterGroup(Group main){
		
		final Shell parentShell = main.getShell();
		
		/** Create TRXs Filters Group */
    	Group trxFilterGroup = new Group(main, SWT.NONE);
    	trxFilterGroup.setLayout(new GridLayout(4, false));
    	trxFilterGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,1 ,2));
    	trxFilterGroup.setText("TRXs Filter");
    	
    	//TODO: edit this label
    	new Label(trxFilterGroup, SWT.LEFT).setText("Filter Status: x Trxs selected out of y");
    	
    	
    	Button loadButton = new Button(trxFilterGroup, SWT.RIGHT);
    	loadButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 1 , 1));
    	loadButton.setText("Load");
    	loadButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO write function for shell
				
			}
    		
    	});
    	
    	Button clearButton = new Button(trxFilterGroup, SWT.RIGHT);
    	clearButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1 , 1));
    	clearButton.setText("Clear");
    	clearButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO write function for shell
				
			}
    		
    	});
    	
    	Button assignButton = new Button(trxFilterGroup, SWT.RIGHT);
    	assignButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1 , 1));
    	assignButton.setText("Assign");
    	assignButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO write function for shell
    			final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
    			subShell.setText("Assign to Domain");
    			subShell.setLayout(new GridLayout(2, false));
    			subShell.setLocation(200, 200);
    			
    			Label infoLabel = new Label (subShell, SWT.LEFT);
    			//TODO update label to show correct no. of TRXs
    			infoLabel.setText("Selected x TRXs will be assigned to:");
    			infoLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
    			
    			Label domainLabel = new Label (subShell, SWT.LEFT);
    			domainLabel.setText("Select Domain");
    			domainLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
    			
    			Combo domainCombo = new Combo(subShell, SWT.DROP_DOWN);
    			//TODO populate combo values
    			domainCombo.setItems(new String[]{"Dummy"});
    			domainCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
    			domainCombo.addModifyListener(new ModifyListener(){

    				@Override
    				public void modifyText(ModifyEvent e) {
    					// TODO Auto-generated method stub
    					
    				}
    				
    			});
    			
    			domainCombo.addSelectionListener(new SelectionListener(){

    				@Override
    				public void widgetDefaultSelected(SelectionEvent e) {
    					// TODO Auto-generated method stub
    					
    				}

    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					widgetSelected(e);
    				}
    				
    			});
    			
    			Button selectButton = new Button(subShell, SWT.PUSH);
    			selectButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
    			selectButton.setText("Assign");
    			selectButton.addSelectionListener(new SelectionAdapter(){
    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					//TODO do something here
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
    		
    	});
    	

		Table filterTable = new Table(trxFilterGroup, SWT.VIRTUAL | SWT.MULTI);
		filterTable.setHeaderVisible(true);
		filterTable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 4 ,1));
		
		String[] headers = { "BSC", "Site", "Sector", "Layer", "Subcell", "TRX_ID", "Band", "Extended", "Hopping Type", "Not clear"};
	    for (String item : headers) {
	      TableColumn column = new TableColumn(filterTable, SWT.NONE);
	      column.setText(item);
	    }
	    
		
	    /**
	     *  TODO Write Code here to populate the table
	    String[][] items = new String[10][headers.length];
	    for (int i = 0; i < items.length; i++){
	    	TableItem item = new TableItem(filterTable, SWT.NONE);
	    	for (int j = 0; j < items[i].length; j++){
	    		item.setText(j, items[i][j]);
	    	}
	    }
	    
	    */
	    for (int i = 0; i < headers.length; i++) {
	    	filterTable.getColumn(i).pack();
	    }

	}
	
	protected static void createButtonsGroup(final Group parentGroup, String caller, final AfpModel model){

		final Shell parentShell = parentGroup.getShell();
		final String thisCaller = caller;
		
		Group buttonsGroup = new Group(parentGroup, SWT.NONE);
    	buttonsGroup.setLayout(new GridLayout(1, false));
    	buttonsGroup.setLayoutData(new GridData(GridData.END, GridData.FILL, false, true, 1 , 10));
    	Button addButton = new Button(buttonsGroup, GridData.BEGINNING);
    	addButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1 , 1));
    	addButton.setText("Add Domain");
    	addButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				AfpWizardUtils.createFrequencyDomainShell(parentShell, "Add", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				AfpWizardUtils.createMalDomainShell(parentShell, "Add", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Add", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Add", false, parentGroup, model);
			}
    		
    	});
    	
    	Button editButton = new Button(buttonsGroup, GridData.BEGINNING);
    	editButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	editButton.setText("Edit Domain");
    	editButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				AfpWizardUtils.createFrequencyDomainShell(parentShell, "Edit", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				AfpWizardUtils.createMalDomainShell(parentShell, "Edit", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Edit", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Edit", false, parentGroup, model);
			}
    		
    	});
    	
    	Button deleteButton = new Button(buttonsGroup, GridData.BEGINNING);
    	deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1 , 1));
    	deleteButton.setText("Delete Domain");
    	deleteButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				AfpWizardUtils.createFrequencyDomainShell(parentShell, "Delete", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				AfpWizardUtils.createMalDomainShell(parentShell, "Delete", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Delete", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				AfpWizardUtils.createSeparationDomainShell(parentShell, "Delete", false, parentGroup, model);
				
			}
    		
    	});
	}
	
	protected static void createMalDomainShell(Shell parentShell, final String action, final Group parentGroup, final AfpModel model){
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
		final AfpHoppingMALDomainModel domainModel = new AfpHoppingMALDomainModel(); 
		
		subShell.setText(action +  " MAL Domain");
		subShell.setLayout(new GridLayout(2, false));
		subShell.setLocation(200, 100);
		
		Label nameLabel = new Label(subShell, SWT.NONE);
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		nameLabel.setText("Domain Name");

		if (action.equals("Add")){
			Text nameText = new Text (subShell, SWT.BORDER | SWT.SINGLE);
			nameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameText.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Text)e.widget).getText();
				}
				
			});
		}
		
		if (action.equals("Edit") || action.equals("Delete")){
			Combo nameCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			//TODO populate combo values
			nameCombo.setItems(model.getAllMALDomainNames());
			nameCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameCombo.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Combo)e.widget).getText();
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
		}
		
		
		
		Group rulesGroup = new Group(subShell, SWT.NONE);
		rulesGroup.setLayout(new GridLayout(2, false));
		rulesGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,2 ,1));
		rulesGroup.setText("MAL Size Rules");
		
		Label trxLabel = new Label(rulesGroup, SWT.LEFT);
		trxLabel.setText("Hopping TRXs");
		makeFontBold(trxLabel);
		
		Label sizeLabel = new Label(rulesGroup, SWT.LEFT);
		sizeLabel.setText("MAL Size");
		makeFontBold(sizeLabel);
		
		new Label(rulesGroup, SWT.CENTER).setText("1");
		Text text1 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text1.setText("3");
		//TODO add some mechanism to have only one listener class for all these buttons.
		text1.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				int size = Integer.parseInt(((Text)e.widget).getText()); 
				if (size < 1)
					actionButton.setEnabled(false);
				domainModel.setMALSize0(size);	
			}
			
		});
		
		new Label(rulesGroup, SWT.CENTER).setText("2");
		Text text2 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text2.setText("3");
		
		new Label(rulesGroup, SWT.CENTER).setText("3");
		Text text3 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text3.setText("4");
		
		new Label(rulesGroup, SWT.CENTER).setText("4");
		Text text4 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text4.setText("4");
		
		new Label(rulesGroup, SWT.CENTER).setText("5");
		Text text5 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text5.setText("5");
		
		new Label(rulesGroup, SWT.CENTER).setText("6");
		Text text6 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text6.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text6.setText("6");
		
		new Label(rulesGroup, SWT.CENTER).setText("7");
		Text text7 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text7.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text7.setText("7");
		
		new Label(rulesGroup, SWT.CENTER).setText("8");
		Text text8 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text8.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text8.setText("8");
		
		new Label(rulesGroup, SWT.CENTER).setText("9");
		Text text9 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text9.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text9.setText("9");
		
		new Label(rulesGroup, SWT.CENTER).setText("10");
		Text text10 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text10.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text10.setText("10");
		
		new Label(rulesGroup, SWT.CENTER).setText("11");
		Text text11 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text11.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text11.setText("11");
		
		new Label(rulesGroup, SWT.CENTER).setText("12");
		Text text12 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text12.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text12.setText("12");
		
		new Label(rulesGroup, SWT.CENTER).setText("13");
		Text text13 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text13.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text13.setText("13");
		
		
		
		actionButton = new Button(subShell, SWT.PUSH);
		actionButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		
		actionButton.setText(action);
		actionButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (action.equals("Add")){
					domainModel.setName(domainName);
					model.addMALDomain(domainModel);
					Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
					newDomainLabel.setText(domainName);
					Label domainTRXLabel = new Label(parentGroup, SWT.LEFT);
					//TODO Do something for TRX
					domainTRXLabel.setText("todo");
					AfpSYHoppingMALsPage.domainLabels.put(domainName, new Label[]{newDomainLabel, domainTRXLabel});
					parentGroup.layout();

					
				}
				//TODO add for edit and delete
				
				if (action.equals("Delete")){
					AfpHoppingMALDomainModel domainModel = model.findMALDomain(domainName);
					
					if (domainModel == null){
						//TODO Do some error handling here;
					}
					model.deleteMALDomain(domainModel);
					AfpSYHoppingMALsPage.deleteDomainLabels(domainName);
					parentGroup.layout();
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
	
	protected static void createSeparationDomainShell(Shell parentShell, final String action, final boolean isSector, final Group parentGroup, final AfpModel model){
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL);
		final AfpSeparationDomainModel domainModel = new AfpSeparationDomainModel(); 
		String entity = isSector? "Sector" : "Site";
		String title = action + " " + entity + " Separation Domain";	
		                
		subShell.setText(title);
		subShell.setLayout(new GridLayout(2, false));
		subShell.setLocation(200, 100);
		
		Label nameLabel = new Label(subShell, SWT.NONE);
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		nameLabel.setText("Domain Name");

		if (action.equals("Add")){
			Text nameText = new Text (subShell, SWT.BORDER | SWT.SINGLE);
			nameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameText.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Text)e.widget).getText();
				}
				
			});
		}
		
		if (action.equals("Edit") || action.equals("Delete")){
			Combo nameCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			//TODO populate combo values
			if (isSector)
				nameCombo.setItems(model.getAllSectorSeparationDomainNames());
			else
				nameCombo.setItems(model.getAllSiteSeparationDomainNames());
			
			nameCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameCombo.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Combo)e.widget).getText();
					
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
		}
		
		
		
		Group rulesGroup = new Group(subShell, SWT.NONE);
		rulesGroup.setLayout(new GridLayout(3, true));
		rulesGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 10));
		
		rulesGroup.setText(entity + " Separation Rules");
		
		Label servingLabel = new Label(rulesGroup, SWT.LEFT);
		servingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		servingLabel.setText("Serving");
		makeFontBold(servingLabel);
		
		Label interferingLabel = new Label(rulesGroup, SWT.LEFT);
		interferingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		interferingLabel.setText("Interfering");
		makeFontBold(interferingLabel);
		
		Label separationLabel = new Label(rulesGroup, SWT.LEFT);
		separationLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		separationLabel.setText("Separation");
		makeFontBold(separationLabel);
		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text1 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text1.setText("NA");
		//TODO add some mechanism to have only one listener class for all these texts.
		text1.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				domainModel.setSeparation0(((Text)e.widget).getText());	
			}
			
		});

		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text2 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text2.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		Text text3 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text3.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text4 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text4.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text5 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text5.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		Text text6 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text6.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text6.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text7 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text7.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text7.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text8 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text8.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text8.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text9 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text9.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text9.setText("2");
		
		
		Button actionButton = new Button(subShell, SWT.PUSH);
		actionButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		actionButton.setText(action);
		actionButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO do something here
				if (action.equals("Add")){
					domainModel.setName(domainName);
					if (isSector){
						model.addSectorSeparationDomain(domainModel);
						Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
						newDomainLabel.setText(domainName);
						Label domainSectorsLabel = new Label(parentGroup, SWT.LEFT);
						//TODO Do something for TRX
						domainSectorsLabel.setText("todo");
						AfpSeparationRulesPage.sectorDomainLabels.put(domainName, new Label[]{newDomainLabel, domainSectorsLabel});
						parentGroup.layout();
					}
					else{
						model.addSiteSeparationDomain(domainModel);
						Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
						newDomainLabel.setText(domainName);
						Label domainSiteLabel = new Label(parentGroup, SWT.LEFT);
						//TODO Do something for TRX
						domainSiteLabel.setText("todo");
						AfpSeparationRulesPage.siteDomainLabels.put(domainName, new Label[]{newDomainLabel, domainSiteLabel});
						parentGroup.layout();
					}
					
				}
				
				//TODO add for edit and delete
				
				if (action.equals("Delete")){
					if (isSector){
						AfpSeparationDomainModel domainModel = model.findSectorSeparationDomain(domainName);
						
						if (domainModel == null){
							//TODO Do some error handling here;
						}
						model.deleteSectorSeparationDomain(domainModel);
						AfpSeparationRulesPage.deleteSectorDomainLabels(domainName);
						parentGroup.layout();
					}
					else{
						AfpSeparationDomainModel domainModel = model.findSiteSeparationDomain(domainName);
						
						if (domainModel == null){
							//TODO Do some error handling here;
						}
						model.deleteSiteSeparationDomain(domainModel);
						AfpSeparationRulesPage.deleteSiteDomainLabels(domainName);
						parentGroup.layout();
					}
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
	

}
