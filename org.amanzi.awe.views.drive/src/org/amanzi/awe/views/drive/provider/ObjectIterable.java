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

package org.amanzi.awe.views.drive.provider;

import java.util.Iterator;

import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ObjectIterable implements Iterable<Object> {
    Iterable< ? extends Object> iterable;

    public ObjectIterable(Iterable<IDataElement> iterable) {
        this.iterable = iterable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Object> iterator() {
        return (Iterator<Object>)iterable.iterator();
    }

}
