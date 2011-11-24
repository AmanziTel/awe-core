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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NetworkPropertiesView extends ViewPart {

    private TableViewer tableViewer;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    
    private static String DESTINATION_OF_THIS_VIEW = "This network properties view destine for view all properties in IDataElements";

    private void createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.FLAT);
        label.setText(labelText + ":");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);

        Composite child = new Composite(frame, SWT.FILL);
        final GridLayout layout = new GridLayout(6, false);
        child.setLayout(layout);
        createLabel(child, DESTINATION_OF_THIS_VIEW);

        addListeners();

        tableViewer = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        tableViewer.getControl().setLayoutData(fData);
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn(new String[] {});
        provider = new TableContentProvider();
        tableViewer.setContentProvider(provider);
    }

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        private final static int DEF_SIZE = 380;
        private final String[] colNames = new String[] {"Property", "Value"};

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
        public void createTableColumn(String[] properties) {
            Table tabl = tableViewer.getTable();
            for (TableColumn column : columns) {
                column.dispose();
            }
            int idx = 0;
            for (String colName : colNames) {
                createColumn(colName, DEF_SIZE, true, idx);
                idx++;
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            tableViewer.setLabelProvider(this);
            tableViewer.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWrapper wrapper = (RowWrapper)element;
            if (columnIndex == 0) {
                return wrapper.getProperty();
            } else {
                return wrapper.getValue().toString();
            }
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
                    if (TableColumnSorter.this.viewer.getComparator() != null) {
                        if (TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
                            int tdirection = TableColumnSorter.this.direction;

                            if (tdirection == ASC) {
                                setSorter(TableColumnSorter.this, DESC);
                            } else if (tdirection == DESC) {
                                setSorter(TableColumnSorter.this, NONE);
                            }
                        } else {
                            setSorter(TableColumnSorter.this, ASC);
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

    private class TableContentProvider implements IStructuredContentProvider {
        List<RowWrapper> elements = new ArrayList<RowWrapper>();

        public TableContentProvider() {
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
        private final String property;
        private final Object value;
        private boolean isEditable;

        private RowWrapper(String property, Object value) {
            super();
            this.property = property;
            this.value = value;
            this.isEditable = false;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }
        
        public void setIsEditable(boolean isEditable) {
        	this.isEditable = isEditable;
        }
        
        public boolean isEditable() {
        	return isEditable;
        }
    }

    private void addListeners() {
    }

    @Override
    public void setFocus() {
    }

}
