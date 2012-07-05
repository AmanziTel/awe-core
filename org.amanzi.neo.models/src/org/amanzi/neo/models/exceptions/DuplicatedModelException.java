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

package org.amanzi.neo.models.exceptions;

import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DuplicatedModelException extends ModelException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -6504791027632079488L;

    private final Class< ? extends IModel> model;

    private final String propertyName;

    private final Object propertyValue;

    /**
     * @param e
     */
    public DuplicatedModelException(final Class< ? extends IModel> model, final String propertyName, final Object propertyValue) {
        super();

        this.model = model;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    /**
     * @return Returns the model.
     */
    public Class< ? extends IModel> getModel() {
        return model;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the propertyValue.
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

}
