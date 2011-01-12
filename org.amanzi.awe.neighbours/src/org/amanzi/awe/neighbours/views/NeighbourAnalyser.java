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
package org.amanzi.awe.neighbours.views;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.INeoServiceProviderListener;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

import au.com.bytecode.opencsv.CSVWriter;


public class NeighbourAnalyser extends ViewPart  implements INeoServiceProviderListener {
    //TODO ZNN need main solution for using class NeoServiceProviderListener instead of interface INeoServiceProviderListener  

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.amanzi.awe.neighbours.views.NeighbourAnalyser"; //$NON-NLS-1$
    private Composite mainFrame;
    private Text text0;
    private Text text1;
    private Text text2;
    private Text text3;
    private Text text4;
    private Combo cGpeh;
    private Combo cNeighbour;
    private Button bStart;
    private LinkedHashMap<String, Node> gpeh;
    private GraphDatabaseService graphDatabaseService=NeoServiceProviderUi.getProvider().getService();
    private LinkedHashMap<String, Node> neighbour;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
    public void createPartControl(Composite parent) {
	    mainFrame = new Composite(parent, SWT.FILL);
	    mainFrame.setLayout(new GridLayout(3  ,false));
	    Label label=new Label(mainFrame,SWT.NONE);
	    label.setText(Messages.NeighbourAnalyser_0);
	    text0 = new Text(mainFrame, SWT.BORDER);
	    GridData layoutData = new GridData();	    
	    layoutData.minimumWidth=50;
	    text0.setLayoutData(layoutData);
	    label=new Label(mainFrame, SWT.LEFT);
	    label.setText(Messages.NeighbourAnalyser_0_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_1);
        text1 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_1_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_2);
        text2 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_2_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_3);
        label.setToolTipText(Messages.NeighbourAnalyser_3_1);
        text3 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_3_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_4);
        text4 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_4_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_5);
        cGpeh= new Combo(mainFrame,SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData=new GridData();
        layoutData.horizontalSpan=2;
        layoutData.grabExcessHorizontalSpace=true;
        layoutData.minimumWidth=200;
        cGpeh.setLayoutData(layoutData);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_6);
        cNeighbour= new Combo(mainFrame, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData=new GridData();
        layoutData.horizontalSpan=2;
        layoutData.grabExcessHorizontalSpace=true;
        layoutData.minimumWidth=200;
        cNeighbour.setLayoutData(layoutData);
        
        bStart=new Button(mainFrame, SWT.PUSH);
        bStart.setText(Messages.NeighbourAnalyser_7);

        
        init();
        addListeners();
	}



	/**
     *initialize
     */
    private void init() {
        formGPEH();
        formNeighbour();
        validateStartButton();
    }
    /**
     * validate start button
     */
    private void validateStartButton(){
        bStart.setEnabled(isValidInput());
    }


    /**
     *is input valid?
     * @return result
     */
    private boolean isValidInput() {
        return gpeh.get(cGpeh.getText())!=null;
    }



    /**
     *forms Neighbour list
     */
    private void formNeighbour() {
        neighbour=new LinkedHashMap<String,Node>();
        Transaction tx = graphDatabaseService.beginTx();
        try{
            Traverser gisWithNeighbour = NeoUtils.getAllReferenceChild(graphDatabaseService, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node= currentPos.currentNode();
                    return NeoUtils.isGisNode(node)&&GisTypes.NETWORK==NeoUtils.getGisType(node, null);
                }
            });
            for (Node node:gisWithNeighbour){
                neighbour.put((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME), node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getOtherNode(node));
            }
        }finally{
            tx.finish();
        }
        String[] result = neighbour.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cNeighbour.setItems(result);
    }



    /**
     *
     */
    private void formGPEH() {
        gpeh=new LinkedHashMap<String,Node>();
        for (Node node:NeoUtils.getAllGpeh(graphDatabaseService)){
            gpeh.put(NeoUtils.getNodeName(node), node);
        }
        String[] result = gpeh.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cGpeh.setItems(result);
    }



    /**
     *add listeners
     */
    private void addListeners() {
        bStart.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                startAnalyse();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cGpeh.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateStartButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cNeighbour.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateStartButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }



    /**
     *
     */
    protected void startAnalyse() {
        final Node gpehNode=gpeh.get(cGpeh.getText());
        final Node netNode=neighbour.get(cNeighbour.getText());
        Job job=new Job("analyse") {
            
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                analyse(gpehNode,netNode,monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }



    /**
     * 
     * @param gpehNode
     * @param netNode 
     * @param monitor 
     * @return
     */
    protected void analyse(Node gpehNode, Node netNode, IProgressMonitor monitor) {
        Transaction tx = graphDatabaseService.beginTx();
        try {
            Node gisNode = NeoUtils.findGisNodeByChild(netNode);
            // Node otherNode=null;
            // for (Relationship relation :
            // gisNode.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA,
            // Direction.OUTGOING)) {
            // otherNode = relation.getOtherNode(gisNode);
            // if ((NeoUtils.getSimpleNodeName(otherNode, null).equals("utran relation"))){
            // break;
            // }
            // }
            String[] fields = PropertyHeader.getPropertyStatistic(gisNode).getNeighbourAllFields("utran relation");
            Traverser traverse = netNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
               Node node=currentPos.currentNode();
               for (Relationship rel:node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR,Direction.OUTGOING)){
                   if (rel.getProperty(INeoConstants.NEIGHBOUR_NAME,"").equals("utran relation")){
                       return true;
                   }
               }
                return false;
            }
        }, GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING,GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING);
            try {
                CSVWriter out = new CSVWriter(new FileWriter("c:/utranRelation.csv"));
                List<String> outList = new LinkedList<String>();
                outList.add("Serv cell name");
                outList.add("Neighbour cell name");
                outList.addAll(Arrays.asList(fields));
                out.writeNext(outList.toArray(new String[0]));
                for (Node servNode : traverse) {

                    String result = (String)servNode.getProperty("userLabel", "");
                    if (StringUtil.isEmpty(result)) {
                        result = NeoUtils.getNodeName(servNode);
                    }
                    for (Relationship rel : NeoUtils.getNeighbourRelations(servNode, "utran relation", NetworkRelationshipTypes.NEIGHBOUR)) {
                        Node neigh = rel.getOtherNode(servNode);
                        outList.clear();
                        outList.add(result);
                        result = (String)neigh.getProperty("userLabel", "");
                        if (StringUtil.isEmpty(result)) {
                            result = NeoUtils.getNodeName(neigh);
                        }
                        outList.add(result);
                        for (String field : fields) {
                            outList.add(rel.getProperty(field, "").toString());
                        }
                        out.writeNext(outList.toArray(new String[0]));
                    }

                }
                out.close();

            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        } finally {
            tx.finish();
        }
        // AnalyseModel model=AnalyseModel.create(gpehNode,neo);
        // final SpreadsheetNode spreadsheet = model.createSpreadSheet("event",neo,monitor);
        // ActionUtil.getInstance().runTask(new Runnable() {
        //            
        // @Override
        // public void run() {
        // NeoSplashUtil.openSpreadsheet(spreadsheet);
        // }
        // },true);

    }



    private void showMessage(String message) {
		MessageDialog.openInformation(
		        mainFrame.getShell(),
			"NeighbourAnalyser", //$NON-NLS-1$
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
    public void setFocus() {
	}
	
    @Override
    public void onNeoStop(Object source) {
        graphDatabaseService = null;
    }

    @Override
    public void onNeoStart(Object source) {
        graphDatabaseService = NeoServiceProviderUi.getProvider().getService();
    }

    @Override
    public void onNeoCommit(Object source) {
    }

    @Override
    public void onNeoRollback(Object source) {
    }

	
}