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

package org.amanzi.awe.views.property.views;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.views.property.PropertyNumberFilter;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.apache.commons.lang.StringUtils;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class PropertyTableView extends ViewPart {

    private EventManager eventManager;

    @SuppressWarnings("unchecked")
    public PropertyTableView() {
        super();
        eventManager = EventManager.getInstance();
        eventManager.addListener(EventsType.ANALYSE, new ShowModelPropertiesActions());
    }

    public static final String PROPERTY_TABLE_ID = "org.amanzi.awe.views.property.views.PropertyTableView";
    private Combo cbData, cbProperty, cbFilter;
    private TableViewer tableViewer;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    private Iterable<IPropertyStatisticalModel> dataModels;

    private void createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.FLAT);
        label.setText(labelText + ":");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    }

    private GridData createLayoutData(int minimumWidth) {
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = minimumWidth;
        return layoutData;
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
        createLabel(child, "Data");
        cbData = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        cbData.setLayoutData(createLayoutData(150));

        createLabel(child, "Property");
        cbProperty = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbProperty.setLayoutData(createLayoutData(150));

        createLabel(child, "Filter");
        cbFilter = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbFilter.setLayoutData(createLayoutData(150));

        addListeners();
        setDataModels();
        enableElements();

        tableViewer = new TableViewer(frame, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION);
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
        private final static int PROP_DEF_SIZE = 80;

        private void createColumn(String label, int size, boolean sortable, final int idx) {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
            TableColumn col = column.getColumn();
            col.setText(label);
            columns.add(col);
            col.setWidth(size);
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
            for (String prop : properties) {
                createColumn(prop, PROP_DEF_SIZE, false, idx);
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
            return wrapper.getPropValue(columnIndex);
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

    private void enableElements() {
        cbProperty.setEnabled(cbData.getSelectionIndex() >= 0);
        cbFilter.setEnabled(cbData.getSelectionIndex() >= 0 && cbProperty.getSelectionIndex() >= 0);
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
            if (newInput == null) {
                return;
            }
            IPropertyStatisticalModel selModel = getSelectedDataModel();
            String[] properties = selModel.getAllPropertyNames(selModel.getType());
            Iterable<IDataElement> dataElements = selModel.getAllElementsByType(selModel.getType());
            ((TableLabelProvider)tableViewer.getLabelProvider()).createTableColumn(properties);
            String selProperty = getSelectedProperty();
            String selFilter = getSelectedFilter();
            for (IDataElement element : dataElements) {
                boolean doFilter = true;
                if (StringUtils.isNotEmpty(selProperty) && StringUtils.isNotEmpty(selFilter)) {
                    Class< ? > propClass = selModel.getPropertyClass(selModel.getType(), selProperty);
                    if (Number.class.isAssignableFrom(propClass)) {
                        for (int i = 0; i < PropertyNumberFilter.values().length; i++) {
                            if (selFilter.equals(PropertyNumberFilter.values()[i].getCaption())) {
                                if ((((DataElement)element).get(selProperty) == null)
                                        ^ PropertyNumberFilter.values()[i].isPropertyNull()) {
                                    doFilter = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (!selFilter.equals(((DataElement)element).get(selProperty)))
                            doFilter = false;
                    }
                }
                if (doFilter) {
                    RowWrapper row = new RowWrapper();
                    for (int q = 0; q < properties.length; q++) {
                        row.addPropValue((((DataElement)element).get(properties[q])));
                    }
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
     * @author kostyukovich_n
     * @since 1.0.0
     */
    private class RowWrapper {
        private List<String> propValues;

        private RowWrapper() {
            super();
            propValues = new ArrayList<String>();
        }

        public void addPropValue(Object value) {
            if (value != null) {
                propValues.add(value.toString());
            } else {
                propValues.add(null);
            }
        }

        public String getPropValue(int index) {
            if (propValues.size() <= index)
                return null;
            return propValues.get(index);
        }

    }

    private void addListeners() {
        cbData.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setProperties();
                setFilters();
                tableViewer.setInput("");
                enableElements();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        cbProperty.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setFilters();
                enableElements();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        cbFilter.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setInput("");
                enableElements();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * handle analyse event show properties of recieved model
     * 
     * @return
     */
    private class ShowModelPropertiesActions implements IEventsListener<AnalyseEvent> {
        @Override
        public void handleEvent(AnalyseEvent data) {
            setDataModels();
            IPropertyStatisticalModel model = (IPropertyStatisticalModel)data.getSelectedModel();
            String modelName = model.getName();
            int i = 0;
            for (String comboEleemnt : cbData.getItems()) {
                if (comboEleemnt.equals(modelName)) {
                    cbData.select(i);
                    break;
                }
                i++;
            }
            setProperties();
            setFilters();
        }

        @Override
        public Object getSource() {
            return PROPERTY_TABLE_ID;
        }
    }

    private IPropertyStatisticalModel getSelectedDataModel() {
        int dataModelIndex = cbData.getSelectionIndex();
        if (dataModelIndex >= 0) {
            String selModel = cbData.getItem(dataModelIndex);
            for (IPropertyStatisticalModel model : dataModels) {
                if (model.getName().equals(selModel)) {
                    return model;
                }
            }
        }
        return null;
    }

    private String getSelectedProperty() {
        int propertyIndex = cbProperty.getSelectionIndex();
        if (propertyIndex >= 0) {
            return cbProperty.getItem(propertyIndex);
        }
        return null;
    }

    private String getSelectedFilter() {
        int filterIndex = cbFilter.getSelectionIndex();
        if (filterIndex >= 0) {
            return cbFilter.getItem(filterIndex);
        }
        return null;
    }

    private void setProperties() {
        IPropertyStatisticalModel dataModel = getSelectedDataModel();
        if (dataModel == null) {
            cbProperty.setItems(new String[] {});
        } else {
            cbProperty.setItems(dataModel.getAllPropertyNames(dataModel.getType()));
        }
    }

    private void setFilters() {
        IPropertyStatisticalModel dataModel = getSelectedDataModel();
        String property = getSelectedProperty();
        if (dataModel != null && !StringUtils.isEmpty(property)) {
            Class< ? > propClass = dataModel.getPropertyClass(dataModel.getType(), property);
            if (Number.class.isAssignableFrom(propClass)) {
                String[] filters = new String[PropertyNumberFilter.values().length];
                for (int i = 0; i < PropertyNumberFilter.values().length; i++) {
                    filters[i] = PropertyNumberFilter.values()[i].getCaption();
                }
                cbFilter.setItems(filters);
            } else {
                Object[] values = dataModel.getPropertyValues(dataModel.getType(), property);
                List<String> propValues = new ArrayList<String>(values.length);
                for (Object obj : values) {
                    propValues.add(obj.toString());
                }
                cbFilter.setItems(propValues.toArray(new String[] {}));
            }
        } else {
            cbFilter.setItems(new String[] {});
        }
    }

    private void setDataModels() {
        try {
            dataModels = ProjectModel.getCurrentProjectModel().findAllModels();
            List<String> models = new ArrayList<String>();
            for (IPropertyStatisticalModel model : dataModels) {
                models.add(model.getName());
            }
            cbData.setItems(models.toArray(new String[] {}));
        } catch (AWEException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void setFocus() {
    }

}
