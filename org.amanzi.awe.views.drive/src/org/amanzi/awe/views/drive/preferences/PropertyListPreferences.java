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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * PropertyListPreferences
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class PropertyListPreferences extends PreferencePage implements IWorkbenchPreferencePage {

    public static final Color BAD_COLOR = new Color(null, 255, 0, 0);
    private final List<RowWr> propertyLists = new ArrayList<RowWr>();
    private Composite mainFrame;
    MultiPropertyIndex<Long> timestampIndex = null;
    private TableViewer viewer;
    private List<String> avaliableProperties = new ArrayList<String>();
    private Combo cAvaliableProperties;
    private Button bAddProperty;

    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Group(parent, SWT.NULL);

        GridLayout mainLayout = new GridLayout(3, false);

        mainFrame.setLayout(mainLayout);

        Label label = new Label(mainFrame, SWT.LEFT);

        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        label.setText("Property lists");

        viewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        TableContentProvider provider = new TableContentProvider();
        createTableColumn();

        viewer.setContentProvider(provider);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);
        viewer.getControl().setToolTipText(
                "To create a new property list, just enter needed values to the first blank line.\n"
                        + "To delete a property list it is need to remove it's name and value.");
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;

        if (!avaliableProperties.isEmpty()) {
            label = new Label(mainFrame, SWT.NONE);
            label.setText("Avaliable properties");
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            cAvaliableProperties = new Combo(mainFrame, SWT.DROP_DOWN | SWT.READ_ONLY);
            layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            cAvaliableProperties.setLayoutData(layoutData);
            String[] items = avaliableProperties.toArray(new String[0]);
            Arrays.sort(items);
            cAvaliableProperties.setItems(items);

            bAddProperty = new Button(mainFrame, SWT.PUSH);
            bAddProperty.setText("Add");

            bAddProperty.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                    IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
                    RowWr wr;
                    if (sel.size() != 1) {
                        wr = propertyLists.get(propertyLists.size() - 1);
                        viewer.setSelection(new StructuredSelection(new Object[] {wr}));
                    } else {
                        wr = (RowWr)sel.getFirstElement();
                    }
                    if (!cAvaliableProperties.getText().isEmpty()) {
                        wr.setProperties(wr.getProperties().isEmpty() ? cAvaliableProperties.getText() : wr.getProperties() + ", " + cAvaliableProperties.getText());
                        viewer.refresh(wr);
                        validate();
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
        }

        viewer.setInput("");
        return mainFrame;
    }

    @Override
    public void init(IWorkbench workbench) {
        formPropertyList();

    }

    /**
     *Preparing existing property lists for display
     */
    private void formPropertyList() {
        propertyLists.clear();
        String val = getPreferenceStore().getString(DataLoadPreferences.PROPERY_LISTS);
        boolean isName = true;
        RowWr wr = null;
        for (String str : val.split(DataLoadPreferences.CRS_DELIMETERS)) {
            if (isName) {
                wr = new RowWr(str, "");
                propertyLists.add(wr);
            } else {
                wr.setProperties(str);
            }
            isName = !isName;
        }

        ArrayList<RowWr> newPropertyLists = new ArrayList<RowWr>(propertyLists.size() + 1);
        for (RowWr row : propertyLists) {
            if (!row.getListName().isEmpty() || !row.getProperties().isEmpty()) {
                newPropertyLists.add(row);
            }
        }
        Collections.sort(newPropertyLists, new Comparator<RowWr>() {
            @Override
            public int compare(RowWr arg0, RowWr arg1) {
                return arg0.getListName().compareTo(arg1.getListName());
            }
        });

        propertyLists.clear();
        propertyLists.addAll(newPropertyLists);
        propertyLists.add(new RowWr("", ""));
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(DataLoadPreferences.PROPERY_LISTS);
        formPropertyList();
        viewer.setInput("");
    }

    @Override
    public boolean performOk() {
        StringBuilder result = new StringBuilder();
        // checkEmpty();
        for (RowWr wr : propertyLists) {
            if (!wr.isEmpty()) {
                result.append(DataLoadPreferences.CRS_DELIMETERS);
                result.append(wr.getListName());
                result.append(DataLoadPreferences.CRS_DELIMETERS);
                result.append(wr.getProperties());
            }
        }
        String string = result.length() > 0 ? result.substring(DataLoadPreferences.CRS_DELIMETERS.length()) : result.toString();
        getPreferenceStore().setValue(DataLoadPreferences.PROPERY_LISTS, string);
        return true;

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
        col.setText("List name");
        col.setWidth(100);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(0));

        column.setEditingSupport(new TableEditableSupport(viewer, true));

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Properties");
        col.setWidth(200);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(1));

        column.setEditingSupport(new TableEditableSupport(viewer, false));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.refresh();

    }

    private class ColLabelProvider extends ColumnLabelProvider {

        private final int i;

        /**
         * @param i
         */
        public ColLabelProvider(int i) {
            super();
            this.i = i;
        }

        @Override
        public Color getForeground(Object element) {
            RowWr wrapper = (RowWr)element;
            boolean result;
            if (i == 0) {
                result = wrapper.isValidName();
            } else {
                result = wrapper.isValidProperties();
            }
            return result ? null : BAD_COLOR;
        }

        @Override
        public String getText(Object element) {
            RowWr wrapper = (RowWr)element;
            return i == 0 ? wrapper.getListName() : wrapper.getProperties();
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
            return propertyLists.toArray(new RowWr[0]);
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
        private final boolean isName;

        /**
         * @param viewer
         */
        public TableEditableSupport(TableViewer viewer, boolean isName) {
            super(viewer);
            this.isName = isName;
            editor = new TextCellEditor(viewer.getTable());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected Object getValue(Object element) {
            return isName ? ((RowWr)element).getListName() : ((RowWr)element).getProperties();
        }

        @Override
        protected void setValue(Object element, Object value) {
            String sValue = ((String)value).trim();
            if (isName) {
                ((RowWr)element).setListName(sValue);
            } else {
                ((RowWr)element).setProperties(sValue);
            }
            checkEmpty();
            validate();
        }

    }

    public class RowWr {
        private String listName;
        private String properties;

        /**
         * @param listName
         * @param properties
         */
        public RowWr(String listName, String properties) {
            super();
            this.listName = listName;
            this.properties = properties;
        }

        /**
         * @return
         */
        public boolean isEmpty() {
            return listName.isEmpty() && properties.isEmpty();
        }

        /**
         * @return Returns the listName.
         */
        public String getListName() {
            return listName;
        }

        /**
         * @param listName The listName to set.
         */
        public void setListName(String listName) {
            this.listName = listName;
        }

        /**
         * @return Returns the properties.
         */
        public String getProperties() {
            return properties;
        }

        /**
         * @param properties The properties to set.
         */
        public void setProperties(String properties) {
            this.properties = properties;
        }

        public boolean isValidName() {
            if (isEmpty()) {
                return true;
            }
            if (listName.isEmpty() || listName.contains(DataLoadPreferences.CRS_DELIMETERS)) {
                return false;
            }

            for (RowWr row : propertyLists) {
                if (row == this) {
                    continue;
                }
                if (row.getListName().equals(this.listName)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isValidProperties() {
            if (isEmpty()) {
                return true;
            }
            if (properties.isEmpty() || properties.contains(DataLoadPreferences.CRS_DELIMETERS)) {
                return false;
            }
            if (Pattern.matches("(.*,( )*,.*)|(^,$)", properties)) {
                return false;
            }
            return true;
        }

        /**
         * @return
         */
        public boolean isValid() {
            return isValidName() && isValidProperties();
        }
    }

    /**
     *
     */
    public void validate() {
        setValid(isPageValid());
    }

    /**
     *
     */
    private void checkEmpty() {
        ArrayList<RowWr> newPropertyLists = new ArrayList<RowWr>(propertyLists.size() + 1);
        for (RowWr row : propertyLists) {
            if (!row.getListName().isEmpty() || !row.getProperties().isEmpty()) {
                newPropertyLists.add(row);
            }
        }

        propertyLists.clear();
        propertyLists.addAll(newPropertyLists);
        propertyLists.add(new RowWr("", ""));

        // Collections.sort(propertyLists, new Comparator<RowWr>() {
        // @Override
        // public int compare(RowWr arg0, RowWr arg1) {
        // if(arg0.getListName().isEmpty() && arg0.getProperties().isEmpty() &&
        // (!arg1.getListName().isEmpty() || !arg1.getProperties().isEmpty())){
        // return -1;
        // }else if(arg1.getListName().isEmpty() && arg1.getProperties().isEmpty() &&
        // (!arg0.getListName().isEmpty() || !arg0.getProperties().isEmpty())){
        // return 1;
        // }
        // return 0;
        // // return arg0.getListName().compareTo(arg1.getListName());
        // }
        // });

        viewer.setInput("");
    }

    /**
     * @return
     */
    private boolean isPageValid() {
        for (RowWr row : propertyLists) {
            if (!row.isValid()) {
                return false;
            }

        }
        return true;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

    /**
     * @param avaliableProperties
     */
    public void setAvaliableProperties(List<String> avaliableProperties) {
        this.avaliableProperties = avaliableProperties;
        Collections.sort(avaliableProperties);

    }
}
