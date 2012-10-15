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

package org.amanzi.awe.ui.tree.item.impl;

import org.amanzi.awe.ui.dto.impl.UIItem;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TreeItem extends UIItem implements ITreeItem {

    private ITreeWrapper wrapper;

    /**
     * @param parent
     * @param child
     */
    public TreeItem(final IModel parent, final Object child, final ITreeWrapper wrapper) {
        super(parent, child);

        this.wrapper = wrapper;
    }

    @Override
    public ITreeWrapper getWrapper() {
        return wrapper;
    }

    protected void setWrapper(final ITreeWrapper wrapper) {
        this.wrapper = wrapper;
    }

}
