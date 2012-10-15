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

package org.amanzi.awe.ui.dto.impl;

import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class UIItem implements IUIItemNew {

    private final IModel parent;

    private final Object child;

    public UIItem(final IModel parent, final Object child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public <T extends IModel> T castParent(final Class<T> clazz) {
        return castObject(clazz, parent);
    }

    @Override
    public <T> T castChild(final Class<T> clazz) {
        return castObject(clazz, child);
    }

    @SuppressWarnings("unchecked")
    private <T> T castObject(final Class<T> clazz, final Object o) {
        if (o != null && clazz != null && clazz.isAssignableFrom(o.getClass())) {
            return (T)o;
        }

        return null;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("UIItem {parent: ").append(parent).append(", child: ").append(child).append("}");

        return builder.toString();
    }
}
