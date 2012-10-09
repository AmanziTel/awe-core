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

import java.text.MessageFormat;

import org.amanzi.neo.services.exceptions.enums.ServiceExceptionReason;
import org.neo4j.graphdb.PropertyContainer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyNotFoundException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 9170995426610424800L;

    private static final String EXCEPTION_MESSAGE = "Node <{0}> does not contain property <{1}>.";

    private final String message;

    /**
     * @param reason
     * @param severity
     * @param message
     */
    public PropertyNotFoundException(final String propertyName, final PropertyContainer node) {
        super(ServiceExceptionReason.PROPERTY_NOT_FOUND);

        message = convertMessage(propertyName, node);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String convertMessage(final String propertyName, final PropertyContainer node) {
        return MessageFormat.format(EXCEPTION_MESSAGE, node, propertyName);
    }

}
