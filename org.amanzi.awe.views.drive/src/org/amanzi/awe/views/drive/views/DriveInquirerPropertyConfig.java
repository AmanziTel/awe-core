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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.AbstractDialog;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
    private List<String> propertyList = new ArrayList<String>();
    private final List<String> propertySlip = new ArrayList<String>();

    private final GraphDatabaseService service;

    /**
     * @param parent
     * @param title
     */
    public DriveInquirerPropertyConfig(Shell parent, Node dataset) {
        super(parent, "Dataset properties configuration (under construction)", SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.dataset = dataset;
        status = SWT.CANCEL;
        service = NeoServiceProvider.getProvider().getService();
    }

    @Override
    protected void createContents(final Shell shell) {
        this.shell = shell;
        shell.setLayout(new GridLayout(4, false));
        // Label label = new Label(shell, SWT.NONE);
        // label.setText("Network:");
        // cNetwork = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        // GridData layoutData = new GridData();
        // layoutData.grabExcessHorizontalSpace = true;
        // layoutData.minimumWidth = 200;
        // cNetwork.setLayoutData(layoutData);
        propertyListTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        data.heightHint = 200;
        data.widthHint = 200;
        createTable(propertyListTable);
        propertyListTable.getControl().setLayoutData(data);
        propertyListTable.setContentProvider(new PropertyListContentProvider());

        propertyListTable.setLabelProvider(new PropertyListLabelProvider());

//        propertySlipTable = CheckboxTableViewer.newCheckList(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table table = new Table(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        propertySlipTable = new CheckboxTableViewer(table);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        data.heightHint = 200;
        data.widthHint = 200;
        createTable(propertySlipTable);
        propertySlipTable.getControl().setLayoutData(data);
        propertySlipTable.setContentProvider(new PropertySlipContentProvider());

        propertySlipTable.setLabelProvider(new PropertyListLabelProvider());

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
                perfomSave();
                shell.close();
            }

        });

        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        // GridData gdBtnCancel = new GridData();
        // gdBtnCancel.horizontalAlignment = GridData.END;
        // gdBtnCancel.widthHint = 80;
        btnCancel.setLayoutData(gdBtnCancel);
        btnCancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }

        });

        addListeners();
        init();
        propertyListTable.setInput("");
        loadSavedData();
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
                for (String existProperty : propertyList) {
                    if (existProperty.compareTo((String)savedProperty) == 0) {
                        propertyListTable.setChecked(existProperty, true);
                        break;
                    }
                }
            }
        updatePropertySlip();
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
            // Relationship relation =
            // dataset.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES,
            // Direction.OUTGOING);
            Object[] selected = propertyListTable.getCheckedElements();
            List<String> selectedList = new ArrayList<String>(selected.length);
            for (Object singleSelection : selected) {
                selectedList.add((String)singleSelection);
                propertyListTable.refresh();
            }

            tr.iterator().next().setProperty(INeoConstants.PROPERTY_NAME_SELECTED_PROPERTIES, selectedList.toArray(new String[0]));

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
    }

    private void addListeners() {
        propertyListTable.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                updatePropertySlip();
            }
        });
    }

    /**
     * @param element
     */
    protected void updatePropertySlip() {
        propertySlipTable.getControl().setVisible(false);
        Object[] selectedPropertes = propertyListTable.getCheckedElements();
        ArrayList<String> newSlipPropertyList = new ArrayList<String>();
        propertySlip.clear();
        for (Object newSlipProperty : selectedPropertes) {
            for (String property : propertySlip) {
                newSlipPropertyList.add(property + ", " + newSlipProperty);
            }
            newSlipPropertyList.add(newSlipProperty.toString());
            propertySlip.addAll(newSlipPropertyList);
            newSlipPropertyList.clear();
        }
        LOGGER.debug(selectedPropertes.length);
        LOGGER.debug(propertySlip.size());
        propertySlipTable.setInput("");
        propertySlipTable.getControl().setVisible(true);
    }

    /**
     * @param tableView2
     */
    private void createTable(TableViewer tableView) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(170);
        column.setText("Avaliable properties");
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
