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

package org.amanzi.awe.views.explorer.view.menu.commands;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.treeview.command.handlers.AbstractTreeCommandHandler;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.network.NetworkElementType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowInTreeCommand extends AbstractTreeCommandHandler {
    private static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";

    public ShowInTreeCommand() {
        super();
    }

    @Override
    protected void handleElement(ITreeItem<IModel> element) {
        if (element.getDataElement().getNodeType().equals(NetworkElementType.NETWORK)) {
            AWEEventManager.getManager().fireShowInViewEvent(NETWORK_TREE_VIEW_ID, element.getDataElement());
        }

    }
}
