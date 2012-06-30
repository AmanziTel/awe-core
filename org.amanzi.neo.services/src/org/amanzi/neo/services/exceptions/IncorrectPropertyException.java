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
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IncorrectPropertyException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 9170995426610424800L;

    private static final String EXCEPTION_MESSAGE = "Node <{0}> contain property <{1}> with value <{2}> but should contain value <{3}>.";

    private final String node;

    private final String propertyName;

    private final Object expectedValue;

    private final Object actualValue;

    /**
     * @param reason
     * @param severity
     * @param message
     */
    public IncorrectPropertyException(Node node, String propertyName, Object expectedValue, Object actualValue) {
        super(ServiceExceptionReason.INCORRECT_PROPERTY);

        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
        this.node = node.toString();
        this.propertyName = propertyName;

    }

    @Override
    public String getMessage() {
        return MessageFormat.format(EXCEPTION_MESSAGE, getNode(), getPropertyName(), getActualValue(), getExpectedValue());
    }

    /**
     * @return Returns the node.
     */
    public String getNode() {
        return node;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the expectedValue.
     */
    public Object getExpectedValue() {
        return expectedValue;
    }

    /**
     * @return Returns the actualValue.
     */
    public Object getActualValue() {
        return actualValue;
    }

}
