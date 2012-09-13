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

package org.amanzi.awe.views.statistcstree.view.actions;

import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.views.statistcstree.providers.StatisticsTreeContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChangeDimensionAction extends Action implements IWorkbenchAction {

    private static final String ID = "org.amanzi.awe.views.statisticstree.action";
    private DimensionType type;
    private TreeViewer viewer;

    public ChangeDimensionAction(DimensionType type, TreeViewer viewer) {
        setId(ID + type.name());
        setText(type.name());
        this.viewer = viewer;
        this.type = type;
    }

    public void run() {
        viewer.setContentProvider(new StatisticsTreeContentProvider(type));
    }

    public void dispose() {
    }

}
