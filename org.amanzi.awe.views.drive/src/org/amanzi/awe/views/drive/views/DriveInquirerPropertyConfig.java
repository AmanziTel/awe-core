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

package org.amanzi.awe.views.drive.views;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.propertyFilter.PropertyFilterModel;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Dialog for configuration property lists for selected drive
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DriveInquirerPropertyConfig extends AbstractDialog<Integer> {
    private static final Logger LOGGER = Logger.getLogger(DriveInquirerPropertyConfig.class);

    private final Node dataset;
    private Shell shell;
    private CheckboxTableViewer propertyListTable;
    private CheckboxTableViewer propertySlipTable;
    private Button bAddComposite;
    private Button bAddSingle;
    private Button bDel;
    private Button bOk;
    private Button bCancel;
    private Button bClear;

    private final Set<String> propertySet = new TreeSet<String>();
    private final Set<String> propertySlip = new TreeSet<String>();

    private final GraphDatabaseService service;

    /**
     * Constructor
     * 
     * @param parent
     * @param title
     */
    public DriveInquirerPropertyConfig(Shell parent, Node dataset) {
        super(parent, "Dataset properties configura\tion", SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.dataset = dataset;
        status = SWT.CANCEL;
        service = NeoServiceProvider.getProvider().getService();
    }

    @Override
    protected void createContents(final Shell shell) {
        this.shell = shell;
        shell.setImage(NodeTypes.DATASET.getImage());
        shell.setLayout(new GridLayout(2, false));

        propertyListTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 300;
        data.widthHint = 200;
        createTable(propertyListTable, "Avaliable properties");
        propertyListTable.getControl().setLayoutData(data);
        propertyListTable.setContentProvider(new PropertyListContentProvider());
        propertyListTable.setLabelProvider(new PropertyListLabelProvider());

        Table table = new Table(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        propertySlipTable = new CheckboxTableViewer(table);
        data = new GridData(SWT.NO, SWT.FILL, true, true);
        data.heightHint = 300;
        data.widthHint = 200;
        createTable(propertySlipTable, "Active properties");
        propertySlipTable.getControl().setLayoutData(data);
        propertySlipTable.setContentProvider(new PropertySlipContentProvider());
        propertySlipTable.setLabelProvider(new PropertyListLabelProvider());

        Group gr1 = new Group(shell, SWT.NULL);
        gr1.setText("Add checked properties to \"active\" list");
        gr1.setLayout(new GridLayout(2, false));
        gr1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group gr2 = new Group(shell, SWT.NULL);
        gr2.setText("Delete properies from \"active\" list");
        gr2.setLayout(new GridLayout(2, false));
        gr2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        new Label(shell, SWT.NULL);

        Composite gr3 = new Composite(shell, SWT.NULL);
        gr3.setLayout(new GridLayout(2, false));
        gr3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        bAddComposite = createButton(gr1, "As composite");
        bAddSingle = createButton(gr1, "All by one");

        bDel = createButton(gr2, "Delete selected");
        bClear = createButton(gr2, "Clear list");

        bOk = createButton(gr3, "OK");
        bCancel = createButton(gr3, "Cancel");

        addListeners();
        init();
        propertyListTable.setInput("");
        propertySlipTable.setInput("");
        loadSavedData();
    }

    /**
     * Create button
     * 
     * @param parent parent composite
     * @param name visible name
     * @return Button
     */
    private Button createButton(Composite parent, String name) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(name);
        button.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return button;
    }

    /**
     * Action perform deleting of selected properties
     */
    protected void deleteProperty() {
        IStructuredSelection selection = (IStructuredSelection)propertySlipTable.getSelection();
        propertySlip.removeAll(selection.toList());
        propertySlipTable.refresh();
    }

    /**
     * Action perform adding all selected properties to active list
     */
    protected void addSingle() {
        Object[] checkedElements = propertyListTable.getCheckedElements();
        if (checkedElements.length > 0) {
            for (Object checked : checkedElements) {
                propertySlip.add(checked.toString());
            }
            propertySlipTable.refresh();
        }
    }

    /**
     * Action perform adding all selected properties as single composite property
     */
    protected void addComposite() {
        Object[] checkedElements = propertyListTable.getCheckedElements();
        if (checkedElements.length > 0) {
            StringBuilder newComposit = new StringBuilder("");
            for (Object checked : checkedElements) {
                newComposit.append(checked).append(", ");
            }
            String result = newComposit.substring(0, newComposit.length() - 2);
            propertySlip.add(result);
            propertySlipTable.refresh();
        }
    }

    /**
     * Loading saved selected properties from PROPERTY node
     */
    private void loadSavedData() {
        Object[] savedProperties = null;
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Traverser tr = dataset.traverse(Order.BREADTH_FIRST, NeoUtils.getStopEvaluator(2), new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Relationship rel = currentPos.lastRelationshipTraversed();
                    return rel != null && rel.isType(GeoNeoRelationshipTypes.PROPERTIES);
                }
            }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);

            savedProperties = (Object[])tr.iterator().next().getProperty(INeoConstants.PROPERTY_NAME_SELECTED_PROPERTIES, null);
            tx.success();
        } catch (Exception ex) {
            tx.failure();
            NeoCorePlugin.error(null, ex);
        } finally {
            NeoUtils.finishTx(tx);
        }
        if (savedProperties != null)
            for (Object savedProperty : savedProperties) {
                propertySlip.add(savedProperty.toString());
            }
        propertySlipTable.refresh();
    }

    /**
     * Action perform saving
     */
    protected void perfomSave() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Traverser tr = dataset.traverse(Order.BREADTH_FIRST, NeoUtils.getStopEvaluator(2), new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Relationship rel = currentPos.lastRelationshipTraversed();
                    return rel != null && rel.isType(GeoNeoRelationshipTypes.PROPERTIES);
                }
            }, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);

            tr.iterator().next().setProperty(INeoConstants.PROPERTY_NAME_SELECTED_PROPERTIES, propertySlip.toArray(new String[0]));

            tx.success();
        } catch (Exception e) {
            tx.failure();
            NeoCorePlugin.error(null, e);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * Init startup data
     */
    private void init() {
        propertySet.clear();

        Transaction tx = NeoUtils.beginTransaction();
        String nodeName = "";
        try {
            nodeName = NeoUtils.getSimpleNodeName(dataset, "");
        } finally {
            NeoUtils.finishTx(tx);
        }
        propertySet.addAll(new PropertyFilterModel().filerProperties(nodeName, Arrays.asList(PropertyHeader.getPropertyStatistic(dataset).getNumericFields())));
    }

    /**
     * Add listeners to UI components
     */
    private void addListeners() {
        bAddComposite.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addComposite();
            }
        });
        bAddSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addSingle();
            }
        });
        bDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteProperty();
            }
        });
        bClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearList();
            }
        });
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                perfomSave();
                shell.close();
            }
        });
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }
        });
    }

    /**
     * Action perform cleaning list of selected properties
     */
    protected void clearList() {
        propertySlip.clear();
        propertySlipTable.refresh();
    }

    /**
     * Create table
     * 
     * @param tableView table
     * @param columnName name of column
     */
    private void createTable(TableViewer tableView, String columnName) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(200);
        column.setText(columnName);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    /**
     * <p>
     * PropertyListContentProvider
     * </p>
     * 
     * @author Saelenchits_N
     * @since 1.0.0
     */
    private class PropertyListContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return propertySet.toArray(new String[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * <p>
     * PropertySlipContentProvider
     * </p>
     * 
     * @author Saelenchits_N
     * @since 1.0.0
     */
    private class PropertySlipContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return propertySlip.toArray(new String[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * <p>
     * PropertyListLabelProvider
     * </p>
     * 
     * @author Saelenchits_N
     * @since 1.0.0
     */
    public class PropertyListLabelProvider extends LabelProvider {
        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            return element.toString();
        }
    }
}
