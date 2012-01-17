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
 * this exception throws if property type not implement to parse
 * </p>
 * @author kruglik_a
 * @since 1.0.0
 */
public class UnsupportedClassException extends AWEException{

    /** long serialVersionUID field */
    private static final long serialVersionUID = 7102999939733473141L;
    
    private static final String defMessage = "could not parse to '%s' because this type unsupported yet";
    
    public UnsupportedClassException(){
        super();
    }
    public UnsupportedClassException(Class<?> type){
        super(defMessage, type);
    }
    
    

}
