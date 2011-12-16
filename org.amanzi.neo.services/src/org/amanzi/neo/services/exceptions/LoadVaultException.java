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
public class LoadVaultException extends AWEException {
    /** long serialVersionUID field */
    private static final long serialVersionUID = 951966276181673202L;
    private Exception loadVaultException;

    public LoadVaultException(Exception e) {
        this.loadVaultException = e;
    }

    @Override
    public Throwable getCause() {
        return loadVaultException;
    }

}
