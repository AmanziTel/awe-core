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
 * <p>
 * This exception is to be raised when some wrong data is passed to be set as a <code>Node</code>
 * property
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class IllegalNodeDataException extends AWEException {
    static final long serialVersionUID = 1;

    public IllegalNodeDataException() {
        super();
    }

    public IllegalNodeDataException(String message) {
        this.message = message;
    }
}
