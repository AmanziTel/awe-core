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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsException extends Exception {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public StatisticsException() {
    }

    /**
     * @param message
     */
    public StatisticsException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public StatisticsException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public StatisticsException(String message, Throwable cause) {
        super(message, cause);
    }

}
