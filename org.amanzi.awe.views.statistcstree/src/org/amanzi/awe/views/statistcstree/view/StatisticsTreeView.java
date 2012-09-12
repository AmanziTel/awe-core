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
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeContentProvider;
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeLabelProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsTreeView extends AbstractTreeView {

    /**
     * @param provider
     */
    public StatisticsTreeView() {
        super(new StatisticsTreeContentProvider(DimensionType.TIME));
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager dropDownMenu = actionBars.getMenuManager();
        for (DimensionType type : DimensionType.values()) {
            dropDownMenu.add(new CustomAction(type));
        }
        super.createPartControl(parent);
    }

    @Override
    protected void setProviders() {
        super.setProviders();
        getTreeViewer().setLabelProvider(new StatisticsTreeLabelProvider());
    }

    private class CustomAction extends Action implements IWorkbenchAction {

        private static final String ID = "org.amanzi.awe.views.statisticstree.action";
        private DimensionType type;

        public CustomAction(DimensionType type) {
            setId(ID + type.name());
            setText(type.name());
            this.type = type;
        }

        public void run() {
            getTreeViewer().setContentProvider(new StatisticsTreeContentProvider(type));
        }

        public void dispose() {
        }

    }
}
