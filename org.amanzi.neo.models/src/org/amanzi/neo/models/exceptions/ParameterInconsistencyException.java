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

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ParameterInconsistencyException extends ModelException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -7590389635119439062L;

    private final String parameterName;

    private final Object parameterValue;

    public ParameterInconsistencyException(String parameterName, Object parameterValue) {
        super();

        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    /**
     * @return Returns the parameterName.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @return Returns the parameterValue.
     */
    public Object getParameterValue() {
        return parameterValue;
    }

}
