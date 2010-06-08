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

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
    protected static final Color GREEN = new Color(null, 0, 255, 0);
    protected static final Color BLUE = new Color(null, 0, 0, 255);
    protected static final Color GRAY = new Color(null, 200, 200, 200);
    protected static final Color BLACK = new Color(null, 0, 0, 0);

    protected static final Color BAD_COLOR = RED;

    protected static final int indEncludance = 0;
    protected static final int indDataset = 1;
    protected static final int indProperty = 2;
    private Composite mainFrame;
    private TableViewer viewer;
    private final List<RowWr> filterRules = new ArrayList<RowWr>();

    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Group(parent, SWT.NULL);
        ((Group)mainFrame).setText("List of rules");

        GridLayout mainLayout = new GridLayout(3, false);
        mainFrame.setLayout(mainLayout);
        
        Label label = new Label(mainFrame, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        label.setText("To create a new rule, just click on \"NEW\" cell and enter needed values.\n" +
                "To delete a rule it is need to mark it's operation type as \"remove\". Rule will be removed during saving the changes.");
        
        viewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        TableContentProvider provider = new TableContentProvider();
        createTableColumn();

        viewer.setContentProvider(provider);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);
//        viewer.getControl().setToolTipText(
//                "To create a new rule, just click on \"NEW\" cell and enter needed values.\n" +
//                "To delete a rule it is need to mark it as \"remove\". Rule will be removed during saving the changes.");

        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;

        viewer.setInput("");

        addListeners();

        return mainFrame;
    }

    @Override
    public void init(IWorkbench workbench) {
        formRulesList();
    }

    /**
     *
     */
    private void formRulesList() {
        filterRules.clear();
        String val = getPreferenceStore().getString(DataLoadPreferences.FILTER_RULES);
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

        // ArrayList<RowWr> newPropertyLists = new ArrayList<RowWr>(filterRules.size() + 1);
        // for (RowWr row : filterRules) {
        // if (row.getIsInclude() != null) {
        // newPropertyLists.add(row);
        // }
        // }

        // Collections.sort(newPropertyLists, new Comparator<RowWr>() {
        // @Override
        // public int compare(RowWr arg0, RowWr arg1) {
        // return arg0.getListName().compareTo(arg1.getListName());
        // }
        // });

        // propertyLists.clear();
        // propertyLists.addAll(newPropertyLists);
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
    }

    /**
     * @param e
     */
    protected void changeIncludance(MouseEvent e) {
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
    public boolean validateProperty(String property) {
//        if (!property.isEmpty())
//            return validateRegExp(property);
//        return false;
        return !property.isEmpty();
    }

    /**
     * @param dataset
     * @return
     */
    public boolean validateDataset(String dataset) {
//        if (dataset.isEmpty())
//            return true;
//        return validateRegExp(dataset);
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
        String string = result.length() > 0 ? result.substring(DataLoadPreferences.CRS_DELIMETERS.length()) : result.toString();
        getPreferenceStore().setValue(DataLoadPreferences.FILTER_RULES, string);
        return true;

    }

    public class RowWr {
        private OperationCase operationCase = OperationCase.NEW;
        private String dataset;
        private String property;
        private boolean isDatasetValid;
        private boolean isPropertyValid;

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

    public class TableEditableSupport extends EditingSupport {

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

    public enum OperationCase {
        INCLUDE("Include"), EXCLUDE("Exclude"), NEW("NEW"), REMOVE_CANDIDAT("remove");

        private final String stringValue;

        private OperationCase(String stringValue) {
            this.stringValue = stringValue;
        }

        public OperationCase nextCase() {
            switch (this) {
            case INCLUDE:
                return EXCLUDE;
            case EXCLUDE:
                return REMOVE_CANDIDAT;
            default:
                return INCLUDE;
            }
        }

        public String getStringValue() {
            return stringValue;
        }

        public static OperationCase getEnumById(String enumId) {
            if (enumId == null) {
                return null;
            }
            for (OperationCase operationCase : OperationCase.values()) {
                if (operationCase.getStringValue().equals(enumId)) {
                    return operationCase;
                }
            }
            return null;
        }
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(DataLoadPreferences.FILTER_RULES);
        formRulesList();
        viewer.setInput("");
    }
}
