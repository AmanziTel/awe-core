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
import org.apache.commons.lang3.ObjectUtils;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IncorrectParentException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -4686087326099974244L;

    private static final String MESSAGE_TEMPLATE = "Incorrect parent of node <{0}>. Expected <{1}> but was <{2}>.";

    private final String child;

    private final String expectedNode;

    private final String actualNode;

    /**
     * @param e
     */
    public IncorrectParentException(Node child, Node expectedParent, Node actualParent) {
        super(ServiceExceptionReason.INCORRECT_PARENT);

        this.child = child.toString();
        this.expectedNode = expectedParent.toString();
        this.actualNode = ObjectUtils.toString(actualParent);
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE_TEMPLATE, getChild(), getExpectedNode(), getActualNode());
    }

    /**
     * @return Returns the child.
     */
    public String getChild() {
        return child;
    }

    /**
     * @return Returns the expectedNode.
     */
    public String getExpectedNode() {
        return expectedNode;
    }

    /**
     * @return Returns the actualNode.
     */
    public String getActualNode() {
        return actualNode;
    }
}
