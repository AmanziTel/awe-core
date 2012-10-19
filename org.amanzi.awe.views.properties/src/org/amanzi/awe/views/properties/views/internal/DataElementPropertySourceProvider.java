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

package org.amanzi.awe.views.properties.views.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElementPropertySourceProvider implements IPropertySourceProvider {
    private Map<Object, IPropertySource> mapping = new HashMap<Object, IPropertySource>();

    @Override
    public IPropertySource getPropertySource(final Object object) {
        if (object instanceof IDataElement) {
            return getPopertySource((IDataElement)object);
        } else if (object instanceof IModel) {
            return getPopertySource(((IModel)object).asDataElement());
        } else if (object instanceof IUIItem) {
            final IUIItem< ? , ? > uiItem = (IUIItem< ? , ? >)object;

            if (uiItem.getChild() instanceof IDataElement) {
                return getPropertySource((IDataElement)uiItem.getChild(), (IModel)uiItem.getParent());
            } else if (uiItem.getChild() instanceof IModel) {
                return getPropertySource(((IModel)uiItem.getChild()).asDataElement(), uiItem.getParent());
            }
        }
        return null;
    }

    /**
     * @param child
     * @param parent
     * @return
     */
    private IPropertySource getPropertySource(IDataElement child, IModel parent) {
        IPropertySource source = getFromMapping(child);
        if (source == null) {
            source = new DataElementPropertySource(child, parent);
            mapping.put(child, source);
        }
        return source;
    }

    /**
     * @param object
     */
    private IPropertySource getPopertySource(IDataElement object) {
        IPropertySource source = getFromMapping(object);
        if (source == null) {
            source = new DataElementPropertySource(object);
            mapping.put(object, source);
        }
        return source;
    }

    /**
     * @param object
     */
    private IPropertySource getFromMapping(Object object) {
        return mapping.get(object);

    }
}
