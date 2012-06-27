package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * Dialog for copy of element
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class CopyOfElementDialog extends AbstractDialog<Integer> {

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
        private final String property;
        private Object value;

        public RowValues(String property, Object value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return this.property;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
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

        @Override
        public Object[] getElements(Object inputElement) {
            return CopyOfElementDialog.this.elements.toArray(new RowValues[0]);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            CopyOfElementDialog.this.elements.clear();
            updateProperies();
            ((TableLabelProvider)CopyOfElementDialog.this.tableViewer.getLabelProvider()).createTableColumn();
            if (CopyOfElementDialog.this.element != null) {
                for (String property : properties) {

                    // other
                    if (CopyOfElementDialog.this.typeElement == 0) {
                        this.elementProperties = new ArrayList<String>(Arrays.asList(OTHER_PROPERTIES));
                    }

                    // site
                    if (CopyOfElementDialog.this.typeElement == 1) {
                        this.elementProperties = new ArrayList<String>(Arrays.asList(SITE_PROPERTIES));
                    }

                    // sector
                    if (CopyOfElementDialog.this.typeElement == 2) {
                        this.elementProperties = new ArrayList<String>(Arrays.asList(SECTOR_PROPERTIES));
                    }

                    if (!this.elementProperties.contains(property)) {
                        Object value = CopyOfElementDialog.this.element.get(property);
                        if ((value instanceof Number) || (value instanceof Boolean) || (value instanceof String)) {
                            RowValues r = new RowValues(property, value);
                            CopyOfElementDialog.this.elements.add(r);
                        }
                    } else {
                        RowValues r = new RowValues(property, StringUtils.EMPTY);
                        CopyOfElementDialog.this.elements.add(r);
                    }
                }
            }
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
        private static final int DEF_SIZE = 147;

        // column name
        private final String PROPERTY_NAME = "Property";
        private final String PROPERTY_VALUE = "Value";

        // column list
        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();

        // create one column
        private void createColumn(String label, int size, final int idx, boolean edit) {
            TableViewerColumn column = new TableViewerColumn(CopyOfElementDialog.this.tableViewer, SWT.LEFT);
            TableColumn col = column.getColumn();
            col.setText(label);
            this.columns.add(col);
            col.setWidth(DEF_SIZE);
            col.setResizable(true);
            if (edit) {
                column.setEditingSupport(new ValueEditingSupport(CopyOfElementDialog.this.tableViewer));
            }
        }

        /**
         * create column table
         */
        public void createTableColumn() {
            Table tabl = CopyOfElementDialog.this.tableViewer.getTable();
            for (TableColumn column : this.columns) {
                column.dispose();
            }
            int idx = 0;
            createColumn(this.PROPERTY_NAME, DEF_SIZE, idx, false);
            idx++;
            createColumn(this.PROPERTY_VALUE, DEF_SIZE, idx, true);
            idx++;

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            CopyOfElementDialog.this.tableViewer.setLabelProvider(this);
            CopyOfElementDialog.this.tableViewer.refresh();
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
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
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor(this.viewer.getTable());
        }

        @Override
        protected Object getValue(Object element) {
            return ((RowValues)element).getValue();
        }

        @Override
        protected void setValue(Object element, Object value) {
            ((RowValues)element).setValue(value);
            this.viewer.refresh();
        }
    }

    // sector
    private static final String[] SECTOR_PROPERTIES = {"name", "ci", "lac"};
    // site
    private static final String[] SITE_PROPERTIES = {"name", "lat", "lon"};

    // other
    private static final String[] OTHER_PROPERTIES = {"name"};

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
    private final IDataElement element;

    /*
     * type of selected element: 1 - site, 2 - sector, 0 - other elements
     */
    private int typeElement;

    /*
     * element type
     */
    private INodeType typeNode;

    /** The b ok. */
    private Button bOk;

    /** The b cancel. */
    private Button bCancel;

    /** Shell */
    private final Shell shell;

    private Shell parentShell;

    // table row elements
    private final List<RowValues> elements = new ArrayList<RowValues>();

    public CopyOfElementDialog(Shell parent, IDataElement element, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.parentShell = parent;
        this.shell = new Shell(parent, SWT.SHELL_TRIM);
        this.status = SWT.CANCEL;
        this.element = element;
    }

    /**
     * Adds the button listeners.
     */
    private void addListeners() {

        // OK
        this.bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CopyOfElementDialog.this.status = SWT.OK;
                try {
                    // copy element
                    boolean save = saveElement();
                    if (save) {
                        // update views
                        EventManager.getInstance().fireEvent(new UpdateDataEvent());
                        CopyOfElementDialog.this.parentShell.close();
                    }
                } catch (AWEException e1) {
                    // TODO Handle AWEException
                    throw (RuntimeException)new RuntimeException().initCause(e1);
                }
            }
        });

        // Cancel
        this.bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CopyOfElementDialog.this.status = SWT.CANCEL;
                CopyOfElementDialog.this.parentShell.close();
            }
        });
    }

    @Override
    protected void createContents(Shell composite) {

        this.parentShell = composite;

        // composite
        composite.setLayout(new GridLayout(1, true));

        // label
        Label label = new Label(composite, SWT.NONE);
        label.setText(NetworkMessages.LABEL_TITLE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        // table
        this.tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        this.tableViewer.setUseHashlookup(true);

        GridData dataTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        dataTable.heightHint = 300;
        dataTable.widthHint = 280;

        this.tableViewer.getControl().setLayoutData(dataTable);

        this.labelProvider = new TableLabelProvider();
        this.labelProvider.createTableColumn();
        this.tableViewer.setLabelProvider(this.labelProvider);

        this.contentProvider = new TableContentProvider();
        this.tableViewer.setContentProvider(this.contentProvider);

        this.tableViewer.setInput(StringUtils.EMPTY);

        // child composite
        Composite child = new Composite(composite, SWT.FILL);
        final GridLayout layout = new GridLayout(2, true);
        child.setLayout(layout);

        // buttons
        this.bOk = new Button(child, SWT.PUSH);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        data.widthHint = 140;
        this.bOk.setLayoutData(data);
        this.bOk.setText(NetworkMessages.BUTTON_OK_TITLE);
        this.bCancel = new Button(child, SWT.PUSH);
        this.bCancel.setLayoutData(data);
        this.bCancel.setText(NetworkMessages.BUTTON_CANCEL_TITLE);
        addListeners();

    }

    /**
     * Checking value - double or no
     * 
     * @param value string value
     * @return true if double
     */
    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException exc) {
            return false;
        }
        return true;
    }

    /**
     * Create new element
     * 
     * @throws AWEException
     */
    private boolean saveElement() throws AWEException {

        // network
        INetworkModel networkModel = (INetworkModel)this.element.get(INeoConstants.NETWORK_MODEL_NAME);

        // parameters
        Map<String, Object> params = new HashMap<String, Object>();
        for (RowValues r : this.elements) {
            if ((r.getProperty().equals(INeoConstants.PROPERTY_LAT_NAME) || r.getProperty().equals(INeoConstants.PROPERTY_LON_NAME))
                    && isDouble(r.getValue().toString())) {
                params.put(r.getProperty(), Double.parseDouble(r.getValue().toString()));
            } else {
                params.put(r.getProperty(), r.getValue());
            }
        }

        // type
        params.put(AbstractService.TYPE, this.typeNode.getId());

        // validation
        if (params.get(AbstractService.NAME).toString().isEmpty()) {
            MessageDialog.openError(this.shell, NetworkMessages.NAME_ERROR_TITLE, NetworkMessages.NAME_ERROR);
            return false;
        } else if (networkModel.findElementByPropertyValue(this.typeNode, AbstractService.NAME, params.get(AbstractService.NAME))
                .size() != 0) {
            MessageDialog.openError(this.shell, NetworkMessages.ELEMENT_EXIST_ERROR_TITLE, NetworkMessages.ELEMENT_EXIST_ERROR);
            return false;
        } else if (this.typeElement == 1) {
            if (!isDouble(params.get(INeoConstants.PROPERTY_LAT_NAME).toString())
                    || !isDouble(params.get(INeoConstants.PROPERTY_LON_NAME).toString())) {
                MessageDialog.openError(this.shell, NetworkMessages.LAT_LON_ERROR_TITLE, NetworkMessages.LAT_LON_ERROR);
                return false;
            }

        } else if (this.typeElement == 2) {
            if (!isDouble(params.get(INeoConstants.PROPERTY_SECTOR_CI).toString())
                    || !isDouble(params.get(INeoConstants.PROPERTY_SECTOR_LAC).toString())) {
                MessageDialog.openError(this.shell, NetworkMessages.CI_LAC_ERROR_TITLE, NetworkMessages.CI_LAC_ERROR);
                return false;
            }
        }

        // create element
        networkModel.createElement(networkModel.getParentElement(this.element), params);
        return true;
    }

    /**
     * Update properties from selected element
     */
    private void updateProperies() {

        properties = new ArrayList<String>();
        this.typeElement = 0;

        // type
        this.typeNode = NodeTypeManager.getType(this.element);
        String type = this.typeNode.getId();

        // if sector
        if (type.equals(NetworkElementNodeType.SECTOR.getId())) {

            // add to properties list unique properties for sector
            for (String element2 : SECTOR_PROPERTIES) {
                properties.add(element2);
            }
            this.typeElement = 2;
        }

        // if site
        else if (type.equals(NetworkElementNodeType.SITE.getId())) {

            // add to properties list unique properties for site
            for (String element2 : SITE_PROPERTIES) {
                properties.add(element2);
            }
            this.typeElement = 1;
        }

        // other elements
        else {
            properties.add(AbstractService.NAME);
        }

        // add other properties
        for (String property : this.element.keySet()) {
            if (!properties.contains(property)) {
                if (!property.equals(NetworkService.SECTOR_COUNT) && !property.equals(AbstractService.TYPE)) {
                    properties.add(property);
                }
            }
        }
    }

}
