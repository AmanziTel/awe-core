package org.amanzi.awe.afp.wizards;

import java.util.HashMap;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class AfpLoadNetworkPage extends WizardPage {
	
	private final GraphDatabaseService service;
	private Combo networkCombo;
	protected String datasetName;
	private HashMap<String, Node> networkNodes;
	private HashMap<String, Node> afpNodes;
	protected static Node datasetNode;
	protected static Node afpNode;
	
	//private Combo afpCombo;
	protected static String afpName = "afp-dataset";
	
	private AfpModel model;
	
	public AfpLoadNetworkPage(String pageName, GraphDatabaseService servise, AfpModel model) {
        super(pageName);
        this.service = servise;
        this.model = model;
        setPageComplete(false);
        setTitle(AfpImportWizard.title);
        setDescription(AfpImportWizard.page0Name);
    }
	
	@Override
	public void createControl(Composite parent) {
		
		Group main = new Group(parent, SWT.FILL);
		main.setLayout(new GridLayout(2, false));
		
		new Label(main, SWT.LEFT).setText("Network: ");
        
		networkCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		networkCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
		networkCombo.setItems(getNetworkDatasets());
//		networkCombo.addModifyListener(new ModifyListener(){
//
//			@Override
//			public void modifyText(ModifyEvent e) {
//				datasetName = networkCombo.getText();
//				datasetNode = networkNodes.get(datasetName);
//				setPageComplete(canFlipToNextPage());
//			}
//			
//		});
		
		networkCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	datasetName = networkCombo.getText().trim();
            	datasetNode = networkNodes.get(datasetName);
            	//afpCombo.setItems(getAfpDatasets(datasetNode));
				setPageComplete(canFlipToNextPage());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
		
		/*
		new Label(main, SWT.LEFT).setText("Afp Dataset: ");
        
		afpCombo = new Combo(main, SWT.DROP_DOWN);
		afpCombo.setLayoutData(new GridData(GridData.FILL, SWT.BEGINNING, true, false));
		
		afpCombo.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				afpName = afpCombo.getText().trim();
				afpNode = afpNodes.get(afpName);
				if (afpName == null || afpName.equals(""))
					setErrorMessage("No Afp Name Specified");
				else setErrorMessage(null);
				setPageComplete(canFlipToNextPage());
			}
			
		});
		
		afpCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	afpName = afpCombo.getText().trim();
            	afpNode = afpNodes.get(afpName);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
		*/
		setPageComplete(true);
		setControl(main);
	}
	
	@Override
    public void setVisible(boolean visible) {
        
        super.setVisible(visible);
    }
	
	/**
     * Gets the networ datasets.
     * 
     * @return the network datasets
     */
    private String[] getNetworkDatasets() {
        networkNodes = new HashMap<String, Node>();
//        Transaction tx = service.beginTx();
//        try {
            for (Node root : NeoUtils.getAllRootTraverser(service, null)) {
            	
                if (NodeTypes.NETWORK.checkNode(root)) {
                    networkNodes.put(NeoUtils.getNodeName(root, service), root);
                }
            }
//        } finally {
//            tx.finish();
//        }
        return networkNodes.keySet().toArray(new String[0]);
    }
    
    private String[] getAfpDatasets(Node networkNode) {
        afpNodes = new HashMap<String, Node>();
        Transaction tx = service.beginTx();
        try {
        	Traverser traverser = networkNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){

				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(NodeTypes.AFP))
						return true;
					return false;
				}
        		
        	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
            for (Node afpNode : traverser) {
            	
                if (NodeTypes.AFP.checkNode(afpNode)) {
                    afpNodes.put(NeoUtils.getNodeName(afpNode, service), afpNode);
                }
            }
        } finally {
            tx.finish();
        }
        return afpNodes.keySet().toArray(new String[0]);
    }
    
    
    @Override
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
        
        if (StringUtils.isEmpty(datasetName)) {
            return false;
        }
        
        if (datasetNode == null) {
            Node root = NeoUtils.findRootNodeByName(datasetName, service);
            if (root != null) {
                return false;
            }
        }
        
        if (afpName == null || afpName.trim().equals(""))
        	return false;
        
        // load afp nodes for the n/w dataset
        
        getAfpDatasets(datasetNode);

        if(afpNodes != null) {
        	afpNode = afpNodes.get(afpName);
        }
        model.setAfpNode(afpNode);
        return true;
    }

}
