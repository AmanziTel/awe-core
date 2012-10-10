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

package org.amanzi.awe.ui.tree.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.awe.ui.tree.expanders.IChildrenExpander;
import org.amanzi.awe.ui.tree.expanders.IRootExpander;
import org.amanzi.awe.ui.tree.expanders.provider.ExpandersProvider;
import org.amanzi.awe.ui.tree.views.IAWETreeView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AWETreeContentProvider implements ITreeContentProvider {

    @SuppressWarnings("unused")
    private class CacheKey {
        private final Class parentClass;

        private final Class childClass;

        public CacheKey(final Class parentClass, final Class childClass) {
            this.parentClass = parentClass;
            this.childClass = childClass;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

        @Override
        public boolean equals(final Object o) {
            return EqualsBuilder.reflectionEquals(this, o, false);
        }
    }

    private final Map<CacheKey, List<IChildrenExpander>> childrenExpanderCache = new HashMap<CacheKey, List<IChildrenExpander>>();

    private final List<IRootExpander> rootExpanders;

    private final ExpandersProvider expandersProvider;

    public AWETreeContentProvider(final IAWETreeView treeView) {
        this.expandersProvider = ExpandersProvider.getProvider();

        rootExpanders = expandersProvider.getRootExpanders(treeView);
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(final Object inputElement) {
        final List<Object> result = new ArrayList<Object>();

        for (final IRootExpander rootExpander : rootExpanders) {
            result.addAll(rootExpander.getRootItems());
        }

        return result.toArray(new Object[result.size()]);
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getParent(final Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {

        return false;
    }

    private boolean hasChildrenInternal(final IUIItem uiItem) {
        for (final IChildrenExpander expander : getChildrenExpanders(uiItem.getParent().getClass(), uiItem.getChild().getClass())) {
            if (expander.hasChildren(uiItem)) {
                return true;
            }
        }

        return false;
    }

    private List<IChildrenExpander> getChildrenExpanders(final Class parent, final Class child) {
        final CacheKey key = new CacheKey(parent, child);

        List<IChildrenExpander> result = childrenExpanderCache.get(key);

        if (result == null) {
            result = ExpandersProvider.getProvider().getChildrenExpander(parent, child);

            childrenExpanderCache.put(key, result);
        }

        return result;
    }
}
