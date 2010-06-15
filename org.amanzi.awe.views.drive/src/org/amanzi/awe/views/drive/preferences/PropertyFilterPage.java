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

package org.amanzi.awe.views.drive.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.preferences.NeoCorePreferencesConstants;
import org.amanzi.neo.core.propertyFilter.OperationCase;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * Preference filter page. This page provides the ability to edit options for filtering.
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class PropertyFilterPage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final Logger LOGGER = Logger.getLogger(PropertyFilterPage.class);

    protected static final Color RED = new Color(null, 255, 0, 0);
    protected static final Color GREEN = new Color(null, 0, 200, 0);
    protected static final Color BLUE = new Color(null, 0, 0, 255);
    protected static final Color GRAY = new Color(null, 120, 120, 120);
    protected static final Color BLACK = new Color(null, 0, 0, 0);

    protected static final Color BAD_COLOR = RED;

    protected static final int indEncludance = 0;
    protected static final int indDataset = 1;
    protected static final int indProperty = 2;
    private Composite mainFrame;
    private TableViewer viewer;
    private Button bUp;
    private Button bTop;
    private Button bDown;
    private Button bBottom;

    private final List<RowWr> filterRules = new ArrayList<RowWr>();

    /**
     * Constructor
     */
    public PropertyFilterPage() {
        super();
        setPreferenceStore(NeoCorePlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Group(parent, SWT.NULL);
        ((Group)mainFrame).setText("List of rules");

        GridLayout mainLayout = new GridLayout(3, false);
        mainFrame.setLayout(mainLayout);

        Label label = new Label(mainFrame, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        label.setText("To create a new rule, just click on \"NEW\" cell and enter needed values.\n"
                + "To delete a rule it is need to mark it's operation type as \"remove\". Rule will be removed during saving the changes.");

        viewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        TableContentProvider provider = new TableContentProvider();
        createTableColumn();

        viewer.setContentProvider(provider);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 4);
        viewer.getControl().setLayoutData(layoutData);

        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;

        bTop = new Button(mainFrame, SWT.PUSH);
        bUp = new Button(mainFrame, SWT.PUSH);
        bDown = new Button(mainFrame, SWT.PUSH);
        bBottom = new Button(mainFrame, SWT.PUSH);

        bTop.setText("Top");
        bUp.setText("Up");
        bDown.setText("Down");
        bBottom.setText("Bottom");

        layoutData = new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false);
        bTop.setLayoutData(layoutData);
        bUp.setLayoutData(layoutData);
        bDown.setLayoutData(layoutData);
        bBottom.setLayoutData(layoutData);

        viewer.setInput("");

        addListeners();

        return mainFrame;
    }

    @Override
    public void init(IWorkbench workbench) {
        formRulesList();
    }

    /**
     * Form rules list
     */
    private void formRulesList() {
        filterRules.clear();
        String val = getPreferenceStore().getString(NeoCorePreferencesConstants.FILTER_RULES);
        int propertyIndex = indEncludance;
        RowWr wr = null;
        for (String str : val.split(DataLoadPreferences.CRS_DELIMETERS)) {
            if (propertyIndex == indEncludance) {
                wr = new RowWr(null, "", "");
                wr.setOperationCase(OperationCase.getEnumById(str));
                propertyIndex = indDataset;
            } else if (propertyIndex == indDataset) {
                wr.setDataset(str);
                propertyIndex = indProperty;
            } else if (propertyIndex == indProperty) {
                wr.setProperty(str);
                propertyIndex = indEncludance;
                filterRules.add(wr);
            }
        }
        filterRules.add(new RowWr(OperationCase.NEW, "", ""));
    }

    /**
     *Create the table columns
     */
    private void createTableColumn() {
        Table table = viewer.getTable();
        TableViewerColumn column;
        TableColumn col;

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Operation");
        col.setWidth(100);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(indEncludance));
        column.setEditingSupport(new TableEditableSupport(viewer, indEncludance));

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Dataset");
        col.setWidth(200);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(indDataset));
        column.setEditingSupport(new TableEditableSupport(viewer, indDataset));

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Property");
        col.setWidth(200);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(indProperty));
        column.setEditingSupport(new TableEditableSupport(viewer, indProperty));

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.refresh();
    }

    /**
     *add listeners on visual items
     */
    private void addListeners() {
        viewer.getControl().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    changeIncludance(e);
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });

        bTop.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem item = viewer.getTable().getItem(viewer.getTable().getSelectionIndex());
                if (item != null) {
                    RowWr selectedRow = (RowWr)item.getData();
                    if (selectedRow.getOperationCase() == OperationCase.NEW)
                        return;
                    int selectedIndex = filterRules.indexOf(selectedRow);
                    if (selectedIndex == 0)
                        return;
                    ArrayList<RowWr> newFilterRules = new ArrayList<RowWr>(filterRules.size());
                    newFilterRules.add(selectedRow);
                    filterRules.remove(selectedRow);
                    newFilterRules.addAll(filterRules);
                    filterRules.clear();
                    filterRules.addAll(newFilterRules);
                    updateTable();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        bUp.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem item = viewer.getTable().getItem(viewer.getTable().getSelectionIndex());
                if (item != null) {
                    RowWr selectedRow = (RowWr)item.getData();
                    if (selectedRow.getOperationCase() == OperationCase.NEW)
                        return;
                    int selectedIndex = filterRules.indexOf(selectedRow);
                    if (selectedIndex == 0)
                        return;
                    RowWr targetRow = filterRules.get(selectedIndex - 1);
                    filterRules.set(selectedIndex, targetRow);
                    filterRules.set(selectedIndex - 1, selectedRow);
                    updateTable();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bDown.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem item = viewer.getTable().getItem(viewer.getTable().getSelectionIndex());
                if (item != null) {
                    RowWr selectedRow = (RowWr)item.getData();
                    if (selectedRow.getOperationCase() == OperationCase.NEW)
                        return;
                    int selectedIndex = filterRules.indexOf(selectedRow);
                    if (selectedIndex == filterRules.size() - 2)
                        return;
                    RowWr targetRow = filterRules.get(selectedIndex + 1);
                    filterRules.set(selectedIndex, targetRow);
                    filterRules.set(selectedIndex + 1, selectedRow);
                    updateTable();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        bBottom.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem item = viewer.getTable().getItem(viewer.getTable().getSelectionIndex());
                if (item != null) {
                    RowWr selectedRow = (RowWr)item.getData();
                    if (selectedRow.getOperationCase() == OperationCase.NEW)
                        return;
                    int selectedIndex = filterRules.indexOf(selectedRow);
                    if (selectedIndex == filterRules.size() - 2)
                        return;
                    filterRules.remove(selectedRow);
                    filterRules.add(selectedRow);
                    updateTable();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    /**
     * @param e
     */
    private void changeIncludance(MouseEvent e) {
        Point p = new Point(e.x, e.y);
        TableItem item = viewer.getTable().getItem(p);
        if (item != null) {
            if (item.getBounds(indEncludance).contains(p)) {
                RowWr row = (RowWr)item.getData();
                row.setOperationCase(row.getOperationCase().nextCase());
                updateTable();

            }
        }

    }

    /**
    *
    */
    public void updateTable() {
        List<RowWr> updatableFilterRules = new ArrayList<RowWr>(filterRules.size());
        for (RowWr row : filterRules) {
            if (row.getOperationCase() != OperationCase.NEW) {
                updatableFilterRules.add(row);
            }
        }
        filterRules.clear();
        filterRules.addAll(updatableFilterRules);
        filterRules.add(new RowWr(OperationCase.NEW, "", ""));
        viewer.refresh();
    }

    /**
     * @param property
     * @return
     */
    private boolean validateProperty(String property) {
        // if (!property.isEmpty())
        // return validateRegExp(property);
        // return false;
        return !property.isEmpty();
    }

    /**
     * @param dataset
     * @return
     */
    private boolean validateDataset(String dataset) {
        // if (dataset.isEmpty())
        // return true;
        // return validateRegExp(dataset);
        return true;
    }

    /**
     * Simple regular expression validation
     * 
     * @param string
     */
    public boolean validateRegExp(String string) {
        try {
            Pattern.compile(string);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    /**
     * Check if all data on page valid.
     * 
     * @return
     */
    private boolean isPageValid() {
        for (RowWr row : filterRules) {
            if (row.getOperationCase() != OperationCase.NEW && row.getOperationCase() != OperationCase.REMOVE_CANDIDAT
                    && !(row.isPropertyValid() && row.isDatasetValid()))
                return false;
        }
        return true;
    }

    @Override
    public boolean performOk() {
        StringBuilder result = new StringBuilder();
        for (RowWr wr : filterRules) {
            if (wr.getOperationCase() != OperationCase.NEW && wr.getOperationCase() != OperationCase.REMOVE_CANDIDAT) {
                result.append(DataLoadPreferences.CRS_DELIMETERS);
                result.append(wr.getOperationCase().getStringValue());
                result.append(DataLoadPreferences.CRS_DELIMETERS);
                result.append(wr.getDataset());
                result.append(DataLoadPreferences.CRS_DELIMETERS);
                result.append(wr.getProperty());
            }
        }
        String string = result.length() > 0 ? result.substring(NeoCorePreferencesConstants.CRS_DELIMETERS.length()) : result.toString();
        getPreferenceStore().setValue(NeoCorePreferencesConstants.FILTER_RULES, string);
        return true;

    }

    private class RowWr {
        private OperationCase operationCase = OperationCase.NEW;
        private String dataset = "";
        private String property = "";
        private boolean isDatasetValid = true;
        private boolean isPropertyValid = false;

        /**
         * @param listName
         * @param properties
         */
        public RowWr(OperationCase operationCase, String dataset, String property) {
            super();
            this.operationCase = operationCase;
            this.dataset = dataset;
            this.property = property;
        }

        /**
         * @return Returns the operationCase.
         */
        public OperationCase getOperationCase() {
            return operationCase;
        }

        /**
         * @param operationCase The operationCase to set.
         */
        public void setOperationCase(OperationCase operationCase) {
            this.operationCase = operationCase;
            setValid(isPageValid());
        }

        /**
         * @return Returns the dataset.
         */
        public String getDataset() {
            return dataset;
        }

        /**
         * @param dataset The dataset to set.
         */
        public void setDataset(String dataset) {
            this.dataset = dataset;
            isDatasetValid = validateDataset(this.dataset);
            setValid(isPageValid());
        }

        /**
         * @return Returns the property.
         */
        public String getProperty() {
            return property;
        }

        /**
         * @param property The property to set.
         */
        public void setProperty(String property) {
            this.property = property;
            isPropertyValid = validateProperty(this.property);
            setValid(isPageValid());
        }

        /**
         * @return Returns the isDatasetValid.
         */
        public boolean isDatasetValid() {
            return isDatasetValid;
        }

        /**
         * @return Returns the isPropertyValid.
         */
        public boolean isPropertyValid() {
            return isPropertyValid;
        }
    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Taskc
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return filterRules.toArray(new RowWr[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class TableEditableSupport extends EditingSupport {

        private final TextCellEditor editor;
        private final int columnIndex;

        /**
         * @param viewer
         */
        public TableEditableSupport(TableViewer viewer, int columnIndex) {
            super(viewer);
            this.columnIndex = columnIndex;
            editor = new TextCellEditor(viewer.getTable());
        }

        @Override
        protected boolean canEdit(Object element) {
            RowWr row = (RowWr)element;
            return columnIndex != indEncludance && row.getOperationCase() != OperationCase.NEW;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected Object getValue(Object element) {
            RowWr row = (RowWr)element;
            switch (columnIndex) {
            case indEncludance:
                return row.getOperationCase().getStringValue();
            case indDataset:
                return row.getDataset();
            case indProperty:
                return row.getProperty();
            default:
                LOGGER.error("TableEditableSupport#getValue contains wrong table column index = " + columnIndex);
            }
            return "";
        }

        @Override
        protected void setValue(Object element, Object value) {
            RowWr row = (RowWr)element;
            switch (columnIndex) {
            case indEncludance:
                // row.setIsInclude(!row.getIsInclude());
                break;
            case indDataset:
                row.setDataset(value.toString());
                break;
            case indProperty:
                row.setProperty(value.toString());
                break;
            default:
                LOGGER.error("TableEditableSupport#setValue contains wrong table column index = " + columnIndex);
            }
            updateTable();
        }

    }

    private class ColLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        /**
         * @param columnIndex
         */
        public ColLabelProvider(int columnIndex) {
            super();
            this.columnIndex = columnIndex;
        }

        @Override
        public Color getForeground(Object element) {
            RowWr row = (RowWr)element;
            if (row.getOperationCase() == OperationCase.NEW || row.getOperationCase() == OperationCase.REMOVE_CANDIDAT)
                return GRAY;
            switch (columnIndex) {
            case indEncludance:
                return row.getOperationCase() == OperationCase.INCLUDE ? GREEN : BLUE;
            case indDataset:
                return row.isDatasetValid() ? BLACK : BAD_COLOR;
            case indProperty:
                return row.isPropertyValid() ? BLACK : BAD_COLOR;
            default:
                return BLACK;
            }
        }

        @Override
        public String getText(Object element) {
            RowWr row = (RowWr)element;
            switch (columnIndex) {
            case indEncludance:
                return row.getOperationCase().getStringValue();
            case indDataset:
                return row.getDataset();
            case indProperty:
                return row.getProperty();
            default:
                LOGGER.error("ColLabelProvider#getText contains wrong table column index = " + columnIndex);
            }
            return "";
        }
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(NeoCorePreferencesConstants.FILTER_RULES);
        formRulesList();
        viewer.setInput("");
    }

}
