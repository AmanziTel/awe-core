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

package org.amanzi.awe.views.neighbours.views;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class NodeToNodeRelationsView extends ViewPart {

    private Combo cbNetwork, cbN2NType, cbN2NName;
    private Button btnSearch;
    private TableViewer table;
    private Iterable<INetworkModel> networkList;
    private Iterable<INodeToNodeRelationsModel> nodeToNodeModels;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;

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
        final GridLayout layout = new GridLayout(7, false);
        child.setLayout(layout);
        createLabel(child, "Data");
        cbNetwork = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = 100;
        cbNetwork.setLayoutData(layoutData);

        createLabel(child, "N2N Type");
        cbN2NType = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        // layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // layoutData.minimumWidth = 150;
        // cDrive.setLayoutData(layoutData);

        createLabel(child, "N2N Name");
        cbN2NName = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        btnSearch = new Button(child, SWT.PUSH);
        btnSearch.setText("Search");

        addListeners();
        setNetworkItems();
        setN2NTypeItems();

        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        table.getControl().setLayoutData(fData);
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn(new String[] {});
         provider = new TableContentProvider();
         table.setContentProvider(provider);
    }

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        // private final Image delete = IconManager.getIconManager().getNeoImage("DELETE_ENABLED");
        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        private final static int DEF_SIZE = 120;
        private final static int PROP_DEF_SIZE = 80;
        private final String[] colNames = new String[] {"Serving", "Neighbour"};

        private void createColumn(String label, int size) {
            TableViewerColumn column = new TableViewerColumn(table, SWT.LEFT);
            TableColumn col = column.getColumn();
            col.setText(label);
            columns.add(col);
            col.setWidth(size);
            col.setResizable(true);
        }

        /**
         * create column table
         */
        public void createTableColumn(String[] properties) {
            Table tabl = table.getTable();
            if (columns.isEmpty()) {
                for (String colName : colNames) {
                    createColumn(colName, DEF_SIZE);
                }
                for (String prop : properties) {
                    createColumn(prop, PROP_DEF_SIZE);
                }
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWrapper wrapper = (RowWrapper)element;
            if (columnIndex == 0) {
                return wrapper.getServingName();
            } else if (columnIndex == 1) {
                return wrapper.getNeighbourName();
            } else {
                return wrapper.getPropValue(columnIndex - 2);
            } 
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
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
            INodeToNodeRelationsModel n2nModel = getSelectedN2NModel();
            String[] properties = n2nModel.getAllProperties();
            ((TableLabelProvider)table.getLabelProvider()).createTableColumn(properties);
            for (IDataElement source : n2nModel.getAllElementsByType(NodeTypes.SECTOR)) {
                Iterable<IDataElement> relations = n2nModel.getN2NRelatedElements(source);
                for (IDataElement element : relations) {
                    Relationship relation = ((DataElement)element).getRelationship();
                    RowWrapper row = new RowWrapper(relation.getStartNode().getProperty(NewAbstractService.NAME).toString(),
                            relation.getEndNode().getProperty(NewAbstractService.NAME).toString());
                    for (int q = 0; q < properties.length; q++) {
                        row.addPropValue(relation.getProperty(properties[q], null));
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
        private final String servingName;
        private final String neighbourName;
        private List<String> propValues;

        private RowWrapper(String servingName, String neighbourName) {
            super();
            this.servingName = servingName;
            this.neighbourName = neighbourName;
            propValues = new ArrayList<String>();
        }

        public String getServingName() {
            return servingName;
        }

        public String getNeighbourName() {
            return neighbourName;
        }

        public void addPropValue(Object value) {
            propValues.add(value.toString());
        }

        public void clearPropValues() {
            propValues.clear();
        }

        public String getPropValue(int index) {
            if (propValues.size() <= index)
                return null;
            return propValues.get(index);
        }

    }

    private void addListeners() {
        cbNetwork.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setN2NModelsItems(getSelectedNetwork());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        btnSearch.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private INetworkModel getSelectedNetwork() {
        int networkIndex = cbNetwork.getSelectionIndex();
        if (networkIndex >= 0) {
            String network = cbNetwork.getItem(networkIndex);
            for (INetworkModel model : networkList) {
                if (model.getName().equals(network)) {
                    return model;
                }
            }
        }
        return null;
    }

    private INodeToNodeRelationsModel getSelectedN2NModel() {
        int modelIndex = cbN2NName.getSelectionIndex();
        if (modelIndex >= 0) {
            String n2nModel = cbN2NName.getItem(modelIndex);
            for (INodeToNodeRelationsModel model : nodeToNodeModels) {
                if (model.getName().equals(n2nModel)) {
                    return model;
                }
            }
        }
        return null;
    }

    private N2NRelTypes getSelectedN2NType() {
        int typeIndex = cbN2NType.getSelectionIndex();
        if (typeIndex >= 0) {
            String n2nType = cbN2NType.getItem(typeIndex);
            for (N2NRelTypes type : N2NRelTypes.values()) {
                if (type.getId().equals(n2nType)) {
                    return type;
                }
            }
        }
        return null;
    }

    private void setN2NModelsItems(INetworkModel network) {
        if (network == null) {
            cbN2NName.setItems(new String[] {});
            nodeToNodeModels = null;
        } else {
            try {
                nodeToNodeModels = network.getNodeToNodeModels(N2NRelTypes.NEIGHBOUR);
                List<String> models = new ArrayList<String>();
                for (INodeToNodeRelationsModel model : nodeToNodeModels) {
                    models.add(model.getName());
                }
                cbN2NName.setItems(models.toArray(new String[] {}));
            } catch (AWEException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

    private void setN2NTypeItems() {
        String[] types = new String[N2NRelTypes.values().length];
        for (int i = 0; i < N2NRelTypes.values().length; i++) {
            types[i] = N2NRelTypes.values()[i].getId();
        }
        cbN2NType.setItems(types);
    }

    private void setNetworkItems() {
        try {
            networkList = ProjectModel.getCurrentProjectModel().findAllNetworkModels();
            List<String> networks = new ArrayList<String>();
            for (INetworkModel network : networkList) {
                networks.add(network.getName());
            }
            cbNetwork.setItems(networks.toArray(new String[] {}));
        } catch (AWEException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void setFocus() {
    }

}
