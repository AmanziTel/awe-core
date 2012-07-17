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

package org.amanzi.awe.statistics.exceptions;

/**
 * <p>
 * exception for throwing when node can't be modified
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class UnableToModifyException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public UnableToModifyException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public UnableToModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public UnableToModifyException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public UnableToModifyException(Throwable cause) {
        super(cause);
    }

}
