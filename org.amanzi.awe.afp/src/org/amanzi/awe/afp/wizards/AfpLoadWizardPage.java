/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.afp.wizards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.awe.afp.loaders.AfpLoader;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.NeighbourLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.wizards.FileFieldEditorExt;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Page for loading afp data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpLoadWizardPage extends WizardPage {
    private static final Logger LOGGER = Logger.getLogger(AfpLoadWizardPage.class);
    private Combo dataset;
    protected String datasetName;
    private HashMap<String, Node> members;
    protected Node datasetNode;
    private FileFieldEditorExt editor;
    private String fileName;
    private final GraphDatabaseService service;
    protected ControlFile controlFile = null;
    
    private GridData gridData;
    private HashMap<String, Node> neighbourLists;
    private HashMap<String, Node> exceptionLists;
    private HashMap<String, Node> interferenceLists;
    private HashMap<String, Node> selectedLists;

	private Text siteSpacing;
	private Text cellSpacing;
	private Text regNbrSpacing;
	private Text minNbrSpacing;
	private Text secondNbrSpacing;
	private Scale qualityScale;
	private Text gMaxRTperCell;
	private Text gMaxRTperSite;
	private Text hoppingType;
	private Text nrOfGroups;
	private Text cellCardinality;
	private Text carriers;
	private Button useGrouping;
	private Button existCliques;
	private Button recalculateAll;
	private Button useTraffic;
	private Button useSONbrs;
	private Button decomposeInCliques;
    private Combo neighbourData;
    private Combo exceptionsData;
    private Combo interferenceData;
    
    private FileFieldEditorExt neighbourEditor;
    private FileFieldEditorExt interferenceEditor;
    private FileFieldEditorExt exceptionEditor;
    
    private Composite parent1;

    /**
     * Instantiates a new afp load wizard page.
     * 
     * @param pageName the page name
     * @param servise the servise
     */
    public AfpLoadWizardPage(String pageName, GraphDatabaseService servise) {
        super(pageName, "Load AFP data", null);
        this.service = servise;
    }

    @Override
    public void createControl(Composite parent) {
    	parent1 = parent;
    	
    	TabFolder tabFolder =new TabFolder(parent, SWT.NONE | SWT.BORDER);
    	TabItem item1 =new TabItem(tabFolder,SWT.NONE);
		item1.setText("Control File");
		
        Group main = new Group(tabFolder, SWT.FILL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText("AFP Dataset");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        dataset = new Combo(main, SWT.DROP_DOWN);
        dataset.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        dataset.setItems(getAfpDatasets());
        dataset.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                datasetName = dataset.getText();
                datasetNode = members.get(datasetName);
                setPageComplete(isValidPage());
                
                siteSpacing.setText(datasetNode.getProperty("SiteSpacing").toString());
                cellSpacing.setText(datasetNode.getProperty("CellSpacing").toString());
                regNbrSpacing.setText(datasetNode.getProperty("RegNbrSpacing").toString());
                minNbrSpacing.setText(datasetNode.getProperty("MinNbrSpacing").toString());
                secondNbrSpacing.setText(datasetNode.getProperty("SecondNbrSpacing").toString());
                qualityScale.setSelection(Integer.parseInt(datasetNode.getProperty("Quality").toString()));
                gMaxRTperCell.setText(datasetNode.getProperty("GMaxRTperCell").toString());
                gMaxRTperSite.setText(datasetNode.getProperty("GMaxRTperSite").toString());
                hoppingType.setText(datasetNode.getProperty("HoppingType").toString());
                nrOfGroups.setText(datasetNode.getProperty("NrOfGroups").toString());
                cellCardinality.setText(datasetNode.getProperty("CellCardinality").toString());
                carriers.setText(datasetNode.getProperty("Carriers").toString());
                useGrouping.setSelection(datasetNode.getProperty("UseGrouping").toString().equals("1"));
                existCliques.setSelection(datasetNode.getProperty("ExistCliques").toString().equals("1"));
                recalculateAll.setSelection(datasetNode.getProperty("RecalculateAll").toString().equals("1"));
                useTraffic.setSelection(datasetNode.getProperty("UseTraffic").toString().equals("1"));
                useSONbrs.setSelection(datasetNode.getProperty("UseSONbrs").toString().equals("1"));
                decomposeInCliques.setSelection(datasetNode.getProperty("DecomposeInCliques").toString().equals("1"));
                
                populateFileLists();
                neighbourData.setItems(neighbourLists.keySet().toArray(new String[0]));
                neighbourData.setText(neighbourData.getItem(0));
                exceptionsData.setItems(exceptionLists.keySet().toArray(new String[0]));
                exceptionsData.setText(exceptionsData.getItem(0));
                interferenceData.setItems(interferenceLists.keySet().toArray(new String[0]));
                interferenceData.setText(interferenceData.getItem(0));
            }
        });
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                datasetName = dataset.getText();
                datasetNode = members.get(datasetName);
                setPageComplete(isValidPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        Button importButton = new Button(main, SWT.PUSH);
        importButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 2, 1));
        importButton.setText("Import New");
        importButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String fileName = new FileDialog(parent1.getShell()).open();
				if (fileName != null) {
					setFileName(fileName);
					datasetName = new java.io.File(getFileName()).getName();					
					
					
					new Job("load AFP data"){

			            @Override
			            protected IStatus run(IProgressMonitor monitor) {
			            	AfpLoader loader = new AfpLoader(datasetName, controlFile, service);
			                try {
			                    loader.run(monitor);
			                } catch (IOException e) {
			                    AweConsolePlugin.exception(e);
			                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
			                }
			                return Status.OK_STATUS;
			            }
			            
			        }.schedule();
			        parent1.getShell().dispose();
			        dataset.setItems(getAfpDatasets());
			        datasetNode = members.get(datasetName);
			        dataset.setText(datasetName);
					
					setPageComplete(isValidPage());
				}
			}
		});
