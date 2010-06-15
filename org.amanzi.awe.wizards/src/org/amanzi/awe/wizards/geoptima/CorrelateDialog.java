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

package org.amanzi.awe.wizards.geoptima;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.amanzi.awe.gps.GPSCorrelator;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoTreeElement;
import org.amanzi.neo.core.utils.NeoTreeLabelProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Correlate dialog
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CorrelateDialog extends Dialog implements IPropertyChangeListener {
    private String property;
    private int status;
    private Combo cNetwork;
    private CheckboxTableViewer tableView;
    private final GraphDatabaseService service;
    private Button bCorrelate;
    private NeoTableContentProvider contentProvider;
    private final Map<String, Node> networks = new TreeMap<String, Node>();
    private final Set<Node> addCorrelate = new HashSet<Node>();
    private final Set<Node> removeCorrelate = new HashSet<Node>();
    private final Set<Node> corelatedNodes = new HashSet<Node>();
    private Shell shell;

    /**
     * @param parent
     */
    public CorrelateDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        service = NeoServiceProvider.getProvider().getService();
        property = null;
    }

    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText("Correlate");

        createContents(shell);
        shell.pack();

        // calculate location
        Point size = parentShell.getSize();
        int dlgWidth = shell.getSize().x;
        int dlgHeight = shell.getSize().y;
        shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
        NeoLoaderPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
        formInput();
        shell.open();
        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        NeoLoaderPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
        return status;
    }

    /**
     *
     */
    private void formInput() {
        property = getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        tableView.setInput(property);
        formNetworks();
    }

    /**
     *
     */
    private void formNetworks() {
        corelatedNodes.clear();
        addCorrelate.clear();
        removeCorrelate.clear();
        tableView.setAllChecked(false);
        networks.clear();
        if (property != null) {
            StringTokenizer st = new StringTokenizer(property, DataLoadPreferences.CRS_DELIMETERS);
            while (st.hasMoreTokens()) {
                String nodeId = st.nextToken();
                Node node = service.getNodeById(Long.parseLong(nodeId));
                if (NodeTypes.NETWORK.checkNode(node)) {
                    networks.put(NeoUtils.getNodeName(node), node);
                }
            }
        }
        cNetwork.setItems(networks.keySet().toArray(new String[0]));
        validateCorrelateButton();
    }


    private void createContents(final Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, false));
        Label label = new Label(shell, SWT.NONE);
        label.setText("Network:");
        cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = 200;
        cNetwork.setLayoutData(layoutData);
        tableView = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        data.heightHint = 200;
        data.widthHint = 200;
        tableView.getControl().setLayoutData(data);
        createTable(tableView);
        contentProvider = new NeoTableContentProvider();
        NeoTreeLabelProvider labelProvider = new NeoTreeLabelProvider();
        tableView.setContentProvider(contentProvider);
        tableView.setLabelProvider(labelProvider);
        bCorrelate = new Button(shell, SWT.PUSH);
        bCorrelate.setText("Correlate");
        bCorrelate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                correlate();
            }

        });
        cNetwork.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setNetwork(cNetwork.getText());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("OK");
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.END;
        gdBtnCancel.widthHint = 80;
        btnOk.setLayoutData(gdBtnCancel);
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                shell.close();
            }

        });
        tableView.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Node networkNode = networks.get(cNetwork.getText());
                if (networkNode != null) {
                    changeSetted(networkNode, ((TableElem)event.getElement()).getNode(), event.getChecked());
                }
            }
        });

    }

    private void validateCorrelateButton() {
        final Node network = networks.get(cNetwork.getText());
        bCorrelate.setEnabled(network != null && (!addCorrelate.isEmpty() || !removeCorrelate.isEmpty()));
    }

    protected void changeSetted(Node networkNode, Node dataset, boolean checked) {
        if (checked) {
            removeCorrelate.remove(dataset);
            if (!corelatedNodes.contains(dataset)) {
                addCorrelate.add(dataset);
            }
        } else {
            addCorrelate.remove(dataset);
            if (corelatedNodes.contains(dataset)) {
                removeCorrelate.add(dataset);
            }
        }
        validateCorrelateButton();
    }

    /**
     * @param text
     */
    protected void setNetwork(String networkName) {
        addCorrelate.clear();
        removeCorrelate.clear();
        corelatedNodes.clear();
        Node networkNode = networks.get(networkName);
        tableView.setAllChecked(false);
        if (networkNode != null) {
            TableElem[] elements = getCorrelatedElements(networkNode);
            if (elements != null) {
                tableView.setCheckedElements(elements);
            }
        }
        validateCorrelateButton();
    }

    private TableElem[] getCorrelatedElements(Node networkNode) {
        corelatedNodes.clear();
        Transaction tx = service.beginTx();
        try{
            Traverser traverse = NeoUtils.getAllCorrelatedDatasets(networkNode, service);
            for (Node dataset:traverse){
                corelatedNodes.add(dataset);
            }
        }finally{
            tx.finish();
        }
        List<TableElem>elem=new ArrayList<TableElem>();
        for (Node node:corelatedNodes){
            elem.add(new TableElem(node, service));
        }
        return elem.toArray(new TableElem[0]);
    }

    protected void correlate() {
        final Node network = networks.get(cNetwork.getText());
        final Node[] removeCorr = removeCorrelate.toArray(new Node[0]);
        if (network == null || (addCorrelate.size() == 0 && removeCorr.length == 0)) {
            return;
        }
        shell.setEnabled(false);
        Job correlate = new Job("process correlation") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    removeCorrelation(network, removeCorr);
                    addCorrelation(network, addCorrelate, monitor);
                    return Status.OK_STATUS;
                } finally {
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            shell.setEnabled(true);
                            updateCorrelation();
                        }

                    }, true);
                }
            }

        };
        correlate.schedule();
    }

    protected void addCorrelation(Node network, Set<Node> addCorr, IProgressMonitor monitor) {
        GPSCorrelator correlator = new GPSCorrelator(network, monitor);
        correlator.correlate(addCorr);
    }

    protected void removeCorrelation(Node network, Node[] removeCorr) {
        // TODO implement
    }

    private void updateCorrelation() {
        setNetwork(cNetwork.getText());
    }

    /**
     * @param tableView2
     */
    private void createTable(TableViewer tableView) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(285);
        column.setText("Set to correlate");
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    private class NeoTableContentProvider implements IStructuredContentProvider {
        LinkedHashSet<TableElem> elements = new LinkedHashSet<TableElem>();

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new TableElem[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                Transaction tx = service.beginTx();
                try {
                    String nodeSet = (String)newInput;
                    StringTokenizer st = new StringTokenizer(nodeSet, DataLoadPreferences.CRS_DELIMETERS);
                    while (st.hasMoreTokens()) {
                        String nodeId = st.nextToken();
                        Node node = service.getNodeById(Long.parseLong(nodeId));
                        if (!NodeTypes.NETWORK.checkNode(node)) {
                            elements.add(new TableElem(node, service));
                        }
                    }
                } finally {
                    tx.finish();
                }
            }
        }

    }

    private static class TableElem extends NeoTreeElement {

        public TableElem(Node node, GraphDatabaseService service) {
            super(node, service);
        }

        @Override
        public NeoTreeElement[] getChildren() {
            return null;
        }

        @Override
        public NeoTreeElement getParent() {
            return null;
        }

        @Override
        public boolean hasChildren() {
            return false;
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (property != getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA)) {
            formInput();
        }
    }

    /**
     * Returs preference store
     * 
     * @return IPreferenceStore
     */
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}
