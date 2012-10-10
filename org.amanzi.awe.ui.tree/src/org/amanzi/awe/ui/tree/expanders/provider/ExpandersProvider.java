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

package org.amanzi.awe.ui.tree.expanders.provider;

import java.util.List;

import org.amanzi.awe.ui.tree.expanders.IChildrenExpander;
import org.amanzi.awe.ui.tree.expanders.IRootExpander;
import org.amanzi.awe.ui.tree.views.IAWETreeView;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public final class ExpandersProvider {

    private static final class ExpandersProviderHolder {
        private static volatile ExpandersProvider instance = new ExpandersProvider();
    }

    private ExpandersProvider() {

    }

    public static synchronized ExpandersProvider getProvider() {
        return ExpandersProviderHolder.instance;
    }

    public synchronized List<IRootExpander> getRootExpanders(final IAWETreeView treeView) {
        return null;
    }

    public synchronized List<IChildrenExpander> getChildrenExpander(final Class parentClass, final Class childClass) {
        return null;
    }

}
