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

import org.amanzi.awe.ui.dto.IUIItemNew;
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
    private final Map<Object, IPropertySource> mapping = new HashMap<Object, IPropertySource>();

    @Override
    public IPropertySource getPropertySource(final Object object) {
        if (object instanceof IDataElement) {
            return getPopertySource((IDataElement)object);
        } else if (object instanceof IModel) {
            return new DataElementPropertySource(((IModel)object).asDataElement());
        } else if (object instanceof IUIItemNew) {
            final IUIItemNew uiItem = (IUIItemNew)object;
            final IDataElement dataElement = uiItem.castChild(IDataElement.class);
            IModel model = uiItem.castParent(IModel.class);

            if (dataElement != null) {
                return getPropertySource(dataElement, model);
            } else if (model == null) {
                model = uiItem.castChild(IModel.class);

                return getPropertySource(model.asDataElement(), model);
            }
        }
        return null;
    }

    /**
     * @param child
     * @param parent
     * @return
     */
    private IPropertySource getPropertySource(final IDataElement child, final IModel parent) {
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
    private IPropertySource getPopertySource(final IDataElement object) {
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
    private IPropertySource getFromMapping(final Object object) {
        return mapping.get(object);

    }
}
