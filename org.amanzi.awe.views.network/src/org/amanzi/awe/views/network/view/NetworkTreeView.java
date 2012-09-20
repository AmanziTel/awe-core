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
package org.amanzi.awe.views.network.view;

import org.amanzi.awe.views.network.provider.NetworkTreeContentProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of INetworkModel nodes
 * defined by the INetworkModel.java class.
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */

public class NetworkTreeView extends AbstractTreeView {

    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";

    /**
     * The constructor.
     */
    public NetworkTreeView() {
        this(new NetworkTreeContentProvider());
    }

    protected NetworkTreeView(final NetworkTreeContentProvider networkTreeContentProvider) {
        super(networkTreeContentProvider);
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        setSearchField(new Text(parent, SWT.BORDER));
        super.createPartControl(parent);
    }

    @Override
    protected ITreeItem< ? , ? > getTreeItem(final IModel model, final IDataElement element) {
        // TODO Auto-generated method stub
        return null;
    }
}