//        editor = new FileFieldEditorExt("fileSelectNeighb", NeoLoaderPluginMessages.NetworkSiteImportWizard_FILE, main); // NON-NLS-1 //$NON-NLS-1$
//        editor.setDefaulDirectory(NeighbourLoader.getDirectory());
//
//        editor.getTextControl(main).addModifyListener(new ModifyListener() {
//            public void modifyText(ModifyEvent e) {
//                setFileName(editor.getStringValue());
//                if (StringUtils.isEmpty(datasetName)) {
//                    datasetName = new java.io.File(getFileName()).getName();
//                    dataset.setText(datasetName);
//                    datasetNode = members.get(datasetName);
//                    setPageComplete(isValidPage());
//                }
//            }
//        });
//        editor.setFileExtensions(new String[] {"*.*"});
//        editor.setFileExtensionNames(new String[] {"All Fiels(*.*)"});
//        editor.setFocus();
        
        item1.setControl(main);
        
        
        TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Properties");
		
		Group propertiesGroup = new Group(tabFolder, SWT.FILL);
        propertiesGroup.setLayout(new GridLayout(2, true));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Site Spacing ");
        siteSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Cell Spacing");
        cellSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Reg Neighbour Spacing");
        regNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Min Neighbour Spacing");
        minNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Second Neighbour Spacing");
        secondNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
       
        
        new Label(propertiesGroup, SWT.LEFT).setText("Quality");
        qualityScale = new Scale (propertiesGroup, SWT.BORDER);
        qualityScale.setPageIncrement(10);
    	qualityScale.setMaximum (100);
    	
        new Label(propertiesGroup, SWT.LEFT).setText("G Max RT per Cell");
        gMaxRTperCell = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("G Max RT per Site");
        gMaxRTperSite = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Hopping Type");
        hoppingType = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Number of Groups");
        nrOfGroups = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        new Label(propertiesGroup, SWT.LEFT).setText("Cell Cardinality");
        cellCardinality = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        //TODO show carriers in some other format
        new Label(propertiesGroup, SWT.LEFT).setText("Carriers");
        carriers = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        
        useGrouping = new Button(propertiesGroup, SWT.CHECK);
        useGrouping.setText("Use Grouping");

    	existCliques = new Button(propertiesGroup, SWT.CHECK);
    	existCliques.setText("Exist Cliques");        
        
        recalculateAll = new Button(propertiesGroup, SWT.CHECK);
        recalculateAll.setText("Recalculate All");
        
        useTraffic = new Button(propertiesGroup, SWT.CHECK);
        useTraffic.setText("Use Traffic");
        
        useSONbrs = new Button(propertiesGroup, SWT.CHECK);
        useSONbrs.setText("Use SO Neighbours");
        
        decomposeInCliques = new Button(propertiesGroup, SWT.CHECK);
        decomposeInCliques.setText("Decompose In Cliques");
        
        item2.setControl(propertiesGroup);
        
        
        TabItem item3 =new TabItem(tabFolder,SWT.NONE);
		item3.setText("Files");
		
		Group filesGroup = new Group(tabFolder, SWT.FILL);
		filesGroup.setLayout(new GridLayout(3, true));
        
        new Label(filesGroup, SWT.LEFT).setText("Neighbours: ");
        
        neighbourData = new Combo(filesGroup, SWT.DROP_DOWN);
        gridData = new GridData(GridData.FILL, SWT.CENTER, true, false);
        gridData.horizontalSpan = 2;
        neighbourData.setLayoutData(gridData);
        
        neighbourData.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedLists.put(neighbourData.getText(), neighbourLists.get(neighbourData.getText()));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        
        neighbourEditor = new FileFieldEditorExt("fileSelectNeighb", "Import File", filesGroup); 
        neighbourEditor.setDefaulDirectory(NeighbourLoader.getDirectory());
        neighbourEditor.setFileExtensions(new String[] {"*.*"});
        neighbourEditor.setFileExtensionNames(new String[] {"All Fiels(*.*)"});
        neighbourEditor.setEnabled(false, filesGroup);


        new Label(filesGroup, SWT.LEFT).setText("Exceptions: ");
        exceptionsData = new Combo(filesGroup, SWT.DROP_DOWN);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.horizontalSpan = 2;
        exceptionsData.setLayoutData(gridData);
        exceptionsData.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedLists.put(exceptionsData.getText(), exceptionLists.get(exceptionsData.getText()));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        
        exceptionEditor = new FileFieldEditorExt("fileSelectException", "Import File", filesGroup);
        exceptionEditor.setDefaulDirectory(NeighbourLoader.getDirectory());
        exceptionEditor.setFileExtensions(new String[] {"*.*"});
        exceptionEditor.setFileExtensionNames(new String[] {"All Fiels(*.*)"});
        exceptionEditor.setEnabled(false, filesGroup);
        
        
        new Label(filesGroup, SWT.LEFT).setText("Interference: ");
        interferenceData = new Combo(filesGroup, SWT.DROP_DOWN);
        gridData = new GridData(GridData.FILL, SWT.CENTER, true, false);
        gridData.horizontalSpan = 2;
        interferenceData.setLayoutData(gridData);
        interferenceData.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedLists.put(interferenceData.getText(), interferenceLists.get(interferenceData.getText()));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        
        interferenceEditor = new FileFieldEditorExt("fileSelectInterf", "Import File", filesGroup); 
        interferenceEditor.setDefaulDirectory(NeighbourLoader.getDirectory());
        interferenceEditor.setFileExtensions(new String[] {"*.*"});
        interferenceEditor.setFileExtensionNames(new String[] {"All Fiels(*.*)"});
        interferenceEditor.setEnabled(false, filesGroup);
        
        
        item3.setControl(filesGroup);

		
		setControl(parent);
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
        if (controlFile == null && members.isEmpty()) {
            return false;
        }
        
        if (StringUtils.isEmpty(datasetName)) {
            return false;
        }
        if (datasetNode == null) {
            Node root = NeoUtils.findRootNodeByName(datasetName, service);
            if (root != null) {
                return false;
            }
        }
        
        if (datasetNode != null && getFileName() != null){
        	AweConsolePlugin.error("This database has already been imported. You can select it from the drop down menu");
        	
        	return false;
        }
        	
        
        return true;
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        try {
            controlFile = new ControlFile(new File(fileName));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            controlFile = null;
        }
        setPageComplete(isValidPage());
        // editor.store();
