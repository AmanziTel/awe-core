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

package org.amanzi.awe.explorer.ui.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.explorer.ui.wrappers.ProjectWrapperFactory;
import org.amanzi.awe.ui.tree.item.ITreeItem;
import org.amanzi.awe.ui.tree.provider.AWETreeContentProvider;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.apache.commons.collections.IteratorUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ExplorerContentProvider extends AWETreeContentProvider {

    private final ProjectWrapperFactory projectWrapperFactory = new ProjectWrapperFactory();

    /**
     * @param factories
     */
    public ExplorerContentProvider(final Set<ITreeWrapperFactory> factories) {
        super(factories);
    }

    @Override
    protected Object[] getChildren(final ITreeWrapper wrapper, final ITreeItem item) {
        return getElementsInternal(item);
    }

    @Override
    protected boolean hasChildren(final ITreeWrapper wrapper, final ITreeItem item) {
        return getElementsInternal(item).length > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(final Object inputElement) {
        final List<ITreeWrapper> wrappers = new ArrayList<ITreeWrapper>();

        final Iterator<ITreeWrapper> items = projectWrapperFactory.getWrappers(inputElement);
        if (items != null) {
            wrappers.addAll(IteratorUtils.toList(items));
        }

        return toObject(wrappers);
    }

}
