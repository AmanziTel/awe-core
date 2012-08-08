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

package org.amanzi.awe.statistics.ui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.awe.statistics.ui.Messages;
import org.amanzi.awe.statistics.ui.StatisticsPlugin;
import org.amanzi.awe.statistics.ui.view.table.StatisticsComparator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * sorting table dialog
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SortingDialog extends Shell {
    private static final Image sortAscImage = StatisticsPlugin.getImageDescriptor("icons/Asc.png").createImage();
    private static final Image sortDescImage = StatisticsPlugin.getImageDescriptor("icons/Desc.png").createImage();
    private static final RegexViewerFilter regexFilter = new RegexViewerFilter();
    private static final String MATCHES_PATTERN = ".*%s.*";
    protected static final ViewerFilter[] EMPTY_FILTERS_ARRAY = new ViewerFilter[] {};
    private static final IContentProvider CONTENT_PROVIDER = new ITreeContentProvider() {

        @Override
        public Object[] getChildren(Object parentElement) {
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return false;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            @SuppressWarnings("unchecked")
            List<String> input = (List<String>)inputElement;
            int size = input.size();
            Object[] elements = new Object[size + 1];
            elements = ArrayUtils.addAll(input.toArray(), elements);
            elements[0] = Messages.sortingDialogLabelSelectAll;
            return elements;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    };
    private static final IBaseLabelProvider LABEL_PROVIDER = new ILabelProvider() {

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            return (String)element;
        }

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

    };

    private Table table;
    private final Point location;
    private CLabel lblSortAsc;
    private CLabel lblSortDesc;
    private Label lblSeparator1;
    private Label lblClearFilter;

    private Collection<String> selection;
    private final Collection<String> groups;
    private Label lblTextFilters;
    private Label lblSeparator2;
    private Button btnSearch;
    private Text txtSearch;
    private CheckboxTreeViewer treeViewer;
    private Button btnSave;
    private Button btnClose;
    private final int colNum;
    private final TableColumn currentColumn;
    private final TableViewer tableViewer;
    private final boolean isAditionalColumnNecessary;

    public SortingDialog(TableViewer viewer, Collection<String> selectionList, Collection<String> groups, int colNum,
            boolean isAditionalColumnNecessary) {
        super(viewer.getTable().getShell(), SWT.BORDER);
        this.tableViewer = viewer;
        this.table = tableViewer.getTable();
        this.selection = selectionList;
        this.groups = groups;
        this.colNum = colNum;
        this.isAditionalColumnNecessary = isAditionalColumnNecessary;

        table.getColumn(colNum);
        this.currentColumn = viewer.getTable().getColumn(colNum);
        location = table.getDisplay().getCursorLocation();
        this.table = viewer.getTable();
        createComponents();
        layoutComponents();
        addListeners();
    }

    /**
     * add listeners to controls
     */
    private void addListeners() {
        lblSortAsc.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                updateSorting(table, colNum, currentColumn, SWT.DOWN);
                close();
            }
        });
        lblSortDesc.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                updateSorting(table, colNum, currentColumn, SWT.UP);
                close();
            }
        });

        lblClearFilter.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                selection.clear();
                selection.addAll(groups);
                tableViewer.setFilters(EMPTY_FILTERS_ARRAY);
                tableViewer.getTable().getShell().notifyListeners(TableListenersType.UPDATE_BUTTON, null);
                close();
            }
        });

        btnSearch.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.setFilters(new ViewerFilter[NumberUtils.INTEGER_ZERO]);
                txtSearch.setText(StringUtils.EMPTY);
                btnSearch.setEnabled(false);
            }
        });
        txtSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtSearch.getText();
                if ((text != null) && !text.isEmpty()) {
                    treeViewer.setSubtreeChecked(e.getSource(), false);
                    regexFilter.setFilterText(text);
                    treeViewer.setFilters(new ViewerFilter[] {regexFilter});
                    btnSearch.setEnabled(true);
                }
            }
        });

        btnSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selection = new ArrayList<String>();
                String txtSearchText = txtSearch.getText();
                for (Object element : treeViewer.getCheckedElements()) {
                    if (txtSearchText.isEmpty() || matches(txtSearchText, (String)element)) {
                        selection.add((String)element);

                    }
                }
                tableViewer.setFilters(new ViewerFilter[] {new StatisticsAggregationFilter(selection)});
                tableViewer.getTable().getShell().notifyListeners(TableListenersType.UPDATE_BUTTON, null);
                close();
            }

        });

        addShellListener(new ShellAdapter() {

            @Override
            public void shellDeactivated(ShellEvent e) {
                close();
            }

        });
    }

    /**
     * create controls
     */
    private void createComponents() {
        lblSortAsc = new CLabel(this, SWT.LEFT);
        lblSortAsc.setImage(sortDescImage);
        lblSortAsc.setText(Messages.sortingDialogLabelSortFromAtoZ);

        lblSortDesc = new CLabel(this, SWT.LEFT);
        lblSortDesc.setText(Messages.sortingDialogLabelSortFromZtoA);
        lblSortDesc.setImage(sortAscImage);

        lblSeparator1 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        setLayoutData(lblSeparator1, lblSortDesc);

        lblClearFilter = new Label(this, SWT.LEFT);
        lblClearFilter.setText(Messages.sortingDialogLabelClearFilter);
        lblClearFilter.setEnabled(selection.size() != groups.size());

        lblTextFilters = new Label(this, SWT.LEFT);
        lblTextFilters.setText(Messages.sortingDialogLabelTextFilters);

        lblSeparator2 = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);

        btnSearch = new Button(this, SWT.PUSH);
        btnSearch.setText(Messages.sortingDialogLableClear);
        btnSearch.setEnabled(false);

        txtSearch = new Text(this, SWT.SINGLE | SWT.BORDER);

        treeViewer = new CheckboxTreeViewer(this);
        configureTreeViewer(treeViewer);

        btnSave = new Button(this, SWT.PUSH);
        btnSave.setText(Messages.sortingDialogLabelOk);

        btnClose = new Button(this, SWT.PUSH);
        btnClose.setText(Messages.sortingDialogLableClose);

    }

    private void layoutComponents() {
        Rectangle clientArea = table.getDisplay().getClientArea();
        int shellWidth = Math.min(300, clientArea.width - location.x);
        int shellHeight = Math.min(300, clientArea.height - location.y);
        setSize(shellWidth, shellHeight);
        setLocation(location);
        setLayoutData(lblSortAsc, NumberUtils.INTEGER_ZERO);
        setLayoutData(lblSortDesc, lblSortAsc);
        setLayoutData(lblSeparator1, lblSortDesc);
        setLayoutData(lblClearFilter, lblSeparator1);
        setLayoutData(lblTextFilters, lblClearFilter);
        setLayoutData(lblSeparator2, lblTextFilters);
        layoutSearchBox(lblSeparator2);
        layoutSaveButton();
        layoutViewer();
        layotCloseButton();

    }

    /**
     * layout close button
     */
    private void layotCloseButton() {
        FormData formData = new FormData();
        formData.bottom = new FormAttachment(100, -5);
        formData.left = new FormAttachment(75, 5);
        formData.right = new FormAttachment(100, -5);
        btnClose.setLayoutData(formData);
    }

    /**
     * layout for tree viewer
     */
    private void layoutViewer() {
        FormData formData = new FormData();
        formData.top = new FormAttachment(btnSearch, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(btnSave, -5);
        treeViewer.getControl().setLayoutData(formData);

    }

    /**
     * layout save Button
     */
    private void layoutSaveButton() {
        FormData formData = new FormData();
        formData.bottom = new FormAttachment(100, -5);
        formData.left = new FormAttachment(50, 5);
        formData.right = new FormAttachment(75, -5);
        btnSave.setLayoutData(formData);

    }

    /**
     * layout search box
     * 
     * @param relativeComponent
     */
    private void layoutSearchBox(Control relativeComponent) {
        FormData formData = new FormData();
        formData.top = new FormAttachment(relativeComponent, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(80, -5);
        txtSearch.setLayoutData(formData);
        formData = new FormData();
        formData.top = new FormAttachment(relativeComponent, 5);
        formData.left = new FormAttachment(80, 2);
        formData.right = new FormAttachment(100, -5);
        btnSearch.setLayoutData(formData);

    }

    /**
     * Creates and sets the layout data
     * 
     * @param control control to set layout data
     * @param top a control above
     */
    private void setLayoutData(Control control, Integer top) {
        FormData formData = new FormData();
        formData.top = new FormAttachment(top, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        control.setLayoutData(formData);
    }

    /**
     * Sets layout data
     * 
     * @param control control to set layout data
     */
    private void setLayoutData(Control control, Control controlAbove) {
        FormData formData = new FormData();
        formData.top = new FormAttachment(controlAbove, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        control.setLayoutData(formData);
    }

    /**
     ** Configures a tree viewer - sets a content provider, a lable provider, input and adds
     * listeners
     * 
     * @param treeViewer tree viewer to configure
     */
    private void configureTreeViewer(final CheckboxTreeViewer treeViewer) {
        treeViewer.setContentProvider(CONTENT_PROVIDER);
        treeViewer.setLabelProvider(LABEL_PROVIDER);
        treeViewer.setInput(groups);
        treeViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                final boolean checked = event.getChecked();
                if (event.getElement().toString().equals(Messages.sortingDialogLabelSelectAll)) {
                    treeViewer.setSubtreeChecked(event.getElement(), true);
                } else if (!checked) {
                    treeViewer.setChecked(Messages.sortingDialogLabelSelectAll, false);
                    selection.remove(event.getElement());
                }
            }
        });
        if (selection != null) {
            treeViewer.setCheckedElements(selection.toArray());
            if (selection.size() == groups.size()) {
                treeViewer.setChecked(Messages.sortingDialogLabelSelectAll, true);
            }
        }
    }

    /**
     * Updates sorting
     * 
     * @param table viewer table
     * @param colNum column number
     * @param currentColumn selected column
     * @param direction sort direction
     */
    private void updateSorting(final Table table, final int colNum, TableColumn currentColumn, int direction) {
        table.setSortDirection(direction);
        table.setSortColumn(currentColumn);
        ((StatisticsComparator)tableViewer.getComparator()).update(colNum, direction, isAditionalColumnNecessary);
        tableViewer.refresh();
    }

    /**
     * Checks if the given text matches the filter text
     * 
     * @param filterText
     * @param textToCompare
     * @return true if matches
     */
    private static boolean matches(String filterText, String textToCompare) {
        Pattern pattern = Pattern.compile(String.format(MATCHES_PATTERN, filterText), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filterText);
        return matcher.matches();
    }

    /**
     * Filter that uses regular expression
     * 
     * @author Pechko_E
     * @since 1.0.0
     */
    private static class RegexViewerFilter extends ViewerFilter {

        private String filter;

        public RegexViewerFilter() {
        }

        public void setFilterText(String text) {
            this.filter = text;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if ((filter == null) || filter.isEmpty()) {
                return true;
            }
            String clearedText = clearTextFromSpecialChars(filter);
            String elem = ((String)element);
            if (elem.equals(Messages.sortingDialogLabelSelectAll)) {
                return true;
            }
            return matches(clearedText, elem);
        }

        private String clearTextFromSpecialChars(String filter) {
            return filter.toLowerCase();
        }
    }

    /**
     * aggregation filter
     * <p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static class StatisticsAggregationFilter extends ViewerFilter {
        private final Collection<String> values;

        /**
         * @param start
         * @param end
         */
        public StatisticsAggregationFilter(Collection<String> selection) {
            this.values = selection;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            StatisticsCell[] cells = (StatisticsCell[])element;
            StatisticsRow row = cells[0].getParent();
            StatisticsGroup group = row.getParent();
            return values.contains(group.getName());
        }
    }
}
