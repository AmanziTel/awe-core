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

package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * Network properties view
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class NetworkPropertiesView extends ViewPart {

    /*
     * ID of this View
     */
    public static final String NEW_NETWORK_PROPERTIES_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkPropertiesView";

    /*
     * table
     */
    private TableViewer tableViewer;

    private CheckboxTableViewer propertyListTable;

    /*
     * table providers
     */
    private MultiplyTableLabelProvider labelProvider;
    private MultiplyTableContentProvider provider;

    /*
     * set for selection elements
     */
    private Set<IDataElement> currentDataElements;

    // all properties
    private static List<String> allProperties;

    // selected properties
    private static List<String> headers;

    private static boolean updateTable = false;

    private static List<RowWrapper> elements = new ArrayList<RowWrapper>();

    // private boolean isEditable;

    private static String DESTINATION_OF_THIS_VIEW = "This network properties view destine for view all properties in IDataElements";
    private static String CELL_MODIFIER_1 = "column1";
    private static String CELL_MODIFIER_2 = "column2";

    public static boolean showMessageBox = true;

    public void updateTableView(Set<IDataElement> dataElements, boolean isEditable) {
        this.currentDataElements = dataElements;
        // this.isEditable = isEditable;
        tableViewer.setInput("");
        tableViewer.refresh();
    }

    public void updateTableView() {
        tableViewer.setInput("");
        tableViewer.refresh();
    }

    private void createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.FLAT);
        label.setText(labelText + ":");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    }

    @Override
    public void createPartControl(Composite mainParent) {
        Composite parent = new Composite(mainParent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        parent.setLayout(formLayout);

        Composite child = new Composite(parent, SWT.FILL);
        final GridLayout layout = new GridLayout(6, false);
        child.setLayout(layout);
        createLabel(child, DESTINATION_OF_THIS_VIEW);

        updateTable = false;

        createViewer(parent, child);
    }

    private void createViewer(Composite parent, Composite child) {
        tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setUseHashlookup(true);

        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        tableViewer.getControl().setLayoutData(fData);

        labelProvider = new MultiplyTableLabelProvider();
        labelProvider.createTableColumn();
        tableViewer.setLabelProvider(labelProvider);

        provider = new MultiplyTableContentProvider();
        tableViewer.setContentProvider(provider);

        tableViewer.setColumnProperties(new String[] {CELL_MODIFIER_1, CELL_MODIFIER_2});

        ColumnViewerEditorActivationStrategy activationStrategy = new ColumnViewerEditorActivationStrategy(tableViewer) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        tableViewer.setCellEditors(new CellEditor[] {new TextCellEditor(tableViewer.getTable()),
                new TextCellEditor(tableViewer.getTable())});

        TableViewerEditor.create(tableViewer, activationStrategy, ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
                | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        tableViewer.setInput("");
    }

    /**
     * TODO Purpose of NewNetworkPropertiesView
     * <p>
     * Label provider for multiply table
     * </p>
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class MultiplyTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        private final static int DEF_SIZE = 110;
        private boolean level = true;

        private void createColumn(String label, int size, boolean sortable, final int idx) {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
            TableColumn col = column.getColumn();
            col.setText(label);
            columns.add(col);
            col.setWidth(DEF_SIZE);
            col.setResizable(true);
            if (sortable) {
                TableColumnSorter cSorter = new TableColumnSorter(tableViewer, col) {
                    protected int doCompare(Viewer v, Object e1, Object e2) {
                        ITableLabelProvider lp = ((ITableLabelProvider)tableViewer.getLabelProvider());
                        String t1 = lp.getColumnText(e1, idx);
                        String t2 = lp.getColumnText(e2, idx);
                        return t1.compareTo(t2);
                    }
                };
                cSorter.setSorter(cSorter, TableColumnSorter.ASC);
            }
        }

        /**
         * create column table
         */
        public void createTableColumn() {
            level = true;
            Table tabl = tableViewer.getTable();
            allProperties = new ArrayList<String>();
            for (TableColumn column : columns) {
                column.dispose();
            }
            int idx = 0;
            
            

            if (currentDataElements != null) {
                allProperties.add(AbstractService.NAME);
                createColumn(AbstractService.NAME, DEF_SIZE, true, idx);
                idx++;
                String type = null;

                for (IDataElement element : currentDataElements) {
                    for (String header : element.keySet()) {
                        if (!allProperties.contains(header)) {
                            allProperties.add(header);
                        }

                    }
                    
                    if (type == null) {
                        type = element.get(AbstractService.TYPE).toString();
                    } else {
                        if (!type.equals(element.get(AbstractService.TYPE).toString())) {
                            level = false;
                        }
                    }
                }

                if (!level) {
                    updateTableView(null, true);
                }

                if (!updateTable) {
                    headers = new ArrayList<String>();
                    headers = allProperties;
                }
                for (String header : headers) {
                    if (!header.equals(AbstractService.NAME)) {
                        createColumn(header, DEF_SIZE, false, idx);
                        idx++;
                    }
                }
            }

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            tableViewer.setLabelProvider(this);
            tableViewer.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWrapper wrapper = (RowWrapper)element;
            return wrapper.getValues().get(columnIndex);
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private static abstract class TableColumnSorter extends ViewerComparator {
        public static final int ASC = 1;

        public static final int NONE = 0;

        public static final int DESC = -1;

        private int direction = 0;

        private TableColumn column;

        private TableViewer viewer;

        public TableColumnSorter(TableViewer viewer, TableColumn column) {
            this.column = column;
            this.viewer = viewer;
            this.column.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    if (TableColumnSorter.this.viewer.getComparator() != null
                            || TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
                        int tdirection = TableColumnSorter.this.direction;

                        if (tdirection == ASC) {
                            setSorter(TableColumnSorter.this, DESC);
                        } else if (tdirection == DESC) {
                            setSorter(TableColumnSorter.this, NONE);
                        }
                    } else {
                        setSorter(TableColumnSorter.this, ASC);
                    }
                }
            });
        }

        public void setSorter(TableColumnSorter sorter, int direction) {
            if (direction == NONE) {
                column.getParent().setSortColumn(null);
                column.getParent().setSortDirection(SWT.NONE);
                viewer.setComparator(null);
            } else {
                column.getParent().setSortColumn(column);
                sorter.direction = direction;

                if (direction == ASC) {
                    column.getParent().setSortDirection(SWT.DOWN);
                } else {
                    column.getParent().setSortDirection(SWT.UP);
                }

                if (viewer.getComparator() == sorter) {
                    viewer.refresh();
                } else {
                    viewer.setComparator(sorter);
                }
            }
        }

        public int compare(Viewer viewer, Object e1, Object e2) {
            return direction * doCompare(viewer, e1, e2);
        }

        protected abstract int doCompare(Viewer TableViewer, Object e1, Object e2);
    }

    /**
     * TODO Purpose of NewNetworkPropertiesView
     * <p>
     * Content provider for multiply table
     * </p>
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class MultiplyTableContentProvider implements IStructuredContentProvider {

        public MultiplyTableContentProvider() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new RowWrapper[0]);
        }

        @Override
        public void dispose() {

        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            elements.clear();

            ((MultiplyTableLabelProvider)tableViewer.getLabelProvider()).createTableColumn();

            if (currentDataElements != null) {
                for (IDataElement element : currentDataElements) {
                    List<String> rowValues = new ArrayList<String>();
                    for (String header : headers) {
                        if (element.keySet().contains(header)) {
                            rowValues.add(element.get(header).toString());
                        } else {
                            rowValues.add("");
                        }
                    }
                    RowWrapper row = new RowWrapper(rowValues);
                    elements.add(row);
                }

            }
        }
    }

    /**
     * <p>
     * Wrapper of one row of table
     * </p>
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    private class RowWrapper {
        private List<String> values;
        @SuppressWarnings("unused")
        private boolean isEditable;

        private RowWrapper(List<String> values) {
            super();
            this.setValues(values);
            this.setEditable(false);
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public void setEditable(boolean isEditable) {
            this.isEditable = isEditable;
        }

    }

    @Override
    public void setFocus() {
    }

    /**
     * copy date to clipboard
     */
    public void copyToClipboard() {

        Clipboard cb = new Clipboard(Display.getDefault());

        StringBuilder sb = new StringBuilder();
        // headers
        sb.append(parseToString(headers));

        // rows
        for (RowWrapper row : elements) {
            sb.append(parseToString(row.getValues()));
        }

        TextTransfer textTransfer = TextTransfer.getInstance();
        cb.setContents(new Object[] {sb.toString()}, new Transfer[] {textTransfer});
    }

    /**
     * transform list at line
     * 
     * @param list
     * @return line
     */
    private String parseToString(List<String> list) {
        String line = "";
        for (String value : list) {
            line = line + value + "\t";
        }
        line = line + System.getProperty("line.separator");
        return line;
    }

    /*
     * filter properties
     */
    public void filter(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        Shell parent = workbenchWindow.getShell();
        Dialog dialog = new Dialog(parent) {
            @Override
            protected Control createDialogArea(Composite parent) {
                Composite composite = (Composite)super.createDialogArea(parent);
                composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                composite.setLayout(new GridLayout());
                final Button button = new Button(composite, SWT.NONE);
                button.setText("Select All");
                button.addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        propertyListTable.setAllChecked(true);
                        propertyListTable.refresh();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        propertyListTable.refresh();
                    }
                });
                propertyListTable = CheckboxTableViewer.newCheckList(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                        | SWT.BORDER | SWT.CHECK);
                GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
                data.heightHint = 300;
                data.widthHint = 200;
                createTable(propertyListTable, "Properties");
                propertyListTable.getControl().setLayoutData(data);
                propertyListTable.setContentProvider(new PropertyListContentProvider());
                propertyListTable.setLabelProvider(new PropertyListLabelProvider());
                propertyListTable.setInput("");
                for (String header : headers) {
                    propertyListTable.setChecked(header, true);
                }
                return dialogArea;
            }

            @Override
            protected Point getInitialSize() {
                return new Point(250, 400);
            }

            @Override
            protected void configureShell(Shell newShell) {
                super.configureShell(newShell);
                newShell.setText("Properties filter");
            }

            @Override
            protected void okPressed() {
                Object[] selectedProperties = propertyListTable.getCheckedElements();
                headers = new ArrayList<String>();
                for (Object o : selectedProperties) {
                    headers.add(o.toString());
                }
                updateTable = true;
                close();
            }

        };

        if (dialog.open() == Dialog.OK) {

        }

    }

    /*
     * create filter properties table
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
     * 
     * TODO Purpose of NetworkPropertiesView
     * <p>
     *  content provider for filter properties table
     * </p>
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class PropertyListContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return allProperties.toArray();
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * 
     * TODO Purpose of NetworkPropertiesView
     * <p>
     * label provider for filter properties table
     * </p>
     * @author Ladornaya_A
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
