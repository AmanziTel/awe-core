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

package org.amanzi.neo.services.exceptions;

import org.amanzi.neo.services.exceptions.enums.ServiceExceptionReason;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DuplicatedNodeException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -4866569763934067767L;

    private final String propertyName;

    private final Object duplicatedValue;

    /**
     * @param reason
     */
    public DuplicatedNodeException(String propertyName, Object duplicatedValue) {
        super(ServiceExceptionReason.DUPLICATED_NODE);

        this.propertyName = propertyName;
        this.duplicatedValue = duplicatedValue;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the duplicatedValue.
     */
    public Object getDuplicatedValue() {
        return duplicatedValue;
    }

}
