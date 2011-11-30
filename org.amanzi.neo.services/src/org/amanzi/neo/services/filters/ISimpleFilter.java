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

package org.amanzi.neo.services.filters;

import java.io.Serializable;

import org.amanzi.neo.services.filters.exceptions.NotComparebleException;
import org.amanzi.neo.services.filters.exceptions.NullValueException;
import org.amanzi.neo.services.model.IDataElement;

/**
 * Interface that describes simple Filter
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public interface ISimpleFilter extends Serializable {
    
    /**
     * Checks Data Element 
     *
     * @param dataElement
     * @return
     * @throws NullValueException
     * @throws NotComparebleException
     */
    boolean check(IDataElement dataElement) throws NullValueException, NotComparebleException; 

}
