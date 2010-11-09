package org.amanzi.awe.wizards.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.DriveTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.OssType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.neo4j.graphdb.Traverser.Order;

public class SelectCorrelationDataPage extends WizardPage {
	
	private Combo cmbNetwork;
	private Combo cmbGps;
	private Combo cmbOss;
	private Combo cmbGpeh;
	
	private HashMap<String, Node> networks;
	private HashMap<String, Node> gps;
	private HashMap<String, Node> oss;
	private HashMap<String, Node> gpeh;

	public SelectCorrelationDataPage(String pageName) {
		super(pageName);
        setTitle("Select data for correlation");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());
        
        Label lblSelectNetwork = new Label(container, SWT.LEFT);
        lblSelectNetwork.setText("Select network to work with:");

        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(0, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectNetwork.setLayoutData(formData);

        cmbNetwork = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectNetwork, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(0, 2);
        cmbNetwork.setLayoutData(formData);
        
        Label lblSelectGps = new Label(container, SWT.LEFT);
        lblSelectGps.setText("Select GPS data:");

        formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(cmbNetwork, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectGps.setLayoutData(formData);

        cmbGps = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectGps, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(cmbNetwork, 2);
        cmbGps.setLayoutData(formData);
        
        Label lblSelectOSS = new Label(container, SWT.LEFT);
        lblSelectOSS.setText("Select OSS Counters data:");

        formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(cmbGps, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectOSS.setLayoutData(formData);

        cmbOss = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectGps, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(cmbGps, 2);
        cmbOss.setLayoutData(formData);
        
        Label lblSelectGPEH = new Label(container, SWT.LEFT);
        lblSelectGPEH.setText("Select GPEH data:");

        formData = new FormData();
        formData.left = new FormAttachment(0, 2);
        formData.top = new FormAttachment(cmbOss, 2);
        formData.right = new FormAttachment(20, 2);
        lblSelectGPEH.setLayoutData(formData);

        cmbGpeh = new Combo(container, SWT.READ_ONLY);
        formData = new FormData();
        formData.left = new FormAttachment(lblSelectGps, 2);
        formData.right = new FormAttachment(100, -2);
        formData.top = new FormAttachment(cmbOss, 2);
        cmbGpeh.setLayoutData(formData);
        
        initializeCombos();

        setControl(container);
	}
	
	private void initializeCombos() {
		GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
		
		//GPS
		gps = NeoUtils.getAllDatasetNodesByType(DriveTypes.GPS, service);
		
		//GPEH
		gpeh = NeoUtils.getAllDatasetNodesByType(DriveTypes.OSS, service);
		
		//OSS
		oss = NeoUtils.getAllDatasetNodesByType(DriveTypes.IDEN, service);
		
		Transaction tx = service.beginTx();
		
		try {
			//Network			
			networks = new HashMap<String, Node>();
			
			Iterable<Node> networkNodes = service.getReferenceNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, 
											    new ReturnableEvaluator() {
													
													@Override
													public boolean isReturnableNode(TraversalPosition currentPos) {
														return currentPos.currentNode().hasProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME) && 
															   currentPos.currentNode().getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME).equals(GisTypes.NETWORK.getHeader());
													}
												},
												GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
			for (Node network : networkNodes) {
				networks.put(NeoUtils.getNodeName(network), network);
			}
		}
		finally {
			tx.success();
			tx.finish();
		}
		
		String[] networksArray = networks.keySet().toArray(new String[0]);
		Arrays.sort(networksArray);
        cmbNetwork.setItems(networksArray);
		String[] gpsArray = gps.keySet().toArray(new String[0]);
		Arrays.sort(gpsArray);
        cmbGps.setItems(gpsArray);
		String[] ossArray = oss.keySet().toArray(new String[0]);
		Arrays.sort(ossArray);
        cmbOss.setItems(ossArray);
        Arrays.sort(gpsArray);
		String[] gpehArray = gpeh.keySet().toArray(new String[0]);
        cmbGpeh.setItems(gpehArray);
		
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePageComplete();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);				
			}
		};
		cmbNetwork.addSelectionListener(listener);
		cmbGps.addSelectionListener(listener);
		cmbOss.addSelectionListener(listener);
		cmbGpeh.addSelectionListener(listener);
	}
	
	private void updatePageComplete() {
		setPageComplete(!cmbNetwork.getText().isEmpty() && 
						(!cmbGpeh.getText().isEmpty() ||
						 !cmbGps.getText().isEmpty() ||
						 !cmbOss.getText().isEmpty()));
	}

	public Node getNetworkGISNode() {
		return getSimpleGisNode(cmbNetwork, networks);
	}
	
	public Node getGPSGisNode() {
		return getGisNodeByDataset(cmbGps, gps);
	}
	
	public Node getGPEHGisNode() {
		return getGisNodeByDataset(cmbGpeh, gpeh);
	}
	
	public Node getOSSNode() {
		return getGisNodeByDataset(cmbOss, oss);
	}
	
	private Node getSimpleGisNode(Combo combo, HashMap<String, Node> map) {
		if (!combo.getText().isEmpty()) {
			return map.get(combo.getText());
		}
		return null;
	}
	
	private Node getGisNodeByDataset(Combo combo, HashMap<String, Node> map) {
		Node dataset = null;
		
		if (!combo.getText().isEmpty()) {
			dataset = map.get(combo.getText());
		}
		else {
			return null;
		}
		
		return NeoUtils.findGisNodeByChild(dataset, NeoServiceProviderUi.getProvider().getService());
	}
}
