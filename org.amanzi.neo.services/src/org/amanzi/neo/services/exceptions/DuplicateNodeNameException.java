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

import org.amanzi.neo.services.enums.INodeType;

/**
 * TODO Purpose of
 * <p>
 * This exception is to be raised when there is an attempt to create a node with duplicate name and
 * type.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class DuplicateNodeNameException extends AWEException {
    static final long serialVersionUID = 1;

    private static String defaultMessage = "Duplicate node name '%s' of type '%s'.";

    public DuplicateNodeNameException() {
        super();
    }

    public DuplicateNodeNameException(String nodeName, INodeType nodeType) {
        super(defaultMessage, nodeName, nodeType.getId());
    }

    public DuplicateNodeNameException(String message, String nodeName, INodeType nodeType) {
        super(message, nodeName, nodeType.getId());
    }
}
