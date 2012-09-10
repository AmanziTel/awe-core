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

package org.amanzi.awe.views.network.provider;

import org.amanzi.awe.views.treeview.provider.impl.AbstractTreeViewItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkTreeItem extends AbstractTreeViewItem<INetworkModel, IDataElement> {

    /**
     * @param root
     * @param child
     */
    public NetworkTreeItem(INetworkModel root, IDataElement child) {
        super(root, child);
    }

    @Override
    public Iterable<IDataElement> getChildren() throws ModelException {
        return getModel().getChildren(getChild());
    }

    @Override
    public boolean hasChildren() throws ModelException {
        return getModel().getChildren(getChild()).iterator().hasNext();
    }

}
