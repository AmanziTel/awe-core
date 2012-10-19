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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.internal.IDatasetModel;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElementPropertySource implements IPropertySource {

    private final IDataElement dataElement;

    private final IModel model;

    public DataElementPropertySource(final IDataElement dataElement) {
        this(dataElement, null);
    }

    public DataElementPropertySource(final IDataElement dataElement, final IModel model) {
        this.dataElement = dataElement;
        this.model = model;
    }

    @Override
    public Object getEditableValue() {
        return null;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        final List<IPropertyDescriptor> propertyDescriptors = new ArrayList<IPropertyDescriptor>();

        propertyDescriptors.add(new DataElementPropertyDescriptor(DataElementPropertyDescriptor.ID_PROPERTY));
        for (final String propertyName : dataElement.keySet()) {
            propertyDescriptors.add(new DataElementPropertyDescriptor(propertyName));
        }

        return propertyDescriptors.toArray(new IPropertyDescriptor[propertyDescriptors.size()]);
    }

    @Override
    public Object getPropertyValue(final Object id) {
        if (id.equals(DataElementPropertyDescriptor.ID_PROPERTY)) {
            return dataElement.getId();
        }
        return dataElement.get(id.toString());
    }

    @Override
    public boolean isPropertySet(final Object id) {
        // TODO: LN: 19.10.2012, incorrect - please look to javadoc of this method
        return dataElement.contains(id.toString());
    }

    @Override
    public void resetPropertyValue(final Object id) {
        // TODO: LN: 19.10.2012, should be implemented
    }

    @Override
    public void setPropertyValue(final Object id, final Object value) {
        if (model instanceof IDatasetModel) {
            IDatasetModel dataset = (IDatasetModel)model;
            try {
                if (!dataElement.get((String)id).getClass().equals(value.getClass())) {
                    // TODO: LN: 19.10.2012, this case should be logged and it should be a Message
                    // Box with Warning
                    return;
                }
                // TODO: LN: 19.10.2012, do we really can change any choosen property?
                dataset.updateProperty(dataElement, (String)id, value);
                dataElement.put((String)id, value);
                AWEEventManager.getManager().fireDataUpdatedEvent(null);
            } catch (ModelException e) {
                // TODO KV: handle exception
            }
        }
    }

}
