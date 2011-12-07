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

import org.amanzi.neo.services.DatasetService.DatasetTypes;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author kruglik_a
 * @since 1.0.0
 */
public class DatasetTypeParameterException extends AWEException{

    /** long serialVersionUID field */
    private static final long serialVersionUID = -6533027214827170224L;
    private static final String defMessage = "DatasetTypeParameterException";

    public DatasetTypeParameterException(){
        super();
    }
    public DatasetTypeParameterException(DatasetTypes type){
        if (type == DatasetTypes.NETWORK){
            this.message = defMessage + ": parameter type can not be NETWORK in this method" ;
        }
        else {
            this.message = defMessage +": type parameter differs from NETWORK";
        }
    }
}
