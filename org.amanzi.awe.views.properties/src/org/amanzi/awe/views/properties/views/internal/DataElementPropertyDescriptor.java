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

import org.amanzi.awe.views.properties.AWEPropertiesPlugin;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElementPropertyDescriptor extends PropertyDescriptor {

    private enum Category {
        PROPERTY, HEADER;
    }

    private static final IGeneralNodeProperties generalNodeProperties;

    static {
        generalNodeProperties = AWEPropertiesPlugin.getDefault().getGeneralNodeProperties();
    }

    /**
     * @param id
     * @param displayName
     */
    public DataElementPropertyDescriptor(final String propertyName) {
        super(propertyName, propertyName);
        setCategory(calculateCategory(propertyName).toString());
    }

    private Category calculateCategory(final String propertyName) {
        if (propertyName.equals(generalNodeProperties.getNodeTypeProperty()) || propertyName.equals("id")) {
            return Category.HEADER;
        }

        return Category.PROPERTY;
    }

}
