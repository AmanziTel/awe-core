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
 * Wraps exceptions that are thrown from Neo database
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class DatabaseException extends AWEException {
    static final long serialVersionUID = 1;
    
    private Exception dbException;
    
    public DatabaseException(Exception e){
        this.dbException = e;
    }
    
    @Override
    public Throwable getCause() {
        return dbException;
    }
}
