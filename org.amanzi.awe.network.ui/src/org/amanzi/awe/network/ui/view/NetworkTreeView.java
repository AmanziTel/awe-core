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
package org.amanzi.awe.network.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.view.AbstractAWETreeView;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of INetworkModel nodes
 * defined by the INetworkModel.java class.
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */

public class NetworkTreeView extends AbstractAWETreeView {

    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.trees.NetworkTree";

    public NetworkTreeView() {
        super();
    }

    @Override
    public String getViewId() {
        return NETWORK_TREE_VIEW_ID;
    }

    @Override
    protected void onItemSeelcted(final ITreeItem selectedItem) {
        IDataElement child = selectedItem.castChild(IDataElement.class);
        if (child != null) {
            if (child.getNodeType().equals(NetworkElementType.SECTOR) || child.getNodeType().equals(NetworkElementType.SITE)) {
                List<IDataElement> elements = new ArrayList<IDataElement>();
                elements.add(child);
                INetworkModel model = selectedItem.castParent(INetworkModel.class);
                AWEEventManager.getManager().fireShowOnMapEvent(model, elements, model.getMainGIS().getBounds(), null);
            }
        }
    }
}