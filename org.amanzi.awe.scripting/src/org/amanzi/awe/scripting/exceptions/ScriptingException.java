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

package org.amanzi.awe.scripting.exceptions;

/**
 * <p>
 * Wrapper exception for scripting
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
// TODO: LN: 01.08.2012, remove unused constructors
public class ScriptingException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 6229682553432736982L;

    /**
     * 
     */
    public ScriptingException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ScriptingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ScriptingException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ScriptingException(Throwable cause) {
        super(cause);
    }

}
