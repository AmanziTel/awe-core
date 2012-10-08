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

package org.amanzi.awe.views.statistcstree.view;

import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeContentProvider;
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeFilteredContentProvider;
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeLabelProvider;
import org.amanzi.awe.views.statistcstree.view.actions.ChangeDimensionAction;
import org.amanzi.awe.views.statistcstree.view.filter.container.IStatisticsTreeFilterContainer;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.TreeViewItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeView extends AbstractTreeView {

    public static final String VIEW_ID = "org.amanzi.awe.views.statistcstree.StatisticsTreeView";

    private static final StatisticsTreeContentProvider DEFAULT_CONTENT_PROVIDER = new StatisticsTreeContentProvider(
            DimensionType.TIME);

    /**
     * @param provider
     */
    public StatisticsTreeView() {
        super(DEFAULT_CONTENT_PROVIDER);
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        super.createPartControl(parent);
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        for (DimensionType type : DimensionType.values()) {
            dropDownMenu.add(new ChangeDimensionAction(type, getTreeViewer()));
        }
    }

    @Override
    protected void setProviders() {
        super.setProviders();
        getTreeViewer().setLabelProvider(new StatisticsTreeLabelProvider());
    }

    @Override
    protected ITreeItem< ? , ? > getTreeItem(final IModel model, final IDataElement element) {
        return new TreeViewItem<IStatisticsModel, IDataElement>((IStatisticsModel)model, element);
    }

    /**
     * @param parent
     * @param filter
     */
    public void filterTree(IModel parent, IStatisticsTreeFilterContainer filter) {
        if (getTreeViewer().getContentProvider() instanceof StatisticsTreeFilteredContentProvider) {
            StatisticsTreeFilteredContentProvider contentProvider = (StatisticsTreeFilteredContentProvider)getTreeViewer()
                    .getContentProvider();
            if (contentProvider.getFilter().equals(filter)) {
                showElement(parent, null);
                return;
            }
        }
        getTreeViewer().setContentProvider(
                new StatisticsTreeFilteredContentProvider(DimensionType.TIME, (IStatisticsModel)parent, filter));
        getTreeViewer().expandToLevel(5);

    }

    @Override
    public void showElement(IModel model, IDataElement element) {
        if (getTreeViewer().getContentProvider() instanceof StatisticsTreeFilteredContentProvider) {
            getTreeViewer().setContentProvider(DEFAULT_CONTENT_PROVIDER);
        }
        super.showElement(model, element);
    }
}