//        NeighbourLoader.setDirectory(editor.getDefaulDirectory());
    }

    /**
     * Gets the afp datasets.
     * 
     * @return the afp datasets
     */
    private String[] getAfpDatasets() {
        members = new HashMap<String, Node>();
        Transaction tx = service.beginTx();
        try {
            for (Node root : NeoUtils.getAllRootTraverser(service, null)) {
            	
                if (NodeTypes.NETWORK.checkNode(root)) {
                    members.put(NeoUtils.getNodeName(root, service), root);
                }
            }
        } finally {
            tx.finish();
        }
        return members.keySet().toArray(new String[0]);
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }
    
    private void populateFileLists(){
    	neighbourLists = new HashMap<String, Node>();
    	exceptionLists = new HashMap<String, Node>();
    	interferenceLists = new HashMap<String, Node>();
    	
    	for (Node neighbour : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING))
    		neighbourLists.put(NeoUtils.getNodeName(neighbour, service), neighbour);
    	
    	for (Node exception : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.EXCEPTION_DATA, Direction.OUTGOING))
    		exceptionLists.put(NeoUtils.getNodeName(exception, service), exception);
    	
    	for (Node interferer : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.INTERFERENCE_DATA, Direction.OUTGOING))
    		interferenceLists.put(NeoUtils.getNodeName(interferer, service), interferer);
    }
    
    /*private String[] getNeighbourLists(){
		neighbourLists = new HashMap<String, Node>();
            
    	for (Node neighbour : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING)){
    		neighbourLists.put(NeoUtils.getNodeName(neighbour, service), neighbour);
    	}
    	
        return neighbourLists.keySet().toArray(new String[0]);
	}
    
    private String[] getExceptionLists(){
		exceptionLists = new HashMap<String, Node>();
    	for (Node exception : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.EXCEPTION_DATA, Direction.OUTGOING)){
    		exceptionLists.put(NeoUtils.getNodeName(exception, service), exception);
       	}
        
        return exceptionLists.keySet().toArray(new String[0]);
	}
    
    private String[] getInterferenceLists(){
		interferenceLists = new HashMap<String, Node>();
    	for (Node interferer : datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.INTERFERENCE_DATA, Direction.OUTGOING)){
    		interferenceLists.put(NeoUtils.getNodeName(interferer, service), interferer);
       	}
        
        return interferenceLists.keySet().toArray(new String[0]);
	}*/
    
    

}
