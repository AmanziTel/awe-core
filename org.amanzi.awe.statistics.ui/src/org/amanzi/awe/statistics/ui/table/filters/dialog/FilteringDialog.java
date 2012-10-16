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

package org.amanzi.awe.statistics.ui.table.filters.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.statistics.ui.table.filters.GroupsFilter;
import org.amanzi.awe.statistics.ui.table.filters.RegexViewerFilter;
import org.amanzi.awe.statistics.ui.table.filters.dialog.KpiComboViewerWidget.IKpiTreeListener;
import org.amanzi.awe.ui.icons.IconManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FilteringDialog implements IKpiTreeListener, ModifyListener, MouseListener {

    public interface IFilterDialogListener extends Listener {
        int UPDATE_SORTING_LISTENER = 1;
    }

    private static final Image sortAscImage = IconManager.getInstance().getImage("Asc");

    private static final Image sortDescImage = IconManager.getInstance().getImage("Desc");

    private static final String SORT_FROM_A_TO_Z_LABEL = "Sorting A to Z";

    private static final String SORT_FROM_Z_TO_A_LABEL = "Sorting Z to A";

    private static final String CLEAR_FILTERS = "Clear filters";

    private static final String TEXT_FILTER = "Text filter...";

    private static final String CLEAR_LABEL = "Clear";

    private static final GridLayout TWO_COLUMN_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_COLUMN_LAYOUT = new GridLayout(1, false);

    private static final RegexViewerFilter REGEX_VIEWER_FILTER = new RegexViewerFilter();

    private static final String OK_LABEL = "OK";

    private static final String CANCEL = "Cancel";

    private static final int MIN_SHWLL_HEIGHT = 400;

    private static final int MIN_SHELL_WIDTH = 300;

    private Button bSortZA;

    private Button bSortAZ;

    private final TableColumn column;

    private Button bClearFilters;

    private Text textField;

    private Button bClear;

    private final Shell shell;

    private final Set<String> groups;
    private KpiComboViewerWidget treeViewer;

    private Button bOk;

    private Button bCancel;

    private final TableViewer tableViewer;

    private List<ViewerFilter> filters = new ArrayList<ViewerFilter>();

    private int currentDirection = SWT.UP;

    public FilteringDialog(final TableViewer tableViewer, final TableColumn column, final Set<String> groups) {
        Table table = tableViewer.getTable();
        shell = new Shell(table.getShell(), SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("Aggregation filters setting");
        // locate dialog
        Point location = table.getDisplay().getCursorLocation();
        Rectangle clientArea = table.getDisplay().getClientArea();
        int shellWidth = Math.min(MIN_SHELL_WIDTH, clientArea.width - location.x);
        int shellHeight = MIN_SHWLL_HEIGHT;
        shell.setSize(shellWidth, shellHeight);
        shell.setLocation(location);
        shell.setLayout(ONE_COLUMN_LAYOUT);
        // instantiate required variables
        filters = new ArrayList<ViewerFilter>(Arrays.asList(tableViewer.getFilters()));
        this.groups = groups;
        this.tableViewer = tableViewer;
        this.column = column;
        // create shell components
        createWidgets();
    }

    /**
     * @param shellLayout
     */
    private void createWidgets() {
        Composite composite = createComposite(shell, TWO_COLUMN_LAYOUT);
        bSortAZ = createButton(composite, sortAscImage, SORT_FROM_A_TO_Z_LABEL);
        bSortZA = createButton(composite, sortDescImage, SORT_FROM_Z_TO_A_LABEL);

        composite = createComposite(shell, ONE_COLUMN_LAYOUT);
        bClearFilters = createButton(composite, null, CLEAR_FILTERS);
        if (filters.isEmpty()) {
            bClearFilters.setEnabled(false);
        }

        Label textFilter = new Label(composite, SWT.NONE);
        textFilter.setText(TEXT_FILTER);

        composite = createComposite(shell, TWO_COLUMN_LAYOUT);
        textField = new Text(composite, SWT.BORDER);
        textField.setLayoutData(getGridData());
        textField.addModifyListener(this);
        bClear = createButton(composite, null, CLEAR_LABEL);

        addKpiViewerWidget();

        composite = createComposite(shell, TWO_COLUMN_LAYOUT);
        bOk = createButton(composite, null, OK_LABEL);
        bCancel = createButton(composite, null, CANCEL);
    }

    /**
     * @param composite
     * @param filteringDialog
     */
    private void addKpiViewerWidget() {
        treeViewer = new KpiComboViewerWidget(shell, this);
        treeViewer.initializeWidget();
        List<String> selected = null;
        // set existed filters from table viewer to tree viewer
        for (ViewerFilter filter : filters) {
            if (filter instanceof GroupsFilter) {
                GroupsFilter groupFilter = (GroupsFilter)filter;
                selected = groupFilter.getValues();
            }
        }
        treeViewer.setItems(groups, selected);
    }

    private GridData getGridData() {
        return new GridData(SWT.FILL, SWT.TOP, true, false);
    }

    /**
     * @param filteringDialog
     * @param layout
     */
    private Composite createComposite(final Composite filteringDialog, final GridLayout layout) {
        Composite composite = new Composite(filteringDialog, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(getGridData());
        return composite;
    }

    public void open() {
        shell.open();
    }

    /**
     * create sorting button
     * 
     * @param layout
     * @param image
     * @param label
     * @return
     */
    private Button createButton(final Composite composite, final Image image, final String label) {
        Button button = new Button(composite, SWT.NONE);
        if (image != null) {
            button.setImage(image);
        }
        button.setText(label);
        button.setLayoutData(getGridData());
        button.addMouseListener(this);
        return button;
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        if (e.getSource().equals(textField)) {
            setFilter();
        }
    }

    /**
     * set filter to tree viewer
     */
    private void setFilter() {
        String filterText = textField.getText();
        REGEX_VIEWER_FILTER.setFilterText(filterText);
        treeViewer.setFilters(REGEX_VIEWER_FILTER);
        bClear.setEnabled(true);
    }

    @Override
    public void mouseDoubleClick(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDown(final MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseUp(final MouseEvent e) {
        Object source = e.getSource();
        FilterDialogEvent event = new FilterDialogEvent(column);
        boolean isNeedToClose = false;
        if (bSortZA.equals(source)) {
            currentDirection = SWT.DOWN;
        } else if (bSortAZ.equals(source)) {
            currentDirection = SWT.UP;
        } else if (bClear.equals(source)) {
            textField.setText(StringUtils.EMPTY);
        } else if (bOk.equals(source)) {
            filters.add(new GroupsFilter(treeViewer.getSelected()));
            isNeedToClose = true;
        } else if (bCancel.equals(source)) {
            shell.close();
            return;
        } else if (bClearFilters.equals(source)) {
            filters.clear();
            treeViewer.reset();
            bClearFilters.setEnabled(false);
        }
        event.setFilters(filters);
        event.setDirection(currentDirection);

        if (e.getSource().equals(bOk)) {
            tableViewer.getTable().notifyListeners(IFilterDialogListener.UPDATE_SORTING_LISTENER, event);
        }
        if (isNeedToClose) {
            shell.close();
        }
    }
}
