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
public abstract class ServiceException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -217926823632324451L;

    private ServiceExceptionReason reason;

    protected ServiceException(ServiceExceptionReason reason) {
        super();

        this.reason = reason;
    }

    protected ServiceException(ServiceExceptionReason reason, Exception exception) {
        super(exception);

        this.reason = reason;
    }

    public ServiceExceptionReason getReason() {
        return reason;
    }

}
