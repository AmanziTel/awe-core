package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import scala.actors.threadpool.Arrays;

/**
 * <p>
 * Dialog for copy of element
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class CopyOfElementDialog extends AbstractDialog<Integer> {

    // sector
    private final static String[] SECTOR_PROPERTIES = {"name", "ci", "lac"};

    // site
    private final static String[] SITE_PROPERTIES = {"name", "lat", "lon"};

    // other
    private final static String[] OTHER_PROPERTIES = {"name"};

    /*
     * table
     */
    private TableViewer tableViewer;

    /*
     * table providers
     */
    private TableContentProvider contentProvider;
    private TableLabelProvider labelProvider;

    /*
     * list content properties of selected element
     */
    private static List<String> properties;

    // selected element for copy
    private IDataElement element;

    /*
     * type of selected element: 1 - site, 2 - sector, 0 - other elements
     */
    private int typeElement;

    /** The b ok. */
    private Button bOk;

    /** The b cancel. */
    private Button bCancel;

    /** Shell */
    private Shell shell;

    // table row elements
    private List<RowValues> elements = new ArrayList<RowValues>();

    public CopyOfElementDialog(Shell parent, IDataElement element, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        status = SWT.CANCEL;
        this.element = element;

    }

    /**
     * Update properties from selected element
     */
    private void updateProperies() {

        properties = new ArrayList<String>();
        typeElement = 0;

        // type
        String type = NodeTypeManager.getType(element).getId();

        // if sector
        if (type.equals(NetworkElementNodeType.SECTOR.getId())) {

            // add to properties list unique properties for sector
            for (int i = 0; i < SECTOR_PROPERTIES.length; i++) {
                properties.add(SECTOR_PROPERTIES[i]);
            }
            typeElement = 2;
        }

        // if site
        else if (type.equals(NetworkElementNodeType.SITE.getId())) {

            // add to properties list unique properties for site
            for (int j = 0; j < SITE_PROPERTIES.length; j++) {
                properties.add(SITE_PROPERTIES[j]);
            }
            typeElement = 1;
        }

        // other elements
        else {
            properties.add(AbstractService.NAME);
        }

        // add other properties
        for (String property : element.keySet()) {
            if (!properties.contains(property)) {
                properties.add(property);
            }
        }
    }

    @Override
    protected void createContents(Shell composite) {

        shell = composite;

        // composite
        composite.setLayout(new GridLayout(1, true));

        // label
        Label label = new Label(composite, SWT.NONE);
        label.setText("Enter data:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        // table
        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setUseHashlookup(true);

        GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        dataTable.heightHint = 300;
        dataTable.widthHint = 280;

        tableViewer.getControl().setLayoutData(dataTable);

        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        tableViewer.setLabelProvider(labelProvider);

        contentProvider = new TableContentProvider();
        tableViewer.setContentProvider(contentProvider);

        tableViewer.setInput(StringUtils.EMPTY);

        // child composite
        Composite child = new Composite(composite, SWT.FILL);
        final GridLayout layout = new GridLayout(2, true);
        child.setLayout(layout);

        // buttons
        bOk = new Button(child, SWT.PUSH);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        data.widthHint = 140;
        bOk.setLayoutData(data);
        bOk.setText("OK");
        bCancel = new Button(child, SWT.PUSH);
        bCancel.setLayoutData(data);
        bCancel.setText("Cancel");
        addListeners();

    }

    /**
     * Adds the button listeners.
     */
    private void addListeners() {

        // OK
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                try {
                    // copy element
                    saveElement();

                    // update views
                    EventManager.getInstance().fireEvent(new UpdateDataEvent());
                } catch (AWEException e1) {
                    // TODO Handle AWEException
                    throw (RuntimeException)new RuntimeException().initCause(e1);
                }
                shell.close();
            }
        });

        // Cancel
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }
        });
    }

    /**
     * Create new element
     * 
     * @throws AWEException
     */
    private void saveElement() throws AWEException {
        
        

        // network
        INetworkModel networkModel = (INetworkModel)element.get(INeoConstants.NETWORK_MODEL_NAME);

        // parameters
        Map<String, Object> params = new HashMap<String, Object>();
        for (RowValues r : elements) {
            params.put(r.getProperty(), r.getValue());
        }

        // validation
        if (networkModel.findElement(params) != null) {
            MessageBox mbox = new MessageBox( shell,SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage("Данный элемент ввода не привязан к источнику данных!");
            mbox.open();
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Such element already exist!",
                    "The element with such name already exists. Enter other name.");
        } else if (typeElement == 1) {
            if (!(params.get(INeoConstants.PROPERTY_LAT_NAME) instanceof Double)
                    || !(params.get(INeoConstants.PROPERTY_LON_NAME) instanceof Double)) {
                MessageDialog.openError(null, "Lat and lon is double!", "Enter in lat and lon double values.");
            }

        } else if (typeElement == 2) {
            if (!(params.get(INeoConstants.PROPERTY_SECTOR_CI) instanceof Double)
                    || !(params.get(INeoConstants.PROPERTY_SECTOR_LAC) instanceof Double)) {
                MessageDialog.openError(null, "Ci and lac is double!", "Enter in ci and lac double values.");
            }
        } else {
            // create element
            networkModel.createElement(networkModel.getParentElement(element), params);
        }

    }

    /**
     * TODO Purpose of CopyOfElementDialog
     * <p>
     * Content provider for property table of selected element
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class TableContentProvider implements IStructuredContentProvider {

        // properties for selected element
        private List<String> elementProperties = new ArrayList<String>();

        @Override
        public void dispose() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            elements.clear();
            updateProperies();
            ((TableLabelProvider)tableViewer.getLabelProvider()).createTableColumn();
            if (element != null) {
                for (String property : properties) {

                    // other
                    if (typeElement == 0) {
                        elementProperties = new ArrayList<String>(Arrays.asList(OTHER_PROPERTIES));
                    }

                    // site
                    if (typeElement == 1) {
                        elementProperties = new ArrayList<String>(Arrays.asList(SITE_PROPERTIES));
                    }

                    // sector
                    if (typeElement == 2) {
                        elementProperties = new ArrayList<String>(Arrays.asList(SECTOR_PROPERTIES));
                    }

                    if (!elementProperties.contains(property)) {
                        Object value = element.get(property);
                        if (value instanceof Number || value instanceof Boolean || value instanceof String) {
                            RowValues r = new RowValues(property, value);
                            elements.add(r);
                        }
                    } else {
                        RowValues r = new RowValues(property, StringUtils.EMPTY);
                        elements.add(r);
                    }
                }
            }
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new RowValues[0]);
        }

    }

    /**
     * TODO Purpose of CopyOfElementDialog
     * <p>
     * Label provider for property table
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        // column size
        private final static int DEF_SIZE = 147;

        // column name
        private final String PROPERTY_NAME = "Property";
        private final String PROPERTY_VALUE = "Value";

        // column list
        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();

        // create one column
        private void createColumn(String label, int size, final int idx, boolean edit) {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
            TableColumn col = column.getColumn();
            col.setText(label);
            columns.add(col);
            col.setWidth(DEF_SIZE);
            col.setResizable(true);
            if (edit) {
                column.setEditingSupport(new ValueEditingSupport(tableViewer));
            }
        }

        /**
         * create column table
         */
        public void createTableColumn() {
            Table tabl = tableViewer.getTable();
            for (TableColumn column : columns) {
                column.dispose();
            }
            int idx = 0;
            createColumn(PROPERTY_NAME, DEF_SIZE, idx, false);
            idx++;
            createColumn(PROPERTY_VALUE, DEF_SIZE, idx, true);
            idx++;

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            tableViewer.setLabelProvider(this);
            tableViewer.refresh();
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            RowValues r = (RowValues)element;
            if (columnIndex == 0) {
                return r.getProperty();
            } else {
                return r.getValue().toString();
            }
        }
    }

    /**
     * TODO Purpose of CopyOfElementDialog
     * <p>
     * Save row values
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    private class RowValues {

        // row values
        private String property;
        private Object value;

        public RowValues(String property, Object value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * TODO Purpose of CopyOfElementDialog
     * <p>
     * Class for editing value column
     * </p>
     * 
     * @author ladornaya_a
     * @since 1.0.0
     */
    public class ValueEditingSupport extends EditingSupport {

        // table
        private final TableViewer viewer;

        public ValueEditingSupport(TableViewer viewer) {
            super(viewer);
            this.viewer = viewer;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor(viewer.getTable());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            return ((RowValues)element).getValue();
        }

        @Override
        protected void setValue(Object element, Object value) {
            ((RowValues)element).setValue(String.valueOf(value));
            viewer.refresh();
        }
    }

}
