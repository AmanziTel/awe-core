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

package org.amanzi.awe.views.property.views.internal;

import org.amanzi.neo.dto.IDataElement;
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

    public DataElementPropertySource(final IDataElement dataElement) {
        this.dataElement = dataElement;
    }

    @Override
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPropertyValue(final Object id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isPropertySet(final Object id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void resetPropertyValue(final Object id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPropertyValue(final Object id, final Object value) {
        // TODO Auto-generated method stub

    }

}
