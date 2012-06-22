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

import org.amanzi.neo.services.exceptions.enums.ExceptionSeverity;
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

    private ExceptionSeverity severity;

    protected ServiceException(ServiceExceptionReason reason, ExceptionSeverity severity) {
        super();

        this.reason = reason;
        this.severity = severity;
    }

    protected ServiceException(ServiceExceptionReason reason, ExceptionSeverity severity, String message) {
        super(message);

        this.reason = reason;
        this.severity = severity;
    }

    protected ServiceException(ServiceExceptionReason reason, ExceptionSeverity severity, Exception exception) {
        super(exception);

        this.reason = reason;
        this.severity = severity;
    }

    public ServiceExceptionReason getReason() {
        return reason;
    }

    public ExceptionSeverity getSeverity() {
        return severity;
    }

}
