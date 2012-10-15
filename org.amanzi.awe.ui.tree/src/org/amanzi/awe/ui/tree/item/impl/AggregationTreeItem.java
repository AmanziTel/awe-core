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

import org.amanzi.awe.ui.dto.impl.AggregationItem;
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
public class AggregationTreeItem extends AggregationItem implements ITreeItem {

    private final ITreeWrapper treeWrapper;

    /**
     * @param parent
     * @param child
     * @param function
     */
    public AggregationTreeItem(final IModel parent, final Object child, final ICollectFunction function,
            final ITreeWrapper treeWraper) {
        super(parent, child, function);

        this.treeWrapper = treeWraper;
    }

    @Override
    public ITreeWrapper getWrapper() {
        return treeWrapper;
    }

}
