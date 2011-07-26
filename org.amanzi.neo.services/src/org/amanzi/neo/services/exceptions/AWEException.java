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

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author kruglik_a
 * @since 1.0.0
 */
public abstract class AWEException extends Exception {

    protected String message;

    /** long serialVersionUID field */
    private static final long serialVersionUID = 8622675133900807087L;

    /**
     * This constructor will generate exception message by calling
     * <code>String.format(message, params);</code>.
     * 
     * @param message
     * @param params
     */
    protected AWEException(String message, Object... params) {
        this.message = String.format(message, params);

    }

    protected AWEException() {
        super();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
