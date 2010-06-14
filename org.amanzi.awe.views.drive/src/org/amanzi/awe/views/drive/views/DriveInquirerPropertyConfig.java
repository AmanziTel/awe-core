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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
    // TODO add title icon
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

    private List<String> propertyList = new ArrayList<String>();
    private final Set<String> propertySlip = new TreeSet<String>();

    private final GraphDatabaseService service;

    /**
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
        // shell.setImage(IconManager.getIconManager().
        shell.setLayout(new GridLayout(2, false));
        // Label label = new Label(shell, SWT.NONE);
        // label.setText("Network:");
        // cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        // GridData layoutData = new GridData();
        // layoutData.grabExcessHorizontalSpace = true;
        // layoutData.minimumWidth = 200;
        // cNetwork.setLayoutData(layoutData);
        propertyListTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        // data.horizontalSpan = 2;
        // data.verticalSpan = 1;
        data.heightHint = 300;
        data.widthHint = 200;
        createTable(propertyListTable, "Avaliable properties");
        propertyListTable.getControl().setLayoutData(data);
        propertyListTable.setContentProvider(new PropertyListContentProvider());
        propertyListTable.setLabelProvider(new PropertyListLabelProvider());

        // GridData greedButtonData = new GridData();
        // greedButtonData.horizontalAlignment = GridData.END;
        // greedButtonData.widthHint = 80;

        // propertySlipTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL |
        // SWT.V_SCROLL | SWT.BORDER);



        Table table = new Table(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        propertySlipTable = new CheckboxTableViewer(table);
        data = new GridData(SWT.NO, SWT.FILL, true, true);
        // data.horizontalSpan = 2;
        // data.verticalSpan = 2;
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
        // gr3.setText("Saving options");
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
     * @param gr1
     */
    private Button createButton(Composite parent, String name) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(name);
        button.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return button;
    }

    /**
     *
     */
    protected void deleteProperty() {
        IStructuredSelection selection = (IStructuredSelection)propertySlipTable.getSelection();
        propertySlip.removeAll(selection.toList());
        propertySlipTable.refresh();
    }

    /**
     *
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
     *
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
     *
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
     *
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

    private void init() {
        propertyList.clear();
        propertyList = Arrays.asList(new PropertyHeader(dataset).getNumericFields());

        // Collections.
    }

    private void addListeners() {
        propertyListTable.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                // updatePropertySlip();
            }
        });

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
     *
     */
    protected void clearList() {
        propertySlip.clear();
        propertySlipTable.refresh();
    }

    /**
     * @param tableView2
     */
    private void createTable(TableViewer tableView, String clumnName) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(200);
        column.setText(clumnName);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    private class PropertyListContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return propertyList.toArray(new String[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

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
