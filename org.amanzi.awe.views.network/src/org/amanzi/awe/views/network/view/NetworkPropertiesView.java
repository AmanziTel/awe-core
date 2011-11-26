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

import org.amanzi.neo.services.model.IDataElement;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Text;
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
    private IDataElement currentDataElement;
    
    private static String DESTINATION_OF_THIS_VIEW = "This network properties view destine for view all properties in IDataElements";

    public void updateTableView(IDataElement dataElement) {
    	currentDataElement = dataElement;
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

        createViewer(parent, child);
    }
    
    private void createViewer(Composite parent, Composite child) {
        tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setUseHashlookup(true);
        
        CellEditor[] editors = new CellEditor[2];
        TextCellEditor textEditor = new TextCellEditor(parent);
        ((Text)textEditor.getControl()).setTextLimit(60);
        editors[0] = textEditor;
        
        String[] arr = new String[2];
        arr[0] = "1";
        arr[1] = "2";
        editors[1] = new ComboBoxCellEditor(parent, arr,
                SWT.READ_ONLY);
        
        tableViewer.setCellEditors(editors);
        
        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        tableViewer.getControl().setLayoutData(fData);
        
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        
        provider = new TableContentProvider();
        tableViewer.setContentProvider(provider);
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setInput("");
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
        public void createTableColumn() {
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
                    if (TableColumnSorter.this.viewer.getComparator() != null ||
                    TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
                        int tdirection = TableColumnSorter.this.direction;

                        if (tdirection == ASC) {
                            setSorter(TableColumnSorter.this, DESC);
                        } else if (tdirection == DESC) {
                            setSorter(TableColumnSorter.this, NONE);
                        }
                    } 
                    else {
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
            } 
            else {
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
        	
            ((TableLabelProvider)tableViewer.getLabelProvider()).createTableColumn();
            if (currentDataElement != null) {
	            for (String property : currentDataElement.keySet()) {
	            	Object value = currentDataElement.get(property);
	            	if (value instanceof Number || value instanceof Boolean || value instanceof String) {
		            	RowWrapper row = new RowWrapper(property, value);
		            	elements.add(row);
	            	}
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

    @Override
    public void setFocus() {
    }
    
//    /**
//     * Sets the property value.
//     * 
//     * @param id the id
//     * @param value the value
//     */
//    @Override
//    public void setPropertyValue(Object id, Object value) {
//        String propertyName = id.toString();
//        INetworkModel networkModel = (INetworkModel)currentDataElement.get(INeoConstants.NETWORK_MODEL_NAME);
//        try {
//            boolean isReadyToUpdate = true;
//            INodeType nodeType = NodeTypeManager.getType(currentDataElement.get(NewAbstractService.TYPE).toString());
//            IDataElement dataElement = null;
//            boolean isCIorLAC = false;
//            // if property is unique then find is exist some element with equal property
//            if (networkModel.isUniqueProperties(propertyName)) {
//                if (nodeType.equals(NetworkElementNodeType.SECTOR)) {
//                    // ci+lac in sector should be unique, not a ci_lac as parameter
//                    // but ci together with lac should be unique
//                    String ci_lac = null;
//                    if (propertyName.equals(NewNetworkService.CELL_INDEX)) {
//                        String lac = currentDataElement.get(NewNetworkService.LOCATION_AREA_CODE).toString();
//                        ci_lac = value.toString() + "_" + lac;
//                        isCIorLAC = true;
//                    } else if (propertyName.equals(NewNetworkService.LOCATION_AREA_CODE)) {
//                        String ci = currentDataElement.get(NewNetworkService.CELL_INDEX).toString();
//                        ci_lac = ci + "_" + value.toString();
//                        isCIorLAC = true;
//                    }
//                    if (isCIorLAC) {
//                        dataElement = networkModel.findSector(propertyName, ci_lac);
//                    } else {
//                        dataElement = networkModel.findSector(propertyName, value.toString());
//                    }
//                    if (dataElement != null) {
//                        isReadyToUpdate = false;
//                    }
//                } else {
//                    Set<IDataElement> elements = networkModel.findElementByPropertyValue(nodeType, propertyName, value);
//                    if (elements.size() > 0) {
//                        dataElement = elements.iterator().next();
//                        isReadyToUpdate = false;
//                    }
//                }
//            }
//            if (isReadyToUpdate) {
//                networkModel.updateElement(currentDataElement, propertyName, value);
//            } else {
//                String propertyDefined = null;
//                if (isCIorLAC) {
//                    propertyDefined = PROPERTY_DEFINED_IN_ELEMENT_CI_LAC;
//                } else {
//                    propertyDefined = PROPERTY_DEFINED_IN_ELEMENT;
//                }
//                String message = MESSAGE_COULD_NOT_CHANGE_PROPERTY + propertyDefined + dataElement.get(NewNetworkService.TYPE)
//                        + " and name " + dataElement.get(NewNetworkService.NAME);
//
//                if (showMessageBox) {
//                    showMessageBox = false;
//                    synchronized (message) {
//                        // if we will use this code then we will get a critical error
//                        // MessageDialog.openWarning(null, TITLE_COULD_NOT_CHANGE_PROPERTY,
//                        // message);
//                        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
//                        msg.setText(TITLE_COULD_NOT_CHANGE_PROPERTY);
//                        msg.setMessage(message);
//                        int result = msg.open();
//                        if (result != SWT.OK) {
//                            return;
//                        }
//                    }
//                    showMessageBox = true;
//                }
//            }
//        } catch (AWEException e) {
//            MessageDialog.openError(null, TITLE_COULD_NOT_CHANGE_PROPERTY, TITLE_COULD_NOT_CHANGE_PROPERTY + "\n" + e);
//        }
//    }
}
