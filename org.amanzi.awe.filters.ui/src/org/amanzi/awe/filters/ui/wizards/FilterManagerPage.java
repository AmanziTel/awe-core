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

package org.amanzi.awe.filters.ui.wizards;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * page for filter manager
 * 
 * @author Vladislav_Kondratenko
 */
public class FilterManagerPage extends WizardPage {
    private static final Logger LOGGER = Logger.getLogger(FilterManagerPage.class);

    private final static String FILTER_CREATION_PAGE_MSG = "Choose dataset";
    private final static String LAYER_LABEL = "Gis Name";
    private final static String FILTER_DESCRIPTION_LABEL = "Filter description";
    private final static String FILTER_NAME = "Filter Name";
    private final static int LAYER_COLUMN_INDEX = 1;
    private final static int FILTER_NAME_COLUMN_INDEX = 2;
    private final static int FILTER_DESCRIPTION_COLUMN_INDEX = 3;

    private Map<String, IRenderableModel> datasetModelMap = new HashMap<String, IRenderableModel>();

    protected Combo cbDataset;
    private Button addNewFilterButton;
    private static TableViewer tableViewer;
    private Group main;

    /**
     * @param pageName
     */
    protected FilterManagerPage(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    /**
     * create main group
     * 
     * @param parent
     */
    protected void createMainLayout(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(1, false));
        parent.getShell().addListener(SWT.CHANGED, new Listener() {

            @Override
            public void handleEvent(Event event) {
                tableViewer.refresh();
            }
        });
    }

    @Override
    public void createControl(Composite parent) {
        createMainLayout(parent);
        createDatasetCombobox();
        createTableViewer();
        createAddButton(parent);
        setControl(main);
    }

    /**
     * create combo box with network models list
     */
    protected void createDatasetCombobox() {
        Label label = new Label(main, SWT.LEFT);
        label.setText(FILTER_CREATION_PAGE_MSG);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridData rootLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        getRootIteam();
        String[] result = datasetModelMap.keySet().toArray(new String[] {});
        Arrays.sort(result);
        cbDataset = new Combo(main, SWT.DROP_DOWN);
        cbDataset.setLayoutData(rootLayoutData);
        cbDataset.setItems(result);
        cbDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTable(datasetModelMap.get(cbDataset.getItem(cbDataset.getSelectionIndex())));
                addNewFilterButton.setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * update table when new dataset selected
     * 
     * @param iRenderableModel
     */
    protected void updateTable(IRenderableModel model) {
        tableViewer.getTable().setVisible(true);
        tableViewer.getTable().setLinesVisible(true);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.setInput(model);

    }

    /**
     * get all renderable models root;
     */
    private void getRootIteam() {
        datasetModelMap = new HashMap<String, IRenderableModel>();
        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
            for (IModel model : projectModel.findAllModels()) {
                if (model instanceof IRenderableModel) {
                    String id = model.getName();
                    datasetModelMap.put(id, (IRenderableModel)model);
                }
            }
        } catch (AWEException e) {
            LOGGER.error("Error while getRootItems work", e);
        }

        DatabaseManagerFactory.getDatabaseManager().commitMainTransaction();
    }

    /**
     * create button for adding filters
     */
    private void createAddButton(final Composite parent) {
        GridData rootLayoutData = new GridData(SWT.LEFT, SWT.LEFT, false, false, 3, 1);
        addNewFilterButton = new Button(main, SWT.PUSH);
        addNewFilterButton.setText("Add filter");
        addNewFilterButton.setSize(100, 100);
        addNewFilterButton.setLayoutData(rootLayoutData);
        addNewFilterButton.setEnabled(false);
        addNewFilterButton.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                new FilterCreationPage(parent.getShell(), datasetModelMap.get(cbDataset.getItem(cbDataset.getSelectionIndex())));
            }
        });
    }

    /**
     * create table for view information about filters and layers
     */
    private void createTableViewer() {
        tableViewer = new TableViewer(main, SWT.FULL_SELECTION | SWT.BORDER);

        Table table = tableViewer.getTable();

        TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.RIGHT);
        TableColumn column = viewerColumn.getColumn();
        column.setText(LAYER_LABEL);
        column.setWidth(100);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider());
        viewerColumn.setLabelProvider(new TableLabelProvider(LAYER_COLUMN_INDEX));

        viewerColumn = new TableViewerColumn(tableViewer, SWT.RIGHT);
        column = viewerColumn.getColumn();
        column.setText(FILTER_NAME);
        column.setWidth(100);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider());
        viewerColumn.setLabelProvider(new TableLabelProvider(FILTER_NAME_COLUMN_INDEX));

        viewerColumn = new TableViewerColumn(tableViewer, SWT.RIGHT);
        column = viewerColumn.getColumn();
        column.setText(FILTER_DESCRIPTION_LABEL);
        column.setWidth(400);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider());
        viewerColumn.setLabelProvider(new TableLabelProvider(FILTER_DESCRIPTION_COLUMN_INDEX));

        table.setVisible(false);
        tableViewer.refresh();

        tableViewer.setContentProvider(new TableContentProvider());

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        tableViewer.getControl().setLayoutData(layoutData);

    }
}
