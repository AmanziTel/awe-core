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

package org.amanzi.awe.neighbours.dialog;

import java.util.LinkedHashMap;

import org.amanzi.awe.neighbours.gpeh.GpehReportCreator;
import org.amanzi.awe.neighbours.gpeh.GpehReportType;
import org.amanzi.awe.neighbours.views.Messages;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Dialog for GPEG report
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportDialog extends Dialog {
    
    /** The status. */
    protected int status = SWT.CANCEL;
    
    /** The c gpeh. */
    private Combo cGpeh;
    
    /** The c network. */
    private Combo cNetwork;
    
    /** The btn save. */
    private Button btnSave;
    
    /** The gpeh. */
    private LinkedHashMap<String, Node> gpeh;
    
    /** The neo. */
    private final GraphDatabaseService neo = NeoServiceProvider.getProvider().getService();
    
    /** The network. */
    private LinkedHashMap<String, Node> network;
    
    /** The c report type. */
    private Combo cReportType;

    /**
     * Instantiates a new gpeh report dialog.
     *
     * @param parent the parent
     */
    public GpehReportDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
    }

    /**
     * Open.
     *
     * @return the int
     */
    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText("Generate GPEH events");

        createContents(shell);
        shell.pack();

        // calculate location
        // Point size = parentShell.getSize();
        // int dlgWidth = shell.getSize().x;
        // int dlgHeight = shell.getSize().y;
        // shell.setLocation(100, 100);
        shell.open();

        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return status;
    }

    /**
     * Creates the contents.
     *
     * @param shell the shell
     * @return the control
     */
    private Control createContents(final Shell shell) {
        shell.setLayout(new GridLayout(3, false));

        Label label = new Label(shell, SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_5);
        cGpeh = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cGpeh.setLayoutData(layoutData);

        label = new Label(shell, SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_6);
        cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cNetwork.setLayoutData(layoutData);

        label = new Label(shell, SWT.NONE);
        label.setText("Report type");
        cReportType = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cReportType.setLayoutData(layoutData);
        
        btnSave=new Button(shell, SWT.PUSH);
        btnSave.setText(Messages.NeighbourAnalyser_7);
        btnSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                generateReport();
                status = SWT.OK;
                shell.close();
            }

        });
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.CENTER;
        btnCancel.setLayoutData(gdBtnCancel);
        btnCancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }

        });
        SelectionListener listener = new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateStartButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        cNetwork.addSelectionListener(listener);
        cGpeh.addSelectionListener(listener);
        cReportType.addSelectionListener(listener);
        init();
        return shell;
    }

    /**
     * Generate report.
     */
    protected void generateReport() {
        final Node gpehNode = gpeh.get(cGpeh.getText());
        final Node netNode = network.get(cNetwork.getText());
        Job job = new Job("generate Report") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                createReport(gpehNode, netNode, monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    /**
     * Creates the report.
     *
     * @param gpehNode the gpeh node
     * @param netNode the net node
     * @param monitor the monitor
     */
    protected void createReport(Node gpehNode, Node netNode, IProgressMonitor monitor) {
        GpehReportCreator creator = new GpehReportCreator(netNode, gpehNode, NeoServiceProvider.getProvider().getService(), NeoServiceProvider.getProvider()
                .getIndexService());
        creator.setMonitor(monitor);
        creator.createMatrix();
        final SpreadsheetNode spreadsheet = creator.createIntaIDCMSpreadSheet("IntraMatrix");
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                NeoSplashUtil.openSpreadsheet(spreadsheet);
            }
        }, true);
    }

    /**
     * initialize.
     */
    private void init() {
        formGPEH();
        formNetwork();
        formReportType();
        validateStartButton();
    }


    /**
     * Form report type.
     */
    private void formReportType() {
        String[] gpeh = new String[GpehReportType.values().length];
        int i = 0;
        for (GpehReportType report : GpehReportType.values()) {
            gpeh[i++] = report.getId();
        }
        cReportType.setItems(gpeh);
    }

    /**
     * validate start button.
     */
    private void validateStartButton() {
        btnSave.setEnabled(isValidInput());
    }

    /**
     * is input valid?.
     *
     * @return result
     */
    private boolean isValidInput() {
        return gpeh.get(cGpeh.getText()) != null && network.get(cNetwork.getText()) != null&&GpehReportType.getEnumById(cReportType.getText())!=null;
    }

    /**
     * forms Networks list.
     */
    private void formNetwork() {
        network = new LinkedHashMap<String, Node>();
        Transaction tx = neo.beginTx();
        try {
            Traverser gisWithNeighbour = NeoUtils.getAllReferenceChild(neo, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    return NeoUtils.isGisNode(node) && GisTypes.NETWORK == NeoUtils.getGisType(node, null);
                }
            });
            for (Node node : gisWithNeighbour) {
                network.put((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME), node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
                        .getOtherNode(node));
            }
        } finally {
            tx.finish();
        }
        cNetwork.setItems(network.keySet().toArray(new String[0]));
    }


    /**
     * Form gpeh.
     */
    private void formGPEH() {
        gpeh = new LinkedHashMap<String, Node>();
        for (Node node : NeoUtils.getAllGpeh(neo)) {
            gpeh.put(NeoUtils.getSimpleNodeName(node, ""), node);
        }
        cGpeh.setItems(gpeh.keySet().toArray(new String[0]));
    }

}
