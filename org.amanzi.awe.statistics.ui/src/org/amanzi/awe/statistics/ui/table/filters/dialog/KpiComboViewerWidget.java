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
import java.util.HashSet;
import java.util.List;

import org.amanzi.awe.statistics.ui.table.filters.dialog.KpiComboViewerWidget.IKpiTreeListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * tree view for group filtering
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class KpiComboViewerWidget extends AbstractAWEWidget<Composite, IKpiTreeListener> implements ICheckStateListener {

    public interface IKpiTreeListener extends AbstractAWEWidget.IAWEWidgetListener {
    }

    public static final String SELECT_ALL_ITEM = "Select All";

    private static final GridLayout ONE_COLUM_LAYOUT = new GridLayout(1, false);

    private static final ViewerFilter[] EMPTY_FILTERS_LIST = new ViewerFilter[0];;

    private final KpiTreeContentProvider contentProvider = new KpiTreeContentProvider();

    private final LabelProvider labelProvider = new LabelProvider();

    private CheckboxTreeViewer treeViewer;

    private List<String> selection = new ArrayList<String>();

    private HashSet<String> groups;

    private boolean isAllSelected;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public KpiComboViewerWidget(final Composite parent, final IKpiTreeListener listener) {
        super(parent, SWT.V_SCROLL | SWT.H_SCROLL, listener);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {

        Composite composite = new Composite(parent, SWT.FILL);
        composite.setLayout(ONE_COLUM_LAYOUT);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        treeViewer = new CheckboxTreeViewer(composite, SWT.FILL | SWT.BORDER);
        treeViewer.setContentProvider(contentProvider);
        treeViewer.getControl().setLayoutData((new GridData(SWT.FILL, SWT.FILL, true, true)));
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.getTree().setVisible(true);
        treeViewer.addCheckStateListener(this);

        return composite;
    }

    protected void setItems(Iterable<String> items, List<String> selected) {
        groups = (HashSet<String>)items;
        if (selected != null) {
            selection = selected;
        }
        groups.add(SELECT_ALL_ITEM);
        treeViewer.setInput(groups.toArray(new String[groups.size()]));
        groups.remove(SELECT_ALL_ITEM);
        if (!selection.isEmpty()) {
            treeViewer.setCheckedElements(selection.toArray());
            if (selection.size() == groups.size()) {
                treeViewer.setChecked(SELECT_ALL_ITEM, true);
            }
        } else {
            selectAll();
        }
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        final boolean checked = event.getChecked();
        String element = event.getElement().toString();
        if (checked) {
            if (element.equals(SELECT_ALL_ITEM)) {
                selectAll();
                return;
            }
            selection.add(element);
        } else if (!checked) {
            deselectItem(element);
        }

    }

    /**
     * @param element
     */
    private void deselectItem(String element) {
        if (element.equals(SELECT_ALL_ITEM)) {
            deselectAll();
            return;
        }
        treeViewer.setChecked(SELECT_ALL_ITEM, false);
        selection.remove(element);
        isAllSelected = false;

    }

    /**
     * deselection action
     */
    private void deselectAll() {
        for (Object selected : treeViewer.getCheckedElements()) {
            treeViewer.setChecked(selected, false);
        }
        treeViewer.setChecked(SELECT_ALL_ITEM, false);
        selection.clear();
    }

    /**
     * selection action
     */
    private void selectAll() {
        isAllSelected = true;
        selection.clear();
        treeViewer.setCheckedElements(groups.toArray());
        selection.addAll(groups);
        treeViewer.setChecked(SELECT_ALL_ITEM, true);
    }

    /**
     * get all selected items;
     * 
     * @return
     */
    protected List<String> getSelected() {
        return this.selection;
    }

    /**
     * set filters for tree view
     * 
     * @param filters
     */
    protected void setFilters(ViewerFilter... filters) {
        treeViewer.setFilters(filters);
        treeViewer.refresh();
        treeViewer.setCheckedElements(selection.toArray());
        if (isAllSelected) {
            treeViewer.setChecked(SELECT_ALL_ITEM, true);
        }
    }

    /**
     * reset tree viewer.
     */
    protected void reset() {
        treeViewer.setFilters(EMPTY_FILTERS_LIST);
        selectAll();
    }
}
