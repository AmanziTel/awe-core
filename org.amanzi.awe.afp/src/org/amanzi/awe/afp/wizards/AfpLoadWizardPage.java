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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.ControlFileProperties;
import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.awe.afp.loaders.AfpLoader;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.wizards.FileFieldEditorExt;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FileFieldEditor;
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
    private String file;
    private final GraphDatabaseService service;
    protected ControlFile controlFile = null;
    
    private GridData gridData;
    private HashMap<String, Node> neighbourLists;
    private HashMap<String, Node> exceptionLists;
    private HashMap<String, Node> interferenceLists;
    private HashMap<String, Node> selectedLists;

	protected Text siteSpacing;
	protected Text cellSpacing;
	protected Text regNbrSpacing;
	protected Text minNbrSpacing;
	protected Text secondNbrSpacing;
	protected Scale qualityScale;
	protected Text gMaxRTperCell;
	protected Text gMaxRTperSite;
	protected Text hoppingType;
	protected Text nrOfGroups;
	protected Text cellCardinality;
	protected Text carriers;
	protected Button useGrouping;
	protected Button existCliques;
	protected Button recalculateAll;
	protected Button useTraffic;
	protected Button useSONbrs;
	protected Button decomposeInCliques;
	private Button neighbourButton;
	private Button exceptionsButton;
	private Button interferenceButton;
    private Combo neighbourData;
    private Combo exceptionsData;
    private Combo interferenceData;
    
    private FileFieldEditorExt neighbourEditor;
    private FileFieldEditorExt interferenceEditor;
    private FileFieldEditorExt exceptionEditor;
    
    private ModifyListener modlistener;
    
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
        modlistener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                datasetName = dataset.getText();
                datasetNode = members.get(datasetName);
                setPageComplete(isValidPage());
                
                siteSpacing.setText(datasetNode.getProperty(ControlFileProperties.SITE_SPACING).toString());
                cellSpacing.setText(datasetNode.getProperty(ControlFileProperties.CELL_SPACING).toString());
                regNbrSpacing.setText(datasetNode.getProperty(ControlFileProperties.REG_NBR_SPACING).toString());
                minNbrSpacing.setText(datasetNode.getProperty(ControlFileProperties.MIN_NEIGBOUR_SPACING).toString());
                secondNbrSpacing.setText(datasetNode.getProperty(ControlFileProperties.SECOND_NEIGHBOUR_SPACING).toString());
                qualityScale.setSelection(Integer.parseInt(datasetNode.getProperty(ControlFileProperties.QUALITY).toString()));
                gMaxRTperCell.setText(datasetNode.getProperty(ControlFileProperties.G_MAX_RT_PER_CELL).toString());
                gMaxRTperSite.setText(datasetNode.getProperty(ControlFileProperties.G_MAX_RT_PER_SITE).toString());
                hoppingType.setText(datasetNode.getProperty(ControlFileProperties.HOPPING_TYPE).toString());
                nrOfGroups.setText(datasetNode.getProperty(ControlFileProperties.NUM_GROUPS).toString());
                cellCardinality.setText(datasetNode.getProperty(ControlFileProperties.CELL_CARDINALITY).toString());
                carriers.setText(datasetNode.getProperty(ControlFileProperties.CARRIERS).toString().substring(2).replaceAll("\\s", ","));
                useGrouping.setSelection(datasetNode.getProperty(ControlFileProperties.USE_GROUPING).toString().equals("1"));
                existCliques.setSelection(datasetNode.getProperty(ControlFileProperties.EXIST_CLIQUES).toString().equals("1"));
                recalculateAll.setSelection(datasetNode.getProperty(ControlFileProperties.RECALCULATE_ALL).toString().equals("1"));
                useTraffic.setSelection(datasetNode.getProperty(ControlFileProperties.USE_TRAFFIC).toString().equals("1"));
                useSONbrs.setSelection(datasetNode.getProperty(ControlFileProperties.USE_SO_NEIGHBOURS).toString().equals("1"));
                decomposeInCliques.setSelection(datasetNode.getProperty(ControlFileProperties.DECOMPOSE_CLIQUES).toString().equals("1"));
                
                populateFileLists();
                neighbourData.setItems(neighbourLists.keySet().toArray(new String[0]));
                neighbourData.setText(neighbourData.getItem(0));
                neighbourButton.setEnabled(true);
                exceptionsData.setItems(exceptionLists.keySet().toArray(new String[0]));
                exceptionsData.setText(exceptionsData.getItem(0));
                exceptionsButton.setEnabled(true);
                interferenceData.setItems(interferenceLists.keySet().toArray(new String[0]));
                interferenceData.setText(interferenceData.getItem(0));
                exceptionsButton.setEnabled(true);
            }
        };
        dataset.addModifyListener(modlistener);
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
	
			        
			        ProgressMonitorDialog dialog = new ProgressMonitorDialog(parent1.getShell());
			        try {
			        	dialog.run(true, true, new IRunnableWithProgress(){
						    public void run(IProgressMonitor monitor) {
						    	AfpLoader loader = new AfpLoader(datasetName, controlFile, service);
				                try {
				                    loader.run(monitor);				                    
				                    
				                } catch (Exception e) {
				                    AweConsolePlugin.exception(e);
				                }
						    }
						});
					} catch (InvocationTargetException e) {
						AweConsolePlugin.exception(e);
					} catch (InterruptedException e) {
						AweConsolePlugin.exception(e);
					}
					
					try {
						dataset.removeModifyListener(modlistener);
						dataset.setItems(getAfpDatasets());
						dataset.addModifyListener(modlistener);
					}catch (Exception e){
						e.printStackTrace();
					}
			        datasetNode = members.get(datasetName);
			        dataset.setText(datasetName);
					
					setPageComplete(isValidPage());
				}
			}
		});
        
        item1.setControl(main);
        
        
        TabItem item2 =new TabItem(tabFolder,SWT.NONE);
		item2.setText("Properties");
		

		
		Group propertiesGroup = new Group(tabFolder, SWT.FILL);
        propertiesGroup.setLayout(new GridLayout(2, true));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Site Spacing ");
        siteSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        siteSpacing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Cell Spacing");
        cellSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        cellSpacing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Reg Neighbour Spacing");
        regNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        regNbrSpacing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Min Neighbour Spacing");
        minNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        minNbrSpacing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Second Neighbour Spacing");
        secondNbrSpacing = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        secondNbrSpacing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        
        new Label(propertiesGroup, SWT.LEFT).setText("Quality");
        qualityScale = new Scale (propertiesGroup, SWT.BORDER);
        qualityScale.setPageIncrement(10);
    	qualityScale.setMaximum (100);
    	
        new Label(propertiesGroup, SWT.LEFT).setText("G Max RT per Cell");
        gMaxRTperCell = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        gMaxRTperCell.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("G Max RT per Site");
        gMaxRTperSite = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        gMaxRTperSite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Hopping Type");
        hoppingType = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        hoppingType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Number of Groups");
        nrOfGroups = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        nrOfGroups.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        new Label(propertiesGroup, SWT.LEFT).setText("Cell Cardinality");
        cellCardinality = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        cellCardinality.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        //TODO show carriers in some other format
        new Label(propertiesGroup, SWT.LEFT).setText("Carriers (Comma-separated)");
        carriers = new Text (propertiesGroup, SWT.BORDER | SWT.SINGLE);
        carriers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
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
        
        setDefaultValues();	
        
        item2.setControl(propertiesGroup);
        
        
        //Tab 3 : Selection of Files
        TabItem item3 =new TabItem(tabFolder,SWT.NONE);
		item3.setText("Files");
		
		Group filesGroup = new Group(tabFolder, SWT.FILL);
		filesGroup.setLayout(new GridLayout(3, true));
        
        new Label(filesGroup, SWT.LEFT).setText("Neighbours: ");
        
        neighbourData = new Combo(filesGroup, SWT.DROP_DOWN);
        neighbourData.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
        
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
        
        neighbourButton = new Button(filesGroup, SWT.PUSH);
        neighbourButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        neighbourButton.setText("Import New");
        if (datasetNode != null)
        	neighbourButton.setEnabled(true);
        neighbourButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				showProgress();
				populateFileLists();
                neighbourData.setItems(neighbourLists.keySet().toArray(new String[0]));
                neighbourData.setText(neighbourData.getItem(0));
                
			}
		});
        

        new Label(filesGroup, SWT.LEFT).setText("Exceptions: ");
        exceptionsData = new Combo(filesGroup, SWT.DROP_DOWN);
        exceptionsData.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
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
        
        exceptionsButton = new Button(filesGroup, SWT.PUSH);
        exceptionsButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
        exceptionsButton.setText("Import New");
        if (datasetNode != null)
        	exceptionsButton.setEnabled(true);
        exceptionsButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				showProgress();
			}
		});

        
        
        new Label(filesGroup, SWT.LEFT).setText("Interference: ");
        interferenceData = new Combo(filesGroup, SWT.DROP_DOWN);
        interferenceData.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
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
        
        interferenceButton = new Button(filesGroup, SWT.PUSH);
        interferenceButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
        interferenceButton.setText("Import New");
        if (datasetNode != null)
        	interferenceButton.setEnabled(true);
        interferenceButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				showProgress();
			}
		});

        
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
    
    private void showProgress(){
    	file = new FileDialog(parent1.getShell()).open();
		if (file != null) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(parent1.getShell());
	        try {
	        	dialog.run(true, true, new IRunnableWithProgress(){
				    public void run(IProgressMonitor monitor) {
				    	AfpLoader loader = new AfpLoader(datasetName, controlFile, service);
				    	Transaction mainTx = service.beginTx();
				        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AfpLoader");
		                try {
		                	loader.loadNeighbourFile(new File(file), datasetNode);
			        		mainTx.success();				                    
		                    
		                } catch (Exception e) {
		                	mainTx.failure();
		                    AweConsolePlugin.exception(e);
		                } finally{
				        	if (mainTx != null) {
				                mainTx.finish();
				                mainTx = null;
				        	}
		                }
				    }
				});
			} catch (InvocationTargetException e) {
				AweConsolePlugin.exception(e);
			} catch (InterruptedException e) {
				AweConsolePlugin.exception(e);
			}
		}
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
    
    private void setDefaultValues(){
    	siteSpacing.setText("2");
    	cellSpacing.setText("3");
    	regNbrSpacing.setText("1");
    	minNbrSpacing.setText("0");
    	secondNbrSpacing.setText("1");
    	qualityScale.setMaximum (100);
    	gMaxRTperCell.setText("1");
    	gMaxRTperSite.setText("1");
    	hoppingType.setText("0");
    	nrOfGroups.setText("6");
    	cellCardinality.setText("61");
    	carriers.setText("1,2,3,4,5,6");
    	useGrouping.setSelection(true);
    	existCliques.setSelection(false);
    	recalculateAll.setSelection(true);
    	useTraffic.setSelection(true);
    	useSONbrs.setSelection(true);
    	decomposeInCliques.setSelection(false);
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
