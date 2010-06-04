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

package org.amanzi.neo.core.utils;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract class NeoTreeContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getChildren(Object parentElement) {
        return ((NeoTreeElement)parentElement).getChildren();
    }

    @Override
    public Object getParent(Object element) {
        return ((NeoTreeElement)element).getParent();
    }

    @Override
    public boolean hasChildren(Object element) {
        return ((NeoTreeElement)element).hasChildren();
    }

}
