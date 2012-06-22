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
 *
 * </p>
 * @author kruglik_a
 * @since 1.0.0
 */
public class IndexPropertyException extends AWEException{

    /** long serialVersionUID field */
    private static final long serialVersionUID = 7063642187752868061L;
    
    private static final String defMessage = "IndexPropertyException: attempt to index value of type '%s' in propertyStatistics with type '%s'";

    public IndexPropertyException(){
        super();
    }
    public IndexPropertyException(Class<?> valueType, Class<?> propStatType){
        super(defMessage, valueType , propStatType);
    }
}
