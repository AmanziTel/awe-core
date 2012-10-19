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

    @Override
    public IPropertySource getPropertySource(final Object object) {
        if (object instanceof IDataElement) {
            return new DataElementPropertySource((IDataElement)object);
        } else if (object instanceof IModel) {
            return new DataElementPropertySource(((IModel)object).asDataElement());
        } else if (object instanceof IUIItem) {
            final IUIItem uiItem = (IUIItem)object;

            IDataElement dataElement = uiItem.castChild(IDataElement.class);
            IModel model = uiItem.castParent(IModel.class);

            if (dataElement != null) {
                return new DataElementPropertySource(dataElement, model);
            } else if (model != null) {
                return new DataElementPropertySource(model.asDataElement(), model);
            }
        }
        return null;
    }

}
